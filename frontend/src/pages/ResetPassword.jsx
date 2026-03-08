import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../api/axios";
import Card from "../components/Card";
import Input from "../components/Input";
import Button from "../components/Button";

export default function ResetPassword() {
  const [params] = useSearchParams();
  const token = params.get("token");

  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const reset = async () => {
    setError("");

    if (!password || password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    try {
      setLoading(true);
      await api.post("/auth/reset-password", {
        token,
        newPassword: password
      });
      window.location.href = "/";
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        "Invalid or expired reset link"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <Card>
        <h2 className="text-xl font-semibold mb-4 text-primary">
          Reset Password
        </h2>

        <Input
          type="password"
          placeholder="New password"
          onChange={e => setPassword(e.target.value)}
        />

        {error && <p className="text-sm text-red-500">{error}</p>}

        <Button onClick={reset} disabled={loading}>
          {loading ? "Resetting..." : "Reset Password"}
        </Button>
      </Card>
    </div>
  );
}
