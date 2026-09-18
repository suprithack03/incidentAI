# Elevated 5xx Errors Runbook

## Purpose

Use this runbook when a service produces an unusually high number of HTTP 5xx errors within a short period.

## Symptoms

- Repeated HTTP 5xx responses.
- Multiple server-side errors occur within a short time window.
- Users receive failed requests.
- The affected service becomes degraded or unavailable.

## Investigation

1. Check the affected service logs for the corresponding error messages.
2. Identify the endpoint or operation producing the errors.
3. Check whether the errors are caused by database failures.
4. Check whether Redis or another dependency is failing.
5. Check whether a downstream service is unavailable.
6. Check recent configuration or application changes.
7. Check whether the errors are isolated to one service or affect multiple services.
8. Compare the current error rate with normal service behavior.

## Possible Causes

- Database failure.
- Redis failure.
- Downstream service failure.
- Application exception.
- Invalid configuration.
- Resource exhaustion.
- Recent application or configuration change.

## Remediation

1. Identify the underlying dependency or application failure.
2. Restore failed dependencies when applicable.
3. Correct invalid configuration.
4. Resolve application exceptions.
5. Roll back a recent change when the change is confirmed to be responsible.
6. Restart the affected service only when necessary after addressing the underlying problem.

## Verification

After remediation:

- Confirm 5xx errors return to normal levels.
- Confirm affected requests succeed.
- Confirm the service returns to a healthy state.
- Monitor the service for recurring error spikes.