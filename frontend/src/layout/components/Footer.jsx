export default function Footer() {
  return (
    <footer className="bg-gray-900 text-white pt-10 pb-6 px-4 w-full">
      <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <h2 className="text-xl font-semibold mb-2">ODMS Express</h2>
          <p className="text-sm text-gray-400">
            Dịch vụ giao hàng nhanh chóng, an toàn và đáng tin cậy. Phục vụ 24/7 trong địa bàn nội thành Hà Nội.
          </p>
        </div>

        <div>
          <h3 className="text-lg font-semibold mb-2">Liên kết</h3>
          <ul className="space-y-1 text-sm text-gray-300">
            <li>
              <a href="/" className="hover:text-white">
                Trang chủ
              </a>
            </li>
            <li>
              <a href="/about" className="hover:text-white">
                Về chúng tôi
              </a>
            </li>
            <li>
              <a href="/services" className="hover:text-white">
                Dịch vụ
              </a>
            </li>
            <li>
              <a href="/pricing" className="hover:text-white">
                Bảng giá
              </a>
            </li>
            <li>
              <a href="/contact" className="hover:text-white">
                Liên hệ
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h3 className="text-lg font-semibold mb-2">Hỗ trợ khách hàng</h3>
          <ul className="space-y-1 text-sm text-gray-300">
            <li>
              <a href="/faq" className="hover:text-white">
                Câu hỏi thường gặp
              </a>
            </li>
            <li>
              <a href="/policy" className="hover:text-white">
                Chính sách bảo mật
              </a>
            </li>
            <li>
              <a href="/terms" className="hover:text-white">
                Điều khoản dịch vụ
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h3 className="text-lg font-semibold mb-2">Liên hệ</h3>
          <p className="text-sm text-gray-300">
            Hotline:{" "}
            <a href="tel:19001234" className="hover:text-white">
              1900 1234Odms
            </a>
          </p>
          <p className="text-sm text-gray-300">
            Email:{" "}
            <a
              href="mailto:support@odms.vn"
              className="hover:text-white"
            >
              support@odms.vn
            </a>
          </p>
          <p className="text-sm text-gray-300">
            Địa chỉ: Nam Từ Liêm, Hà Nội, Việt Nam
          </p>
        </div>
      </div>

      <div className="border-t border-gray-700 mt-8 pt-4 text-center text-sm text-gray-500">
        © {new Date().getFullYear()} ODMS Express. All rights reserved.
      </div>
    </footer>
  );
}
