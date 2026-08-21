import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import FloatingActions from '../components/FloatingActions';

export default function CustomerLayout() {
  return <div className="site-shell"><Navbar /><main><Outlet /></main><Footer /><FloatingActions /></div>;
}
