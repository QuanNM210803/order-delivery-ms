import { useAuthStore } from "src/share/stores/authStore";
import { useLoadingStore } from "src/share/stores/loadingStore";
import Header from "./components/Header";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import { Outlet } from "react-router";

export default function Layout() {
  const setUser = useAuthStore((state) => state.setUser);
  const user = useAuthStore((state) => state.user);
  const setLoading = useLoadingStore((state) => state.setLoading);

  // useEffect(() => {
  //   setLoading(true);
  //   authApi
  //     .getMe()
  //     .then((res) => {
  //       setUser(res.data.data);
  //     })
  //     .catch((err) => {
  //       // void
  //     })
  //     .finally(() => {
  //       setLoading(false);
  //     });
  // }, []);

  return (
    <>
      <div>
        <Header />
        <Navbar />
        <div className="w-full pb-80">
          <Outlet />
        </div>
        <Footer />
      </div>
    </>
  );
}
