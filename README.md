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


#### Docker configuration

Setting up a Docker Environment with Zookeeper, Broker, and a Schema Registry container. I've included the docker-compose within the repo. When starting the containers its best to monitor the logs.

      docker logs -f avro-springboot_zk1_1
      docker logs -f avro-springboot_broker_1
      docker logs -f schema-registry
      
There should not be any exceptions in ay of the logs. Wait for the sequence to calm and use the next commands to test the environment:

      alias broker="winpty docker exec -it avro-springboot_broker_1 bash"
      alias schema="winpty docker exec -it schema-registry bash"
 
##### Testing the broker
      
Initially for the broker you'll need 2 windows where you've connected with the <i><b>"broker"</b></i> alias. In one window using the following:

    kafka-console-producer --broker-list localhost:9092 --topic test-topic
      
In the second, use the following command:

    kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic      
  
In the producer terminal, type in any string and hit return. the message should show up in the consumer window. If it does, and there are no exceptions you broker is working. If not, its broken and something is spitting out in the logs.

##### Testing the schema       
              
The Avro Schema is tested on the other container much in the same way. Use 2 windows and connect to the schema container with the <i><b>"schema"</b></i> alias

In one window using the following to add some schema to the environment.

    kafka-avro-console-producer --broker-list broker:39092 --topic greetings --property value.schema='{"type": "record","name":"Greeting","fields": [{"name": "greeting","type": "string"}]}'

In the second window perform the following:

    kafka-avro-console-consumer --bootstrap-server broker:39092 --topic greetings --from-beginning
    
The in the first window, enter the following:

    {"greeting": "Good Morning!, AVRO"}
    {"greeting": "Good Evening!, AVRO"}
    {"greeting": "Good Night!, AVRO"}

If there are no exceptions, and the messages are printed in the avro consumer, you're good! Then you can enter the following bad message and you'll get the exception showing it failed the scehma:

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
        
Once through this there will be a functioning AVRO Kafka docker Env to run the microservices.         

    