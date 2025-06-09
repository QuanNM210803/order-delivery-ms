import { Outlet } from "react-router";
import { useAuthStore } from "src/share/stores/authStore";
import roles from "src/share/constants/roles";
import NoPermission from "src/noPermission/NoPermission";
import { useLoadingStore } from "src/share/stores/loadingStore";
import StatusToggler from "src/deliveryStaff/components/StatusToggler";

export default function DeliveryStaffLayout() {
  const user = useAuthStore((state) => state.user);
  const isLoading = useLoadingStore((state) => state.isLoading);

  if (!user?.roles.includes(roles.DELIVERY_STAFF)) {
    return <NoPermission />;
  }
  return (
    <>
      <Outlet />
      <StatusToggler />
    </>
  );

}
