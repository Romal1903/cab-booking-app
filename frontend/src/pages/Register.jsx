import { useState } from "react";
import api from "../api/axios";
import Input from "../components/Input";
import Button from "../components/Button";
import Card from "../components/Card";

export default function Register({ onSwitch }) {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    role: "RIDER",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const register = async () => {
    setError("");

    if (!form.name || !form.email || !form.password) {
      setError("All fields are required");
      return;
    }

    if (form.password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    try {
      setLoading(true);
      await api.post("/auth/register", form);
      onSwitch();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        "Registration failed"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <h2 className="text-2xl font-semibold text-center mb-4 text-primary">
        Register
      </h2>

      <div className="space-y-3">
        <Input placeholder="Name"
          onChange={e => setForm({ ...form, name: e.target.value })}
        />
        <Input placeholder="Email"
          onChange={e => setForm({ ...form, email: e.target.value })}
        />
        <Input type="password" placeholder="Password (min 6 chars)"
          onChange={e => setForm({ ...form, password: e.target.value })}
        />

        <select
          className="w-full px-4 py-2 border rounded-lg"
          onChange={e => setForm({ ...form, role: e.target.value })}
        >
          <option value="RIDER">Rider</option>
          <option value="DRIVER">Driver</option>
        </select>

        {error && <p className="text-sm text-red-500">{error}</p>}

        <div className="flex justify-center">
          <Button onClick={register} disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </Button>
        </div>
      </div>

      <p className="text-center text-sm mt-4">
        Already have an account?{" "}
        <span
          onClick={onSwitch}
          className="text-primary cursor-pointer font-medium
                    hover:text-primaryDark hover:underline
                    transition-all duration-200 hover:scale-105 inline-block"
        >
          Login
        </span>
      </p>
    </Card>
  );
}
