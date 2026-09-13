import { useState } from 'react';
import { ArrowRight, LockKeyhole, Mail, Phone, UserRound } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';
import { images } from '../../data/siteData';
import { getApiError } from '../../services/api';

export default function RegisterPage() {
  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '' });
  const [submitting, setSubmitting] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });
  const submit = async (event) => {
    event.preventDefault(); setSubmitting(true);
    try { await register(form); toast.success('Your travel account is ready!'); navigate('/packages'); }
    catch (error) { toast.error(getApiError(error, 'Could not create your account.')); }
    finally { setSubmitting(false); }
  };
  return (
    <section className="auth-page auth-page--reverse">
      <div className="auth-page__visual" style={{ backgroundImage: `url(${images.island})` }}><div><span className="eyebrow eyebrow--light">Begin with curiosity</span><h1>Your Sri Lankan story starts here.</h1><p>Save your bookings, follow their status and discover journeys designed for you.</p></div></div>
      <div className="auth-page__form"><div className="auth-card"><span className="eyebrow">Join Serendib Trails</span><h2>Create your account</h2><p>It only takes a moment to start planning.</p><form onSubmit={submit} autoComplete="on"><label className="field"><span>Full name</span><div className="input-icon"><UserRound /><input name="name" autoComplete="name" value={form.name} onChange={update} placeholder="Your name" required maxLength="100" /></div></label><label className="field"><span>Email address</span><div className="input-icon"><Mail /><input name="email" type="email" autoComplete="email" value={form.email} onChange={update} placeholder="you@example.com" required /></div></label><label className="field"><span>Phone number</span><div className="input-icon"><Phone /><input name="phone" type="tel" autoComplete="tel" value={form.phone} onChange={update} placeholder="+94 77 123 4567" maxLength="30" /></div></label><label className="field"><span>Password</span><div className="input-icon"><LockKeyhole /><input name="password" type="password" autoComplete="new-password" value={form.password} onChange={update} placeholder="Minimum 8 characters" minLength="8" required /></div></label><button className="button button--primary button--full" disabled={submitting}>{submitting ? 'Creating account...' : <>Create account <ArrowRight /></>}</button></form><p className="auth-switch">Already a traveller? <Link to="/login">Sign in</Link></p></div></div>
    </section>
  );
}
