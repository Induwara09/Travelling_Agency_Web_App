import { useEffect, useMemo, useState } from 'react';
import { CalendarDays, Minus, Plus, ShieldCheck, UsersRound } from 'lucide-react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import ImageWithFallback from '../../components/ImageWithFallback';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { images } from '../../data/siteData';
import { bookingApi, getApiError, packageApi } from '../../services/api';
import { formatCurrency, toDateInput } from '../../utils/format';

export default function BookingPage() {
  const { packageId } = useParams();
  const [item, setItem] = useState(null);
  const [form, setForm] = useState({ travelDate: '', numberOfGuests: 2 });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  useEffect(() => { packageApi.get(packageId).then((response) => setItem(response.data)).catch((err) => setError(getApiError(err))); }, [packageId]);
  const total = useMemo(() => Number(item?.price || 0) * Number(form.numberOfGuests), [item, form.numberOfGuests]);
  const submit = async (event) => {
    event.preventDefault(); setSubmitting(true);
    try { await bookingApi.create({ packageId: Number(packageId), travelDate: form.travelDate, numberOfGuests: Number(form.numberOfGuests) }); toast.success('Your booking request has been created!'); navigate('/my-bookings'); }
    catch (err) { toast.error(getApiError(err, 'Could not create the booking.')); }
    finally { setSubmitting(false); }
  };
  if (error) return <div className="section container"><ErrorState message={error} /></div>;
  if (!item) return <LoadingState />;
  return (
    <section className="section container booking-page"><div className="booking-page__intro"><span className="eyebrow">Secure your journey</span><h1>One step closer to Sri Lanka</h1><p>Choose your preferred date and travelling party. The agency will confirm the next steps.</p></div><div className="booking-page__grid"><div className="booking-summary"><ImageWithFallback src={item.imageUrl} fallback={images.package} alt={item.name} /><div><span className="pill">{item.category}</span><h2>{item.name}</h2><p><CalendarDays /> {item.durationDays} days</p><p>From <strong>{formatCurrency(item.price)}</strong> per person</p></div></div><form className="booking-form" onSubmit={submit}><label className="field"><span>Travel date</span><input type="date" min={toDateInput()} value={form.travelDate} onChange={(event) => setForm({ ...form, travelDate: event.target.value })} required /></label><div className="field"><span>Number of guests</span><div className="guest-stepper"><button type="button" onClick={() => setForm({ ...form, numberOfGuests: Math.max(1, form.numberOfGuests - 1) })}><Minus /></button><div><UsersRound /><strong>{form.numberOfGuests}</strong><small>travellers</small></div><button type="button" onClick={() => setForm({ ...form, numberOfGuests: Math.min(20, form.numberOfGuests + 1) })}><Plus /></button></div></div><div className="booking-total"><span>Estimated total</span><strong>{formatCurrency(total)}</strong><small>Final inclusions and payment details are confirmed by the agency.</small></div><button className="button button--primary button--full" disabled={submitting}>{submitting ? 'Creating booking...' : 'Confirm booking request'}</button><p className="secure-note"><ShieldCheck /> Your booking starts as PENDING. No online payment is collected here.</p></form></div></section>
  );
}
