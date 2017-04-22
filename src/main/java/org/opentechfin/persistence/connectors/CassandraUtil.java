package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.typesafe.config.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.opentechfin.utils.ConfigHolder;
import org.opentechfin.utils.Constants;
import org.opentechfin.utils.VerifyArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operate on {@link CassandraConnector}
 * Created by boweiliu on 4/15/17.
 */
public class CassandraUtil {

  private static Logger logger = LoggerFactory.getLogger(CassandraUtil.class);

  private static String[] typeOfSeries = {"h", "l", "o", "c", "v"};

  private static Set<String> stockSymbols = new HashSet<>();

  public static void addStock(String symbol, CassandraConnector connector) {
    VerifyArgs.nonNullOrEmptyStr(symbol, "stock symbol cannot be null or empty");
    String query = "DEFAULT VALUE";
    try {
      List<ResultSetFuture> resultSetFutureList = new ArrayList<>();
      for (int i = 0; i < typeOfSeries.length; i++) {
        query = String.format("ALTER TABLE %1$s.%2$s\n"
            + "  ADD %3$s_%4$s float;", connector.STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY,
            symbol.toLowerCase(),
            typeOfSeries[i]);
        resultSetFutureList.add(connector.getSession().executeAsync(query));
      }
      resultSetFutureList.forEach(resultSetFuture -> {
        try {
          resultSetFuture.get();
        } catch (InterruptedException e) {
          e.printStackTrace();  // which is unlikely to happen
        } catch (ExecutionException e) {
          if (e.getCause() instanceof InvalidQueryException) {
            if (e.getCause().getMessage().contains("conflicts with an existing column")) {
              logger.warn(e.getCause().getMessage());
            } else {
              throw new RuntimeException(e);
            }
          } else {
            throw new RuntimeException(e);
          }
        }
      });
    } catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }

  public static boolean hasStock(String symbol, CassandraConnector connector) {
    VerifyArgs.nonNullOrEmptyStr(symbol, "stock symbol cannot be null or empty");
    String[] columns = new String[typeOfSeries.length];
    for (int i = 0; i < typeOfSeries.length; i++) {
      columns[i] = symbol.toLowerCase() + "_" + typeOfSeries[i];
    }
    Config config = ConfigHolder.staticGetConfig();
    Select.Where statement = QueryBuilder.select().all().from("system_schema", "columns")
        .where(QueryBuilder.in("column_name", Arrays.asList(columns)))
        .and(QueryBuilder.eq("keyspace_name", config.getString("cassandra.STOCK_KEY_SPACE")))
        .and(QueryBuilder.eq("table_name", config.getString("cassandra.STOCK_COL_FAMILY")));
    try {
      ResultSet resultSet = connector.getSession().execute(statement);
      return !resultSet.isExhausted();
    } catch (Exception ex) {
      logger.error("executed query: \n{}", statement.getQueryString());
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }

  public static void removeStock(String symbol, CassandraConnector connector) {
    VerifyArgs.nonNullOrEmptyStr(symbol, "stock symbol cannot be null or empty");
    String query = "DEFAULT VALUE";
    try {
      List<ResultSetFuture> resultSetFutureList = new ArrayList<>();
      for (int i = 0; i < typeOfSeries.length; i++) {
        query = String.format("ALTER TABLE %1$s.%2$s\n"
                + "  DROP %3$s_%4$s;", connector.STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY,
            symbol.toLowerCase(),
            typeOfSeries[i]);
        resultSetFutureList.add(connector.getSession().executeAsync(query));
      }
      resultSetFutureList.forEach(resultSetFuture -> {
        try {
          resultSetFuture.get();
        } catch (InterruptedException ite) {
          ite.printStackTrace();
        } catch (ExecutionException e) {
          if (e.getCause() instanceof InvalidQueryException) {
            if (e.getCause().getMessage().contains("was not found in table ")) {
              logger.warn(e.getCause().getMessage());
            } else {
              throw new RuntimeException(e);
            }
          } else {
            throw new RuntimeException(e);
          }
        }
      });
    } catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }
}
