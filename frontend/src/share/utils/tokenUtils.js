const tokenUtils = {
  getAccessToken: () => {
    return localStorage.getItem("access-token") || null;
  },

  setAccessToken: (token) => {
    localStorage.setItem("access-token", token);
  },

  clearAccessToken: () => {
    localStorage.removeItem("access-token");
  },
};

export default tokenUtils;
