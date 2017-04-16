package org.opentechfin.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javax.inject.Inject;

/**
 */
public class ConfigHolder {

  private final Config config;

  @Inject
  private ConfigHolder() {
    config = ConfigFactory.load();
  }

  public Config getConfig() {
    return config;
  }
}
