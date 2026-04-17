import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { authApi } from '../api/authApi';
import { parseApiError } from '../api/httpClient';
import { clearSession, getSession, saveSession } from '../utils/storage';
import { extractEmailFromToken, extractRoleFromToken, isTokenExpired } from '../utils/jwt';

const AuthContext = createContext(null);

function normalizeAuthResponse(authResponse) {
  return {
    accessToken: authResponse.accessToken,
    refreshToken: authResponse.refreshToken,
    tokenType: authResponse.tokenType || 'Bearer',
    role: extractRoleFromToken(authResponse.accessToken) || 'USER',
    email: extractEmailFromToken(authResponse.accessToken)
  };
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getSession());

  useEffect(() => {
    const sync = () => setSession(getSession());
    window.addEventListener('auth:changed', sync);
    window.addEventListener('storage', sync);
    return () => {
      window.removeEventListener('auth:changed', sync);
      window.removeEventListener('storage', sync);
    };
  }, []);

  useEffect(() => {
    const checkExpiry = () => {
      const current = getSession();
      if (!current?.accessToken) return;
      if (!isTokenExpired(current.accessToken)) return;

      clearSession();
      setSession(null);
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    };

    checkExpiry();
    const timer = window.setInterval(checkExpiry, 30000);
    return () => window.clearInterval(timer);
  }, []);

  const persist = useCallback((nextSession) => {
    saveSession(nextSession);
    setSession(nextSession);
  }, []);

  const initiateLogin = useCallback(async ({ email, password }) => {
    try {
      return await authApi.loginInitiate({ email, password });
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, []);

  const verifyLoginOtp = useCallback(async ({ email, otp }) => {
    try {
      const auth = await authApi.loginVerify(email, otp);
      const nextSession = normalizeAuthResponse(auth);
      persist(nextSession);
      return nextSession;
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, [persist]);

  const initiateRegister = useCallback(async (payload) => {
    try {
      return await authApi.registerInitiate(payload);
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, []);

  const verifyRegisterOtp = useCallback(async ({ email, otp }) => {
    try {
      const auth = await authApi.registerVerify(email, otp);
      const nextSession = normalizeAuthResponse(auth);
      persist(nextSession);
      return nextSession;
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, [persist]);

  const forgotPassword = useCallback(async (email) => {
    try {
      return await authApi.forgotPassword(email);
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, []);

  const resetPassword = useCallback(async ({ email, otp, newPassword }) => {
    try {
      return await authApi.resetPassword({ email, otp, newPassword });
    } catch (error) {
      throw new Error(parseApiError(error));
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      if (session?.accessToken) {
        await authApi.logout();
      }
    } catch {
      // Ignore remote logout errors to ensure local logout always succeeds.
    } finally {
      clearSession();
      setSession(null);
    }
  }, [session?.accessToken]);

  const value = useMemo(() => ({
    session,
    isAuthenticated: Boolean(session?.accessToken) && !isTokenExpired(session.accessToken),
    isAdmin: session?.role === 'ADMIN',
    initiateLogin,
    verifyLoginOtp,
    initiateRegister,
    verifyRegisterOtp,
    forgotPassword,
    resetPassword,
    logout
  }), [
    session,
    initiateLogin,
    verifyLoginOtp,
    initiateRegister,
    verifyRegisterOtp,
    forgotPassword,
    resetPassword,
    logout
  ]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
