import { useState } from 'react';
import { ArrowRight, Eye, EyeOff, LockKeyhole, Mail } from 'lucide-react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';
import { images } from '../../data/siteData';
import { getApiError } from '../../services/api';

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [show, setShow] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const submit = async (event) => {
    event.preventDefault(); setSubmitting(true);
    try {
      const user = await login(form);
      toast.success(`Welcome back, ${user.name}!`);
      navigate(user.role === 'ADMIN' ? '/admin/dashboard' : (location.state?.from?.pathname || '/'), { replace: true });
    } catch (error) { toast.error(getApiError(error, 'Invalid email or password.')); }
    finally { setSubmitting(false); }
  };

  return (
    <section className="auth-page">
      <div className="auth-page__visual" style={{ backgroundImage: `url(${images.hero})` }}><div><span className="eyebrow eyebrow--light">Return to your journey</span><h1>The island is waiting.</h1><p>Sign in to manage bookings and continue planning your Sri Lankan escape.</p></div></div>
      <div className="auth-page__form"><div className="auth-card"><span className="eyebrow">Welcome back</span><h2>Sign in to Serendib</h2><p>Enter your account details below.</p><form onSubmit={submit} autoComplete="on"><label className="field"><span>Email address</span><div className="input-icon"><Mail /><input name="email" type="email" autoComplete="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} placeholder="you@example.com" required /></div></label><label className="field"><span>Password</span><div className="input-icon"><LockKeyhole /><input name="password" type={show ? 'text' : 'password'} autoComplete="current-password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} placeholder="Your password" required /><button type="button" onClick={() => setShow(!show)} aria-label={show ? 'Hide password' : 'Show password'}>{show ? <EyeOff /> : <Eye />}</button></div></label><button className="button button--primary button--full" disabled={submitting}>{submitting ? 'Signing in...' : <>Sign in <ArrowRight /></>}</button></form><p className="auth-switch">New here? <Link to="/register">Create an account</Link></p></div></div>
    </section>
  );
}
