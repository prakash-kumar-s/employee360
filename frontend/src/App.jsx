import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
} from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";

import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ApplyLeave from "./pages/ApplyLeave";
import LeaveHistory from "./pages/LeaveHistory";
import ApprovalDetails from "./pages/ApprovalDetails";
import Approvals from "./pages/Approvals";
import Notifications from "./pages/Notifications";
import AuditLog from "./pages/AuditLog";
import Administration from "./pages/Administration";

function App() {

    return (
        <BrowserRouter>

            <AuthProvider>

                <Routes>

                    <Route
                        path="/login"
                        element={<Login />}
                    />

                    <Route
                        element={
                            <ProtectedRoute>
                                <Layout />
                            </ProtectedRoute>
                        }
                    >

                        <Route
                            path="/dashboard"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD", "HR"]}>
                                    <Dashboard />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/apply-leave"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD"]}>
                                    <ApplyLeave />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/leave-history"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD"]}>
                                    <LeaveHistory />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/approvals"
                            element={
                                <ProtectedRoute allowedRoles={["MANAGER", "DEPARTMENT_HEAD", "HR"]}>
                                    <Approvals />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/approvals/:requestId"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD", "HR"]}>
                                    <ApprovalDetails />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/notifications"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD", "HR"]}>
                                    <Notifications />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/audit"
                            element={
                                <ProtectedRoute allowedRoles={["EMPLOYEE", "MANAGER", "DEPARTMENT_HEAD", "HR"]}>
                                    <AuditLog />
                                </ProtectedRoute>
                            }
                        />

                        <Route
                            path="/admin"
                            element={
                                <ProtectedRoute allowedRoles={["ADMIN"]}>
                                    <Administration />
                                </ProtectedRoute>
                            }
                        />

                    </Route>

                    <Route
                        path="/"
                        element={
                            <Navigate
                                to="/dashboard"
                                replace
                            />
                        }
                    />

                    <Route
                        path="*"
                        element={
                            <Navigate
                                to="/dashboard"
                                replace
                            />
                        }
                    />

                </Routes>

            </AuthProvider>

        </BrowserRouter>
    );
}

export default App;