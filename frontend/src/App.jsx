import { useEffect, useRef, useState } from "react";
import Login from "./Login";
import "./App.css";

const getAuthHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem("incidentai_token")}`,
});

function App() {
  const [loggedIn, setLoggedIn] = useState(
    Boolean(localStorage.getItem("incidentai_token"))
  );

  const [incidents, setIncidents] = useState([]);
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [servicesLoading, setServicesLoading] = useState(true);
  const [error, setError] = useState("");
  const [servicesError, setServicesError] = useState("");

  const [connectionTest, setConnectionTest] = useState("");
  const [testingConnection, setTestingConnection] = useState(false);

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [severityFilter, setSeverityFilter] = useState("ALL");
  const [serviceFilter, setServiceFilter] = useState("ALL");
  const [incidentView, setIncidentView] = useState("ALL");

  const [selectedIncident, setSelectedIncident] =
    useState(null);

  const [selectedIncidentAnomalies, setSelectedIncidentAnomalies] =
    useState([]);

  const [anomaliesLoading, setAnomaliesLoading] =
    useState(false);

  const [anomaliesError, setAnomaliesError] =
    useState("");

  const [statusUpdating, setStatusUpdating] =
    useState(false);

  const [statusUpdateError, setStatusUpdateError] =
    useState("");

  const incidentDetailsRef = useRef(null);

  const handleLogin = (loginData) => {
    localStorage.setItem(
      "incidentai_token",
      loginData.token
    );

    setLoggedIn(true);
  };

  const testIncidentCoreConnection = async () => {
    setTestingConnection(true);
    setConnectionTest("");

    const token = localStorage.getItem(
      "incidentai_token"
    );

    if (!token) {
      setConnectionTest(
        "❌ No JWT token found in local storage."
      );

      setTestingConnection(false);
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8084/api/services",
        {
          headers: getAuthHeaders(),
        }
      );

      if (response.ok) {
        setConnectionTest(
          `✅ Incident Core reachable — HTTP ${response.status}`
        );
      } else {
        setConnectionTest(
          `⚠️ Incident Core reachable, but returned HTTP ${response.status}`
        );
      }
    } catch (err) {
      setConnectionTest(
        `❌ Connection failed: ${err.message}`
      );
    }

    setTestingConnection(false);
  };

  const loadIncidents = (view = incidentView) => {
    setLoading(true);
    setError("");

    const endpoint =
      view === "ACTIVE"
        ? "http://localhost:8084/api/incidents/active"
        : "http://localhost:8084/api/incidents";

    fetch(endpoint, {
      headers: getAuthHeaders(),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `HTTP ${response.status}`
          );
        }

        return response.json();
      })
      .then((data) => {
        setIncidents(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(
          "Incident Core request error:",
          err
        );

        setError(
          `Unable to load incidents from Incident Core. ${err.message}`
        );

        setLoading(false);
      });
  };

  const loadServices = () => {
    setServicesLoading(true);
    setServicesError("");

    fetch(
      "http://localhost:8084/api/services",
      {
        headers: getAuthHeaders(),
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `HTTP ${response.status}`
          );
        }

        return response.json();
      })
      .then((data) => {
        setServices(data);
        setServicesLoading(false);
      })
      .catch((err) => {
        console.error(
          "Incident Core services request error:",
          err
        );

        setServicesError(
          `Service health request failed: ${err.message}`
        );

        setServicesLoading(false);
      });
  };

  useEffect(() => {
    if (!loggedIn) {
      return;
    }

    loadIncidents(incidentView);

    const refreshInterval =
      setInterval(() => {
        loadIncidents(incidentView);
      }, 30000);

    return () => {
      clearInterval(refreshInterval);
    };
  }, [loggedIn, incidentView]);

  useEffect(() => {
    if (!loggedIn) {
      return;
    }

    loadServices();

    const serviceRefreshInterval =
      setInterval(() => {
        loadServices();
      }, 30000);

    return () => {
      clearInterval(
        serviceRefreshInterval
      );
    };
  }, [loggedIn]);

  const clearIncidentDetails = () => {
    setSelectedIncident(null);
    setSelectedIncidentAnomalies([]);
    setAnomaliesError("");
    setStatusUpdateError("");
  };

  const changeIncidentView = (view) => {
    clearIncidentDetails();
    setIncidentView(view);
    loadIncidents(view);
  };

  const clearFilters = () => {
    clearIncidentDetails();

    setStatusFilter("ALL");
    setSeverityFilter("ALL");
    setServiceFilter("ALL");
    setIncidentView("ALL");

    loadIncidents("ALL");
  };

  const loadIncidentAnomalies = (
    incidentId
  ) => {
    setAnomaliesLoading(true);
    setAnomaliesError("");
    setSelectedIncidentAnomalies([]);

    fetch(
      `http://localhost:8084/api/incidents/${incidentId}/anomalies`,
      {
        headers: getAuthHeaders(),
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `HTTP ${response.status}`
          );
        }

        return response.json();
      })
      .then((data) => {
        setSelectedIncidentAnomalies(data);
        setAnomaliesLoading(false);
      })
      .catch((err) => {
        console.error(
          "Incident anomalies request error:",
          err
        );

        setAnomaliesError(
          `Unable to load anomalies. ${err.message}`
        );

        setAnomaliesLoading(false);
      });
  };

  const updateIncidentStatus = (
    newStatus
  ) => {
    if (!selectedIncident) {
      return;
    }

    setStatusUpdating(true);
    setStatusUpdateError("");

    fetch(
      `http://localhost:8084/api/incidents/${selectedIncident.id}/status?status=${newStatus}`,
      {
        method: "PUT",
        headers: getAuthHeaders(),
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `HTTP ${response.status}`
          );
        }

        return response.json();
      })
      .then((updatedIncident) => {
        setSelectedIncident(
          updatedIncident
        );

        setIncidents(
          (currentIncidents) =>
            currentIncidents.map(
              (incident) =>
                incident.id ===
                updatedIncident.id
                  ? updatedIncident
                  : incident
            )
        );

        setStatusUpdating(false);
      })
      .catch((err) => {
        console.error(
          "Incident status update error:",
          err
        );

        setStatusUpdateError(
          `Unable to update incident status. ${err.message}`
        );

        setStatusUpdating(false);
      });
  };

  const criticalCount =
    incidents.filter(
      (incident) =>
        incident.severity === "CRITICAL"
    ).length;

  const highCount =
    incidents.filter(
      (incident) =>
        incident.severity === "HIGH"
    ).length;

  const mediumCount =
    incidents.filter(
      (incident) =>
        incident.severity === "MEDIUM"
    ).length;

  const lowCount =
    incidents.filter(
      (incident) =>
        incident.severity === "LOW"
    ).length;

  const userServiceCount =
    incidents.filter((incident) =>
      incident.title.includes(
        "user-service"
      )
    ).length;

  const paymentServiceCount =
    incidents.filter((incident) =>
      incident.title.includes(
        "payment-service"
      )
    ).length;

  const inventoryServiceCount =
    incidents.filter((incident) =>
      incident.title.includes(
        "inventory-service"
      )
    ).length;

  const filteredIncidents =
    incidents.filter((incident) => {
      const serviceName =
        incident.title.replace(
          "Incident in ",
          ""
        );

      const matchesStatus =
        statusFilter === "ALL" ||
        incident.status ===
          statusFilter;

      const matchesSeverity =
        severityFilter === "ALL" ||
        incident.severity ===
          severityFilter;

      const matchesService =
        serviceFilter === "ALL" ||
        serviceName === serviceFilter;

      return (
        matchesStatus &&
        matchesSeverity &&
        matchesService
      );
    });

  const selectSeverity = (severity) => {
    clearIncidentDetails();
    setSeverityFilter(severity);
  };

  const selectIncident = (
    incident
  ) => {
    setSelectedIncident(incident);
    setStatusUpdateError("");
    loadIncidentAnomalies(
      incident.id
    );

    setTimeout(() => {
      incidentDetailsRef.current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }, 0);
  };

  const closeIncidentDetails = () => {
    clearIncidentDetails();
  };

  if (!loggedIn) {
    return (
      <Login onLogin={handleLogin} />
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>IncidentAI</h1>

        <p>
          Incident monitoring dashboard
        </p>

        <span className="auto-refresh-indicator">
          Auto-refresh enabled · Updates
          every 30 seconds
        </span>
      </header>

      <section className="summary-section">
        <h2>Incident Summary</h2>

        <button
          onClick={
            testIncidentCoreConnection
          }
          disabled={testingConnection}
          style={{
            marginBottom: "20px",
            padding: "10px 16px",
            cursor: testingConnection
              ? "not-allowed"
              : "pointer",
          }}
        >
          {testingConnection
            ? "Testing..."
            : "Test Incident Core Connection"}
        </button>

        {connectionTest && (
          <p
            style={{
              marginBottom: "20px",
              fontWeight: "bold",
            }}
          >
            {connectionTest}
          </p>
        )}

        <div className="summary-grid">
          <div className="summary-card">
            <h3>Total Incidents</h3>
            <p>
              {incidents.length}
            </p>
          </div>

          <button
            className={`summary-card severity-card ${
              severityFilter ===
              "CRITICAL"
                ? "selected"
                : ""
            }`}
            onClick={() =>
              selectSeverity(
                "CRITICAL"
              )
            }
          >
            <h3>Critical</h3>
            <p>
              {criticalCount}
            </p>
          </button>

          <button
            className={`summary-card severity-card ${
              severityFilter === "HIGH"
                ? "selected"
                : ""
            }`}
            onClick={() =>
              selectSeverity("HIGH")
            }
          >
            <h3>High</h3>
            <p>{highCount}</p>
          </button>

          <button
            className={`summary-card severity-card ${
              severityFilter ===
              "MEDIUM"
                ? "selected"
                : ""
            }`}
            onClick={() =>
              selectSeverity("MEDIUM")
            }
          >
            <h3>Medium</h3>
            <p>
              {mediumCount}
            </p>
          </button>

          <button
            className={`summary-card severity-card ${
              severityFilter === "LOW"
                ? "selected"
                : ""
            }`}
            onClick={() =>
              selectSeverity("LOW")
            }
          >
            <h3>Low</h3>
            <p>{lowCount}</p>
          </button>
        </div>

        <div className="service-summary">
          <h3>
            Incidents by Service
          </h3>

          <div className="service-summary-grid">
            <div className="service-summary-card">
              <strong>
                User Service
              </strong>

              <span>
                {userServiceCount}
              </span>
            </div>

            <div className="service-summary-card">
              <strong>
                Payment Service
              </strong>

              <span>
                {paymentServiceCount}
              </span>
            </div>

            <div className="service-summary-card">
              <strong>
                Inventory Service
              </strong>

              <span>
                {inventoryServiceCount}
              </span>
            </div>
          </div>
        </div>
      </section>

      <section className="service-health-section">
        <h2>Service Health</h2>

        {servicesLoading && (
          <p>
            Loading service health...
          </p>
        )}

        {servicesError && (
          <p>{servicesError}</p>
        )}

        {!servicesLoading &&
          !servicesError && (
            <div className="service-health-grid">
              {services.map(
                (service) => (
                  <div
                    className="service-health-card"
                    key={service.id}
                  >
                    <div>
                      <strong>
                        {service.name}
                      </strong>
                    </div>

                    <span
                      className={`service-status service-status-${service.status.toLowerCase()}`}
                    >
                      {
                        service.status
                      }
                    </span>
                  </div>
                )
              )}
            </div>
          )}
      </section>

      <section className="incidents-section">
        <h2>
          Recent Incidents
        </h2>

        <div className="incident-controls">
          <button
            onClick={() => {
              clearIncidentDetails();
              loadIncidents();
            }}
            disabled={loading}
          >
            {loading
              ? "Refreshing..."
              : "Refresh Incidents"}
          </button>

          <button
            className={
              incidentView ===
              "ALL"
                ? "view-button selected"
                : "view-button"
            }
            onClick={() =>
              changeIncidentView(
                "ALL"
              )
            }
          >
            All Incidents
          </button>

          <button
            className={
              incidentView ===
              "ACTIVE"
                ? "view-button selected"
                : "view-button"
            }
            onClick={() =>
              changeIncidentView(
                "ACTIVE"
              )
            }
          >
            Active Incidents
          </button>

          <button
            onClick={
              clearFilters
            }
          >
            Clear Filters
          </button>

          <select
            value={statusFilter}
            onChange={(event) => {
              clearIncidentDetails();
              setStatusFilter(
                event.target.value
              );
            }}
          >
            <option value="ALL">
              All Statuses
            </option>

            <option value="OPEN">
              Open
            </option>

            <option value="INVESTIGATING">
              Investigating
            </option>

            <option value="RESOLVED">
              Resolved
            </option>
          </select>

          <select
            value={
              severityFilter
            }
            onChange={(event) => {
              clearIncidentDetails();
              setSeverityFilter(
                event.target.value
              );
            }}
          >
            <option value="ALL">
              All Severities
            </option>

            <option value="CRITICAL">
              Critical
            </option>

            <option value="HIGH">
              High
            </option>

            <option value="MEDIUM">
              Medium
            </option>

            <option value="LOW">
              Low
            </option>
          </select>

          <select
            value={
              serviceFilter
            }
            onChange={(event) => {
              clearIncidentDetails();
              setServiceFilter(
                event.target.value
              );
            }}
          >
            <option value="ALL">
              All Services
            </option>

            <option value="user-service">
              User Service
            </option>

            <option value="payment-service">
              Payment Service
            </option>

            <option value="inventory-service">
              Inventory Service
            </option>
          </select>
        </div>

        <p className="filter-count">
          Showing{" "}
          {
            filteredIncidents.length
          }{" "}
          of{" "}
          {incidents.length}{" "}
          incidents
        </p>

        {error && (
          <p>{error}</p>
        )}

        {!error && (
          <div className="incident-list">
            {filteredIncidents.map(
              (incident) => (
                <button
                  className="incident-card"
                  key={incident.id}
                  onClick={() =>
                    selectIncident(
                      incident
                    )
                  }
                >
                  <strong>
                    Incident{" "}
                    {incident.id}
                  </strong>

                  <span>
                    {incident.title.replace(
                      "Incident in ",
                      ""
                    )}
                  </span>

                  <span>
                    {new Date(
                      incident.createdAt
                    ).toLocaleString()}
                  </span>

                  <span
                    className={`severity ${incident.severity.toLowerCase()}`}
                  >
                    {
                      incident.severity
                    }
                  </span>

                  <span
                    className={`incident-status status-${incident.status.toLowerCase()}`}
                  >
                    {
                      incident.status
                    }
                  </span>
                </button>
              )
            )}

            {filteredIncidents.length ===
              0 && (
              <p>
                No incidents found
                for the selected
                filters.
              </p>
            )}
          </div>
        )}

        {selectedIncident && (
          <div
            className="selected-incident"
            ref={incidentDetailsRef}
          >
            <h2>
              Incident Details
            </h2>

            <div className="selected-incident-details">
              <div>
                <strong>
                  Incident ID
                </strong>

                <span>
                  {
                    selectedIncident.id
                  }
                </span>
              </div>

              <div>
                <strong>
                  Service
                </strong>

                <span>
                  {selectedIncident.title.replace(
                    "Incident in ",
                    ""
                  )}
                </span>
              </div>

              <div>
                <strong>
                  Severity
                </strong>

                <span
                  className={`severity ${selectedIncident.severity.toLowerCase()}`}
                >
                  {
                    selectedIncident.severity
                  }
                </span>
              </div>

              <div>
                <strong>
                  Status
                </strong>

                <select
                  value={
                    selectedIncident.status
                  }
                  onChange={(
                    event
                  ) =>
                    updateIncidentStatus(
                      event.target
                        .value
                    )
                  }
                  disabled={
                    statusUpdating
                  }
                >
                  <option value="OPEN">
                    Open
                  </option>

                  <option value="INVESTIGATING">
                    Investigating
                  </option>

                  <option value="RESOLVED">
                    Resolved
                  </option>
                </select>
              </div>

              <div>
                <strong>
                  Created At
                </strong>

                <span>
                  {new Date(
                    selectedIncident.createdAt
                  ).toLocaleString()}
                </span>
              </div>
            </div>

            {statusUpdating && (
              <p>
                Updating incident
                status...
              </p>
            )}

            {statusUpdateError && (
              <p>
                {
                  statusUpdateError
                }
              </p>
            )}

            <div className="anomalies-section">
              <div className="anomalies-header">
                <h3>
                  Incident
                  Anomalies
                </h3>

                {!anomaliesLoading &&
                  !anomaliesError && (
                    <span className="anomaly-count">
                      {
                        selectedIncidentAnomalies.length
                      }{" "}
                      {
                        selectedIncidentAnomalies.length ===
                        1
                          ? "anomaly"
                          : "anomalies"
                      }
                    </span>
                  )}
              </div>

              {anomaliesLoading && (
                <p>
                  Loading incident
                  anomalies...
                </p>
              )}

              {anomaliesError && (
                <p>
                  {
                    anomaliesError
                  }
                </p>
              )}

              {!anomaliesLoading &&
                !anomaliesError &&
                selectedIncidentAnomalies.length ===
                  0 && (
                  <p>
                    No anomalies
                    found for
                    this incident.
                  </p>
                )}

              {!anomaliesLoading &&
                !anomaliesError &&
                selectedIncidentAnomalies.length >
                  0 && (
                  <div className="anomaly-list">
                    {selectedIncidentAnomalies.map(
                      (
                        incidentAnomaly
                      ) => {
                        const anomaly =
                          incidentAnomaly.anomaly;

                        return (
                          <div
                            className="anomaly-card"
                            key={
                              incidentAnomaly.id
                            }
                          >
                            <div>
                              <strong>
                                Anomaly ID
                              </strong>

                              <span>
                                {
                                  anomaly.id
                                }
                              </span>
                            </div>

                            <div>
                              <strong>
                                Type
                              </strong>

                              <span>
                                {
                                  anomaly.type
                                }
                              </span>
                            </div>

                            <div>
                              <strong>
                                Service
                              </strong>

                              <span>
                                {
                                  anomaly
                                    .service
                                    .name
                                }
                              </span>
                            </div>

                            <div>
                              <strong>
                                Detected At
                              </strong>

                              <span>
                                {new Date(
                                  anomaly.detectedAt
                                ).toLocaleString()}
                              </span>
                            </div>

                            <div>
                              <strong>
                                Metric Value
                              </strong>

                              <span>
                                {
                                  anomaly.metricValue
                                }
                              </span>
                            </div>

                            <div>
                              <strong>
                                Threshold
                              </strong>

                              <span>
                                {
                                  anomaly.thresholdUsed
                                }
                              </span>
                            </div>
                          </div>
                        );
                      }
                    )}
                  </div>
                )}
            </div>

            <button
              onClick={
                closeIncidentDetails
              }
            >
              Close Details
            </button>
          </div>
        )}
      </section>
    </div>
  );
}

export default App;