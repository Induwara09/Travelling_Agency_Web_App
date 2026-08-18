import { Instagram, Mail, MapPin, Phone } from 'lucide-react';
import { Link } from 'react-router-dom';
import Brand from './Brand';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer__grid">
        <div className="footer__brand">
          <Brand light />
          <p>Thoughtful island journeys shaped by local stories, wild landscapes and warm Sri Lankan hospitality.</p>
          <div className="footer__social"><a href="#" aria-label="Instagram"><Instagram /></a></div>
        </div>
        <div><h4>Explore</h4><Link to="/destinations">Destinations</Link><Link to="/packages">Tour packages</Link><Link to="/hotels">Luxury stays</Link></div>
        <div><h4>Your trip</h4><Link to="/register">Create account</Link><Link to="/my-bookings">My bookings</Link><Link to="/login">Sign in</Link></div>
        <div className="footer__contact"><h4>Contact</h4><span><MapPin /> Colombo, Sri Lanka</span><span><Phone /> +94 11 234 5678</span><span><Mail /> hello@serendibtrails.lk</span></div>
      </div>
      <div className="container footer__bottom">
        <span>© {new Date().getFullYear()} Serendib Trails. Academic project.</span>
        <span>Travel imagery credits are documented in IMAGE_CREDITS.md.</span>
      </div>
    </footer>
  );
}
