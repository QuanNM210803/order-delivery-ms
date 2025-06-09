import { useState } from "react";

export default function OrderStatus({ statusInfo }) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="bg-red-700 z-[1] w-64 shadow-md rounded-md">
      <div
        className="text-white p-3 cursor-pointer select-none"
        onClick={() => setIsExpanded((state) => !state)}
      >
        {statusInfo.status}
      </div>
      {isExpanded && (
        <div className="bg-white rounded-b-md p-4 italic text-sm">
          <div>Người cập nhật: {statusInfo.createdBy}</div>
          <div>{statusInfo.updatedAt}</div>
          {statusInfo.reasonCancel && <div>Lí do hủy: {statusInfo.reasonCancel}</div>}
          {statusInfo.noteCancel && <div>Ghi chú khi hủy: {statusInfo.noteCancel}</div>}
        </div>
      )}
    </div>
  );
}
