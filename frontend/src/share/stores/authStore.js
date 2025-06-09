import { create } from "zustand";
import roles from "../constants/roles";

export const useAuthStore = create(
  (set) => ({
    user: null,
    setUser: (userData) => {
      set({ user: userData });
    },
    logout: () => set({ user: null }),
  }),
  {
    name: "auth-storage",
  },
);
