import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;

export const connectSocket = (onMessage) => {
  if (stompClient?.active) return;

  const token = localStorage.getItem("token");

  stompClient = new Client({
    webSocketFactory: () =>
      new SockJS(import.meta.env.VITE_WS_URL),

    connectHeaders: {
      Authorization: token ? `Bearer ${token}` : "",
    },

    reconnectDelay: 5000,
    debug: () => {},

    onConnect: () => {

      stompClient.subscribe("/topic/drivers", (message) => {
        onMessage(JSON.parse(message.body));
      });

      stompClient.subscribe("/user/queue/rider", (message) => {
        onMessage(JSON.parse(message.body));
      });

      stompClient.subscribe("/topic/admin/rides", (message) => {
        onMessage(JSON.parse(message.body));
      });
    },
  });

  stompClient.activate();
};

export const disconnectSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
};
