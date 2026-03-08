import { useEffect, useState } from "react";
import api from "../api/axios";
import Button from "../components/Button";
import Card from "../components/Card";
import { connectSocket, disconnectSocket } from "../websocket/socket";

export default function DriverAvailableRides() {
  const [rides, setRides] = useState([]);

  const loadRides = () =>
    api.get("/rides/available").then(res => setRides(res.data));

  useEffect(() => {
    loadRides();

    connectSocket((event) => {

      if (event.event === "RIDE_REQUESTED") {
        loadRides();
      }

      if (event.event === "SOS_ALERT") {
        alert("🚨 SOS ALERT from active rider!");
      }

    });

    return () => disconnectSocket();
  }, []);

  const acceptRide = async (id) => {
    await api.post(`/rides/accept/${id}`);
    setRides(rides.filter(r => r.id !== id));
  };

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">
        {rides.length === 0 && (
          <p className="text-center text-slate-500">
            No available rides
          </p>
        )}

        {rides.map(ride => (
          <Card key={ride.id}>
            <div className="space-y-2 text-left">
              <div>
                Rider: <b>{ride.riderName}</b>
              </div>

              <div className="text-sm">
                Pickup: {ride.pickupLat}, {ride.pickupLng}
              </div>

              <div className="text-sm">
                Drop: {ride.dropLat}, {ride.dropLng}
              </div>

              <div className="text-sm font-medium text-green-600">
                Fare: ₹{ride.fare}
              </div>

              <div className="flex justify-center">
                <Button onClick={() => acceptRide(ride.id)}>
                  Accept Ride
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
