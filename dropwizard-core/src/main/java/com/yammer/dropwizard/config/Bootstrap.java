package com.yammer.dropwizard.config;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.Command;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.json.ObjectMapperFactory;

import java.util.List;

public class Bootstrap<T extends Configuration> {
    private String name;
    private Optional<T> configuration = Optional.<T>absent();
    private final ObjectMapperFactory objectMapperFactory;
    private final List<Bundle> bundles;
    private final List<ConfiguredBundle<? super T>> configuredBundles;
    private final List<Command> commands;
    private final MetricRegistry metricRegistry;

    public Bootstrap(Service<T> service) {
        this.name = service.getClass().getSimpleName();
        this.objectMapperFactory = new ObjectMapperFactory();
        this.bundles = Lists.newArrayList();
        this.configuredBundles = Lists.newArrayList();
        this.commands = Lists.newArrayList();
        this.metricRegistry = new MetricRegistry();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addBundle(Bundle bundle) {
        bundle.initialize(this);
        bundles.add(bundle);
    }

    public void addBundle(ConfiguredBundle<? super T> bundle) {
        bundle.initialize(this);
        configuredBundles.add(bundle);
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void addCommand(ConfiguredCommand<T> command) {
        commands.add(command);
    }

    public ObjectMapperFactory getObjectMapperFactory() {
        return objectMapperFactory;
    }

    public void runWithBundles(T configuration, Environment environment) throws Exception {
        for (Bundle bundle : bundles) {
            bundle.run(environment);
        }
        for (ConfiguredBundle<? super T> bundle : configuredBundles) {
            bundle.run(configuration, environment);
        }
    }

    public ImmutableList<Command> getCommands() {
        return ImmutableList.copyOf(commands);
    }

    /**
     * Return an Optional container for the Configuration object (which might not exist,
     * depending on whether the service flow actually set it).
     *
     * @return an Optional containing (or not) the Configuration
     */
    public Optional<T> getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Optional<T> configuration) {
        this.configuration = configuration;
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }
}
