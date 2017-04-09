package timeseries;

import com.sun.istack.internal.NotNull;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.annotation.Nullable;

/**
 *
 */
public abstract class TimeSeries implements Iterable<DataPoint> {

  private final boolean isReverseOrdered;

  private final boolean isBounded;

  private final int sizeStepInSeconds;

  private final LocalDateTime leftBoundary;

  private final LocalDateTime rightBoundary;

  private TimeSeries(Builder builder) {
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

  public abstract Builder builder();

  public TimeSeries subSeries(LocalDateTime leftBoundary, LocalDateTime rightBoundary) {
    return builder().withBoundaries(leftBoundary, rightBoundary).isReverseOrder(isReverseOrdered)
        .withSizeStepInSeconds(sizeStepInSeconds)
        .build();
  }

  public abstract DataPoint getDataPoint(LocalDateTime localDateTime);

  public abstract static class Builder {
    private boolean isReverseOrdered;
    private boolean isBounded;
    private int sizeStepInSeconds;
    private @Nullable LocalDateTime leftBound;
    private @Nullable LocalDateTime rightBound;


    private Builder() {

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
