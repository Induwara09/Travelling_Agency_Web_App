import { useCallback, useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import HotelCard from '../../components/HotelCard';
import PageHero from '../../components/PageHero';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { getApiError, hotelApi } from '../../services/api';

export default function HotelsPage() {
  const [items, setItems] = useState([]);
  const [filters, setFilters] = useState({ search: '', starRating: '', maxPrice: '', available: 'true' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => {
    setLoading(true); setError('');
    const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== ''));
    try { setItems((await hotelApi.list(params)).data); }
    catch (err) { setError(getApiError(err, 'Could not load hotels.')); }
    finally { setLoading(false); }
  }, [filters]);
  useEffect(() => { const timer = setTimeout(load, 250); return () => clearTimeout(timer); }, [load]);
  const update = (event) => setFilters((current) => ({ ...current, [event.target.name]: event.target.value }));

  return (
    <>
      <PageHero eyebrow="Beautiful stays" title="Wake up somewhere unforgettable" text="Browse selected 3–5 star hotels, then visit the official hotel website for live room availability and pricing." />
      <section className="section container">
        <div className="filter-panel filter-panel--hotels"><label className="field field--search"><Search /><input name="search" value={filters.search} onChange={update} placeholder="Search hotel or location..." /></label><label className="field"><span>Hotel class</span><select name="starRating" value={filters.starRating} onChange={update}><option value="">3–5 stars</option><option value="3">3 stars</option><option value="4">4 stars</option><option value="5">5 stars</option></select></label><label className="field"><span>Up to / night</span><select name="maxPrice" value={filters.maxPrice} onChange={update}><option value="">Any rate</option><option value="30000">LKR 30,000</option><option value="60000">LKR 60,000</option><option value="120000">LKR 120,000</option></select></label></div>
        <p className="rate-note">Displayed rates are indicative values entered by the administrator. Check the official website for current dates, taxes and room types.</p>
        {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="cards-grid">{items.map((hotel, index) => <HotelCard key={hotel.id} hotel={hotel} index={index} />)}</div> : <EmptyState title="No hotels matched" text="Try another location, class or nightly rate." />}
      </section>
    </>
  );
}
