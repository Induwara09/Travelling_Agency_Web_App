import { useEffect, useState } from 'react';
import { ArrowLeft, ExternalLink, MapPin, Star } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import ImageWithFallback from '../../components/ImageWithFallback';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { images } from '../../data/siteData';
import { getApiError, hotelApi } from '../../services/api';
import { formatCurrency } from '../../utils/format';

export default function HotelDetailsPage() {
  const { id } = useParams();
  const [hotel, setHotel] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { hotelApi.get(id).then((result) => setHotel(result.data)).catch((err) => setError(getApiError(err, 'Hotel not found.'))); }, [id]);
  if (error) return <div className="section container"><ErrorState message={error} /></div>;
  if (!hotel) return <LoadingState />;
  return (
    <section className="section container product-details">
      <Link className="back-link" to="/hotels"><ArrowLeft /> Back to hotels</Link>
      <div className="product-details__grid">
        <div className="product-details__visual"><ImageWithFallback src={hotel.imageUrl} fallback={images.hotel} alt={hotel.name} /><span className="pill pill--gold">{hotel.starRating || 4} star stay</span></div>
        <div className="product-details__content"><span className="eyebrow">Selected stay</span><h1>{hotel.name}</h1><div className="product-meta"><span><MapPin /> {hotel.location}</span><span><Star fill="currentColor" /> {hotel.rating || 'New'} guest rating</span></div><p>{hotel.description || 'A comfortable Sri Lankan stay selected for its location and guest experience.'}</p><div className="booking-box"><div><small>Indicative from / night</small><strong>{formatCurrency(hotel.pricePerNight)}</strong></div>{hotel.websiteUrl ? <a className="button button--primary" href={hotel.websiteUrl} target="_blank" rel="noreferrer">Official website <ExternalLink /></a> : <span className="status status--pending">Website coming soon</span>}</div><p className="rate-note">Room rates change by date, room type, guests, promotions and taxes. Confirm the live rate on the official website.</p></div>
      </div>
    </section>
  );
}
