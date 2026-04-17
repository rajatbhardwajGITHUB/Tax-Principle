import axios from 'axios';
import { clearSession, getSession, saveSession } from '../utils/storage';
import { extractEmailFromToken, extractRoleFromToken } from '../utils/jwt';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export const publicClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000
});

export const httpClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000
});

let refreshRequest = null;

async function refreshTokens() {
  const session = getSession();
  if (!session?.refreshToken) {
    throw new Error('No refresh token available');
  }

  const response = await publicClient.post('/auth/refresh', {
    refreshToken: session.refreshToken
  });

  const auth = response.data;
  const nextSession = {
    ...session,
    accessToken: auth.accessToken,
    refreshToken: auth.refreshToken,
    tokenType: auth.tokenType || 'Bearer',
    role: extractRoleFromToken(auth.accessToken) || session.role || 'USER',
    email: extractEmailFromToken(auth.accessToken) || session.email || null
  };

  saveSession(nextSession);
  return nextSession;
}

httpClient.interceptors.request.use((config) => {
  const token = getSession()?.accessToken;
  if (token && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error?.response?.status;
    const originalRequest = error?.config;

    if (!originalRequest || status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    const url = originalRequest.url || '';
    const isAuthEndpoint = [
      '/auth/login',
      '/auth/register',
      '/auth/login/initiate',
      '/auth/login/verify',
      '/auth/register/initiate',
      '/auth/register/verify',
      '/auth/password/forgot',
      '/auth/password/reset',
      '/auth/refresh'
    ].some((path) => url.includes(path));

    if (isAuthEndpoint) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      if (!refreshRequest) {
        refreshRequest = refreshTokens().finally(() => {
          refreshRequest = null;
        });
      }

      const updatedSession = await refreshRequest;
      originalRequest.headers.Authorization = `Bearer ${updatedSession.accessToken}`;
      return httpClient(originalRequest);
    } catch (refreshError) {
      clearSession();
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
      return Promise.reject(refreshError);
    }
  }
);

export function parseApiError(error) {
  const apiError = error?.response?.data;
  if (apiError?.message) return apiError.message;
  if (error?.message) return error.message;
  return 'Something went wrong. Please try again.';
}
