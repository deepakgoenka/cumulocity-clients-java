package com.cumulocity.agent.server.servers.jaxrs;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.context.DeviceContextFilter;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

@Component("jaxrsServer")
public class JaxrsServer implements Server {

    private static final Logger log = LoggerFactory.getLogger(JaxrsServer.class);

    private final String host;

    private final int port;

    private final String applicationId;

    private final ResourceConfig resourceConfig;

    private final WebApplicationContext applicationContext;

    private final Service service = new AbstractService() {

        private HttpServer server;

        @Override
        protected void doStart() {
            server = HttpServer.createSimpleServer(null, new InetSocketAddress(host, port));

            server.getListener("grizzly")
                    .getTransport()
                    .setWorkerThreadPoolConfig(
                            ThreadPoolConfig.defaultConfig().setPoolName("grizzly-" + applicationId).setCorePoolSize(10)
                                    .setMaxPoolSize(100));

            WebappContext context = new WebappContext(applicationId, "/" + applicationId);

            registerFilter(DeviceContextFilter.class);
            context.addServlet("jersey-servlet", new ServletContainer(resourceConfig)).addMapping("/*");
            context.addListener(new ContextLoaderListener(applicationContext));
            context.deploy(server);
            try {
                server.start();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        private void registerFilter(final Class<?> type) {
            try {
                resourceConfig.register(applicationContext.getBean(type));
            } catch (Exception e) {
                log.info("failed to register " + type, e);
            }
        }

        @Override
        protected void doStop() {
            server.shutdownNow();
        }
    };

    @Autowired
    public JaxrsServer(@Value("${server.host:0.0.0.0}") String host, @Value("${server.port:80}") int port,
            @Value("${application.context:${server.id}}") String contextPath, ResourceConfig resourceConfig, WebApplicationContext context) {
        this.host = host;
        this.port = port;
        this.applicationId = contextPath;
        this.resourceConfig = resourceConfig;
        this.applicationContext = context;
    }

    @Override
    public void start() {
        service.startAsync();
        service.awaitRunning();
    }

    @Override
    public void stop() {
        service.stopAsync();
        service.awaitTerminated();
    }

    @Override
    public void awaitTerminated() {
        service.awaitTerminated();
    }
}
