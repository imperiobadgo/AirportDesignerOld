package com.pukekogames.airportdesigner.Helper;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.RoadType;
import com.pukekogames.airportdesigner.Helper.Geometry.Line;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Street;
import com.pukekogames.airportdesigner.Objects.Roads.Taxiway;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 31.07.2016.
 */
public class BuildingHelper {

    public static boolean calcBuildPos(int x, int y, RoadIntersection startIntersection, RoadIntersection buildIntersection, CopyOnWriteArrayList<Line> lines, CopyOnWriteArrayList<PointFloat> intersectPoints) {

        if (startIntersection == null || buildIntersection == null) return false;

        boolean canBuild = true;
        boolean snappedToOtherRoad = false;
        int checkRadius = 2600;
        int minRadius = GameInstance.Settings().buildMinRadius;
        int snapRadius = 200;
        int lengthSnap = 300;
//        PointFloat pos = new PointFloat();
        float maxBuildDot = 0.65f;

        Line buildLine = new Line(startIntersection.getPosition(), buildIntersection.getPosition());
        float buildLineLength = buildLine.getDirectionLength();

        double smallestDistance = Double.MAX_VALUE;
        ArrayList<RoadIntersection> nearIntersection = new ArrayList<RoadIntersection>();
        for (int i = 0; i < GameInstance.Airport().getRoadIntersectionCount(); i++) {
            RoadIntersection intersection = GameInstance.Airport().getRoadIntersection(i);
            double distance = Math.sqrt(Math.pow(x - intersection.getPosition().x, 2) + Math.pow(y - intersection.getPosition().y, 2));
            if (distance < checkRadius) {
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nearIntersection.add(0, intersection);
                } else {
                    nearIntersection.add(intersection);
                }
            }
        }

        //collect all linedirections from the roads of the startIntersection
        lines.clear();
        intersectPoints.clear();
        if (startIntersection != null) {
            for (Road road : startIntersection.getRoadArray()) {

                float roadX = road.getDirX() * road.getLength();
                float roadY = road.getDirY() * road.getLength();
                Line newLine = new Line(road.getStartPosition(), road.getEndPosition());
                lines.add(newLine);

                PointFloat nextPositionOnLine = newLine.nearestPointOnLine(buildIntersection.getPosition(), true, false);
                if (nextPositionOnLine != null) {

                    float buildPosDistance = (float) Math.sqrt(Math.pow(buildIntersection.getPosition().x - nextPositionOnLine.x, 2) +
                            Math.pow(buildIntersection.getPosition().y - nextPositionOnLine.y, 2));

                    if (buildPosDistance < buildLineLength) {
                        //dot product needs normalized vectors
                        float dot = (roadX / road.getLength()) * (buildLine.getDirection().x / buildLineLength) + (roadY / road.getLength()) * (buildLine.getDirection().y / buildLineLength);
                        //if the build angle is to steep
                        if (Math.abs(dot) > maxBuildDot) {
                            canBuild = false;
                            intersectPoints.add(new PointFloat(road.getCenterPosition().x, road.getCenterPosition().y));
                        }
                    }

                }


                //add line with 90 degree difference
                float perpendicularHeading = (road.getHeading() + 90) % 360;
                roadX = (float) Math.cos(Math.toRadians(perpendicularHeading));
                roadY = (float) Math.sin(Math.toRadians(perpendicularHeading));
                newLine = new Line(startIntersection.getPosition().x, startIntersection.getPosition().y, roadX, roadY);
                lines.add(newLine);
            }

            RoadType roadType = GameInstance.Settings().buildRoad;
            if (roadType == RoadType.street || roadType == RoadType.taxiway) {
                //add line foreach other road
                for (int i = 0; i < GameInstance.Airport().getRoadCount(); i++) {
                    Road road = GameInstance.Airport().getRoad(i);

                    if ((road instanceof Taxiway && roadType == RoadType.street) || (road instanceof Street && roadType == RoadType.taxiway)) {

                        Line newLine = new Line(road.getStartPosition(), road.getEndPosition());
                        newLine.setHasEnding(true);
                        lines.add(newLine);
                    }
                }
            }
        }

        //snap buildingposition to the nearest line
        if (lines.size() > 0) {
            PointFloat xy = new PointFloat(x, y);
            Line nextLine = null;
            double smallestDistanceToLine = Double.MAX_VALUE;
            for (Line line : lines) {
                double distance = line.pointToLineDistance(xy);
                if (distance < smallestDistanceToLine) {
                    nextLine = line;
                    smallestDistanceToLine = distance;
                }
            }
            if (nextLine != null && smallestDistanceToLine < GameInstance.Settings().snapToLineDistance) {
                PointFloat pointOnLine = nextLine.nearestPointOnLine(new PointFloat(x, y), false, nextLine.hasEnding());
                if (pointOnLine != null) {
                    x = (int) pointOnLine.x;
                    y = (int) pointOnLine.y;
                    if (nextLine.hasEnding()) {
                        snappedToOtherRoad = true;
                    }
                }
            }
        }

        boolean snappedToNearestIntersection = false;

