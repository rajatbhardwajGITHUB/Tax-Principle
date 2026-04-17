import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { purchaseApi } from '../api/purchaseApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';

export default function AdminDashboardPage() {
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');

  const metricsQuery = useQuery({
    queryKey: ['admin-metrics', from, to],
    queryFn: () => purchaseApi.dashboardMetrics({ from: from || undefined, to: to || undefined })
  });

  const purchasesQuery = useQuery({
    queryKey: ['admin-dashboard', from, to],
    queryFn: () =>
      purchaseApi.dashboardAdmin({
        from: from || undefined,
        to: to || undefined,
        page: 0,
        size: 10,
        sort: 'purchasedAt,desc'
      })
  });

  const error = metricsQuery.error
    ? parseApiError(metricsQuery.error)
    : purchasesQuery.error
      ? parseApiError(purchasesQuery.error)
      : '';

  const metrics = metricsQuery.data;
  const rows = purchasesQuery.data?.content || [];

  return (
    <section className="page-card">
      <h1>Admin Dashboard</h1>
      <p className="muted">Overview of purchase metrics and latest records.</p>

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
      {metricsQuery.isLoading || purchasesQuery.isLoading ? <Loader label="Loading dashboard..." /> : null}

      {!metricsQuery.isLoading && !purchasesQuery.isLoading && !error ? (
        <>
          <div className="stats-grid">
            <article className="stat-card">
              <h3>Total Purchases</h3>
              <p>{metrics?.totalPurchases ?? 0}</p>
            </article>
            <article className="stat-card">
              <h3>Success Count</h3>
              <p>{metrics?.successCount ?? 0}</p>
            </article>
            <article className="stat-card">
              <h3>Total Revenue</h3>
              <p>INR {Number(metrics?.totalRevenue ?? 0).toFixed(2)}</p>
            </article>
          </div>

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
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.purchaseId}>
                    <td>{row.purchaseId}</td>
                    <td>{row.userEmail}</td>
                    <td>{row.serviceName}</td>
                    <td>INR {Number(row.amount || 0).toFixed(2)}</td>
                    <td>{row.status}</td>
                    <td>{new Date(row.purchasedAt).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      ) : null}
    </section>
  );
}
