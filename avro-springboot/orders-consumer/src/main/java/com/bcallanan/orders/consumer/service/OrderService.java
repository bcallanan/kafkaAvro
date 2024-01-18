package com.bcallanan.orders.consumer.service;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import com.bcallanan.domain.generated.Order;
import com.bcallanan.domain.generated.OrderUpdate;
import com.bcallanan.domain.generated.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	ObjectMapper objectMapper;
	
//	@Autowired
//	private OrderRepository OrderRepository;
	
	public void processOrderEvent( ConsumerRecord< String, GenericRecord> consumerRecord ) 
	        throws JsonMappingException, JsonProcessingException {
		
        var order = consumerRecord.value();
        order.getClass().toString();
        if ( consumerRecord.value() instanceof Order ) {
            order = (Order) consumerRecord.value();
            log.info( "generic order {}", order);
        }
        else if ( consumerRecord.value() instanceof OrderUpdate ) {
            order = (OrderUpdate) consumerRecord.value();
        }
        //Order order = objectMapper.readValue( consumerRecord.value(), Order.class);
		
		log.info( "order Event object {}", order);
		
		// used mainly in mocking out the integration test with a customized exception on a special instance
		if ( order != null  ) {
		    if ( ! ((Order) order).getStatus().equals( Status.NEW)  &&
				//order.getStoreEventId() != null && // with a 'new' message event enum type it will be null
		            ((Order)order).getId().equals( "999" )) {
		        throw new RecoverableDataAccessException("Temporary exception case" );
		    }
		
    		switch ( ((Order)order).getStatus()) {
    			case NEW:
    				// Create/save event type
    				save( order );
    				break;
    				
    			case UPDATED:
    				// update event type
    				// validate the store event
    				//validate( order);
    				// then save the event
    				save( order );
    				break;
    				
    			default:
    				log.info( "Invalid Store Event Enum Type.");
    		}
		}
	}

//	/**
//	 * 
//	 * @param storeEvent
//	 */
//	 private void validate(Order order) {
//		 
//		 if ( order.getId() == null ) {
//				// the Kafka listener handler will handle this exception and will print to the logger
//			 throw new IllegalArgumentException( "Store Event Id is missing."); 
//		 }
//		 
//		Optional< order > orderOptional = orderRepository.findById( order.getId() );
//		if ( ! orderOptional.isPresent() ) {
//			// the Kafka listener handler will handle this exception and will print to the logger
//			throw new IllegalArgumentException( "Not a valid Order Event. OrderId mismatch"); 
//		}
//		 
//		log.info( "Successfully validated the persisted Order Event exists for update: {}",
//				orderOptional.get());
//	}

	private void save( GenericRecord order) {
		
//		// set the reference in the book for the event
//		order.getStore().setOrder(order);
//		
		//Persist the event
		//orderRepository.save( order );
		
		log.info( "Successfully Persisted the event {}", order);
	}
	
	
}
