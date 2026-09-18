import { useEffect, useState } from "react";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function AuditLog() {

    const { user } = useAuth();

    const [leaves, setLeaves] = useState([]);
    const [selectedRequest, setSelectedRequest] =
        useState("");

    const [auditLogs, setAuditLogs] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [loadingAudit, setLoadingAudit] =
        useState(false);

    const [error, setError] =
        useState("");

    useEffect(() => {

        const loadLeaves = async () => {

            try {

                const response =
                    await api.get(
                        "/audit/requests"
                    );

                setLeaves(response.data);

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load leave requests."
                );

            } finally {
                setLoading(false);
            }
        };

        loadLeaves();

    }, [user]);

    const loadAudit = async (requestId) => {

        if (!requestId) {
            setAuditLogs([]);
            return;
        }

        try {

            setLoadingAudit(true);
            setError("");

            const response =
                await api.get(
                    `/audit/request/${requestId}`
                );

            setAuditLogs(response.data);

        } catch (error) {

            console.error(error);

            setError(
                "Unable to load audit information."
            );

        } finally {
            setLoadingAudit(false);
        }
    };

    const handleSelect = (event) => {

        const requestId =
            event.target.value;

        setSelectedRequest(requestId);

        loadAudit(requestId);
    };

    return (
        <div>

            <div className="page-heading">

                <div>
                    <h2>Audit Log</h2>
                    <p>
                        View the approval history of leave requests.
                    </p>
                </div>

            </div>

            <section className="card">

                {loading ? (
                    <p>Loading requests...</p>
                ) : (

                    <div className="form-group">

                        <label>
                            Select Leave Request
                        </label>

                        <select
                            value={selectedRequest}
                            onChange={handleSelect}
                        >
                            <option value="">
                                Select request
                            </option>

                            {leaves.map((leave) => (
                                <option
                                    key={leave.id}
                                    value={leave.id}
                                >
                                    Request #{leave.id}
                                    {leave.userName ? ` (${leave.userName})` : ""}
                                    {" - "}
                                    {leave.leaveTypeName}
                                    {" - "}
                                    {leave.status}
                                </option>
                            ))}

                        </select>

                    </div>

                )}

            </section>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {selectedRequest && (
                <section className="card">

                    <div className="card-header">
                        <h3>
                            Audit History
                        </h3>
                    </div>

                    {loadingAudit ? (
                        <p>
                            Loading audit history...
                        </p>
                    ) : auditLogs.length === 0 ? (
                        <p className="empty">
                            No audit records found.
                        </p>
                    ) : (

                        <div className="table-container">

                            <table>

                                <thead>
                                    <tr>
                                        <th>Action</th>
                                        <th>User</th>
                                        <th>Previous Status</th>
                                        <th>New Status</th>
                                        <th>Timestamp</th>
                                    </tr>
                                </thead>

                                <tbody>

                                    {auditLogs.map(
                                        (log) => (
                                            <tr key={log.id}>

                                                <td>
                                                    {log.action}
                                                </td>

                                                <td>
                                                    {log.userName}
                                                </td>

                                                <td>
                                                    {log.previousStatus}
                                                </td>

                                                <td>
                                                    {log.newStatus}
                                                </td>

                                                <td>
                                                    {new Date(
                                                        log.timestamp
                                                    ).toLocaleString()}
                                                </td>

                                            </tr>
                                        )
                                    )}

                                </tbody>

                            </table>

                        </div>

                    )}

                </section>
            )}

        </div>
    );
}

export default AuditLog;