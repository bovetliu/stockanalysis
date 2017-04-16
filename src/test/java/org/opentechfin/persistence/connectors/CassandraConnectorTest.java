package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opentechfin.testlib.TestBase;
import org.opentechfin.utils.ConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class CassandraConnectorTest extends TestBase {

  private final Logger logger = LoggerFactory.getLogger(CassandraConnectorTest.class);

  @Inject
  private CassandraConnector cassandraConnector;

  @Inject
  private ConfigHolder configHolder;

  @Test
  public void testTableInitialization() {
    Assert.assertTrue(cassandraConnector.checkIfRelevantTableExists());
  }

  @Test
  public void testAddStockSymbol() {
    CassandraUtil.addStockToCassandra("AMD", cassandraConnector);
    CassandraUtil.addStockToCassandra("AMD", cassandraConnector);
    CassandraUtil.addStockToCassandra("NVDA", cassandraConnector);
  }

  @After
  public void cleanDataBase() {
    String query = "DEFAULT_VALUE";
    try {
      query = String.format("DROP TABLE IF EXISTS %1$s.%2$s;",
          configHolder.getConfig().getString("cassandra.STOCK_KEY_SPACE"),
          configHolder.getConfig().getString("cassandra.STOCK_COL_FAMILY"));
      cassandraConnector.getSession().execute(query);
    }catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }
}