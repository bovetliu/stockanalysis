package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import java.util.Iterator;
import org.opentechfin.timeseries.iterator.TimeSeriesIterator;

/**
 *
 */
public abstract class TimeSeries implements Iterable<DataPoint> {

  protected final boolean isReverseOrdered;

  protected final boolean isBounded;

  protected final int sizeStepInSeconds;

  protected final LocalDateTime leftBoundary;

  protected final LocalDateTime rightBoundary;

  protected TimeSeries(TimeSeriesBuilder builder) {
    isReverseOrdered = builder.isReverseOrdered();
    isBounded = builder.isBounded();
    sizeStepInSeconds = builder.getSizeStepInSeconds();
    leftBoundary = builder.getLeftBound();
    rightBoundary = builder.getRightBound();
  }


  @Override
  public Iterator<DataPoint> iterator() {
    return new TimeSeriesIterator(isReverseOrdered, isBounded, sizeStepInSeconds,
        this,
        leftBoundary,
        rightBoundary);
  }

  public abstract TimeSeries subSeries(LocalDateTime leftBoundary, LocalDateTime rightBoundary);

  public abstract DataPoint getDataPoint(int idx);

  public abstract DataPoint getDataPoint(LocalDateTime localDateTime);

}
