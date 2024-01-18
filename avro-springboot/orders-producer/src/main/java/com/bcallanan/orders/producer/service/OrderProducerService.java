/**
 * 
 */
package com.bcallanan.orders.producer.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bcallanan.domain.generated.Address;
import com.bcallanan.domain.generated.Order;
import com.bcallanan.domain.generated.OrderItem;
import com.bcallanan.domain.generated.OrderUpdate;
import com.bcallanan.domain.generated.Status;
import com.bcallanan.domain.generated.Store;
import com.bcallanan.orders.producer.dto.AddressDTO;
import com.bcallanan.orders.producer.dto.OrderDTO;
import com.bcallanan.orders.producer.dto.OrderItemDTO;
import com.bcallanan.orders.producer.dto.OrderUpdateDTO;
import com.bcallanan.orders.producer.kafka.KafkaOrderPublisher;
import com.bcallanan.orders.producer.kafka.KafkaOrderUpdatePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 */
@Service
public class OrderProducerService {

    private KafkaOrderPublisher kafkaOrderPublisher;
    private KafkaOrderUpdatePublisher kafkaOrderUpdatePublisher;
    
    
    /**
     * @param kafkaOrderPublisher
     */
    public OrderProducerService(KafkaOrderPublisher kafkaOrderPublisher, KafkaOrderUpdatePublisher kafkaOrderUpdatePublisher) {
        this.kafkaOrderPublisher = kafkaOrderPublisher;
        this.kafkaOrderUpdatePublisher = kafkaOrderUpdatePublisher;
    }

    public OrderUpdateDTO updateOrder( String orderId,
            OrderUpdateDTO orderUpdateDTO) 
            throws JsonProcessingException {
        
        OrderUpdate orderUpdate = mapOrderUpdateResource( orderId, orderUpdateDTO );
        
        
        kafkaOrderPublisher.sendOrderUpdateEventRecord(orderUpdate);
        
        return orderUpdateDTO;
    }
    
    public OrderDTO newOrder( OrderDTO orderDTO) 
            throws JsonProcessingException {
        
        Order order = mapOrderResource( orderDTO );
        
        orderDTO.setId( order.getId().toString());
        
        kafkaOrderPublisher.sendOrderEventRecord(order);
        
        return orderDTO;
    }

    private OrderUpdate mapOrderUpdateResource( String orderId,
            OrderUpdateDTO orderUpdateDTO) {
        
        return OrderUpdate.newBuilder()
                .setId( UUID.fromString( orderId ) )
                .setStatus( orderUpdateDTO.getStatus() )
                .build();
    }
    
    private Order mapOrderResource( OrderDTO orderDTO) {
        
        Store store = getStore( orderDTO );
        List<OrderItem> orderItems = getOrderItems( orderDTO.getOrderItems() );
        
        return Order.newBuilder()
                .setId( UUID.randomUUID() )
                .setFullname( orderDTO.getName() )
                .setNickName( orderDTO.getNickName())
                .setStore( store )
                .setPickUp( orderDTO.getPickUp())
                .setOrderedTime( Instant.now())
                //.setOrderedTime( orderDTO.getOrderedTime().toInstant( ZoneOffset.UTC))
                .setOrderItems( orderItems )
                .setStatus( Status.NEW )
                .build();
    }

    private OrderItem getOrderItem( OrderItemDTO orderItemDTO ) {
     
        return OrderItem.newBuilder()
                .setName( orderItemDTO.getName())
                .setQuantity( orderItemDTO.getQuantity())
                .setSize( orderItemDTO.getSize())
                .setCost( orderItemDTO.getCost())
                .build();
    }
    private List<OrderItem> getOrderItems(List<OrderItemDTO> orderItems) {
 
        return orderItems.stream()
                .map( orderItemDTO -> getOrderItem( orderItemDTO ))
                .collect( Collectors.toList());
    }

    private Store getStore(OrderDTO orderDTO) {
        
        Address address =
                getStoreAddress( orderDTO.getStore().getAddress() );
        
        return Store.newBuilder()
               .setAddress( address )
               .setStoreId( orderDTO.getStore().getStoreId())
               .build();
    }
    private Address getStoreAddress(AddressDTO addressDTO) {
        return Address.newBuilder()
                .setAddressLine1( addressDTO.getAddressLine1())
                .setAddressLine2( addressDTO.getAddressLine2())
                .setCity( addressDTO.getCity())
                .setCountry( addressDTO.getCountry())
                .setState( addressDTO.getState())
                .setZip( addressDTO.getZip())
                .build();
    }

}
