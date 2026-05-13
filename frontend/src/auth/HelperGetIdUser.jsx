import { jwtDecode } from "jwt-decode";

export function getUserId() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    const decoded = jwtDecode(token);
    return decoded.userId; // zależy jak backend wystawia claim
}