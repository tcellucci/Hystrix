/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.strategy.properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.netflix.hystrix.strategy.Archaius1HystrixPlugins;

public class HystrixPropertiesChainedArchaius1PropertyTest {
    
    @Rule
    public Archaius1HystrixPlugins archaiusPlugins = new Archaius1HystrixPlugins();

    protected HystrixDynamicProperties hystrixProperties;
    
    @Before
    public void init() {
        hystrixProperties = archaiusPlugins.getHystrixPlugins().getDynamicProperties();
    }
    
    
    @Test
    public void testString() throws Exception {

        HystrixDynamicProperty<String> pString = hystrixProperties.getString("defaultString", "default-default");
        HystrixDynamicProperty<String> fString = HystrixPropertiesChainedProperty.forString().add("overrideString", null).add(pString).build();
        
        assertTrue("default-default".equals(fString.get()));

        archaiusPlugins.getConfig().setProperty("defaultString", "default");
        assertTrue("default".equals(fString.get()));

        archaiusPlugins.getConfig().setProperty("overrideString", "override");
        assertTrue("override".equals(fString.get()));

        archaiusPlugins.getConfig().clearProperty("overrideString");
        assertTrue("default".equals(fString.get()));

        archaiusPlugins.getConfig().clearProperty("defaultString");
        assertTrue("default-default".equals(fString.get()));
    }

    @Test
    public void testInteger() throws Exception {

        HystrixDynamicProperty<Integer> pInt = hystrixProperties.getInteger("defaultInt", -1);
        HystrixDynamicProperty<Integer> fInt = HystrixPropertiesChainedProperty.forInteger().add("overrideInt", null).add(pInt).build();

        assertTrue(-1 == fInt.get());

        archaiusPlugins.getConfig().setProperty("defaultInt", 10);
        assertTrue(10 == fInt.get());

        archaiusPlugins.getConfig().setProperty("overrideInt", 11);
        assertTrue(11 == fInt.get());

        archaiusPlugins.getConfig().clearProperty("overrideInt");
        assertTrue(10 == fInt.get());

        archaiusPlugins.getConfig().clearProperty("defaultInt");
        assertTrue(-1 == fInt.get());
    }

    @Test
    public void testBoolean() throws Exception {

        HystrixDynamicProperty<Boolean> pBoolean = hystrixProperties.getBoolean("defaultBoolean", true);
        HystrixDynamicProperty<Boolean> fBoolean = HystrixPropertiesChainedProperty.forBoolean().add("overrideBoolean", null).add(pBoolean).build();

        System.out.println("pBoolean: " + pBoolean.get());
        System.out.println("fBoolean: " + fBoolean.get());

        assertTrue(fBoolean.get());

        archaiusPlugins.getConfig().setProperty("defaultBoolean", Boolean.FALSE);

        System.out.println("pBoolean: " + pBoolean.get());
        System.out.println("fBoolean: " + fBoolean.get());

        assertFalse(fBoolean.get());

        archaiusPlugins.getConfig().setProperty("overrideBoolean", Boolean.TRUE);
        assertTrue(fBoolean.get());

        archaiusPlugins.getConfig().clearProperty("overrideBoolean");
        assertFalse(fBoolean.get());

        archaiusPlugins.getConfig().clearProperty("defaultBoolean");
        assertTrue(fBoolean.get());
    }

