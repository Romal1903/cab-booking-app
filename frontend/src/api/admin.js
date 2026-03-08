import api from "./axios";

export const getAdminStats = () => api.get("/admin/stats");
export const getAllUsers = () => api.get("/admin/users");
export const getDrivers = () => api.get("/admin/drivers");
export const getAllRides = () => api.get("/admin/rides");
export const toggleUserStatus = (id, enabled) => api.patch(`/admin/users/${id}/status?enabled=${enabled}`);
