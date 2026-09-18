import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import api from "../services/api";

function ApprovalDetails() {

    const { requestId } = useParams();

    const [steps, setSteps] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const loadSteps = async () => {

            try {

                const response =
                    await api.get(
                        `/approvals/${requestId}`
                    );

                setSteps(response.data);

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load approval details."
                );

            } finally {
                setLoading(false);
            }
        };

        loadSteps();

    }, [requestId]);

    return (
        <div>

            <div className="page-heading">
                <div>
                    <h2>
                        Approval Timeline
                    </h2>

                    <p>
                        Leave Request #{requestId}
                    </p>
                </div>
            </div>

            <section className="card">

                {loading && (
                    <p>Loading approval timeline...</p>
                )}

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {!loading &&
                    !error &&
                    steps.length === 0 && (
                        <p className="empty">
                            No approval steps found.
                        </p>
                    )}

                <div className="timeline">

                    {steps.map((step) => (

                        <div
                            className="timeline-item"
                            key={step.approvalStepId}
                        >

                            <div className="timeline-number">
                                {step.stepOrder}
                            </div>

                            <div className="timeline-content">

                                <h3>
                                    {step.approverRole}
                                </h3>

                                <p>
                                    Employee:{" "}
                                    {step.employeeName}
                                </p>

                                <p>
                                    {step.startDate}
                                    {" → "}
                                    {step.endDate}
                                </p>

                                <span
                                    className={`status status-${step.approvalStatus.toLowerCase()}`}
                                >
                                    {step.approvalStatus}
                                </span>

                                {step.rejectionReason && (
                                    <p>
                                        Rejection reason:{" "}
                                        {step.rejectionReason}
                                    </p>
                                )}

                            </div>

                        </div>

                    ))}

                </div>

            </section>

        </div>
    );
}

export default ApprovalDetails;