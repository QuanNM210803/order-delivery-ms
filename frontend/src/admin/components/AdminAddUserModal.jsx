import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { authApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";

const roles = {
  ADMIN: "ADMIN",
  DELIVERY_STAFF: "DELIVERY_STAFF",
  CUSTOMER: "CUSTOMER",
};

export default function AdminAddUserModal({ onCancel, onOK }) {
  const setLoading = useLoadingStore((state) => state.setLoading);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm();

  const password = watch("password", "");

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await authApi.registerSystemUser(data);
      toast.success(response.data.message);
      onOK();
    } catch (err) {
      toast.error(
        err?.response?.data.message || "Có lỗi xảy ra khi thêm người dùng",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      onClick={onCancel}
      className="bg-black/40 z-[99] fixed flex justify-center items-center w-full h-full top-0 left-0"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-[700px] max-h-[90vh] overflow-auto flex flex-col rounded-sm"
      >
        <div className="border-t-4 text-lg font-medium border-green-700 text-green-700 bg-gray-100 px-4 py-2">
          Thêm người dùng
        </div>

        <form
          onSubmit={handleSubmit(onSubmit)}
          className="px-6 py-4 flex flex-col gap-4"
        >
          <div className="flex gap-6">
            <div className="flex flex-col gap-2 w-1/2">
              <label className="text-sm font-semibold">
                Tên đăng nhập <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                {...register("username", {
                  required: "Vui lòng nhập tên đăng nhập",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.username && (
                <span className="text-xs text-red-500">
                  {errors.username.message}
                </span>
              )}

              <label className="text-sm font-semibold">
                Email <span className="text-red-500">*</span>
              </label>
              <input
                type="email"
                {...register("email", {
                  required: "Vui lòng nhập email",
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: "Email không hợp lệ",
                  },
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.email && (
                <span className="text-xs text-red-500">
                  {errors.email.message}
                </span>
              )}

              <label className="text-sm font-semibold">
                Mật khẩu <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                {...register("password", {
                  required: "Vui lòng nhập mật khẩu",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.password && (
                <span className="text-xs text-red-500">
                  {errors.password.message}
                </span>
              )}

              <label className="text-sm font-semibold">
                Xác nhận mật khẩu <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                {...register("confirmPassword", {
                  required: "Vui lòng xác nhận mật khẩu",
                  validate: (value) =>
                    value === password || "Mật khẩu không khớp",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.confirmPassword && (
                <span className="text-xs text-red-500">
                  {errors.confirmPassword.message}
                </span>
              )}

              <label className="text-sm font-semibold">
                Vai trò <span className="text-red-500">*</span>
              </label>
              <select
                {...register("roleName", { required: "Vui lòng chọn vai trò" })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
                defaultValue=""
              >
                <option value="" disabled>
                  Chọn vai trò
                </option>
                {Object.entries(roles).slice(0, 2).map(([key, value]) => (
                  <option key={key} value={value}>
                    {value}
                  </option>
                ))}
              </select>
              {errors.roleName && (
                <span className="text-xs text-red-500">
                  {errors.roleName.message}
                </span>
              )}
            </div>

            <div className="flex flex-col gap-2 w-1/2">
              <label className="text-sm font-semibold">
                Họ và tên <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                {...register("fullName", {
                  required: "Vui lòng nhập tên của bạn",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.fullName && (
                <span className="text-xs text-red-500">
                  {errors.fullName.message}
                </span>
              )}

              <label className="text-sm font-semibold">
                Số điện thoại <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                {...register("phone", {
                  required: "Vui lòng nhập số điện thoại",
                  pattern: {
                    value: /^[0-9]+$/,
                    message: "Số điện thoại không hợp lệ",
                  },
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
              {errors.phone && (
                <span className="text-xs text-red-500">
                  {errors.phone.message}
                </span>
              )}

              <label className="text-sm font-semibold">Địa chỉ</label>
              <input
                type="text"
                {...register("address")}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-700 focus:border-red-700 duration-200"
              />
            </div>
          </div>

          <div className="flex justify-end gap-2 mt-4">
            <button
              type="submit"
              className="bg-green-700 text-white px-6 py-2 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
            >
              Thêm
            </button>
            <button
              onClick={onCancel}
              type="button"
              className="bg-white text-red-500 px-6 py-2 rounded-sm border hover:bg-red-50 duration-100 cursor-pointer"
            >
              Đóng
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
