package com.netflix.hystrix.strategy.properties.archaius2;

import com.netflix.archaius.api.Property;
import com.netflix.archaius.api.PropertyRepository;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperties;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperty;

public class Archaius2DynamicProperties implements HystrixDynamicProperties{
    private final PropertyRepository propertyRepository;
    
    public Archaius2DynamicProperties(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public HystrixDynamicProperty<String> getString(String name, String fallback) {
        Property<String> property = propertyRepository.get(name, String.class).orElse(fallback);
        return new Archaius2HystrixProperty<>(property);
    }

    @Override
    public HystrixDynamicProperty<Integer> getInteger(String name, Integer fallback) {
        Property<Integer> property = propertyRepository.get(name, Integer.class).orElse(fallback);
        return new Archaius2HystrixProperty<>(property);
    }

    @Override
    public HystrixDynamicProperty<Long> getLong(String name, Long fallback) {
        Property<Long> property = propertyRepository.get(name, Long.class).orElse(fallback);
        return new Archaius2HystrixProperty<>(property);
    }

    @Override
    public HystrixDynamicProperty<Boolean> getBoolean(String name, Boolean fallback) {
        Property<Boolean> property = propertyRepository.get(name, Boolean.class).orElse(fallback);
        return new Archaius2HystrixProperty<>(property);
    }

    static class Archaius2HystrixProperty<T> implements HystrixDynamicProperty<T> {
        private final Property<T> archaius2Property;
        private Archaius2HystrixProperty(Property<T> archaius2Property) {
           this.archaius2Property = archaius2Property;
        }

        @Override
        public T get() {
            return archaius2Property.get();
        }

        @Override
        public String getName() {
            return archaius2Property.getKey();
        }

        @Override
        public void addCallback(Runnable callback) {
            archaius2Property.onChange(v->callback.run());
        }
        
    }

}
