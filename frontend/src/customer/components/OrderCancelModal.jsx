export default function OrderCancelModal({ onCancel, onOK }) {
  const handleCancel = () => {
    onOK();
  }

  return <div onClick={onCancel} className="bg-black/40 z-[99]  fixed flex justify-center items-center w-full h-full top-0 left-0">
    <div onClick={(e) => e.stopPropagation()} className="bg-white w-[450px] h-[180px] flex flex-col rounded-sm overflow-clip">
      <div className="border-t-4 text-lg font-medium border-red-500 text-red-500 bg-gray-100 px-4 py-2">
        Hủy đơn hàng
      </div>
      <div className="px-4 flex-1 py-2">Bạn có chắc chắn muốn hủy đơn hàng?</div>
      <div className="px-4 py-2 flex flex-row justify-end gap-2 bg-gray-100">
        <button onClick={handleCancel} className="bg-red-500 text-white px-4 py-2 rounded-sm cursor-pointer hover:brightness-95 duration-100">Hủy đơn hàng</button>
        <button onClick={onCancel} className="bg-white text-red-500 px-4 py-2 rounded-sm cursor-pointer border">Đóng</button>
      </div>
    </div> 
  </div>   
}
