package com.bcallanan.orders.producer.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bcallanan.domain.generated.PickUp;
import com.bcallanan.domain.generated.Status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull
    @NotEmpty
    private String name;
    
    @NotNull
    @NotEmpty
    private String nickName;
    
    private String nickName1;
    private StoreDTO store;
    private List<OrderItemDTO> orderItems;
    
//    @NotNull
//    @NotEmpty
    private PickUp pickUp;
    private Status status;
    private LocalDateTime orderedTime;
}
