package org.opentechfin.testlib;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.persistence.connectors.CassandraUtil;
import org.opentechfin.persistence.connectors.DailyNamedFileConnector;
import org.opentechfin.utils.ConfigHolder;
import org.opentechfin.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockDataSeedHelper {
  private static ConfigHolder configHolder = ConfigHolder.staticGetConfigHolder();
  private static Logger logger = LoggerFactory.getLogger(StockDataSeedHelper.class);


  public static void loadGoogleFinance(File file, String symbol, String keySpace) {
    Preconditions.checkArgument(file != null, "file must not be null");
    CassandraConnector cassandraConnector =
        new CassandraConnector(ConfigHolder.staticGetConfigHolder(), keySpace);
    CassandraUtil.addStock("amd", cassandraConnector);
    Config config = configHolder.getConfig();
    String[] colNames = new String[] {
        symbol + "_c",
        symbol + "_h",
        symbol + "_l",
        symbol + "_o",
        symbol + "_v"
    };
    PreparedStatement preparedStatement = cassandraConnector.getSession().prepare(
        String.format("INSERT INTO %s.stock_col_family (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
            keySpace,
            config.getString("cassandra.DATE_COL_NAME"),
            config.getString("cassandra.SEC_OF_DAY_COL_NAME"),
            symbol + "_c",
            symbol + "_h",
            symbol + "_l",
            symbol + "_o",
            symbol + "_v"));
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 1024 * 4)) {
      String currLine;
      boolean encounteredTimezone = false;
      int unixTimestampStart = 0;
      // COLUMNS=DATE,CLOSE,HIGH,LOW,OPEN,VOLUME
      while ((currLine = bufferedReader.readLine()) != null) {
        if (!encounteredTimezone) {
          if (currLine.contains("TIMEZONE_OFFSET")) {
            encounteredTimezone = true;
            continue;
          } else {  // metadata intepretation block
            continue;
          }
        }
        String[] token = currLine.split(",");
        final LocalDateTime localDateTime;
        if (currLine.startsWith("a")) {
          unixTimestampStart = Integer.parseInt(token[0].substring(1), 10);
          localDateTime = TimeUtils.secondToEasternTime(unixTimestampStart);
        } else {
          int unixTimestamp = unixTimestampStart + 60 * Integer.parseInt(token[0], 10);
          localDateTime = TimeUtils.secondToEasternTime(unixTimestamp);
        }
        Float closePrice = Float.parseFloat(token[1]);
        Float highPrice = Float.parseFloat(token[2]);
        Float lowPrice = Float.parseFloat(token[3]);
        Float openPrice = Float.parseFloat(token[4]);
        Double volume = Double.parseDouble(token[5]);

        BoundStatement bound = preparedStatement.bind();
        bound.setDate(0, TimeUtils.javaLocalDateToDatastaxLocalDate(localDateTime.toLocalDate()));
        bound.setTime(1, ChronoUnit.MILLIS.between(localDateTime.toLocalDate().atStartOfDay(), localDateTime) * 1_000_000L);
        bound.setFloat(2, closePrice);
        bound.setFloat(3, highPrice);
        bound.setFloat(4, lowPrice);
        bound.setFloat(5, openPrice);
        bound.setDouble(6, volume);
        cassandraConnector.getSession().execute(bound);
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } finally {
      cassandraConnector.getCluster().close();
    }
  }

  public static void main(String[] args) {
    URL url = DailyNamedFileConnector.class.getClassLoader().getResource(
        "googlefinance/google_finance_2017-04-23_to_prev_20d.txt");
    File file = new File(Preconditions.checkNotNull(url, "url cannot be null").getFile());
    loadGoogleFinance(file, "amd", "stock_key_space");
//    Long days = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), LocalDateTime.now().toLocalDate());
//    System.out.println(days);
  }
}
