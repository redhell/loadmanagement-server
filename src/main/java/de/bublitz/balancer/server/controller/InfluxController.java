package de.bublitz.balancer.server.controller;

import de.bublitz.balancer.server.model.ConsumptionPoint;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.*;
import org.influxdb.impl.InfluxDBResultMapper;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings({"Deprecated", "deprecation"})
public class InfluxController {

    private final InfluxDB influxDB;
    private final String dbName = "loaddata";

    public InfluxController()  {
        influxDB = connectDatabase();
        if(!influxDB.databaseExists(dbName)) {
            createDatabase();
        }
        influxDB.setDatabase(dbName);
        influxDB.enableBatch(100, 200, TimeUnit.MILLISECONDS);
    }

    private InfluxDB connectDatabase() {
        // Connect to database assumed on localhost with default credentials.
        return InfluxDBFactory.connect("http://localhost:8086", "admin", "admin");
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
                log.info("Database version: {}", response.getVersion());
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
                .time(System.currentTimeMillis() - 100, TimeUnit.MILLISECONDS)
                .addField("name", consumptionPoint.getName())
                .addField("consumption", consumptionPoint.getConsumption())
                .build();
        influxDB.write(point);
    }

    public void addPoints(List<ConsumptionPoint> consumptionPoints) {
        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .retentionPolicy("defaultPolicy")
                .build();
        consumptionPoints.forEach(consumptionPoint -> {
            Point point = Point.measurement("consumption")
                    .time(consumptionPoint.getTime().toEpochMilli(), TimeUnit.MILLISECONDS)
                    .addField("name", consumptionPoint.getName())
                    .addField("consumption", consumptionPoint.getConsumption())
                    .build();
            batchPoints.point(point);
        });
        log.info("Consumption List size: " + consumptionPoints.size());
        influxDB.write(batchPoints);
        influxDB.flush();
    }


    public List<ConsumptionPoint> getPointsByName(String name) {
        String query = "select * from consumption where \"name\" = '" + name + "' order by time desc";
        return getData(query);
    }

    private List<ConsumptionPoint> getData(String query) {
        if(pingServer()) {
            // Run the query
            Query queryObject = new Query(query, dbName);
            QueryResult queryResult = influxDB.query(queryObject);

            // Map it
            InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
            return resultMapper.toPOJO(queryResult, ConsumptionPoint.class);
        } else {
            return new LinkedList<>();
        }
    }

    /**
     * Löscht Daten der Serie
     * @param name Name der Entität
     */
    public void delete(String name) {
        influxDB.query(new Query("DROP SERIES FROM \"consumption\" WHERE \"name\" = '" + name + "'"));
    }
}
