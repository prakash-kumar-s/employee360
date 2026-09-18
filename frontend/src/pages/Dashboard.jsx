import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function Dashboard() {

    const { user } = useAuth();
    const navigate = useNavigate();

    const [balances, setBalances] = useState([]);
    const [leaveRequests, setLeaveRequests] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [pendingApprovals, setPendingApprovals] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [processingId, setProcessingId] = useState(null);

    const [rejectingId, setRejectingId] = useState(null);
    const [rejectionReason, setRejectionReason] = useState("");


    const isEmployee =
        user?.role === "EMPLOYEE";

    const isApprover =
        user?.role === "MANAGER" ||
        user?.role === "DEPARTMENT_HEAD" ||
        user?.role === "HR" ||
        user?.role === "ADMIN";


    /*
     * GET BACKEND ERROR MESSAGE
     */

    const getErrorMessage = (error, defaultMessage) => {

        if (error.response?.data?.message) {
            return error.response.data.message;
        }

        if (typeof error.response?.data === "string") {
            return error.response.data;
        }

        if (error.response?.status === 401) {
            return "Your session has expired. Please login again.";
        }

        if (error.response?.status === 403) {
            return "You are not authorized to access this information.";
        }

        if (error.response?.status === 404) {
            return "The requested information was not found.";
        }

        if (error.response?.status >= 500) {
            return "The server encountered an error.";
        }

        if (error.message === "Network Error") {
            return "Cannot connect to the Employee360 server.";
        }

        return defaultMessage;
    };


    /*
     * LOAD DASHBOARD
     */

    useEffect(() => {

        if (!user?.id) {

            setLoading(false);

            setError(
                "User information is missing. Please logout and login again."
            );

            return;
        }


        const loadDashboard = async () => {

            setLoading(true);
            setError("");


            /*
             * EMPLOYEE
             */

            if (isEmployee) {

                try {

                    const response =
                        await api.get(
                            `/leaves/balance/${user.id}`
                        );

                    setBalances(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );

                } catch (error) {

                    console.error(
                        "BALANCE API ERROR:",
                        error
                    );

                    setError(
                        getErrorMessage(
                            error,
                            "Unable to load leave balance."
                        )
                    );
                }


                try {

                    const response =
                        await api.get(
                            `/leaves/user/${user.id}`
                        );

                    setLeaveRequests(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );

                } catch (error) {

                    console.error(
                        "LEAVE HISTORY API ERROR:",
                        error
                    );

                    if (!error.response) {

                        setError(
                            getErrorMessage(
                                error,
                                "Unable to load leave history."
                            )
                        );
                    }
                }


                try {

                    const response =
                        await api.get(
                            `/notifications/user/${user.id}`
                        );

                    setNotifications(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );

                } catch (error) {

                    console.error(
                        "NOTIFICATION API ERROR:",
                        error
                    );
                }


                setLoading(false);

                return;
            }


            /*
             * MANAGER / DEPARTMENT HEAD / HR / ADMIN
             */

            if (isApprover) {

                /*
                 * LOAD PENDING APPROVALS
                 */

                try {

                    const response =
                        await api.get(
                            `/approvals/manager/${user.id}`
                        );

                    console.log(
                        "PENDING APPROVALS:",
                        response.data
                    );

                    setPendingApprovals(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );

                } catch (error) {

                    console.error(
                        "PENDING APPROVAL API ERROR:",
                        error
                    );

                    setError(
                        getErrorMessage(
                            error,
                            "Unable to load pending approvals."
                        )
                    );
                }


                /*
                 * LOAD NOTIFICATIONS
                 */

                try {

                    const response =
                        await api.get(
                            `/notifications/user/${user.id}`
                        );

                    console.log(
                        "NOTIFICATIONS:",
                        response.data
                    );

                    setNotifications(
                        Array.isArray(response.data)
                            ? response.data
                            : []
                    );

                } catch (error) {

                    console.error(
                        "NOTIFICATION API ERROR:",
                        error
                    );

                    /*
                     * Notification failure should NOT
                     * prevent pending approvals from
                     * appearing.
                     */

                    setNotifications([]);
                }


                setLoading(false);

                return;
            }


            setLoading(false);
        };


        loadDashboard();

    }, [user, isEmployee, isApprover]);


    /*
     * APPROVE
     */

    const handleApprove = async (requestId) => {

        const confirmed =
            window.confirm(
                "Are you sure you want to approve this leave request?"
            );

        if (!confirmed) {
            return;
        }


        try {

            setProcessingId(requestId);
            setError("");

            await api.post(
                `/approvals/${requestId}/approve`
            );


            /*
             * Remove it from pending list.
             */

            setPendingApprovals(
                current =>
                    current.filter(
                        approval =>
                            approval.requestId !== requestId
                    )
            );


        } catch (error) {

            console.error(
                "APPROVE API ERROR:",
                error
            );

            setError(
                getErrorMessage(
                    error,
                    "Unable to approve leave request."
                )
            );

        } finally {

            setProcessingId(null);
        }
    };


    /*
     * START REJECT
     */

    const handleStartReject = (requestId) => {

        setRejectingId(requestId);

        setRejectionReason("");

        setError("");
    };


    /*
     * CANCEL REJECT
     */

    const handleCancelReject = () => {

        setRejectingId(null);

        setRejectionReason("");
    };


    /*
     * REJECT
     */

    const handleReject = async (requestId) => {

        if (!rejectionReason.trim()) {

            setError(
                "Please enter a rejection reason."
            );

            return;
        }


        const confirmed =
            window.confirm(
                "Are you sure you want to reject this leave request?"
            );

        if (!confirmed) {
            return;
        }


        try {

            setProcessingId(requestId);
            setError("");


            await api.post(
                `/approvals/${requestId}/reject`,
                {
                    rejectionReason:
                        rejectionReason.trim()
                }
            );


            /*
             * Remove rejected request
             * from pending approvals.
             */

            setPendingApprovals(
                current =>
                    current.filter(
                        approval =>
                            approval.requestId !== requestId
                    )
            );


            setRejectingId(null);

            setRejectionReason("");


        } catch (error) {

            console.error(
                "REJECT API ERROR:",
                error
            );

            setError(
                getErrorMessage(
                    error,
                    "Unable to reject leave request."
                )
            );

        } finally {

            setProcessingId(null);
        }
    };


    /*
     * LOADING
     */

    if (loading) {

        return (
            <div className="page-container">

                <div className="page-header">

                    <div>

                        <h2>
                            Dashboard
                        </h2>

                        <p>
                            Welcome back, {user?.name}
                        </p>

                    </div>

                </div>

                <div className="message-card">

                    Loading dashboard...

                </div>

            </div>
        );
    }


    /*
     * EMPLOYEE DASHBOARD
     */

    if (isEmployee) {

        return (
            <div className="page-container">

                <div className="page-header">

                    <div>

                        <span className="page-eyebrow">
                            Employee Portal
                        </span>

                        <h2>
                            Dashboard
                        </h2>

                        <p>
                            Welcome back, {user?.name}
                        </p>

                    </div>

                    <button
                        className="primary-button"
                        onClick={() =>
                            navigate("/apply-leave")
                        }
                    >
                        Apply Leave
                    </button>

                </div>


                {error && (

                    <div className="error-message">

                        <strong>
                            Unable to load information
                        </strong>

                        <span>
                            {error}
                        </span>

                    </div>

                )}


                <section className="dashboard-section">

                    <div className="section-heading">

                        <div>

                            <span className="section-eyebrow">
                                Overview
                            </span>

                            <h3>
                                Leave Balance
                            </h3>

                        </div>

                    </div>


                    {balances.length === 0 ? (

                        <div className="empty-state">
                            No leave balance available.
                        </div>

                    ) : (

                        <div className="card-grid">

                            {balances.map(
                                balance => (

                                    <div
                                        className="dashboard-card"
                                        key={balance.id}
                                    >

                                        <div className="card-title-row">

                                            <div className="leave-icon">
                                                {balance.leaveTypeName
                                                    ?.charAt(0)
                                                    ?.toUpperCase()}
                                            </div>

                                            <h4>
                                                {
                                                    balance.leaveTypeName
                                                }
                                            </h4>

                                        </div>


                                        <div className="balance-details">

                                            <div>

                                                <span>
                                                    Total
                                                </span>

                                                <strong>
                                                    {
                                                        balance.totalLeaves
                                                    }
                                                </strong>

                                            </div>


                                            <div>

                                                <span>
                                                    Used
                                                </span>

                                                <strong>
                                                    {
                                                        balance.usedLeaves
                                                    }
                                                </strong>

                                            </div>


                                            <div>

                                                <span>
                                                    Remaining
                                                </span>

                                                <strong>
                                                    {
                                                        balance.remainingLeaves
                                                    }
                                                </strong>

                                            </div>

                                        </div>

                                    </div>

                                )
                            )}

                        </div>

                    )}

                </section>


                <section className="dashboard-section">

                    <div className="section-heading">

                        <div>

                            <span className="section-eyebrow">
                                Activity
                            </span>

                            <h3>
                                Recent Leave Requests
                            </h3>

                        </div>

                        <button
                            className="text-button"
                            onClick={() =>
                                navigate("/leave-history")
                            }
                        >
                            View History →
                        </button>

                    </div>


                    {leaveRequests.length === 0 ? (

                        <div className="empty-state">
                            No leave requests found.
                        </div>

                    ) : (

                        <div className="table-container">

                            <table>

                                <thead>

                                    <tr>

                                        <th>
                                            Leave Type
                                        </th>

                                        <th>
                                            Start Date
                                        </th>

                                        <th>
                                            End Date
                                        </th>

                                        <th>
                                            Reason
                                        </th>

                                        <th>
                                            Status
                                        </th>

                                    </tr>

                                </thead>


                                <tbody>

                                    {leaveRequests
                                        .slice(0, 5)
                                        .map(
                                            leave => (

                                                <tr
                                                    key={
                                                        leave.id
                                                    }
                                                >

                                                    <td>
                                                        {
                                                            leave.leaveTypeName
                                                        }
                                                    </td>

                                                    <td>
                                                        {
                                                            leave.startDate
                                                        }
                                                    </td>

                                                    <td>
                                                        {
                                                            leave.endDate
                                                        }
                                                    </td>

                                                    <td>
                                                        {
                                                            leave.reason
                                                        }
                                                    </td>

                                                    <td>

                                                        <span
                                                            className={
                                                                `status-badge status-${leave.status?.toLowerCase()}`
                                                            }
                                                        >
                                                            {
                                                                leave.status
                                                            }
                                                        </span>

                                                    </td>

                                                </tr>

                                            )
                                        )}

                                </tbody>

                            </table>

                        </div>

                    )}

                </section>


                <section className="dashboard-section">

                    <div className="section-heading">

                        <div>

                            <span className="section-eyebrow">
                                Updates
                            </span>

                            <h3>
                                Recent Notifications
                            </h3>

                        </div>

                    </div>


                    {notifications.length === 0 ? (

                        <div className="empty-state">
                            No notifications available.
                        </div>

                    ) : (

                        <div className="notification-list">

                            {notifications
                                .slice(0, 5)
                                .map(
                                    notification => (

                                        <div
                                            className="notification-card"
                                            key={
                                                notification.id
                                            }
                                        >

                                            <div className="notification-dot">
                                            </div>

                                            <div className="notification-content">

                                                <strong>
                                                    {
                                                        notification.type
                                                    }
                                                </strong>

                                                <p>
                                                    {
                                                        notification.message
                                                    }
                                                </p>

                                                <small>
                                                    {
                                                        notification.createdAt
                                                    }
                                                </small>

                                            </div>

                                        </div>

                                    )
                                )}

                        </div>

                    )}

                </section>

            </div>
        );
    }


    /*
     * APPROVER DASHBOARD
     */

    return (
        <div className="page-container">

            <div className="page-header">

                <div>

                    <span className="page-eyebrow">
                        Approval Portal
                    </span>

                    <h2>
                        Dashboard
                    </h2>

                    <p>
                        Welcome back, {user?.name}
                    </p>

                </div>

                <div className="role-display">
                    {user?.role}
                </div>

            </div>


            {error && (

                <div className="error-message">

                    <strong>
                        Dashboard Error
                    </strong>

                    <span>
                        {error}
                    </span>

                </div>

            )}


            {/* SUMMARY */}

            <div className="summary-grid">

                <div className="summary-card">

                    <div className="summary-icon">
                        {pendingApprovals.length}
                    </div>

                    <div>

                        <span>
                            Pending Approvals
                        </span>

                        <strong>
                            {pendingApprovals.length}
                        </strong>

                    </div>

                </div>


                <div className="summary-card">

                    <div className="summary-icon">
                        {notifications.length}
                    </div>

                    <div>

                        <span>
                            Notifications
                        </span>

                        <strong>
                            {notifications.length}
                        </strong>

                    </div>

                </div>

            </div>


            {/* PENDING APPROVALS */}

            <section className="dashboard-section">

                <div className="section-heading">

                    <div>

                        <span className="section-eyebrow">
                            Action Required
                        </span>

                        <h3>
                            Pending Approvals
                        </h3>

                        <p>
                            Leave requests waiting for your decision.
                        </p>

                    </div>


                    <button
                        className="secondary-button"
                        onClick={() =>
                            navigate("/approvals")
                        }
                    >
                        View All Approvals
                    </button>

                </div>


                {pendingApprovals.length === 0 ? (

                    <div className="empty-state">

                        <strong>
                            No pending approvals
                        </strong>

                        <span>
                            There are currently no leave requests
                            waiting for your action.
                        </span>

                    </div>

                ) : (

                    <div className="approval-list">

                        {pendingApprovals
                            .slice(0, 5)
                            .map(
                                approval => (

                                    <div
                                        className="approval-card"
                                        key={
                                            approval.approvalStepId
                                        }
                                    >

                                        <div className="approval-card-header">

                                            <div className="employee-info">

                                                <div className="employee-avatar">
                                                    {
                                                        approval.employeeName
                                                            ?.charAt(0)
                                                            ?.toUpperCase()
                                                    }
                                                </div>

                                                <div>

                                                    <strong>
                                                        {
                                                            approval.employeeName
                                                        }
                                                    </strong>

                                                    <span>
                                                        Request #
                                                        {
                                                            approval.requestId
                                                        }
                                                    </span>

                                                </div>

                                            </div>


                                            <span className="status-badge status-pending">
                                                PENDING
                                            </span>

                                        </div>


                                        <div className="approval-details-grid">

                                            <div className="detail-item">

                                                <span>
                                                    Leave Type
                                                </span>

                                                <strong>
                                                    {
                                                        approval.leaveTypeName
                                                    }
                                                </strong>

                                            </div>


                                            <div className="detail-item">

                                                <span>
                                                    Start Date
                                                </span>

                                                <strong>
                                                    {
                                                        approval.startDate
                                                    }
                                                </strong>

                                            </div>


                                            <div className="detail-item">

                                                <span>
                                                    End Date
                                                </span>

                                                <strong>
                                                    {
                                                        approval.endDate
                                                    }
                                                </strong>

                                            </div>


                                            <div className="detail-item">

                                                <span>
                                                    Approval Step
                                                </span>

                                                <strong>
                                                    Step {
                                                        approval.stepOrder
                                                    }
                                                </strong>

                                            </div>

                                        </div>


                                        <div className="approval-reason">

                                            <span>
                                                Reason
                                            </span>

                                            <p>
                                                {
                                                    approval.reason
                                                }
                                            </p>

                                        </div>


                                        {rejectingId ===
                                        approval.requestId ? (

                                            <div className="rejection-panel">

                                                <strong>
                                                    Reject Leave Request
                                                </strong>

                                                <p>
                                                    Enter the reason for
                                                    rejecting this request.
                                                </p>


                                                <div className="form-group">

                                                    <label>
                                                        Rejection Reason
                                                    </label>

                                                    <textarea
                                                        rows="4"
                                                        value={
                                                            rejectionReason
                                                        }
                                                        onChange={
                                                            event =>
                                                                setRejectionReason(
                                                                    event.target.value
                                                                )
                                                        }
                                                        placeholder="Enter rejection reason..."
                                                        disabled={
                                                            processingId ===
                                                            approval.requestId
                                                        }
                                                    />

                                                </div>


                                                <div className="form-actions">

                                                    <button
                                                        className="danger-button"
                                                        disabled={
                                                            processingId ===
                                                            approval.requestId
                                                        }
                                                        onClick={() =>
                                                            handleReject(
                                                                approval.requestId
                                                            )
                                                        }
                                                    >
                                                        {processingId ===
                                                        approval.requestId
                                                            ? "Rejecting..."
                                                            : "Confirm Rejection"}
                                                    </button>


                                                    <button
                                                        className="secondary-button"
                                                        disabled={
                                                            processingId ===
                                                            approval.requestId
                                                        }
                                                        onClick={
                                                            handleCancelReject
                                                        }
                                                    >
                                                        Cancel
                                                    </button>

                                                </div>

                                            </div>

                                        ) : (

                                            <div className="approval-actions">

                                                <button
                                                    className="approve-button"
                                                    disabled={
                                                        processingId !==
                                                        null
                                                    }
                                                    onClick={() =>
                                                        handleApprove(
                                                            approval.requestId
                                                        )
                                                    }
                                                >
                                                    {processingId ===
                                                    approval.requestId
                                                        ? "Approving..."
                                                        : "✓ Approve"}
                                                </button>


                                                <button
                                                    className="reject-button"
                                                    disabled={
                                                        processingId !==
                                                        null
                                                    }
                                                    onClick={() =>
                                                        handleStartReject(
                                                            approval.requestId
                                                        )
                                                    }
                                                >
                                                    ✕ Reject
                                                </button>


                                                <button
                                                    className="view-button"
                                                    disabled={
                                                        processingId !==
                                                        null
                                                    }
                                                    onClick={() =>
                                                        navigate(
                                                            `/approvals/${approval.requestId}`
                                                        )
                                                    }
                                                >
                                                    View Details
                                                </button>

                                            </div>

                                        )}

                                    </div>

                                )
                            )}

                    </div>

                )}

            </section>


            {/* NOTIFICATIONS */}

            <section className="dashboard-section">

                <div className="section-heading">

                    <div>

                        <span className="section-eyebrow">
                            Updates
                        </span>

                        <h3>
                            Recent Notifications
                        </h3>

                    </div>

                    <button
                        className="text-button"
                        onClick={() =>
                            navigate("/notifications")
                        }
                    >
                        View All →
                    </button>

                </div>


                {notifications.length === 0 ? (

                    <div className="empty-state">
                        No notifications available.
                    </div>

                ) : (

                    <div className="notification-list">

                        {notifications
                            .slice(0, 5)
                            .map(
                                notification => (

                                    <div
                                        className="notification-card"
                                        key={
                                            notification.id
                                        }
                                    >

                                        <div className="notification-dot">
                                        </div>

                                        <div className="notification-content">

                                            <strong>
                                                {
                                                    notification.type
                                                }
                                            </strong>

                                            <p>
                                                {
                                                    notification.message
                                                }
                                            </p>

                                            <small>
                                                {
                                                    notification.createdAt
                                                }
                                            </small>

                                        </div>

                                    </div>

                                )
                            )}

                    </div>

                )}

            </section>

        </div>
    );
}

export default Dashboard;