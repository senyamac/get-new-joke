/*
 * Copyright Avaya Inc., All Rights Reserved. THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Avaya
 * Inc. The copyright notice above does not evidence any actual or intended publication of such
 * source code. Some third-party source code components may have been modified from their original
 * versions by Avaya Inc. The modifications are Copyright Avaya Inc., All Rights Reserved. Avaya -
 * Confidential & Restricted. May not be distributed further without written permission of the Avaya
 * owner.
 */

package com.example.getnewjoke.kafka;

import com.example.avro.Joke;
import com.example.getnewjoke.model.JokeEntity;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaJokeProducer {

  private final KafkaProducer<String, Joke> producer;

  @Value("${kafka.topic}")
  private String kafkaTopic;

  @Autowired
  public KafkaJokeProducer(
      @Value("${kafka.bootstrap-server.url}") final String kafkaBootstrapServerUrl,
      @Value("${kafka.schema-registry.url}") final String schemaRegistryUrl
  ) {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServerUrl);
    props.put(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 0);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

    producer = new KafkaProducer<>(props);
  }

  public void addNewJokeToKafka(JokeEntity jokeEntity) {
    final Joke joke = new Joke(String.valueOf(jokeEntity.getId()), jokeEntity.getCategory(),
        jokeEntity.getType(), jokeEntity.getJoke());
    final ProducerRecord<String, Joke> producerRecord = new ProducerRecord<>(kafkaTopic,
        joke.getId().toString(), joke);
    producer.send(producerRecord);
    producer.flush();
  }
}
