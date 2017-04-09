package personal.boweiliu.utils;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

/**
 */
public class TimeUtils {

  public static final LocalDateTime Jan1st2016 = LocalDate.of(2016,1,1).atStartOfDay();

  public static final ImmutableBiMap<ChronoUnit, ChronoField> CHRONO_UNIT_VS_CHRONO_FIELD =
      ImmutableBiMap.<ChronoUnit, ChronoField>builder()
      .put(ChronoUnit.YEARS, ChronoField.YEAR)
      .put(ChronoUnit.MONTHS, ChronoField.MONTH_OF_YEAR)
      .put(ChronoUnit.DAYS, ChronoField.DAY_OF_MONTH)
      .put(ChronoUnit.HOURS, ChronoField.HOUR_OF_DAY)
      .put(ChronoUnit.MINUTES, ChronoField.MINUTE_OF_HOUR)
      .put(ChronoUnit.SECONDS, ChronoField.SECOND_OF_MINUTE)
      .build();

  public static LocalDateTime adjustToSharp(LocalDateTime localDateTime, ChronoUnit temporalUnit,
      TemporalDirection direction) {

    Objects.requireNonNull(localDateTime, "localDateTime parameter cannot be null");
    Objects.requireNonNull(temporalUnit, "temporalUnit parameter cannot be null");
    Objects.requireNonNull(direction, "direction parameter cannot be null");
    if (ChronoUnit.YEARS.compareTo(temporalUnit) < 0) {
      throw new IllegalArgumentException("cannot adjust to a level larger than YEARS");
    }
    if (ChronoUnit.SECONDS.compareTo(temporalUnit) > 0) {
      throw new IllegalArgumentException("cannot adjust to a level smaller than SECONDS");
    }
    if (temporalUnit == ChronoUnit.WEEKS) {
      // 1 Monday  , 7 Sunday
      if (!isAtStartOfDay(localDateTime)) {
        localDateTime = adjustToSharp(localDateTime, ChronoUnit.DAYS, direction);
      }
      int dayOfWeekInt = localDateTime.getDayOfWeek().getValue();
      int delta = dayOfWeekInt - 1;
      localDateTime = localDateTime.minusDays(delta).withHour(0).withMinute(0).withSecond(0).withNano(0);
      return direction == TemporalDirection.FORWARD ? localDateTime.plusWeeks(1) : localDateTime;
    }
    LocalDateTime res = LocalDateTime.from(localDateTime);
    for (Map.Entry<ChronoUnit, ChronoField> entry : CHRONO_UNIT_VS_CHRONO_FIELD.entrySet()) {
      ChronoUnit unit = entry.getKey();
      ChronoField field = entry.getValue();
      if (unit.compareTo(temporalUnit) < 0) {
        res = res.with(field, 0);
      }
    }
    if (direction == TemporalDirection.FORWARD) {
      if (res.compareTo(localDateTime) < 0) {
        res = res.plus(1, temporalUnit);
      }
    }
    return res;
  }

  public static boolean isAtStartOfDay(LocalDateTime localDateTime) {
    Objects.requireNonNull(localDateTime, "localDateTime cannot be null.");
    return localDateTime.isEqual(LocalDate.of(
        localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()).atStartOfDay());
  }

  public enum TemporalDirection {
    FORWARD, BACKWARD
  }
}
