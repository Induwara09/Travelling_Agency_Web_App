import { createContext, useContext, useMemo, useState } from 'react';
import { authApi } from '../services/api';

const AuthContext = createContext(null);
const USER_KEY = 'travel-user';
const TOKEN_KEY = 'travel-token';

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);

  const saveSession = (data) => {
    const sessionUser = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role
    };
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(USER_KEY, JSON.stringify(sessionUser));
    setUser(sessionUser);
    return sessionUser;
  };

  const login = async (credentials) => saveSession((await authApi.login(credentials)).data);
  const register = async (details) => saveSession((await authApi.register(details)).data);
  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  };

  const value = useMemo(() => ({
    user,
    token: localStorage.getItem(TOKEN_KEY),
    isAuthenticated: Boolean(user && localStorage.getItem(TOKEN_KEY)),
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout
  }), [user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
