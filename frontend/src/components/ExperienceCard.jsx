import { motion } from 'framer-motion';
import { ArrowUpRight, Clock3, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import { images } from '../data/siteData';
import ImageWithFallback from './ImageWithFallback';

export default function ExperienceCard({ item, index = 0 }) {
  const fallback = images.experience(item.category, item.title);
  return <motion.article className="experience-card" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true, amount: .2 }} transition={{ delay: index * .06 }}><div className="experience-card__image"><ImageWithFallback src={item.imageUrl} fallback={fallback} alt={item.title} /><span>{item.category}</span></div><div className="experience-card__body"><div className="experience-card__meta"><span><MapPin /> {item.location}</span><span><Clock3 /> {item.durationHours} hours</span></div><h3>{item.title}</h3><p>{item.shortDescription}</p><Link to={`/contact?interest=${encodeURIComponent(item.title)}`}>Add to my journey <ArrowUpRight /></Link></div></motion.article>;
}
