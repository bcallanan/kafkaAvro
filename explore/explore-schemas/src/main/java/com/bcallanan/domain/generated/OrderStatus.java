/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.bcallanan.domain.generated;
@org.apache.avro.specific.AvroGenerated
public enum OrderStatus implements org.apache.avro.generic.GenericEnumSymbol<OrderStatus> {
  PROCESSING, READY_FOR_PICKUP  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"OrderStatus\",\"namespace\":\"com.bcallanan.domain.generated\",\"symbols\":[\"PROCESSING\",\"READY_FOR_PICKUP\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
