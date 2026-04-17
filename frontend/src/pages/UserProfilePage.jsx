import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { purchaseApi } from '../api/purchaseApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';

const statusSteps = ['CREATED', 'SUCCESS', 'REFUNDED'];

function statusIndex(status) {
  if (status === 'FAILED') return 0;
  const idx = statusSteps.indexOf(status);
  return idx >= 0 ? idx : 0;
}

function Stepper({ status }) {
  const active = statusIndex(status);
  return (
    <div className="stepper">
      {statusSteps.map((step, index) => (
        <div key={step} className={`step ${index <= active ? 'active' : ''}`}>
          <span>{step}</span>
        </div>
      ))}
      {status === 'FAILED' ? <div className="step failed"><span>FAILED</span></div> : null}
    </div>
  );
}

export default function UserProfilePage() {
  const [selectedId, setSelectedId] = useState(null);

  const purchasesQuery = useQuery({
    queryKey: ['profile-purchases'],
    queryFn: () => purchaseApi.listMy({ page: 0, size: 20, sort: 'purchasedAt,desc' })
  });

  const eventsQuery = useQuery({
    queryKey: ['profile-events', selectedId],
    queryFn: () => purchaseApi.getMyEvents(selectedId),
    enabled: Boolean(selectedId)
  });

  const error = purchasesQuery.error
    ? parseApiError(purchasesQuery.error)
    : eventsQuery.error
      ? parseApiError(eventsQuery.error)
      : '';

  const purchases = purchasesQuery.data?.content || [];

  return (
    <section className="page-card">
      <h1>My Profile Tracker</h1>
      <p className="muted">Track your purchases with status steps and event timeline.</p>

      <ErrorAlert message={error} />
      {purchasesQuery.isLoading ? <Loader label="Loading profile tracker..." /> : null}

      {!purchasesQuery.isLoading && !error ? (
        <div className="profile-grid">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Purchase ID</th>
                  <th>Service</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Tracker</th>
                </tr>
              </thead>
              <tbody>
                {purchases.map((row) => (
                  <tr key={row.purchaseId}>
                    <td>{row.purchaseId}</td>
                    <td>{row.serviceName}</td>
                    <td>INR {Number(row.amount || 0).toFixed(2)}</td>
                    <td>{row.status}</td>
                    <td>
                      <button type="button" className="btn small secondary" onClick={() => setSelectedId(row.purchaseId)}>
                        View Tracker
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="tracker-panel">
            <h3>Purchase Tracker</h3>
            {selectedId ? (
              <>
                <p className="muted">Purchase ID: {selectedId}</p>
                <Stepper status={purchases.find((p) => p.purchaseId === selectedId)?.status || 'CREATED'} />
                {eventsQuery.isLoading ? <Loader label="Loading timeline..." /> : null}
                <ul className="event-list">
                  {(eventsQuery.data || []).map((event) => (
                    <li key={event.id}>
                      <strong>{event.status}</strong> - {event.message || event.action}
                      <div className="muted">{new Date(event.createdAt).toLocaleString()}</div>
                    </li>
                  ))}
                </ul>
              </>
            ) : (
              <p className="muted">Select a purchase to see its tracking steps.</p>
            )}
          </div>
        </div>
      ) : null}
    </section>
  );
}
