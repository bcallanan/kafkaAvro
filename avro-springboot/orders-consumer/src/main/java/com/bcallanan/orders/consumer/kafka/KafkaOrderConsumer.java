package com.bcallanan.orders.consumer.kafka;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bcallanan.orders.consumer.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaOrderConsumer {

    @Value("${spring.kafka.topic}")
    public String topic;

    @Autowired
    private OrderService orderService;
    
    /**
     * This method annotates and autowires the KafkaListener. This is instrumental to receiving
     * consumable kafka messages from the KafkaListenerContainerFactory. The container factory is
     * set appropriately by default.
     *   
     * We are also using the KafkaConsumerFactory -> DefaultKafkaConsumerFactory
     * 
     * The @KafkaListener Annotation uses the ConcurrentMessageListenerContainer behind the scenes
     *   
     * @param consumerRecord
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     * 
     */
    @KafkaListener( topics = { "${spring.kafka.topic}" },
           // autoStartup = "${coffeeOrdersConsumer.startup:true}"
        groupId = "${spring.kafka.consumer.group-id}")//, containerFactory = "KafkaListenerContainerFactory")
    public void onMessage( ConsumerRecord<String, GenericRecord> consumerRecord)
            throws JsonMappingException, JsonProcessingException {
        
        log.info( "ConsumerRecord: {}", consumerRecord);
        orderService.processOrderEvent( consumerRecord );
        
    }
}
