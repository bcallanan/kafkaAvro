/**
 * 
 */
package com.bcallanan.avro;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.bcallanan.domain.generated.Order;
import com.bcallanan.domain.generated.OrderId;
import com.bcallanan.domain.generated.OrderUpdate;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class AvroSchemaRegistryConsumer {

    private static final String ORDER_SCHEMA_TOPIC = "orders-sr";

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
                KafkaAvroDeserializer.class.getName());
        props.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                KafkaAvroDeserializer.class.getName());
        props.put( KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://192.168.99.108:38081");
        
        //Without this property the event record is treated as a "generic record" and
        //won't deserialize against the schema 
        props.put( KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        
        props.put( ConsumerConfig.GROUP_ID_CONFIG, "order-sr.consumer");
        
        //KafkaConsumer< OrderId, Order> kafkaConsumer = new KafkaConsumer<>( props );
        KafkaConsumer< OrderId, GenericRecord> kafkaConsumer = new KafkaConsumer<>( props );
        kafkaConsumer.subscribe( Collections.singletonList( ORDER_SCHEMA_TOPIC) );

        while( true ) {
            
            ConsumerRecords<OrderId, GenericRecord>  records = kafkaConsumer.poll( Duration.ofMillis( 100 ));
            //ConsumerRecords<OrderId, Order>  records = kafkaConsumer.poll( Duration.ofMillis( 100 ));
            
            records.forEach( ( record) -> {
                
                var order = record.value();
//                try {
//                    order = decodeAvroOrder( record.value() );
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    log.error( "exception: {}", e.getMessage(), e);
//                }
                
                String zone = ZoneId.SHORT_IDS.get( "EST");
                if ( order instanceof OrderUpdate) {
                    System.out.println( "Order update " + order.toString());
                   
                }
                else if ( order instanceof Order ) {
                
                    LocalDateTime.ofInstant( ((Order) order).getOrderedTime(), ZoneId.of( zone ));
                    System.out.println( "Order " + order.toString());
                }
                log.info( "Consumer message: {}", order);
            });
        }
    }

    private static Order decodeAvroOrder(byte[] orderEncodedTextArray ) throws IOException {
        return Order.fromByteBuffer( ByteBuffer.wrap( orderEncodedTextArray) );
    }
}
