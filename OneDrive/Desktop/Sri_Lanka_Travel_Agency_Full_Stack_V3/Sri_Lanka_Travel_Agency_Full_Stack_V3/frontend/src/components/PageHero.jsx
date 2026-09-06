import { motion } from 'framer-motion';

export default function PageHero({ eyebrow, title, text, compact = false, image }) {
  return (
    <section className={`page-hero ${image ? 'page-hero--image' : ''} ${compact ? 'page-hero--compact' : ''}`} style={image ? { backgroundImage: `linear-gradient(90deg, rgba(7,31,37,.9), rgba(7,31,37,.4)), url(${image})` } : undefined}>
      <div className="page-hero__glow" />
      <motion.div className="container" initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }}>
        {eyebrow && <span className="eyebrow eyebrow--light">{eyebrow}</span>}
        <h1>{title}</h1>
        {text && <p>{text}</p>}
      </motion.div>
    </section>
  );
}
