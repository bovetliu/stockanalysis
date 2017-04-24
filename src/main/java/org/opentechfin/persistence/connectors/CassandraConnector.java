package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.base.Preconditions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;
import org.opentechfin.timeseries.DataPoint;
import org.opentechfin.utils.ConfigHolder;
import org.opentechfin.utils.Constants;
import org.opentechfin.utils.TimeUtils;
import org.opentechfin.utils.VerifyArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The connector use Cassandra as persistence medium.
 */
public class CassandraConnector implements PageConnector<DataPoint> {

  private static final Logger logger = LoggerFactory.getLogger(CassandraConnector.class);

  private final ConfigHolder configHolder;
  private final Cluster cluster;
  private final Session session;
  public final String STOCK_KEY_SPACE;
  private final String STOCK_COL_FAMILY;
  private final String DATE_COL_NAME;
  private final String SEC_OF_DAY_COL_NAME;

  /**
   * Connect to local server, used by default and used by Dependency injection
   */
  @Inject
  public CassandraConnector(ConfigHolder configHolderParam) {
    configHolder = configHolderParam;
    cluster = Cluster.builder()
        .addContactPoint(configHolder.getConfig().getString("cassandra.CONTACT_POINT"))
        .build();
    session = cluster.connect();
    STOCK_KEY_SPACE = configHolder.getConfig().getString("cassandra.STOCK_KEY_SPACE");
    STOCK_COL_FAMILY = configHolder.getConfig().getString("cassandra.STOCK_COL_FAMILY");
    DATE_COL_NAME = configHolder.getConfig().getString("cassandra.DATE_COL_NAME");
    SEC_OF_DAY_COL_NAME = configHolder.getConfig().getString("cassandra.SEC_OF_DAY_COL_NAME");
    if (!checkIfRelevantTableExists()) {
      createDefaultTable();
    }
  }

  public CassandraConnector(ConfigHolder configHolderParam, String stockKeySpaceParam) {
    configHolder = configHolderParam;
    cluster = Cluster.builder()
        .addContactPoint(configHolder.getConfig().getString("cassandra.CONTACT_POINT"))
        .build();
    session = cluster.connect();
    STOCK_KEY_SPACE = stockKeySpaceParam;
    STOCK_COL_FAMILY = configHolder.getConfig().getString("cassandra.STOCK_COL_FAMILY");
    DATE_COL_NAME = configHolder.getConfig().getString("cassandra.DATE_COL_NAME");
    SEC_OF_DAY_COL_NAME = configHolder.getConfig().getString("cassandra.SEC_OF_DAY_COL_NAME");
    if (!checkIfRelevantTableExists()) {
      createDefaultTable();
    }
  }

  public Cluster getCluster() {
    return cluster;
  }

  public Session getSession() {
    return session;
  }

  protected boolean checkIfRelevantTableExists() {
    String query = "DEFAULT VALUE";
    try {
      query = String.format("SELECT keyspace_name, table_name FROM system_schema.tables\n" +
      "  WHERE keyspace_name = '%s' AND table_name = '%s';", STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY);
      ResultSet resultSet = session.execute(query);
      return !resultSet.isExhausted();
    } catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }

  protected void createDefaultTable() {
    String query = "DEFAULT VALUE";
    try {
      query = String.format("CREATE KEYSPACE IF NOT EXISTS %s\n"
          + "  WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};",
          Preconditions.checkNotNull(STOCK_KEY_SPACE, "STOCK_KEY_SPACE should not be null here"));
      session.execute(query);

      query = String.format("CREATE TABLE %1$s.%2$s (\n"
              + "    %3$s date,\n"
              + "    %4$s time,\n"
              + "    PRIMARY KEY(%3$s, %4$s)\n"
              + ") WITH comment='stock exchange data'\n"
              + "   AND read_repair_chance = 1.0\n"
              + "   AND memtable_flush_period_in_ms = 600000;", STOCK_KEY_SPACE,
          STOCK_COL_FAMILY,
          DATE_COL_NAME,
          SEC_OF_DAY_COL_NAME);
      session.execute(query);
    } catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }

  /**
   * pageSize defined how many DataPoint in a page. pageNumber defined what is the number of this page. 0-started
   * repoName defined which time-series of a stock to be fetched.
   * @param pageMeta meta
   * @return a page of DataPoint.
   */
  @Override
  public Page<DataPoint> fetchPage(PageMeta pageMeta) {

    Select select = QueryBuilder.select(DATE_COL_NAME, SEC_OF_DAY_COL_NAME, pageMeta.getRepoName())
        .from(STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY);


    Clause dateClause = QueryBuilder.eq(DATE_COL_NAME,
            TimeUtils.javaLocalDateToDatastaxLocalDate(TimeUtils.Jan1st2016.plusDays(pageMeta.getPageNumber()).toLocalDate()));
    Select.Where whereAddedSelect = select.where(dateClause);
    ResultSet resultSEt;
    try {
      resultSEt = session.execute(whereAddedSelect);
    } catch (Exception ex) {
      logger.error("executed query: \n{}", whereAddedSelect.getQueryString());
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
    if (resultSEt.isExhausted()) { // does not hit anything
      return Page.emptyPage(pageMeta);
    }
    List<DataPoint> pageContent = new ArrayList<>();
    for (Row row : resultSEt) {
      com.datastax.driver.core.LocalDate datastaxLocalDate = row.getDate(DATE_COL_NAME);
      LocalDate java8LocalDate = LocalDate.of(datastaxLocalDate.getYear(),
          datastaxLocalDate.getMonth(),
          datastaxLocalDate.getDay());
      LocalDateTime ldt = LocalDateTime.of(java8LocalDate,
          LocalTime.ofSecondOfDay(row.getTime(SEC_OF_DAY_COL_NAME) / 1_000_000_000));
      pageContent.add(DataPoint.create(ldt, row.getFloat(pageMeta.getRepoName())));
    }
    return Page.create(pageMeta, pageContent);
  }



  public static String[] timeSeriesFromMeata(PageMeta pageMeta) {
    String[] tokenByColon = pageMeta.getRepoName().split("_");
    tokenByColon[0] = tokenByColon[0].trim();
    if (tokenByColon.length == 1) {
      return new String[]{
          tokenByColon[0] + "_h",
          tokenByColon[0] + "_l",
          tokenByColon[0] + "_o",
          tokenByColon[0] + "_c",
          tokenByColon[0] + "_v"
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
