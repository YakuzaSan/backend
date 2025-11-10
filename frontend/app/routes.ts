import { route, index, type RouteConfig } from "@react-router/dev/routes";

export default[
    index("./login.tsx"),
    route("dashboard", "./dashboard.tsx"),
] satisfies RouteConfig;
