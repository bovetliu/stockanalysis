package org.opentechfin.utils;

import com.typesafe.config.Config;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import org.opentechfin.timeseries.DataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegularUtils {

  private static Logger logger = LoggerFactory.getLogger(RegularUtils.class);

  public static  <T> boolean isSorted(List<T> list, BiFunction<T,T,Integer> compFunc) {
    T prev = null;
    // null means do not know current list is incrementing or decrementing
    // 1 means incrementing list.
    // -1 means decrementing list.
    Integer status = null;
    for (T current : list) {
      if (prev == null) {
        prev = current;
        continue;
      }
      int compareResult = compFunc.apply(current, prev);
      if (compareResult == 0) {
        prev = current;
        continue;
      }
      if (status == null) {
        status = compareResult;
        prev = current;
        continue;
      }
      if (compareResult != status) {
        return false;
      }
      prev = current;
    }
    return true;
  }

  public static List<DataPoint> getDataPointsFromTwoList(List<String> timeStampList, List<Double> valueList) {
    Objects.requireNonNull(timeStampList, "timeStamp list cannot be null");
    Objects.requireNonNull(valueList, "valueList cannot be null");
    if (timeStampList.size() != valueList.size()) {
      throw new IllegalArgumentException("timeStampList and valueList should have equal sizes");
    }
    List<DataPoint> resDataPointList = new ArrayList<>();
    Config config;
    Iterator<String> timeStampIter = timeStampList.iterator();
    Iterator<Double> valueListIter = valueList.iterator();
    while (timeStampIter.hasNext()) {
      String timeStr = timeStampIter.next();
      Double value = valueListIter.next();
      LocalDateTime localDateTime = LocalDateTime.parse(timeStr);
      resDataPointList.add(DataPoint.create(localDateTime, value.floatValue()));
    }
    return resDataPointList;
  }


}
