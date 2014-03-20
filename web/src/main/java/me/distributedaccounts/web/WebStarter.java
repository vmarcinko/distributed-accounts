package me.distributedaccounts.web;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.distributedaccounts.web.console.ConsoleController;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.IOException;

public class WebStarter {
    public static void main(String[] args) throws InterruptedException, IOException {
        OptionParser parser = new OptionParser();
        parser.accepts("clusterName").withRequiredArg();
        parser.accepts("instanceName").withRequiredArg();
        OptionSet options = parser.parse(args);

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        MutablePropertySources propertySources = ctx.getEnvironment().getPropertySources();
        propertySources.addFirst(new JOptCommandLinePropertySource(options));

        ctx.load("classpath:applicationContext.xml");
        ctx.refresh();

        ctx.registerShutdownHook();
        ctx.start();

        ConsoleController consoleController = ctx.getBean("consoleController", ConsoleController.class);
        consoleController.readAndProcessConsoleInputs();

        ctx.stop();
        ctx.close();
    }
}
