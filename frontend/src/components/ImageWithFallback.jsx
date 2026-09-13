import { useEffect, useState } from 'react';

export default function ImageWithFallback({ src, fallback, alt, ...props }) {
  const [current, setCurrent] = useState(src || fallback);
  useEffect(() => setCurrent(src || fallback), [src, fallback]);
  return <img {...props} src={current} alt={alt} onError={() => setCurrent(fallback)} />;
}
