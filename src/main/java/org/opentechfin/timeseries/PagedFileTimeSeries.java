package org.opentechfin.timeseries;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;
import org.opentechfin.persistence.connectors.DailyNamedFileConnector;
import org.opentechfin.utils.TimeUtils;
import org.opentechfin.utils.TimeUtils.TemporalDirection;

/**
 *
 */
public class PagedFileTimeSeries extends TimeSeries {

  protected DailyNamedFileConnector connector;

  protected LinkedHashMap<PageMeta, Page<DataPoint>> cache;

  protected int pageSize;

  protected PagedFileTimeSeries(Builder builder) {
    super(builder);
    Objects.requireNonNull(builder.connector, "builder.connector cannot be null when creating PagedFileTimeSeries.");
    connector = builder.connector;
    pageSize = builder.pageSize;
    if (pageSize <= 0) {
      throw new IllegalArgumentException("pageSize cannot be negative or zero: " + pageSize);
    }
    cache = new LinkedHashMap<>();
  }


  @Override
  public DataPoint getDataPoint(int idx) {
    int pageNum = idx / pageSize + 1;
    String repoName = connector.folder;
    PageMeta pageMeta = PageMeta.create(pageNum, pageSize, repoName, PageMeta.DEFAULT_BIFUNC);
    Page<DataPoint> page = cache.computeIfAbsent(pageMeta, pageMeta1 -> connector.fetchPage(pageMeta1));
    int inPageIdx = idx % pageSize;
    return page.getPageContent().get(inPageIdx);
  }

  @Override
  public DataPoint getDataPoint(LocalDateTime localDateTime) {
    LocalDateTime atStartOfDate = TimeUtils.adjustToSharp(localDateTime, ChronoUnit.HOURS, TemporalDirection.BACKWARD);
    int pageNum = (int) ChronoUnit.DAYS.between(TimeUtils.Jan1st2016, atStartOfDate) + 1;
    PageMeta pageMeta = PageMeta.create(pageNum, pageSize, connector.folder);
    Page<DataPoint> page = cache.computeIfAbsent(pageMeta, pageMeta1 -> connector.fetchPage(pageMeta1));
    int inPageIdx = localDateTime.getHour();
    return page.getPageContent().get(inPageIdx);
  }

  @Override
  public TimeSeries subSeries(LocalDateTime leftBoundary, LocalDateTime rightBoundary) {
    throw new UnsupportedOperationException();
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder extends TimeSeriesBuilder {

    private DailyNamedFileConnector connector;

    private int pageSize;

    /*
    * isReverseOrdered:boolean
    * sizeStepInSeconds:int
    * isBounded:boolean
    * leftBound:LocalDateTime
    * rightBound:LocalDateTime
    *
    * */

    @Override
    public TimeSeries build() {
      return new PagedFileTimeSeries(this);
    }

    public DailyNamedFileConnector getConnector() {
      return connector;
    }

    public Builder withConnector(DailyNamedFileConnector connector) {
      this.connector = connector;
      return this;
    }

    public int getPageSize() {
      return pageSize;
    }

    public Builder withPageSize(int pageSize) {
      if (pageSize <= 0) {
        throw new IllegalArgumentException("pageSize cannot be negative");
      }
      this.pageSize = pageSize;
      return this;
    }
  }
}
