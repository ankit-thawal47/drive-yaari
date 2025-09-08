import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { vehicleAPI } from '../services/api';

const AddVehicle = () => {
  const [formData, setFormData] = useState({
    licensePlate: '',
    make: '',
    model: '',
    year: '',
    color: '',
    vehicleType: 'STANDARD',
    transmission: 'AUTO',
    seatingCapacity: 5,
    features: '',
    description: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const { isHost } = useAuth();
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
    setSuccess('');

    try {
      await vehicleAPI.registerVehicle({
        ...formData,
        year: formData.year ? parseInt(formData.year) : null,
        seatingCapacity: parseInt(formData.seatingCapacity),
      });

      setSuccess('Vehicle registered successfully! It will be available for rent once verified.');
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to register vehicle');
    } finally {
      setLoading(false);
    }
  };

  if (!isHost()) {
    return (
      <div className="container" style={{ paddingTop: '50px' }}>
        <div className="alert alert-error">
          Only hosts can add vehicles. Please register as a host first.
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '600px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h2 style={{ margin: 0 }}>Add Your Vehicle</h2>
          <p style={{ margin: '10px 0 0 0', color: '#666' }}>
            Register your vehicle to start earning money by renting it out.
          </p>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">License Plate *</label>
                <input
                  type="text"
                  name="licensePlate"
                  value={formData.licensePlate}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="e.g., SBA1234X"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Vehicle Type *</label>
                <select
                  name="vehicleType"
                  value={formData.vehicleType}
                  onChange={handleChange}
                  className="form-select"
                  required
                >
                  <option value="ECONOMY">Economy ($8/hour)</option>
                  <option value="STANDARD">Standard ($12/hour)</option>
                  <option value="PREMIUM">Premium ($25/hour)</option>
                </select>
              </div>
            </div>

            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">Make</label>
                <input
                  type="text"
                  name="make"
                  value={formData.make}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="e.g., Toyota"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Model</label>
                <input
                  type="text"
                  name="model"
                  value={formData.model}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="e.g., Camry"
                />
              </div>
            </div>

            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">Year</label>
                <input
                  type="number"
                  name="year"
                  value={formData.year}
                  onChange={handleChange}
                  className="form-input"
                  min="2000"
                  max={new Date().getFullYear() + 1}
                  placeholder="e.g., 2020"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Color</label>
                <input
                  type="text"
                  name="color"
                  value={formData.color}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="e.g., White"
                />
              </div>
            </div>

            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">Transmission</label>
                <select
                  name="transmission"
                  value={formData.transmission}
                  onChange={handleChange}
                  className="form-select"
                >
                  <option value="AUTO">Automatic</option>
                  <option value="MANUAL">Manual</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Seating Capacity</label>
                <select
                  name="seatingCapacity"
                  value={formData.seatingCapacity}
                  onChange={handleChange}
                  className="form-select"
                >
                  <option value={2}>2 seats</option>
                  <option value={4}>4 seats</option>
                  <option value={5}>5 seats</option>
                  <option value={7}>7 seats</option>
                  <option value={8}>8+ seats</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Features</label>
              <input
                type="text"
                name="features"
                value={formData.features}
                onChange={handleChange}
                className="form-input"
                placeholder="e.g., GPS, Bluetooth, USB charging"
              />
              <small style={{ color: '#666' }}>Separate multiple features with commas</small>
            </div>

            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                className="form-input"
                rows="3"
                placeholder="Tell renters about your vehicle..."
              />
            </div>

            <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: 'var(--light-yellow)', borderRadius: 'var(--border-radius)' }}>
              <h4 style={{ margin: '0 0 10px 0' }}>What happens next?</h4>
              <ul style={{ margin: 0, paddingLeft: '20px' }}>
                <li>Your vehicle will be reviewed for verification</li>
                <li>Once verified, it will be available for rent</li>
                <li>You'll earn money when renters book your vehicle</li>
                <li>All bookings are covered by comprehensive insurance</li>
              </ul>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              style={{ width: '100%' }}
              disabled={loading}
            >
              {loading ? <span className="loading-spinner"></span> : 'Register Vehicle'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddVehicle;