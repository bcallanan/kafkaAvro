# kafkaAvro

Event message serialization in Kafka is based on two options:

1) Binary Serialization
    - Serialized into a byte array
    - Non-human readable
    - More efficient cause the data is compact and consumes less memory
    - Serialization process is generally faster 
     
    Options: AVRO, ProtocolBuf(google), Thrift(facebook)
    
    The benefit of these serializable options is that they all support the Scheme Interface definition language (S-IDL). The data is a fixed data structure that guarantees the structure of the kafka message. 
    
    One of the main reason for the popularity of AVRO is that the schema definition is still supported in JSON, while the others have their own language definitions and thus has higher learner curves.
    
    What is AVRO? -> https://www.confluent.io/blog/avro-kafka-data/
    
1) PlainText Serialization
    - Serialized as encoded text
    - Human readable (JSON/XML)
    - Data is verbose and can be inefficient
    - Serialization process is slower compared to binary serialization
    
    Options: JSON, XML
    
Over the next few pages of this readme document, we'll review how to develop a serialized AVRO Schema to serialize a payload as a Producer and de-serialize the same payload with the AVRO Schema as a Consumer. 

We'll also show how to migrate the events from one registry version to the next.

Then we'll also persist the transacted events after consumption in a JDBC Persistent store (Postgres) and more.


