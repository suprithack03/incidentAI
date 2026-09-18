# Redis Failure Runbook

## Purpose

Use this runbook when a service cannot connect to Redis or Redis operations are failing.

## Symptoms

- Redis connection refused errors
- Redis connection timeout errors
- Redis unavailable errors
- Cache operations fail repeatedly
- Increased database load because cache requests cannot be served

## Investigation

1. Check the affected service logs for Redis error messages.
2. Verify that the Redis server is running.
3. Verify the Redis host and port configured for the service.
4. Check whether the affected service can reach Redis.
5. Check whether multiple services are experiencing Redis failures.
6. Check Redis resource usage and connection limits.
7. Determine whether the failure affects only cache operations or the service's primary database operations as well.

## Possible Causes

- Redis server is unavailable.
- Incorrect Redis host or port.
- Network connectivity problem.
- Redis resource exhaustion.
- Redis connection limit reached.
- Incorrect Redis configuration.

## Remediation

1. Restore Redis availability if the Redis server is down.
2. Correct invalid Redis connection configuration.
3. Resolve network connectivity problems.
4. Resolve Redis resource or connection-limit problems.
5. Restart the affected service only when necessary after Redis connectivity is restored.
6. Verify that the service can perform Redis operations successfully.

## Verification

After remediation:

- Confirm Redis connections succeed.
- Confirm Redis errors stop appearing in service logs.
- Confirm cache operations work normally.
- Confirm the affected service returns to a healthy state.
- Monitor for repeated Redis failures.