    @Test
    public void testChainingString() throws Exception {

        HystrixDynamicProperty<String> node1 = hystrixProperties.getString("node1", "v1");
        HystrixDynamicProperty<String> node2 = HystrixPropertiesChainedProperty.forString().add("node2", null).add(node1).build();
        HystrixDynamicProperty<String> node3 = HystrixPropertiesChainedProperty.forString().add("node3", null).add(node2).build();

        assertTrue("" + node3.get(), "v1".equals(node3.get()));

        archaiusPlugins.getConfig().setProperty("node1", "v11");
        assertTrue("v11".equals(node3.get()));

        archaiusPlugins.getConfig().setProperty("node2", "v22");
        assertTrue("v22".equals(node3.get()));

        archaiusPlugins.getConfig().clearProperty("node1");
        assertTrue("v22".equals(node3.get()));

        archaiusPlugins.getConfig().setProperty("node3", "v33");
        assertTrue("v33".equals(node3.get()));

        archaiusPlugins.getConfig().clearProperty("node2");
        assertTrue("v33".equals(node3.get()));

        archaiusPlugins.getConfig().setProperty("node2", "v222");
        assertTrue("v33".equals(node3.get()));

        archaiusPlugins.getConfig().clearProperty("node3");
        assertTrue("v222".equals(node3.get()));

        archaiusPlugins.getConfig().clearProperty("node2");
        assertTrue("v1".equals(node3.get()));

        archaiusPlugins.getConfig().setProperty("node2", "v2222");
        assertTrue("v2222".equals(node3.get()));
    }

    @Test
    public void testChainingInteger() throws Exception {

        HystrixDynamicProperty<Integer> node1 = hystrixProperties.getInteger("node1", 1);
        HystrixDynamicProperty<Integer> node2 = HystrixPropertiesChainedProperty.forInteger().add("node2", null).add(node1).build();
        HystrixDynamicProperty<Integer> node3 = HystrixPropertiesChainedProperty.forInteger().add("node3", null).add(node2).build();

        assertTrue("" + node3.get(), 1 == node3.get());

        archaiusPlugins.getConfig().setProperty("node1", 11);
        assertTrue(11 == node3.get());

        archaiusPlugins.getConfig().setProperty("node2", 22);
        assertTrue(22 == node3.get());

        archaiusPlugins.getConfig().clearProperty("node1");
        assertTrue(22 == node3.get());

        archaiusPlugins.getConfig().setProperty("node3", 33);
        assertTrue(33 == node3.get());

        archaiusPlugins.getConfig().clearProperty("node2");
        assertTrue(33 == node3.get());

        archaiusPlugins.getConfig().setProperty("node2", 222);
        assertTrue(33 == node3.get());

        archaiusPlugins.getConfig().clearProperty("node3");
        assertTrue(222 == node3.get());

        archaiusPlugins.getConfig().clearProperty("node2");
        assertTrue(1 == node3.get());

        archaiusPlugins.getConfig().setProperty("node2", 2222);
        assertTrue(2222 == node3.get());
    }

    @Test
    public void testAddCallback() throws Exception {

        final HystrixDynamicProperty<String> node1 = hystrixProperties.getString("n1", "n1");
        final HystrixDynamicProperty<String> node2 = HystrixPropertiesChainedProperty.forString().add("n2", null).add(node1).build();

        final AtomicInteger callbackCount = new AtomicInteger(0);

        node2.addCallback(new Runnable() {
            @Override
            public void run() {
                callbackCount.incrementAndGet();
            }
        });

        assertTrue(0 == callbackCount.get());

        assertTrue("n1".equals(node2.get()));
        assertTrue(0 == callbackCount.get());

        archaiusPlugins.getConfig().setProperty("n1", "n11");
        assertTrue("n11".equals(node2.get()));
        assertTrue(0 == callbackCount.get());

        archaiusPlugins.getConfig().setProperty("n2", "n22");
        assertTrue("n22".equals(node2.get()));
        assertTrue(1 == callbackCount.get());

        archaiusPlugins.getConfig().clearProperty("n1");
        assertTrue("n22".equals(node2.get()));
        assertTrue(1 == callbackCount.get());

        archaiusPlugins.getConfig().setProperty("n2", "n222");
        assertTrue("n222".equals(node2.get()));
        assertTrue(2 == callbackCount.get());

        archaiusPlugins.getConfig().clearProperty("n2");
        assertTrue("n1".equals(node2.get()));
        assertTrue(3 == callbackCount.get());
    }

}
