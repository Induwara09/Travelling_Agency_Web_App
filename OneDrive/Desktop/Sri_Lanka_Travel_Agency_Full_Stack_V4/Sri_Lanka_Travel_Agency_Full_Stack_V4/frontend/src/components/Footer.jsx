import { ArrowRight, Instagram, Mail, MapPin, Phone } from 'lucide-react';
import { Link } from 'react-router-dom';
import Brand from './Brand';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer__invite"><div><span className="eyebrow eyebrow--light">The island is calling</span><h2>Bring us the dream. We’ll help build the journey.</h2></div><Link className="button button--gold" to="/contact">Start planning <ArrowRight /></Link></div>
      <div className="container footer__grid">
        <div className="footer__brand">
          <Brand light />
          <p>Thoughtful island journeys shaped by local stories, wild landscapes and warm Sri Lankan hospitality.</p>
          <div className="footer__social"><a href="#" aria-label="Instagram"><Instagram /></a></div>
        </div>
        <div><h4>Explore</h4><Link to="/destinations">371 destinations</Link><Link to="/packages">Tour packages</Link><Link to="/destinations?featured=true">Featured places</Link><Link to="/destinations?category=Heritage%20%26%20History">Island heritage</Link></div>
        <div><h4>Serendib</h4><Link to="/about">About us</Link><Link to="/contact">Plan a trip</Link><Link to="/my-bookings">My bookings</Link><Link to="/login">Admin / traveller sign in</Link></div>
        <div className="footer__contact"><h4>Contact</h4><span><MapPin /> Colombo, Sri Lanka</span><span><Phone /> +94 11 234 5678</span><span><Mail /> hello@serendibtrails.lk</span></div>
      </div>
      <div className="container footer__bottom">
        <span>© {new Date().getFullYear()} Serendib Trails. Academic full-stack project.</span>
        <span>Destination photography credits are listed in IMAGE_CREDITS.md.</span>
      </div>
    </footer>
  );
}
