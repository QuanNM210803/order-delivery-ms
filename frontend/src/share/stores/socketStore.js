import { io } from "socket.io-client";
import { create } from "zustand";
import tokenUtils from "../utils/tokenUtils";

export const useSocketStore = create((set) => ({
  socketClient: null,
  setSocketClient: (client) => set({ socketClient: client }),
  connectSocket: () => {
    const connectionUrl = "http://localhost:8099?token=" + tokenUtils.getAccessToken();
    const socketClient = new io(connectionUrl);
    set({ socketClient });
    socketClient.on("connect", () => {
      console.log("Socket connected");
    });
    socketClient.on("disconnect", () => {
      console.log("Socket disconnected");
    });
    return socketClient;
  }
}));