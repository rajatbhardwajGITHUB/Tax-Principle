import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { purchaseApi } from '../api/purchaseApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';

export default function UserDashboardPage() {
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const dashboardQuery = useQuery({
    queryKey: ['my-dashboard', from, to, page, size],
    queryFn: () => purchaseApi.dashboardMy({ from: from || undefined, to: to || undefined, page, size })
  });

  const error = useMemo(() => {
    if (!dashboardQuery.error) return '';
    return parseApiError(dashboardQuery.error);
  }, [dashboardQuery.error]);

  const content = dashboardQuery.data?.content || [];
  const totalPages = dashboardQuery.data?.totalPages || 1;

  return (
    <section className="page-card">
      <h1>My Dashboard</h1>
      <p className="muted">Filter and review your purchases by date range.</p>

      <div className="filter-row">
        <label>
          From
          <input type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
        </label>
        <label>
          To
          <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />
        </label>
      </div>

      <ErrorAlert message={error} />
      {dashboardQuery.isLoading ? <Loader label="Loading dashboard..." /> : null}

      {!dashboardQuery.isLoading && !error ? (
        <>
          {content.length === 0 ? (
            <div className="centered-card">No purchases in selected range.</div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Purchase ID</th>
                    <th>Service</th>
                    <th>Amount</th>
                    <th>Status</th>
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
