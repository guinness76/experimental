package org.experimental.metrics;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * Created by mattgross on 3/14/2016.
 */
public class MetricsTests {

    @Test
    public void testMetrics() throws Exception{

        MetricRegistry registry = new MetricRegistry();

        /* Counter- can be incremented or decremented at any time. Can be used to represent a capacity. */
        Counter counter = registry.counter("counter");

        /* Gauge- pulls the most recent value */
        CpuGauge cpuGauge = new CpuGauge();
        String cpuName = MetricRegistry.name("CpuStatus", "load");
        registry.register(cpuName, cpuGauge);

        /* Meter- measures how many times per second/minute/5 minutes/15 minutes a particular event occurs,
         * Be forewarned: the meter count returns a count of all events since the startup of the application.
         * This count cannot be reset. You will need to use methods like getOneMinuteRate() to return stats
         * for the last minute.
         */
        Meter cpuThresholdEvents = registry.meter("CpuStatus.thresholdEvents");
        int threshold = 40;

        /* Histogram: given a collection of values, the histogram provides some stats on the collection. Stats include
            minimum value, maximum value, calculation of 95th percentile, etc. The values are sorted as they are calculated,
            so their arrangement does not provide history. Like the meter, calling histogram.getCount() will return
            a count of all events since application startup. You will need to use histogram.getSnapshot() to get
            the most recent stats.
            Also see: http://stackoverflow.com/questions/30987757/dropwizard-metrics-meters-vs-timers
         */
        Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
        registry.register("CpuStatus.histogram", histogram);

        /* Timer- times events and then provides a histogram later on the processing time of the recorded events. The
        * timer also provides a meter, which measures how often the event occurred. The timer does NOT give information
        * on the duration of a specific event. Rather, it gives trends on how long events are taking overall and can
        * show spikes that occur when specific events take longer than average.
        */
        Timer timer = registry.timer("testTime");

        Random r= new Random(new Date().getTime());

        int oneMinute = 600;
        int tenMinutes = oneMinute * 10;
        for(int i=0; i<tenMinutes; i++){
            Timer.Context context = timer.time();
            Thread.currentThread().sleep(100);
            int next = Math.abs(r.nextInt() % 100);
            cpuGauge.load = next;
            histogram.update(next);
            counter.inc();

            if(next >= threshold ){
                cpuThresholdEvents.mark();
            }
            context.stop();

            if(i % 600 == 0){
                System.out.printf("*** Minute Mark, histogram has recorded %d total events, with %d events in snapshot ***\n",
                        timer.getCount(), timer.getSnapshot().size());
            }
        }


        Map<String, Gauge> gaugeMap = registry.getGauges();
        Gauge finalGauge = gaugeMap.get(cpuName);

        Map<String, Meter> meterMap = registry.getMeters();
        cpuThresholdEvents = meterMap.get("CpuStatus.thresholdEvents");

        Map<String, Histogram> histogramMap = registry.getHistograms();
        histogram = histogramMap.get("CpuStatus.histogram");

        System.out.println("--- CPU stats ---");
        System.out.printf("Timer final count:%d\n", timer.getCount());
        System.out.printf("Timer mean rate:%f events per second\n", timer.getMeanRate());
        System.out.printf("Timer one minute rate:%f events per second\n", timer.getOneMinuteRate());
        System.out.printf("Timer snapshot median duration:%f ms\n", (timer.getSnapshot().getMedian()/1000)/1000);
        System.out.printf("Timer snapshot mean duration:%f\n", (timer.getSnapshot().getMean()/1000)/1000);
        System.out.printf("Timer snapshot min duration value:%d\n", (timer.getSnapshot().getMin()/1000)/1000);
        System.out.printf("Timer snapshot max duration value:%d\n", (timer.getSnapshot().getMax()/1000)/1000);
        System.out.printf("Timer snapshot standard deviation:%f\n", (timer.getSnapshot().getStdDev()/1000)/1000);
        System.out.printf("Timer snapshot 75th percentile duration:%f ms\n", (timer.getSnapshot().get75thPercentile()/1000)/1000);
        System.out.printf("Timer snapshot 99th percentile duration:%f ms\n", (timer.getSnapshot().get99thPercentile()/1000)/1000);
        System.out.printf("Number of samples as per counter:%d\n", counter.getCount());
        System.out.printf("CPU current load:%d\n", finalGauge.getValue());
        System.out.printf("Over threshold events since startup:%d\n", cpuThresholdEvents.getCount());
        System.out.printf("Over threshold one minute rate: %f events per second\n", cpuThresholdEvents.getOneMinuteRate());
        System.out.printf("Min value:%d\n", histogram.getSnapshot().getMin());
        System.out.printf("Max value:%d\n", histogram.getSnapshot().getMax());
//        long[] historicalValues = histogram.getSnapshot().getValues();
//        for(long value: historicalValues){
//            System.out.println(Strings.repeat("#", (int) value/10));
//        }

        /* Typical output:
*** Minute Mark, histogram has recorded 1 total events, with 1 events in snapshot ***
*** Minute Mark, histogram has recorded 601 total events, with 601 events in snapshot ***
*** Minute Mark, histogram has recorded 1201 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 1801 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 2401 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 3001 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 3601 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 4201 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 4801 total events, with 1028 events in snapshot ***
*** Minute Mark, histogram has recorded 5401 total events, with 1028 events in snapshot ***
--- CPU stats ---
Timer final count:6000
Timer mean rate:9.974950 events per second
Timer one minute rate:9.982098 events per second
Timer snapshot median duration:100.063215 ms
Timer snapshot mean duration:100.064394
Timer snapshot min duration value:99
Timer snapshot max duration value:100
Timer snapshot standard deviation:0.033083
Timer snapshot 75th percentile duration:100.070680 ms
Timer snapshot 99th percentile duration:100.103337 ms
Number of samples as per counter:6000
CPU current load:48
Over threshold events since startup:3610
Over threshold one minute rate: 6.146890 events per second
Min value:0
Max value:99


         */
    }

    private class CpuGauge implements Gauge<Integer> {
        public int load = 0;

        @Override
        public Integer getValue() {
            return load;
        }
    }
}
