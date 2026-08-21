import { useEffect, useState } from 'react';
import { ArrowLeft, MapPin } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import PackageCard from '../../components/PackageCard';
import SectionHeading from '../../components/SectionHeading';
import ImageWithFallback from '../../components/ImageWithFallback';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { destinationApi, getApiError, packageApi } from '../../services/api';
import { images } from '../../data/siteData';

export default function DestinationDetailsPage() {
  const { id } = useParams();
  const [destination, setDestination] = useState(null);
  const [packages, setPackages] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([destinationApi.get(id), packageApi.list({ destinationId: id, status: 'ACTIVE' })])
      .then(([destinationResult, packagesResult]) => { setDestination(destinationResult.data); setPackages(packagesResult.data); })
      .catch((err) => setError(getApiError(err, 'Destination not found.')));
  }, [id]);

  if (error) return <div className="section container"><ErrorState message={error} /></div>;
  if (!destination) return <LoadingState />;

  return (
    <>
      <section className="detail-hero">
        <ImageWithFallback src={destination.imageUrl} fallback={images.destination(destination.name)} alt={destination.name} />
        <div className="detail-hero__overlay" />
        <div className="container detail-hero__content"><Link to="/destinations"><ArrowLeft /> All destinations</Link><span><MapPin /> {destination.location}</span><h1>{destination.name}</h1></div>
      </section>
      <section className="section container details-copy"><div><span className="eyebrow">About the destination</span><h2>Where landscape and local life meet</h2></div><p>{destination.description || `${destination.name} is one of Sri Lanka's most memorable places, filled with local character and remarkable scenery.`}</p></section>
      <section className="section section--tint"><div className="container"><SectionHeading eyebrow="Travel this region" title={`Tours featuring ${destination.name}`} />{packages.length ? <div className="cards-grid">{packages.map((item, index) => <PackageCard key={item.id} item={item} index={index} />)}</div> : <p className="muted-copy">No active packages for this destination yet.</p>}</div></section>
    </>
  );
}
