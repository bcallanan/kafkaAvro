/**
 * 
 */
package com.bcallanan.avro;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.bcallanan.avro.util.OrderUtil;
import com.bcallanan.domain.generated.Order;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class AvroSchemRegistryProducer {

    private static final String ORDER_SCHEMA_TOPIC = "orders-sr";

    /**
     * @param args
     * @throws IOException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Properties props = new Properties();
        
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.108:39092");
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class.getName());
        props.put( KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "192.168.99.108:38081");
        
        KafkaProducer< String, Order> kafkaProducer = new KafkaProducer<>( props );
        
        Order order = OrderUtil.buildNewOrder();
        byte[] byteArrayValue = order.toByteBuffer().array();
        
        //A key/value pair to be sent to Kafka. This consists of a topic name to which
        // the record is being sent, an optional partition number, and an optional key and value. 
        ProducerRecord< String, Order> producerRecord = 
                new ProducerRecord<>( ORDER_SCHEMA_TOPIC, order );
        
        // block call add the get
        var metaData = kafkaProducer.send(producerRecord).get();
        
        kafkaProducer.close();
        System.out.println(metaData );
        log.info( "avro record topic: {}, partition: {} ", metaData.topic(), metaData.partition(), metaData.toString());
    }
}
