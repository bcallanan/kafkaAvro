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
public class AddressDTO {
    private String addressline1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
