import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
    role: 'RENTER',
    phoneNumber: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
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

    const result = await register(formData);
    
    if (result.success) {
      navigate('/');
    } else {
      setError(result.message);
    }
    setLoading(false);
  };

  return (
    <div className="container" style={{ maxWidth: '400px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h2 style={{ textAlign: 'center', margin: 0 }}>Join DriveYaari</h2>
        </div>
        <div className="card-body">
          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="form-input"
                required
              />
            </div>

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
                minLength="6"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="tel"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
                className="form-input"
                placeholder="+65-1234-5678"
              />
            </div>

            <div className="form-group">
              <label className="form-label">I want to</label>
              <select
                name="role"
                value={formData.role}
                onChange={handleChange}
                className="form-select"
                required
              >
                <option value="RENTER">Rent vehicles</option>
                <option value="HOST">Host my vehicle</option>
              </select>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              style={{ width: '100%' }}
              disabled={loading}
            >
              {loading ? <span className="loading-spinner"></span> : 'Create Account'}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '20px' }}>
            <span>Already have an account? </span>
            <Link to="/login" style={{ color: 'var(--primary-yellow)' }}>
              Login here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;