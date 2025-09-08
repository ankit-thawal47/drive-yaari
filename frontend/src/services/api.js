import axios from 'axios';

const API_BASE_URL = '/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getCurrentUser: () => api.get('/auth/me'),
};

// Vehicle API
export const vehicleAPI = {
  getVehicles: (searchParams) => api.post('/get-vehicles', searchParams),
  getVehicleTypes: () => api.get('/vehicle-types'),
  registerVehicle: (vehicleData) => api.post('/register-vehicle', vehicleData),
};

// Pricing API
export const pricingAPI = {
  calculatePricing: (pricingRequest) => api.post('/calculate-pricing', pricingRequest),
};

// Trip API
export const tripAPI = {
  rentVehicle: (licensePlate, locationData) => api.post(`/rent-vehicle/${licensePlate}`, locationData),
  getTripById: (tripId) => api.get(`/trip/${tripId}`),
  startTrip: (tripId, startData) => api.post(`/trip/${tripId}/start`, startData),
  completeTrip: (tripId, completeData) => api.post(`/trip/${tripId}/complete`, completeData),
  cancelTrip: (tripId, cancelData) => api.post(`/trip/${tripId}/cancel`, cancelData),
  submitRating: (tripId, ratingData) => api.post(`/trip/${tripId}/rating`, ratingData),
  getActiveTrip: () => api.get('/trips/active'),
  getMyTrips: () => api.get('/trips/my-trips'),
};

export default api;