/**
 * Copyright 2016 Netflix, Inc.
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
package com.netflix.hystrix.contrib.sample.stream;

import com.netflix.hystrix.metric.sample.HystrixUtilization;
import com.netflix.hystrix.metric.sample.HystrixUtilizationStream;
import com.netflix.hystrix.serial.SerialHystrixUtilization;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperty;

import rx.Observable;
import rx.functions.Func1;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;

/**
 * Streams Hystrix config in text/event-stream format.
 * <p>
 * Install by:
 * <p>
 * 1) Including hystrix-metrics-event-stream-*.jar in your classpath.
 * <p>
 * 2) Adding the following to web.xml:
 * <pre>{@code
 * <servlet>
 *  <description></description>
 *  <display-name>HystrixUtilizationSseServlet</display-name>
 *  <servlet-name>HystrixUtilizationSseServlet</servlet-name>
 *  <servlet-class>com.netflix.hystrix.contrib.sample.stream.HystrixUtilizationSseServlet</servlet-class>
 * </servlet>
 * <servlet-mapping>
 *  <servlet-name>HystrixUtilizationSseServlet</servlet-name>
 *  <url-pattern>/hystrix/utilization.stream</url-pattern>
 * </servlet-mapping>
 * } </pre>
 */
public class HystrixUtilizationSseServlet extends HystrixSampleSseServlet {

    private static final long serialVersionUID = -7812908330777694972L;

    /* used to track number of connections and throttle */
    private static AtomicInteger concurrentConnections = new AtomicInteger(0);
    private HystrixDynamicProperty<Integer> maxConcurrentConnections;

    public HystrixUtilizationSseServlet() {
        this(HystrixUtilizationStream.getInstance().observe(), DEFAULT_PAUSE_POLLER_THREAD_DELAY_IN_MS);
    }

    /* package-private */ HystrixUtilizationSseServlet(Observable<HystrixUtilization> sampleStream, int pausePollerThreadDelayInMs) {
        super(sampleStream.map(new Func1<HystrixUtilization, String>() {
            @Override
            public String call(HystrixUtilization hystrixUtilization) {
                return SerialHystrixUtilization.toJsonString(hystrixUtilization);
            }
        }), pausePollerThreadDelayInMs);
    }
    
    @Override
    public void init() throws ServletException {
        maxConcurrentConnections =
                HystrixPlugins.getInstance().getDynamicProperties().getInteger("hystrix.config.stream.maxConcurrentConnections", 5);
        super.init();
    }

    @Override
    protected int getMaxNumberConcurrentConnectionsAllowed() {
        return maxConcurrentConnections.get();
    }

    @Override
    protected int getNumberCurrentConnections() {
        return concurrentConnections.get();
    }

    @Override
    protected int incrementAndGetCurrentConcurrentConnections() {
        return concurrentConnections.incrementAndGet();
    }

    @Override
    protected void decrementCurrentConcurrentConnections() {
        concurrentConnections.decrementAndGet();
    }
}

