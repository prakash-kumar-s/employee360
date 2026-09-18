import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function ApplyLeave() {

    const { user } = useAuth();
    const navigate = useNavigate();

    const [leaveTypes, setLeaveTypes] = useState([]);

    const [leaveTypeId, setLeaveTypeId] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [reason, setReason] = useState("");

    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {

        let mounted = true;

        const loadLeaveTypes = async () => {

            try {

                setLoading(true);
                setError("");

                const response =
                    await api.get("/leaves/types");

                if (mounted) {
                    setLeaveTypes(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );
                }

            } catch (error) {

                console.error(
                    "Unable to load leave types:",
                    error
                );

                if (mounted) {

                    setError(
                        error.response?.data?.message ||
                        "Unable to load leave types."
                    );

                    setLeaveTypes([]);
                }

            } finally {

                if (mounted) {
                    setLoading(false);
                }
            }
        };

        loadLeaveTypes();

        return () => {
            mounted = false;
        };

    }, []);

    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");
        setSuccess("");

        if (!user?.id) {
            setError(
                "User information is missing. Please login again."
            );
            return;
        }

        if (!leaveTypeId) {
            setError(
                "Please select a leave type."
            );
            return;
        }

        if (!startDate) {
            setError(
                "Please select a start date."
            );
            return;
        }

        if (!endDate) {
            setError(
                "Please select an end date."
            );
            return;
        }

        if (startDate > endDate) {
            setError(
                "Start date cannot be after end date."
            );
            return;
        }

        if (!reason.trim()) {
            setError(
                "Please enter a reason."
            );
            return;
        }

        try {

            setSubmitting(true);

            const response =
                await api.post(
                    "/leaves",
                    {
                        userId: user.id,
                        leaveTypeId: Number(leaveTypeId),
                        startDate: startDate,
                        endDate: endDate,
                        reason: reason.trim(),
                    }
                );

            setSuccess(
                `Leave request submitted successfully. Request ID: ${response.data.id}`
            );

            setLeaveTypeId("");
            setStartDate("");
            setEndDate("");
            setReason("");

        } catch (error) {

            console.error(
                "Leave application failed:",
                error
            );

            setError(
                error.response?.data?.message ||
                "Unable to submit leave request."
            );

        } finally {

            setSubmitting(false);
        }
    };

    return (
        <div className="page-container">

            <div className="page-header">

                <div>
                    <h2>Apply Leave</h2>

                    <p>
                        Submit a leave request for approval.
                    </p>
                </div>

            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {success && (
                <div className="success-message">
                    {success}
                </div>
            )}

            <div className="form-card">

                <form onSubmit={handleSubmit}>

                    <div className="form-group">

                        <label htmlFor="leaveType">
                            Leave Type
                        </label>

                        {loading ? (

                            <div className="loading-message">
                                Loading leave types...
                            </div>

                        ) : leaveTypes.length === 0 ? (

                            <div className="empty-state">
                                No leave types are currently configured.
                            </div>

                        ) : (

                            <select
                                id="leaveType"
                                value={leaveTypeId}
                                onChange={(event) =>
                                    setLeaveTypeId(
                                        event.target.value
                                    )
                                }
                            >

                                <option value="">
                                    Select leave type
                                </option>

                                {leaveTypes.map(
                                    (leaveType) => (

                                        <option
                                            key={leaveType.id}
                                            value={leaveType.id}
                                        >
                                            {leaveType.name}
                                        </option>

                                    )
                                )}

                            </select>
                        )}

                    </div>

                    <div className="form-row">

                        <div className="form-group">

                            <label htmlFor="startDate">
                                Start Date
                            </label>

                            <input
                                id="startDate"
                                type="date"
                                value={startDate}
                                onChange={(event) =>
                                    setStartDate(
                                        event.target.value
                                    )
                                }
                            />

                        </div>

                        <div className="form-group">

                            <label htmlFor="endDate">
                                End Date
                            </label>

                            <input
                                id="endDate"
                                type="date"
                                value={endDate}
                                onChange={(event) =>
                                    setEndDate(
                                        event.target.value
                                    )
                                }
                            />

                        </div>

                    </div>

                    <div className="form-group">

                        <label htmlFor="reason">
                            Reason
                        </label>

                        <textarea
                            id="reason"
                            rows="5"
                            value={reason}
                            onChange={(event) =>
                                setReason(
                                    event.target.value
                                )
                            }
                            placeholder="Enter the reason for your leave"
                        />

                    </div>

                    <div className="form-actions">

                        <button
                            type="submit"
                            disabled={
                                submitting ||
                                loading ||
                                leaveTypes.length === 0
                            }
                        >
                            {submitting
                                ? "Submitting..."
                                : "Apply Leave"}
                        </button>

                        <button
                            type="button"
                            className="secondary-button"
                            onClick={() =>
                                navigate("/dashboard")
                            }
                        >
                            Cancel
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default ApplyLeave;