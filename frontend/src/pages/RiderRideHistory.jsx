import { useEffect, useState } from "react";
import api from "../api/axios";
import Button from "../components/Button";
import Card from "../components/Card";
import StripePayment from "../components/StripePayment";
import RatingModal from "../components/RatingModal";
import { connectSocket, disconnectSocket } from "../websocket/socket";
import LiveRideMap from "../components/LiveRideMap";
import SOSButton from "../components/SOSButton";
import { useNavigate } from "react-router-dom";

export default function RiderRideHistory() {

  const [rides, setRides] = useState([]);
  const [selectedRide, setSelectedRide] = useState(null);
  const [ratingRideId, setRatingRideId] = useState(null);
  const [ratedRides, setRatedRides] = useState({});
  const [rideRatings, setRideRatings] = useState({});
  const [supportRideId, setSupportRideId] = useState(null);

  useEffect(() => {

    loadRides();

    connectSocket((event) => {  
      if (event.event === "PAYMENT_COMPLETED") {
        loadRides();
      }
    });

    return () => disconnectSocket();

  }, []);

  const loadRides = async () => {

    try {

      const res = await api.get("/rides/rider/history");
      setRides(res.data);

      const ratedMap = {};
      const ratingData = {};

      await Promise.all(
        res.data.map(async (ride) => {

          const exists = await api.get(`/ratings/rider/exists/${ride.id}`);
          ratedMap[ride.id] = exists.data;

          if (exists.data) {
            const r = await api.get(`/ratings/ride/${ride.id}`);
            ratingData[ride.id] = r.data;
          }

        })
      );

      setRatedRides(ratedMap);
      setRideRatings(ratingData);

    } catch (err) {

      console.error("Failed to load rides", err);

    }

  };

  const cancelRide = async (id) => {

    try {

      await api.post(`/rides/cancel/${id}`);
      loadRides();

    } catch (err) {

      console.error("Cancel failed", err);

    }

  };

  const handleRatingSuccess = (ratingData) => {

    const rideId = ratingRideId;

    setRatedRides(prev => ({
      ...prev,
      [rideId]: true
    }));

    setRideRatings(prev => ({
      ...prev,
      [rideId]: ratingData
    }));

    setRatingRideId(null);

  };

  const downloadInvoice = async (rideId) => {

    try {

      const res = await api.get(`/rides/invoice/${rideId}`, {
        responseType: "blob"
      });

      const blob = new Blob([res.data], { type: "application/pdf" });

      const url = window.URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `invoice-${rideId}.pdf`;

      document.body.appendChild(a);
      a.click();

      a.remove();
      window.URL.revokeObjectURL(url);

    } catch (err) {

      console.error("Invoice download failed", err);
      alert("Failed to download invoice");
    }
  };

  const createSupportTicket = async (rideId) => {

    try {

      const subject = prompt("Issue subject");
      if (!subject) return;

      const message = prompt("Describe your issue");
      if (!message) return;

      await api.post("/support", {
        rideId: rideId,
        subject: subject,
        message: message,
        status: "OPEN"
      });

      alert("Support ticket submitted");

    } catch (err) {

      console.error("Support ticket failed", err);
      alert("Failed to submit ticket");

    }

  };

  return (
    <div className="flex justify-center">

      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">

        {rides.length === 0 && (
          <p className="text-slate-500">No rides yet</p>
        )}

        {rides.map((ride) => (

          <Card key={ride.id}>

            <div className="space-y-3 text-left">

              <div>
                Pickup: {ride.pickupLat}, {ride.pickupLng}
              </div>

              <div>
                Drop: {ride.dropLat}, {ride.dropLng}
              </div>

              <div className="font-semibold">
                Status: {ride.status}
              </div>

              {ride.driverName && (
                <div className="text-sm text-slate-500">
                  Driver: {ride.driverName}
                </div>
              )}

              {ride.status === "STARTED" && (
                <>
                  <LiveRideMap ride={ride} />

                  <div className="flex justify-center mt-3">
                    <SOSButton rideId={ride.id} />
                  </div>
                </>
              )}

              <div className="text-green-600 font-semibold">
                Fare: ₹{ride.fare}
              </div>

              <div
                className={`font-medium ${
                  ride.paymentStatus === "PAID"
                    ? "text-green-600"
                    : "text-orange-500"
                }`}
              >
                Payment: {ride.paymentStatus}
              </div>

              {ratedRides[ride.id] && rideRatings[ride.id]?.rating && (
                <div className="text-sm text-slate-600">
                  ⭐ Rating: {rideRatings[ride.id].rating}/5
                  {rideRatings[ride.id].review && (
                    <div className="italic text-slate-500">
                      “{rideRatings[ride.id].review}”
                    </div>
                  )}
                </div>
              )}

              {ride.status === "COMPLETED" &&
                ride.paymentStatus === "PENDING" && (

                  selectedRide === ride.id ? (
                    <StripePayment
                      rideId={ride.id}
                      onSuccess={() => {
                        setSelectedRide(null);
                        loadRides();
                      }}
                    />
                  ) : (
                    <div className="flex justify-center">
                      <Button onClick={() => setSelectedRide(ride.id)}>
                        Pay Now
                      </Button>
                    </div>
                  )
                )}

              {ride.status === "COMPLETED" &&
                ride.paymentStatus === "PAID" &&
                !ratedRides[ride.id] && (
                  <div className="flex justify-center">
                    <Button onClick={() => setRatingRideId(ride.id)}>
                      Rate Ride
                    </Button>
                  </div>
                )}

              {ride.status === "COMPLETED" &&
                ride.paymentStatus === "PAID" && (
                  <div className="flex justify-center">
                    <Button onClick={() => downloadInvoice(ride.id)}>
                      Download Invoice
                    </Button>
                  </div>
                )}

                <div className="flex justify-center">
                  <Button onClick={() => createSupportTicket(ride.id)}>
                    Report Issue
                  </Button>
                </div>

              {(ride.status === "REQUESTED" ||
                ride.status === "ACCEPTED") && (
                <div className="flex justify-center">
                  <Button onClick={() => cancelRide(ride.id)}>
                    Cancel Ride
                  </Button>
                </div>
              )}
            </div>
          </Card>
        ))}
      </div>

      {ratingRideId && (
        <RatingModal
          rideId={ratingRideId}
          onClose={() => setRatingRideId(null)}
          onSubmitted={handleRatingSuccess}
        />
      )}
    </div>
  );
}
