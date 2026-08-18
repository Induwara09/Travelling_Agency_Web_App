import { ArrowUpRight, MapPin } from 'lucide-react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { images } from '../data/siteData';
import ImageWithFallback from './ImageWithFallback';

export default function DestinationCard({ destination, index = 0 }) {
  const fallback = images.destination(destination.name);
  return (
    <motion.article className="image-card destination-card" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * 0.06 }}>
      <ImageWithFallback src={destination.imageUrl} fallback={fallback} alt={destination.name} />
      <div className="image-card__shade" />
      <div className="image-card__content">
        <span><MapPin size={15} /> {destination.location}</span>
        <h3>{destination.name}</h3>
        <Link to={`/destinations/${destination.id}`}>Discover <ArrowUpRight size={17} /></Link>
      </div>
    </motion.article>
  );
}
