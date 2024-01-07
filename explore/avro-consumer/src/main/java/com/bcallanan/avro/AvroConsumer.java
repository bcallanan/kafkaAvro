/**
 * 
 */
package com.bcallanan.avro;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.bcallanan.domain.generated.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class AvroConsumer {

    private static final String ORDER_TOPIC = "orders";

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
        props.put( ConsumerConfig.GROUP_ID_CONFIG, "order.consumer");
        
        KafkaConsumer< String, byte[]> kafkaConsumer = new KafkaConsumer<>( props );
        kafkaConsumer.subscribe( Collections.singletonList( ORDER_TOPIC) );

        while( true ) {
            
            ConsumerRecords<String, byte[]>  records = kafkaConsumer.poll( Duration.ofMillis( 100 ));
            
            records.forEach( ( record) -> {
                
                Order order = null;
                try {
                    order = decodeAvroOrder( record.value() );
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    log.error( "exception: {}", e.getMessage(), e);
                }
                System.out.println( "Order " + order);
                log.info( "Consumer message: {}", order);
            });
        }
    }

    private static Order decodeAvroOrder(byte[] orderEncodedTextArray ) throws IOException {
        return Order.fromByteBuffer( ByteBuffer.wrap( orderEncodedTextArray) );
    }
}
