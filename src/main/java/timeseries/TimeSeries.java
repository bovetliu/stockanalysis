package timeseries;

import com.sun.istack.internal.NotNull;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.annotation.Nullable;

/**
 *
 */
public abstract class TimeSeries implements Iterable<DataPoint> {

  protected final boolean isReverseOrdered;

  protected final boolean isBounded;

  protected final int sizeStepInSeconds;

  protected final LocalDateTime leftBoundary;

  protected final LocalDateTime rightBoundary;

  protected TimeSeries(Builder builder) {
    isReverseOrdered = builder.isReverseOrdered();
    isBounded = builder.isBounded();
    sizeStepInSeconds = builder.getSizeStepInSeconds();
    leftBoundary = builder.getLeftBound();
    rightBoundary = builder.getRightBound();
  }


  public @NotNull Iterator<DataPoint> iterator() {
    return new TimeSeriesIterator(isReverseOrdered, isBounded, sizeStepInSeconds,
        this,
        leftBoundary,
        rightBoundary);
  }

  public TimeSeries subSeries(LocalDateTime leftBoundary, LocalDateTime rightBoundary) {
    return builder().withBoundaries(leftBoundary, rightBoundary).isReverseOrder(isReverseOrdered)
        .withSizeStepInSeconds(sizeStepInSeconds)
        .build();
  }

  public abstract DataPoint getDataPoint(int idx);

  public abstract DataPoint getDataPoint(LocalDateTime localDateTime);

  public static Builder builder() {
    throw new UnsupportedOperationException("Static builder convenience method is not available in TimeSeries");
  }

  public abstract static class Builder {
    protected boolean isReverseOrdered;
    protected boolean isBounded;
    protected int sizeStepInSeconds;
    protected  @Nullable LocalDateTime leftBound;
    protected  @Nullable LocalDateTime rightBound;


    protected Builder() {

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

    public Builder withSizeStepInSeconds(int sizeStepInSecondsParam) {
      sizeStepInSeconds = sizeStepInSecondsParam;
      return this;
    }

    public Builder isReverseOrder(boolean isReverseOrderedParam) {
      isReverseOrdered = isReverseOrderedParam;
      return this;
    }

    public Builder withBoundaries(@Nullable LocalDateTime leftBoundParam, @Nullable LocalDateTime rightBoundParam) {
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
}
