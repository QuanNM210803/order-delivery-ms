import { useMemo } from "react";
import { useNavigate } from "react-router";
import Dropdown from "src/share/components/Dropdown";
import roles from "src/share/constants/roles";
import { useAuthStore } from "src/share/stores/authStore";

export default function Navbar() {
  const user = useAuthStore((state) => state.user);
  const navigate = useNavigate();

  const commonNavbarItems = useMemo(() => {
    return [
      {
        text: "Tra cứu",
        isDropdown: false,
        handler: () => navigate("/"),
      },
    ];
  }, []);

  const customerNavbarItems = useMemo(() => {
    return [
      {
        text: "Đơn hàng",
        isDropdown: true,
        handler: null,
        options: [
          {
            text: "Danh sách",
            handler: () => navigate("customer/my-orders"),
          },
          {
            text: "Tạo đơn hàng",
            handler: () => navigate("customer/create-order"),
          },
        ],
      },
    ];
  }, []);

  const adminNavbarItems = useMemo(() => {
    return [
      {
        text: "Quản lí",
        isDropdown: true,
        handler: null,
        options: [
          {
            text: "Đơn hàng",
            handler: () => navigate("admin/orders"),
          },
          {
            text: "Người dùng",
            handler: () => navigate("admin/users"),
          },
        ],
      },
    ];
  }, []);

  const deliveryStaffItems = useMemo(() => {
    return [
      {
        text: "Đơn hàng",
        isDropdown: true,
        handler: null,
        options: [
          {
            text: "Đang thực hiện",
            handler: () => navigate("delivery/orders/ongoing"),
          },
          {
            text: "Đã hoàn thành",
            handler: () => navigate("delivery/orders/completed"),
          },
          {
            text: "Đã hủy",
            handler: () => navigate("delivery/orders/cancelled"),
          },
        ],
      },
      {
        text: "Thống kê",
        isDropdown: false,
        handler: () => navigate("delivery/statistics"),
      },
    ];
  }, []);

  const navbarItems = useMemo(() => {
    const items = [];
    if (user?.roles[0] == roles.CUSTOMER || !user) {
      items.push(...commonNavbarItems);
    }

    if (user?.roles[0] == roles.CUSTOMER) {
      items.push(...customerNavbarItems);
    }

    if (user?.roles[0] == roles.ADMIN) {
      items.push(...adminNavbarItems);
    }

    if (user?.roles[0] == roles.DELIVERY_STAFF) {
      items.push(...deliveryStaffItems);
    }

    return items;
  }, [
    user,
    commonNavbarItems,
    customerNavbarItems,
    adminNavbarItems,
    deliveryStaffItems,
  ]);

  return (
    <div className="bg-white sticky left-0 w-full top-0 h-14 shadow-md gap-8 flex flex-row items-center px-8 z-10">
      {navbarItems.map((item, index) => (
        <div key={index}>
          {item.isDropdown ? (
            <div
              key={index}
              className="cursor-pointer duration-100 hover:text-red-600"
            >
              <Dropdown text={item.text} optionsList={item.options} />
            </div>
          ) : (
            <div
              key={index}
              className="cursor-pointer duration-100 hover:text-red-600"
            >
              <div onClick={item.handler}>{item.text}</div>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
