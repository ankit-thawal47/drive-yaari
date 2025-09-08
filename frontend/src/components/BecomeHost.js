import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

const BecomeHost = () => {
  const { user, isLoggedIn, isHost } = useAuth();
  const navigate = useNavigate();
  const [agreed, setAgreed] = useState(false);

  if (!isLoggedIn) {
    return (
      <div className="container" style={{ paddingTop: '50px' }}>
        <div className="card">
          <div className="card-header">
            <h1 style={{ margin: 0 }}>Become a Host</h1>
            <p style={{ margin: '10px 0 0 0', color: '#666' }}>
              Start earning money by renting out your vehicle
            </p>
          </div>
          <div className="card-body">
            <div className="alert alert-info">
              Please <Link to="/login" style={{ color: 'var(--primary-yellow)' }}>login</Link> or <Link to="/register" style={{ color: 'var(--primary-yellow)' }}>register</Link> to become a host.
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (isHost()) {
    return (
      <div className="container" style={{ paddingTop: '50px' }}>
        <div className="card">
          <div className="card-header">
            <h1 style={{ margin: 0 }}>You're Already a Host!</h1>
          </div>
          <div className="card-body">
            <div className="alert alert-success">
              You're already registered as a host. Start adding your vehicles!
            </div>
            <Link to="/add-vehicle" className="btn btn-primary">
              Add Your First Vehicle
            </Link>
          </div>
        </div>
      </div>
    );
  }

  const handleBecomeHost = () => {
    if (agreed) {
      navigate('/dashboard');
    }
  };

  return (
    <div className="container" style={{ maxWidth: '700px', paddingTop: '50px' }}>
      <div className="card">
        <div className="card-header">
          <h1 style={{ margin: 0 }}>Become a Host with DriveYaari</h1>
          <p style={{ margin: '10px 0 0 0', color: '#666' }}>
            Turn your car into a source of income
          </p>
        </div>
        <div className="card-body">
          <div style={{ marginBottom: '30px' }}>
            <h3>Why host with DriveYaari?</h3>
            <div className="grid grid-2" style={{ marginTop: '20px' }}>
              <div>
                <h4 style={{ color: 'var(--primary-yellow)' }}>💰 Earn Extra Income</h4>
                <p>Make money from your idle vehicle. Average hosts earn SGD $500-1200 per month.</p>
              </div>
              <div>
                <h4 style={{ color: 'var(--primary-yellow)' }}>🛡️ Full Insurance Coverage</h4>
                <p>Every trip is covered by comprehensive insurance for peace of mind.</p>
              </div>
              <div>
                <h4 style={{ color: 'var(--primary-yellow)' }}>📱 Easy Management</h4>
                <p>Manage bookings, track earnings, and communicate with renters through our app.</p>
              </div>
              <div>
                <h4 style={{ color: 'var(--primary-yellow)' }}>🔧 24/7 Support</h4>
                <p>Our team is here to help whenever you need assistance.</p>
              </div>
            </div>
          </div>

          <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--border-radius)' }}>
            <h3 style={{ marginTop: 0 }}>How it works:</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '15px' }}>
                <div style={{ 
                  width: '30px', 
                  height: '30px', 
                  borderRadius: '50%', 
                  backgroundColor: 'var(--primary-yellow)', 
                  color: 'white', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center', 
                  fontWeight: 'bold',
                  flexShrink: 0
                }}>1</div>
                <div>
                  <strong>Add your vehicle</strong>
                  <p style={{ margin: '5px 0 0 0', color: '#666' }}>Upload photos and details about your car</p>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '15px' }}>
                <div style={{ 
                  width: '30px', 
                  height: '30px', 
                  borderRadius: '50%', 
                  backgroundColor: 'var(--primary-yellow)', 
                  color: 'white', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center', 
                  fontWeight: 'bold',
                  flexShrink: 0
                }}>2</div>
                <div>
                  <strong>Get verified</strong>
                  <p style={{ margin: '5px 0 0 0', color: '#666' }}>We'll verify your vehicle and documents</p>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '15px' }}>
                <div style={{ 
                  width: '30px', 
                  height: '30px', 
                  borderRadius: '50%', 
                  backgroundColor: 'var(--primary-yellow)', 
                  color: 'white', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center', 
                  fontWeight: 'bold',
                  flexShrink: 0
                }}>3</div>
                <div>
                  <strong>Start earning</strong>
                  <p style={{ margin: '5px 0 0 0', color: '#666' }}>Accept bookings and start making money</p>
                </div>
              </div>
            </div>
          </div>

          <div style={{ marginBottom: '30px', padding: '15px', backgroundColor: 'var(--light-blue)', borderRadius: 'var(--border-radius)' }}>
            <h4 style={{ margin: '0 0 15px 0' }}>Requirements:</h4>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>Vehicle must be 2010 or newer</li>
              <li>Valid vehicle registration and insurance</li>
              <li>Clean driving record</li>
              <li>Vehicle must pass safety inspection</li>
            </ul>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={agreed}
                onChange={(e) => setAgreed(e.target.checked)}
                style={{ marginTop: '3px' }}
              />
              <span style={{ fontSize: '14px', lineHeight: '1.4' }}>
                I agree to DriveYaari's <strong>Terms of Service</strong> and <strong>Host Agreement</strong>. 
                I understand that my vehicle will be listed for rent and I'm responsible for maintaining it in good condition.
              </span>
            </label>
          </div>

          <div style={{ textAlign: 'center' }}>
            <button 
              onClick={handleBecomeHost}
              className="btn btn-primary"
              style={{ padding: '15px 40px', fontSize: '16px' }}
              disabled={!agreed}
            >
              Become a Host
            </button>
          </div>

          <div style={{ marginTop: '20px', fontSize: '12px', color: '#999', textAlign: 'center' }}>
            <strong>Demo Note:</strong> This is a demo system. In production, you would go through a verification process.
          </div>
        </div>
      </div>
    </div>
  );
};

export default BecomeHost;