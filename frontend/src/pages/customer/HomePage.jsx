import { useEffect, useMemo, useState } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, BadgeCheck, Compass, Headphones, MapPinned, ShieldCheck, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import DestinationCard from '../../components/DestinationCard';
import IslandMap from '../../components/IslandMap';
import PackageCard from '../../components/PackageCard';
import SectionHeading from '../../components/SectionHeading';
import { EmptyState } from '../../components/StateViews';
import { destinationApi, packageApi } from '../../services/api';
import { heroSlides, images, journeyStyles, testimonials } from '../../data/siteData';

export default function HomePage() {
  const [data, setData] = useState({ destinations: [], packages: [] });
  const [loading, setLoading] = useState(true);
  const [slide, setSlide] = useState(0);

  useEffect(() => {
    let active = true;
    Promise.allSettled([destinationApi.list({ featured: true }), packageApi.list({ status: 'ACTIVE' })]).then((results) => {
      if (!active) return;
      setData({
        destinations: results[0].status === 'fulfilled' ? results[0].value.data : [],
        packages: results[1].status === 'fulfilled' ? results[1].value.data : []
      });
      setLoading(false);
    });
    return () => { active = false; };
  }, []);

  useEffect(() => {
    const timer = window.setInterval(() => setSlide((value) => (value + 1) % heroSlides.length), 6000);
    return () => window.clearInterval(timer);
  }, []);

  const featured = useMemo(() => {
    const selected = [];
    const usedDistricts = new Set();
    data.destinations.forEach((item) => {
      if (selected.length < 8 && !usedDistricts.has(item.district)) { selected.push(item); usedDistricts.add(item.district); }
    });
    return selected;
  }, [data.destinations]);
  const current = heroSlides[slide];
  const moveSlide = (direction) => setSlide((value) => (value + direction + heroSlides.length) % heroSlides.length);

  return <>
    <section className="cinematic-hero">
      <AnimatePresence mode="wait"><motion.img key={current.image} className="cinematic-hero__image" src={current.image} alt={current.eyebrow} initial={{ opacity: 0, scale: 1.07 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0 }} transition={{ duration: 1.15 }} /></AnimatePresence>
      <div className="cinematic-hero__shade" />
      <div className="container cinematic-hero__content"><AnimatePresence mode="wait"><motion.div key={current.title} initial={{ opacity: 0, y: 28 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -18 }} transition={{ duration: .55 }}><span className="hero-kicker"><Sparkles /> {current.eyebrow}</span><h1>{current.title}</h1><p>{current.text}</p><div className="hero-actions"><Link className="button button--gold" to="/destinations">Explore all places <ArrowRight /></Link><Link className="button button--ghost-light" to="/contact">Plan my journey</Link></div></motion.div></AnimatePresence></div>
      <div className="container hero-caption"><span><MapPinned /> {current.accent}</span><div>{heroSlides.map((_, index) => <button key={index} aria-label={`Go to slide ${index + 1}`} className={slide === index ? 'active' : ''} onClick={() => setSlide(index)} />)}</div></div>
      <div className="hero-arrows"><button onClick={() => moveSlide(-1)} aria-label="Previous slide"><ArrowLeft /></button><button onClick={() => moveSlide(1)} aria-label="Next slide"><ArrowRight /></button></div>
      <div className="hero-trust-v2"><span><BadgeCheck /> 25 districts</span><span><ShieldCheck /> Secure booking</span><span><Headphones /> Human support</span></div>
    </section>

    <section className="island-numbers"><div className="container"><motion.div initial={{ opacity: 0, y: 18 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}><strong>371</strong><span>curated places</span></motion.div><motion.div initial={{ opacity: 0, y: 18 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: .08 }}><strong>25</strong><span>island districts</span></motion.div><motion.div initial={{ opacity: 0, y: 18 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: .16 }}><strong>8</strong><span>travel themes</span></motion.div><motion.div initial={{ opacity: 0, y: 18 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: .24 }}><strong>1</strong><span>unforgettable island</span></motion.div></div></section>

    <section className="welcome-section section container"><div><span className="eyebrow">Ayubowan • Welcome</span><h2>One island. Hundreds of reasons to explore.</h2></div><div><p>Browse a destination-first collection built across every Sri Lankan district. Find famous icons, ancient places, natural beauty and quieter discoveries in one clear guide.</p><Link className="link-arrow" to="/about">Our approach <ArrowRight /></Link></div></section>

    <section className="journey-section section--tint"><div className="container"><SectionHeading eyebrow="Choose a feeling" title="Find your Sri Lanka" text="Start with what moves you, then discover the places that match." /><div className="journey-row">{journeyStyles.map((item, index) => <motion.div key={item.id} className="journey-orbit" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * .08 }}><Link to={`/destinations?category=${encodeURIComponent(item.category)}`}><span><img src={item.image} alt="" /></span><strong>{item.title}</strong><small>{item.subtitle}</small></Link></motion.div>)}</div></div></section>

    <section className="island-section"><div className="container island-section__grid"><div><span className="eyebrow eyebrow--light">A small island of big stories</span><h2>Every district. One beautiful map.</h2><p>Follow the routes from the far north to the deep south, connecting cultural cities, misty mountains, wild plains and warm seas.</p><div className="island-categories"><span>History & heritage</span><span>Wildlife & nature</span><span>Sacred places</span><span>Coast & beaches</span><span>Waterfalls & trails</span></div><Link className="button button--gold" to="/destinations">Open destination guide <ArrowRight /></Link></div><IslandMap /></div></section>

    <section className="section container"><SectionHeading eyebrow="Editor’s island selection" title="Places that stay with you" text="A rotating selection from across Sri Lanka, managed directly through the admin destination catalogue." action={<Link className="link-arrow" to="/destinations?featured=true">All featured <ArrowRight /></Link>} />{loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : featured.length ? <div className="destination-grid destination-grid--home">{featured.map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div> : <EmptyState title="Destinations are being prepared" text="Start the backend to load the full 25-district catalogue." />}</section>

    <section className="visual-story"><div className="visual-story__image"><img src={images.wildlife} alt="Wild elephants in Sri Lanka" loading="lazy" /></div><div className="visual-story__copy"><span className="eyebrow eyebrow--light">Travel deeper</span><h2>Ancient, wild, sacred and wonderfully alive.</h2><p>From centuries-old stonework to protected landscapes and fishing villages, the island changes character every few hours.</p><div><Link to="/destinations?category=Heritage%20%26%20History">Heritage trails <ArrowRight /></Link><Link to="/destinations?category=Wildlife%20%26%20Nature">Wild places <ArrowRight /></Link><Link to="/destinations?category=Beaches%20%26%20Coast">Coastal escapes <ArrowRight /></Link></div></div><div className="visual-story__tiles"><img src={images.heritage} alt="Galle heritage" loading="lazy" /><img src={images.tea} alt="Sri Lankan highlands" loading="lazy" /><img src={images.coast} alt="Sri Lankan coast" loading="lazy" /></div></section>

    <section className="section container"><SectionHeading eyebrow="Handpicked routes" title="Journeys with room to wander" text="Flexible multi-day tours connecting island highlights with quieter discoveries." action={<Link className="link-arrow" to="/packages">Browse tours <ArrowRight /></Link>} />{loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : data.packages.length ? <div className="cards-grid">{data.packages.slice(0, 3).map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <EmptyState title="Tour packages are coming soon" />}</section>

    <section className="section section--tint"><div className="container testimonials"><SectionHeading eyebrow="Traveller notes" title="Stories brought home" text="A few words from travellers who followed their curiosity." /><div className="testimonial-grid">{testimonials.map((item, index) => <motion.blockquote key={item.name} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * .08 }}><span>“</span><p>{item.quote}</p><footer><strong>{item.name}</strong><small>{item.country}</small></footer></motion.blockquote>)}</div></div></section>
    <section className="container final-cta"><div><span className="eyebrow eyebrow--light">Your island story starts here</span><h2>Ready to turn curiosity into a journey?</h2><p>Tell us what you love. We will help connect the right places.</p></div><Link className="button button--gold" to="/contact">Start planning <ArrowRight /></Link></section>
  </>;
}
