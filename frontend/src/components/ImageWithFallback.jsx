import { useState } from 'react';

export default function ImageWithFallback({ src, fallback, alt, ...props }) {
  const [current, setCurrent] = useState(src || fallback);
  return <img {...props} src={current} alt={alt} onError={() => setCurrent(fallback)} />;
}
