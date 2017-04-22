package org.opentechfin.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javax.inject.Inject;

/**
 */
public class ConfigHolder {

  private static ConfigHolder singleton;

  private final Config config;

  @Inject
  private ConfigHolder() {
    config = ConfigFactory.load();
    singleton = this;
  }

  public Config getConfig() {
    return singleton.config;
  }

  public static Config staticGetConfig() {
    if (singleton == null) {
      singleton = new ConfigHolder();
    }
    return singleton.config;
  }
}
