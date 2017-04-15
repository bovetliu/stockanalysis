package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.Nonnull;

/**
 */
public class DataPoint {

  /**
   * all dateTime of DataPoint is in EasternTime
   */
  private final LocalDateTime dateTime;
  private final float value;

  private DataPoint(LocalDateTime localDateTimeParam, float valueParam) {
    dateTime = localDateTimeParam;
    value = valueParam;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public float getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s : %7.2f", dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), value);
  }

  @Nonnull
  public static DataPoint create(LocalDateTime dateTimeParam, float valueParam) {
    return new DataPoint(dateTimeParam, valueParam);
  }
}
