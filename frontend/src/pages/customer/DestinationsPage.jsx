import { useCallback, useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import DestinationCard from '../../components/DestinationCard';
import PageHero from '../../components/PageHero';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { destinationApi, getApiError } from '../../services/api';

export default function DestinationsPage() {
  const [items, setItems] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true); setError('');
    try { setItems((await destinationApi.list(search)).data); }
    catch (err) { setError(getApiError(err, 'Could not load destinations.')); }
    finally { setLoading(false); }
  }, [search]);

  useEffect(() => { const timer = setTimeout(load, 280); return () => clearTimeout(timer); }, [load]);

  return (
    <>
      <PageHero eyebrow="Explore Sri Lanka" title="Every corner tells a different story" text="Search coastal cities, cultural landmarks and cool highland escapes." />
      <section className="section container">
        <div className="search-bar"><Search /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search by destination or location..." /></div>
        {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="destination-grid destination-grid--catalog">{items.map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div> : <EmptyState title="No destinations matched" text="Try a different place or search term." />}
      </section>
    </>
  );
}
