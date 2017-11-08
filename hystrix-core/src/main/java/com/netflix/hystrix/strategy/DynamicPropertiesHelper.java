package com.netflix.hystrix.strategy;

import com.netflix.hystrix.strategy.properties.HystrixDynamicProperties;

public class DynamicPropertiesHelper {
    private static HystrixDynamicProperties dynamicProperties;
    
    public static void setDynamicProperties(HystrixDynamicProperties dynamicProperties) {
        DynamicPropertiesHelper.dynamicProperties = dynamicProperties;
    }
    
    public static HystrixDynamicProperties getDynamicProperties() {
        return DynamicPropertiesHelper.dynamicProperties;
    }
}
