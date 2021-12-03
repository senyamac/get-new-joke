/*
 * Copyright Avaya Inc., All Rights Reserved. THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Avaya
 * Inc. The copyright notice above does not evidence any actual or intended publication of such
 * source code. Some third-party source code components may have been modified from their original
 * versions by Avaya Inc. The modifications are Copyright Avaya Inc., All Rights Reserved. Avaya -
 * Confidential & Restricted. May not be distributed further without written permission of the Avaya
 * owner.
 */

package com.example.getnewjoke.service;

import com.example.getnewjoke.kafka.KafkaJokeProducer;
import com.example.getnewjoke.model.JokeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NewJokeService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${api.url}")
  private String apiUrl;

  @Autowired
  private KafkaJokeProducer producer;

  public NewJokeService() {
    restTemplate = new RestTemplate();
    objectMapper = new ObjectMapper();
  }

  public JokeEntity getNewJokeFromApi() {
    String message = restTemplate.getForObject(apiUrl, String.class);
    JokeEntity jokeEntity = new JokeEntity();
    try {
      jokeEntity = objectMapper.readValue(message, JokeEntity.class);
      log.debug(jokeEntity.toString());
    } catch (JsonProcessingException e) {
      log.error("Error while trying to convert API response to JokeEntity, {} {}", e.getMessage(),
          e);
    }
    return jokeEntity;
  }

  @Scheduled(fixedDelay = 3000)
  public void scheduledTask() {
    log.debug("Start process");
    JokeEntity joke = getNewJokeFromApi();
    producer.addNewJokeToKafka(joke);
    log.debug("Joke was sent");
  }
}
