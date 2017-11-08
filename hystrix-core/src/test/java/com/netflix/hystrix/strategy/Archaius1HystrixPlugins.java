package com.netflix.hystrix.strategy;

import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.rules.ExternalResource;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.strategy.properties.archaius.HystrixDynamicPropertiesArchaius;

public class Archaius1HystrixPlugins extends ExternalResource {
    private HystrixPlugins hystrixPlugins;
    private AbstractConfiguration config;
    
    public Archaius1HystrixPlugins() {
    }

    @Override
    protected void before() throws Throwable {
        config = ConfigurationManager.getConfigInstance();
        hystrixPlugins = HystrixPlugins.create(new HystrixDynamicPropertiesArchaius());        
    }
    
    @Override
    protected void after() {
        super.after();
        config.clear();
    }

    public AbstractConfiguration getConfig() {
        return config;
    }
    
    public HystrixPlugins getHystrixPlugins() {
        return hystrixPlugins;
    }
}
