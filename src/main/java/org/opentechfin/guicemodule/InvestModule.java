package org.opentechfin.guicemodule;

import com.google.inject.AbstractModule;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.utils.ConfigHolder;

/**
 */
public class InvestModule extends AbstractModule {
    protected void configure() {
        bind(ConfigHolder.class).asEagerSingleton();
        bind(CassandraConnector.class).asEagerSingleton();
    }
}
