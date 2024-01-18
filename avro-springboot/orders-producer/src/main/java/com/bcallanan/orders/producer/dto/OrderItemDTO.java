/**
 * 
 */
package com.bcallanan.orders.producer.dto;

import java.math.BigDecimal;

import com.bcallanan.domain.generated.Size;

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
    private Size size;
    private Integer quantity;
    private BigDecimal cost; 
}
