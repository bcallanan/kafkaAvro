package com.bcallanan.orders.producer.kafka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.bcallanan.domain.generated.Order;
import com.bcallanan.domain.generated.OrderUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaOrderUpdatePublisher {

	@Value("${spring.kafka.topic}")
	public String topic;

	private final KafkaTemplate<String, OrderUpdate> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaOrderUpdatePublisher(KafkaTemplate<String, OrderUpdate> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}
	
	public CompletableFuture<SendResult<String, OrderUpdate>> sendOrderUpdateEventRecordASync( OrderUpdate order) 
			throws JsonProcessingException {

		String key = order.getId().toString();
		//String value = objectMapper.writeValueAsString( order );
		
		// 1) Blocking call - get metadata about the kafka cluster
		//    if fails go into the failure scenario
		//    else it succeeds
		// 2) Sends message happens and returns completable Future
		CompletableFuture<SendResult<String, OrderUpdate>> completableFuture = 
				kafkaTemplate.send( topic, key, order );
		
		// org.apache.j=kafka.common.errors.TimeoutException: Topic store-events not present in metadata after <timeout-value>

		return completableFuture.whenComplete((sendResult, throwable ) -> {
			
			if ( throwable != null ) {
				handleFailure( key, order, throwable);
			}
			else {
				handleSuccess( key, order, sendResult);
			}
		});
	}

    /**
     * Uses an async call with providing a producer record
     * 
     * @param storeEventDTO
     * @return
     * @throws JsonProcessingException
     */
    public CompletableFuture<SendResult<String, OrderUpdate>> sendOrderEventRecord( OrderUpdate order ) 
            throws JsonProcessingException {

        String key = order.getId().toString();
        
        var producerRecord = buildProducerRecord( key, order );
        // 1) Blocking call - get metadata about the kafka cluster -- controlled by time out value max.block.ms = 60 sec
        //    if fails go into the failure scenario
        //    else it succeeds
        
        // org.apache.j=kafka.common.errors.TimeoutException: Topic store-events not present in metadata after <timeout-value>
        
        
        // 2) Sends message happens and returns completable Future
                    //see retries config value
                    // there's also a backoff value 
        CompletableFuture<SendResult<String, OrderUpdate>> completableFuture = 
                kafkaTemplate.send( producerRecord );
        
        return //CompletableFuture<SendResult<String, Order>> future = 
                completableFuture.whenComplete((sendResult, throwable ) -> {
            
            if ( throwable != null ) {
                handleFailure( key, order, throwable);
            }
            else {
                handleSuccess( key, order, sendResult);
            }
        });
    }
	
    /**
     * Builds a kafka Record with headers
     * 
     * @param key
     * @param value
     * @return
     */
    private ProducerRecord<String, OrderUpdate> buildProducerRecord(String key, OrderUpdate value) {
        
        return new ProducerRecord<>( topic, null, key, value);//, recordHeaders);
    }

	/**
	 * This syncronous method waits for the kafka record to be sent and then is followed by a get which causes it to 
	 * wait until the event is completed.
	 *  
	 * @param storeEventDTO
	 * @return
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public SendResult<String, OrderUpdate> sendOrderEventRecordSyncApproach( OrderUpdate order ) 
			throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

		String key = order.getId().toString();
//		Order value = objectMapper.writeValueAsString( orderDTO);
		
		// 1) Blocking call - get metadata about the kafka cluster
		//    if fails go into the failure scenario
		//    else it succeeds
		// 2) Block and wait until the message is sent to the kafka cluster
		SendResult<String, OrderUpdate> result = kafkaTemplate
				.send( topic, key, order )
				.get( 3, TimeUnit.SECONDS); //<---------- cause a wait
				//.get(); add a time out instead
		
		// org.apache.j=kafka.common.errors.TimeoutException: Topic store-events not present in metadata after <timeout-value>

		handleSuccess( key, order, result);
		
		return result;
	}

	private void handleSuccess(String key, OrderUpdate value, SendResult<String, OrderUpdate> sendResult) {
		// TODO Auto-generated method stub
		log.info( "Message sent successfully: key {}, value: {}, portition:{} ",
				key, value, sendResult.getRecordMetadata().partition() );
	}

	private void handleFailure(String key, OrderUpdate value, Throwable throwable) {
		log.error( "Error sending the message and te exception is {}", throwable.getMessage(), throwable );
	}
}
