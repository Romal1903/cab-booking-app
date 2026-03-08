package com.example.cabbookingapplication.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RideFareService {

    private static final double BASE_FARE = 40;
    private static final double PER_KM_RATE = 12;

    private final RestTemplate restTemplate = new RestTemplate();

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        try {

            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + lon1 + "," + lat1 + ";"
                    + lon2 + "," + lat2
                    + "?overview=false";

            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null) return haversine(lat1, lon1, lat2, lon2);

            var routes = (java.util.List<Map>) response.get("routes");

            if (routes == null || routes.isEmpty()) {
                return haversine(lat1, lon1, lat2, lon2);
            }

            Map route = routes.get(0);

            double distanceMeters = ((Number) route.get("distance")).doubleValue();

            double distanceKm = distanceMeters / 1000.0;

            return Math.round(distanceKm * 100.0) / 100.0;

        } catch (Exception e) {

            return haversine(lat1, lon1, lat2, lon2);

        }
    }

    public int calculateETA(double lat1, double lon1, double lat2, double lon2) {

        try {

            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + lon1 + "," + lat1 + ";"
                    + lon2 + "," + lat2
                    + "?overview=false";

            Map response = restTemplate.getForObject(url, Map.class);

            var routes = (java.util.List<Map>) response.get("routes");

            Map route = routes.get(0);

            double durationSeconds = ((Number) route.get("duration")).doubleValue();

            return (int) Math.round(durationSeconds / 60);

        } catch (Exception e) {

            return 10;

        }
    }

    public double calculateFare(double distanceKm) {

        return Math.round((BASE_FARE + distanceKm * PER_KM_RATE) * 100.0) / 100.0;

    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {

        double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(R * c * 100.0) / 100.0;

    }
}
