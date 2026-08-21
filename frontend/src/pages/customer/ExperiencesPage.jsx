import { useEffect, useMemo, useState } from 'react';
import PageHero from '../../components/PageHero';
import ExperienceCard from '../../components/ExperienceCard';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { experienceApi, getApiError } from '../../services/api';
import { experienceFallbacks, images } from '../../data/siteData';

export default function ExperiencesPage() {
  const [items, setItems] = useState([]);
  const [category, setCategory] = useState('All');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  useEffect(() => { experienceApi.list({ active: true }).then((response) => setItems(response.data.length ? response.data : experienceFallbacks)).catch((err) => { setItems(experienceFallbacks); setError(getApiError(err, 'Live experiences are temporarily unavailable. Showing our signature collection.')); }).finally(() => setLoading(false)); }, []);
  const categories = ['All', ...new Set(items.map((item) => item.category))];
  const visible = useMemo(() => category === 'All' ? items : items.filter((item) => item.category === category), [category, items]);
  return <><PageHero eyebrow="Feel the island" title="Experiences worth travelling for" text="Choose the moments that matter to you—from first-light fortresses to the last train through tea country." image={images.wildlife} /><section className="section container"><div className="filter-pills">{categories.map((item) => <button key={item} className={category === item ? 'active' : ''} onClick={() => setCategory(item)}>{item}</button>)}</div>{loading ? <LoadingState /> : <>{error && <div className="inline-notice">{error}</div>}<div className="experience-grid">{visible.map((item, index) => <ExperienceCard key={item.id} item={item} index={index} />)}</div></>}</section></>;
}
