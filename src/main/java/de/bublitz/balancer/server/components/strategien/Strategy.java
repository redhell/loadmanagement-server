package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.AbstractConsumer;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
@Log4j2
public abstract class Strategy {

    private final LoadStrategy loadStrategy;

    protected Anschluss anschluss;
    protected LinkedList<ChargeBox> chargingList;
    protected LinkedList<ChargeBox> suspended;
    protected List<ChargeBox> tmpSuspended;
    protected double anschlussLoad;

    public Strategy(Anschluss anschluss, LoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
        chargingList = new LinkedList<>();
        suspended = new LinkedList<>();
        tmpSuspended = new LinkedList<>();
        this.anschluss = anschluss;
        anschlussLoad = anschluss.getCurrentLoad();
    }

    public abstract void optimize() throws NotStoppedException;

    public abstract void addLV(ChargeBox chargeBox);

    public void removeLV(ChargeBox chargeBox) {
        if (chargingList.contains(chargeBox)) {
            chargingList.remove(chargeBox);
        } else {
            suspended.remove(chargeBox);
        }
    }

    public void calculateFitting(double tmpLoad) {
        double restCapacity = anschluss.getHardLimit() - tmpLoad;

        // check tmpSuspended first
        List<Boolean> result = runKnapsack(tmpSuspended, restCapacity);
        restCapacity -= readdChargeBox(tmpSuspended, result);

        // check already suspended
        result = runKnapsack(suspended, restCapacity);
        restCapacity -= readdChargeBox(suspended, result);
    }

    private double readdChargeBox(List<ChargeBox> chargeBoxList, List<Boolean> results) {
        double tmpCapacity = 0;
        List<ChargeBox> tmpCbList = new LinkedList<>();
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i)) {
                ChargeBox tmpSuspendedBox = chargeBoxList.get(i);
                log.debug("Readding " + tmpSuspendedBox.getName());
                tmpCbList.add(tmpSuspendedBox);
                chargingList.add(tmpSuspendedBox);
                tmpCapacity += tmpSuspendedBox.getCurrentLoad();
            }
        }

        chargeBoxList.removeAll(tmpCbList);
        return tmpCapacity;
    }

    private List<Boolean> runKnapsack(List<ChargeBox> suspendedList, double restCapacity) {
        int n = suspendedList.size();
        List<Integer> wtList = new LinkedList<>();
        suspendedList.forEach(c -> {
            double cbLoad;
            if (c.getCurrentLoad() > c.getIdleConsumption()) {
                cbLoad = c.getCurrentLoad();
            } else {
                cbLoad = c.getLastLoad();
            }
            wtList.add((int) Math.ceil(cbLoad));
        });
        int[] wt = wtList.stream().mapToInt(Integer::intValue).toArray();
        int[] val = new int[n];
        Arrays.fill(val, 1);
        return knapSack((int) Math.floor(restCapacity), wt, val, n);
    }


    public List<Boolean> knapSack(int W, int[] wt, int[] val, int n) {
        List<Boolean> booleans = new LinkedList<>();
        if (n <= 0 || W <= 0) {
            return booleans;
        }
        int i, w;
        int[][] K = new int[n + 1][W + 1];

        // Build table K[][] in bottom up manner
        for (i = 0; i <= n; i++) {
            for (w = 0; w <= W; w++) {
                if (i == 0 || w == 0)
                    K[i][w] = 0;
                else if (wt[i - 1] <= w)
                    K[i][w] = Math.max(val[i - 1] + K[i - 1][w - wt[i - 1]], K[i - 1][w]);
                else
                    K[i][w] = K[i - 1][w];
            }
        }

        int ind = n;
        int weight = W;
        while (ind > 0) {
            if (K[ind][weight] != K[ind - 1][weight]) {
                booleans.add(true);
                weight -= wt[ind - 1];
            } else {
                booleans.add(false);
            }
            ind -= 1;
        }
        return booleans;
        //return K[n][W];
    }

    public String printChargingList() {
        return getString(chargingList);
    }

    public String printSuspendedList() {
        return getString(suspended);
    }

    public String printConsumerList() {
        return getString(anschluss.getConsumerList());
    }

    public String printConsumerLoad() {
        List<Consumer> consumerList = anschluss.getConsumerList();
        if (consumerList.isEmpty()) {
            return "{}";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        consumerList.forEach(consumer -> {
            stringBuilder.append(consumer.getCurrentLoad()).append("A, ");
        });
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length() - 1, "}");
        return stringBuilder.toString();
    }

    private <T extends AbstractConsumer> String getString(List<T> chargeBoxList) {
        if (chargeBoxList.size() == 0) {
            return "{}";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        chargeBoxList.forEach(chargeBox -> {
            stringBuilder.append(chargeBox.getName()).append(", ");
        });
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length() - 1, "}");
        return stringBuilder.toString();
    }

    protected boolean stop(ChargeBox chargeBox) {
        chargeBox.setLastLoad(chargeBox.getCurrentLoad());
        // Test?
        if (chargeBox.getStopURL().contains("testStop")) {
            chargeBox.setCurrentLoad(0);
            return true;
        }
        try {
            return queryURL(chargeBox.getStopURL());
        } catch (IOException ex) {
            log.error("Could not stop charging session");
            log.error(ex.getMessage());
        }
        return false;
    }

    protected boolean start(ChargeBox chargeBox) {
        if (chargeBox.getStartURL().contains("testStart")) {
            if (chargeBox.getLastLoad() > 0)
                chargeBox.setCurrentLoad(chargeBox.getLastLoad());
            return true;
        }
        try {
            return queryURL(chargeBox.getStartURL());
        } catch (IOException ex) {
            log.error("Could not start charging session");
            log.error(ex.getMessage());
        }
        return false;
    }

    private boolean queryURL(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(url));
        return response.getCode() == 200;
    }
}
