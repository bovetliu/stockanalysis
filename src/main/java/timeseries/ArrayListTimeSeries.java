package timeseries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import utils.RegularUtils;

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
  public DataPoint getDataPoint(int idx) {
    return listOfPoints.get(idx);
  }

  @Override
  public DataPoint getDataPoint(LocalDateTime localDateTime) {
    throw new UnsupportedOperationException("Get DataPoint using LocalDateTime is not supported.");
  }

  public static class Builder extends TimeSeries.Builder {
    private ArrayList<DataPoint> listOfPoints;

    public ArrayList<DataPoint> getListOfPoints() {
      return listOfPoints;
    }

    public Builder withListOfPoints(ArrayList<DataPoint> listOfPointsParam) {
      Objects.requireNonNull(listOfPointsParam, "listOfPoints cannot be null.");
      if (!RegularUtils.isSorted(listOfPointsParam, (curr, prev) -> {
        Objects.requireNonNull(curr, "element to be compared, should not be null. One element in "
            + "listOfPointsParam is null.");
        Objects.requireNonNull(curr.dateTime, "one dateTime field of a DataPoint is null, which is illegal.");
        return prev.dateTime.compareTo(curr.dateTime);
      })) {
        throw new IllegalArgumentException("should be sorted based on date time.");
      }
      listOfPoints = listOfPointsParam;
      return this;
    }

    @Override
    public TimeSeries build() {
      return new ArrayListTimeSeries(this);
    }
  }
}
