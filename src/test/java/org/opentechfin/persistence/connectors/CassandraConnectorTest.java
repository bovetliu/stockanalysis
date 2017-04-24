package org.opentechfin.persistence.connectors;

import javax.inject.Inject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.opentechfin.testlib.TestBase;
import org.opentechfin.utils.ConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class CassandraConnectorTest extends TestBase {

  private final Logger logger = LoggerFactory.getLogger(CassandraConnectorTest.class);

  private CassandraConnector cassandraConnector = new CassandraConnector(ConfigHolder.staticGetConfigHolder());

  @Test
  public void testTableInitialization() {
    Assert.assertTrue(cassandraConnector.checkIfRelevantTableExists());
  }

  @Test
  public void testCassandraUtil() {
    CassandraUtil.addStock("AMD", cassandraConnector);
    CassandraUtil.addStock("AMD", cassandraConnector);
    CassandraUtil.addStock("NVDA", cassandraConnector);
    Assert.assertTrue(CassandraUtil.hasStock("NVDA", cassandraConnector));
    CassandraUtil.removeStock("AMD", cassandraConnector);
    CassandraUtil.removeStock("AMD", cassandraConnector);
    CassandraUtil.removeStock("NVDA", cassandraConnector);
    Assert.assertFalse(CassandraUtil.hasStock("AMD", cassandraConnector));
  }

  @After
  public void cleanDataBase() {
    super.cleanDataBase(cassandraConnector);
  }
}
