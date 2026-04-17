import { Navigate, createBrowserRouter } from 'react-router-dom';
import AppLayout from './AppLayout';
import ProtectedRoute from '../components/ProtectedRoute';
import AdminRoute from '../components/AdminRoute';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import LandingPage from '../pages/LandingPage';
import ServiceListPage from '../pages/ServiceListPage';
import ServiceDetailPage from '../pages/ServiceDetailPage';
import MyPurchasesPage from '../pages/MyPurchasesPage';
import UserDashboardPage from '../pages/UserDashboardPage';
import UserProfilePage from '../pages/UserProfilePage';
import AdminServicesPage from '../pages/AdminServicesPage';
import AdminServiceFormPage from '../pages/AdminServiceFormPage';
import AdminPurchasesPage from '../pages/AdminPurchasesPage';
import AdminDashboardPage from '../pages/AdminDashboardPage';
import UnauthorizedPage from '../pages/UnauthorizedPage';
import NotFoundPage from '../pages/NotFoundPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <Navigate to="/login" replace /> },
      { path: 'landing-preview', element: <LandingPage /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      { path: 'unauthorized', element: <UnauthorizedPage /> },
      {
        element: <ProtectedRoute />,
        children: [
          { path: 'services', element: <ServiceListPage /> },
          { path: 'services/:id', element: <ServiceDetailPage /> },
          { path: 'purchases', element: <MyPurchasesPage /> },
          { path: 'dashboard', element: <UserDashboardPage /> },
          { path: 'profile', element: <UserProfilePage /> },
          {
            element: <AdminRoute />,
            children: [
              { path: 'admin/services', element: <AdminServicesPage /> },
              { path: 'admin/services/new', element: <AdminServiceFormPage mode="create" /> },
              { path: 'admin/services/:id/edit', element: <AdminServiceFormPage mode="edit" /> },
              { path: 'admin/purchases', element: <AdminPurchasesPage /> },
              { path: 'admin/dashboard', element: <AdminDashboardPage /> }
            ]
          }
        ]
      },
      { path: '*', element: <NotFoundPage /> }
    ]
  }
]);

export default router;
