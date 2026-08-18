import { useEffect, useState } from 'react';
import { ArrowLeft, CalendarDays, Check, MapPin, ShieldCheck } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import ImageWithFallback from '../../components/ImageWithFallback';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { images } from '../../data/siteData';
import { getApiError, packageApi } from '../../services/api';
import { formatCurrency } from '../../utils/format';

export default function PackageDetailsPage() {
  const { id } = useParams();
  const [item, setItem] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { packageApi.get(id).then((result) => setItem(result.data)).catch((err) => setError(getApiError(err, 'Package not found.'))); }, [id]);
  if (error) return <div className="section container"><ErrorState message={error} /></div>;
  if (!item) return <LoadingState />;

  return (
    <section className="section container product-details">
      <Link className="back-link" to="/packages"><ArrowLeft /> Back to tours</Link>
      <div className="product-details__grid">
        <div className="product-details__visual"><ImageWithFallback src={item.imageUrl} fallback={images.package} alt={item.name} /><span className="pill pill--gold">{item.category}</span></div>
        <div className="product-details__content"><span className="eyebrow">Signature journey</span><h1>{item.name}</h1><div className="product-meta"><span><CalendarDays /> {item.durationDays} days</span><span><MapPin /> {item.destinationName}</span></div><p>{item.description}</p><div className="included-list"><span><Check /> Curated itinerary</span><span><Check /> Local assistance</span><span><Check /> Flexible travel date</span></div><div className="booking-box"><div><small>From per person</small><strong>{formatCurrency(item.price)}</strong></div><Link className={`button button--primary ${item.status !== 'ACTIVE' ? 'is-disabled' : ''}`} to={`/booking/${item.id}`}>Book this journey</Link></div><p className="secure-note"><ShieldCheck /> Secure account-based booking. Payment status is managed by the agency.</p></div>
      </div>
    </section>
  );
}
