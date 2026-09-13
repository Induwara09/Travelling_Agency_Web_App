import heroSigiriya from '../assets/hero-sigiriya-v2.webp';
import coastSunset from '../assets/coast-sunset.webp';
import teaTrain from '../assets/tea-train.webp';
import wildlifeElephants from '../assets/wildlife-elephants.webp';
import galleHeritage from '../assets/galle-heritage.webp';
import islandHero from '../assets/sri-lanka-hero.webp';

const destinationImages = {
  ella: teaTrain, kandy: teaTrain, nuwara: teaTrain, badulla: teaTrain,
  sigiriya: heroSigiriya, dambulla: heroSigiriya, matale: heroSigiriya,
  galle: galleHeritage, jaffna: galleHeritage, anuradhapura: galleHeritage, polonnaruwa: galleHeritage,
  mirissa: coastSunset, bentota: coastSunset, beach: coastSunset, bay: coastSunset,
  yala: wildlifeElephants, minneriya: wildlifeElephants, wildlife: wildlifeElephants, elephant: wildlifeElephants
};

const findDestinationImage = (name = '', district = '', category = '') => {
  const safeCategory = category || '';
  const value = `${name || ''} ${district || ''} ${safeCategory}`.toLowerCase();
  const key = Object.keys(destinationImages).find((item) => value.includes(item));
  if (key) return destinationImages[key];
  if (safeCategory.includes('Beach')) return coastSunset;
  if (safeCategory.includes('Wildlife')) return wildlifeElephants;
  if (safeCategory.includes('Heritage') || safeCategory.includes('Sacred')) return galleHeritage;
  if (safeCategory.includes('Hiking') || safeCategory.includes('Waterfall')) return teaTrain;
  return islandHero;
};

export const images = {
  hero: heroSigiriya, coast: coastSunset, tea: teaTrain, wildlife: wildlifeElephants,
  heritage: galleHeritage, island: islandHero,
  destination: findDestinationImage, package: teaTrain
};

export const heroSlides = [
  { image: heroSigiriya, eyebrow: 'Sigiriya • Cultural Triangle', title: 'A whole island of stories.', text: 'Explore 371 remarkable places across every district of Sri Lanka—from ancient kingdoms to hidden natural escapes.', accent: 'Ancient wonders' },
  { image: teaTrain, eyebrow: 'Ella • Central Highlands', title: 'Take the beautiful way there.', text: 'Ride through cloud forest, tea gardens, waterfalls and mountain towns at an unhurried island pace.', accent: 'Highland journeys' },
  { image: wildlifeElephants, eyebrow: 'Wild Sri Lanka', title: 'Meet the island untamed.', text: 'Discover national parks, forest reserves, wetlands and wildlife habitats with respect for every landscape.', accent: 'Wildlife encounters' },
  { image: galleHeritage, eyebrow: 'Galle • Southern Province', title: 'Walk through living history.', text: 'Fort walls, sacred spaces and coastal towns reveal centuries of culture in every district.', accent: 'Living heritage' },
  { image: coastSunset, eyebrow: 'Indian Ocean Coast', title: 'Follow the sun to the sea.', text: 'Find surf bays, quiet islands, lagoons and golden beaches around Sri Lanka’s extraordinary shoreline.', accent: 'Coastal calm' }
];

export const destinationCategories = [
  'Sacred Places', 'Heritage & History', 'Wildlife & Nature', 'Beaches & Coast',
  'Lakes & Waterways', 'Hiking & Adventure', 'Waterfalls', 'City & Culture'
];

export const districts = [
  'Ampara', 'Anuradhapura', 'Badulla', 'Batticaloa', 'Colombo', 'Galle', 'Gampaha',
  'Hambantota', 'Jaffna', 'Kalutara', 'Kandy', 'Kegalle', 'Kilinochchi', 'Kurunegala',
  'Mannar', 'Matale', 'Matara', 'Monaragala', 'Mullaitivu', 'Nuwara Eliya',
  'Polonnaruwa', 'Puttalam', 'Ratnapura', 'Trincomalee', 'Vavuniya'
];

export const journeyStyles = [
  { id: 'heritage', title: 'Ancient Ceylon', subtitle: 'Heritage & history', image: galleHeritage, category: 'Heritage & History' },
  { id: 'adventure', title: 'Highland Spirit', subtitle: 'Hiking & adventure', image: teaTrain, category: 'Hiking & Adventure' },
  { id: 'wild', title: 'Untamed Island', subtitle: 'Wildlife & nature', image: wildlifeElephants, category: 'Wildlife & Nature' },
  { id: 'barefoot', title: 'Barefoot Coast', subtitle: 'Beaches & coast', image: coastSunset, category: 'Beaches & Coast' },
  { id: 'sacred', title: 'Sacred Lanka', subtitle: 'Temples & traditions', image: heroSigiriya, category: 'Sacred Places' }
];

export const islandHotspots = [
  { label: 'Jaffna', x: 50, y: 10 }, { label: 'Sigiriya', x: 51, y: 37 },
  { label: 'Kandy', x: 47, y: 49 }, { label: 'Ella', x: 57, y: 64 },
  { label: 'Yala', x: 64, y: 78 }, { label: 'Galle', x: 37, y: 88 },
  { label: 'Colombo', x: 27, y: 64 }
];

export const categories = ['Adventure', 'Beach', 'Culture', 'Family', 'Food', 'Honeymoon', 'Luxury', 'Wildlife'];

export const testimonials = [
  { quote: 'The route felt personal, calm and beautifully balanced. Ella at sunrise was unforgettable.', name: 'Maya & Leon', country: 'Germany' },
  { quote: 'A smooth booking experience with wonderful local recommendations at every stop.', name: 'Noah Williams', country: 'United Kingdom' },
  { quote: 'From the coast to the cultural triangle, every detail was handled professionally.', name: 'Aiko Tanaka', country: 'Japan' }
];
