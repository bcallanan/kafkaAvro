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
    
Over the next few pages of this read-me document, we'll review how to develop a serialized AVRO Schema to serialize a payload as a Producer and de-serialize the same payload with the AVRO Schema as a Consumer. 

We'll also show how to migrate the events from one registry version to the next.

Then we'll also persist the transacted events after consumption in a JDBC Persistent store (Postgres) and more.


#### Docker configuration

Setting up a Docker Environment with Zookeeper, Broker, and a Schema Registry container. I've included the docker-compose within the repo. When starting the containers its best to monitor the logs.

      docker logs -f avro-springboot_zk1_1
      docker logs -f avro-springboot_broker_1
      docker logs -f schema-registry
      
There should not be any exceptions in ay of the logs. Wait for the sequence to calm and use the next commands to test the environment:

      alias broker="winpty docker exec -it avro-springboot_broker_1 bash"
      alias schema="winpty docker exec -it schema-registry bash"
 
#### Testing the broker
      
Initially for the broker you'll need 2 windows where you've connected with the <i><b>"broker"</b></i> alias. In one window using the following:

    kafka-console-producer --broker-list localhost:9092 --topic test-topic
      
In the second, use the following command:

    kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic      
  
In the producer terminal, type in any string and hit return. the message should show up in the consumer window. If it does, and there are no exceptions you broker is working. If not, its broken and something is spitting out in the logs.

#### Testing the schema       
              
The Avro Schema is tested on the other container much in the same way. Use 2 windows and connect to the schema container with the <i><b>"schema"</b></i> alias

In one window using the following to add some schema to the environment.

    kafka-avro-console-producer --broker-list broker:39092 --topic greetings --property value.schema='{"type": "record","name":"Greeting","fields": [{"name": "greeting","type": "string"}]}'

In the second window perform the following:

    kafka-avro-console-consumer --bootstrap-server broker:39092 --topic greetings --from-beginning
    
The in the first window, enter the following:

    {"greeting": "Good Morning!, AVRO"}
    {"greeting": "Good Evening!, AVRO"}
    {"greeting": "Good Night!, AVRO"}

If there are no exceptions, and the messages are printed in the avro consumer, you're good! Then you can enter the following bad message and you'll get the exception showing it failed the schema:

    greetings
    [2023-12-31 14:44:33,372] INFO [Producer clientId=console-producer] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms. (org.apache.kafka.clients.producer.KafkaProducer)
    [2023-12-31 14:44:33,395] INFO Metrics scheduler closed (org.apache.kafka.common.metrics.Metrics)
    [2023-12-31 14:44:33,395] INFO Closing reporter org.apache.kafka.common.metrics.JmxReporter (org.apache.kafka.common.metrics.Metrics)
    [2023-12-31 14:44:33,396] INFO Metrics reporters closed (org.apache.kafka.common.metrics.Metrics)
    [2023-12-31 14:44:33,397] INFO App info kafka.producer for console-producer unregistered (org.apache.kafka.common.utils.AppInfoParser)
    org.apache.kafka.common.errors.SerializationException: Error deserializing json greetings to Avro of schema {"type":"record","name":"Greeting","fields":[{"name":"greeting","type":"string"}]}
        at io.confluent.kafka.formatter.AvroMessageReader.readFrom(AvroMessageReader.java:127)
        at kafka.tools.ConsoleProducer$$anon$1$$anon$2.hasNext(ConsoleProducer.scala:67)der.java:405)
        at kafka.tools.ConsoleProducer$.loopReader(ConsoleProducer.scala:90)
        at kafka.tools.ConsoleProducer$.main(ConsoleProducer.scala:99)
        at kafka.tools.ConsoleProducer.main(ConsoleProducer.scala)
    Caused by: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'greetings': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
    at [Source: (String)"greetings"; line: 1, column: 10]
        
Once through this there will be a functioning AVRO Kafka docker Env to run the micro-services.         


#### Avro Schema Reference

