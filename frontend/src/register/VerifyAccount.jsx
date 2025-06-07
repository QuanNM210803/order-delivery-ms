import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router";
import Footer from "src/layout/components/Footer";
import Header from "src/layout/components/Header";
import { authApi } from "src/share/api";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import ErrorIcon from "@mui/icons-material/Error";

export default function VerifyAccount() {
  const { token } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    let timeoutId;

    authApi
      .verifyAccount(token)
      .then(() => {
        setSuccess(true);
        timeoutId = setTimeout(() => navigate("/login"), 3000);
      })
      .catch(() => {
        setSuccess(false);
      })
      .finally(() => {
        setLoading(false);
      });

    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  }, [token]);

  return (
    <div>
      <Header />
      <div className="min-h-[90vh] flex items-center justify-center">
        <div className="bg-white p-12 shadow-md rounded-sm">
          {loading ? (
            <div className="text-lg animate-pulse text-gray-600">
              Đang xác minh tài khoản...
            </div>
          ) : success ? (
            <div className="text-center space-y-4">
              <CheckCircleIcon
                className="text-green-500"
                sx={{ fontSize: 64 }}
              />
              <h2 className="text-xl font-semibold text-green-600">
                Xác minh tài khoản thành công!
              </h2>
              <p>Cảm ơn bạn đã chọn Odms. Đang chuyển hướng...</p>
              <Link
                to="/login"
                className="inline-block px-4 py-2 bg-red-700 text-white rounded hover:brightness-95 cursor-pointer duration-100"
              >
                Đăng nhập ngay
              </Link>
            </div>
          ) : (
            <div className="text-center space-y-4">
              <ErrorIcon className="text-red-500" sx={{ fontSize: 64 }} />
              <h2 className="text-xl font-semibold text-red-600">
                Xác minh thất bại
              </h2>
              <p>Liên kết xác minh không hợp lệ hoặc đã hết hạn.</p>
            </div>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
}
