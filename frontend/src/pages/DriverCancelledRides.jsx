import { useEffect, useState } from "react";
import api from "../api/axios";
import Card from "../components/Card";

export default function DriverCancelledRides() {
  const [rides, setRides] = useState([]);

  useEffect(() => {
    api.get("/rides/driver/cancelled").then(res => setRides(res.data));
  }, []);

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">
        {rides.length === 0 && (
          <p className="text-center text-slate-500">
            No cancelled rides
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

              <div className="font-semibold text-red-500">
                CANCELLED
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
