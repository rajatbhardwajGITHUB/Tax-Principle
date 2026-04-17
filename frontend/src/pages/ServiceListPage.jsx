import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';
import useAuth from '../hooks/useAuth';

export default function ServiceListPage() {
  const { isAdmin } = useAuth();
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const query = useQuery({
    queryKey: ['services', page, size],
    queryFn: () => serviceApi.list({ page, size })
  });

  const content = query.data?.content || [];
  const totalPages = query.data?.totalPages || 1;

  const error = useMemo(() => {
    if (!query.error) return '';
    return parseApiError(query.error);
  }, [query.error]);

  return (
    <section className="page-card">
      <div className="row-spread">
        <div>
          <h1>Service Catalog</h1>
          <p className="muted">Browse active services available to users.</p>
        </div>
        {isAdmin ? (
          <Link to="/admin/services" className="btn secondary">
            Admin Services
          </Link>
        ) : null}
      </div>

      {query.isLoading ? <Loader label="Loading services..." /> : null}
      <ErrorAlert message={error} />

      {!query.isLoading && !error ? (
        <>
          {content.length === 0 ? (
            <div className="centered-card">No active services found.</div>
          ) : (
            <div className="grid-cards">
              {content.map((service) => (
                <article key={service.id} className="service-card">
                  <p className="service-code">{service.code}</p>
                  <h3>{service.name}</h3>
                  <p>{service.description || 'No description available'}</p>
                  <strong className="price">INR {Number(service.price || 0).toFixed(2)}</strong>
                  <Link to={`/services/${service.id}`} className="btn small">
                    View details
                  </Link>
                </article>
              ))}
            </div>
          )}

          <Pagination
            page={page}
            totalPages={totalPages}
            onPrev={() => setPage((prev) => Math.max(prev - 1, 0))}
            onNext={() => setPage((prev) => (prev + 1 < totalPages ? prev + 1 : prev))}
          />
        </>
      ) : null}
    </section>
  );
}
