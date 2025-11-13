import { useEffect, useState } from "react";
import { useNavigate, Outlet } from "react-router";

interface User {
    name?: string;
    login?: string;
    email?: string;
    avatar_url?: string;
    id?: number;
    type?: string;
}

export default function Dashboard() {
    const [user, setUser] = useState<User | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`${import.meta.env.VITE_API_URL}/api/user`, {
            credentials: "include",
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.error) {
                    navigate("/");
                } else {
                    setUser(data);
                }
            })
            .catch(() => navigate("/"));
    }, [navigate]);

    async function handleLogout() {
        try {
            await fetch(`${import.meta.env.VITE_API_URL}/api/logout`, {
                method: "POST",
                credentials: "include",
            });
            setUser(null);
            navigate("/");
        } catch (error) {
            console.error("Logout error:", error);
        }
    }

    if (!user) {
        return <p className="p-8">Loading...</p>;
    }

    return (
        <div className="p-8">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-3xl font-bold">Dashboard</h1>
                <button
                    onClick={handleLogout}
                    className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
                >
                    Logout
                </button>
            </div>

            <div className="bg-gray-100 p-4 rounded-lg">
                <p>
                    <strong>👋 Willkommen,</strong> {user.name}!
                </p>
                
                {user.type === "github" && (
                    <>
                        <p>
                            GitHub Username: <strong>{user.login}</strong>
                        </p>
                        {user.avatar_url && (
                            <img
                                src={user.avatar_url}
                                alt="Avatar"
                                className="w-20 h-20 rounded-full mt-4"
                            />
                        )}
                    </>
                )}
            </div>

            <Outlet />
        </div>
    );
}
