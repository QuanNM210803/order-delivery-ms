import { useState } from "react";
import { useForm } from "react-hook-form";
import OrderStatusPanel from "src/share/components/OrderStatusPanel";

export default function LookupPanel() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const mockedOrderStatus = {
    sender: {
      name: "Nguyễn Văn A",
      phone: "123456789",
      pickupAddress: "Nam Từ Liêm",
    },
    recipent: {
      name: "Nguyễn Văn B",
      phone: "0987654321",
      deliveryAddress: "Bắc Từ Liêm",
    },
    orderStatus: [
      {
        name: "CREATED",
        timeStamp: "16:22 - 23/10/2025",
        user: "Nguyễn Văn A",
      },
      {
        name: "ASSIGNED",
        timeStamp: "16:24 - 23/10/2025",
        user: "Nguyễn Văn A",
      },
      {
        name: "PICKED_UP",
        timeStamp: "16:50 - 23/10/2025",
        user: "Nguyễn Văn C",
      },
    ],
  };
  const [orderStatus, setOrderStatus] = useState(mockedOrderStatus);

  const onSubmit = (data) => {
    console.log(data);
  };

  return (
    <div className="shadow-md bg-white mx-64 rounded-sm px-8 py-6 mt-12 pb-24">
      <div className="mb-8 text-lg font-medium text-center">
        Tra cứu trạng thái đơn hàng
      </div>
      <div className="flex flex-col justify-around">
        <form
          className="flex flex-col gap-0.5 min-w-72 max-w-72 mx-auto"
          onSubmit={handleSubmit(onSubmit)}
        >
          <label className="text-sm">Mã đơn hàng:</label>
          <input
            type="text"
            {...register("orderCode", {
              required: "Vui lòng nhập mã đơn hàng",
            })}
            className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
          {errors.orderCode && (
            <span className="text-xs text-red-500 mt-0.5">
              {errors.orderCode.message}
            </span>
          )}
          <div className="flex flex-col gap-0.5">
            <label className="text-sm">SĐT người nhận/gửi:</label>
            <input
              type="text"
              {...register("phone_number", {
                required:
                  "Vui lòng nhập số điện thoại người nhận hoặc người gửi",
                pattern: {
                  value: /^[0-9]+$/,
                  message: "Số điện thoại không hợp lệ",
                },
              })}
              className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
            />
            {errors.phone_number && (
              <span className="text-xs text-red-500 mt-0.5">
                {errors.phone_number.message}
              </span>
            )}
          </div>
          <button
            type="submit"
            className="bg-red-700 text-white w-full py-2 mt-4 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
          >
            Tra cứu
          </button>
        </form>
        <div className="mt-8">
          {orderStatus && <OrderStatusPanel orderStatus={orderStatus} />}
        </div>
      </div>
    </div>
  );
}
