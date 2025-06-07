import { create } from "zustand";
import { persist } from "zustand/middleware";
import roles from "../constants/roles";

export const useAuthStore = create(
  (set) => ({
    // user: {
    //   fullName: "duong",
    //   roles: [
    //     roles.CUSTOMER
    //   ]
    // },
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
