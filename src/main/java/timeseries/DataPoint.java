package timeseries;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;

/**
 */
public class DataPoint {

  /**
   * all dateTime of DataPoint is in EasternTime
   */
  public final LocalDateTime dateTime;
  public final float value;

  private DataPoint(LocalDateTime localDateTimeParam, float valueParam) {
    dateTime = localDateTimeParam;
    value = valueParam;
  }


  @Nonnull
  public static DataPoint create(LocalDateTime dateTimeParam, float valueParam) {
    return new DataPoint(dateTimeParam, valueParam);
  }

}
