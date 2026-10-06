import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function LeaveHistory() {

    const { user } = useAuth();

    const [leaves, setLeaves] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const loadHistory = async () => {

            try {

                const response =
                    await api.get(
                        `/leaves/user/${user.id}`
                    );

                setLeaves(response.data);

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load leave history."
                );

            } finally {
                setLoading(false);
            }
        };

        loadHistory();

    }, [user]);

    return (
        <div>

            <div className="page-heading">

                <div>
                    <h2>Leave History</h2>
                    <p>
                        View your submitted leave requests.
                    </p>
                </div>

                <Link
                    className="primary-link"
                    to="/apply-leave"
                >
                    Apply Leave
                </Link>

            </div>

            <section className="card">

                {loading && (
                    <p>Loading leave history...</p>
                )}

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {!loading &&
                    !error &&
                    leaves.length === 0 && (
                        <p className="empty">
                            No leave requests found.
                        </p>
                    )}

                {!loading &&
                    leaves.length > 0 && (

                        <div className="table-container">

                            <table>

                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Leave Type</th>
                                        <th>Start Date</th>
                                        <th>End Date</th>
                                        <th>Reason</th>
                                        <th>Status</th>
                                        <th>Rejection details</th>
                                        <th>Details</th>
                                    </tr>
                                </thead>

                                <tbody>

                                    {leaves.map((leave) => (
                                        <tr key={leave.id}>

                                            <td>
                                                #{leave.id}
                                            </td>

                                            <td>
                                                {leave.leaveTypeName}
                                            </td>

                                            <td>
                                                {leave.startDate}
                                            </td>

                                            <td>
                                                {leave.endDate}
                                            </td>

                                            <td>
                                                {leave.reason || "-"}
                                            </td>

                                            <td>
                                                <span
                                                    className={`status status-${leave.status.toLowerCase()}`}
                                                >
                                                    {leave.status}
                                                </span>
                                            </td>

                                            <td>
                                                {leave.status === "REJECTED" ? (
                                                    <>
                                                        <div>Rejected by: {leave.rejectedByName || "Unknown"}</div>
                                                        <div>Reason: {leave.rejectionReason || "No reason provided."}</div>
                                                    </>
                                                ) : "-"}
                                            </td>

                                            <td>
                                                <Link
                                                    to={`/approvals/${leave.id}`}
                                                >
                                                    View
                                                </Link>
                                            </td>

                                        </tr>
                                    ))}

                                </tbody>

                            </table>

                        </div>

                    )}

            </section>

        </div>
    );
}

export default LeaveHistory;