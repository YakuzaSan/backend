import { useEffect, useState } from "react";
import { Outlet } from "react-router";

interface User {
    name?: string;
    login: string;
    email?: string;
    avatar_url?: string;
    id?: number;
}

export default function Dashboard() {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        fetch("http://localhost:8080/api/user", {
            credentials: "include",
        })
            .then((res) => res.json())
            .then((data) => {
                if (!data.error) {
                    setUser(data);
                }
            })
            .catch((err) => console.error("Fehler beim Laden des Users:", err));
    }, []);

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-4">Dashboard</h1>

            {user ? (
                <div className="bg-gray-100 p-4 rounded-lg">
                    <p>
                        <strong>👋 Willkommen,</strong> {user.name || user.login}!
                    </p>
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
                </div>
            ) : (
                <p>Lade Benutzerinformationen...</p>
            )}

            <Outlet />
        </div>
    );
}
