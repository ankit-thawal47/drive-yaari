import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { tripAPI } from '../services/api';

const Dashboard = () => {
  const { user, isHost, isRenter } = useAuth();
  const [activeTrip, setActiveTrip] = useState(null);
  const [allTrips, setAllTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTrips();
  }, []);

  const loadTrips = async () => {
    try {
      setLoading(true);
      const response = await tripAPI.getMyTrips();
      const trips = response.data || [];
      setAllTrips(trips);
      
      // Find active trip (IN_PROGRESS or PENDING)
      const active = trips.find(trip => 
        trip.status === 'IN_PROGRESS' || trip.status === 'PENDING'
      );
      setActiveTrip(active);
    } catch (error) {
      if (error.response?.status !== 404) {
        setError('Failed to load trips');
      }
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const formatPrice = (amount) => {
    return amount ? `SGD $${amount.toFixed(2)}` : 'N/A';
  };

  const getStatusBadge = (status) => {
    const statusClass = `status-badge status-${status?.toLowerCase()}`;
    return <span className={statusClass}>{status}</span>;
  };

  const getLocationName = (location) => {
    if (!location || !location.lat || !location.lon) return null;
    
    const lat = location.lat;
    const lon = location.lon;
    
    // Map coordinates back to location names (approximate matching)
    if (Math.abs(lat - 1.2834) < 0.01 && Math.abs(lon - 103.8607) < 0.01) return 'Marina Bay Sands';
    if (Math.abs(lat - 1.3048) < 0.01 && Math.abs(lon - 103.8318) < 0.01) return 'Orchard Road';
    if (Math.abs(lat - 1.3644) < 0.01 && Math.abs(lon - 103.9915) < 0.01) return 'Changi Airport';
    if (Math.abs(lat - 1.3329) < 0.01 && Math.abs(lon - 103.7436) < 0.01) return 'Jurong East';
    if (Math.abs(lat - 1.4382) < 0.01 && Math.abs(lon - 103.7890) < 0.01) return 'Woodlands';
    if (Math.abs(lat - 1.3496) < 0.01 && Math.abs(lon - 103.9568) < 0.01) return 'Tampines';
    
    return `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
  };

  if (loading) {
    return (
      <div className="container" style={{ paddingTop: '50px', textAlign: 'center' }}>
        <div className="loading-spinner" style={{ width: '40px', height: '40px' }}></div>
        <p style={{ marginTop: '20px' }}>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="container" style={{ paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h1 style={{ margin: 0 }}>
            Welcome back, {user.name}!
          </h1>
          <p style={{ margin: '5px 0 0 0', color: '#666' }}>
            {isHost() ? 'Host Dashboard' : 'Renter Dashboard'} • 
            <span style={{ 
              color: user.isVerified ? 'var(--success-green)' : 'var(--warning-yellow)',
              fontWeight: 'bold',
              marginLeft: '5px'
            }}>
              {user.isVerified ? '✓ Verified' : '⚠ Unverified'}
            </span>
          </p>
        </div>
      </div>

      {error && (
        <div className="alert alert-error" style={{ marginTop: '20px' }}>
          {error}
        </div>
      )}

      {/* Active Trip */}
      {activeTrip && (
        <div className="card" style={{ marginTop: '20px' }}>
          <div className="card-header">
            <h2 style={{ margin: 0 }}>Active Trip</h2>
          </div>
          <div className="card-body">
            <div className="grid grid-2">
              <div>
                <p><strong>Trip ID:</strong> {activeTrip.id}</p>
                <p><strong>Status:</strong> {getStatusBadge(activeTrip.status)}</p>
                <p><strong>Vehicle:</strong> {activeTrip.vehicleId}</p>
                {isRenter() && <p><strong>Host:</strong> {activeTrip.ownerId}</p>}
                {isHost() && <p><strong>Renter:</strong> {activeTrip.renterId}</p>}
              </div>
              <div>
                <p><strong>Started:</strong> {activeTrip.actualStartTimeEpoch ? formatDate(activeTrip.actualStartTimeEpoch) : 'Not started'}</p>
                <p><strong>Planned End:</strong> {formatDate(activeTrip.plannedEndTimeEpoch)}</p>
                <p><strong>Total Amount:</strong> {formatPrice(activeTrip.totalAmount)}</p>
                <p><strong>Security Deposit:</strong> {formatPrice(activeTrip.securityDeposit)}</p>
              </div>
            </div>

            {isRenter() && activeTrip.status === 'PENDING' && (
              <div style={{ marginTop: '20px' }}>
                <Link 
                  to={`/trip/${activeTrip.id}/start`}
                  className="btn btn-success"
                  style={{ marginRight: '10px' }}
                >
                  Start Trip
                </Link>
                <Link 
                  to={`/trip/${activeTrip.id}/cancel`}
                  className="btn btn-danger"
                >
                  Cancel Trip
                </Link>
              </div>
            )}

            {isRenter() && activeTrip.status === 'IN_PROGRESS' && (
              <div style={{ marginTop: '20px' }}>
                <Link 
                  to={`/trip/${activeTrip.id}/complete`}
                  className="btn btn-primary"
                >
                  Complete Trip
                </Link>
              </div>
            )}

            {activeTrip.status === 'COMPLETED' && (
              <div style={{ marginTop: '20px' }}>
                <Link 
                  to={`/trip/${activeTrip.id}/rating`}
                  className="btn btn-primary"
                >
                  {activeTrip.renterRating || activeTrip.ownerRating ? 'View Rating' : 'Submit Rating'}
                </Link>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Trip History */}
      {allTrips.length > 0 && (
        <div className="card" style={{ marginTop: '20px' }}>
          <div className="card-header">
            <h2 style={{ margin: 0 }}>My Trips</h2>
            <p style={{ margin: '5px 0 0 0', color: '#666' }}>
              Your rental history and current bookings
            </p>
          </div>
          <div className="card-body">
            {allTrips.map((trip, index) => (
              <div key={trip.id} style={{ 
                marginBottom: index === allTrips.length - 1 ? 0 : '20px',
                padding: '20px', 
                border: '1px solid var(--gray-200)', 
                borderRadius: 'var(--border-radius)',
                backgroundColor: trip.status === 'PENDING' || trip.status === 'IN_PROGRESS' ? 'var(--light-yellow)' : 'white'
              }}>
                <div className="grid grid-2">
                  <div>
                    <p><strong>Trip ID:</strong> {trip.id}</p>
                    <p><strong>Vehicle:</strong> {trip.vehicleId}</p>
                    <p><strong>Status:</strong> {getStatusBadge(trip.status)}</p>
                    <p><strong>Booked:</strong> {formatDate(trip.dateOfBookingEpoch)}</p>
                    {trip.pickUpLocation && (
                      <p><strong>Pickup:</strong> {getLocationName(trip.pickUpLocation)}</p>
                    )}
                    {trip.dropLocation && (
                      <p><strong>Drop:</strong> {getLocationName(trip.dropLocation)}</p>
                    )}
                  </div>
                  <div>
                    <p><strong>Duration:</strong> {trip.plannedDurationHours}h</p>
                    <p><strong>Total:</strong> {formatPrice(trip.totalAmount)}</p>
                    <p><strong>Deposit:</strong> {formatPrice(trip.securityDeposit)}</p>
                    {trip.actualStartTimeEpoch && (
                      <p><strong>Started:</strong> {formatDate(trip.actualStartTimeEpoch)}</p>
                    )}
                    {trip.actualEndTimeEpoch && (
                      <p><strong>Completed:</strong> {formatDate(trip.actualEndTimeEpoch)}</p>
                    )}
                  </div>
                </div>
                
                {/* Trip Actions */}
                <div style={{ marginTop: '15px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                  {trip.status === 'PENDING' && isRenter() && (
                    <>
                      <Link 
                        to={`/trip/${trip.id}/start`}
                        className="btn btn-success btn-sm"
                      >
                        Start Trip
                      </Link>
                      <Link 
                        to={`/trip/${trip.id}/cancel`}
                        className="btn btn-danger btn-sm"
                      >
                        Cancel
                      </Link>
                    </>
                  )}
                  
                  {trip.status === 'IN_PROGRESS' && isRenter() && (
                    <Link 
                      to={`/trip/${trip.id}/complete`}
                      className="btn btn-primary btn-sm"
                    >
                      Complete Trip
                    </Link>
                  )}
                  
                  {trip.status === 'COMPLETED' && (
                    <Link 
                      to={`/trip/${trip.id}/rating`}
                      className="btn btn-secondary btn-sm"
                    >
                      {trip.renterRating || trip.ownerRating ? 'View Rating' : 'Rate Trip'}
                    </Link>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Quick Actions */}
      <div className="card" style={{ marginTop: '20px' }}>
        <div className="card-header">
          <h2 style={{ margin: 0 }}>Quick Actions</h2>
        </div>
        <div className="card-body">
          <div className="grid grid-3">
            {isHost() ? (
              <>
                <Link to="/add-vehicle" className="btn btn-primary" style={{ textAlign: 'center', padding: '20px' }}>
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>🚗</div>
                  <div>Add Vehicle</div>
                </Link>
                <div className="btn btn-secondary" style={{ textAlign: 'center', padding: '20px', opacity: 0.7 }}>
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>📊</div>
                  <div>View Earnings</div>
                  <small style={{ display: 'block', marginTop: '5px' }}>Coming soon</small>
                </div>
                <div className="btn btn-secondary" style={{ textAlign: 'center', padding: '20px', opacity: 0.7 }}>
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>🚙</div>
                  <div>My Vehicles</div>
                  <small style={{ display: 'block', marginTop: '5px' }}>Coming soon</small>
                </div>
              </>
            ) : (
              <>
                <Link to="/" className="btn btn-primary" style={{ textAlign: 'center', padding: '20px' }}>
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>🔍</div>
                  <div>Browse Vehicles</div>
                </Link>
                <div 
                  onClick={() => document.querySelector('.card:nth-of-type(3)')?.scrollIntoView({ behavior: 'smooth' })}
                  className="btn btn-secondary" 
                  style={{ textAlign: 'center', padding: '20px', cursor: 'pointer' }}
                >
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>📱</div>
                  <div>Trip History</div>
                  <small style={{ display: 'block', marginTop: '5px' }}>{allTrips.length} trips</small>
                </div>
                <div className="btn btn-secondary" style={{ textAlign: 'center', padding: '20px', opacity: 0.7 }}>
                  <div style={{ fontSize: '24px', marginBottom: '10px' }}>⭐</div>
                  <div>My Reviews</div>
                  <small style={{ display: 'block', marginTop: '5px' }}>Coming soon</small>
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Account Status */}
      {!user.isVerified && (
        <div className="card" style={{ marginTop: '20px' }}>
          <div className="card-header">
            <h2 style={{ margin: 0, color: 'var(--warning-yellow)' }}>Account Verification Needed</h2>
          </div>
          <div className="card-body">
            <p>Your account is not yet verified. To start {isHost() ? 'hosting vehicles' : 'renting vehicles'}, please complete the verification process.</p>
            <div style={{ marginTop: '15px' }}>
              <strong>Verification Requirements:</strong>
              <ul style={{ marginTop: '10px', paddingLeft: '20px' }}>
                <li>Valid government ID</li>
                <li>Proof of address</li>
                {isHost() && <li>Vehicle registration documents</li>}
                <li>Phone number verification</li>
              </ul>
            </div>
            <div className="alert alert-info" style={{ marginTop: '15px' }}>
              <strong>Note:</strong> This is a demo system. In production, you would upload documents for manual review.
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;