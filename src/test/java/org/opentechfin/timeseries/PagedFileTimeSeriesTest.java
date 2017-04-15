package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.opentechfin.persistence.connectors.DailyNamedFileConnector;
import org.opentechfin.testlib.TestBase;

/**
 */
public class PagedFileTimeSeriesTest extends TestBase {

  /**
   * Test Scenario:
   * a series data points are stored in two files in folder "for_file_paged_test". In this test, it shows that paging is
   * transparent to user. During iteration, next page is automatically fetched.
   */
  @Test
  public void walkThroughTest() {
    // initialize one connector
    DailyNamedFileConnector connector = new DailyNamedFileConnector("for_file_paged_test");
    // build one TimeSeries
    TimeSeries timeSeries = PagedFileTimeSeries.Builder()
        .withConnector(connector)
        .withPageSize(24)
        .withBoundaries(LocalDateTime.of(2016, 1, 1, 0 ,0), LocalDateTime.of(2016, 1, 2, 23, 0, 0))
        .withSizeStepInSeconds(3600)
        .withIsReverseOrder(false)
        .build();
    int cnt = 0;
    for (DataPoint dataPoint : timeSeries) {
//      System.out.println(dataPoint);
      cnt++;
    }
    Assert.assertEquals(48, cnt);
  }
}
