package kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemo {


    private static final String groupId = "FirstGroup";
    private static final String topic = "HelloTopic";
    final KafkaConsumer<String, String> consumer;

    public static void main(String[] args) {
        ConsumerDemo demo = new ConsumerDemo();
        demo.consume();
    }

    public ConsumerDemo() {
        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ProducerDemo.BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Turn off auto-commit so we have full control over when the offsets get committed. Otherwise offsets
        // get committed 5 seconds after calling poll(), regardless of what actually happens.
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // Shows how record retrieval can be throttled as needed
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        // create consumer
        consumer = new KafkaConsumer<String, String>(properties);
    }

    public void consume() {
        Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getName());

        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Cause an exception in the while loop of the main thread that will cause it to exit the loop
                consumer.wakeup();

                try {
                    // Wait for the mainThread to exit its loop
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            // subscribe consumer to our topic(s)
            consumer.subscribe(Arrays.asList(topic));

            // poll for new data
            while(true){
                ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

                for (ConsumerRecord<String, String> record : records){
                    logger.info("Key: " + record.key() + ", Value: " + record.value());
                    logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
                }

                consumer.commitSync();  // commit the offsets

                // Wait for a bit to simulate waiting for more records
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (WakeupException we) {
            logger.info("Wake up called");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Graceful close. Also commits offsets if needed.
            consumer.close();
        }

    }
}
