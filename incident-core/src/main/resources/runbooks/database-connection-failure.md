# Database Connection Failure Runbook

## Purpose

Use this runbook when a service cannot connect to its PostgreSQL database or database connections are being refused.

## Symptoms

- Database connection refused errors
- Database authentication failures
- Connection timeout errors
- Repeated database connection failures in service logs
- Service becomes unavailable because database access is lost

## Investigation

1. Check the affected service logs and identify the database error message.
2. Verify that the PostgreSQL database server is running.
3. Verify the database host and port configured for the service.
4. Verify the database name and database user configuration.
5. Check whether database authentication is failing.
6. Check whether the database is reachable from the affected service.
7. Check database connection pool usage and determine whether the pool is exhausted.
8. Check whether multiple services are experiencing database connection failures.

## Possible Causes

- PostgreSQL server is unavailable.
- Incorrect database host or port.
- Incorrect database credentials.
- Network connectivity problem.
- Database connection pool exhaustion.
- Database resource exhaustion.

## Remediation

1. Restore PostgreSQL availability if the database server is down.
2. Correct invalid database connection configuration.
3. Correct authentication configuration when credentials are invalid.
4. Resolve connection pool exhaustion before restarting the affected service.
5. Restart the affected service only after database connectivity has been restored.
6. Verify that new database connections can be established.

## Verification

After remediation:

- Confirm the service can connect to PostgreSQL.
- Confirm database connection errors stop appearing in logs.
- Confirm the service returns to a healthy state.
- Monitor for repeated database failures.