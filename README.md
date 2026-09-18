[//]: # (Done: pyroscope wired into all services via Dockerfile agent + JAVA_TOOL_OPTIONS; tempo works with metrics_generator + remote write)

[//]: # (info the logs on docker will show only info not debug because the config in config server )
[//]: # (TODO @EnableAsync)
[//]: # (TODO add search service to api gateway)
[//]: # (TODO extract shared dto and enums into a shared `common-dto` library and depend on it as a Maven module. For now, local copies are the pragmatic choice.)
[//]: # (TODO fix the mapper that has alot of redundant code)
[//]: # (TODO make all localhost has 2 states either localhost in local profile or docker container name in docker profile)

### `@EnableJpaAuditing` --> `@CreatedDate` / `@LastModifiedDate`

> **`@RequestHeader("X-User-Id")`:** In a JWT-secured microservice the API gateway validates the token and forwards the
> authenticated user's ID as a trusted header. This avoids requiring Spring Security's full `@AuthenticationPrincipal`
> chain in every downstream service. Alternatively wire up JWT filter locally and use `@AuthenticationPrincipal`.

--------------------------------------------

# IMDB — Microservices Architecture

## 1. Service Decomposition Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          API GATEWAY / BFF                              │
│              (Spring Cloud Gateway  |  Rate Limiting  |  Auth Filter)   │
└───────┬─────────┬──────────┬─────────┬─────────┬────────────────────────┘
        │         │          │         │         │
  ┌─────▼──┐ ┌────▼───┐ ┌────▼───┐ ┌───▼────┐ ┌──▼──────┐
  │  Auth  │ │  User  │ │ Title  │ │ People │ │Ratings/ │
  │Service │ │Service │ │Service │ │Service │ │Reviews  │
  └────────┘ └────────┘ └────────┘ └────────┘ └─────────┘
  ┌──────────┐ ┌───────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────────┐
  │  Lists & │ │ Media │ │  News  │ │Awards  │ │Contrib-│ │  Audit     │
  │Watchlist │ │Service│ │Service │ │Service │ │ution   │ │  Service   │
  └──────────┘ └───────┘ └────────┘ └────────┘ └────────┘ └────────────┘

                    ┌──────────────────────────┐
                    │   Message Broker (Kafka) │
                    └──────────────────────────┘
```

### Flow

```
Client → POST /auth/login
       → Auth Service calls User Service GET /users/by-username/{u}
       → Verifies password hash locally
       → Issues JWT (15 min) + refresh token (7 days, stored hashed)
       → Returns both tokens to client

Client → API requests include Authorization: Bearer <JWT>
       → API Gateway validates JWT signature (shared secret / public key)
       → Extracts userId + roles, forwards as headers to downstream services
```

---

## 2. Inter-Service Communication

### Synchronous (REST / HTTP)

Used only for real-time, user-facing reads where data is needed immediately.

| Caller          | Called Service | Purpose                                          |
|-----------------|----------------|--------------------------------------------------|
| Auth Service    | User Service   | Fetch credentials during login                   |
| API Gateway     | Auth Service   | JWT validation                                   |
| News Service    | User Service   | Resolve author display name for article response |
| Ratings Service | Title Service  | Validate title exists before accepting rating    |

> **Rule:** Synchronous calls are kept to an absolute minimum to avoid tight coupling. Services tolerate eventual
> consistency for non-critical data.

---

## 3. Event-Driven Contracts (Kafka Topics)

```
┌──────────────────────────────────────────────────────────────────┐
│ Topic naming convention:  {service-domain}.{entity}.{event}      │
│ Payload format:           JSON, schema-versioned                 │
│ Partitioning:             by entityId (UUID) for ordering        │
└──────────────────────────────────────────────────────────────────┘
```

| Topic                    | Producer     | Consumers                            | Payload (key fields)                     |
|--------------------------|--------------|--------------------------------------|------------------------------------------|
| `user.registered`        | User         | Audit                                | `{ userId, email, username, registeredAt }` |
| `user.deactivated`       | User         | Auth, Audit                          | `{ userId }`                             |
| `title.created`          | Title        | Audit                                | `{ titleId, type, primaryTitle }`        |
| `title.updated`          | Title        | Media (cache bust)                   | `{ titleId, changedFields }`             |
| `title.deleted`          | Title        | Ratings, Lists, Contributions, Audit | `{ titleId }`                            |
| `person.created`         | People       | Audit                                | `{ personId, name }`                     |
| `cast.added`             | People       | Audit                                | `{ titleId, personId, characterName }`   |
| `rating.created`         | Ratings      | Audit                                | `{ ratingId, userId, titleId, score }`   |
| `rating.aggregated`      | Ratings      | Title                                | `{ titleId, avgRating, voteCount }`      |
| `review.created`         | Ratings      | Audit                                | `{ reviewId, userId, titleId }`          |
| `contribution.submitted` | Contribution | Audit                                | `{ type, entityId, titleId, userId }`    |
| `news.published`         | News         | Audit                                | `{ articleId, slug, authorId }`          |

---

## 4. Infrastructure Components

```
┌─────────────────────────────────────────────────────────────────┐
│                     Infrastructure Stack                        │
│                                                                 │
│  ┌─────────────────┐   ┌──────────────────┐   ┌─────────────┐   │
│  │  API Gateway    │   │  Service Registry│   │   Config    │   │
│  │  Spring Cloud   │   │  Eureka / Consul │   │   Server    │   │
│  │  Gateway        │   │                  │   │ Spring Cloud│   │
│  └─────────────────┘   └──────────────────┘   └─────────────┘   │
│                                                                 │
│  ┌─────────────────┐   ┌──────────────────┐   ┌─────────────┐   │
│  │  Message Broker │   │  Distributed     │   │  Observ-    │   │
│  │  Apache Kafka   │   │  Cache           │   │  ability    │   │
│  │  + Zookeeper    │   │  Redis           │   │ Prometheus  │   │
│  └─────────────────┘   └──────────────────┘   │ + Grafana   │   │
│                                               └─────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Databases: 1 PostgreSQL cluster, separate schemas      │    │
│  │  (or separate instances per service for full isolation) │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### Redis Usage

| Service         | Cache Key Pattern      | TTL                  |
|-----------------|------------------------|----------------------|
| Title Service   | `title:{id}`           | 10 min               |
| People Service  | `person:{id}`          | 10 min               |
| Ratings Service | `rating:avg:{titleId}` | 5 min                |
| Auth Service    | `token:revoked:{jti}`  | matches token expiry |

### Gateway Responsibilities

- Forwards `X-User-Id` and `X-User-Roles` headers to downstream services
- Request/response logging
- Circuit breaking (Resilience4j)

### Data Migration Order

When migrating from the monolith:

1. User Service (no dependencies)
2. Auth Service (depends on User)
3. Title Service (no hard dependencies)
4. People Service (references Title UUIDs)
5. Ratings & Reviews Service (references User + Title UUIDs)
6. Lists & Watchlist Service (references User + Title UUIDs)
7. Media Service (references Title + Person UUIDs)
8. Awards Service (references Title + Person UUIDs)
9. Contribution Service (references Title + User UUIDs)
10. News Service (references User + Title + Person UUIDs)
11. Audit Service (last — consumes events from all others)

---------------------------------------------------------------------------