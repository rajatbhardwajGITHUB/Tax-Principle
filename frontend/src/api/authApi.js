import { httpClient, publicClient } from './httpClient';

export const authApi = {
  registerInitiate(payload) {
    return publicClient.post('/auth/register/initiate', payload).then((res) => res.data);
  },
  registerVerify(email, otp) {
    return publicClient.post('/auth/register/verify', { email, otp }).then((res) => res.data);
  },
  loginInitiate(payload) {
    return publicClient.post('/auth/login/initiate', payload).then((res) => res.data);
  },
  loginVerify(email, otp) {
    return publicClient.post('/auth/login/verify', { email, otp }).then((res) => res.data);
  },
  forgotPassword(email) {
    return publicClient.post('/auth/password/forgot', { email }).then((res) => res.data);
  },
  resetPassword(payload) {
    return publicClient.post('/auth/password/reset', payload).then((res) => res.data);
  },
  refresh(refreshToken) {
    return publicClient.post('/auth/refresh', { refreshToken }).then((res) => res.data);
  },
  logout() {
    return httpClient.post('/auth/logout').then((res) => res.data);
  }
};
