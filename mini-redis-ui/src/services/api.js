import axios from "axios";

const baseURL = (import.meta.env.VITE_API_BASE_URL || "/api/cache").replace(/\/$/, "");

const api = axios.create({
    baseURL,
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 10_000,
});

export default api;
