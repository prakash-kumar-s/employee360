import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

    const [user, setUser] = useState(() => {

        const storedUser =
            localStorage.getItem("employee360_user");

        if (!storedUser) {
            return null;
        }

        try {
            return JSON.parse(storedUser);
        } catch {
            localStorage.removeItem("employee360_user");
            localStorage.removeItem("employee360_token");

            return null;
        }
    });

    const login = (loginResponse) => {

        const loggedInUser = {
            id: loginResponse.userId,
            userId: loginResponse.userId,
            username: loginResponse.username,
            name: loginResponse.name,
            role: loginResponse.role,
        };

        localStorage.setItem(
            "employee360_token",
            loginResponse.token
        );

        localStorage.setItem(
            "employee360_user",
            JSON.stringify(loggedInUser)
        );

        setUser(loggedInUser);
    };

    const logout = () => {

        localStorage.removeItem(
            "employee360_token"
        );

        localStorage.removeItem(
            "employee360_user"
        );

        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                logout,
                isAuthenticated: user !== null,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

// The hook shares the auth context with AuthProvider by design.
// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    return useContext(AuthContext);
}