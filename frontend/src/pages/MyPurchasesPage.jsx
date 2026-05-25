import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { paymentApi } from '../api/paymentApi';
import { purchaseApi } from '../api/purchaseApi';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';
import Pagination from '../components/Pagination';

let razorpayScriptPromise = null;

function loadRazorpayScript() {
  if (window.Razorpay) return Promise.resolve(true);
  if (!razorpayScriptPromise) {
    razorpayScriptPromise = new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  }
  return razorpayScriptPromise;
}

export default function MyPurchasesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [serviceId, setServiceId] = useState('');
  const [statusMessage, setStatusMessage] = useState('');
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
    mutationFn: async () => {
      if (!serviceId) {
        throw new Error('Select a service before starting payment');
      }

      const order = await paymentApi.createOrder({ serviceId: Number(serviceId) });
      const scriptLoaded = await loadRazorpayScript();
      if (!scriptLoaded) {
        throw new Error('Unable to load Razorpay checkout');
      }

      return new Promise((resolve, reject) => {
        const razorpay = new window.Razorpay({
          key: order.keyId,
          amount: order.amountInPaise,
          currency: order.currency,
          name: 'Tax Principal',
          description: order.serviceName,
          order_id: order.orderId,
          handler: async (response) => {
            try {
              const purchase = await paymentApi.verifyPayment({
                orderId: response.razorpay_order_id,
                paymentId: response.razorpay_payment_id,
                signature: response.razorpay_signature
              });
              resolve(purchase);
            } catch (verificationError) {
              reject(verificationError);
            }
          },
          modal: {
            ondismiss: () => reject(new Error('Payment window was closed before completion'))
          },
          theme: {
            color: '#1f4f82'
          }
        });

        razorpay.on('payment.failed', (response) => {
          reject(new Error(response?.error?.description || 'Payment failed'));
        });

        razorpay.open();
      });
    },
    onSuccess: () => {
      setCreateError('');
      setStatusMessage('Payment completed successfully.');
      queryClient.invalidateQueries({ queryKey: ['my-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['my-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['admin-purchases'] });
      queryClient.invalidateQueries({ queryKey: ['admin-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['admin-metrics'] });
    },
    onError: (error) => {
      setStatusMessage('');
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
    setCreateError('');
    setStatusMessage('');
    await createMutation.mutateAsync();
  };

  return (
    <section className="page-card">
      <h1>My Purchases</h1>
      <p className="muted">Select a service and pay through Razorpay checkout.</p>

      <form className="purchase-create-row" onSubmit={handleCreate}>
        <select value={serviceId} onChange={(e) => setServiceId(e.target.value)}>
          <option value="">Select a service</option>
          {services.map((service) => (
            <option key={service.id} value={service.id}>
              {service.name} (INR {Number(service.price || 0).toFixed(2)})
            </option>
          ))}
        </select>
        <button type="submit" className="btn" disabled={createMutation.isPending}>
          {createMutation.isPending ? 'Opening Checkout...' : 'Pay With Razorpay'}
        </button>
      </form>

      {statusMessage ? <p className="muted">{statusMessage}</p> : null}
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
