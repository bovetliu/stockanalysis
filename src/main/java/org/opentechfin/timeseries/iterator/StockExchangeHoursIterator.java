package org.opentechfin.timeseries.iterator;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import org.opentechfin.timeseries.DataPoint;
import org.opentechfin.timeseries.TimeSeries;
import org.opentechfin.utils.TimeUtils;

/**
 * Created by boweiliu on 4/24/17.
 */
public class StockExchangeHoursIterator extends TimeSeriesIterator {

  /**
   * TODO(Bowei) javadoc
   *
   * @param isReverseOrderedParam iterate in reverse order or not.
   * @param isBoundedParam is this iteration bounded
   * @param leftBoundaryParam nullable left boundary
   * @param rightBoundaryParam nullable right boundary   @throws IllegalArgumentException when stepInSecondParam lte 0.
   */
  public StockExchangeHoursIterator(boolean isReverseOrderedParam, boolean isBoundedParam, int stepInSecondParam,
      TimeSeries timeSeriesParam, @Nullable LocalDateTime leftBoundaryParam,
      @Nullable LocalDateTime rightBoundaryParam) {
    super(isReverseOrderedParam, isBoundedParam, stepInSecondParam, timeSeriesParam, leftBoundaryParam,
        rightBoundaryParam);
  }


  @Override
  public DataPoint next() {
    DataPoint res = next;
    LocalDateTime effectiveLeftBoundary = leftBoundary == null ? TimeUtils.Jan1st2016 : leftBoundary;
    LocalDateTime effectiveRightBoundary = rightBoundary == null ? LocalDateTime.now() : rightBoundary;
    LocalDateTime nextDateTime;
    nextDateTime = next.getDateTime().plusSeconds(isReverseOrdered ? -stepInSecond : stepInSecond);
    if (isReverseOrdered) {
      if (nextDateTime.toLocalTime().isBefore(TimeUtils.MKT_OPEN_TIME)) {
        nextDateTime = LocalDateTime.of(nextDateTime.minusDays(1).toLocalDate(),
            TimeUtils.MKT_CLOSE_TIME.minusMinutes(1));
      }
    } else {
      if (!nextDateTime.toLocalTime().isBefore(TimeUtils.MKT_CLOSE_TIME)) {
        nextDateTime = LocalDateTime.of(nextDateTime.plusDays(1).toLocalDate(),
            TimeUtils.MKT_OPEN_TIME);
      }
    }
    if (isReverseOrdered) {
      next = nextDateTime.compareTo(effectiveLeftBoundary) < 0 ? null : timeSeries.getDataPoint(nextDateTime);
    } else {
      next = nextDateTime.compareTo(effectiveRightBoundary) > 0 ? null : timeSeries.getDataPoint(nextDateTime);
    }
    return res;
  }
}
