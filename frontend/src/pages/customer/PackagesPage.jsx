import { useCallback, useEffect, useState } from 'react';
import { Filter, Search } from 'lucide-react';
import PackageCard from '../../components/PackageCard';
import PageHero from '../../components/PageHero';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { categories } from '../../data/siteData';
import { getApiError, packageApi } from '../../services/api';

export default function PackagesPage() {
  const [items, setItems] = useState([]);
  const [filters, setFilters] = useState({ search: '', category: '', maxPrice: '', maxDuration: '', status: 'ACTIVE' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true); setError('');
    const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== ''));
    try { setItems((await packageApi.list(params)).data); }
    catch (err) { setError(getApiError(err, 'Could not load tour packages.')); }
    finally { setLoading(false); }
  }, [filters]);

  useEffect(() => { const timer = setTimeout(load, 250); return () => clearTimeout(timer); }, [load]);
  const update = (event) => setFilters((current) => ({ ...current, [event.target.name]: event.target.value }));

  return (
    <>
      <PageHero eyebrow="Curated journeys" title="Travel deeper, not faster" text="Choose an island experience shaped around culture, nature and time to truly connect." />
      <section className="section container">
        <div className="filter-panel">
          <label className="field field--search"><Search /><input name="search" value={filters.search} onChange={update} placeholder="Search tours..." /></label>
          <label className="field"><span>Category</span><select name="category" value={filters.category} onChange={update}><option value="">All categories</option>{categories.map((category) => <option key={category}>{category}</option>)}</select></label>
          <label className="field"><span>Budget up to</span><select name="maxPrice" value={filters.maxPrice} onChange={update}><option value="">Any budget</option><option value="50000">LKR 50,000</option><option value="100000">LKR 100,000</option><option value="250000">LKR 250,000</option><option value="500000">LKR 500,000</option></select></label>
          <label className="field"><span>Duration</span><select name="maxDuration" value={filters.maxDuration} onChange={update}><option value="">Any length</option><option value="3">Up to 3 days</option><option value="7">Up to 7 days</option><option value="14">Up to 14 days</option></select></label>
          <button className="icon-filter" title="Filters are applied automatically"><Filter /></button>
        </div>
        <div className="results-summary"><strong>{items.length}</strong> journeys found</div>
        {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="cards-grid">{items.map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <EmptyState title="No journeys matched" text="Try expanding your budget, duration or category." />}
      </section>
    </>
  );
}
