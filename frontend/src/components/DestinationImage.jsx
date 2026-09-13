import { useEffect, useMemo, useState } from 'react';

const API_URL = 'https://commons.wikimedia.org/w/api.php';
const MAX_CONCURRENT_SEARCHES = 4;
const memoryCache = new Map();
const queue = [];
let activeSearches = 0;

const normalize = (value = '') => value
  .toLowerCase()
  .normalize('NFKD')
  .replace(/[^a-z0-9]+/g, ' ')
  .trim();

const cacheKey = (name, district) => `serendib-image:${normalize(name)}:${normalize(district)}`;

const enqueue = (task) => new Promise((resolve, reject) => {
  queue.push({ task, resolve, reject });
  runQueue();
});

function runQueue() {
  while (activeSearches < MAX_CONCURRENT_SEARCHES && queue.length) {
    const next = queue.shift();
    activeSearches += 1;
    next.task().then(next.resolve, next.reject).finally(() => {
      activeSearches -= 1;
      runQueue();
    });
  }
}

const scorePage = (page, name) => {
  const title = normalize(page.title?.replace(/^file:/i, ''));
  const exact = normalize(name);
  const tokens = exact.split(' ').filter((token) => token.length > 2);
  let score = title.includes(exact) ? 100 : 0;
  tokens.forEach((token) => { if (title.includes(token)) score += 12; });
  if (title.includes('sri lanka') || title.includes('srilanka')) score += 8;
  return score;
};

async function searchCommons(name, district) {
  const key = cacheKey(name, district);
  if (memoryCache.has(key)) return memoryCache.get(key);

  try {
    const stored = window.sessionStorage.getItem(key);
    if (stored) {
      const parsed = JSON.parse(stored);
      memoryCache.set(key, Promise.resolve(parsed));
      return parsed;
    }
  } catch { /* Private browsing may disable storage; live search still works. */ }

  const request = enqueue(async () => {
    const params = new URLSearchParams({
      action: 'query', format: 'json', origin: '*', generator: 'search',
      gsrsearch: `${name} ${district || ''} Sri Lanka`, gsrnamespace: '6', gsrlimit: '8',
      prop: 'imageinfo', iiprop: 'url|mime|size', iiurlwidth: '1200'
    });
    const response = await fetch(`${API_URL}?${params.toString()}`);
    if (!response.ok) throw new Error('Commons image search failed');
    const payload = await response.json();
    const pages = Object.values(payload.query?.pages || {})
      .filter((page) => {
        const info = page.imageinfo?.[0];
        return info?.mime?.startsWith('image/') && info.mime !== 'image/svg+xml' && (info.width || 0) >= 600;
      })
      .sort((a, b) => scorePage(b, name) - scorePage(a, name));
    const page = pages[0];
    const info = page?.imageinfo?.[0];
    const result = info ? {
      imageUrl: info.thumburl || info.url,
      sourceUrl: info.descriptionurl,
      title: page.title?.replace(/^File:/i, '')
    } : null;
    if (result) {
      try { window.sessionStorage.setItem(key, JSON.stringify(result)); }
      catch { /* Caching is an optimization, not a requirement. */ }
    }
    return result;
  });

  memoryCache.set(key, request);
  return request;
}

const isCommonsSeed = (value = '') => /commons\.wikimedia\.org|upload\.wikimedia\.org/i.test(value || '');

const isLikelyExactSeed = (destination) => {
  if (!destination?.imageUrl || !isCommonsSeed(destination.imageUrl)) return false;
  let source = destination.imageSourceUrl || destination.imageUrl;
  try { source = decodeURIComponent(source); } catch { /* Keep the encoded URL. */ }
  const sourceName = normalize(source);
  const generic = new Set(['sri', 'lanka', 'national', 'park', 'temple', 'beach', 'falls', 'waterfall', 'fort', 'district', 'main', 'access', 'the', 'and']);
  const distinctive = normalize(destination.name).split(' ').filter((token) => token.length >= 4 && !generic.has(token));
  return distinctive.some((token) => sourceName.includes(token.slice(0, Math.min(token.length, 6))));
};

export default function DestinationImage({ destination, fallback, onResolved, ...props }) {
  const customImage = destination?.imageUrl && (destination.imageMode === 'CUSTOM' || !isCommonsSeed(destination.imageUrl)) ? destination.imageUrl : null;
  const exactSeed = isLikelyExactSeed(destination) ? destination.imageUrl : null;
  const initial = customImage || exactSeed || fallback;
  const [current, setCurrent] = useState(initial);
  const searchIdentity = useMemo(() => `${destination?.name || ''}|${destination?.district || ''}`, [destination?.name, destination?.district]);

  useEffect(() => {
    let mounted = true;
    setCurrent(initial);
    if (customImage) {
      onResolved?.({ imageUrl: customImage, sourceUrl: destination?.imageSourceUrl || null, title: destination?.name });
      return () => { mounted = false; };
    }
    searchCommons(destination?.name || '', destination?.district || '')
      .then((result) => {
        if (!mounted || !result?.imageUrl) return;
        setCurrent(result.imageUrl);
        onResolved?.(result);
      })
      .catch(() => { if (mounted) setCurrent(exactSeed || fallback); });
    return () => { mounted = false; };
  }, [searchIdentity, customImage, exactSeed, initial, fallback]);

  return <img {...props} src={current} alt={props.alt || destination?.name || ''} onError={() => setCurrent(fallback)} />;
}
