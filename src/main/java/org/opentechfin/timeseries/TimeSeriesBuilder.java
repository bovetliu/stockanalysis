package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import javax.annotation.Nullable;

/**
 */
public abstract class TimeSeriesBuilder {
  protected boolean isReverseOrdered = false;
  protected boolean isBounded = false;
  protected int sizeStepInSeconds = 60;
  protected  @Nullable LocalDateTime leftBound;
  protected  @Nullable LocalDateTime rightBound;


  protected TimeSeriesBuilder() {
  }

  public abstract TimeSeries build();

  public boolean isReverseOrdered() {
    return isReverseOrdered;
  }

  public boolean isBounded() {
    return isBounded;
  }

  public int getSizeStepInSeconds() {
    return sizeStepInSeconds;
  }

  @Nullable
  public LocalDateTime getLeftBound() {
    return leftBound;
  }

  @Nullable
  public LocalDateTime getRightBound() {
    return rightBound;
  }

  public TimeSeriesBuilder withSizeStepInSeconds(int sizeStepInSecondsParam) {
    sizeStepInSeconds = sizeStepInSecondsParam;
    return this;
  }

  public TimeSeriesBuilder withIsReverseOrder(boolean isReverseOrderedParam) {
    isReverseOrdered = isReverseOrderedParam;
    return this;
  }

  public TimeSeriesBuilder withBoundaries(@Nullable LocalDateTime leftBoundParam, @Nullable LocalDateTime rightBoundParam) {
    if (leftBoundParam == null && rightBoundParam == null) {
      leftBound = rightBound = null;
      isBounded = false;
      return this;
    }
    leftBound = leftBoundParam;
    rightBound = rightBoundParam;
    isBounded = true;
    return this;
  }
}
