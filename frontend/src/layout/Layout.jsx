import { useAuthStore } from "src/share/stores/authStore";
import { useLoadingStore } from "src/share/stores/loadingStore";
import Header from "./components/Header";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import { Outlet, useNavigate } from "react-router";
import { useEffect, useState } from "react";
import { authApi } from "src/share/api";
import tokenUtils from "src/share/utils/tokenUtils";
import { toast } from "react-toastify";

export default function Layout() {
  const setUser = useAuthStore((state) => state.setUser);
  const user = useAuthStore((state) => state.user);
  const setLoading = useLoadingStore((state) => state.setLoading);
  const isLoading = useLoadingStore((state) => state.isLoading);
  const [isFetchingUser, setIsFetchingUser] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    setIsFetchingUser(true);
    authApi
      .getMe()
      .then((res) => {
        setUser(res.data.data);
      })
      .catch((err) => {
        tokenUtils.clearAccessToken();
      })
      .finally(() => {
        setLoading(false);
        setIsFetchingUser(false);
      });
  }, []);

  return (
    <>
      <div>
        <Header />
        {user && <Navbar />}
        <div className="w-full pb-80">
          { !isFetchingUser && <Outlet /> }
        </div>
        <Footer />
      </div>
    </>
  );
}
