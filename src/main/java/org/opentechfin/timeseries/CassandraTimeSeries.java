package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.persistence.connectors.PageConnector;
import org.opentechfin.utils.TimeUtils;
import org.opentechfin.utils.TimeUtils.TemporalDirection;

/**
 */
public class CassandraTimeSeries extends TimeSeries {

  private final PageConnector<DataPoint> pageConnector;
  private final String repoName;
  protected LinkedHashMap<PageMeta, Page<DataPoint>> cache;

  protected CassandraTimeSeries(Builder builder) {
    super(builder);
    pageConnector = builder.getCassandraConnector();
    repoName = builder.getRepoName();
    cache = new LinkedHashMap<>();
  }


  @Override
  public TimeSeries subSeries(LocalDateTime leftBoundary, LocalDateTime rightBoundary) {
    throw new UnsupportedOperationException("currently do not support");
  }

  @Override
  public DataPoint getDataPoint(int idx) {
    throw new UnsupportedOperationException("currently do not support");
  }

  @Override
  public DataPoint getDataPoint(LocalDateTime localDateTime) {
    LocalDateTime roundedToMin = TimeUtils.adjustToSharp(localDateTime,
        ChronoUnit.MINUTES,
        TemporalDirection.BACKWARD);
    LocalTime localTime = roundedToMin.toLocalTime();
    if (roundedToMin.toLocalTime().isBefore(LocalTime.of(9, 30)) ||
        roundedToMin.toLocalTime().isAfter(LocalTime.of(16, 29))) {
      throw new IllegalArgumentException("illegal time :" + localDateTime);
    }
    int pageNum = (int) ChronoUnit.DAYS.between(TimeUtils.Jan1st2016, roundedToMin);
    int pageSize = 390;
    PageMeta pageMeta = PageMeta.create(pageNum, pageSize, repoName);
    Page<DataPoint> page = cache.computeIfAbsent(pageMeta, (pageConnector::fetchPage));
    if (page.getPageContent().isEmpty()) {
      throw new IllegalStateException("empty page is fetched. Check whether trading day or data has not yet populated");
    }
    int idxOfPoint = (int) ChronoUnit.MINUTES.between(TimeUtils.MKT_OPEN_TIME, localTime);
    return page.getPageContent().get(idxOfPoint);
  }


  public static class Builder extends TimeSeriesBuilder {

    private CassandraConnector cassandraConnector;
    private String repoName;

    public CassandraConnector getCassandraConnector() {
      return cassandraConnector;
    }

    public Builder withCassandraConnector(CassandraConnector cassandraConnector) {
      this.cassandraConnector = cassandraConnector;
      return this;
    }

    @Override
    public TimeSeries build() {
      return null;
    }

    public String getRepoName() {
      return repoName;
    }

    public Builder withRepoName(String repoName) {
      this.repoName = repoName;
      return this;
    }
  }
}
