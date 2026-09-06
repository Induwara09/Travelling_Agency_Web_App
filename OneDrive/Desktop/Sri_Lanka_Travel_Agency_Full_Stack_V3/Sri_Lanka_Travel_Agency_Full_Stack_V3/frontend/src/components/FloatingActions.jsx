import { MessageCircleMore, Phone } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function FloatingActions() {
  return <div className="floating-actions"><a href="tel:+94112345678" aria-label="Call Serendib Trails"><Phone /></a><Link to="/contact" aria-label="Plan a trip"><MessageCircleMore /></Link></div>;
}
