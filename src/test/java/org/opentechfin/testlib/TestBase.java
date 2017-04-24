package org.opentechfin.testlib;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.opentechfin.guicemodule.InvestModule;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.utils.ConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This child of this class will search test conf as externalized test input
 */
public abstract class TestBase {

  protected final Config TEST_INPUT;

  protected final InvestModule investModule;

  protected final Injector injector;

  protected final ConfigHolder configHolder = ConfigHolder.staticGetConfigHolder();

  private final Logger logger = LoggerFactory.getLogger(TestBase.class);

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

  protected void cleanDataBase(CassandraConnector cassandraConnector) {
    String query = "DEFAULT_VALUE";
    try {
      query = String.format("DROP TABLE IF EXISTS %1$s.%2$s;",
          configHolder.getConfig().getString("cassandra.STOCK_KEY_SPACE"),
          configHolder.getConfig().getString("cassandra.UNIT_TEST_STOCK_KEY_SPACE"));
      cassandraConnector.getSession().execute(query);
    }catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }
}
