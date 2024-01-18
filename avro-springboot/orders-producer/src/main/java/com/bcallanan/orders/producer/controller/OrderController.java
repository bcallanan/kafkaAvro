/**
 * 
 */
package com.bcallanan.orders.producer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bcallanan.orders.producer.dto.OrderDTO;
import com.bcallanan.orders.producer.dto.OrderUpdateDTO;
import com.bcallanan.orders.producer.service.OrderProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@RestController
@Slf4j
public class OrderController {
    
    private OrderProducerService orderProducerService;
    
    
    /**
     * @param orderProducerService
     */
    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }


    @ResponseStatus( HttpStatus.CREATED )
    @PostMapping( value={ "/v1/orders" })
    public OrderDTO newOrder( @RequestBody @Valid OrderDTO orderDTO ) throws JsonProcessingException {

        log.info( "Order received: {}", orderDTO);
        
        return orderProducerService.newOrder( orderDTO );
    }

    @ResponseStatus( HttpStatus.OK )
    @PutMapping( value={ "/v1/orderUpdate/{order_id}" })
    public OrderUpdateDTO updateOrder( @PathVariable( "order_id") String orderId,
            @RequestBody @Valid OrderUpdateDTO orderUpdateDTO) throws JsonProcessingException {

        log.info( "Order received: {}", orderUpdateDTO);
        
        return orderProducerService.updateOrder( orderId, orderUpdateDTO );
    }
}
