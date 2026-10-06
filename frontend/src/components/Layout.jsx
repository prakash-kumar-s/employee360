import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Layout() {

    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const role = user?.role;

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="app-layout">

            <aside className="sidebar">

                <div className="brand">
                    <h2>Employee360</h2>
                    <span>Leave Management</span>
                </div>

                <nav>

                    {role !== "ADMIN" && (
                        <NavLink to="/dashboard">
                            Dashboard
                        </NavLink>
                    )}

                    {user && role !== "ADMIN" && role !== "HR" && (
                        <>
                            <NavLink to="/apply-leave">
                                Apply Leave
                            </NavLink>

                            <NavLink to="/leave-history">
                                Leave History
                            </NavLink>
                        </>
                    )}

                    {(
                        role === "MANAGER" ||
                        role === "DEPARTMENT_HEAD" ||
                        role === "HR"
                    ) && (
                        <NavLink to="/approvals">
                            Approvals
                        </NavLink>
                    )}

                    {role !== "ADMIN" && (
                        <>
                            <NavLink to="/notifications">
                                Notifications
                            </NavLink>

                            <NavLink to="/audit">
                                Audit Log
                            </NavLink>
                        </>
                    )}

                    {role === "ADMIN" && (
                        <NavLink to="/admin">
                            System Administration
                        </NavLink>
                    )}

                </nav>

                <div className="sidebar-bottom">

                    <div className="user-summary">
                        <strong>{user?.name}</strong>
                        <span>{role}</span>
                    </div>

                    <button
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>

                </div>

            </aside>

            <main className="main-content">

                <header className="topbar">
                    <div>
                        <h1>Employee360</h1>
                    </div>

                    <div className="topbar-user">
                        {user?.name}
                    </div>
                </header>

                <section className="page-content">
                    <Outlet />
                </section>

            </main>

        </div>
    );
}

export default Layout;