/**
 * 
 */
package com.bcallanan.orders.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {

    private String name;
    private SizeEnum size;
    private Integer quantity;
    private Float cost; 
    
}
