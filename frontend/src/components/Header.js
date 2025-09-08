import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { user, logout, isLoggedIn, isHost } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo">
            🚗 DriveYaari
          </Link>
          
          <nav className="nav-links">
            {!isLoggedIn ? (
              <>
                <Link to="/become-host" className="nav-link">Become a Host</Link>
                <Link to="/login" className="btn btn-secondary">Login</Link>
                <Link to="/register" className="btn btn-primary">Sign Up</Link>
              </>
            ) : (
              <>
                <Link to="/dashboard" className="nav-link">
                  {isHost() ? 'Host Dashboard' : 'My Trips'}
                </Link>
                {isHost() && (
                  <Link to="/add-vehicle" className="nav-link">Add Vehicle</Link>
                )}
                <div className="nav-link">
                  Hello, {user.name}
                </div>
                <button 
                  onClick={handleLogout}
                  className="btn btn-secondary"
                >
                  Logout
                </button>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;