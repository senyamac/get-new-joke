/*
 * Copyright Avaya Inc., All Rights Reserved. THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Avaya
 * Inc. The copyright notice above does not evidence any actual or intended publication of such
 * source code. Some third-party source code components may have been modified from their original
 * versions by Avaya Inc. The modifications are Copyright Avaya Inc., All Rights Reserved. Avaya -
 * Confidential & Restricted. May not be distributed further without written permission of the Avaya
 * owner.
 */

package com.example.getnewjoke.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JokeEntity {
  private String joke;

  public void setJoke(@NonNull String joke) {
    if (joke.length() >= 255) {
      joke = joke.substring(0, 254);
    }
    this.joke = joke;
  }

  @Override
  public String toString() {
    return "JokeEntity{" +
        "joke='" + joke + '\'' +
        '}';
  }
}
