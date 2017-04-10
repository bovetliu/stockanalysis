package org.opentechfin.persistence.connectors;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;
import org.opentechfin.timeseries.DataPoint;
import org.opentechfin.utils.VerifyArgs;

/**
 * this class is only created for test
 */
public class DailyNamedFileConnector implements PageConnector<DataPoint>{

  public final String folder;

  public DailyNamedFileConnector(String fullPathDir) {
    VerifyArgs.nonNullOrEmptyStr(fullPathDir, "folder name cannot be null or empty.");
    folder = fullPathDir;
  }


  @Override
  public Page<DataPoint> fetchPage(PageMeta pageMeta) {
    int pageNumber = pageMeta.getPageNumber();
    List<DataPoint> pageContent = new ArrayList<>();
    LocalDate localDate = LocalDate.of(2016, 1, pageNumber);
    URL url = DailyNamedFileConnector.class.getClassLoader().getResource(
        folder + "/" + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    File file = new File(Preconditions.checkNotNull(url).getFile());
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))  {
      String currLine;
      while ((currLine = bufferedReader.readLine()) != null) {
        String[] token = currLine.split(",");
        if (token.length != 2) {
          continue;
        }
        pageContent.add(DataPoint.create(LocalDateTime.parse(token[0]), Float.valueOf(token[1])));
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return Page.create(pageMeta, pageContent);
  }
}
