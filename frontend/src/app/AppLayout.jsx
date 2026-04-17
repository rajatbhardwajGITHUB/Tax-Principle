import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

export default function AppLayout() {
  const { isAuthenticated, isAdmin, session, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-wrap">
          <Link to={isAuthenticated ? '/services' : '/login'} className="brand-link">
            <span className="brand-mark">TP</span>
            <span className="brand-copy">
              <strong>Tax Principals</strong>
              <small>Advisory Workspace</small>
            </span>
          </Link>
          {isAuthenticated ? <span className="email-chip">{session?.email || 'Logged in'}</span> : null}
        </div>

        <nav className="menu">
          {isAuthenticated ? (
            <>
              <NavLink to="/services" className="menu-link">Services</NavLink>
              {!isAdmin ? <NavLink to="/purchases" className="menu-link">Purchases</NavLink> : null}
              {!isAdmin ? <NavLink to="/profile" className="menu-link">Profile Tracker</NavLink> : null}
              {!isAdmin ? <NavLink to="/dashboard" className="menu-link">Dashboard</NavLink> : null}
              {isAdmin ? <NavLink to="/admin/services" className="menu-link">Admin</NavLink> : null}
              {isAdmin ? <NavLink to="/admin/purchases" className="menu-link">Admin Purchases</NavLink> : null}
              {isAdmin ? <NavLink to="/admin/dashboard" className="menu-link">Admin Dashboard</NavLink> : null}
              <button type="button" className="btn secondary" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" className="menu-link">Login</NavLink>
              <NavLink to="/register" className="menu-link">Register</NavLink>
            </>
          )}
        </nav>
      </header>

      <main className="page-wrap">
        <Outlet />
      </main>
    </div>
  );
}
