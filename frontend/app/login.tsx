import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaGithub } from "react-icons/fa";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const text = await response.text();
                setMessage(text);
                setTimeout(() => {
                    navigate("/dashboard");
                }, 500);
            }
        } catch (error) {
            console.error("Fetch error:", error);
            setMessage("Server nicht erreichbar");
        }
    }

    function handleGitHubLogin() {
        // Spring Boot's automatischer OAuth2 Endpoint
        // Spring leitet automatisch zu GitHub weiter und behandelt den kompletten OAuth Flow
        window.location.href = "http://localhost:8080/oauth2/authorization/github";
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-900">
            <div className="w-96 rounded-lg">
                {/* Normales Login-Formular */}
                <form
                    onSubmit={handleSubmit}
                    className="bg-gray-800 p-8  shadow-lg "
                >
                    <h1 className="text-3xl font-bold mb-6 text-white text-center">
                        Login
                    </h1>
                    <div className="mb-4">
                        <label className="block text-gray-300 text-sm mb-2">E-Mail</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="you@example.com"
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
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
                    >
                        Login
                    </button>
                    {message && <p className="text-center text-white">{message}</p>}
                </form>


                <div className="flex flex-col items-center bg-gray-800 shadow-lg px-8">
                    <span className="text-white mb-4 ">or</span>
                    <button onClick={() => handleGitHubLogin()} className="
                        flex w-full
                        py-2
                        items-center
                        justify-center
                        bg-red-800
                        text-white
                        rounded
                        hover:bg-gray-700
                        mb-4 ">
                        <FaGithub className="mr-2" />
                        Login mit GitHub
                    </button>
                </div>
            </div>
        </div>
    );
}
