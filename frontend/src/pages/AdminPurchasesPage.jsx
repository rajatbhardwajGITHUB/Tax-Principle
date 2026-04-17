import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { purchaseApi } from '../api/purchaseApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';

export default function AdminPurchasesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['admin-purchases', page, size],
    queryFn: () => purchaseApi.listAll({ page, size, sort: 'purchasedAt,desc' })
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, status }) => purchaseApi.updateStatus(id, { status }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['my-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['profile-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['admin-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['admin-metrics'] });
    }
  });

  const error = useMemo(() => {
    if (!query.error) return '';
    return parseApiError(query.error);
  }, [query.error]);

  const content = query.data?.content || [];
  const totalPages = query.data?.totalPages || 1;

  return (
    <section className="page-card">
      <h1>Admin Purchases</h1>
      <p className="muted">View purchases across all users.</p>

      <ErrorAlert message={error} />
      {query.isLoading ? <Loader label="Loading admin purchases..." /> : null}

      {!query.isLoading && !error ? (
        <>
          {content.length === 0 ? (
            <div className="centered-card">No purchases found.</div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Purchase ID</th>
                    <th>User</th>
                    <th>Service</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Purchased At</th>
                    <th>Update Status</th>
                  </tr>
                </thead>
                <tbody>
                  {content.map((row) => (
                    <tr key={row.purchaseId}>
                      <td>{row.purchaseId}</td>
                      <td>{row.userEmail}</td>
                      <td>{row.serviceName}</td>
                      <td>INR {Number(row.amount || 0).toFixed(2)}</td>
                      <td>{row.status}</td>
                      <td>{new Date(row.purchasedAt).toLocaleString()}</td>
                      <td>
                        <select
                          value={row.status}
                          onChange={(e) =>
                            statusMutation.mutate({ id: row.purchaseId, status: e.target.value })
                          }
                        >
                          <option value="CREATED">CREATED</option>
                          <option value="SUCCESS">SUCCESS</option>
                          <option value="FAILED">FAILED</option>
                          <option value="REFUNDED">REFUNDED</option>
                        </select>
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
