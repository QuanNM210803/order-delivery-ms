import { useEffect } from "react";
import { useNavigate } from "react-router";

export default function NotFound() {
  const navigate = useNavigate();

  useEffect(() => {
    const redirectTimeout = setTimeout(() => navigate("/"), 3000);

    return () => {
      clearTimeout(redirectTimeout);
    };
  }, []);

  return (
    <>
      <svg viewBox="0 0 300 200" xmlns="http://www.w3.org/2000/svg">
        <text
          x="50%"
          y="60"
          text-anchor="middle"
          font-size="36"
          font-family="Comic Sans MS, sans-serif"
          fill="#555"
          stroke="#333"
          stroke-width="1"
        >
          404
        </text>
        <text
          x="50%"
          y="90"
          text-anchor="middle"
          font-size="14"
          font-family="Comic Sans MS, sans-serif"
          fill="#666"
        >
          Page Not Found
        </text>

        <g fill="#ccc">
          <circle cx="260" cy="30" r="6" />
          <circle cx="240" cy="45" r="3" />
          <circle cx="30" cy="150" r="5" />
          <circle cx="45" cy="40" r="2.5" />
        </g>
      </svg>
    </>
  );
}
