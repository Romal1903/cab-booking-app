import { useState, useEffect } from "react";
import { Routes, Route } from "react-router-dom";
import api from "./api/axios";
import Login from "./pages/Login";
import Register from "./pages/Register";
import RiderHome from "./pages/RiderHome";
import DriverHome from "./pages/DriverHome";
import AdminLayout from "./pages/AdminLayout";
import ForgotPassword from "./pages/ForgotPassword";
import OAuthSuccess from "./pages/OAuthSuccess";
import SelectRole from "./pages/SelectRole";

function App() {
  const token = localStorage.getItem("token");
  const [user, setUser] = useState(null);
  const [page, setPage] = useState("login");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;

    api.get("/auth/me")
      .then(res => setUser(res.data))
      .catch(() => {
        localStorage.removeItem("token");
        setUser(null);
      });
  }, []);

  return (
    <Routes>
      <Route path="/oauth-success" element={<OAuthSuccess />} />
      <Route path="/select-role" element={<SelectRole />} />

      <Route
        path="*"
        element={
          token && user?.role === "ADMIN" ? (
            <AdminLayout />
          ) : token && user?.role === "DRIVER" ? (
            <DriverHome />
          ) : token && user?.role === "RIDER" ? (
            <RiderHome />
          ) : (
            <div className="min-h-screen flex items-center justify-center bg-sky-50">
              {page === "login" && (
                <Login
                  onForgot={() => setPage("forgot")}
                  onRegister={() => setPage("register")}
                />
              )}

              {page === "register" && (
                <Register onSwitch={() => setPage("login")} />
              )}

              {page === "forgot" && (
                <ForgotPassword onBack={() => setPage("login")} />
              )}
            </div>
          )
        }
      />
    </Routes>
  );
}

export default App;
