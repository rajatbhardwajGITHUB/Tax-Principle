import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import useAuth from '../hooks/useAuth';
import ErrorAlert from '../components/ErrorAlert';

const registerSchema = z.object({
  name: z.string().min(2, 'Name is required'),
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  phoneNumber: z.string().regex(/^\d{10,15}$/, 'Enter a valid phone number (10-15 digits)'),
  role: z.enum(['USER', 'ADMIN'])
});

export default function RegisterPage() {
  const { isAuthenticated, initiateRegister, verifyRegisterOtp, session } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [pendingEmail, setPendingEmail] = useState('');
  const [pendingRole, setPendingRole] = useState('USER');
  const [otp, setOtp] = useState('');
  const [verifyingOtp, setVerifyingOtp] = useState(false);

  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors, isSubmitting }
  } = useForm({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: '',
      email: '',
      password: '',
      phoneNumber: '',
      role: 'USER'
    }
  });

  if (isAuthenticated) {
    return <Navigate to={session?.role === 'ADMIN' ? '/admin/services' : '/services'} replace />;
  }

  const handleInitiate = async (values) => {
    setError('');
    setInfo('');
    try {
      const response = await initiateRegister({
        ...values
      });

      setPendingEmail(response?.email || values.email);
      setPendingRole(values.role);
      setInfo(response?.otp
        ? `${response.message}. Dev OTP: ${response.otp}`
        : response?.message || 'OTP sent to your email. Please verify to complete signup.');
    } catch (err) {
      setError(err.message);
    }
  };

  const handleVerifyOtp = async () => {
    if (!otp.trim()) {
      setError('OTP is required');
      return;
    }

    setError('');
    setInfo('');
    setVerifyingOtp(true);
    try {
      const nextSession = await verifyRegisterOtp({ email: pendingEmail, otp: otp.trim() });
      navigate(nextSession?.role === 'ADMIN' || pendingRole === 'ADMIN' ? '/admin/services' : '/services', {
        replace: true
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setVerifyingOtp(false);
    }
  };

  const handleResendOtp = async () => {
    await handleInitiate(getValues());
  };

  return (
    <section className="auth-card">
      <h1>Register</h1>
      <p className="muted">Create your account to start using services.</p>
      <ErrorAlert message={error} />
      {info ? <p className="muted">{info}</p> : null}

      {!pendingEmail ? (
        <form onSubmit={handleSubmit(handleInitiate)} className="form-grid">
          <label>
            Name
            <input type="text" placeholder="John Doe" {...register('name')} />
            {errors.name ? <span className="field-error">{errors.name.message}</span> : null}
          </label>

          <label>
            Email
            <input type="email" placeholder="user@example.com" {...register('email')} />
            {errors.email ? <span className="field-error">{errors.email.message}</span> : null}
          </label>

          <label>
            Password
            <input type="password" placeholder="Minimum 8 characters" {...register('password')} />
            {errors.password ? <span className="field-error">{errors.password.message}</span> : null}
          </label>

          <label>
            Phone Number
            <input type="text" placeholder="9999999999" {...register('phoneNumber')} />
            {errors.phoneNumber ? <span className="field-error">{errors.phoneNumber.message}</span> : null}
          </label>

          <label>
            Role
            <select {...register('role')}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
            {errors.role ? <span className="field-error">{errors.role.message}</span> : null}
          </label>

          <button type="submit" className="btn" disabled={isSubmitting}>
            {isSubmitting ? 'Sending OTP...' : 'Register'}
          </button>
        </form>
      ) : (
        <div className="form-grid">
          <label>
            OTP
            <input
              type="text"
              placeholder="Enter 6 digit OTP"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
            />
          </label>

          <button type="button" className="btn" onClick={handleVerifyOtp} disabled={verifyingOtp}>
            {verifyingOtp ? 'Verifying...' : 'Verify OTP'}
          </button>

          <button type="button" className="btn btn-secondary" onClick={handleResendOtp} disabled={isSubmitting}>
            Resend OTP
          </button>

          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setPendingEmail('');
              setOtp('');
              setInfo('');
              setError('');
            }}
          >
            Edit Details
          </button>
        </div>
      )}

      <p className="muted">
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </section>
  );
}
