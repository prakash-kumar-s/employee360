import { useCallback, useEffect, useState } from "react";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function Approvals() {

    const { user } = useAuth();

    const [approvals, setApprovals] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [rejectingId, setRejectingId] =
        useState(null);

    const [rejectionReason, setRejectionReason] =
        useState("");

    const fetchApprovals = useCallback(() => {
        return api.get(`/approvals/manager/${user.id}`);
    }, [user]);

    const loadApprovals = useCallback(async () => {
        try {
            const response = await fetchApprovals();
            setApprovals(response.data);
        } catch (error) {
            console.error(error);
            setError("Unable to load pending approvals.");
        }
    }, [fetchApprovals]);

    useEffect(() => {
        if (!user?.id) return undefined;

        let active = true;
        fetchApprovals()
            .then((response) => {
                if (active) setApprovals(response.data);
            })
            .catch((error) => {
                console.error(error);
                if (active) setError("Unable to load pending approvals.");
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => {
            active = false;
        };
    }, [fetchApprovals, user?.id]);

    const handleApprove = async (requestId) => {

        try {

            setError("");
            setMessage("");

            await api.post(
                `/approvals/${requestId}/approve`
            );

            setMessage(
                "Leave request approved successfully."
            );

            await loadApprovals();

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to approve leave request."
            );
        }
    };

    const handleReject = async (requestId) => {

        if (!rejectionReason.trim()) {
            setError(
                "Please enter a rejection reason."
            );
            return;
        }

        try {

            setError("");
            setMessage("");

            await api.post(
                `/approvals/${requestId}/reject`,
                {
                    rejectionReason:
                        rejectionReason.trim(),
                }
            );

            setMessage(
                "Leave request rejected successfully."
            );

            setRejectingId(null);
            setRejectionReason("");

            await loadApprovals();

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to reject leave request."
            );
        }
    };

    return (
        <div>

            <div className="page-heading">

                <div>
                    <h2>Pending Approvals</h2>

                    <p>
                        Leave requests waiting for your action.
                    </p>
                </div>

            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {message && (
                <div className="success-message">
                    {message}
                </div>
            )}

            <section className="card">

                {loading ? (
                    <p>Loading approvals...</p>
                ) : approvals.length === 0 ? (
                    <p className="empty">
                        No pending approvals.
                    </p>
                ) : (

                    <div className="approval-list">

                        {approvals.map((approval) => (

                            <div
                                className="approval-card"
                                key={approval.approvalStepId}
                            >

                                <div className="approval-header">

                                    <div>
                                        <h3>
                                            {approval.employeeName}
                                        </h3>

                                        <p>
                                            {approval.leaveTypeName}
                                        </p>
                                    </div>

                                    <span className="status status-pending">
                                        PENDING
                                    </span>

                                </div>

                                <div className="approval-details">

                                    <p>
                                        <strong>
                                            Dates:
                                        </strong>{" "}
                                        {approval.startDate}
                                        {" → "}
                                        {approval.endDate}
                                    </p>

                                    <p>
                                        <strong>
                                            Reason:
                                        </strong>{" "}
                                        {approval.reason || "-"}
                                    </p>

                                    <p>
                                        <strong>
                                            Approval Level:
                                        </strong>{" "}
                                        {approval.stepOrder}
                                    </p>

                                    <p>
                                        <strong>
                                            Required Role:
                                        </strong>{" "}
                                        {approval.approverRole}
                                    </p>

                                </div>

                                {rejectingId ===
                                    approval.requestId ? (

                                    <div className="reject-box">

                                        <textarea
                                            value={
                                                rejectionReason
                                            }
                                            onChange={(event) =>
                                                setRejectionReason(
                                                    event.target.value
                                                )
                                            }
                                            placeholder="Enter rejection reason"
                                            rows="3"
                                        />

                                        <div className="button-row">

                                            <button
                                                className="danger-button"
                                                onClick={() =>
                                                    handleReject(
                                                        approval.requestId
                                                    )
                                                }
                                            >
                                                Confirm Rejection
                                            </button>

                                            <button
                                                className="secondary-button"
                                                onClick={() => {
                                                    setRejectingId(
                                                        null
                                                    );
                                                    setRejectionReason(
                                                        ""
                                                    );
                                                }}
                                            >
                                                Cancel
                                            </button>

                                        </div>

                                    </div>

                                ) : (

                                    <div className="button-row">

                                        <button
                                            className="success-button"
                                            onClick={() =>
                                                handleApprove(
                                                    approval.requestId
                                                )
                                            }
                                        >
                                            Approve
                                        </button>

                                        <button
                                            className="danger-button"
                                            onClick={() =>
                                                setRejectingId(
                                                    approval.requestId
                                                )
                                            }
                                        >
                                            Reject
                                        </button>

                                    </div>

                                )}

                            </div>

                        ))}

                    </div>

                )}

            </section>

        </div>
    );
}

export default Approvals;