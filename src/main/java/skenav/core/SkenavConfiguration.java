package skenav.core;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.jetty.HttpsConnectorFactory;
import io.dropwizard.server.AbstractServerFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.SslConnectionFactory;
import skenav.core.db.Database;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkenavConfiguration extends Configuration {
    public SkenavConfiguration() {
        super();
        if (!Cache.INSTANCE.getTlsAlreadySet()) {
            HttpConnectorFactory httpconnectorfactory = new HttpConnectorFactory();
            httpconnectorfactory.setPort(80);
            List<ConnectorFactory> applicationConnectors = new ArrayList<ConnectorFactory>();
            applicationConnectors.add(httpconnectorfactory);
            if (new File(OS.getSkenavDirectory() + "database.mv.db").exists() && new Database().getAppData("usetls").equals("true") && new File(OS.getSkenavDirectory() + "SkenavKeyStore.jks").exists()) {
                System.out.println("tls connectors triggered");
                HttpsConnectorFactory httpsconnectorfactory = new HttpsConnectorFactory();
                httpsconnectorfactory.setPort(443);
                System.out.println("tls port set");
                httpsconnectorfactory.setKeyStoreType("JKS");
                System.out.println("keystore type set");
                httpsconnectorfactory.setKeyStorePath(OS.getSkenavDirectory() + "SkenavKeyStore.jks");
                System.out.println("keystore path is: " + httpsconnectorfactory.getKeyStorePath());
                System.out.println("keystore path set");

                //TODO: generate keystore password
                httpsconnectorfactory.setKeyStorePassword("changeit");
                //httpsconnectorfactory.setBindHost(new Database().getAppData("CA domain"));
                applicationConnectors.add(httpsconnectorfactory);
            }
            DefaultServerFactory serverFactory = (DefaultServerFactory) getServerFactory();
            serverFactory.setApplicationConnectors(applicationConnectors);
        }

    }


}
