import { useState } from "react";
import api from "../api/axios";
import Input from "../components/Input";
import Button from "../components/Button";
import Card from "../components/Card";

export default function Login({ onForgot, onRegister }) {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const login = async () => {
    setError("");

    if (!form.email || !form.password) {
      setError("Email and password are required");
      return;
    }

    try {
      setLoading(true);

      const res = await api.post("/auth/login", form);

      const token = res.data.token;

      localStorage.setItem("token", token);

      const me = await api.get("/auth/me");

      localStorage.setItem("user", JSON.stringify(me.data));

      window.location.reload();

    } catch (err) {
      const status = err.response?.status;

      setError(
        err.response?.data?.message ||
        err.response?.data ||
        (status === 401
          ? "Invalid email or password"
          : status === 403
          ? "Account disabled by admin"
          : "Login failed")
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <h2 className="text-2xl font-semibold text-center mb-4 text-primary">
        Login
      </h2>

      <div className="space-y-3">
        <Input
          placeholder="Email"
          onChange={e => setForm({ ...form, email: e.target.value })}
        />

        <Input
          type="password"
          placeholder="Password"
          onChange={e => setForm({ ...form, password: e.target.value })}
        />

        {error && <p className="text-sm text-red-500">{error}</p>}

        <div className="flex justify-center">
          <Button onClick={login} disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </Button>
        </div>
      </div>

      <div className="flex justify-center mt-2">
        <Button
          onClick={() => {
            window.location.href = import.meta.env.VITE_GOOGLE_AUTH_URL;
          }}
        >
          Login with Google
        </Button>
      </div>

      <p
        onClick={onForgot}
        className="text-sm text-primary cursor-pointer text-center mt-2 
                  hover:text-primaryDark hover:underline 
                  transition-all duration-200 hover:scale-105"
      >
        Forgot password?
      </p>

      <p className="text-center text-sm mt-4">
        Don’t have an account?{" "}
        <span
          onClick={onRegister}
          className="text-primary cursor-pointer font-medium
                    hover:text-primaryDark hover:underline
                    transition-all duration-200 hover:scale-105 inline-block"
        >
          Register
        </span>
      </p>
    </Card>
  );
}
