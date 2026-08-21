import { useState } from 'react';
import { BarChart3, BedDouble, CalendarCheck2, Compass, LogOut, Map, Menu, MessageSquareText, Sparkles, Users, X } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import Brand from '../components/Brand';
import ThemeToggle from '../components/ThemeToggle';
import { useAuth } from '../context/AuthContext';

const nav = [
  ['Dashboard', 'dashboard', BarChart3],
  ['Destinations', 'destinations', Map],
  ['Packages', 'packages', Compass],
  ['Hotels', 'hotels', BedDouble],
  ['Experiences', 'experiences', Sparkles],
  ['Inquiries', 'inquiries', MessageSquareText],
  ['Bookings', 'bookings', CalendarCheck2],
  ['Users', 'users', Users]
];

export default function AdminLayout() {
  const [open, setOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const signOut = () => { logout(); navigate('/login'); };

  return (
    <div className="admin-shell">
      <aside className={`admin-sidebar ${open ? 'is-open' : ''}`}>
        <div className="admin-sidebar__brand"><Brand light /><button onClick={() => setOpen(false)}><X /></button></div>
        <div className="admin-profile"><span className="avatar avatar--large">{user?.name?.charAt(0)}</span><div><strong>{user?.name}</strong><small>Administrator</small></div></div>
        <nav>{nav.map(([label, path, Icon]) => <NavLink key={path} to={path} onClick={() => setOpen(false)}><Icon /> {label}</NavLink>)}</nav>
        <button className="admin-logout" onClick={signOut}><LogOut /> Sign out</button>
      </aside>
      <div className="admin-main">
        <header className="admin-topbar"><button className="admin-menu" onClick={() => setOpen(true)}><Menu /></button><div><span className="eyebrow">Management portal</span><strong>Serendib Trails Admin</strong></div><ThemeToggle compact /></header>
        <main className="admin-content"><Outlet /></main>
      </div>
      {open && <div className="sidebar-scrim" onClick={() => setOpen(false)} />}
    </div>
  );
}
