package kafka.tutorial1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ProducerDemo {

    // The WSL2 IP address defined in the advertised.listeners property of the broker set in
    // ~/dev/kafka_2.13-3.1.0/config/server.properties.
    public static final String BOOTSTRAP_SERVERS = "172.22.80.131:9092";

    public static void main(String[] args) {
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // create a producer record
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        boolean keepRunning = true;     // set to false with a debugger
        while (keepRunning) {
            Date now = new Date();
            ProducerRecord<String, String> record =
                new ProducerRecord<String, String>("HelloTopic",
                    String.format("hello from the Java side, the current date is %s", sdf.format(now)));

            // send data - asynchronous
            producer.send(record);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();

    }
}
