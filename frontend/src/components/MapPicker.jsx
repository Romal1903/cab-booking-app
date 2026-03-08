import { MapContainer, TileLayer, Marker, useMapEvents, Polyline } from "react-leaflet";
import { useEffect, useState } from "react";
import L from "leaflet";
import axios from "axios";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

function LocationMarker({ onSelect }) {
  useMapEvents({
    click(e) {
      onSelect(e.latlng);
    },
  });
  return null;
}

export default function MapPicker({
  pickup,
  drop,
  setPickup,
  setDrop,
}) {

  const [route, setRoute] = useState([]);

  const handleSelect = (latlng) => {
    if (!pickup) {
      setPickup(latlng);
    } else if (!drop) {
      setDrop(latlng);
    }
  };

  useEffect(() => {

    if (!pickup || !drop) {
      setRoute([]);
      return;
    }

    const fetchRoute = async () => {

      const url =
        `https://router.project-osrm.org/route/v1/driving/` +
        `${pickup.lng},${pickup.lat};${drop.lng},${drop.lat}` +
        `?overview=full&geometries=geojson`;

      const res = await axios.get(url);

      const coords = res.data.routes[0].geometry.coordinates;

      const latlngs = coords.map(c => [c[1], c[0]]);

      setRoute(latlngs);

    };

    fetchRoute();

  }, [pickup, drop]);

  return (
    <MapContainer
      center={[20.60123571333762, 72.93202252962485]}
      zoom={13}
      className="h-[350px] w-full rounded-xl"
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <LocationMarker onSelect={handleSelect} />

      {pickup && <Marker position={pickup} />}

      {drop && <Marker position={drop} />}

      {route.length > 0 && (
        <Polyline positions={route} color="blue" />
      )}
    </MapContainer>
  );
}
