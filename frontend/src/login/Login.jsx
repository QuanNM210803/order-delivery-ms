import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router";
import { toast } from "react-toastify";
import Footer from "src/layout/components/Footer";
import { authApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";
import RiderImage from "src/assets/rider.jpg";
import tokenUtils from "src/share/utils/tokenUtils";

export default function Login() {
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm();

  const setLoading = useLoadingStore((state) => state.setLoading);
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await authApi.login(data);
      tokenUtils.setAccessToken(response.data.data.token);
      navigate("/");
    } catch (err) {
      if (err?.response?.status == 401) {
        setError("username", {
          type: "custom",
          message: "Sai thông tin đăng nhập",
        });
      } else {
        toast.error(err?.response?.data.message || "Có lỗi xảy ra");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full flex-row flex h-[100vh]">
      <div className=" flex flex-1 justify-center items-center w-[40%]">
        <div className="bg-white pb-6 h-fit pl-3 max-w-[360px] w-[360px] min-w-[360px]">
          <div className="font-bold text-5xl text-red-800 italic text-center mb-4">
            ODMS Express
          </div>
          <div className="text-left uppercase w-full font-bold text-lg py-4 text-red-700">
            Đăng nhập
          </div>
          <form className="" onSubmit={handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-0.5">
              <label className="text-sm">Tên đăng nhập</label>
              <input
                type="text"
                {...register("username", {
                  required: "Vui lòng nhập tên đăng nhập",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.username && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.username.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">Mật khẩu</label>
              <input
                type="password"
                {...register("password", {
                  required: "Vui lòng nhập mật khẩu",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.password && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.password.message}
                </span>
              )}
            </div>
            <button
              type="submit"
              className="bg-red-700 text-white w-full py-2 mt-4 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
            >
              Đăng nhập
            </button>
          </form>
          <div className="text-right text-sm mt-2">
            <span>Chưa có tài khoản? </span>
            <Link to="/register" className="text-red-700 italic underline">
              Đăng kí ngay
            </Link>
          </div>
        </div>
      </div>
      <div className="max-w-[60%] flex items-center p-8 object-contain">
        <img className="h-full max-w-full rounded-lg" src={RiderImage} />
      </div>
    </div>
  );
}
