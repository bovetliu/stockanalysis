package org.opentechfin.persistence;

import java.time.LocalDateTime;
import org.junit.Test;
import org.opentechfin.utils.TimeUtils;

/**
 *
 */
public class SimpleSeriesPagedByFileTest {

  @Test
  public void test() {
    TimeUtils.dateTimePrint(LocalDateTime.of(2016,1,1,0,0),
        LocalDateTime.of(2016,1,3,0,0),
        3600);
  }
}
