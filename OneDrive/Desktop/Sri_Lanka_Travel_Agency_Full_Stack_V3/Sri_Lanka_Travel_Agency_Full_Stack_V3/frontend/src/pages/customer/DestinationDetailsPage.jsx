import { useEffect, useState } from 'react';
import { ArrowLeft, ArrowUpRight, Camera, MapPin, Star } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import DestinationCard from '../../components/DestinationCard';
import ImageWithFallback from '../../components/ImageWithFallback';
import PackageCard from '../../components/PackageCard';
import SectionHeading from '../../components/SectionHeading';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { images } from '../../data/siteData';
import { destinationApi, getApiError, packageApi } from '../../services/api';

export default function DestinationDetailsPage() {
  const { id } = useParams();
  const [destination, setDestination] = useState(null);
  const [related, setRelated] = useState([]);
  const [packages, setPackages] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    window.scrollTo(0, 0);
    destinationApi.get(id)
      .then(async ({ data }) => {
        setDestination(data);
        const [packageResult, relatedResult] = await Promise.allSettled([
          packageApi.list({ destinationId: id, status: 'ACTIVE' }),
          destinationApi.list({ district: data.district })
        ]);
        setPackages(packageResult.status === 'fulfilled' ? packageResult.value.data : []);
        setRelated(relatedResult.status === 'fulfilled' ? relatedResult.value.data.filter((item) => `${item.id}` !== `${id}`).slice(0, 3) : []);
      })
      .catch((err) => setError(getApiError(err, 'Destination not found.')));
  }, [id]);

  if (error) return <div className="section container"><ErrorState message={error} /></div>;
  if (!destination) return <LoadingState />;
  const fallback = images.destination(destination.name, destination.district, destination.category);
  const tags = (destination.tags || '').split(',').map((tag) => tag.trim()).filter(Boolean);

  return (
    <>
      <section className="detail-hero destination-detail-v3">
        <ImageWithFallback src={destination.imageUrl} fallback={fallback} alt={destination.name} />
        <div className="detail-hero__overlay" />
        <div className="container detail-hero__content"><Link to="/destinations"><ArrowLeft /> All destinations</Link><div className="detail-chips"><span><MapPin /> {destination.district || destination.location}</span><span>{destination.category}</span>{destination.featured && <span><Star fill="currentColor" /> Featured</span>}</div><h1>{destination.name}</h1><p>{destination.shortDescription}</p></div>
      </section>
      <section className="section container destination-story"><div><span className="eyebrow">Discover the place</span><h2>Where landscape, history and local life meet.</h2><div className="destination-tags">{tags.map((tag) => <span key={tag}>{tag}</span>)}</div></div><div><p>{destination.description || `${destination.name} is one of Sri Lanka's most memorable places, filled with local character and remarkable scenery.`}</p><p className="travel-note">Please check opening hours, weather and access locally before travelling. Visit natural and sacred places respectfully.</p>{destination.imageSourceUrl && <a className="source-link" href={destination.imageSourceUrl} target="_blank" rel="noreferrer"><Camera /> View photo source <ArrowUpRight /></a>}</div></section>
      {related.length > 0 && <section className="section section--tint"><div className="container"><SectionHeading eyebrow={`More from ${destination.district}`} title="Continue exploring nearby" /><div className="destination-grid">{related.map((item, index) => <DestinationCard key={item.id} destination={item} index={index} />)}</div></div></section>}
      <section className="section container"><SectionHeading eyebrow="Travel this region" title={`Tours featuring ${destination.name}`} />{packages.length ? <div className="cards-grid">{packages.map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <p className="muted-copy">No active package is linked to this place yet. Ask our team for a custom route.</p>}</section>
    </>
  );
}
