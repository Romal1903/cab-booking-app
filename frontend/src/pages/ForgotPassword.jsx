import { useState } from "react";
import api from "../api/axios";
import Input from "../components/Input";
import Button from "../components/Button";

export default function ForgotPassword({ onBack }) {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setError("");
    setSuccess("");

    if (!email) {
      setError("Email is required");
      return;
    }

    try {
      setLoading(true);
      const res = await api.post("/auth/forgot-password", { email });
      setSuccess(res.data?.message || "Reset link sent");
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        "Failed to send reset link"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-md space-y-4 w-full max-w-md">
      <h2 className="text-lg font-semibold text-primary">
        Forgot Password
      </h2>

      <Input
        type="email"
        placeholder="Enter your email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      {error && <p className="text-sm text-red-500">{error}</p>}
      {success && <p className="text-sm text-green-600">{success}</p>}

      <div className="flex justify-center">
        <Button onClick={handleSubmit} disabled={loading}>
          {loading ? "Sending..." : "Send Reset Link"}
        </Button>
      </div>

      <p
        onClick={onBack}
        className="text-sm text-primary cursor-pointer text-center mt-2 
                  hover:text-primaryDark hover:underline 
                  transition-all duration-200 hover:scale-105"
      >
        Back to Login
      </p>
    </div>
  );
}
