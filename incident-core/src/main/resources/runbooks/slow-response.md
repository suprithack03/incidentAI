# Slow Response Runbook

## Purpose

Use this runbook when a service is responding more slowly than the expected response-time threshold.

## Symptoms

- Response times exceed the configured threshold.
- Multiple consecutive slow responses are observed.
- Users experience increased request latency.
- Service requests take significantly longer than normal.

## Investigation

1. Check the affected service logs for slow requests.
2. Identify whether the slow responses affect a specific endpoint or multiple endpoints.
3. Check database query execution time.
4. Check database connection pool usage.
5. Check CPU and memory usage of the affected service.
6. Check whether Redis or another dependency is responding slowly.
7. Check whether another downstream service is causing increased latency.
8. Compare the current response time with normal service behavior.

## Possible Causes

- Slow database queries.
- Database connection pool exhaustion.
- High CPU or memory usage.
- Slow Redis operations.
- Slow downstream service.
- Increased request load.
- Resource contention.

## Remediation

1. Identify and optimize slow database queries when applicable.
2. Resolve database connection pool exhaustion.
3. Resolve resource constraints on the affected service.
4. Restore availability or performance of slow dependencies.
5. Reduce unnecessary processing in slow request paths.
6. Restart the service only when necessary after the underlying problem is addressed.

## Verification

After remediation:

- Confirm response times return below the configured threshold.
- Confirm consecutive slow-response detections stop.
- Confirm dependent services are responding normally.
- Monitor the service for recurring latency increases.