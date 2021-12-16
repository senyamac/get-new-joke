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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NewJokeService {

  private final RestTemplate restTemplate;

  @Value("${api.url}")
  private String apiUrl;

  @Autowired
  private KafkaJokeProducer producer;

  public NewJokeService() {
    restTemplate = new RestTemplate();
  }

  public Optional<JokeEntity> getNewJokeFromApi() {
    String message = null;
    try {
      message = restTemplate.getForObject(apiUrl, String.class);
    } catch (RestClientException e) {
      log.warn("Unable to get joke from API. Due to: {}", e.getMessage());
    }
    if (message == null) {
      return Optional.empty();
    }
    JokeEntity jokeEntity = new JokeEntity();
    jokeEntity.setJoke(message);
    return Optional.of(jokeEntity);
  }

  @Scheduled(fixedDelay = 3000)
  public void scheduledTask() {
    log.debug("Start process");
    Optional<JokeEntity> joke = getNewJokeFromApi();
    if (joke.isPresent()) {
      log.debug(joke.toString());
      producer.addNewJokeToKafka(joke.get());
      log.debug("Joke sent");
    } else {
      log.info("Skip joke due to impossibility getting from API");
    }
  }
}
