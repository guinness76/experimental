package org.experimental.protobuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Example of a class that hasn't been Protobuffed yet
public class AgentMetricsReport implements Serializable {
    private final List<Double> metrics = new ArrayList<>();
    private final List<String> events = new ArrayList<>();

    public void addMetric(Double theMetric) {
        metrics.add(theMetric);
    }

    public void addEvent(String theEvent) {
        events.add(theEvent);
    }

    public List<Double> getMetrics() {
        return metrics;
    }

    public List<String> getEvents() {
        return events;
    }
}
