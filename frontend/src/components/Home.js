import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { vehicleAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import VehicleCard from './VehicleCard';

const Home = () => {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { isLoggedIn, isRenter } = useAuth();

  useEffect(() => {
    loadVehicles();
  }, []);

  const loadVehicles = async () => {
    try {
      setLoading(true);
      const response = await vehicleAPI.getVehicles({
        pickUpLocation: { latitude: 1.3521, longitude: 103.8198 }, // India center
        dropLocation: { latitude: 1.3521, longitude: 103.8198 }
      });
      
      if (response.data.success) {
        setVehicles(response.data.vehicles);
      } else {
        setError(response.data.message);
      }
    } catch (error) {
      setError('Failed to load vehicles');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Hero Section */}
      <section className="hero">
        <div className="container">
          <h1>Rent your neighbor's car</h1>
          <p>
            Discover and book unique vehicles from verified hosts in India.
            Safe, convenient, and affordable car sharing.
          </p>
          {!isLoggedIn && (
            <div style={{ display: 'flex', gap: '20px', justifyContent: 'center' }}>
              <Link to="/register" className="btn btn-primary" style={{ fontSize: '18px', padding: '15px 30px' }}>
                Get Started
              </Link>
              <Link to="/become-host" className="btn btn-secondary" style={{ fontSize: '18px', padding: '15px 30px', color: 'white', borderColor: 'white' }}>
                Become a Host
              </Link>
            </div>
          )}
        </div>
      </section>

      {/* Vehicle Listings */}
      <section style={{ padding: '60px 0' }}>
        <div className="container">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' }}>
            <h2 style={{ fontSize: '32px', margin: 0 }}>Available Vehicles</h2>
            <button 
              onClick={loadVehicles}
              className="btn btn-secondary"
              disabled={loading}
            >
              {loading ? <span className="loading-spinner"></span> : 'Refresh'}
            </button>
          </div>

          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <div className="loading-spinner" style={{ width: '40px', height: '40px' }}></div>
              <p style={{ marginTop: '20px', color: '#666' }}>Loading vehicles...</p>
            </div>
          ) : vehicles.length === 0 ? (
            <div className="card">
              <div className="card-body" style={{ textAlign: 'center', padding: '60px' }}>
                <h3 style={{ color: '#666', marginBottom: '20px' }}>No vehicles available</h3>
                <p style={{ color: '#999', marginBottom: '30px' }}>
                  There are no vehicles available for rent at the moment.
                </p>
                {isLoggedIn && !isRenter() && (
                  <Link to="/add-vehicle" className="btn btn-primary">
                    Add Your Vehicle
                  </Link>
                )}
              </div>
            </div>
          ) : (
            <div className="grid grid-3">
              {vehicles.map((vehicle) => (
                <VehicleCard key={vehicle.id} vehicle={vehicle} />
              ))}
            </div>
          )}

          {!isLoggedIn && vehicles.length > 0 && (
            <div style={{ textAlign: 'center', marginTop: '40px' }}>
              <div className="alert alert-info">
                <strong>Want to rent a vehicle?</strong> Please{' '}
                <Link to="/login" style={{ color: 'inherit', textDecoration: 'underline' }}>
                  login
                </Link>{' '}
                or{' '}
                <Link to="/register" style={{ color: 'inherit', textDecoration: 'underline' }}>
                  create an account
                </Link>{' '}
                to start renting.
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Features Section */}
      <section style={{ backgroundColor: 'var(--gray-50)', padding: '60px 0' }}>
        <div className="container">
          <h2 style={{ textAlign: 'center', fontSize: '32px', marginBottom: '50px' }}>
            Why Choose DriveYaari?
          </h2>
          
          <div className="grid grid-3">
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '48px', marginBottom: '20px' }}>🚗</div>
              <h3 style={{ marginBottom: '15px' }}>Variety of Vehicles</h3>
              <p style={{ color: '#666' }}>
                From economy cars to luxury vehicles, find the perfect ride for any occasion.
              </p>
            </div>
            
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '48px', marginBottom: '20px' }}>🛡️</div>
              <h3 style={{ marginBottom: '15px' }}>Safe & Insured</h3>
              <p style={{ color: '#666' }}>
                All vehicles are verified and covered by comprehensive insurance.
              </p>
            </div>
            
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '48px', marginBottom: '20px' }}>💰</div>
              <h3 style={{ marginBottom: '15px' }}>Affordable Pricing</h3>
              <p style={{ color: '#666' }}>
                Transparent pricing with no hidden fees. Pay only for what you use.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;