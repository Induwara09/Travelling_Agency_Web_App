import { Leaf } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Brand({ light = false }) {
  return (
    <Link className={`brand ${light ? 'brand--light' : ''}`} to="/" aria-label="Serendib Trails home">
      <span className="brand__mark"><Leaf size={22} /></span>
      <span><strong>Serendib</strong><small>TRAILS</small></span>
    </Link>
  );
}
