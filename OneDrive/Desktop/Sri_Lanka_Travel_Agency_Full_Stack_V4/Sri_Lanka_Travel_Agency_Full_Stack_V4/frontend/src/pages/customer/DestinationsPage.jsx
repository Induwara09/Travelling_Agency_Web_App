import { useCallback, useEffect, useMemo, useState } from 'react';
import { ChevronLeft, ChevronRight, RotateCcw, Search, SlidersHorizontal } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import DestinationCard from '../../components/DestinationCard';
import PageHero from '../../components/PageHero';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { destinationCategories, districts } from '../../data/siteData';
import { destinationApi, getApiError } from '../../services/api';

const PAGE_SIZE = 18;

export default function DestinationsPage() {
  const [params, setParams] = useSearchParams();
  const [items, setItems] = useState([]);
  const [search, setSearch] = useState(params.get('search') || '');
  const [district, setDistrict] = useState(params.get('district') || '');
  const [category, setCategory] = useState(params.get('category') || '');
  const [featured, setFeatured] = useState(params.get('featured') === 'true');
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true); setError('');
    try {
      const query = { search: search || undefined, district: district || undefined, category: category || undefined, featured: featured || undefined };
      setItems((await destinationApi.list(query)).data);
      const visible = Object.fromEntries(Object.entries(query).filter(([, value]) => value !== undefined));
      setParams(visible, { replace: true });
    } catch (err) { setError(getApiError(err, 'Could not load destinations.')); }
    finally { setLoading(false); }
  }, [search, district, category, featured, setParams]);

  useEffect(() => { const timer = setTimeout(load, 260); return () => clearTimeout(timer); }, [load]);
  useEffect(() => setPage(1), [search, district, category, featured]);

  const pageCount = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
  const visibleItems = useMemo(() => items.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE), [items, page]);
  const clear = () => { setSearch(''); setDistrict(''); setCategory(''); setFeatured(false); };

  return (
    <>
      <PageHero eyebrow="25 districts • 371 places" title="The complete Sri Lanka collection" text="Find ancient cities, sacred landmarks, beaches, waterfalls, wildlife and lesser-known local treasures." />
      <section className="section container destination-catalogue">
        <div className="catalogue-filter">
          <label className="field field--search"><Search /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search a place, location or tag..." /></label>
          <label className="field"><span>District</span><select value={district} onChange={(event) => setDistrict(event.target.value)}><option value="">All 25 districts</option>{districts.map((item) => <option key={item}>{item}</option>)}</select></label>
          <label className="field"><span>Travel interest</span><select value={category} onChange={(event) => setCategory(event.target.value)}><option value="">All categories</option>{destinationCategories.map((item) => <option key={item}>{item}</option>)}</select></label>
          <label className="featured-toggle"><input type="checkbox" checked={featured} onChange={(event) => setFeatured(event.target.checked)} /><span>Featured only</span></label>
          <button className="button button--secondary" type="button" onClick={clear}><RotateCcw /> Reset</button>
        </div>
        <div className="catalogue-summary"><div><SlidersHorizontal /><strong>{items.length}</strong> places found</div><span>Page {page} of {pageCount}</span></div>
        {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : visibleItems.length ? <div className="destination-grid destination-grid--catalog">{visibleItems.map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div> : <EmptyState title="No destinations matched" text="Try another district, category or search term." />}
        {!loading && pageCount > 1 && <nav className="catalogue-pagination" aria-label="Destination pages"><button disabled={page === 1} onClick={() => setPage((value) => value - 1)}><ChevronLeft /> Previous</button><span>{page} / {pageCount}</span><button disabled={page === pageCount} onClick={() => setPage((value) => value + 1)}>Next <ChevronRight /></button></nav>}
      </section>
    </>
  );
}