The reference material for defining schema types can be found [here](http://avro.apache.org/docs/current/spec.html 'Avro Schema reference').
 

Initial schema for the initial test case can be found [here](https://github.com/bcallanan/kafkaAvro/tree/main/explore/explore-schemas/src/main/avro). This example is w/o spring-boot and just pure 'org.apache.kafka' apis. This test case will test the schema as a published event record into Kafka and then can be decoded and consumed on the other side...

#### Avro Data Exchanges Reference

Avro Data that is encoded has no type definitions. So, the serializers perform schema validations. So, both the producers and consumers have or need the schema dependency.

The schema registery holds the schema for validation see [here](https://www.confluent.io/blog/schemas-contracts-compatibility/?_ga=2.205203091.2005966880.1650190514-1685861233.1648224453&_gac=1.154929994.1648739558.CjwKCAjwopWSBhB6EiwAjxmqDdc7q5-nqdT-Zx3DI64gxdYDjq4-Ile0txJr4rgDFYY4HAytwrpGZRoCNgUQAvD_BwE)

Here's an example diagram showing the integration of the schema registry.

![Alt text](./SchemaRegistry.jpg?raw=true "Avro Schema Registry")

#### Schema Registry

The Avro Schema registry is just like every other RESTful platform and supports the following resource types:
  1) Subjects: Fundamentally a scope in which the Schema's evolve. (eg Versioning)
  1) Schemas: The resource that is used to represent a schema (get by id)
  1) Config: The resource that is used to update the cluster-level config for the schema registry
  1) Compatibility: The resource that is used to check the compatibility between schemas
  
You can use the following postman collection for access, [here](./schema-registry.postman_collection.json). Substitute in the url IP/Port.

Follow the link to access the API reference documentation [here](https://docs.confluent.io/platform/current/schema-registry/develop/api.html)

Here's an example of the schema at this juncture:
<details>
   <summary>(click-me)</summary>

    {"type": "record",
    "name": "Order",
    "namespace": "com.bcallanan.domain.generated",
    "fields": [
        {   "name": "id",
            "type": {
                "type": "record",
                "name": "OrderId",
                "fields": [{
                        "name": "id",
                        "type": "int"
                    }
                ]
            }
        },
        {   "name": "name",
            "type": "string"
        },
        {   "name": "nickname",
            "type": "string",
            "doc": "Optional nick name field",
            "default": ""
        },
        {   "name": "store",
            "type": {
                "type": "record",
                "name": "Store",
                "fields": [{
                        "name": "id",
                        "type": "int"
                    },
                    {   "name": "address",
                        "type": {
                            "type": "record",
                            "name": "Address",
                            "fields": [{
                                    "name": "addressLine1",
                                    "type": "string"
                                },
                                {   "name": "addressLine2",
                                    "type": "string",
                                    "doc": "Optional send address line",
                                    "default": ""
                                },
                                {   "name": "city",
                                    "type": "string"
                                },
                                {   "name": "state",
                                    "type": "string"
                                },
                                {   "name": "country",
                                    "type": "string"
                                },
                                {   "name": "zip",
                                    "type": "string"
                                }
                            ]
                        }
                    }
                ]
            }
        },
        {   "name": "OrderItems",
            "type": {
                "type": "array",
                "items": {
                    "type": "record",
                    "name": "OrderItem",
                    "fields": [{
                            "name": "name",
                            "type": "string"
                        },
                        {   "name": "size",
                            "type": {
                                "type": "enum",
                                "name": "Size",
                                "symbols": [
                                    "SM",
                                    "MED",
                                    "LG",
                                    "XLG"
                                ]
                            }
                        },
                        {   "name": "quantity",
                            "type": "int"
                        },
                        {   "name": "cost",
                            "type": {
                                "type": "bytes",
                                "logicalType": "decimal",
                                "precision": 3,
                                "scale": 2
                            }
                        }
                    ]
                }
            }
        },
        {   "name": "ordered_time",
            "type": {
                "type": "long",
                "logicalType": "timestamp-millis"
            }
        },
        {   "name": "status",
            "type": {
                "type": "enum",
                "name": "Status",
                "symbols": [
                    "NEW",
                    "CLOSED",
                    "UPDATED",
                    "DELETED",
                    "COMPLETED"
                ]
            },
            "default": "NEW"
        }
    ]}    
</details>

#### Data Evolution - Schema versioning

As data changes the need to alter the schema is inevitable. So, this then becomes the basis for making needed changes to the schema. The schema then changes incrementally. The Producer ultimately alters the schema as they are dealing with the changes in the data. The downstream consumers must then handle both the old and the new versions of the schema to de-serialize the data seamlessly. 

##### Compatibility Types

For reference go [here](https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html)


This has to be the most important section of schema validation. There are several type of compatibility:

  - BACKWARD: this 'default' compatibility means that consumers using the new schema can read data produced with the last schema. 
       - For example, if there are three schemas for a subject that change in order X-2, X-1, and X then BACKWARD compatibility ensures that consumers using the new schema X can process data written by producers using schema X or X-1, but not necessarily X-2.
         - If the consumer using the new schema needs to be able to process data written by all registered schemas, not just the last two schemas, then use BACKWARD_TRANSITIVE instead of BACKWARD. 
         
             The main reason that BACKWARD compatibility mode is the default, and preferred for Kafka, is so that you can rewind consumers to the beginning of the topic. With FORWARD compatibility mode, you aren’t guaranteed the ability to read old messages.
             
    Note: 'Backward' compatibility is when the Consumer schema is updated first.
    
         - Backward Compatibility Rules: Delete Fields and/or Add new optional fields.
          
  - FORWARD: this compatibility means that data produced with a new schema can be read by consumers using the last schema, even though they may not be able to use the full capabilities of the new schema.
       - For example, if there are three schemas for a subject that change in order X-2, X-1, and X then FORWARD compatibility ensures that data written by producers using the new schema X can be processed by consumers using schema X or X-1, but not necessarily X-2. 
         - If data produced with a new schema needs to be read by consumers using all registered schemas, not just the last two schemas, then use FORWARD_TRANSITIVE.
         
    Note: 'Forward' compatibility is when the Producer schema is updated first.
    
         - Forward Compatibility Rules: Add new Fields and/or Delete optional fields.
    
         
  - FULL: this compatibility means schemas are both backward and forward compatible. Schemas evolve in a fully compatible way: old data can be read with the new schema, and new data can also be read with the last schema.
       - For example, if there are three schemas for a subject that change in order X-2, X-1, and X then FULL compatibility ensures that consumers using the new schema X can process data written by producers using schema X or X-1, but not necessarily X-2, and that data written by producers using the new schema X can be processed by consumers using schema X or X-1, but not necessarily X-2. If the new schema needs to be forward and backward compatible with all registered schemas, not just the last two schemas, then use FULL_TRANSITIVE
       
    Note: 'Full' compatibility is when the Producer and Consumer is updated in either order.
       
    - Full Compatibility Rules: Add Optional new Fields and/or Delete optional fields.
       
  - NONE: this compatibility type means schema compatibility checks are disabled. Sometimes we make incompatible changes (Not a desired option).
     - For example, modifying a field type from Number to String. In this case, you will either need to upgrade all producers and consumers to the new schema version at the same time.
     
    Note: 'None' compatibility is when 'again' the Producer and Consumer is updated together. A Plan to mitigate this would be to create a brand-new topic and start migrating applications to use the new topic and new schema, avoiding the need to handle two incompatible versions in the same topic.
    
#### Schema Naming Stragegies

When producing records to a given topic, the records are based on three abstractions:
  1) Topic: Collection of messages with key and values.
  1) Schema: Schema defines the structure of the data.
  1) Subject: A named/ordered history of the schema versions. Its where the schema evolves.

