import React from "react";

export default function PriceTable({ data }) {
  if (!data) return null;

  const { distanceHeaders, rows } = data;

  return (
    <div className="overflow-x-auto">
      <div className="font-bold mb-2">Bảng giá phí vận chuyển (nghìn đồng)</div>
      <table className="min-w-full border border-gray-300 text-sm text-left">
        <thead className="bg-gray-100">
          <tr>
            <th className="border border-gray-300 px-4 py-2 bg-gray-200 font-bold">
            </th>
            {distanceHeaders.map((header) => (
              <th
                key={header.id}
                className="border border-gray-300 px-4 py-2 font-bold"
              >
                {header.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.weightRange.id}>
              <th
                scope="row"
                className="border border-gray-300 px-4 py-2 bg-gray-100 font-bold"
              >
                {row.weightRange.label}
              </th>
              {distanceHeaders.map((header) => (
                <td
                  key={header.id}
                  className="border border-gray-300 px-4 py-2 text-center"
                >
                  {row.prices[header.id]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
