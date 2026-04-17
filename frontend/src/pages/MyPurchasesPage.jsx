import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { purchaseApi } from '../api/purchaseApi';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';

export default function MyPurchasesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [serviceId, setServiceId] = useState('');
  const [paymentReference, setPaymentReference] = useState('');
  const [createError, setCreateError] = useState('');
  const queryClient = useQueryClient();

  const servicesQuery = useQuery({
    queryKey: ['services', 'purchase-select'],
    queryFn: () => serviceApi.list({ page: 0, size: 100 })
  });

  const purchasesQuery = useQuery({
    queryKey: ['my-purchases', page, size],
    queryFn: () => purchaseApi.listMy({ page, size, sort: 'purchasedAt,desc' })
  });

  const createMutation = useMutation({
    mutationFn: (payload) => purchaseApi.create(payload),
    onSuccess: () => {
      setCreateError('');
      setPaymentReference('');
      queryClient.invalidateQueries({ queryKey: ['my-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['my-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['admin-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['admin-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['admin-metrics'] });
    },
    onError: (error) => {
      setCreateError(parseApiError(error));
    }
  });

  const error = useMemo(() => {
    if (!purchasesQuery.error) return '';
    return parseApiError(purchasesQuery.error);
  }, [purchasesQuery.error]);

  const content = purchasesQuery.data?.content || [];
  const totalPages = purchasesQuery.data?.totalPages || 1;
  const services = servicesQuery.data?.content || [];

  const handleCreate = async (event) => {
    event.preventDefault();
    if (!serviceId || !paymentReference.trim()) {
      setCreateError('Select service and provide payment reference');
      return;
    }
    await createMutation.mutateAsync({
      serviceId: Number(serviceId),
      paymentReference: paymentReference.trim()
    });
  };

  return (
    <section className="page-card">
      <h1>My Purchases</h1>
      <p className="muted">Create and track your purchase records.</p>

      <form className="purchase-create-row" onSubmit={handleCreate}>
        <select value={serviceId} onChange={(e) => setServiceId(e.target.value)}>
          <option value="">Select a service</option>
          {services.map((service) => (
            <option key={service.id} value={service.id}>
              {service.name} (INR {Number(service.price || 0).toFixed(2)})
            </option>
          ))}
        </select>
        <input
          type="text"
          placeholder="Payment reference"
          value={paymentReference}
          onChange={(e) => setPaymentReference(e.target.value)}
        />
        <button type="submit" className="btn" disabled={createMutation.isPending}>
          {createMutation.isPending ? 'Creating...' : 'Create Purchase'}
        </button>
      </form>

      <ErrorAlert message={createError || error} />
      {purchasesQuery.isLoading ? <Loader label="Loading purchases..." /> : null}

      {!purchasesQuery.isLoading && !error ? (
        <>
          {content.length === 0 ? (
            <div className="centered-card">No purchases found.</div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Service</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Payment Ref</th>
                    <th>Purchased At</th>
                  </tr>
                </thead>
                <tbody>
                  {content.map((row) => (
                    <tr key={row.purchaseId}>
                      <td>{row.purchaseId}</td>
                      <td>{row.serviceName}</td>
                      <td>INR {Number(row.amount || 0).toFixed(2)}</td>
                      <td>{row.status}</td>
                      <td>{row.paymentReference}</td>
                      <td>{new Date(row.purchasedAt).toLocaleString()}</td>
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
