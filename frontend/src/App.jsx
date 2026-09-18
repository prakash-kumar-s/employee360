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
                            element={<Dashboard />}
                        />

                        <Route
                            path="/apply-leave"
                            element={<ApplyLeave />}
                        />

                        <Route
                            path="/leave-history"
                            element={<LeaveHistory />}
                        />

                        <Route
                            path="/approvals"
                            element={<Approvals />}
                        />

                        <Route
                            path="/approvals/:requestId"
                            element={<ApprovalDetails />}
                        />

                        <Route
                            path="/notifications"
                            element={<Notifications />}
                        />

                        <Route
                            path="/audit"
                            element={<AuditLog />}
                        />

                        <Route
                            path="/admin"
                            element={<Administration />}
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