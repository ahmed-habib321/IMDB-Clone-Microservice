package org.example.apigateway.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class GatewayMetrics {

    private final MeterRegistry meterRegistry;
    private final Timer requestTimer;


    public void recordRequest(String method, String route, int status) {
        String outcome = status < 400 ? "SUCCESS" : status < 500 ? "CLIENT_ERROR" : "SERVER_ERROR";
        Counter.builder("gateway.requests.total")
                .description("Total gateway requests")
                .tag("method", method)
                .tag("route", normalizeRoute(route))
                .tag("status", String.valueOf(status))
                .tag("outcome", outcome)
                .register(meterRegistry)
                .increment();
    }

    public void recordRequestDuration(String method, String route, long durationMs) {
        Timer.builder("gateway.request.duration")
                .description("API Gateway request duration")
                .tag("method", method)
                .tag("route", normalizeRoute(route))
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordAuthFailure(String reason) {
        Counter.builder("gateway.auth.failures")
                .description("Authentication failures")

                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    public void recordRateLimitExceeded() {
        Counter.builder("gateway.rate_limit.exceeded")
                .description("Rate limit rejections")

                .register(meterRegistry)
                .increment();
    }

    private String normalizeRoute(String route) {
        if (route == null) return "unknown";
        return route.replaceAll("/\\d+", "/{id}");
    }

    public GatewayMetrics(MeterRegistry registry) {
        this.meterRegistry = registry;
        this.requestTimer = Timer.builder("gateway.request.duration")
                .description("API Gateway request duration")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }


}