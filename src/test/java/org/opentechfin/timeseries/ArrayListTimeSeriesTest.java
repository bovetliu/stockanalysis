package org.opentechfin.timeseries;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentechfin.utils.RegularUtils;
import testlib.TestBase;

public class ArrayListTimeSeriesTest extends TestBase {

  /**
   * Scenario Description:
   */
  @Test
  public void testArrayListTimeSeries01() {
    List<DataPoint> dataPointList = RegularUtils.getDataPointsFromTwoList(
        TEST_INPUT.getStringList("timeseries.timestampStrList"),
        TEST_INPUT.getDoubleList("timeseries.valueList"));
    ArrayListTimeSeries arrayListTimeSeries = (ArrayListTimeSeries) ArrayListTimeSeries.builder()
        .withListOfPoints(dataPointList)
        .withSizeStepInSeconds(60)
        .withBoundaries(dataPointList.get(0).dateTime, dataPointList.get(dataPointList.size() - 1).dateTime)
        .build();
    Assert.assertEquals("2017-04-06T09:30:00 :   34.05", arrayListTimeSeries.getDataPoint(0).toString());
    Assert.assertEquals("2017-04-06T09:31:00 : 8934.33", arrayListTimeSeries.getDataPoint(1).toString());
    Assert.assertEquals("2017-04-06T09:32:00 :    3.30", arrayListTimeSeries.getDataPoint(2).toString());
  }
}
