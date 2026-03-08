import { MapContainer, TileLayer, Marker, Polyline, useMap } from "react-leaflet";
import { useEffect, useState, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import L from "leaflet";

const carIcon = new L.Icon({
  iconUrl: "https://cdn-icons-png.flaticon.com/512/744/744465.png",
  iconSize: [40, 40],
  iconAnchor: [20, 20],
});

function RecenterMap({ position }) {
  const map = useMap();

  useEffect(() => {
    if (position) {
      map.flyTo(position, map.getZoom(), {
        animate: true,
        duration: 1,
      });
    }
  }, [position, map]);

  return null;
}

export default function LiveRideMap({ ride }) {
  const [driverPos, setDriverPos] = useState(null);
  const [path, setPath] = useState([]);
  const [eta, setEta] = useState(null);
  const [routeLine, setRouteLine] = useState([]);

  const markerRef = useRef(null);
  const clientRef = useRef(null);

  useEffect(() => {
    if (!driverPos) return;
    if (!markerRef.current) return;

    const marker = markerRef.current;
    const start = marker.getLatLng();
    const end = L.latLng(driverPos.lat, driverPos.lng);

    const frames = 30;
    let frame = 0;

    const interval = setInterval(() => {
      frame++;
      const lat = start.lat + (end.lat - start.lat) * (frame / frames);
      const lng = start.lng + (end.lng - start.lng) * (frame / frames);

      marker.setLatLng([lat, lng]);

      if (frame >= frames) clearInterval(interval);
    }, 50);

    return () => clearInterval(interval);
  }, [driverPos]);

  useEffect(() => {
    if (clientRef.current) return;

    const token = localStorage.getItem("token");

    const client = new Client({
      webSocketFactory: () => new SockJS(import.meta.env.VITE_WS_URL),
      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : ""
      },
      reconnectDelay: 10000,
      debug: () => {},
      onConnect: () => {
        console.log("Subscribed to driver location:", ride.id);

        client.subscribe(`/topic/driver-location/${ride.id}`, (message) => {
          const data = JSON.parse(message.body);
          console.log("Driver location received:", data);

          const newPos = {
            lat: data.latitude,
            lng: data.longitude
          };

          setDriverPos(newPos);

          setPath(prev => {
            const updated = [...prev, [newPos.lat, newPos.lng]];
            if (updated.length > 100) updated.shift();
            return updated;
          });

          calculateETA(newPos.lat, newPos.lng);
        });
      }
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
    };
  }, [ride.id]);

  const calculateETA = async (lat, lng) => {
    try {
      const url =
        `https://router.project-osrm.org/route/v1/driving/` +
        `${lng},${lat};${ride.pickupLng},${ride.pickupLat}` +
        `?overview=false`;

      const res = await fetch(url);
      const data = await res.json();

      if (!data.routes || data.routes.length === 0) return;

      const seconds = data.routes[0].duration;
      setEta(Math.ceil(seconds / 60));
    } catch (err) {
      console.error("ETA calculation failed", err);
    }
  };

  useEffect(() => {
    if (ride.status !== "STARTED") return;

    const fetchRoute = async () => {
      try {
        const url =
          `https://router.project-osrm.org/route/v1/driving/` +
          `${ride.pickupLng},${ride.pickupLat};${ride.dropLng},${ride.dropLat}` +
          `?overview=full&geometries=geojson`;

        const res = await fetch(url);
        const data = await res.json();

        if (data.routes && data.routes.length > 0) {
          const coords = data.routes[0].geometry.coordinates.map(([lng, lat]) => [lat, lng]);
          setRouteLine(coords);
        }
      } catch (err) {
        console.error("Route line fetch failed", err);
      }
    };

    fetchRoute();
  }, [ride.pickupLat, ride.pickupLng, ride.dropLat, ride.dropLng, ride.status]);

  return (
    <div className="space-y-2">
      <MapContainer
        center={
          driverPos
            ? [driverPos.lat, driverPos.lng]
            : [ride.pickupLat, ride.pickupLng]
        }
        zoom={13}
        className="h-[350px] w-full rounded-xl"
      >
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

        <Marker position={[ride.pickupLat, ride.pickupLng]} />

        <Marker position={[ride.dropLat, ride.dropLng]} />

        {driverPos && (
          <>
            <RecenterMap position={[driverPos.lat, driverPos.lng]} />

            <Marker
              ref={(ref) => {
                if (ref) markerRef.current = ref;
              }}
              position={[driverPos.lat, driverPos.lng]}
              icon={carIcon}
            />
          </>
        )}

        {path.length > 0 && <Polyline positions={path} color="green" />}

        {ride.status === "STARTED" && routeLine.length > 0 && (
          <Polyline positions={routeLine} color="blue" />
        )}
      </MapContainer>

      {eta && (
        <div className="text-center text-sm text-slate-600">
          🚗 Driver arriving in ~{eta} minutes
        </div>
      )}
    </div>
  );
}
