import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { FaGithub } from "react-icons/fa";
import { api } from "./utils/api";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState<"error" | "success">("error");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await api.post("/api/login", { email, password });
            const data = await response.json();

            if (response.ok && !data.error) {
                setMessageType("success");
                setMessage("Login erfolgreich!");
                setTimeout(() => {
                    navigate("/dashboard");
                }, 500);
            } else {
                setMessageType("error");
                setMessage(data.error || "Login fehlgeschlagen");
            }
        } catch (error) {
            console.error("Fetch error:", error);
            setMessageType("error");
            setMessage("Server nicht erreichbar");
        } finally {
            setLoading(false);
        }
    }

    function handleGitHubLogin() {
        const apiUrl = import.meta.env.VITE_API_URL;
        window.location.href = `${apiUrl}/oauth2/authorization/github`;
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-900">
            <div className="w-96">
                <form
                    onSubmit={handleSubmit}
                    className="bg-gray-800 p-8 rounded-lg shadow-lg"
                >
                    <h1 className="text-3xl font-bold mb-6 text-white text-center">
                        Login
                    </h1>

                    {message && (
                        <div className={`mb-4 p-3 rounded text-sm ${
                            messageType === "error" 
                                ? "bg-red-900 text-red-200" 
                                : "bg-green-900 text-green-200"
                        }`}>
                            {message}
                        </div>
                    )}

                    <div className="mb-4">
                        <label className="block text-gray-300 text-sm mb-2">E-Mail</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="you@example.com"
                            disabled={loading}
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-300 text-sm mb-2">Passwort</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="••••••••"
                            disabled={loading}
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white py-2 rounded font-semibold transition"
                    >
                        {loading ? "Wird angemeldet..." : "Login"}
                    </button>
                </form>

                <div className="flex flex-col items-center bg-gray-800 shadow-lg px-8 py-6 rounded-lg">
                    <span className="text-gray-400 text-sm mb-4">oder</span>
                    <button 
                        onClick={() => handleGitHubLogin()} 
                        disabled={loading}
                        className="flex w-full py-2 items-center justify-center bg-gray-700 hover:bg-gray-600 disabled:bg-gray-600 text-white rounded font-semibold transition mb-6"
                    >
                        <FaGithub className="mr-2" />
                        Mit GitHub anmelden
                    </button>
                </div>

                <div className="text-center mt-4 text-gray-300">
                    Noch nicht registriert?{" "}
                    <Link to="/register" className="text-blue-400 hover:text-blue-300 font-semibold">
                        Hier registrieren
                    </Link>
                </div>
            </div>
        </div>
    );
}
