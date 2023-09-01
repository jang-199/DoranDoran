package com.dorandoran.doranserver.global.util.distance;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class DistanceUtil {

    // km 기준
    public Integer getDistance(String[] splitLocation, Point targetPoint) {
        GeometryFactory geometryFactory = new GeometryFactory();
        String latitude = splitLocation[0];
        String longitude = splitLocation[1];
        Coordinate coordinate = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
        Point point = geometryFactory.createPoint(coordinate);

        return  (int) Math.round(point.distance(targetPoint) * 100);
    }

    //10진수를 radian(라디안)으로 변환
    private static double deg2rad(double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private static double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }
}
