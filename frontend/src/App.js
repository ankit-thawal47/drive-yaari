import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Header from './components/Header';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/Home';
import Dashboard from './components/Dashboard';
import AddVehicle from './components/AddVehicle';
import TripStart from './components/TripStart';
import TripComplete from './components/TripComplete';
import TripCancel from './components/TripCancel';
import BecomeHost from './components/BecomeHost';
import './styles.css';

const ProtectedRoute = ({ children }) => {
  const { isLoggedIn } = useAuth();
  return isLoggedIn ? children : <Navigate to="/login" />;
};

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Header />
          <main>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/become-host" element={<BecomeHost />} />
              
              <Route path="/dashboard" element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              } />
              
              <Route path="/add-vehicle" element={
                <ProtectedRoute>
                  <AddVehicle />
                </ProtectedRoute>
              } />
              
              <Route path="/trip/:tripId/start" element={
                <ProtectedRoute>
                  <TripStart />
                </ProtectedRoute>
              } />
              
              <Route path="/trip/:tripId/complete" element={
                <ProtectedRoute>
                  <TripComplete />
                </ProtectedRoute>
              } />
              
              <Route path="/trip/:tripId/cancel" element={
                <ProtectedRoute>
                  <TripCancel />
                </ProtectedRoute>
              } />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;