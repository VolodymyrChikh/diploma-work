import { createContext, useState, useEffect } from 'react';
import PropTypes from 'prop-types';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const authToken = localStorage.getItem('authToken');
        const userData = localStorage.getItem('userData');
        
        console.log("AuthContext init - token present:", !!token || !!authToken);
        
        if (token && !authToken) {
            localStorage.setItem('authToken', token);
        } else if (authToken && !token) {
            localStorage.setItem('token', authToken);
        }
        
        const activeToken = token || authToken;
        
        if (userData && activeToken) {
            try {
                const parsedUser = JSON.parse(userData);
                console.log("Using stored user data:", parsedUser);
                setUser(parsedUser);
                setIsAuthenticated(true);
                setLoading(false);
                return;
            } catch (e) {
                console.error("Failed to parse userData:", e);
            }
        }
        
        if (activeToken) {
            fetch('http://localhost:9000/users/me', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${activeToken}`,
                }
            })
            .then(response => {
                console.log("Auth check response:", response.status);
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Invalid token');
            })
            .then(userData => {
                console.log("User data received:", userData);
                setUser(userData);
                setIsAuthenticated(true);
            })
            .catch((err) => {
                console.error("Auth check error:", err);
                localStorage.removeItem('token');
                setIsAuthenticated(false);
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });
        } else {
            setIsAuthenticated(false);
            setUser(null);
            setLoading(false);
        }
    }, []);

    const login = async (email, password) => {
        try {
            console.log("Attempting login for:", email);
            
            const response = await fetch('http://localhost:9000/auth/authenticate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            console.log("Login response status:", response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error("Login error:", errorText);
                throw new Error(`Login failed: ${response.status} ${errorText}`);
            }

            const data = await response.json();
            console.log("Login successful, data received:", data);
            
            if (data.token && data.token.token) {
                localStorage.setItem('token', data.token.token);
                localStorage.setItem('authToken', data.token.token);
                localStorage.setItem('userData', JSON.stringify(data.user));
            } else if (data.token) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('authToken', data.token);
                
                if (data.user) {
                    localStorage.setItem('userData', JSON.stringify(data.user));
                }
            } else {
                console.error("No token in response:", data);
                throw new Error("No token received");
            }

            const tokenToUse = data.token && data.token.token ? data.token.token : data.token;
            
            const userResponse = await fetch('http://localhost:9000/users/me', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${tokenToUse}`,
                },
            });

            console.log("User data fetch status:", userResponse.status);
            
            if (!userResponse.ok) {
                const errorText = await userResponse.text();
                console.error("User data fetch error:", errorText);
                throw new Error(`Failed to get user data: ${userResponse.status}`);
            }

            const userData = await userResponse.json();
            console.log("User data received:", userData);
            
            setUser(userData);
            setIsAuthenticated(true);
            return { success: true };
        } catch (error) {
            console.error("Login process error:", error);
            return { success: false, error: error.message };
        }
    };

    const register = async (userData) => {
        try {
            const response = await fetch('http://localhost:9000/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });

            if (!response.ok) {
                throw new Error('Registration failed');
            }

            const data = await response.json();
            return { success: true, data };
        } catch (error) {
            return { success: false, error: error.message };
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('authToken');
        localStorage.removeItem('userData');
        setIsAuthenticated(false);
        setUser(null);
    };

    const getAuthInfo = () => {
        const token = localStorage.getItem('token');
        const authToken = localStorage.getItem('authToken');
        const userData = localStorage.getItem('userData');
        
        return {
            hasToken: !!token,
            hasAuthToken: !!authToken,
            hasUserData: !!userData,
            isAuthenticated
        };
    };
    
    return (
        <AuthContext.Provider
            value={{
                isAuthenticated,
                user,
                loading,
                login,
                register,
                logout,
                getAuthInfo
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

AuthProvider.propTypes = {
    children: PropTypes.node.isRequired,
};
