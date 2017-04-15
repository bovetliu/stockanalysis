package org.opentechfin.persistence.connectors;

import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;

/**
 *
 * @param <T> the content inside a {@link Page} fetched by this connector.
 */
public interface PageConnector<T>{

  Page<T> fetchPage(PageMeta pageMeta);
}
