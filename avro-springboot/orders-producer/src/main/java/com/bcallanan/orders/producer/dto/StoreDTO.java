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
public class StoreDTO {
    private Integer id;
    private AddressDTO address;
}
