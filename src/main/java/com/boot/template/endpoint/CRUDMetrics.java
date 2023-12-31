package com.boot.template.endpoint;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Configuration
public class CRUDMetrics {
    final static Logger logger = LoggerFactory.getLogger(CRUDMetrics.class);

    MeterRegistry registry;

    Counter counter;

    AtomicLong existingObjectCount = new AtomicLong();
    Gauge gauge;

    DistributionSummary summary;

    public CRUDMetrics(MeterRegistry registry) {
        //Create and register a new counter to count uploaded objects. 
        counter = registry.counter("uploadedObjects", "myTag1", "myTag1Value", "myTag2", "myTag2Value");

        //Create and register a new gauge to keep track of existingObjectCount.
        registry.gauge("existingObjects", Tags.empty(), existingObjectCount);

        //Creatge and register a new summary/histogram with its metadata.
        summary = registry.summary("testSummary", "myTag1", "myTag1Value");
    }

    //Time things by registering this TimedAspect bean, and then using a matching @Timed annotation on the method to be timed. (See CRUDController.java)
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    public void countUploadedObject() {
        logger.info("Incrementing Counter...");
        counter.increment();
    }

    public void incrementStoredObjectsCount() {
        existingObjectCount.incrementAndGet();
    }

    public void decrementStoredObjectsCount() {
        existingObjectCount.decrementAndGet();
    }

    public void recordSummaryValue(double value) {
        summary.record(value);
    }

}
