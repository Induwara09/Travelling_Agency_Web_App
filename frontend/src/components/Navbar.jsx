import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { ChevronDown, LogOut, Mail, Menu, Phone, UserRound, X } from 'lucide-react';
import Brand from './Brand';
import ThemeToggle from './ThemeToggle';
import { useAuth } from '../context/AuthContext';

const links = [
  ['Home', '/'],
  ['About', '/about'],
  ['Destinations', '/destinations'],
  ['Experiences', '/experiences'],
  ['Tours', '/packages'],
  ['Hotels', '/hotels'],
  ['Contact', '/contact']
];

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const signOut = () => {
    logout();
    setProfileOpen(false);
    navigate('/');
  };

  return (
    <header className="navbar-wrap">
      <div className="contact-strip"><div className="container"><span>Made in Sri Lanka, for curious travellers</span><div><a href="mailto:hello@serendibtrails.lk"><Mail /> hello@serendibtrails.lk</a><a href="tel:+94112345678"><Phone /> Call us</a></div></div></div>
      <nav className="navbar container">
        <Brand />
        <button className="mobile-menu-button" onClick={() => setOpen(!open)} aria-label="Toggle navigation">
          {open ? <X /> : <Menu />}
        </button>

        <div className={`navbar__content ${open ? 'is-open' : ''}`}>
          <div className="navbar__links">
            {links.map(([label, path]) => (
              <NavLink key={path} to={path} onClick={() => setOpen(false)}>{label}</NavLink>
            ))}
          </div>
          <div className="navbar__actions">
            <ThemeToggle compact />
            {user ? (
              <div className="profile-menu">
                <button className="profile-menu__button" onClick={() => setProfileOpen(!profileOpen)}>
                  <span className="avatar">{user.name?.charAt(0)?.toUpperCase()}</span>
                  <span>{user.name?.split(' ')[0]}</span>
                  <ChevronDown size={16} />
                </button>
                {profileOpen && (
                  <div className="profile-menu__panel">
                    <div className="profile-menu__meta"><strong>{user.name}</strong><small>{user.email}</small></div>
                    <Link to="/my-bookings" onClick={() => setProfileOpen(false)}><UserRound size={17} /> My bookings</Link>
                    {isAdmin && <Link to="/admin/dashboard" onClick={() => setProfileOpen(false)}>Admin dashboard</Link>}
                    <button onClick={signOut}><LogOut size={17} /> Sign out</button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <Link className="text-link" to="/login">Sign in</Link>
                <Link className="button button--primary button--small" to="/contact">Plan my trip</Link>
              </>
            )}
          </div>
        </div>
      </nav>
    </header>
  );
}
