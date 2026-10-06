import { useCallback, useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function Administration() {
    const { user } = useAuth();
    const [activeTab, setActiveTab] = useState("users");

    // Data lists
    const [users, setUsers] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [leaveTypes, setLeaveTypes] = useState([]);
    const [holidays, setHolidays] = useState([]);
    const [workflowRules, setWorkflowRules] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    // Form states
    const [userForm, setUserForm] = useState({ name: "", username: "", password: "", role: "EMPLOYEE", departmentId: "", managerId: "" });
    const [deptForm, setDeptForm] = useState({ name: "" });
    const [leaveTypeForm, setLeaveTypeForm] = useState({ name: "", entitlement: 10, maxConsecutiveLeave: 5 });
    const [holidayForm, setHolidayForm] = useState({ name: "", date: "" });
    const [ruleForm, setRuleForm] = useState({ minDays: 1, maxDays: "", approverRole: "MANAGER", approvalLevel: 1 });

    const isAuthorized = user?.role === "ADMIN";
    const isAdmin = user?.role === "ADMIN";

    const fetchAllData = useCallback(async () => {
        const [usersRes, deptsRes, leaveTypesRes, holidaysRes, rulesRes] = await Promise.all([
                api.get("/admin/users"),
                api.get("/admin/departments"),
                api.get("/admin/leave-types"),
                api.get("/admin/holidays"),
                isAdmin
                    ? api.get("/admin/workflow-rules")
                    : Promise.resolve({ data: [] })
            ]);
        return [usersRes, deptsRes, leaveTypesRes, holidaysRes, rulesRes];
    }, [isAdmin]);

    useEffect(() => {
        if (!isAuthorized) return undefined;

        let active = true;
        fetchAllData()
            .then(([usersRes, deptsRes, leaveTypesRes, holidaysRes, rulesRes]) => {
                if (!active) return;
                setUsers(Array.isArray(usersRes.data) ? usersRes.data : []);
                setDepartments(Array.isArray(deptsRes.data) ? deptsRes.data : []);
                setLeaveTypes(Array.isArray(leaveTypesRes.data) ? leaveTypesRes.data : []);
                setHolidays(Array.isArray(holidaysRes.data) ? holidaysRes.data : []);
                setWorkflowRules(Array.isArray(rulesRes.data) ? rulesRes.data : []);
            })
            .catch((err) => {
                console.error(err);
                if (active) setError("Failed to load administration data.");
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => {
            active = false;
        };
    }, [isAuthorized, fetchAllData]);

    const loadAllData = async () => {
        try {
            const [usersRes, deptsRes, leaveTypesRes, holidaysRes, rulesRes] =
                await fetchAllData();
            setUsers(Array.isArray(usersRes.data) ? usersRes.data : []);
            setDepartments(Array.isArray(deptsRes.data) ? deptsRes.data : []);
            setLeaveTypes(Array.isArray(leaveTypesRes.data) ? leaveTypesRes.data : []);
            setHolidays(Array.isArray(holidaysRes.data) ? holidaysRes.data : []);
            setWorkflowRules(Array.isArray(rulesRes.data) ? rulesRes.data : []);
        } catch (err) {
            console.error(err);
            setError("Failed to load administration data.");
        }
    };

    if (!isAuthorized) {
        return (
            <div className="error-message">
                Access Denied. You do not have permission to view Administration.
            </div>
        );
    }

    // Handlers for User
    const handleCreateUser = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await api.post("/admin/users", {
                ...userForm,
                departmentId: Number(userForm.departmentId),
                managerId: userForm.managerId ? Number(userForm.managerId) : null
            });
            setMessage("User created successfully.");
            setUserForm({ name: "", username: "", password: "", role: "EMPLOYEE", departmentId: "", managerId: "" });
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create user.");
        }
    };

    const handleDeleteUser = async (id) => {
        if (!window.confirm("Are you sure you want to delete this user?")) return;
        setError("");
        setMessage("");
        try {
            await api.delete(`/admin/users/${id}`);
            setMessage("User deleted successfully.");
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete user.");
        }
    };

    // Handlers for Department
    const handleCreateDept = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await api.post("/admin/departments", deptForm);
            setMessage("Department created successfully.");
            setDeptForm({ name: "" });
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create department.");
        }
    };

    const handleDeleteDept = async (id) => {
        if (!window.confirm("Are you sure you want to delete this department?")) return;
        setError("");
        setMessage("");
        try {
            await api.delete(`/admin/departments/${id}`);
            setMessage("Department deleted successfully.");
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete department.");
        }
    };

    // Handlers for Leave Type
    const handleCreateLeaveType = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await api.post("/admin/leave-types", {
                name: leaveTypeForm.name,
                entitlement: Number(leaveTypeForm.entitlement),
                maxConsecutiveLeave: Number(leaveTypeForm.maxConsecutiveLeave)
            });
            setMessage("Leave type created successfully.");
            setLeaveTypeForm({ name: "", entitlement: 10, maxConsecutiveLeave: 5 });
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create leave type.");
        }
    };

    const handleDeleteLeaveType = async (id) => {
        if (!window.confirm("Are you sure you want to delete this leave type?")) return;
        setError("");
        setMessage("");
        try {
            await api.delete(`/admin/leave-types/${id}`);
            setMessage("Leave type deleted successfully.");
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete leave type.");
        }
    };

    // Handlers for Holiday
    const handleCreateHoliday = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await api.post("/admin/holidays", holidayForm);
            setMessage("Holiday created successfully.");
            setHolidayForm({ name: "", date: "" });
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create holiday.");
        }
    };

    const handleDeleteHoliday = async (id) => {
        if (!window.confirm("Are you sure you want to delete this holiday?")) return;
        setError("");
        setMessage("");
        try {
            await api.delete(`/admin/holidays/${id}`);
            setMessage("Holiday deleted successfully.");
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete holiday.");
        }
    };

    // Handlers for Workflow Rule
    const handleCreateRule = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await api.post("/admin/workflow-rules", {
                minDays: Number(ruleForm.minDays),
                maxDays: ruleForm.maxDays ? Number(ruleForm.maxDays) : null,
                approverRole: ruleForm.approverRole,
                approvalLevel: Number(ruleForm.approvalLevel)
            });
            setMessage("Workflow rule created successfully.");
            setRuleForm({ minDays: 1, maxDays: "", approverRole: "MANAGER", approvalLevel: 1 });
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create workflow rule.");
        }
    };

    const handleDeleteRule = async (id) => {
        if (!window.confirm("Are you sure you want to delete this workflow rule?")) return;
        setError("");
        setMessage("");
        try {
            await api.delete(`/admin/workflow-rules/${id}`);
            setMessage("Workflow rule deleted successfully.");
            loadAllData();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete workflow rule.");
        }
    };

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h2>{isAdmin ? "System Administration" : "HR Management"}</h2>
                    <p>
                        {isAdmin
                            ? "Manage organization settings and leave approval workflow rules."
                            : "Manage system settings and leave approval workflow rules."}
                    </p>
                </div>
            </div>

            {error && <div className="error-message">{error}</div>}
            {message && <div className="success-message">{message}</div>}

            <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
                <button className={activeTab === "users" ? "primary-button" : "secondary-button"} onClick={() => setActiveTab("users")}>Users</button>
                <button className={activeTab === "departments" ? "primary-button" : "secondary-button"} onClick={() => setActiveTab("departments")}>Departments</button>
                <button className={activeTab === "leaveTypes" ? "primary-button" : "secondary-button"} onClick={() => setActiveTab("leaveTypes")}>Leave Types</button>
                <button className={activeTab === "holidays" ? "primary-button" : "secondary-button"} onClick={() => setActiveTab("holidays")}>Holidays</button>
                {isAdmin && (
                    <button className={activeTab === "rules" ? "primary-button" : "secondary-button"} onClick={() => setActiveTab("rules")}>Workflow Rules</button>
                )}
            </div>

            {loading ? (
                <p>Loading administration data...</p>
            ) : (
                <>
                    {/* USERS TAB */}
                    {activeTab === "users" && (
                        <section className="card">
                            <h3>Users Management</h3>
                            <form onSubmit={handleCreateUser} style={{ marginBottom: "20px" }}>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Name</label>
                                        <input type="text" required value={userForm.name} onChange={(e) => setUserForm({ ...userForm, name: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Username</label>
                                        <input type="text" required value={userForm.username} onChange={(e) => setUserForm({ ...userForm, username: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Password</label>
                                        <input type="password" required value={userForm.password} onChange={(e) => setUserForm({ ...userForm, password: e.target.value })} />
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Role</label>
                                        <select value={userForm.role} onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}>
                                            <option value="EMPLOYEE">EMPLOYEE</option>
                                            <option value="MANAGER">MANAGER</option>
                                            <option value="DEPARTMENT_HEAD">DEPARTMENT_HEAD</option>
                                            <option value="HR">HR</option>
                                            <option value="ADMIN">ADMIN</option>
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label>Department</label>
                                        <select required value={userForm.departmentId} onChange={(e) => setUserForm({ ...userForm, departmentId: e.target.value })}>
                                            <option value="">Select Department</option>
                                            {departments.map((d) => (
                                                <option key={d.id} value={d.id}>{d.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label>Manager (Optional)</label>
                                        <select value={userForm.managerId} onChange={(e) => setUserForm({ ...userForm, managerId: e.target.value })}>
                                            <option value="">None</option>
                                            {users.map((u) => (
                                                <option key={u.id} value={u.id}>{u.name} ({u.role})</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                                <button type="submit" className="primary-button">Add User</button>
                            </form>

                            <div className="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Username</th>
                                            <th>Role</th>
                                            <th>Department</th>
                                            <th>Manager</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {users.map((u) => (
                                            <tr key={u.id}>
                                                <td>{u.id}</td>
                                                <td>{u.name}</td>
                                                <td>{u.username}</td>
                                                <td>{u.role}</td>
                                                <td>{u.departmentName || "-"}</td>
                                                <td>{u.managerName || "-"}</td>
                                                <td>
                                                    <button className="danger-button" onClick={() => handleDeleteUser(u.id)}>Delete</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {/* DEPARTMENTS TAB */}
                    {activeTab === "departments" && (
                        <section className="card">
                            <h3>Departments</h3>
                            <form onSubmit={handleCreateDept} style={{ marginBottom: "20px" }}>
                                <div className="form-group">
                                    <label>Department Name</label>
                                    <input type="text" required value={deptForm.name} onChange={(e) => setDeptForm({ name: e.target.value })} />
                                </div>
                                <button type="submit" className="primary-button">Add Department</button>
                            </form>
                            <div className="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {departments.map((d) => (
                                            <tr key={d.id}>
                                                <td>{d.id}</td>
                                                <td>{d.name}</td>
                                                <td>
                                                    <button className="danger-button" onClick={() => handleDeleteDept(d.id)}>Delete</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {/* LEAVE TYPES TAB */}
                    {activeTab === "leaveTypes" && (
                        <section className="card">
                            <h3>Leave Types</h3>
                            <form onSubmit={handleCreateLeaveType} style={{ marginBottom: "20px" }}>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Name</label>
                                        <input type="text" required value={leaveTypeForm.name} onChange={(e) => setLeaveTypeForm({ ...leaveTypeForm, name: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Entitlement (Days)</label>
                                        <input type="number" min="0" required value={leaveTypeForm.entitlement} onChange={(e) => setLeaveTypeForm({ ...leaveTypeForm, entitlement: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Max Consecutive Days</label>
                                        <input type="number" min="1" required value={leaveTypeForm.maxConsecutiveLeave} onChange={(e) => setLeaveTypeForm({ ...leaveTypeForm, maxConsecutiveLeave: e.target.value })} />
                                    </div>
                                </div>
                                <button type="submit" className="primary-button">Add Leave Type</button>
                            </form>
                            <div className="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Entitlement</th>
                                            <th>Max Consecutive</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {leaveTypes.map((lt) => (
                                            <tr key={lt.id}>
                                                <td>{lt.id}</td>
                                                <td>{lt.name}</td>
                                                <td>{lt.entitlement}</td>
                                                <td>{lt.maxConsecutiveLeave}</td>
                                                <td>
                                                    <button className="danger-button" onClick={() => handleDeleteLeaveType(lt.id)}>Delete</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {/* HOLIDAYS TAB */}
                    {activeTab === "holidays" && (
                        <section className="card">
                            <h3>Holidays</h3>
                            <form onSubmit={handleCreateHoliday} style={{ marginBottom: "20px" }}>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Holiday Name</label>
                                        <input type="text" required value={holidayForm.name} onChange={(e) => setHolidayForm({ ...holidayForm, name: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Date</label>
                                        <input type="date" required value={holidayForm.date} onChange={(e) => setHolidayForm({ ...holidayForm, date: e.target.value })} />
                                    </div>
                                </div>
                                <button type="submit" className="primary-button">Add Holiday</button>
                            </form>
                            <div className="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Date</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {holidays.map((h) => (
                                            <tr key={h.id}>
                                                <td>{h.id}</td>
                                                <td>{h.name}</td>
                                                <td>{h.date}</td>
                                                <td>
                                                    <button className="danger-button" onClick={() => handleDeleteHoliday(h.id)}>Delete</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {/* WORKFLOW RULES TAB */}
                    {activeTab === "rules" && (
                        <section className="card">
                            <h3>Workflow Rules</h3>
                            <form onSubmit={handleCreateRule} style={{ marginBottom: "20px" }}>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Min Days</label>
                                        <input type="number" min="1" required value={ruleForm.minDays} onChange={(e) => setRuleForm({ ...ruleForm, minDays: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Max Days (Optional)</label>
                                        <input type="number" min="1" value={ruleForm.maxDays} onChange={(e) => setRuleForm({ ...ruleForm, maxDays: e.target.value })} />
                                    </div>
                                    <div className="form-group">
                                        <label>Approver Role</label>
                                        <select value={ruleForm.approverRole} onChange={(e) => setRuleForm({ ...ruleForm, approverRole: e.target.value })}>
                                            <option value="MANAGER">MANAGER</option>
                                            <option value="DEPARTMENT_HEAD">DEPARTMENT_HEAD</option>
                                            <option value="HR">HR</option>
                                            <option value="ADMIN">ADMIN</option>
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label>Approval Level</label>
                                        <input type="number" min="1" required value={ruleForm.approvalLevel} onChange={(e) => setRuleForm({ ...ruleForm, approvalLevel: e.target.value })} />
                                    </div>
                                </div>
                                <button type="submit" className="primary-button">Add Workflow Rule</button>
                            </form>
                            <div className="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Min Days</th>
                                            <th>Max Days</th>
                                            <th>Approver Role</th>
                                            <th>Approval Level</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {workflowRules.map((r) => (
                                            <tr key={r.id}>
                                                <td>{r.id}</td>
                                                <td>{r.minDays}</td>
                                                <td>{r.maxDays || "No limit"}</td>
                                                <td>{r.approverRole}</td>
                                                <td>{r.approvalLevel}</td>
                                                <td>
                                                    <button className="danger-button" onClick={() => handleDeleteRule(r.id)}>Delete</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}
                </>
            )}
        </div>
    );
}

export default Administration;
