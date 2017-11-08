package com.netflix.hystrix.strategy;

import org.junit.rules.ExternalResource;

import com.netflix.archaius.DefaultPropertyFactory;
import com.netflix.archaius.api.config.SettableConfig;
import com.netflix.archaius.config.DefaultSettableConfig;
import com.netflix.hystrix.strategy.properties.archaius2.Archaius2DynamicProperties;

public class Archaius2HystrixPlugins extends ExternalResource {

    private SettableConfig config;
    private HystrixPlugins hystrixPlugins;
    
    public Archaius2HystrixPlugins() {
    }

    @Override
    protected void before() throws Throwable {
        config = new DefaultSettableConfig();
        hystrixPlugins = HystrixPlugins.create(new Archaius2DynamicProperties(new DefaultPropertyFactory(config)));        
    }

    public SettableConfig getConfig() {
        return config;
    }
    
    public HystrixPlugins getHystrixPlugins() {
        return hystrixPlugins;
    }
}
