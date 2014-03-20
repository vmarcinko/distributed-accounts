package me.distributedaccounts.mgmt.service.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

public class SingleDispatcherServletJettyService implements InitializingBean, DisposableBean, ApplicationContextAware {
    private int port;
    private String configLocation;
    private String servletPath;
    private Resource contextPathResource;

    private Server server;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebApplicationContext webApplicationContext = constructWebApplicationContext(applicationContext);
        ServletContextHandler servletContextHandler = constructServletContextHandler(webApplicationContext);

        server = new Server(port);
        server.setHandler(servletContextHandler);
        server.start();
    }

    private WebApplicationContext constructWebApplicationContext(ApplicationContext parent) {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setParent(parent);
        context.setConfigLocation(configLocation);
        return context;
    }

    private ServletContextHandler constructServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath("/");

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        contextHandler.addServlet(new ServletHolder(dispatcherServlet), servletPath);

        contextHandler.addEventListener(new ContextLoaderListener(context));
//        contextHandler.setResourceBase(contextPathResource.getURI().toString());
        return contextHandler;
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
        server.destroy();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConfigLocation(String configLocation) {
        Resource resource = null;
        this.configLocation = configLocation;
    }

    public void setContextPathResource(Resource contextPathResource) throws IOException {
        this.contextPathResource = contextPathResource;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }
}
