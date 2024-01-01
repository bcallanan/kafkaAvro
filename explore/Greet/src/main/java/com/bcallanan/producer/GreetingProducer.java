/**
 * 
 */
package com.bcallanan.producer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.bcallanan.Greeting;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class GreetingProducer {

    private static final String GREETING_TOPIC = "greeting";

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
                ByteArraySerializer.class.getName());
        
        KafkaProducer< String, byte[]> kafkaProducer = new KafkaProducer<>( props );
        
        Greeting greeting = buildGreeting( "Hello, Schema Registry");
        byte[] byteArrayValue = greeting.toByteBuffer().array();
        
        //A key/value pair to be sent to Kafka. This consists of a topic name to which
        // the record is being sent, an optional partition number, and an optional key and value. 
        ProducerRecord< String, byte[]> producerRecord = new ProducerRecord<>( GREETING_TOPIC,
                byteArrayValue );
        
        // block call add the get
        var metaData = kafkaProducer.send(producerRecord).get();
        
        log.info( "avro record topic: {}, partition: {} ", metaData.topic(), metaData.partition(), metaData.toString());
    }

    private static Greeting buildGreeting( String greetingString) {
 
        // TODO Auto-generated method stub
        return Greeting.newBuilder()
                .setGreeting(greetingString)
                .build();
    }   

}
