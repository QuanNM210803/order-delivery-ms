package com.odms.delivery.constant;

import lombok.Getter;

@Getter
public class Message {
    /*
    * Error messages
    * */
    public static final String ORDER_REASON_CANCEL_REQUIRED = "order.reason.cancel.required";
    public static final String ORDER_NOT_FOUND = "order.not.found";
    public static final String ORDER_ALREADY_ASSIGNED = "order.already.assigned";
    public static final String ORDER_ALREADY_CANCELLED = "order.already.cancelled";
    public static final String ORDER_STATUS_NOT_ALLOW = "order.status.not.allow";

    /*
    * Validation messages
    * */
    public static final String ORDER_CODE_NOT_BLANK = "order.code.not.blank";
    public static final String ORDER_STATUS_NOT_NULL = "order.status.not.null";

    /*
    * Other messages
    * */
    public static final String UPDATE_DELIVERY_ORDER_STATUS_SUCCESS = "order.update.delivery.status.success";

}
