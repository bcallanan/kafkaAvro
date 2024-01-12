package com.bcallanan.orders.producer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private String id;
    
    private String name;
    private String nickname;
    private String nickname1;
    private StoreDTO store;
    private List<OrderItemDTO> orderItems;
}
