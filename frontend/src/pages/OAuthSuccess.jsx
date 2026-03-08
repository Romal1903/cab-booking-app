import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";

export default function OAuthSuccess() {
  const [params] = useSearchParams();

  useEffect(() => {
    const token = params.get("token");
    const needsRole = params.get("needsRole") === "true";

    if (!token) {
      alert("Authentication failed");
      window.location.href = "/";
      return;
    }

    localStorage.setItem("token", token);

    if (needsRole) {
      window.location.href = "/select-role";
    } else {
      window.location.href = "/";
    }
  }, []);

  return null;
}
