import { Outlet, useNavigate } from "react-router";
import Header from "./components/Header";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import { useAuthStore } from "src/share/stores/authStore";
import roles from "src/share/constants/roles";
import NoPermission from "src/noPermission/NoPermission";

export default function CustomerLayout() {
  const user = useAuthStore((state) => state.user);

  if (!user?.roles.includes(roles.CUSTOMER)) {
    return <NoPermission />;
  }
  return <Outlet />;
}
