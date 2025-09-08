import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tripAPI } from '../services/api';

const TripCancel = () => {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [formData, setFormData] = useState({
    reason: '',
    additionalNotes: '',
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
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await tripAPI.cancelTrip(tripId, {
        reason: formData.reason + (formData.additionalNotes ? `\nNotes: ${formData.additionalNotes}` : '')
      });
      
      navigate('/dashboard', { 
        state: { message: 'Trip cancelled successfully. Your payment will be refunded according to our cancellation policy.' }
      });
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to cancel trip');
    } finally {
      setSubmitting(false);
    }
  };

  const canCancel = () => {
    return trip && (trip.status === 'PENDING' || trip.status === 'CONFIRMED');
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

  if (!canCancel()) {
    return (
      <div className="container" style={{ paddingTop: '50px' }}>
        <div className="alert alert-error">
          This trip cannot be cancelled. Current status: {trip.status}
          <div style={{ marginTop: '10px' }}>
            <button 
              onClick={() => navigate('/dashboard')}
              className="btn btn-secondary"
            >
              Back to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '600px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h1 style={{ margin: 0, color: 'var(--error-red)' }}>Cancel Trip</h1>
          <p style={{ margin: '10px 0 0 0', color: '#666' }}>
            Are you sure you want to cancel this trip? This action cannot be undone.
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
                <p><strong>Booked:</strong> {new Date(trip.dateOfBookingEpoch).toLocaleString()}</p>
              </div>
              <div>
                <p><strong>Total Amount:</strong> SGD ${trip.totalAmount?.toFixed(2) || 'N/A'}</p>
                <p><strong>Security Deposit:</strong> SGD ${trip.securityDeposit?.toFixed(2) || 'N/A'}</p>
                <p><strong>Planned Start:</strong> {new Date(trip.plannedStartTimeEpoch).toLocaleString()}</p>
                <p><strong>Planned End:</strong> {new Date(trip.plannedEndTimeEpoch).toLocaleString()}</p>
              </div>
            </div>
          </div>

          {/* Cancellation Policy */}
          <div style={{ marginBottom: '30px', padding: '15px', backgroundColor: 'var(--light-yellow)', borderRadius: 'var(--border-radius)' }}>
            <h4 style={{ margin: '0 0 10px 0' }}>Cancellation Policy:</h4>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>Free cancellation up to 24 hours before trip start</li>
              <li>50% refund for cancellations within 24 hours</li>
              <li>No refund for cancellations after trip has started</li>
              <li>Security deposit will be fully refunded</li>
            </ul>
          </div>

          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Cancellation Reason *</label>
              <select
                name="reason"
                value={formData.reason}
                onChange={handleChange}
                className="form-select"
                required
              >
                <option value="">Select a reason</option>
                <option value="Change of plans">Change of plans</option>
                <option value="Found alternative transportation">Found alternative transportation</option>
                <option value="Vehicle no longer needed">Vehicle no longer needed</option>
                <option value="Emergency situation">Emergency situation</option>
                <option value="Pricing concerns">Pricing concerns</option>
                <option value="Vehicle condition concerns">Vehicle condition concerns</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Additional Notes</label>
              <textarea
                name="additionalNotes"
                value={formData.additionalNotes}
                onChange={handleChange}
                className="form-input"
                rows="3"
                placeholder="Please provide any additional details about your cancellation..."
              />
            </div>

            <div style={{ display: 'flex', gap: '15px', marginTop: '30px' }}>
              <button
                type="button"
                onClick={() => navigate('/dashboard')}
                className="btn btn-secondary"
                style={{ flex: 1 }}
              >
                Keep Trip
              </button>
              <button
                type="submit"
                className="btn btn-danger"
                style={{ flex: 1 }}
                disabled={submitting || !formData.reason}
              >
                {submitting ? (
                  <>
                    <span className="loading-spinner" style={{ marginRight: '8px' }}></span>
                    Cancelling...
                  </>
                ) : (
                  'Cancel Trip'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default TripCancel;