package org.opentechfin.testlib;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.opentechfin.guicemodule.InvestModule;

/**
 * This child of this class will search test conf as externalized test input
 */
public abstract class TestBase {

  protected final Config TEST_INPUT;

  protected final InvestModule investModule;

  protected final Injector injector;

  protected TestBase() {
    TEST_INPUT = ConfigFactory.parseResources("test_input.conf");
    investModule = InvestModule.getSingleton();
    injector = Guice.createInjector(investModule);
    injector.injectMembers(this);
  }

  public InvestModule getInvestModule() {
    return investModule;
  }

  public Injector getInjector() {
    return injector;
  }
}
