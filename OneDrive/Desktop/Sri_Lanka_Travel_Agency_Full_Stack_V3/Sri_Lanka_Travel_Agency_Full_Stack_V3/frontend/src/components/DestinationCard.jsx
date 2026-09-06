import { ArrowUpRight, MapPin, Star } from 'lucide-react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { images } from '../data/siteData';
import ImageWithFallback from './ImageWithFallback';

export default function DestinationCard({ destination, index = 0 }) {
  const fallback = images.destination(destination.name, destination.district, destination.category);
  return (
    <motion.article
      className="image-card destination-card destination-card--v3"
      initial={{ opacity: 0, y: 28 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: .15 }}
      transition={{ delay: Math.min(index, 8) * .045, duration: .45 }}
    >
      <ImageWithFallback src={destination.imageUrl} fallback={fallback} alt={destination.name} loading="lazy" />
      <div className="image-card__shade" />
      <div className="destination-card__badges">
        <span>{destination.category || 'Explore'}</span>
        {destination.featured && <span className="featured-chip"><Star size={12} fill="currentColor" /> Featured</span>}
      </div>
      <div className="image-card__content">
        <span><MapPin size={15} /> {destination.district || destination.location}</span>
        <h3>{destination.name}</h3>
        <p>{destination.shortDescription || destination.description}</p>
        <Link to={`/destinations/${destination.id}`}>Discover place <ArrowUpRight size={17} /></Link>
      </div>
    </motion.article>
  );
}
