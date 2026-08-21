import { ArrowRight, CalendarDays, MapPin } from 'lucide-react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { images } from '../data/siteData';
import { formatCurrency } from '../utils/format';
import ImageWithFallback from './ImageWithFallback';

export default function PackageCard({ item, index = 0 }) {
  return (
    <motion.article className="package-card" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * 0.06 }}>
      <div className="package-card__image">
        <ImageWithFallback src={item.imageUrl} fallback={images.package} alt={item.name} />
        <span className="pill pill--gold">{item.category}</span>
      </div>
      <div className="package-card__body">
        <div className="package-card__meta"><span><CalendarDays /> {item.durationDays} days</span><span><MapPin /> {item.destinationName}</span></div>
        <h3>{item.name}</h3>
        <p>{item.description || 'A thoughtfully curated Sri Lankan experience.'}</p>
        <div className="package-card__footer"><div><small>From / person</small><strong>{formatCurrency(item.price)}</strong></div><Link className="round-link" to={`/packages/${item.id}`} aria-label={`View ${item.name}`}><ArrowRight /></Link></div>
      </div>
    </motion.article>
  );
}
