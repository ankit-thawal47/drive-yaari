import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { tripAPI } from '../services/api';

const VehicleCard = ({ vehicle }) => {
  const { isLoggedIn, isRenter } = useAuth();
  const [renting, setRenting] = useState(false);
  const [message, setMessage] = useState('');
  const [pickupLocation, setPickupLocation] = useState('');
  const [dropLocation, setDropLocation] = useState('');

  const handleRent = async () => {
    if (!isLoggedIn) {
      setMessage('Please login to rent a vehicle');
      return;
    }

    if (!isRenter()) {
      setMessage('Only renters can rent vehicles');
      return;
    }

    if (!pickupLocation || !dropLocation) {
      setMessage('Please select both pickup and drop locations');
      return;
    }

    try {
      setRenting(true);
      setMessage('');
      
      const response = await tripAPI.rentVehicle(vehicle.licensePlate, {
        pickupLocation,
        dropLocation
      });
      setMessage('Vehicle rented successfully! Check your dashboard for trip details.');
      
      // Refresh the page after a delay to show updated vehicle status
      setTimeout(() => {
        window.location.reload();
      }, 2000);
      
    } catch (error) {
      setMessage(error.response?.data?.message || 'Failed to rent vehicle');
    } finally {
      setRenting(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'FREE': return 'var(--success-green)';
      case 'RENTED': return 'var(--warning-yellow)';
      case 'RESTING': return 'var(--gray-600)';
      case 'REPAIRING': return 'var(--error-red)';
      default: return 'var(--gray-600)';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'FREE': return 'Available';
      case 'RENTED': return 'Rented';
      case 'RESTING': return 'Not Available';
      case 'REPAIRING': return 'Under Maintenance';
      default: return status;
    }
  };

  return (
    <div className="vehicle-card">
      <div className="vehicle-image">
        🚗
      </div>
      
      <div className="vehicle-info">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px' }}>
          <h3 className="vehicle-title">
            {vehicle.make && vehicle.model ? `${vehicle.make} ${vehicle.model}` : vehicle.licensePlate}
            {vehicle.year && ` (${vehicle.year})`}
          </h3>
          <span 
            style={{ 
              color: getStatusColor(vehicle.status),
              fontWeight: 'bold',
              fontSize: '12px',
              textTransform: 'uppercase'
            }}
          >
            {getStatusText(vehicle.status)}
          </span>
        </div>

        {vehicle.isVerified && (
          <div style={{ marginBottom: '10px' }}>
            <span className="feature-tag" style={{ background: 'var(--success-green)', color: 'white' }}>
              ✓ Verified
            </span>
          </div>
        )}

        <div className="vehicle-features">
          <span className="feature-tag">
            {vehicle.vehicleType || 'STANDARD'}
          </span>
          {vehicle.transmission && (
            <span className="feature-tag">
              {vehicle.transmission}
            </span>
          )}
          {vehicle.seatingCapacity && (
            <span className="feature-tag">
              {vehicle.seatingCapacity} seats
            </span>
          )}
          {vehicle.color && (
            <span className="feature-tag">
              {vehicle.color}
            </span>
          )}
        </div>

        {vehicle.features && (
          <div style={{ marginBottom: '15px', fontSize: '14px', color: '#666' }}>
            Features: {vehicle.features}
          </div>
        )}

        {vehicle.description && (
          <div style={{ marginBottom: '15px', fontSize: '14px', color: '#666' }}>
            {vehicle.description}
          </div>
        )}

        <div style={{ marginBottom: '15px' }}>
          <div className="vehicle-price">
            From INR Rs.12/hour
          </div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            + Rs.0.45/km
          </div>
        </div>

        {message && (
          <div className={`alert ${message.includes('successfully') ? 'alert-success' : 'alert-error'}`}>
            {message}
          </div>
        )}

        {vehicle.status === 'FREE' && vehicle.isVerified && isLoggedIn && isRenter() ? (
          <div style={{ marginBottom: '15px' }}>
            <div style={{ marginBottom: '10px' }}>
              <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>
                Pickup Location:
              </label>
              <select 
                value={pickupLocation} 
                onChange={(e) => setPickupLocation(e.target.value)}
                style={{ 
                  width: '100%', 
                  padding: '8px', 
                  borderRadius: '4px', 
                  border: '1px solid #ddd',
                  fontSize: '14px'
                }}
              >
                <option value="">Select pickup location</option>
                <option value="Marina Bay Sands">Marina Bay Sands</option>
                <option value="Orchard Road">Orchard Road</option>
                <option value="Changi Airport">Changi Airport</option>
                <option value="Jurong East">Jurong East</option>
                <option value="Woodlands">Woodlands</option>
                <option value="Tampines">Tampines</option>
              </select>
            </div>
            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>
                Drop Location:
              </label>
              <select 
                value={dropLocation} 
                onChange={(e) => setDropLocation(e.target.value)}
                style={{ 
                  width: '100%', 
                  padding: '8px', 
                  borderRadius: '4px', 
                  border: '1px solid #ddd',
                  fontSize: '14px'
                }}
              >
                <option value="">Select drop location</option>
                <option value="Marina Bay Sands">Marina Bay Sands</option>
                <option value="Orchard Road">Orchard Road</option>
                <option value="Changi Airport">Changi Airport</option>
                <option value="Jurong East">Jurong East</option>
                <option value="Woodlands">Woodlands</option>
                <option value="Tampines">Tampines</option>
              </select>
            </div>
          </div>
        ) : null}

        {vehicle.status === 'FREE' && vehicle.isVerified ? (
          <button 
            onClick={handleRent}
            className="btn btn-primary"
            style={{ width: '100%' }}
            disabled={renting || !isLoggedIn || !isRenter() || !pickupLocation || !dropLocation}
          >
            {renting ? (
              <>
                <span className="loading-spinner" style={{ marginRight: '8px' }}></span>
                Renting...
              </>
            ) : (
              'Rent Now'
            )}
          </button>
        ) : (
          <div style={{ textAlign: 'center', color: '#666', fontStyle: 'italic' }}>
            {!vehicle.isVerified ? 'Pending verification' : 'Not available'}
          </div>
        )}

        <div style={{ marginTop: '10px', fontSize: '12px', color: '#999', textAlign: 'center' }}>
          License: {vehicle.licensePlate}
        </div>
      </div>
    </div>
  );
};

export default VehicleCard;