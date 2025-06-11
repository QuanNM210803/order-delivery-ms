import { useEffect } from "react";
import { useNavigate } from "react-router";
import { toast } from "react-toastify";
import { authApi } from "src/share/api";
import { useAuthStore } from "src/share/stores/authStore";
import { useLoadingStore } from "src/share/stores/loadingStore";

export default function Profile() {
  const setLoading = useLoadingStore((state) => state.setLoading);
  const setUser = useAuthStore((state) => state.setUser);
  const user = useAuthStore((state) => state.user);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    authApi
      .getMe()
      .then((res) => {
        setUser(res.data.data);
      })
      .catch((err) => {
        toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
        navigate("/");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <div className="p-8">
      <div className="p-4 rounded-md shadow-md">
        <div className="mb-4 text-lg font-medium text-left">Hồ sơ của tôi</div>
        {user && (
          <div className="flex flex-wrap gap-4">
            <div className="flex flex-col gap-2">
              {[
                { label: "Họ tên", value: user?.fullName },
                { label: "Email", value: user?.email },
                { label: "SĐT", value: user?.phone },
                { label: "Địa chỉ", value: user?.address },
              ].map(({ label, value }) => (
                <div
                  key={label}
                  className="flex flex-col gap-1 min-w-[300px] max-w-[450px] flex-1"
                >
                  <div className="font-medium">{label}:</div>
                  <div className="rounded-md border border-gray-500 bg-gray-100 py-1 px-2 min-h-[33.6px] break-words">
                    {value ?? ""}
                  </div>
                </div>
              ))}
            </div>

            <div className="flex flex-col gap-2 h-fit">
              {[
                { label: "Tên đăng nhập", value: user?.username },
                { label: "Vai trò", value: user?.roles?.join(", ") },
                { label: "Ngày tham gia", value: user?.createdAt },
              ].map(({ label, value }) => (
                <div
                  key={label}
                  className="flex flex-col gap-1 min-w-[300px] max-w-[450px] flex-1"
                >
                  <div className="font-medium">{label}:</div>
                  <div className="rounded-md border border-gray-500 bg-gray-100 py-1 px-2 min-h-[33.6px] break-words">
                    {value ?? ""}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
