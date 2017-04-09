package utils;

import java.util.List;
import java.util.function.BiFunction;

public class RegularUtils {

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
        continue;
      }
      if (status == null) {
        status = compareResult;
        continue;
      }
      if (compareResult != status) {
        return false;
      }
    }
    return true;
  }
}
