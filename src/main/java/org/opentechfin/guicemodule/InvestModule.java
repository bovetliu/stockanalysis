package org.opentechfin.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.opentechfin.persistence.connectors.CassandraConnector;
import org.opentechfin.utils.ConfigHolder;

/**
 */
public class InvestModule extends AbstractModule {
    private static InvestModule singleton;

    private Injector injector;

    private InvestModule() {

    }

    protected void configure() {
        bind(ConfigHolder.class).asEagerSingleton();
        bind(CassandraConnector.class).asEagerSingleton();
    }

    public static InvestModule getSingleton() {
        if (singleton == null) {
            singleton = new InvestModule();
        }
        return singleton;
    }

    public static Injector injector() {
        InvestModule investModule = getSingleton();

        if (investModule.injector == null) {
            investModule.injector = Guice.createInjector(investModule);
        }
        return investModule.injector;
    }
}
