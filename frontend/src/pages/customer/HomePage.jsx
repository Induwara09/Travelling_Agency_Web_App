import { useEffect, useState } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, BadgeCheck, Compass, Headphones, Leaf, MapPinned, ShieldCheck, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import DestinationCard from '../../components/DestinationCard';
import ExperienceCard from '../../components/ExperienceCard';
import HotelCard from '../../components/HotelCard';
import IslandMap from '../../components/IslandMap';
import PackageCard from '../../components/PackageCard';
import SectionHeading from '../../components/SectionHeading';
import { EmptyState } from '../../components/StateViews';
import { destinationApi, experienceApi, hotelApi, packageApi } from '../../services/api';
import { experienceFallbacks, heroSlides, images, journeyStyles, testimonials } from '../../data/siteData';

export default function HomePage() {
  const [data, setData] = useState({ destinations: [], packages: [], hotels: [], experiences: experienceFallbacks });
  const [loading, setLoading] = useState(true);
  const [slide, setSlide] = useState(0);

  useEffect(() => {
    let active = true;
    const load = async () => {
      const results = await Promise.allSettled([destinationApi.list(), packageApi.list({ status: 'ACTIVE' }), hotelApi.list({ available: true }), experienceApi.list({ active: true })]);
      if (!active) return;
      setData({
        destinations: results[0].status === 'fulfilled' ? results[0].value.data : [],
        packages: results[1].status === 'fulfilled' ? results[1].value.data : [],
        hotels: results[2].status === 'fulfilled' ? results[2].value.data : [],
        experiences: results[3].status === 'fulfilled' && results[3].value.data.length ? results[3].value.data : experienceFallbacks
      });
      setLoading(false);
    };
    load();
    return () => { active = false; };
  }, []);

  useEffect(() => {
    const timer = window.setInterval(() => setSlide((value) => (value + 1) % heroSlides.length), 6500);
    return () => window.clearInterval(timer);
  }, []);

  const moveSlide = (direction) => setSlide((value) => (value + direction + heroSlides.length) % heroSlides.length);
  const current = heroSlides[slide];

  return <>
    <section className="cinematic-hero">
      <AnimatePresence mode="wait"><motion.img key={current.image} className="cinematic-hero__image" src={current.image} alt="Sri Lankan landscape" initial={{ opacity: 0, scale: 1.05 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0 }} transition={{ duration: 1.1 }} /></AnimatePresence>
      <div className="cinematic-hero__shade" />
      <div className="container cinematic-hero__content"><AnimatePresence mode="wait"><motion.div key={current.title} initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -18 }} transition={{ duration: .55 }}><span className="hero-kicker"><Sparkles /> {current.eyebrow}</span><h1>{current.title}</h1><p>{current.text}</p><div className="hero-actions"><Link className="button button--gold" to="/contact">Design my journey <ArrowRight /></Link><Link className="button button--ghost-light" to="/destinations">Explore Sri Lanka</Link></div></motion.div></AnimatePresence></div>
      <div className="container hero-caption"><span><MapPinned /> {current.accent}</span><div>{heroSlides.map((_, index) => <button key={index} aria-label={`Go to slide ${index + 1}`} className={slide === index ? 'active' : ''} onClick={() => setSlide(index)} />)}</div></div>
      <div className="hero-arrows"><button onClick={() => moveSlide(-1)} aria-label="Previous slide"><ArrowLeft /></button><button onClick={() => moveSlide(1)} aria-label="Next slide"><ArrowRight /></button></div>
      <div className="hero-trust-v2"><span><BadgeCheck /> Local perspective</span><span><ShieldCheck /> Secure booking</span><span><Headphones /> Human support</span></div>
    </section>

    <section className="welcome-section section container"><div><span className="eyebrow">Ayubowan • Welcome</span><h2>One island. A thousand ways to feel alive.</h2></div><div><p>We connect the places you have imagined with the small, unscripted moments you never could. Travel slowly, follow your curiosity and let Sri Lanka meet you along the way.</p><Link className="link-arrow" to="/about">Our approach <ArrowRight /></Link></div></section>

    <section className="journey-section section--tint"><div className="container"><SectionHeading eyebrow="The paths are many—you choose" title="What’s your journey?" text="Start with a feeling. We will help shape the route." /><div className="journey-row">{journeyStyles.map((item, index) => <motion.div key={item.id} className="journey-orbit" initial={{ opacity: 0, y: 24 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * .08 }}><Link to={`/experiences?category=${item.category}`}><span><img src={item.image} alt="" /></span><strong>{item.title}</strong><small>{item.subtitle}</small></Link></motion.div>)}</div></div></section>

    <section className="island-section"><div className="container island-section__grid"><div><span className="eyebrow eyebrow--light">A small island of big stories</span><h2>From ancient rock to ocean edge.</h2><p>Move through changing landscapes in a single day: cultural cities, misty mountains, wild plains and warm southern seas.</p><div className="island-categories"><span>History & culture</span><span>Wildlife & nature</span><span>Tea country</span><span>Coast & surf</span><span>Food & craft</span></div><Link className="button button--gold" to="/destinations">Discover destinations <ArrowRight /></Link></div><IslandMap /></div></section>

    <section className="section container"><SectionHeading eyebrow="Moments worth the journey" title="Feel Sri Lanka, not just see it" text="Immersive experiences created around nature, culture and local connection." action={<Link className="link-arrow" to="/experiences">All experiences <ArrowRight /></Link>} /><div className="experience-grid">{data.experiences.slice(0, 3).map((item, index) => <ExperienceCard key={item.id} item={item} index={index} />)}</div></section>

    <section className="section section--tint"><div className="container"><SectionHeading eyebrow="Places that stay with you" title="The island, one story at a time" text="Ancient kingdoms, emerald highlands and sunlit coasts—each region has its own rhythm." action={<Link className="link-arrow" to="/destinations">View all <ArrowRight /></Link>} />{loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : data.destinations.length ? <div className="destination-grid">{data.destinations.slice(0, 4).map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div> : <EmptyState title="Destinations are being curated" text="An administrator can add destinations from the dashboard." />}</div></section>

    <section className="section container"><SectionHeading eyebrow="Handpicked routes" title="Journeys with room to wander" text="Flexible multi-day tours connecting the island’s highlights with quieter discoveries." action={<Link className="link-arrow" to="/packages">Browse tours <ArrowRight /></Link>} />{loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : data.packages.length ? <div className="cards-grid">{data.packages.slice(0, 3).map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <EmptyState title="Tour packages are coming soon" />}</section>

    <section className="section container"><div className="story-panel"><div className="story-panel__image"><img src={images.hotel} alt="Luxury eco resort overlooking Sri Lankan tea country" /></div><div className="story-panel__content"><span className="eyebrow">Travel gently</span><h2>Luxury that feels closer to nature</h2><p>Choose stays that celebrate local design, regional food and the landscapes that make Sri Lanka extraordinary.</p><ul><li><Leaf /> Eco-conscious recommendations</li><li><Compass /> Locally inspired experiences</li><li><ShieldCheck /> Trusted 3–5 star stays</li></ul><Link className="button button--primary" to="/hotels">Explore stays <ArrowRight /></Link></div></div></section>

    <section className="section section--dark"><div className="container"><SectionHeading eyebrow="Beautiful stays" title="Rest somewhere remarkable" text="Explore trusted 3–5 star hotels and continue to official hotel websites for live availability and rates." action={<Link className="link-arrow link-arrow--light" to="/hotels">See hotels <ArrowRight /></Link>} />{data.hotels.length ? <div className="cards-grid">{data.hotels.slice(0, 3).map((hotel, index) => <HotelCard key={hotel.id} hotel={hotel} index={index} />)}</div> : <EmptyState title="Hotel collection is being prepared" />}</div></section>

    <section className="section container testimonials"><SectionHeading eyebrow="Traveller notes" title="Stories brought home" text="A few words from travellers who followed their curiosity." /><div className="testimonial-grid">{testimonials.map((item, index) => <motion.blockquote key={item.name} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * .08 }}><span>“</span><p>{item.quote}</p><footer><strong>{item.name}</strong><small>{item.country}</small></footer></motion.blockquote>)}</div></section>
    <section className="container final-cta"><div><span className="eyebrow eyebrow--light">Your island story starts here</span><h2>Ready to trade ordinary for unforgettable?</h2><p>Tell us how you want to feel. We will help turn it into a journey.</p></div><Link className="button button--gold" to="/contact">Start planning <ArrowRight /></Link></section>
  </>;
}
