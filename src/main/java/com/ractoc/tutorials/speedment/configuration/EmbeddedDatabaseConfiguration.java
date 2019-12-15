package com.ractoc.tutorials.speedment.configuration;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.constraints.NotNull;

@Configuration
public class EmbeddedDatabaseConfiguration implements ServletContextListener {
    // General database configuration parameters
    @Value("${dbms.port}")
    private int port;
    @Value("${dbms.schema}")
    private String schema;

    // embeded database configuration parameters
    @Value("${dbms.embedded.data.dir}")
    private String dataDir;
    @Value("${dbms.embedded.script.schema}")
    private String schemaScript;
    @Value("${dbms.embedded.script.data}")
    private String dataScript;
    @Value("${dbms.embedded}")
    private boolean embedded;

    private DB db;

    @NotNull
    @Bean
    ServletListenerRegistrationBean<ServletContextListener> getContextListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(this);
        return srb;
    }

    @Override
    public void contextInitialized(
            ServletContextEvent sce) {
        // only start the database when running in embedded mode
        if (embedded) {
            // start database
            DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
            configBuilder.setPort(port);
            configBuilder.setDataDir(dataDir);
            configBuilder.setSecurityDisabled(true);
            try {
                db = DB.newEmbeddedDB(configBuilder.build());
                db.start();
                db.createDB(schema);
                db.source(schemaScript);
                if (StringUtils.isNotEmpty(dataScript)) {
                    db.source(dataScript);
                }
            } catch (ManagedProcessException e) {
                throw new UnableToInitializeDatabaseException(e);
            }
        }
    }

    @Override
	public void contextDestroyed(ServletContextEvent sce) {
        // shutdown database
        if (db != null) {
            try {
                db.stop();
            } catch (ManagedProcessException e) {
                throw new UnableToShutDownDatabaseException(e);
            }
        }
    }

}
