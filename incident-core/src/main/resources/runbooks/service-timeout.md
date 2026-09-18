# Service Timeout Runbook

## Purpose

Use this runbook when requests to a service or one of its dependencies do not complete within the expected time.

## Symptoms

- Request timeout errors.
- Requests remain pending for longer than the configured timeout.
- Multiple timeout errors appear in service logs.
- Users experience failed or delayed requests.

## Investigation

1. Check the affected service logs for timeout errors.
2. Identify the request or dependency that timed out.
3. Check whether the database is responding normally.
4. Check whether Redis is responding normally.
5. Check whether a downstream service is responding normally.
6. Check response-time measurements for the affected dependency.
7. Check CPU, memory, and connection-pool usage.
8. Determine whether timeouts affect one service or multiple services.

## Possible Causes

- Slow database operation.
- Database connection pool exhaustion.
- Redis unavailability or slow response.
- Downstream service failure.
- Network connectivity problems.
- High service resource usage.
- Excessive request load.

## Remediation

1. Restore availability of the affected dependency.
2. Resolve slow database or Redis operations.
3. Resolve connection-pool exhaustion.
4. Resolve downstream service failures.
5. Resolve network connectivity problems.
6. Address resource exhaustion or excessive request load.
7. Restart the affected service only when necessary after the underlying problem is addressed.

## Verification

After remediation:

- Confirm requests complete within the expected timeout.
- Confirm timeout errors stop appearing in logs.
- Confirm dependent services respond normally.
- Confirm the affected service returns to a healthy state.
- Monitor for recurring timeout errors.