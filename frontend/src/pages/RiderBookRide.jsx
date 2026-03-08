import { useState, useEffect } from "react";
import api from "../api/axios";
import Button from "../components/Button";
import MapPicker from "../components/MapPicker";

export default function BookRide() {
  const [pickup, setPickup] = useState(null);
  const [drop, setDrop] = useState(null);
  const [estimate, setEstimate] = useState(null);
  const [distance, setDistance] = useState(null);

  useEffect(() => {
    if (pickup && drop) {
      api.get("/rides/estimate", {
        params: {
          pickupLat: pickup.lat,
          pickupLng: pickup.lng,
          dropLat: drop.lat,
          dropLng: drop.lng
        }
      }).then(res => setEstimate(res.data));
    }
  }, [pickup, drop]);

  const bookRide = async () => {
    await api.post("/rides/book", {
      pickupLat: pickup.lat,
      pickupLng: pickup.lng,
      dropLat: drop.lat,
      dropLng: drop.lng
    });
    alert("Ride booked");
    setPickup(null);
    setDrop(null);
    setEstimate(null);
  };

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-4 text-center">
        <MapPicker pickup={pickup} drop={drop} setPickup={setPickup} setDrop={setDrop} setDistance={setDistance}/>

        {estimate && (
          <div className="text-center space-y-1">
            <div>
              Distance: {estimate.distanceKm} km
            </div>

            <div className="text-blue-600">
              ETA: ~{Math.ceil(estimate.distanceKm * 3)} minutes
            </div>

            <div className="text-green-600 font-semibold">
              Fare: ₹{estimate.estimatedFare}
            </div>
          </div>
        )}

        <div className="flex justify-center">
          <Button onClick={bookRide}>Confirm Ride</Button>
        </div>
      </div>
    </div>
  );
}
