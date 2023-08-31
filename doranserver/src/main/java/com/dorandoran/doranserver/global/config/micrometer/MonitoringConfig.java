package com.dorandoran.doranserver.global.config.micrometer;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MonitoringConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public CountedAspect countedAspect() {
        return new CountedAspect(meterRegistry);
    }

    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect(meterRegistry);
    }
}
