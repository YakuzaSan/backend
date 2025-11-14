const API_URL = import.meta.env.VITE_API_URL;

interface FetchOptions extends RequestInit {
    includeCsrf?: boolean;
    headers?: Record<string, string>;
}

async function fetchWithDefaults(
    endpoint: string,
    options: FetchOptions = {}
): Promise<Response> {
    const { includeCsrf = true, headers = {}, ...rest } = options;

    const defaultHeaders: Record<string, string> = {
        "Content-Type": "application/json",
        ...headers,
    };

    if (includeCsrf) {
        const csrfToken = document.cookie
            .split("; ")
            .find((row) => row.startsWith("XSRF-TOKEN="))
            ?.split("=")[1];

        if (csrfToken) {
            defaultHeaders["X-XSRF-TOKEN"] = csrfToken;
        }
    }

    return fetch(`${API_URL}${endpoint}`, {
        credentials: "include",
        headers: defaultHeaders,
        ...rest,
    });
}

export const api = {
    post: (endpoint: string, data: Record<string, unknown>) =>
        fetchWithDefaults(endpoint, {
            method: "POST",
            body: JSON.stringify(data),
        }),

    get: (endpoint: string) =>
        fetchWithDefaults(endpoint, { method: "GET", includeCsrf: false }),

    logout: () =>
        fetchWithDefaults("/api/logout", { method: "POST" }),
};
