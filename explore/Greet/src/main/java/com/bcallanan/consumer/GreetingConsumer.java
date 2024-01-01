/**
 * 
 */
package com.bcallanan.consumer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.bcallanan.Greeting;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class GreetingConsumer {

    private static final String GREETING_TOPIC = "greeting";

    /**
     * @param args
     * @throws IOException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Properties props = new Properties();
        
        props.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.108:39092");
        props.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ByteArrayDeserializer.class.getName());
        props.put( ConsumerConfig.GROUP_ID_CONFIG, "greeting.consumer");
        
        KafkaConsumer< String, byte[]> kafkaConsumer = new KafkaConsumer<>( props );
        kafkaConsumer.subscribe( Collections.singletonList( GREETING_TOPIC) );

        while( true ) {
            
            ConsumerRecords<String, byte[]>  records = kafkaConsumer.poll( Duration.ofMillis( 100 ));
            
            records.forEach( ( record) -> {
                
                Greeting greeting = null;
                try {
                    greeting = decodeAvroGreeting( record.value() );
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                log.info( "Consumer message: {}", greeting);
            });
        }
    }

    private static Greeting decodeAvroGreeting(byte[] greetingEncodedTextArray ) throws IOException {
        // TODO Auto-generated method stub
        return Greeting.fromByteBuffer( ByteBuffer.wrap(greetingEncodedTextArray) );
    }

    private static Greeting buildGreeting( String greetingString) {
 
        // TODO Auto-generated method stub
        return Greeting.newBuilder()
                .setGreeting(greetingString)
                .build();
    }   

}
