package org.example.apigateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

@Configuration
public class RoutesConfig {

    @Value("${EurekaOrigin}")
    private String eurekaOrigin;

    @Bean
    public RouterFunction<ServerResponse> serviceRoutes() {
        List<RouteSpec> routeSpecs = List.of(
                // ---- discovery (direct uri, no lb) ----
                new RouteSpec("discovery-service", "/eureka", null, eurekaOrigin, "/"),
                new RouteSpec("discovery-service-static", "/eureka/**", null, eurekaOrigin, null),

                // ---- prefix-convention APIs + aggregated api-docs ----
                new RouteSpec("user-service", "/api/v1/user/**", "user-service"),
                new RouteSpec("user-service-api-docs", "/aggregate/user-service/v3/api-docs", "user-service", "/v3/api-docs"),
                // auth-service has no code yet; auth lives in api-gateway.
                new RouteSpec("awards-service", "/api/v1/awards/**", "awards-service"),
                new RouteSpec("awards-service-api-docs", "/aggregate/awards-service/v3/api-docs", "awards-service", "/v3/api-docs"),
                new RouteSpec("notification-service", "/api/v1/notification/**", "notification-service"),
                new RouteSpec("notification-service-api-docs", "/aggregate/notification-service/v3/api-docs", "notification-service", "/v3/api-docs"),
                new RouteSpec("ratings-reviews-service", "/api/v1/ratings-reviews/**", "ratings-reviews-service"),
                new RouteSpec("ratings-reviews-service-api-docs", "/aggregate/ratings-reviews-service/v3/api-docs", "ratings-reviews-service", "/v3/api-docs"),
                new RouteSpec("news-service", "/api/v1/news/**", "news-service"),
                new RouteSpec("news-service-api-docs", "/aggregate/news-service/v3/api-docs", "news-service", "/v3/api-docs"),
                new RouteSpec("media-service", "/api/v1/media/**", "media-service"),
                new RouteSpec("media-service-api-docs", "/aggregate/media-service/v3/api-docs", "media-service", "/v3/api-docs"),
                new RouteSpec("lists-service", "/api/v1/lists/**", "lists-service"),
                new RouteSpec("lists-service-api-docs", "/aggregate/lists-service/v3/api-docs", "lists-service", "/v3/api-docs"),
                new RouteSpec("contribution-service", "/api/v1/contribution/**", "contribution-service"),
                new RouteSpec("contribution-service-api-docs", "/aggregate/contribution-service/v3/api-docs", "contribution-service", "/v3/api-docs"),
                new RouteSpec("people-service", "/api/v1/people/**", "people-service"),
                new RouteSpec("people-service-api-docs", "/aggregate/people-service/v3/api-docs", "people-service", "/v3/api-docs"),
                new RouteSpec("title-service", "/api/v1/title/**", "title-service"),
                new RouteSpec("title-service-api-docs", "/aggregate/title-service/v3/api-docs", "title-service", "/v3/api-docs"),
                new RouteSpec("search-service", "/api/v1/search/**", "search-service"),
                new RouteSpec("search-service-api-docs", "/aggregate/search-service/v3/api-docs", "search-service", "/v3/api-docs"),

                // ---- non-conventional paths ----
                // media + contribution own title-scoped sub-paths, so they must
                // be evaluated before the ratings-reviews /api/v1/titles/** catch-all.
                new RouteSpec("media-title-images", "/api/v1/titles/*/images/**", "media-service"),
                new RouteSpec("media-title-trailers", "/api/v1/titles/*/trailers/**", "media-service"),
                new RouteSpec("media-title-box-office", "/api/v1/titles/*/box-office/**", "media-service"),
                new RouteSpec("media-people-images", "/api/v1/people/*/images/**", "media-service"),
                new RouteSpec("media-box-office-top", "/api/v1/box-office/top", "media-service"),
                new RouteSpec("contribution-trivia", "/api/v1/titles/*/trivia/**", "contribution-service"),
                new RouteSpec("contribution-goofs", "/api/v1/titles/*/goofs/**", "contribution-service"),
                new RouteSpec("contribution-quotes", "/api/v1/titles/*/quotes/**", "contribution-service"),
                new RouteSpec("ratings-reviews-titles", "/api/v1/titles/**", "ratings-reviews-service"),
                new RouteSpec("ratings-reviews-moderate", "/api/v1/reviews/**", "ratings-reviews-service"),
                new RouteSpec("ratings-reviews-users", "/api/v1/users/**", "ratings-reviews-service"),
                new RouteSpec("lists-watchlist", "/api/v1/watchlist/**", "lists-service"),
                new RouteSpec("people-cast", "/api/v1/cast/**", "people-service"),
                new RouteSpec("people-crew", "/api/v1/crew/**", "people-service"),
                new RouteSpec("media-static", "/media/**", "media-service")
        );

        RouterFunction<ServerResponse> routes = null;

        for (RouteSpec spec : routeSpecs) {
            RouterFunctions.Builder builder = GatewayRouterFunctions.route(spec.name())
                    .route(RequestPredicates.path(spec.path()), HandlerFunctions.http());

            if (spec.rewrittenTo() != null) {
                builder = builder.before(BeforeFilterFunctions.rewritePath(spec.path(), spec.rewrittenTo()));
            }

            if (spec.directUri() != null) {
                builder = builder.before(BeforeFilterFunctions.uri(spec.directUri()));
            } else {
                builder = builder.filter(LoadBalancerFilterFunctions.lb(spec.service()));
            }

            routes = chain(routes, builder.build());
        }

        return routes;
    }

    private RouterFunction<ServerResponse> chain(RouterFunction<ServerResponse> acc, RouterFunction<ServerResponse> next) {
        return (acc == null) ? next : acc.and(next);
    }

    record RouteSpec(String name, String path, String service, String directUri, String rewrittenTo) {
        RouteSpec(String name, String path, String service) {
            this(name, path, service, null, null);
        }

        RouteSpec(String name, String path, String service, String rewrittenTo) {
            this(name, path, service, null, rewrittenTo);
        }
    }
}