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

import com.bcallanan.domain.generated.OrderStatus;
import com.bcallanan.domain.generated.OrderUpdate;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class AvroSchemRegistryUpdateProducer {

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
                KafkaAvroSerializer.class.getName());
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class.getName());
        props.put( KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://192.168.99.108:38081");
        props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY,
                TopicRecordNameStrategy.class.getName());
        
        KafkaProducer< String, OrderUpdate> kafkaProducer = new KafkaProducer<>( props );
        
        OrderUpdate order = buildOrderUpdateEvent();
        
        //A key/value pair to be sent to Kafka. This consists of a topic name to which
        // the record is being sent, an optional partition number, and an optional key and value. 
        ProducerRecord< String, OrderUpdate> producerRecord = 
                new ProducerRecord<>( ORDER_SCHEMA_TOPIC, order.getId().toString(), order );
        
        // block call add the get
        var metaData = kafkaProducer.send(producerRecord).get();
        
        kafkaProducer.close();
        System.out.println("update avro record topic: " +  metaData.topic() + " : " + metaData.toString());
        System.out.println("published the producer record: " +  producerRecord );
        log.info( "avro record topic: {}, partition: {} ", metaData.topic(), metaData.partition(), metaData.toString());
    }

    private static OrderUpdate buildOrderUpdateEvent() {
        return OrderUpdate.newBuilder()
            .setId( "19f712f1-3bb8-45d7-8f7a-332ee66c8dbc" )
            .setStatus( OrderStatus.PROCESSING)
            .build();
    }
}
