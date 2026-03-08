import { useEffect, useState, useRef } from "react";
import api from "../api/axios";
import Button from "../components/Button";
import Card from "../components/Card";
import { connectSocket, disconnectSocket } from "../websocket/socket";

export default function DriverActiveRides() {

  const [rides, setRides] = useState([]);
  const watchId = useRef(null);

  const getDriverId = () => {
  try {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user?.driverId) return user.driverId;
    if (user?.id) return user.id;
  } catch {}

  console.error("Driver not found in localStorage");
  return null;
};

  const startLocationTracking = (rideId) => {

    if (!navigator.geolocation) return;

    const driverId = getDriverId();
    if (!driverId) return;

    watchId.current = navigator.geolocation.watchPosition(
      async (pos) => {

        const latitude = pos.coords.latitude;
        const longitude = pos.coords.longitude;

        console.log("Sending driver location:", latitude, longitude);

        try {

          await api.post("/location/update", {
            driverId: driverId,
            rideId: rideId,
            latitude: latitude,
            longitude: longitude
          });

        } catch (err) {

          console.error(
            "Location update failed:",
            err.response?.data || err.message
          );

        }

      },
      (err) => console.error("GPS error:", err),
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        timeout: 10000
      }
    );
  };

  const stopLocationTracking = () => {

    if (watchId.current) {
      navigator.geolocation.clearWatch(watchId.current);
      watchId.current = null;
    }

  };

  const loadRides = () =>
    api.get("/rides/driver/active").then(res => setRides(res.data));

  useEffect(() => {

    loadRides();

    connectSocket((event) => {

      if (
        event.event === "RIDE_ACCEPTED" ||
        event.event === "RIDE_STARTED"
      ) {
        loadRides();
      }

      if (event.event === "SOS_ALERT") {
        alert("🚨 EMERGENCY SOS from rider on ride " + event.rideId);
      }

    });

    return () => {
      disconnectSocket();
      stopLocationTracking();
    };

  }, []);

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">

        {rides.length === 0 && (
          <p className="text-center text-slate-500">
            No active rides
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

              <div className="text-sm">
                Distance: {ride.distanceKm} km
              </div>

              <div className="text-sm font-medium text-green-600">
                Fare: ₹{ride.fare}
              </div>

              <div className="text-sm font-semibold">
                Status: {ride.status}
              </div>

              <div className="flex justify-center">

                {ride.status === "ACCEPTED" && (

                  <Button
                    onClick={() => {

                      api.post(`/rides/start/${ride.id}`)
                      .then(() => {
                        startLocationTracking(ride.id);
                        loadRides();
                      });
                    }}
                  >
                    Start Ride
                  </Button>

                )}

                {ride.status === "STARTED" && (

                  <Button
                    onClick={() => {

                      api.post(`/rides/complete/${ride.id}`)
                        .then(() => {

                          stopLocationTracking();
                          loadRides();

                        });

                    }}
                  >
                    Complete Ride
                  </Button>

                )}

              </div>

            </div>

          </Card>

        ))}

      </div>

    </div>
  );
}
