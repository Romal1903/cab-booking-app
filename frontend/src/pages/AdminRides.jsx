import { useEffect, useState } from "react";
import { getAllRides } from "../api/admin";
import Card from "../components/Card";
import { connectSocket, disconnectSocket } from "../websocket/socket";

export default function AdminRides() {
  const [rides, setRides] = useState([]);

  useEffect(() => {
    loadRides();

    connectSocket((event) => {

      if (
        event.event === "RIDE_REQUESTED" ||
        event.event === "RIDE_ACCEPTED" ||
        event.event === "RIDE_STARTED" ||
        event.event === "RIDE_COMPLETED" ||
        event.event === "RIDE_CANCELLED" ||
        event.event === "PAYMENT_COMPLETED"
      ) {
        loadRides();
      }

      if (event.event === "SOS_ALERT") {
        alert("🚨 SOS ALERT for ride " + event.rideId);
      }

    });

    return () => disconnectSocket();
  }, []);

  const loadRides = () => {
    getAllRides()
      .then(res => setRides(res.data))
      .catch(console.error);
  };

  return (
    <div className="min-h-screen bg-sky-50">
      <div className="flex justify-center px-4 py-6">
        <div className="w-full max-w-3xl flex flex-col items-center space-y-6">

          {rides.length === 0 && (
            <p className="text-slate-500">No rides available</p>
          )}

          {rides.map(ride => (
            <Card key={ride.id}>
              <div className="space-y-2 text-left">
                <div>
                  Rider: <b>{ride.riderName ?? "—"}</b>
                </div>
                <div>
                  Driver: <b>{ride.driverName ?? "—"}</b>
                </div>
                <div className="text-sm">
                  Status: <span className="font-medium">{ride.status}</span>
                </div>
                <div className="text-green-600 font-semibold">
                  Fare: ₹{ride.fare}
                </div>
                <div className="text-sm">
                  Payment: {ride.paymentStatus}
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}
