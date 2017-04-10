package org.opentechfin.persistence.connectors;

import org.opentechfin.persistence.Page;
import org.opentechfin.persistence.PageMeta;

/**
 * Created by boweiliu on 4/9/17.
 */
public interface PageConnected <T>{

  Page<T> fetchPage(PageMeta pageMeta);
}