##### Topic name strategy  

Topic Subject naming is the schema registry default and is constructed as follows. Use when:
  1) when a single type of event message is to be published in a topic.
  1) the schemas of all the event messages in the topic must be compatible with each other.
  1) subject name is derived as follows:

 - {topic-name}-key
 - {topic-name}-value

    props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, SubjectNameStrategy.class.getName());

  ex.

  - Topic Name: orders-sr
  - Schema: Order
  - Subject: orders-sr-key & orders-sr-value

##### Record name strategy

Record Naming Strategy is the strategy nameing of the schema is fully qualified to the exact schema type. This is used when:

  1) There are multiple types of related events are being published in the same topic and ordering of the events need to be maintained.
     - Single partition topic enforces ordering.
     - Schema registry checks the compatibility for a particular record type, regardless of topic.
     - Subject name is derived from the fully qualified record name(FQRN). e.g. package path defined in the compiled avro classes.

        - subject: FQRN - com.bcallanan.domain.generated.Order

    props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, RecordNameStrategy.class.getName());

Usecase:

  - Bank transactions for an account. They must flow in order. If a deposit is out of order and a debit comes thru first the result would be insufficient funds.
    
     
##### Topic record name strategy

Topic Record Naming Strategy is very similar to the previous strategy of record naming. With the focus of the schema check only being done for the specific topic. This is used when:
 
  1) Again here, There are multiple types of related events are being published in the same topic and ordering of the events need to be maintained.
     - Schema registry checks the compatibility for a particular record type, only for the current topic.
     
 - {topic-name}-{FQRN: RecordName}

    props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class.getName());

  ex. subject

  - Topic Name: orders-sr 
  - Record Name: com.bcallanan.domain.generated.Order
  
    - orders-sr-com.bcallanan.domain.generated.Order
     
     
On Confluent's website there's a document that discusses these three naming options and how or when to use one option over another, [here](https://www.confluent.io/blog/put-several-event-types-kafka-topic/).

In both, Record Naming Strategy and Topic Record Naming Strategy the idea is to treat the incoming consumable object as a GenericRecord and cast to the specific type with an InstanceOf conditional. Then do the appropriate actions based on the type and the processing that would be needed for the object. This way you've got a single consumer service monitoring a single topic yet receiving multiple event types. 
