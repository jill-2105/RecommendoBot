// src/api/authService.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authService = {
  login: async (username, password) => {
    const res = await api.post('/api/auth/login', { username, password });
    // expect { token, username, role, expiresIn }
    if (res.data && res.data.data && res.data.data.token) {
      const { token } = res.data.data;
      localStorage.setItem('jwt_token', token);
    }
    return res.data;
  },

  register: async (username, password, email) => {
    const res = await api.post('/api/auth/register', { username, password, email });
    return res.data;
  },

  logout: () => {
    localStorage.removeItem('jwt_token');
  },
};

export default authService;
