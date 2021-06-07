package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.ConsumptionPoint;
import lombok.extern.log4j.Log4j2;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@SuppressWarnings({"Deprecated", "deprecation"})
public class InfluxService {

    private final InfluxDB influxDB;
    private final String dbName = "loaddata";

    public InfluxService() {
        influxDB = connectDatabase();
        if (!influxDB.databaseExists(dbName)) {
            createDatabase();
        }
        influxDB.setDatabase(dbName);
        influxDB.enableBatch(100, 200, TimeUnit.MILLISECONDS);
    }

    private InfluxDB connectDatabase() {
        // Connect to database assumed on localhost with default credentials.
        return InfluxDBFactory.connect("http://192.168.188.25:49153", "admin", "admin");
    }

    private void createDatabase() {
        influxDB.createDatabase(dbName);
        influxDB.createRetentionPolicy("defaultPolicy", dbName, "30d", 1, true);
        influxDB.setRetentionPolicy("defaultPolicy");
        influxDB.setDatabase(dbName);
    }

    /**
     * Löscht die Datenbank und erstellt sie neu!
     */
    public void recreateDatabase() {
        Query queryObject = new Query("DROP DATABASE \"" + dbName + "\"", dbName);
        influxDB.query(queryObject);
        createDatabase();
    }

    private boolean pingServer() {
        try {
            // Ping and check for version string
            Pong response = influxDB.ping();
            if (response.getVersion().equalsIgnoreCase("unknown")) {
                log.error("Error pinging server.");
                return false;
            } else {
                log.debug("Database version: {}", response.getVersion());
                return true;
            }
        } catch (InfluxDBIOException idbo) {
            log.error("Exception while pinging database: ", idbo);
            return false;
        }
    }

    public List<ConsumptionPoint> getAllPoints() {
        String query = "select * from consumption order by time desc";
        return getData(query);
    }

    public void addPoint(ConsumptionPoint consumptionPoint) {
        Point point = Point.measurement("consumption")
                .tag("cbName", consumptionPoint.getCbName())
                .time(System.currentTimeMillis() - 100, TimeUnit.MILLISECONDS)
                .addField("name", consumptionPoint.getName())
                .addField("consumption", consumptionPoint.getConsumption())
                .addField("measurand", consumptionPoint.getMeasurand())
                .build();
        influxDB.write(point);
        influxDB.flush();
    }

    public void addPoints(List<ConsumptionPoint> consumptionPoints) {
        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .retentionPolicy("defaultPolicy")
                .build();
        consumptionPoints.forEach(consumptionPoint -> {
            Point point = Point.measurement("consumption")
                    .time(consumptionPoint.getTime().toEpochMilli(), TimeUnit.MILLISECONDS)
                    .tag("cbName", consumptionPoint.getCbName())
                    .addField("name", consumptionPoint.getName())
                    .addField("consumption", consumptionPoint.getConsumption())
                    .addField("measurand", consumptionPoint.getMeasurand())
                    .build();
            batchPoints.point(point);
        });
        log.debug("Consumption List size: " + consumptionPoints.size());
        influxDB.write(batchPoints);
        influxDB.flush();
    }


    public List<ConsumptionPoint> getPointsByName(String name) {
        String query = "select * from consumption where \"name\" = '" + name + "' order by time desc";
        return getData(query);
    }

    public ConsumptionPoint getLastPoint(String name) {
        String query = "select * from consumption where \"name\" = '" + name + "' order by time desc LIMIT 1";
        LinkedList<ConsumptionPoint> cpListe = getData(query);
        if (!cpListe.isEmpty())
            return cpListe.getFirst();
        else {
            ConsumptionPoint cp = new ConsumptionPoint();
            cp.setCbName(name);
            cp.setConsumption(0.0);
            cp.setMeasurand("A");
            cp.setTime(LocalDateTime.now().minusHours(1).atZone(ZoneId.of("Europe/Berlin")).toInstant());
            return cp;
        }
    }

    private LinkedList<ConsumptionPoint> getData(String query) {
        if (pingServer()) {
            // Run the query
            Query queryObject = new Query(query, dbName);
            QueryResult queryResult = influxDB.query(queryObject);

            // Map it
            //InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
            LinkedList<ConsumptionPoint> returnList = new LinkedList<>();
            try {
                queryResult.getResults()
                        .get(0)
                        .getSeries()
                        .forEach(series -> series
                                .getValues()
                                .forEach(point -> {
                                    ConsumptionPoint consumptionPoint = new ConsumptionPoint();
                                    consumptionPoint.setTime(Instant.parse((String) point.get(0)));
                                    consumptionPoint.setName((String) point.get(1));
                                    consumptionPoint.setConsumption((double) point.get(2));
                                    consumptionPoint.setMeasurand((String) point.get(3));
                                    consumptionPoint.setCbName((String) point.get(4));
                                    returnList.add(consumptionPoint);
                                }));
            } catch (NullPointerException ex) {
                log.debug("No data found");
            }
            return returnList;
            //return resultMapper.toPOJO(queryResult, ConsumptionPoint.class);
        } else {
            return new LinkedList<>();
        }
    }

    /**
     * Löscht Daten der Serie
     * @param name Name der Entität
     */
    public void delete(String name) {
        //Query q1 = new Query("SELECT * INTO consumption_clean FROM consumption WHERE \"name\" !=\"" + name + "\" GROUP BY *");
        //Query q2 = new Query("DROP measurement consumption");
        //Query q3 = new Query("SELECT * INTO consumption FROM consumption_clean GROUP BY *");
        QueryResult queryResult = influxDB.query(new Query("DROP SERIES FROM \"consumption\" WHERE \"cbName\" = '" + name + "'"));
        influxDB.flush();
        //QueryResult queryResult = influxDB.query(q1);
        //queryResult = influxDB.query(q2);
        //queryResult = influxDB.query(q3);
        log.debug(queryResult.getResults().size());
    }
}
