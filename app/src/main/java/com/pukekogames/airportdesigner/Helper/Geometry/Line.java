package com.pukekogames.airportdesigner.Helper.Geometry;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 31.07.2016.
 */
public class Line implements Serializable {
    private static final long serialVersionUID = -8557032934777243750L;
    private PointFloat startPoint;
    private PointFloat direction;
    private boolean hasEnding = false;

    public Line(float x, float y, float dx, float dy) {
        startPoint = new PointFloat(x, y);
        direction = new PointFloat(dx, dy);
    }

    public Line(PointFloat startPoint, PointFloat endPoint) {
        this.startPoint = new PointFloat(startPoint.x, startPoint.y);
        direction = new PointFloat(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }


    public PointFloat getStartPoint() {
        return startPoint;
    }

    public PointFloat getDirection() {
        return direction;
    }

    public PointFloat nearestPointOnLine(PointFloat point, boolean clampToSegment, boolean onlyLineSegment) {
        float apx = point.x - startPoint.x;
        float apy = point.y - startPoint.y;
        float abx = direction.x;
        float aby = direction.y;

        float ab2 = abx * abx + aby * aby;
        float ap_ab = apx * abx + apy * aby;
        float t = ap_ab / ab2;

        if (onlyLineSegment) {
            if (t < 0 || t > 1) {
                return null;
            }
        }

        //clamp to startpoint
        if (clampToSegment) {
            if (t < 0) {
                t = 0;
            } else if (t > 1) {
                t = 1;
            }
        }

        return new PointFloat(startPoint.x + abx * t, startPoint.y + aby * t);
    }

    public float nearestPointOnLineParameter(PointFloat point, boolean clampToSegment) {
        float apx = point.x - startPoint.x;
        float apy = point.y - startPoint.y;
        float abx = direction.x;
        float aby = direction.y;

        float ab2 = abx * abx + aby * aby;
        float ap_ab = apx * abx + apy * aby;
        float t = ap_ab / ab2;

        //clamp to startpoint
        if (clampToSegment) {
            if (t < 0) {
                t = 0;
            } else if (t > 1) {
                t = 1;
            }
        }
        return t;
    }

    public double pointToLineDistance(PointFloat P) {
        PointFloat A = startPoint;
        PointFloat B = new PointFloat(startPoint.x + direction.x, startPoint.y + direction.y);

        double normalLength = Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
        return Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)) / normalLength;
    }

    public PointFloat getIntersectPoint(Line otherLine, boolean addSegmentOffsetToEnd) {
        float x1 = startPoint.x;
        float y1 = startPoint.y;
        float x3 = otherLine.getStartPoint().x;
        float y3 = otherLine.getStartPoint().y;
        PointFloat dir = direction;
        PointFloat oDir = otherLine.direction;

        float directionLength = (dir.x * dir.x) + (dir.y * dir.y);
        float offset = 0;

        if (addSegmentOffsetToEnd){

            offset = - 0.05f;//
        }else{
            offset = + 0.5f;//add seperation to next line
        }

        double denom = (oDir.y) * (dir.x) - (oDir.x) * (dir.y);//crossproduct
        if (denom == 0.0) { // Lines are parallel.

            double distance = pointToLineDistance(otherLine.getStartPoint());
            if (distance > 0) {
                return null;
            } else {
                //lines are equal -> check, whether segments are overlapping
                PointFloat otherEndPoint = new PointFloat(x3 + oDir.x, y3 + oDir.y);
                float nearestStartPoint = nearestPointOnLineParameter(otherLine.getStartPoint(), false);
                float nearestEndPoint = nearestPointOnLineParameter(otherEndPoint, false);

                if ((nearestStartPoint >= 1 && nearestEndPoint >= 1) || (nearestStartPoint <= 0 && nearestEndPoint <= 0)) {
                    //segments do not overlap
                    return null;
                }else {
                    return otherEndPoint;
                }
            }
        }


        double ua = ((oDir.x) * (y1 - y3) - (oDir.y) * (x1 - x3)) / denom;
        double ub = ((dir.x) * (y1 - y3) - (dir.y) * (x1 - x3)) / denom;

        if (ua > 0.05f && ua < 1.0f + offset && ub > 0.0f && ub < 1.0f) {
            // Get the intersection point.
            return new PointFloat((float) (x1 + ua * (dir.x)), (float) (y1 + ua * (dir.y)));
        }
        return null;
    }

    public boolean intersectLine(Line otherLine) {
        float x1 = startPoint.x;
        float y1 = startPoint.y;
        float x3 = otherLine.getStartPoint().x;
        float y3 = otherLine.getStartPoint().y;
        PointFloat dir = direction;
        PointFloat oDir = otherLine.direction;


        double denom = (oDir.y) * (dir.x) - (oDir.x) * (dir.y);//crossproduct
        if (denom == 0.0) { // Lines are parallel.

            double distance = pointToLineDistance(otherLine.getStartPoint());
            if (distance > 0) {
                return false;
            } else {
                //lines are equal -> check, whether segments are overlapping
                PointFloat otherEndPoint = new PointFloat(x3 + oDir.x, y3 + oDir.y);
                float nearestStartPoint = nearestPointOnLineParameter(otherLine.getStartPoint(), false);
                float nearestEndPoint = nearestPointOnLineParameter(otherEndPoint, false);

                if ((nearestStartPoint >= 1 && nearestEndPoint >= 1) || (nearestStartPoint <= 0 && nearestEndPoint <= 0)) {
                    //segments do not overlap
                    return false;
                } else {
                    return true;
                }
            }
        }
        double ua = ((oDir.x) * (y1 - y3) - (oDir.y) * (x1 - x3)) / denom;
        double ub = ((dir.x) * (y1 - y3) - (dir.y) * (x1 - x3)) / denom;
        if (ua > 0.0f && ua < 1.0f && ub > 0.0f && ub < 1.0f) {
            // Get the intersection point.
            return true;
        }
        return false;
    }

    public float getDirectionLength(){
       return  (float) Math.sqrt(Math.pow(direction.x, 2) + Math.pow(direction.y, 2));
    }

    public boolean hasEnding() {
        return hasEnding;
    }

    public void setHasEnding(boolean hasEnding) {
        this.hasEnding = hasEnding;
    }
}
