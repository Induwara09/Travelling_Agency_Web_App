import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowRight, BadgeCheck, Compass, Headphones, Leaf, MapPinned, ShieldCheck, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import DestinationCard from '../../components/DestinationCard';
import PackageCard from '../../components/PackageCard';
import HotelCard from '../../components/HotelCard';
import SectionHeading from '../../components/SectionHeading';
import { EmptyState } from '../../components/StateViews';
import { destinationApi, hotelApi, packageApi } from '../../services/api';
import { images, testimonials } from '../../data/siteData';

export default function HomePage() {
  const [data, setData] = useState({ destinations: [], packages: [], hotels: [] });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      destinationApi.list(),
      packageApi.list({ status: 'ACTIVE' }),
      hotelApi.list({ available: true })
    ]).then(([destinations, packages, hotels]) => setData({
      destinations: destinations.data,
      packages: packages.data,
      hotels: hotels.data
    })).catch(() => {
      // The page remains usable while the backend is starting or temporarily offline.
      setData({ destinations: [], packages: [], hotels: [] });
    }).finally(() => setLoading(false));
  }, []);

  return (
    <>
      <section className="home-hero" style={{ backgroundImage: `url(${images.hero})` }}>
        <div className="home-hero__overlay" />
        <div className="container home-hero__content">
          <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: .65 }}>
            <span className="hero-kicker"><Sparkles /> Tailor-made island journeys</span>
            <h1>Find your wild.<br /><em>Feel Sri Lanka.</em></h1>
            <p>From misty tea country to golden southern shores, discover thoughtful escapes created around the way you love to travel.</p>
            <div className="hero-actions"><Link className="button button--gold" to="/packages">Explore tours <ArrowRight /></Link><Link className="button button--ghost-light" to="/destinations">See destinations</Link></div>
            <div className="hero-trust"><span><BadgeCheck /> Local expertise</span><span><ShieldCheck /> Secure booking</span><span><Headphones /> Personal support</span></div>
          </motion.div>
        </div>
        <div className="hero-floating-card"><span className="hero-floating-card__icon"><MapPinned /></span><div><small>Signature route</small><strong>Colombo → Kandy → Ella → Galle</strong></div></div>
      </section>

      <section className="stats-strip"><div className="container stats-grid"><div><strong>20+</strong><span>Curated experiences</span></div><div><strong>4.9</strong><span>Traveller rating</span></div><div><strong>100%</strong><span>Local knowledge</span></div><div><strong>24/7</strong><span>Trip assistance</span></div></div></section>

      <section className="section container">
        <SectionHeading eyebrow="Places that stay with you" title="Discover the island, one story at a time" text="Ancient kingdoms, emerald highlands and sunlit coasts—each region has its own rhythm." action={<Link className="link-arrow" to="/destinations">View all <ArrowRight /></Link>} />
        {loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : data.destinations.length ? <div className="destination-grid">{data.destinations.slice(0, 4).map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div> : <EmptyState title="Destinations are being curated" text="An administrator can add destinations from the dashboard." />}
      </section>

      <section className="section section--tint">
        <div className="container">
          <SectionHeading eyebrow="Handpicked escapes" title="Journeys made for more than sightseeing" text="Flexible, immersive tours with room to slow down and connect." action={<Link className="link-arrow" to="/packages">Browse tours <ArrowRight /></Link>} />
          {loading ? <div className="card-skeleton-grid"><i /><i /><i /></div> : data.packages.length ? <div className="cards-grid">{data.packages.slice(0, 3).map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <EmptyState title="Tour packages are coming soon" />}
        </div>
      </section>

      <section className="section container">
        <div className="story-panel">
          <div className="story-panel__image"><img src={images.hotel} alt="Luxury eco resort overlooking Sri Lankan tea country" /></div>
          <div className="story-panel__content"><span className="eyebrow">Travel gently</span><h2>Luxury that feels closer to nature</h2><p>Choose stays that celebrate local design, regional food and the landscapes that make Sri Lanka extraordinary.</p><ul><li><Leaf /> Eco-conscious recommendations</li><li><Compass /> Locally inspired experiences</li><li><ShieldCheck /> Trusted 3–5 star stays</li></ul><Link className="button button--primary" to="/hotels">Explore stays <ArrowRight /></Link></div>
        </div>
      </section>

      <section className="section section--dark">
        <div className="container">
          <SectionHeading eyebrow="Beautiful stays" title="Rest somewhere remarkable" text="Explore trusted 3–5 star hotels and continue to each official hotel website for live availability and rates." action={<Link className="link-arrow link-arrow--light" to="/hotels">See hotels <ArrowRight /></Link>} />
          {data.hotels.length ? <div className="cards-grid">{data.hotels.slice(0, 3).map((hotel, index) => <HotelCard key={hotel.id} hotel={hotel} index={index} />)}</div> : <EmptyState title="Hotel collection is being prepared" />}
        </div>
      </section>

      <section className="section container testimonials">
        <SectionHeading eyebrow="Traveller notes" title="Stories brought home" text="A few words from travellers who followed their curiosity." />
        <div className="testimonial-grid">{testimonials.map((item, index) => <motion.blockquote key={item.name} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: index * .08 }}><span>“</span><p>{item.quote}</p><footer><strong>{item.name}</strong><small>{item.country}</small></footer></motion.blockquote>)}</div>
      </section>

      <section className="container final-cta"><div><span className="eyebrow eyebrow--light">Your island story starts here</span><h2>Ready to trade ordinary for unforgettable?</h2><p>Create your account, choose a journey and let Sri Lanka surprise you.</p></div><Link className="button button--gold" to="/register">Start planning <ArrowRight /></Link></section>
    </>
  );
}
