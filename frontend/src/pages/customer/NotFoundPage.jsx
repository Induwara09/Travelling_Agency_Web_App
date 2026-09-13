import { Compass } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return <main className="not-found"><Compass /><span className="eyebrow">404 · Off the map</span><h1>This path ends here.</h1><p>Let’s take you back to the island journey.</p><Link className="button button--primary" to="/">Return home</Link></main>;
}
