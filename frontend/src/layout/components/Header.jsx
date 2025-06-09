import { Link, useLocation, useNavigate } from "react-router";
import { toast } from "react-toastify";
import { useAuthStore } from "src/share/stores/authStore";
import DeliveryIcon from "src/assets/headerdelivericon.svg";
import Dropdown from "src/share/components/Dropdown";
import { useMemo } from "react";
import tokenUtils from "src/share/utils/tokenUtils";
import { common } from "@mui/material/colors";

export default function Header() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const location = useLocation();
  const navigate = useNavigate();

  const commonOptionsList = useMemo(() => {
    const handleLogout = () => {
      logout();
      tokenUtils.clearAccessToken();
      navigate('/');
    }

    return [
      {
        text: "Trang cá nhân",
        handler:() => navigate('/my-profile'),
      },
      {
        text: "Đăng xuất",
        handler: handleLogout,
      },
    ];
  }, []);

  return (
    <header className="h-16 bg-red-600 px-8 shadow-md flex flex-row items-center justify-between w-full">
      <div
        className="font-medium cursor-pointer text-lg text-white flex flex-row gap-4 items-center"
        onClick={() => navigate("/")}
      >
        <img className="max-h-16" src={DeliveryIcon} alt="Your SVG" />
        <div>ODMS Express</div>
      </div>
      {user ? (
        <div className="text-white">
          <Dropdown
            align={"right"}
            text={user.fullName}
            optionsList={commonOptionsList}
          />
        </div>
      ) : (
        <Link
          className="px-2 py-1 text-white"
          to={`/login?redirect=${location.pathname}`}
        >
          Đăng nhập/Đăng kí
        </Link>
      )}
    </header>
  );
}
