package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;
import org.opentechfin.timeseries.DataPoint;
import org.opentechfin.utils.Constants;
import org.opentechfin.utils.TimeUtils;

/**
 * The connector use Cassandra as persistence medium.
 */
public class CassandraConnector implements PageConnector<DataPoint> {

  private final Cluster cluster;
  private final Session session;
  private CassandraConnector() {
    cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
    session = cluster.connect();
  }

  public Cluster getCluster() {
    return cluster;
  }

  public Session getSession() {
    return session;
  }

  /**
   * pageSize defined how many DataPoint in a page. pageNumber defined what is the number of this page. 0-started
   * repoName defined which time-series of a stock to be fetched.
   * @param pageMeta meta
   * @return a page of DataPoint.
   */
  @Override
  public Page<DataPoint> fetchPage(PageMeta pageMeta) {

    Select select = QueryBuilder.select(Constants.DATE_COL_NAME, Constants.SEC_OF_DAY_COL_NAME, pageMeta.getRepoName())
        .from(Constants.STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY);

    Clause dateClause = QueryBuilder.eq(Constants.DATE_COL_NAME,
            TimeUtils.Jan1st2016.plusDays(pageMeta.getPageNumber()).format(DateTimeFormatter.ISO_LOCAL_DATE));
    Select.Where whereAddedSelect = select.where(dateClause);
    ResultSet resultSEt = session.execute(whereAddedSelect);
    if (resultSEt.isExhausted()) { // does not hit anything
      return Page.emptyPage(pageMeta);
    }
    List<DataPoint> pageContent = new ArrayList<>();
    for (Row row : resultSEt) {
      com.datastax.driver.core.LocalDate datastaxLocalDate = row.getDate(Constants.DATE_COL_NAME);
      LocalDate java8LocalDate = LocalDate.of(datastaxLocalDate.getYear(),
          datastaxLocalDate.getMonth(),
          datastaxLocalDate.getDay());
      LocalDateTime ldt = LocalDateTime.of(java8LocalDate,
          LocalTime.ofSecondOfDay(row.getInt(Constants.SEC_OF_DAY_COL_NAME)));
      pageContent.add(DataPoint.create(ldt, row.getFloat(pageMeta.getRepoName())));
    }
    return Page.create(pageMeta, pageContent);
  }

  public static String[] timeSeriesFromMeata(PageMeta pageMeta) {
    String[] tokenByColon = pageMeta.getRepoName().split(":");
    tokenByColon[0] = tokenByColon[0].trim();
    if (tokenByColon.length == 1) {
      return new String[]{
          tokenByColon[0] + ":h",
          tokenByColon[0] + ":l",
          tokenByColon[0] + ":o",
          tokenByColon[0] + ":c",
          tokenByColon[0] + ":v"
      };
    } else if (tokenByColon.length == 2) {
      String[] seriesTypes = tokenByColon[1].split(",");
      for (int i = 0; i < seriesTypes.length; i++) {
        seriesTypes[i] = tokenByColon[0] + ":" + seriesTypes[i].trim();
      }
      return seriesTypes;
    }
    throw new IllegalArgumentException("repoName format incorrect, correct format is <stock_symbol>:h,l,o,c,v");
  }
}
