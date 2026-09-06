import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('travel-token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/api/auth/')) {
      localStorage.removeItem('travel-token');
      localStorage.removeItem('travel-user');
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: (payload) => api.post('/api/auth/login', payload),
  register: (payload) => api.post('/api/auth/register', payload)
};

export const destinationApi = {
  list: (params = {}) => api.get('/api/destinations', { params: typeof params === 'string' ? { search: params } : params }),
  get: (id) => api.get(`/api/destinations/${id}`),
  create: (payload) => api.post('/api/destinations', payload),
  update: (id, payload) => api.put(`/api/destinations/${id}`, payload),
  remove: (id) => api.delete(`/api/destinations/${id}`)
};

export const packageApi = {
  list: (params = {}) => api.get('/api/packages', { params }),
  get: (id) => api.get(`/api/packages/${id}`),
  create: (payload) => api.post('/api/packages', payload),
  update: (id, payload) => api.put(`/api/packages/${id}`, payload),
  remove: (id) => api.delete(`/api/packages/${id}`)
};

export const inquiryApi = {
  create: (payload) => api.post('/api/inquiries', payload),
  all: () => api.get('/api/admin/inquiries'),
  updateStatus: (id, status) => api.put(`/api/admin/inquiries/${id}/status`, { status }),
  remove: (id) => api.delete(`/api/admin/inquiries/${id}`)
};

export const bookingApi = {
  create: (payload) => api.post('/api/bookings', payload),
  mine: () => api.get('/api/bookings/my'),
  cancel: (id) => api.put(`/api/bookings/${id}/cancel`),
  all: () => api.get('/api/admin/bookings'),
  updateStatus: (id, payload) => api.put(`/api/admin/bookings/${id}/status`, payload),
  remove: (id) => api.delete(`/api/admin/bookings/${id}`)
};

export const adminApi = {
  dashboard: () => api.get('/api/admin/dashboard'),
  users: () => api.get('/api/admin/users'),
  updateRole: (id, role) => api.put(`/api/admin/users/${id}/role`, { role }),
  removeUser: (id) => api.delete(`/api/admin/users/${id}`)
};

export const getApiError = (error, fallback = 'Something went wrong. Please try again.') =>
  error.response?.data?.message || fallback;
