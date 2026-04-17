import { httpClient } from './httpClient';

export const serviceApi = {
  list({ page = 0, size = 10 } = {}) {
    return httpClient
      .get('/service/user/services', { params: { page, size } })
      .then((res) => res.data);
  },
  getById(id) {
    return httpClient.get(`/service/user/services/${id}`).then((res) => res.data);
  },
  create(payload) {
    return httpClient.post('/service/admin/services', payload).then((res) => res.data);
  },
  update(id, payload) {
    return httpClient
      .put(`/service/admin/services/updateService/${id}`, payload)
      .then((res) => res.data);
  },
  setStatus(id, status) {
    return httpClient
      .patch(`/service/admin/services/updatedStatus/${id}`, null, { params: { status } })
      .then((res) => res.data);
  },
  adminList({ page = 0, size = 10 } = {}) {
    return httpClient.get('/service/admin/services', { params: { page, size } }).then((res) => res.data);
  }

};
