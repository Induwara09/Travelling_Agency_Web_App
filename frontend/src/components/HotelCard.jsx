import { ArrowRight, ExternalLink, MapPin, Star } from 'lucide-react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { images } from '../data/siteData';
import { formatCurrency } from '../utils/format';
import ImageWithFallback from './ImageWithFallback';

export default function HotelCard({ hotel, index = 0 }) {
  return (
    <motion.article className="hotel-card" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * 0.06 }}>
      <div className="hotel-card__image"><ImageWithFallback src={hotel.imageUrl} fallback={images.hotel} alt={hotel.name} /><span className="availability">{hotel.available ? 'Available' : 'Unavailable'}</span></div>
      <div className="hotel-card__body">
        <div className="hotel-card__rating"><span><Star fill="currentColor" /> {hotel.rating || 'New'}</span><span>{'★'.repeat(Math.max(0, Math.min(5, hotel.starRating || 4)))}</span></div>
        <h3>{hotel.name}</h3>
        <p className="location-line"><MapPin /> {hotel.location}</p>
        <div className="hotel-card__footer"><div><small>Indicative / night</small><strong>{formatCurrency(hotel.pricePerNight)}</strong></div><div className="card-actions">{hotel.websiteUrl && <a href={hotel.websiteUrl} target="_blank" rel="noreferrer" aria-label="Official hotel website"><ExternalLink /></a>}<Link to={`/hotels/${hotel.id}`}><ArrowRight /></Link></div></div>
      </div>
    </motion.article>
  );
}
