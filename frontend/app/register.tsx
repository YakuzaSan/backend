import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { api } from "./utils/api";

export default function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState<"error" | "success">("error");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        
        if (password !== confirmPassword) {
            setMessageType("error");
            setMessage("Passwörter stimmen nicht überein!");
            return;
        }

        if (password.length < 6) {
            setMessageType("error");
            setMessage("Passwort muss mindestens 6 Zeichen lang sein!");
            return;
        }

        setLoading(true);
        try {
            const response = await api.post("/api/register", { email, password });
            const data = await response.json();

            if (response.ok && !data.error) {
                setMessageType("success");
                setMessage("Registrierung erfolgreich! Weiterleitung zum Dashboard...");
                setTimeout(() => {
                    navigate("/dashboard");
                }, 1500);
            } else {
                setMessageType("error");
                setMessage(data.error || "Registrierung fehlgeschlagen");
            }
        } catch (error) {
            console.error("Fetch error:", error);
            setMessageType("error");
            setMessage("Server nicht erreichbar");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-900">
            <div className="w-96">
                <form
                    onSubmit={handleSubmit}
                    className="bg-gray-800 p-8 rounded-lg shadow-lg"
                >
                    <h1 className="text-3xl font-bold mb-6 text-white text-center">
                        Registrieren
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

                    <div className="mb-4">
                        <label className="block text-gray-300 text-sm mb-2">Passwort</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="••••••••"
                            minLength={6}
                            disabled={loading}
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-gray-300 text-sm mb-2">Passwort bestätigen</label>
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="••••••••"
                            minLength={6}
                            disabled={loading}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white py-2 rounded font-semibold transition"
                    >
                        {loading ? "Wird registriert..." : "Registrieren"}
                    </button>
                </form>

                <div className="text-center mt-4 text-gray-300">
                    Bereits registriert?{" "}
                    <Link to="/" className="text-blue-400 hover:text-blue-300 font-semibold">
                        Zum Login
                    </Link>
                </div>
            </div>
        </div>
    );
}
