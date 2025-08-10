package com.odms.order.constant;

public class Message {

    /*
    * Error messages
    * */
    public static final String ORDER_NOT_FOUND = "order.not.found";

    /*
    * Validation messages
    * */
    public static final String ORDER_RECEIVER_NAME_REQUIRE = "order.receiver.name.require";
    public static final String ORDER_RECEIVER_PHONE_REQUIRE = "order.receiver.phone.require";
    public static final String ORDER_DELIVERY_ADDRESS_REQUIRE = "order.delivery.address.require";
    public static final String ORDER_PICKUP_ADDRESS_REQUIRE = "order.pickup.address.require";
    public static final String ORDER_DESCRIPTION_REQUIRE = "order.description.require";
    public static final String ORDER_WEIGHT_REQUIRE = "order.weight.require";
    public static final String ORDER_PRICE_REQUIRE = "order.price.require";
    public static final String ESTIMATE_PICKUP_ADDRESS_REQUIRE = "estimate.pickup.address.require";
    public static final String ESTIMATE_DELIVERY_ADDRESS_REQUIRE = "estimate.delivery.address.require";
    public static final String ESTIMATE_WEIGHT_REQUIRE = "estimate.weight.require";
    public static final String FILTER_START_DATE_PAST_OR_PRESENT = "filter.start.date.past.or.present";
    /*
    * Other messages
    * */
    public static final String ORDER_CREATED = "order.created";
}
