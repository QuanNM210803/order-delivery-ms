package com.odms.order.utils;

import java.util.Map;

public class WebUtils {
    public static String getCurrentPhone(){
        Map<String, Object> currentUser = nmquan.commonlib.utils.WebUtils.getCurrentUser();
        return currentUser != null ? (String) currentUser.get("phone") : null;
    }
}
