import { Link, useLocation, useNavigate } from "react-router";
import { toast } from "react-toastify";
import { useAuthStore } from "src/share/stores/authStore";
import DeliveryIcon from "src/assets/headerdelivericon.svg";
import Dropdown from "src/share/components/Dropdown";
import { useMemo } from "react";

export default function Header() {
  const user = useAuthStore((state) => state.user);
  const location = useLocation();
  const navigate = useNavigate();

  const optionsList = useMemo(() => {
    return [
      {
        text: "Đơn hàng của tôi",
        handler: () => navigate("my-orders"),
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
        <div>Odms</div>
      </div>
      {user ? (
        <div className="text-white">
          <Dropdown
            align={"right"}
            text={user.fullName}
            optionsList={optionsList}
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
