import { useEffect, useState } from "react";
import api from "../api/axios";
import Card from "../components/Card";
import { connectSocket, disconnectSocket } from "../websocket/socket";

export default function DriverCompletedRides() {
  const [rides, setRides] = useState([]);

  const loadRides = () =>
    api.get("/rides/driver/completed").then(res => setRides(res.data));

  useEffect(() => {
    loadRides();

    connectSocket((event) => {
      if (event.event === "RIDE_COMPLETED") {
        loadRides();
      }
    });

    return () => disconnectSocket();
  }, []);

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">
        {rides.length === 0 && (
          <p className="text-center text-slate-500">
            No completed rides
          </p>
        )}

        {rides.map(ride => (
          <Card key={ride.id}>
            <div className="space-y-2 text-left">
              <div>
                Rider: <b>{ride.riderName}</b>
              </div>

              <div>
                Pickup: {ride.pickupLat}, {ride.pickupLng}
              </div>

              <div>
                Drop: {ride.dropLat}, {ride.dropLng}
              </div>

              <div>
                Distance: {ride.distanceKm} km
              </div>

              <div className="text-green-600 font-semibold">
                Fare Earned: ₹{ride.fare}
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
