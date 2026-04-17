import { httpClient } from './httpClient';

export const purchaseApi = {
  create(payload) {
    return httpClient.post('/purchases', payload).then((res) => res.data);
  },
  listMy(params = {}) {
    return httpClient.get('/purchases/me', { params }).then((res) => res.data);
  },
  getMyById(id) {
    return httpClient.get(`/purchases/me/${id}`).then((res) => res.data);
  },
  getMyEvents(id) {
    return httpClient.get(`/purchases/me/${id}/events`).then((res) => res.data);
  },
  listAll(params = {}) {
    return httpClient.get('/admin/purchases', { params }).then((res) => res.data);
  },
  getById(id) {
    return httpClient.get(`/admin/purchases/${id}`).then((res) => res.data);
  },
  updateStatus(id, payload) {
    return httpClient.patch(`/admin/purchases/${id}/status`, payload).then((res) => res.data);
  },
  dashboardMy(params = {}) {
    return httpClient.get('/dashboard/me/purchases', { params }).then((res) => res.data);
  },
  dashboardAdmin(params = {}) {
    return httpClient.get('/admin/dashboard/purchases', { params }).then((res) => res.data);
  },
  dashboardMetrics(params = {}) {
    return httpClient.get('/admin/dashboard/metrics', { params }).then((res) => res.data);
  }
};
