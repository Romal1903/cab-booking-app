import { useState } from "react";
import api from "../api/axios";
import Button from "./Button";
import Card from "./Card";

export default function RatingModal({ rideId, onClose, onSubmitted }) {
  const [rating, setRating] = useState(3);
  const [review, setReview] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submitRating = async () => {
    if (submitting) return;

    try {
      setSubmitting(true);

      const res = await api.post("/ratings", {
        rideId,
        rating,
        review
      });

      onSubmitted({
        rating: res.data.rating ?? rating,
        review: res.data.review ?? review
      });

    } catch (err) {
      console.error("Rating submission failed", err);
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex justify-center items-center">
      <Card className="w-full max-w-sm space-y-4">
        <h2 className="text-lg font-semibold text-center">
          Rate Your Ride
        </h2>

        <div className="flex justify-center gap-3">
          {[1, 2, 3, 4, 5].map((num) => (
            <label key={num} className="flex flex-col items-center cursor-pointer">
              <input
                type="radio"
                name="rating"
                value={num}
                checked={rating === num}
                onChange={() => setRating(num)}
                className="hidden"
              />
              <span
                className={`text-2xl ${
                  rating >= num ? "text-yellow-400" : "text-gray-300"
                }`}
              >
                ★
              </span>
              <span className="text-xs">{num}</span>
            </label>
          ))}
        </div>

        <textarea
          className="w-full border rounded p-2 text-sm"
          placeholder="Optional review"
          value={review}
          onChange={(e) => setReview(e.target.value)}
        />

        <div className="flex justify-center gap-2">
          <Button onClick={submitRating} disabled={submitting}>
            {submitting ? "Submitting..." : "Submit"}
          </Button>
          <Button onClick={onClose} variant="secondary">
            Cancel
          </Button>
        </div>
      </Card>
    </div>
  );
}
