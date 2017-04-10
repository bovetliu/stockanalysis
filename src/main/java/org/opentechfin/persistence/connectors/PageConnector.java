package org.opentechfin.persistence.connectors;

import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;

/**
 */
public interface PageConnector<T>{

  Page<T> fetchPage(PageMeta pageMeta);
}
