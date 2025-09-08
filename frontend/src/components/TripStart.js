import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tripAPI } from '../services/api';

const TripStart = () => {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [formData, setFormData] = useState({
    startOdometerReading: '',
    notes: '',
    hasVehicleIssues: false,
    issueDescription: '',
  });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTrip();
  }, [tripId]);

  const loadTrip = async () => {
    try {
      const response = await tripAPI.getTripById(tripId);
      setTrip(response.data);
    } catch (error) {
      setError('Failed to load trip details');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await tripAPI.startTrip(tripId, {
        ...formData,
        startOdometerReading: parseInt(formData.startOdometerReading)
      });
      
      navigate('/dashboard', { 
        state: { message: 'Trip started successfully!' }
      });
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to start trip');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ paddingTop: '50px', textAlign: 'center' }}>
        <div className="loading-spinner" style={{ width: '40px', height: '40px' }}></div>
        <p style={{ marginTop: '20px' }}>Loading trip details...</p>
      </div>
    );
  }

  if (!trip) {
    return (
      <div className="container" style={{ paddingTop: '50px' }}>
        <div className="alert alert-error">
          Trip not found or you don't have permission to view it.
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '600px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h1 style={{ margin: 0 }}>Start Your Trip</h1>
          <p style={{ margin: '10px 0 0 0', color: '#666' }}>
            Please inspect the vehicle and record the odometer reading before starting your trip.
          </p>
        </div>
        <div className="card-body">
          {/* Trip Details */}
          <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--border-radius)' }}>
            <h3 style={{ margin: '0 0 15px 0' }}>Trip Details</h3>
            <div className="grid grid-2">
              <div>
                <p><strong>Trip ID:</strong> {trip.id}</p>
                <p><strong>Vehicle:</strong> {trip.vehicleId}</p>
                <p><strong>Status:</strong> <span className={`status-badge status-${trip.status.toLowerCase()}`}>{trip.status}</span></p>
              </div>
              <div>
                <p><strong>Total Amount:</strong> SGD ${trip.totalAmount?.toFixed(2) || 'N/A'}</p>
                <p><strong>Security Deposit:</strong> SGD ${trip.securityDeposit?.toFixed(2) || 'N/A'}</p>
                <p><strong>Planned End:</strong> {new Date(trip.plannedEndTimeEpoch).toLocaleString()}</p>
              </div>
            </div>
          </div>

          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Starting Odometer Reading *</label>
              <input
                type="number"
                name="startOdometerReading"
                value={formData.startOdometerReading}
                onChange={handleChange}
                className="form-input"
                placeholder="e.g., 12500"
                required
                min="0"
              />
              <small style={{ color: '#666' }}>
                Record the exact odometer reading from the vehicle dashboard
              </small>
            </div>

            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input
                  type="checkbox"
                  name="hasVehicleIssues"
                  checked={formData.hasVehicleIssues}
                  onChange={handleChange}
                />
                <span className="form-label" style={{ margin: 0 }}>
                  Report vehicle issues
                </span>
              </label>
            </div>

            {formData.hasVehicleIssues && (
              <div className="form-group">
                <label className="form-label">Issue Description *</label>
                <textarea
                  name="issueDescription"
                  value={formData.issueDescription}
                  onChange={handleChange}
                  className="form-input"
                  rows="3"
                  placeholder="Describe any scratches, dents, or other issues you notice..."
                  required={formData.hasVehicleIssues}
                />
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Additional Notes</label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                className="form-input"
                rows="2"
                placeholder="Any additional comments about the vehicle pickup..."
              />
            </div>

            <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: 'var(--light-yellow)', borderRadius: 'var(--border-radius)' }}>
              <h4 style={{ margin: '0 0 10px 0' }}>Before you start:</h4>
              <ul style={{ margin: 0, paddingLeft: '20px' }}>
                <li>Check the vehicle for any existing damage</li>
                <li>Ensure you have the keys and necessary documents</li>
                <li>Take photos of the vehicle condition (recommended)</li>
                <li>Record the exact odometer reading</li>
                <li>Check fuel level and tire condition</li>
              </ul>
            </div>

            <div style={{ display: 'flex', gap: '15px' }}>
              <button
                type="button"
                onClick={() => navigate('/dashboard')}
                className="btn btn-secondary"
                style={{ flex: 1 }}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-success"
                style={{ flex: 2 }}
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="loading-spinner" style={{ marginRight: '8px' }}></span>
                    Starting Trip...
                  </>
                ) : (
                  'Start Trip'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default TripStart;