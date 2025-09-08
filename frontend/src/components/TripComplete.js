import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tripAPI } from '../services/api';

const TripComplete = () => {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [formData, setFormData] = useState({
    endOdometerReading: '',
    notes: '',
    hasVehicleIssues: false,
    issueDescription: '',
    fuelLevel: '1.0',
    requiresCleaning: false,
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
      await tripAPI.completeTrip(tripId, {
        ...formData,
        endOdometerReading: parseInt(formData.endOdometerReading),
        fuelLevel: parseFloat(formData.fuelLevel)
      });
      
      navigate('/dashboard', { 
        state: { message: 'Trip completed successfully! Your security deposit will be refunded shortly.' }
      });
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to complete trip');
    } finally {
      setSubmitting(false);
    }
  };

  const calculateDistance = () => {
    if (trip?.startOdometerReading && formData.endOdometerReading) {
      return parseInt(formData.endOdometerReading) - trip.startOdometerReading;
    }
    return null;
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

  const distance = calculateDistance();

  return (
    <div className="container" style={{ maxWidth: '600px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h1 style={{ margin: 0 }}>Complete Your Trip</h1>
          <p style={{ margin: '10px 0 0 0', color: '#666' }}>
            Please inspect the vehicle and record the final odometer reading to complete your trip.
          </p>
        </div>
        <div className="card-body">
          {/* Trip Details */}
          <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--border-radius)' }}>
            <h3 style={{ margin: '0 0 15px 0' }}>Trip Summary</h3>
            <div className="grid grid-2">
              <div>
                <p><strong>Trip ID:</strong> {trip.id}</p>
                <p><strong>Vehicle:</strong> {trip.vehicleId}</p>
                <p><strong>Started:</strong> {new Date(trip.actualStartTimeEpoch).toLocaleString()}</p>
                <p><strong>Starting Odometer:</strong> {trip.startOdometerReading?.toLocaleString()} km</p>
              </div>
              <div>
                <p><strong>Total Amount:</strong> SGD ${trip.totalAmount?.toFixed(2) || 'N/A'}</p>
                <p><strong>Security Deposit:</strong> SGD ${trip.securityDeposit?.toFixed(2) || 'N/A'}</p>
                {distance && (
                  <p><strong>Distance Traveled:</strong> {distance.toLocaleString()} km</p>
                )}
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
              <label className="form-label">Ending Odometer Reading *</label>
              <input
                type="number"
                name="endOdometerReading"
                value={formData.endOdometerReading}
                onChange={handleChange}
                className="form-input"
                placeholder="e.g., 12650"
                required
                min={trip.startOdometerReading || 0}
              />
              <small style={{ color: '#666' }}>
                Must be greater than or equal to starting reading: {trip.startOdometerReading?.toLocaleString() || 'N/A'} km
              </small>
              {distance && (
                <div style={{ marginTop: '5px', padding: '10px', backgroundColor: 'var(--light-yellow)', borderRadius: '4px' }}>
                  <strong>Distance: {distance.toLocaleString()} km</strong>
                </div>
              )}
            </div>

            <div className="form-group">
              <label className="form-label">Fuel Level</label>
              <select
                name="fuelLevel"
                value={formData.fuelLevel}
                onChange={handleChange}
                className="form-select"
              >
                <option value="1.0">Full (100%)</option>
                <option value="0.75">3/4 Tank (75%)</option>
                <option value="0.5">Half Tank (50%)</option>
                <option value="0.25">1/4 Tank (25%)</option>
                <option value="0.1">Nearly Empty (10%)</option>
              </select>
            </div>

            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input
                  type="checkbox"
                  name="requiresCleaning"
                  checked={formData.requiresCleaning}
                  onChange={handleChange}
                />
                <span className="form-label" style={{ margin: 0 }}>
                  Vehicle requires cleaning
                </span>
              </label>
              <small style={{ color: '#666' }}>
                Check this if the vehicle needs interior/exterior cleaning
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
                  placeholder="Describe any new scratches, dents, or other issues..."
                  required={formData.hasVehicleIssues}
                />
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Trip Feedback</label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                className="form-input"
                rows="2"
                placeholder="How was your trip? Any feedback for the host..."
              />
            </div>

            <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: 'var(--light-yellow)', borderRadius: 'var(--border-radius)' }}>
              <h4 style={{ margin: '0 0 10px 0' }}>What happens next:</h4>
              <ul style={{ margin: 0, paddingLeft: '20px' }}>
                <li>Your security deposit will be processed for refund</li>
                <li>Final charges will be calculated based on actual usage</li>
                <li>You'll receive a trip summary via email</li>
                <li>You can rate and review the host</li>
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
                    Completing Trip...
                  </>
                ) : (
                  'Complete Trip'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default TripComplete;