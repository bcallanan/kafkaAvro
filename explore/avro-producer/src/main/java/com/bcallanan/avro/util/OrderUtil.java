package com.bcallanan.avro.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import com.bcallanan.domain.generated.Address;
import com.bcallanan.domain.generated.Order;
import com.bcallanan.domain.generated.OrderId;
import com.bcallanan.domain.generated.OrderItem;
import com.bcallanan.domain.generated.Size;
import com.bcallanan.domain.generated.Status;
import com.bcallanan.domain.generated.Store;

public class OrderUtil {

    public static Order buildNewOrder(){
        OrderId orderId=   OrderId.newBuilder()
                .setId(randomId())
                .build();

        return Order.newBuilder()
                .setId( orderId)
                // .setId(UUID.randomUUID())
                .setName("Brian Callanan")
                //.setNickName("BBB")
                // .setFullName("Brian Paul Callanan")
                .setStore(generateStore())
                .setOrderItems(generateOrderItems())
                .setOrderedTime(Instant.now())
                //.setPickUp(PickUp.IN_STORE)
                //.setPickUpType(PickUp.IN_STORE)
                .setStatus( Status.NEW)
                .build();


    }
//        public static OrderOld buildNewOrderV2(){
//
//            return CoffeeOrderOld.newBuilder()
//                    .setId(randomId())
//                    .setName("Chicago 1234")
//                    .setStore(generateStore())
//                    .setOrderLineItems(generateOrderLineItems())
//                    .build();
//
//
//        }
    
    private static List<OrderItem> generateOrderItems() {

        OrderItem orderItem = OrderItem.newBuilder()
                .setName("Caffe Latte")
                .setQuantity(1)
                .setSize(Size.MED)
                .setCost(BigDecimal.valueOf(3.99))
                .build();

        return List.of(orderItem);

    }

    private static Store generateStore(){

        return  Store.newBuilder()
                .setId(randomId())
                .setAddress( buildAddress() )
                .build();
    }

    private static Address buildAddress() {

        return Address.newBuilder()
                .setAddressLine1("1234 Address Line 1")
                .setCity( "Chicago" )
                .setState( "IL" )
                .setCountry( "USA" )
                .setZip("12345")
                .build();

    }

    public static int randomId(){
        Random random = new Random();
        return random.nextInt(1000);
    }
}