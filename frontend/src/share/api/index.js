import axios from "axios";
import tokenUtils from "../utils/tokenUtils";

const api = axios.create({
  baseURL: "http://localhost:8888/api/v1",
  withCredentials: false,
});

api.interceptors.request.use((config) => {
  const token = tokenUtils.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const API_PREFIX = {
  auth: "/auth",
  order: "/order",
  tracking: "/tracking",
  delivery: "/delivery",
};

export const authApi = {
  login: (credentials) => {
    return api.post(`${API_PREFIX.auth}/auth/login`, credentials);
  },

  register: (credentials) => {
    const { confirmPassword, ...payload } = credentials;
    return api.post(`${API_PREFIX.auth}/auth/register/customer`, payload);
  },

  registerSystemUser: (credentials) => {
    const { confirmPassword, ...payload } = credentials;
    return api.post(`${API_PREFIX.auth}/auth/register/system-user`, payload);
  },

  verifyAccount: (token) => {
    return api.patch(`${API_PREFIX.auth}/auth/verify-email/${token}`);
  },

  getMe: () => {
    return api.get(`${API_PREFIX.auth}/user/my-info`);
  },

  filterUser: (queryString) => {
    return api.get(`${API_PREFIX.auth}/user/filter?${queryString}`);
  }
};

export const orderApi = {
  getOrderStat: (queryString) => {
    return api.get(`${API_PREFIX.order}/statistics/delivery_staff?${queryString}`)
  },

  createOrder: (payload) => {
    return api.post(`${API_PREFIX.order}/order/create`, payload);
  },

  getCustomerOrder: (queryString) => {
    return api.get(`${API_PREFIX.order}/order/filter/customer?${queryString}`);
  },

  getAdminOrder: (queryString) => {
    return api.get(`${API_PREFIX.order}/order/filter/admin?${queryString}`);
  },

  getDeliveryStaffOngoingOrder: (queryString) => {
    return api.get(
      `${API_PREFIX.order}/order/filter/delivery_staff?${queryString}`,
    );
  },

  getDeliveryStaffCompletedOrder: (queryString) => {
    return api.get(
      `${API_PREFIX.order}/order/filter/delivery_staff?${queryString}&status=COMPLETED`,
    );
  },

  getDeliveryStaffCancelledOrder: (queryString) => {
    return api.get(
      `${API_PREFIX.order}/order/filter/delivery_staff?${queryString}&status=CANCELLED`,
    );
  },

  estimateShippingFee: (payload) => {
    return api.post(
      `${API_PREFIX.order}/shipping-fee/estimate-shipping-fee`,
      payload,
    );
  },

  getMatrix: () => {
    return api.get(`${API_PREFIX.order}/shipping-fee/matrix`);
  },
};

export const trackingApi = {
  getOrderDetail: (orderCode) => {
    return api.get(
      `${API_PREFIX.tracking}/tracking/order/details?orderCode=${orderCode}`,
    );
  },

  getOrderDetailPublic: (orderCode, phone) => {
    return api.get(
      `${API_PREFIX.tracking}/tracking/public/order/details?orderCode=${orderCode}&phone=${phone}`,
    );
  },
};

export const deliveryApi = {
  updateOrderStatus: (payload) => {
    return api.patch(`${API_PREFIX.delivery}/delivery-order/update-status`, {
      ...payload,
    });
  },

  getDrivers: () => {
    return api.get(
      `${API_PREFIX.auth}/delivery-staff/find-delivery-staff/true`,
    );
  },

  updateDriverStatus: () => {
    return api.patch(
      `${API_PREFIX.auth}/delivery-staff/update/status-finding-order`,
    );
  },

  getDriverStatus: () => {
    return api.get(`${API_PREFIX.auth}/delivery-staff/my-status-finding-order`);
  },
};

export const thirdPartyApi = {
  getDistricts: () => {
    return axios.get(`https://provinces.open-api.vn/api/p/01?depth=3`);
  },
};
