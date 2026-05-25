import { httpClient } from './httpClient';

export const paymentApi = {
  createOrder(payload) {
    return httpClient.post('/payments/orders', payload).then((res) => res.data);
  },
  verifyPayment(payload) {
    return httpClient.post('/payments/verify', payload).then((res) => res.data);
  }
};
