import heroSigiriya from '../assets/hero-sigiriya-v2.webp';
import coastSunset from '../assets/coast-sunset.webp';
import teaTrain from '../assets/tea-train.webp';
import wildlifeElephants from '../assets/wildlife-elephants.webp';
import galleHeritage from '../assets/galle-heritage.webp';
import hotelImage from '../assets/luxury-eco-resort.webp';

const destinationImages = { ella: teaTrain, kandy: teaTrain, nuwara: teaTrain, sigiriya: heroSigiriya, dambulla: heroSigiriya, galle: galleHeritage, mirissa: coastSunset, bentota: coastSunset, yala: wildlifeElephants, minneriya: wildlifeElephants };

const findDestinationImage = (name = '') => {
  const key = Object.keys(destinationImages).find((item) => name.toLowerCase().includes(item));
  return key ? destinationImages[key] : heroSigiriya;
};

const experienceImage = (category = '', title = '') => {
  const value = `${category} ${title}`.toLowerCase();
  if (value.includes('wild')) return wildlifeElephants;
  if (value.includes('beach') || value.includes('coast')) return coastSunset;
  if (value.includes('culture') || value.includes('galle')) return galleHeritage;
  if (value.includes('food') || value.includes('tea') || value.includes('adventure')) return teaTrain;
  return heroSigiriya;
};

export const images = { hero: heroSigiriya, coast: coastSunset, tea: teaTrain, wildlife: wildlifeElephants, heritage: galleHeritage, hotel: hotelImage, destination: findDestinationImage, package: teaTrain, experience: experienceImage };

export const heroSlides = [
  { image: heroSigiriya, eyebrow: 'Sigiriya • Cultural Triangle', title: 'Follow the island beyond ordinary.', text: 'Ancient kingdoms, village tables and slow mornings—woven into a journey made around you.', accent: 'Culture in every layer' },
  { image: teaTrain, eyebrow: 'Ella • Central Highlands', title: 'Take the beautiful way there.', text: 'Ride through cloud forests and tea gardens on one of the world’s most memorable rail journeys.', accent: 'Highland stories by rail' },
  { image: coastSunset, eyebrow: 'Southern Coast • Indian Ocean', title: 'Let the coast reset your rhythm.', text: 'Golden bays, living heritage and barefoot evenings selected for the way you want to feel.', accent: 'Salt air, softer days' }
];

export const journeyStyles = [
  { id: 'authentic', title: 'Authentic Ceylon', subtitle: 'Culture & craft', image: galleHeritage, category: 'Culture' },
  { id: 'adventure', title: 'Highland Spirit', subtitle: 'Rail & trails', image: teaTrain, category: 'Adventure' },
  { id: 'wild', title: 'Untamed Island', subtitle: 'Wildlife & nature', image: wildlifeElephants, category: 'Wildlife' },
  { id: 'barefoot', title: 'Barefoot South', subtitle: 'Coast & calm', image: coastSunset, category: 'Beach' },
  { id: 'luxury', title: 'Quiet Luxury', subtitle: 'Rest & renew', image: hotelImage, category: 'Luxury' }
];

export const islandHotspots = [
  { label: 'Jaffna', x: 50, y: 10 }, { label: 'Sigiriya', x: 51, y: 37 }, { label: 'Kandy', x: 47, y: 49 }, { label: 'Ella', x: 57, y: 64 }, { label: 'Yala', x: 64, y: 78 }, { label: 'Galle', x: 37, y: 88 }, { label: 'Colombo', x: 27, y: 64 }
];

export const experienceFallbacks = [
  { id: 'rail', title: 'Rails Through Tea Country', category: 'Adventure', location: 'Kandy to Ella', durationHours: 8, shortDescription: 'Ride through cloud forest and emerald estates on Sri Lanka’s legendary highland railway.', imageUrl: teaTrain, featured: true },
  { id: 'sigiriya', title: 'Sigiriya at First Light', category: 'Culture', location: 'Sigiriya', durationHours: 5, shortDescription: 'Climb the ancient rock fortress early, then slow down over village flavours.', imageUrl: heroSigiriya, featured: true },
  { id: 'wild', title: 'Wild Elephant Country', category: 'Wildlife', location: 'Minneriya', durationHours: 4, shortDescription: 'Observe wild elephants with an ethical naturalist guide and a patient pace.', imageUrl: wildlifeElephants, featured: true },
  { id: 'galle', title: 'Galle Fort Afterglow', category: 'Culture', location: 'Galle', durationHours: 3, shortDescription: 'Walk lighthouse lanes and coral-stone ramparts as the ocean turns gold.', imageUrl: galleHeritage },
  { id: 'coast', title: 'Barefoot Southern Coast', category: 'Beach', location: 'Mirissa', durationHours: 6, shortDescription: 'Hidden coves, palm shade and an unhurried day shaped by the tide.', imageUrl: coastSunset },
  { id: 'tea', title: 'Tea, Table & Tradition', category: 'Food', location: 'Nuwara Eliya', durationHours: 4, shortDescription: 'Meet growers, taste single-origin tea and share a seasonal hill-country table.', imageUrl: teaTrain }
];

export const categories = ['Adventure', 'Beach', 'Culture', 'Family', 'Food', 'Honeymoon', 'Luxury', 'Wildlife'];

export const officialHotelSites = [
  { name: '98 Acres Resort & Spa', url: 'https://www.resort98acres.com/' },
  { name: 'Jetwing Lighthouse', url: 'https://www.jetwinghotels.com/jetwinglighthouse/' },
  { name: 'Heritance Kandalama', url: 'https://www.heritancehotels.com/kandalama/' }
];

export const testimonials = [
  { quote: 'The itinerary felt personal, calm and beautifully balanced. Ella at sunrise was unforgettable.', name: 'Maya & Leon', country: 'Germany' },
  { quote: 'A smooth booking experience with wonderful local recommendations at every stop.', name: 'Noah Williams', country: 'United Kingdom' },
  { quote: 'From the coast to the cultural triangle, every detail was handled professionally.', name: 'Aiko Tanaka', country: 'Japan' }
];
