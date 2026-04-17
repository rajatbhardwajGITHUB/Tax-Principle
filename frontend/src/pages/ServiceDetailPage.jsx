import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';

export default function ServiceDetailPage() {
  const { id } = useParams();

  const query = useQuery({
    queryKey: ['service-detail', id],
    queryFn: () => serviceApi.getById(id),
    enabled: Boolean(id)
  });

  if (query.isLoading) {
    return <Loader label="Loading service details..." />;
  }

  const error = query.error ? parseApiError(query.error) : '';
  const service = query.data;

  return (
    <section className="page-card narrow">
      <div className="row-spread">
        <h1>Service Detail</h1>
        <Link to="/services" className="btn secondary">
          Back to list
        </Link>
      </div>

      <ErrorAlert message={error} />

      {!error && service ? (
        <div className="detail-grid">
          <div>
            <h3>Code</h3>
            <p>{service.code}</p>
          </div>
          <div>
            <h3>Name</h3>
            <p>{service.name}</p>
          </div>
          <div>
            <h3>Description</h3>
            <p>{service.description || 'No description available'}</p>
          </div>
          <div>
            <h3>Price</h3>
            <p>INR {Number(service.price || 0).toFixed(2)}</p>
          </div>
          <div>
            <h3>Status</h3>
            <p>{service.active ? 'Active' : 'Inactive'}</p>
          </div>
        </div>
      ) : null}
    </section>
  );
}
