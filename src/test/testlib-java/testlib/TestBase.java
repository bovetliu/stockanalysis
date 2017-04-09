package testlib;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This child of this class will search test conf as externalized test input
 */
public abstract class TestBase {

  protected final Config TEST_INPUT;

  protected TestBase() {
    TEST_INPUT = ConfigFactory.load();
  }

}
