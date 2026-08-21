import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import CustomerLayout from './layouts/CustomerLayout';
import AdminLayout from './layouts/AdminLayout';
import ProtectedRoute from './components/ProtectedRoute';
import HomePage from './pages/customer/HomePage';
import DestinationsPage from './pages/customer/DestinationsPage';
import DestinationDetailsPage from './pages/customer/DestinationDetailsPage';
import PackagesPage from './pages/customer/PackagesPage';
import PackageDetailsPage from './pages/customer/PackageDetailsPage';
import HotelsPage from './pages/customer/HotelsPage';
import HotelDetailsPage from './pages/customer/HotelDetailsPage';
import BookingPage from './pages/customer/BookingPage';
import MyBookingsPage from './pages/customer/MyBookingsPage';
import LoginPage from './pages/customer/LoginPage';
import RegisterPage from './pages/customer/RegisterPage';
import NotFoundPage from './pages/customer/NotFoundPage';
import AboutPage from './pages/customer/AboutPage';
import ExperiencesPage from './pages/customer/ExperiencesPage';
import ContactPage from './pages/customer/ContactPage';
import DashboardPage from './pages/admin/DashboardPage';
import AdminDestinationsPage from './pages/admin/AdminDestinationsPage';
import AdminPackagesPage from './pages/admin/AdminPackagesPage';
import AdminHotelsPage from './pages/admin/AdminHotelsPage';
import AdminBookingsPage from './pages/admin/AdminBookingsPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminExperiencesPage from './pages/admin/AdminExperiencesPage';
import AdminInquiriesPage from './pages/admin/AdminInquiriesPage';

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }, [pathname]);

  return null;
}

export default function App() {
  return (
    <>
      <ScrollToTop />
      <Routes>
        <Route element={<CustomerLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/experiences" element={<ExperiencesPage />} />
          <Route path="/contact" element={<ContactPage />} />
          <Route path="/destinations" element={<DestinationsPage />} />
          <Route path="/destinations/:id" element={<DestinationDetailsPage />} />
          <Route path="/packages" element={<PackagesPage />} />
          <Route path="/packages/:id" element={<PackageDetailsPage />} />
          <Route path="/hotels" element={<HotelsPage />} />
          <Route path="/hotels/:id" element={<HotelDetailsPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/booking/:packageId" element={<BookingPage />} />
            <Route path="/my-bookings" element={<MyBookingsPage />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute roles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="destinations" element={<AdminDestinationsPage />} />
            <Route path="packages" element={<AdminPackagesPage />} />
            <Route path="hotels" element={<AdminHotelsPage />} />
            <Route path="experiences" element={<AdminExperiencesPage />} />
            <Route path="inquiries" element={<AdminInquiriesPage />} />
            <Route path="bookings" element={<AdminBookingsPage />} />
            <Route path="users" element={<AdminUsersPage />} />
          </Route>
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  );
}
