package me.distributedaccounts.search.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.core.env.MutablePropertySources;

public class ServiceStarter {

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        parser.accepts("host").withRequiredArg();
        parser.accepts("port").withRequiredArg();
        parser.accepts("indexFile").withRequiredArg();
        OptionSet options = parser.parse(args);

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        MutablePropertySources propertySources = ctx.getEnvironment().getPropertySources();
        propertySources.addFirst(new JOptCommandLinePropertySource(options));
        ctx.load("classpath:applicationContext.xml");
        ctx.refresh();

        ctx.registerShutdownHook();
        ctx.start();

        Thread.currentThread().join();

        ctx.stop();
        ctx.close();
    }
}
