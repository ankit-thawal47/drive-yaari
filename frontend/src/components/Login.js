import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    const result = await login(formData);
    
    if (result.success) {
      navigate('/');
    } else {
      setError(result.message);
    }
    setLoading(false);
  };

  // Pre-fill demo accounts
  const fillDemoAccount = (type) => {
    const demoAccounts = {
      host: { email: 'host@drivelah.com', password: 'password123' },
      renter: { email: 'renter@drivelah.com', password: 'password123' },
      admin: { email: 'admin@drivelah.com', password: 'admin123' }
    };
    setFormData(demoAccounts[type]);
  };

  return (
    <div className="container" style={{ maxWidth: '400px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h2 style={{ textAlign: 'center', margin: 0 }}>Login to DriveYaari</h2>
        </div>
        <div className="card-body">
          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          {/* Demo Account Buttons */}
          <div style={{ marginBottom: '20px', textAlign: 'center' }}>
            <p style={{ marginBottom: '10px', color: '#666', fontSize: '14px' }}>
              Quick Demo Login:
            </p>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', flexWrap: 'wrap' }}>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => fillDemoAccount('host')}
                style={{ fontSize: '12px', padding: '6px 12px' }}
              >
                Host Demo
              </button>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => fillDemoAccount('renter')}
                style={{ fontSize: '12px', padding: '6px 12px' }}
              >
                Renter Demo
              </button>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => fillDemoAccount('admin')}
                style={{ fontSize: '12px', padding: '6px 12px' }}
              >
                Admin Demo
              </button>
            </div>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="form-input"
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className="form-input"
                required
              />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              style={{ width: '100%' }}
              disabled={loading}
            >
              {loading ? <span className="loading-spinner"></span> : 'Login'}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '20px' }}>
            <span>Don't have an account? </span>
            <Link to="/register" style={{ color: 'var(--primary-yellow)' }}>
              Sign up here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;