        if (nearIntersection.size() == 0) {
            //snap to closest point
            float buildX, buildY;
            if (startIntersection != null) {

                float dirStartX = x - startIntersection.getPosition().x;
                float dirStartY = y - startIntersection.getPosition().y;
                double lengthToStart = Math.sqrt(Math.pow(dirStartX, 2) + Math.pow(dirStartY, 2));
                if (lengthToStart % 1000 < lengthSnap && lengthToStart > 1000 + lengthSnap) {
                    dirStartX /= (float) lengthToStart;
                    dirStartY /= (float) lengthToStart;
                    int snapLength = (int) Math.round(lengthToStart / 1000);
//                    System.out.println("lengthToStart " + lengthToStart + " :  snaplength " + snapLength + " lengthToStart % 1000 " + lengthToStart % 1000);
                    buildX = startIntersection.getPosition().x + dirStartX * snapLength * 1000;
                    buildY = startIntersection.getPosition().y + dirStartY * snapLength * 1000;
                } else {
                    buildX = x;
                    buildY = y;
                }
            } else {
                buildX = x;
                buildY = y;
            }
            buildIntersection.setTablePosition(buildX, buildY);

        } else {

            PointFloat nearestPos = nearIntersection.get(0).getPosition();
            float dirX = x - nearestPos.x;
            float dirY = y - nearestPos.y;
            double length = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
            if (length > minRadius) {
                //nothing to snap to
//                if (startIntersection != null) {
//
//                    float dirStartX = (float) (startIntersection.getPosition().x - x);
//                    float dirStartY = (float) (startIntersection.getPosition().y - y);
//                    double lengthToStart = Math.sqrt(Math.pow(dirStartX, 2) + Math.pow(dirStartY, 2));
//                    if (lengthToStart % 1000 < lengthSnap && length > minRadius + lengthSnap) {
//                        dirStartX /= (float) lengthToStart;
//                        dirStartY /= (float) lengthToStart;
//                        int snapLength = (int) Math.round(lengthToStart / 1000);
////                System.out.println("lengthToStart " + lengthToStart + " :  snaplength " + snapLength + " lengthToStart % 1000 " + lengthToStart % 1000);
//                        pos.x = startIntersection.getPosition().x + dirStartX * snapLength;
//                        pos.y = startIntersection.getPosition().y + dirStartY * snapLength;
//                    }else{
//                        pos.set(x, y);
//                    }
//                }else{
//                }


                buildIntersection.setTablePosition(x, y);
            } else if (length < snapRadius) {

                //snap to next point
                buildIntersection.setTablePosition(nearestPos.x, nearestPos.y);
                snappedToNearestIntersection = true;

            } else {
                //create difference to startpoint(shorter than min build length)
                dirX /= (float) length;
                dirY /= (float) length;
//                pos.set(nearestPos.x + dirX * minRadius, nearestPos.y + dirY * minRadius);
                buildIntersection.setTablePosition(nearestPos.x + dirX * minRadius, nearestPos.y + dirY * minRadius);
            }
        }
        if (startIntersection == null) {
            return true;
        }


        Line roadLine = null;

        if (snappedToNearestIntersection) {
            RoadIntersection nearestIntersection = nearIntersection.get(0);
            if (nearestIntersection != null) {
                for (Road road : nearestIntersection.getRoadArray()) {

                    float roadX = road.getDirX() * road.getLength();
                    float roadY = road.getDirY() * road.getLength();
                    roadLine = new Line(road.getStartPosition(), road.getEndPosition());

                    PointFloat nextPositionOnLine = roadLine.nearestPointOnLine(startIntersection.getPosition(), true, false);
                    if (nextPositionOnLine != null) {

                        float buildPosDistance = (float) Math.sqrt(Math.pow(startIntersection.getPosition().x - nextPositionOnLine.x, 2) +
                                Math.pow(startIntersection.getPosition().y - nextPositionOnLine.y, 2));

                        if (buildPosDistance < buildLineLength) {
                            //dot product needs normalized vectors
                            float dot = (roadX / road.getLength()) * (buildLine.getDirection().x / buildLineLength) + (roadY / road.getLength()) * (buildLine.getDirection().y / buildLineLength);
                            //if the build angle is to steep
                            if (Math.abs(dot) > maxBuildDot) {
                                canBuild = false;
                                intersectPoints.add(new PointFloat(road.getCenterPosition().x, road.getCenterPosition().y));
                            }
                        }

                    }


                }

            }
        }


        //check for roadIntersections
        for (int i = 0; i < GameInstance.Airport().getRoadCount(); i++) {
            Road road = GameInstance.Airport().getRoad(i);
            roadLine = new Line(road.getStartPosition(), road.getEndPosition());
            PointFloat intersection = buildLine.getIntersectPoint(roadLine, snappedToOtherRoad);
            if (intersection != null) {
                intersectPoints.add(intersection);
                canBuild = false;
            }

        }

        //check for close roadIntersections
        for (int i = 0; i < GameInstance.Airport().getRoadIntersectionCount(); i++) {
            RoadIntersection roadIntersection = GameInstance.Airport().getRoadIntersection(i);
            double buildIntersectionDistance = CommonMethods.getDistance(roadIntersection.getPosition(), buildIntersection.getPosition());
            if (roadIntersection == startIntersection || buildIntersectionDistance < 0.1) {
                continue;
            }
            float parameter = buildLine.nearestPointOnLineParameter(roadIntersection.getPosition(), false);
            if (parameter < 0 || parameter > 1) {
                //roadIntersection outside roadLineSegment
                continue;
            }

            double distance = buildLine.pointToLineDistance(roadIntersection.getPosition());
            if (distance < minRadius / 1.5) {
                intersectPoints.add(new PointFloat(roadIntersection.getPosition().x, roadIntersection.getPosition().y));
                canBuild = false;
            }

        }

        return canBuild;
    }
}
