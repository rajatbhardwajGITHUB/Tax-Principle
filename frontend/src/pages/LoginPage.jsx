import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import ErrorAlert from '../components/ErrorAlert';

export default function LoginPage() {
  const { isAuthenticated, initiateLogin, verifyLoginOtp, forgotPassword, resetPassword, session } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [error, setError] = useState('');
  const [info, setInfo] = useState('');

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [pendingEmail, setPendingEmail] = useState('');
  const [loginOtp, setLoginOtp] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const [showForgotPassword, setShowForgotPassword] = useState(false);
  const [forgotEmail, setForgotEmail] = useState('');
  const [resetOtp, setResetOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [resetSubmitting, setResetSubmitting] = useState(false);

  if (isAuthenticated) {
    return <Navigate to={session?.role === 'ADMIN' ? '/admin/services' : '/services'} replace />;
  }

  const completeLoginRedirect = () => {
    const fromPath = location.state?.from?.pathname;
    if (fromPath) {
      navigate(fromPath, { replace: true });
      return;
    }
    navigate('/services', { replace: true });
  };

  const handleLoginInitiate = async (e) => {
    e.preventDefault();
    setError('');
    setInfo('');

    if (!email.trim() || !password) {
      setError('Email and password are required');
      return;
    }

    setSubmitting(true);
    try {
      const response = await initiateLogin({ email: email.trim(), password });
      setPendingEmail(response?.email || email.trim());
      setInfo(response?.otp
        ? `${response.message}. Dev OTP: ${response.otp}`
        : response?.message || 'OTP sent to your email. Please verify to continue.');
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleLoginVerify = async (e) => {
    e.preventDefault();
    setError('');
    setInfo('');

    if (!loginOtp.trim()) {
      setError('OTP is required');
      return;
    }

    setSubmitting(true);
    try {
      await verifyLoginOtp({ email: pendingEmail, otp: loginOtp.trim() });
      completeLoginRedirect();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleForgotOtp = async () => {
    setError('');
    setInfo('');

    if (!forgotEmail.trim()) {
      setError('Email is required for password reset');
      return;
    }

    setResetSubmitting(true);
    try {
      const response = await forgotPassword(forgotEmail.trim());
      setInfo(response?.otp
        ? `${response.message}. Dev OTP: ${response.otp}`
        : response?.message || 'Password reset OTP sent to your email.');
    } catch (err) {
      setError(err.message);
    } finally {
      setResetSubmitting(false);
    }
  };

  const handleResetPassword = async () => {
    setError('');
    setInfo('');

    if (!forgotEmail.trim() || !resetOtp.trim() || !newPassword) {
      setError('Email, OTP, and new password are required');
      return;
    }

    setResetSubmitting(true);
    try {
      const response = await resetPassword({
        email: forgotEmail.trim(),
        otp: resetOtp.trim(),
        newPassword
      });
      setInfo(response?.message || 'Password reset successful. Please login with new password.');
      setResetOtp('');
      setNewPassword('');
    } catch (err) {
      setError(err.message);
    } finally {
      setResetSubmitting(false);
    }
  };

  return (
    <section className="auth-card">
      <h1>Login</h1>
      <p className="muted">Sign in with your account details.</p>
      <ErrorAlert message={error} />
      {info ? <p className="muted">{info}</p> : null}

      {!pendingEmail ? (
        <form onSubmit={handleLoginInitiate} className="form-grid">
          <label>
            Email
            <input
              type="email"
              placeholder="user@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </label>

          <label>
            Password
            <input
              type="password"
              placeholder="Your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>

          <button type="submit" className="btn" disabled={submitting}>
            {submitting ? 'Sending OTP...' : 'Login'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleLoginVerify} className="form-grid">
          <label>
            OTP
            <input
              type="text"
              placeholder="Enter 6 digit OTP"
              value={loginOtp}
              onChange={(e) => setLoginOtp(e.target.value)}
            />
          </label>

          <button type="submit" className="btn" disabled={submitting}>
            {submitting ? 'Verifying...' : 'Verify OTP'}
          </button>

          <button
            type="button"
            className="btn btn-secondary"
            disabled={submitting}
            onClick={() => {
              setPendingEmail('');
              setLoginOtp('');
              setInfo('');
              setError('');
            }}
          >
            Back
          </button>
        </form>
      )}

      <p className="muted">
        <button
          type="button"
          className="text-link-btn"
          onClick={() => {
            setShowForgotPassword((prev) => !prev);
            setError('');
            setInfo('');
          }}
        >
          {showForgotPassword ? 'Back to Login' : 'Forgot Password?'}
        </button>
      </p>

      {showForgotPassword ? (
        <div className="form-grid" style={{ marginTop: '1rem' }}>
          <label>
            Email
            <input
              type="email"
              placeholder="user@example.com"
              value={forgotEmail}
              onChange={(e) => setForgotEmail(e.target.value)}
            />
          </label>

          <button type="button" className="btn btn-secondary" onClick={handleForgotOtp} disabled={resetSubmitting}>
            {resetSubmitting ? 'Sending OTP...' : 'Send Reset OTP'}
          </button>

          <label>
            Reset OTP
            <input
              type="text"
              placeholder="Enter reset OTP"
              value={resetOtp}
              onChange={(e) => setResetOtp(e.target.value)}
            />
          </label>

          <label>
            New Password
            <input
              type="password"
              placeholder="Minimum 8 characters"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
          </label>

          <button type="button" className="btn" onClick={handleResetPassword} disabled={resetSubmitting}>
            {resetSubmitting ? 'Resetting...' : 'Reset Password'}
          </button>
        </div>
      ) : null}

      <p className="muted">
        New here? <Link to="/register">Create an account</Link>
      </p>
    </section>
  );
}
