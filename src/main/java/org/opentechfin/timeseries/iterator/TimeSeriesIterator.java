package org.opentechfin.timeseries.iterator;

import java.time.LocalDateTime;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.opentechfin.timeseries.DataPoint;
import org.opentechfin.timeseries.TimeSeries;
import org.opentechfin.utils.TimeUtils;

/**
 * This is default iterator. No gap
 */
public class TimeSeriesIterator implements Iterator<DataPoint> {

  protected final boolean isReverseOrdered;

  protected final boolean isBounded;

  protected final int stepInSecond;

  protected DataPoint next;

  @Nullable
  protected final LocalDateTime leftBoundary;

  @Nullable
  protected final LocalDateTime rightBoundary;

  protected final TimeSeries timeSeries;

  /**
   * TODO(Bowei) javadoc
   * @param isReverseOrderedParam iterate in reverse order or not.
   * @param isBoundedParam is this iteration bounded
   * @param leftBoundaryParam nullable left boundary
   * @param rightBoundaryParam nullable right boundary
   * @throws IllegalArgumentException when stepInSecondParam lte 0.
   */
  public TimeSeriesIterator(boolean isReverseOrderedParam, boolean isBoundedParam, int stepInSecondParam,
      TimeSeries timeSeriesParam,
      @Nullable LocalDateTime leftBoundaryParam,
      @Nullable LocalDateTime rightBoundaryParam) {
    if (stepInSecondParam <= 0) {
      throw new IllegalArgumentException(String.format("%d is illegal step length in second", stepInSecondParam));
    }
    isReverseOrdered = isReverseOrderedParam;
    isBounded = isBoundedParam;
    stepInSecond = stepInSecondParam;
    timeSeries = timeSeriesParam;

    leftBoundary = leftBoundaryParam;
    rightBoundary = rightBoundaryParam;

    if (isReverseOrdered) {
      next = rightBoundary != null ? timeSeries.getDataPoint(rightBoundary) :
          timeSeries.getDataPoint(LocalDateTime.now());
    } else {
      next = leftBoundary != null ? timeSeries.getDataPoint(leftBoundary) :
          timeSeries.getDataPoint(TimeUtils.Jan1st2016);
    }
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public DataPoint next() {
    DataPoint res = next;
    LocalDateTime effectiveLeftBoundary = leftBoundary == null ? TimeUtils.Jan1st2016 : leftBoundary;
    LocalDateTime effectiveRightBoundary = rightBoundary == null ? LocalDateTime.now() : rightBoundary;
    final LocalDateTime nextDateTime;
    nextDateTime = next.getDateTime().plusSeconds(isReverseOrdered ? -stepInSecond : stepInSecond);
    if (isReverseOrdered) {
      next = nextDateTime.compareTo(effectiveLeftBoundary) < 0 ? null : timeSeries.getDataPoint(nextDateTime);
    } else {
      next = nextDateTime.compareTo(effectiveRightBoundary) > 0 ? null : timeSeries.getDataPoint(nextDateTime);
    }
    return res;
  }

  public boolean isReverseOrdered() {
    return isReverseOrdered;
  }

  public boolean isBounded() {
    return isBounded;
  }

  public int getStepInSecond() {
    return stepInSecond;
  }

  @Nullable
  public LocalDateTime getLeftBoundary() {
    return leftBoundary;
  }

  @Nullable
  public LocalDateTime getRightBoundary() {
    return rightBoundary;
  }
}
