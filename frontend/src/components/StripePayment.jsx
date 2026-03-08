import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import Button from "./Button";
import api from "../api/axios";

export default function StripePayment({ rideId, onSuccess }) {
  const stripe = useStripe();
  const elements = useElements();
  const isReady = stripe && elements;

  const pay = async () => {
    if (!stripe || !elements) return;

    let clientSecret;

    try {
      const res = await api.post(`/rides/pay/${rideId}`);
      clientSecret = res.data.clientSecret;
    } catch (err) {
      console.error("Backend payment error", err.response?.data || err);
      alert("Payment initialization failed. Please try again.");
      return;
    }

    const result = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    });

    if (result.error) {
      alert(result.error.message);
      return;
    }

    if (result.paymentIntent?.status === "succeeded") {
      await api.post("/rides/pay/confirm", {
        paymentIntentId: result.paymentIntent.id,
      });

      onSuccess();
    }
  };

  return (
    <div className="space-y-3">
      <CardElement />

      <div className="flex justify-center">
        <Button onClick={pay} disabled={!stripe || !elements}>
          {!stripe ? "Loading..." : "Pay Fare"}
        </Button>
      </div>
    </div>
  );
}
