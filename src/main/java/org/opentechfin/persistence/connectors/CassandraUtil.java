package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.exceptions.InvalidQueryException;
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

  public static void addStockToCassandra(String symbol, CassandraConnector connector) {
    VerifyArgs.nonNullOrEmptyStr(symbol, "stock symbol cannot be null or empty");
    String query = "DEFAULT VALUE";
    String[] typeOfSeries = {"h", "l", "o", "c", "v"};
    try {
      for (int i = 0; i < typeOfSeries.length; i++) {
        query = String.format("ALTER TABLE %1$s.%2$s\n"
            + "  ADD %3$s_%4$s float;", connector.STOCK_KEY_SPACE, Constants.STOCK_COL_FAMILY,
            symbol.toLowerCase(),
            typeOfSeries[i]);
        try {
          connector.getSession().execute(query);
        } catch (InvalidQueryException iqe) {
          if (iqe.getMessage().contains("conflicts with an existing column")) {
            logger.warn(symbol + "_" + typeOfSeries[i] + " already added, suppress InvalidQueryException and continue");
          } else {
            throw iqe;
          }
        }
      }
    } catch (Exception ex) {
      logger.error("executed query: \n{}", query);
      logger.error("Exception {} caught!", ex.getClass().getName());
      logger.error("detail:\n",ex);
      throw ex;
    }
  }
}
