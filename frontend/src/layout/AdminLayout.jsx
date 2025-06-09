import { Outlet } from "react-router";
import { useAuthStore } from "src/share/stores/authStore";
import roles from "src/share/constants/roles";
import NoPermission from "src/noPermission/NoPermission";

export default function AdminLayout() {
  const user = useAuthStore((state) => state.user);

  if (!user?.roles.includes(roles.ADMIN)) {
    return <NoPermission />;
  }
  return <Outlet />;
}
