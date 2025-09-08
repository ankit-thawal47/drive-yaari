import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check if user is logged in on app start
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('user');
    
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      if (response.data.success) {
        const { token, user: userData } = response.data;
        localStorage.setItem('authToken', token);
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);
        return { success: true };
      } else {
        return { success: false, message: response.data.message };
      }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Login failed' 
      };
    }
  };

  const register = async (userData) => {
    try {
      const response = await authAPI.register(userData);
      if (response.data.success) {
        const { token, user: newUser } = response.data;
        localStorage.setItem('authToken', token);
        localStorage.setItem('user', JSON.stringify(newUser));
        setUser(newUser);
        return { success: true };
      } else {
        return { success: false, message: response.data.message };
      }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Registration failed' 
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isHost = () => user?.role === 'HOST';
  const isRenter = () => user?.role === 'RENTER';
  const isAdmin = () => user?.role === 'ADMIN';

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isLoggedIn: !!user,
    isHost,
    isRenter,
    isAdmin,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};