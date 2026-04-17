import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';

export default function AdminServicesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [actionError, setActionError] = useState('');
  const queryClient = useQueryClient();

  const servicesQuery = useQuery({
    queryKey: ['admin-services', page, size],
    queryFn: () => serviceApi.adminList({ page, size })

  });

  const toggleMutation = useMutation({
    mutationFn: ({ id, status }) => serviceApi.setStatus(id, status),
    onSuccess: () => {
      setActionError('');
      queryClient.invalidateQueries({ queryKey: ['admin-services'] });
      queryClient.invalidateQueries({ queryKey: ['services'] });
    },
    onError: (error) => {
      setActionError(parseApiError(error));
    }
  });

  const listError = useMemo(() => {
    if (!servicesQuery.error) return '';
    return parseApiError(servicesQuery.error);
  }, [servicesQuery.error]);

  const content = servicesQuery.data?.content || [];
  const totalPages = servicesQuery.data?.totalPages || 1;

  const handleStatusToggle = async (service) => {
    const nextStatus = !service.active;
    const ok = window.confirm(
      `Change status for "${service.name}" to ${nextStatus ? 'active' : 'inactive'}?`
    );
    if (!ok) return;
    await toggleMutation.mutateAsync({ id: service.id, status: nextStatus });
  };

  return (
    <section className="page-card">
      <div className="row-spread">
        <div>
          <h1>Admin Services</h1>
          <p className="muted">Manage service catalog entries from this dashboard.</p>
        </div>
        <Link to="/admin/services/new" className="btn">
          Create Service
        </Link>
      </div>

      <div className="helper-note">
        Only active services are shown in this list. Inactive services may be hidden.
      </div>

      {servicesQuery.isLoading ? <Loader label="Loading admin services..." /> : null}
      <ErrorAlert message={listError || actionError} />

      {!servicesQuery.isLoading && !listError ? (
        <>
          {content.length === 0 ? (
            <div className="centered-card">No services found.</div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {content.map((service) => (
                    <tr key={service.id}>
                      <td>{service.id}</td>
                      <td>{service.code}</td>
                      <td>{service.name}</td>
                      <td>INR {Number(service.price || 0).toFixed(2)}</td>
                      <td>
                        <span className={service.active ? 'badge ok' : 'badge off'}>
                          {service.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="action-row">
                        <Link to={`/admin/services/${service.id}/edit`} className="btn small secondary">
                          Edit
                        </Link>
                        <button
                          type="button"
                          className="btn small danger"
                          disabled={toggleMutation.isPending}
                          onClick={() => handleStatusToggle(service)}
                        >
                          {toggleMutation.isPending ? 'Saving...' : service.active ? 'Deactivate' : 'Activate'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
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
