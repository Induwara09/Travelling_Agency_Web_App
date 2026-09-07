import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import sriLankaMap from '../assets/sl-map.webp';

export default function IslandMap() {
  return (
    <motion.figure
      className="island-map"
      initial={{ opacity: 0, y: 36, scale: 0.97 }}
      whileInView={{ opacity: 1, y: 0, scale: 1 }}
      viewport={{ once: true, amount: 0.2 }}
      transition={{ duration: 0.85, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="island-map__frame">
        <img
          src={sriLankaMap}
          alt="Illustrated Sri Lanka tourist road map showing famous destinations, heritage sites, beaches and national parks"
          loading="lazy"
          decoding="async"
        />
      </div>

      <Link className="map-link" to="/destinations">
        Explore the island <span aria-hidden="true">→</span>
      </Link>
    </motion.figure>
  );
}
