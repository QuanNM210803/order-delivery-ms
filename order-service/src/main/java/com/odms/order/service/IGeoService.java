package com.odms.order.service;

import org.json.JSONObject;

public interface IGeoService {
    Double getDistance(String location1, String location2);
    JSONObject getCoordinates(String location);
}
