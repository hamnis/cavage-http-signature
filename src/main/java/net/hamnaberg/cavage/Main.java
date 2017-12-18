package net.hamnaberg.cavage;

import io.vavr.collection.HashMap;
import okio.ByteString;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.register(new Resources());
        config.register(new SignatureFilter(HashMap.of("key1", ByteString.encodeUtf8("0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87"))));
        Server server = JettyHttpContainerFactory.createServer(URI.create("http://0.0.0.0:9999"), config);

        server.start();
        server.join();

    }
}
