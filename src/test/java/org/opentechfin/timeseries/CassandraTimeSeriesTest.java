package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import javax.inject.Inject;
import jnr.ffi.annotations.In;
import org.junit.After;
import org.junit.Test;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.testlib.StockDataSeedHelper;

import com.google.common.base.Preconditions;
import java.io.File;
import java.net.URL;
import org.junit.Before;
import org.opentechfin.persistence.connectors.DailyNamedFileConnector;
import org.opentechfin.testlib.TestBase;
import org.opentechfin.utils.ConfigHolder;

/**
 *
 */
public class CassandraTimeSeriesTest extends TestBase {

  private ConfigHolder configHolder = ConfigHolder.staticGetConfigHolder();

  @Inject
  private CassandraConnector cassandraConnector;

  @Test
  public void testFunctionality() {
    TimeSeries cassandraTimeSeries = CassandraTimeSeries.builder()
        .withCassandraConnector(cassandraConnector)
        .withRepoName("amd_l")
        .withBoundaries(LocalDateTime.of(2017, 4, 17, 9, 30, 0), LocalDateTime.of(2017, 4, 21, 15, 59, 0))
        .withIsReverseOrder(false)
        .withSizeStepInSeconds(60)
        .build();

    for (DataPoint dataPoint : cassandraTimeSeries) {
      System.out.println(dataPoint);
    }
  }

  @Before
  public void loadSeedFile() {
    URL url = DailyNamedFileConnector.class.getClassLoader().getResource(
        "googlefinance/google_finance_2017-04-23_to_prev_20d.txt");
    File file = new File(Preconditions.checkNotNull(url, "url cannot be null").getFile());
    StockDataSeedHelper.loadGoogleFinance(file, "amd", configHolder.getConfig().getString("cassandra.STOCK_KEY_SPACE"));
  }

  @After
  public void clearDataBase() {
    super.cleanDataBase(cassandraConnector);
  }

}
