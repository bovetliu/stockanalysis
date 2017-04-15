package org.opentechfin.persistence.connectors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 */
public class CassandraConnector {

  private final Cluster cluster;
  private final Session session;
  private CassandraConnector() {
    cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
    session = cluster.connect();
  }

}
