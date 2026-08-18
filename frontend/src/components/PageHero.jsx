import { motion } from 'framer-motion';

export default function PageHero({ eyebrow, title, text, compact = false }) {
  return (
    <section className={`page-hero ${compact ? 'page-hero--compact' : ''}`}>
      <div className="page-hero__glow" />
      <motion.div className="container" initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }}>
        {eyebrow && <span className="eyebrow eyebrow--light">{eyebrow}</span>}
        <h1>{title}</h1>
        {text && <p>{text}</p>}
      </motion.div>
    </section>
  );
}
