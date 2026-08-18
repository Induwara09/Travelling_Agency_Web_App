import heroImage from '../assets/sri-lanka-hero.webp';
import hotelImage from '../assets/luxury-eco-resort.webp';

const commons = (filename) =>
  `https://commons.wikimedia.org/wiki/Special:Redirect/file/${encodeURIComponent(filename)}?width=1600`;

const destinationImages = {
  ella: commons('Nine Arches Bridge in Ella.jpg'),
  sigiriya: commons('Sigiriya Sri Lanka.jpg'),
  galle: commons('Galle Dutch Fort, Sri Lanka.jpg'),
  mirissa: commons('Mirissa Beach Sri Lanka.jpg')
};

export const images = {
  hero: heroImage,
  hotel: hotelImage,
  destination: (name = '') => {
    const key = Object.keys(destinationImages).find((item) => name.toLowerCase().includes(item));
    return key ? destinationImages[key] : heroImage;
  },
  package: heroImage
};

export const categories = ['Adventure', 'Beach', 'Cultural', 'Family', 'Honeymoon', 'Luxury', 'Wildlife'];

export const officialHotelSites = [
  { name: '98 Acres Resort & Spa', url: 'https://www.resort98acres.com/' },
  { name: 'Jetwing Lighthouse', url: 'https://www.jetwinghotels.com/jetwinglighthouse/' },
  { name: 'Heritance Kandalama', url: 'https://www.heritancehotels.com/kandalama/' }
];

export const testimonials = [
  {
    quote: 'The itinerary felt personal, calm and beautifully balanced. Ella at sunrise was unforgettable.',
    name: 'Maya & Leon',
    country: 'Germany'
  },
  {
    quote: 'A smooth booking experience with wonderful local recommendations at every stop.',
    name: 'Noah Williams',
    country: 'United Kingdom'
  },
  {
    quote: 'From the coast to the cultural triangle, every detail was handled professionally.',
    name: 'Aiko Tanaka',
    country: 'Japan'
  }
];
