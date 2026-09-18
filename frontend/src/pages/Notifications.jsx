import { useEffect, useState } from "react";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function Notifications() {

    const { user } = useAuth();

    const [notifications, setNotifications] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {

        const loadNotifications = async () => {

            try {

                const response =
                    await api.get(
                        `/notifications/user/${user.id}`
                    );

                setNotifications(
                    response.data
                );

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load notifications."
                );

            } finally {
                setLoading(false);
            }
        };

        loadNotifications();

    }, [user]);

    return (
        <div>

            <div className="page-heading">
                <div>
                    <h2>Notifications</h2>
                    <p>
                        Your leave management notifications.
                    </p>
                </div>
            </div>

            <section className="card">

                {loading && (
                    <p>Loading notifications...</p>
                )}

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {!loading &&
                    !error &&
                    notifications.length === 0 && (
                        <p className="empty">
                            No notifications found.
                        </p>
                    )}

                <div className="notification-list">

                    {notifications.map(
                        (notification) => (

                            <div
                                className={`notification-item ${
                                    notification.isRead
                                        ? ""
                                        : "notification-unread"
                                }`}
                                key={notification.id}
                            >

                                <div>
                                    <strong>
                                        {notification.type}
                                    </strong>

                                    <p>
                                        {notification.message}
                                    </p>
                                </div>

                                <small>
                                    {new Date(
                                        notification.createdAt
                                    ).toLocaleString()}
                                </small>

                            </div>

                        )
                    )}

                </div>

            </section>

        </div>
    );
}

export default Notifications;