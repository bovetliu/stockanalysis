package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.opentechfin.utils.RegularUtils;

/**
 */
public class ArrayListTimeSeries extends TimeSeries {

  private final ArrayList<DataPoint> listOfPoints;

  private ArrayListTimeSeries(Builder builder) {
    super(builder);
    Objects.requireNonNull(builder.getListOfPoints(), "listOfPoints should not be null.");
    listOfPoints = builder.getListOfPoints();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Iterator<DataPoint> iterator() {
    return listOfPoints.iterator();
  }

  @Override
  public DataPoint getDataPoint(int idx) {
    return listOfPoints.get(idx);
  }

  @Override
  public DataPoint getDataPoint(LocalDateTime localDateTime) {
    throw new UnsupportedOperationException("Get DataPoint using LocalDateTime is not supported.");
  }

  @Override
  public ArrayListTimeSeries subSeries(LocalDateTime l, LocalDateTime r) {
    throw new UnsupportedOperationException("");
  }

  protected static class Builder extends TimeSeriesBuilder {
    private ArrayList<DataPoint> listOfPoints;

    public ArrayList<DataPoint> getListOfPoints() {
      return listOfPoints;
    }

    public Builder withListOfPoints(List<DataPoint> listOfPointsParam) {
      Objects.requireNonNull(listOfPointsParam, "listOfPoints cannot be null.");
      if (!RegularUtils.isSorted(listOfPointsParam, (curr, prev) -> {
        Objects.requireNonNull(curr, "element to be compared, should not be null. One element in "
            + "listOfPointsParam is null.");
        Objects.requireNonNull(curr.getDateTime(), "one dateTime field of a DataPoint is null, which is illegal.");
        return prev.getDateTime().compareTo(curr.getDateTime());
      })) {
        throw new IllegalArgumentException("should be sorted based on date time.");
      }
      listOfPoints = new ArrayList<>(listOfPointsParam);
      return this;
    }

    @Override
    public ArrayListTimeSeries  build() {
      return new ArrayListTimeSeries(this);
    }
  }
}
