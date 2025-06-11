import { Route, Routes } from "react-router";
import "./App.css";
import Layout from "./layout/Layout";
import { ToastContainer } from "react-toastify";
import NotFound from "./notFound/NotFound";
import Login from "./login/Login";
import LoadingOverlay from "./share/components/LoadingOverlay";
import Register from "./register/Register";
import VerifyAccount from "./register/VerifyAccount";
import Home from "./guest/views/Home";
import OrderList from "./customer/views/OrderList";
import OrderStat from "./customer/views/OrderStat";
import CustomerLayout from "./layout/CustomerLayout";
import AdminLayout from "./layout/AdminLayout";
import AdminOrderList from "./admin/views/AdminOrderList";
import CreateOrder from "./customer/views/CreateOrder";
import OrderDetail from "./customer/views/OrderDetail";
import AdminOrderDetail from "./admin/views/AdminOrderDetail";
import DeliveryStaffLayout from "./layout/DeliveryStaffLayout";
import DeliveryStaffOngoingOrders from "./deliveryStaff/views/DeliveryStaffOngoingOrders";
import StatusToggler from "./deliveryStaff/components/StatusToggler";
import DeliveryStaffCompletedOrder from "./deliveryStaff/views/DeliveryStaffCompletedOrder";
import DeliveryStaffCancelledOrder from "./deliveryStaff/views/DeliveryStaffCancelledOrder";
import DeliveryStaffOrderDetail from "./deliveryStaff/views/DeliveryStaffOrderDetail";
import DeliveryStaffStat from "./deliveryStaff/views/DeliveryStaffStat";
import Profile from "./profile/Profile";
import AdminUserList from "./admin/views/AdminUserList";

function App() {
  return (
    <>
      <Routes>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/register" element={<Register />}></Route>
        <Route path="/auth/verify-email/:token" element={<VerifyAccount />} />
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/my-profile" element={<Profile />} />
          <Route element={<CustomerLayout />}>
            <Route path="customer/my-orders" element={<OrderList />} />
            <Route path="customer/orders-stat" element={<OrderStat />} />
            <Route path="customer/create-order" element={<CreateOrder />} />
            <Route
              path="customer/order/detail/:orderCode"
              element={<OrderDetail />}
            />
          </Route>
          <Route element={<AdminLayout />}>
            <Route path="admin/orders" element={<AdminOrderList />} />
            <Route path="admin/users" element={<AdminUserList />} />
            <Route
              path="admin/order/detail/:orderCode"
              element={<AdminOrderDetail />}
            />
          </Route>
          <Route element={<DeliveryStaffLayout />}>
            <Route
              path="delivery/orders/ongoing"
              element={<DeliveryStaffOngoingOrders />}
            />
            <Route
              path="delivery/orders/completed"
              element={<DeliveryStaffCompletedOrder />}
            />
            <Route
              path="delivery/orders/cancelled"
              element={<DeliveryStaffCancelledOrder />}
            />
            <Route
              path="delivery/order/detail/:orderCode"
              element={<DeliveryStaffOrderDetail />}
            />
            <Route path="delivery/statistics" element={<DeliveryStaffStat />} />
          </Route>
        </Route>
        <Route path="*" element={<NotFound />}></Route>
      </Routes>
      <ToastContainer
        position="bottom-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        pauseOnHover
        theme="light"
        limit={5}
      />
      <LoadingOverlay />
      <StatusToggler />
    </>
  );
}

export default App;
