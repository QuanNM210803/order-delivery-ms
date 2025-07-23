package com.odms.order.service.impl;

import com.odms.order.service.IGeoService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoServiceImpl implements IGeoService {

    @Autowired
    @Qualifier("external")
    private RestTemplate restTemplate;

    @Value("${api-key.open_route_service}")
    private String OPEN_ROUTE_SERVICE_API_KEY;

    @Override
    public Double getDistance(String location1, String location2) {
        try {
            JSONObject coordinates1 = getCoordinates(location1);
            double lon1 = coordinates1.getDouble("lon");
            double lat1 = coordinates1.getDouble("lat");

            JSONObject coordinates2 = getCoordinates(location2);
            double lon2 = coordinates2.getDouble("lon");
            double lat2 = coordinates2.getDouble("lat");

            String routeUrl = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + OPEN_ROUTE_SERVICE_API_KEY + "&start=" + lon1 + "," + lat1 + "&end=" + lon2 + "," + lat2;
            JSONObject routeResponse = new JSONObject(restTemplate.getForObject(routeUrl, String.class));
            JSONObject routes = routeResponse
                    .getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("properties")
                    .getJSONObject("summary");

            return routes.has("distance") ? routes.getDouble("distance") : 0.1;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONObject getCoordinates(String location) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" + location + ", Ha Noi, Viet Nam&format=json&limit=1";
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray response = new JSONArray(jsonResponse);
            return response.getJSONObject(0);
        } catch (JSONException e) {
            String[] parts = location.split(",", 2);
            if (parts.length >= 2) {
                location = parts[1].trim();
            }
            String url = "https://nominatim.openstreetmap.org/search?q=" + location + ", Ha Noi, Viet Nam&format=json&limit=1";
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray response = new JSONArray(jsonResponse);
            return response.getJSONObject(0);
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
