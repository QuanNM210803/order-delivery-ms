import { create } from "zustand";

export const useDeliveryStatusStore = create((set) => ({
  status: false,
  setStatus: (status) => {
    set({ status: status });
  },
}));
