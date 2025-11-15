import { Outlet } from "react-router";
import { useAuth } from "./hooks/useAuth";

export default function Dashboard() {
    const { user, loading, logout } = useAuth();

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gray-900">
                <div className="text-center">
                    <p className="text-white text-lg">Authentifizierung wird geprüft...</p>
                    <div className="mt-4 flex justify-center">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500" />
                    </div>
                </div>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gray-900">
                <div className="text-center">
                    <p className="text-red-400 text-lg">Authentifizierung erforderlich</p>
                </div>
            </div>
        );
    }

    async function handleLogout() {
        await logout();
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
