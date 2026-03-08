import api from "../api/axios";
import Button from "../components/Button";
import Card from "../components/Card";

export default function SelectRole() {
  const setRole = async (role) => {
    try {
      const res = await api.post("/auth/set-role", { role });
      localStorage.setItem("token", res.data.token);
      window.location.href = "/";
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data ||
        "Failed to set role";

      alert(msg);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-sky-50">
      <Card>
        <h2 className="text-xl font-semibold text-center mb-4 text-primary">
          Select Your Role
        </h2>

        <div className="flex gap-4 justify-center">
          <Button onClick={() => setRole("RIDER")}>Rider</Button>
          <Button onClick={() => setRole("DRIVER")}>Driver</Button>
        </div>
      </Card>
    </div>
  );
}
