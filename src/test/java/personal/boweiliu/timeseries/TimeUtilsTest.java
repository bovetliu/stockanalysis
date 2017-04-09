package personal.boweiliu.timeseries;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.junit.Test;
import personal.boweiliu.utils.TimeUtils;
import personal.boweiliu.utils.TimeUtils.TemporalDirection;

/**
 * Unit test for simple App.
 */
public class TimeUtilsTest {

    @Test
    public void testLocalDateTimeRounding() {
        // 2017-04-07T12:09:45
        LocalDateTime localDateTime = LocalDateTime.of(2017, 4, 7, 12, 9, 45);
        Assert.assertEquals(LocalDateTime.of(2017, 4, 3, 0, 0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.WEEKS, TemporalDirection.BACKWARD));
        Assert.assertEquals(LocalDateTime.of(2017, 4, 10, 0, 0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.WEEKS, TemporalDirection.FORWARD));

        Assert.assertEquals(LocalDateTime.of(2017,4,8,0,0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.DAYS, TemporalDirection.FORWARD));
        Assert.assertEquals(LocalDateTime.of(2017,4,7,0,0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.DAYS, TemporalDirection.BACKWARD));

        Assert.assertEquals(LocalDateTime.of(2017,4,7,13,0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.HOURS, TemporalDirection.FORWARD));
        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.HOURS, TemporalDirection.BACKWARD));

        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,10,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.MINUTES, TemporalDirection.FORWARD));
        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,9,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.MINUTES, TemporalDirection.BACKWARD));

        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,9,45),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.SECONDS, TemporalDirection.FORWARD));
        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,9,45),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.SECONDS, TemporalDirection.BACKWARD));
        Assert.assertEquals(LocalDateTime.of(2017,4,7,12,0,0),
            TimeUtils.adjustToSharp(localDateTime, ChronoUnit.HOURS, TemporalDirection.BACKWARD));

        LocalDateTime localDateTime02 = LocalDateTime.of(2017, 4, 7, 0, 0, 0);
        Assert.assertEquals(localDateTime02,
            TimeUtils.adjustToSharp(localDateTime02, ChronoUnit.HOURS, TemporalDirection.BACKWARD));
        Assert.assertEquals(localDateTime02,
            TimeUtils.adjustToSharp(localDateTime02, ChronoUnit.HOURS, TemporalDirection.FORWARD));

    }
}