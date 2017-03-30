package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.*;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Geometry.Vector2D;
import com.pukekogames.airportdesigner.Helper.Pathfinding.Dijkstra;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.*;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehiclePerformance;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 12.06.2016.
 */
public abstract class Vehicle extends ClickableGameObject {
    private static final long serialVersionUID = 4022562322734572481L;
    PointFloat targetPoint;
    PointInt centerPos;
    Vector2D headingDirection;
    Vector2D toTarget;
    float collisionRadius;
    float speed = 2f;
    float targetSpeed = 0f;
    int waitTime = 0;
    Road currentRoad = null;
    Road nextRoad = null;
    RoadIntersection targetIntersection = null;
    ArrayList<Road> nextRoads;
    boolean sameDirectionAsRoad = false;
    Dijkstra pathfinding;
    VehiclePerformance performance;
    Vector2D closestVehicleVector = null;
    Vehicle closestVehicle = null;
    int lastDistanceSet = 0;

    ArrayList<GameplayWarning> warnings;

    boolean hasFailedToFoundPath;
    int ignoreCollisionTime = 0;
    int timeWaitingBlockedWay = 0;

    PointInt roadTarget;//for debugging heading point back to road
    PointInt headingPoint;//for debugging supposed heading direction

    Vehicle(float x, float y) {
        super(Alignment.Table, x, y, 40, 50);
        targetPoint = new PointFloat(0, 0);
        centerPos = new PointInt();
        headingDirection = new Vector2D(heading);
        toTarget = new Vector2D(0, 0);
        nextRoads = new ArrayList<Road>();
        roadTarget = new PointInt(0, 0);
        headingPoint = new PointInt(0, 0);
        closestVehicleVector = new Vector2D(0, 0);

        warnings = new ArrayList<>();
    }

    void updateHeadingAndDistanceToTarget() {
        updateHeadingAndDistanceToTarget(false);

    }

    void setDistanceToNextVehicle() {
        if (lastDistanceSet > 0) {
            lastDistanceSet--;
            return;
        } else {
            lastDistanceSet = (centerPos.x + centerPos.y) % 20 + 30;//get some randomness
        }

        Vehicle closestVehicle = null;
        double smallestDistance = Double.POSITIVE_INFINITY;
        double minDistance = 1500;
        for (int i = 0; i < GameInstance.Airport().getVehicleCount(); i++) {
            Vehicle vehicle = GameInstance.Airport().getVehicle(i);
            if (vehicle.equals(this)) continue;
            closestVehicleVector.set(getAlign_X(), getAlign_Y(), vehicle.getAlign_X(), vehicle.getAlign_Y());
            double distance = closestVehicleVector.Length();

            float dotProduct = Vector2D.DotProduct(headingDirection, closestVehicleVector);
            float lookangle = 0.1f;
            if (this instanceof StreetVehicle) {
                lookangle = -0.1f;
            }
            if (this instanceof StreetVehicle && vehicle instanceof StreetVehicle){
                lookangle = 0.7f;
            }
            if (distance < smallestDistance && dotProduct > lookangle) {
                closestVehicle = vehicle;
                smallestDistance = distance;
            }
        }
        if (closestVehicle != null) {
            closestVehicleVector.set(getAlign_X(), getAlign_Y(), closestVehicle.getAlign_X(), closestVehicle.getAlign_Y());
        } else {
            closestVehicleVector.set(0, 0);
        }
        this.closestVehicle = closestVehicle;
    }

    float updateHeadingAndDistanceToTarget(boolean continueHeading) {

        toTarget.set(targetPoint.x, targetPoint.y, Align_X, Align_Y);

        if (speed < 2) return 0;

        float headingToTarget = getHeadingToTarget(currentRoad, toTarget.getX(), toTarget.getY(), performance.targetPointDistance);


        float headingDifference = headingToTarget - heading;
        headingDifference = (headingDifference + 180) % 360 - 180;
        if (headingDifference < -180) headingDifference += 360;

        if (Math.abs(headingDifference) > performance.turnRate && !continueHeading) {
            if (headingDifference >= 0) {
                heading += performance.turnRate;
            } else {
                heading -= performance.turnRate;
            }
        }

        headingDirection.set(heading);
        return headingDifference;
    }

    void updateVelocity() {
        if (ignoreCollisionTime > -1) {
            ignoreCollisionTime--;
        }

        if (GameInstance.Settings().CollisionDetection && ignoreCollisionTime < 1) {
            int detectPossibleCollision = 0;
            boolean stopNow = false;
            boolean slowDown = false;


            if (closestVehicle != null) {

                float possibleCollisionRadius = collisionRadius + collisionRadius * speed * 0.2f + closestVehicle.collisionRadius;

                Vector2D otherHeadingVector = closestVehicle.getHeadingDirection();
                float headingDifferenceTarget = Vector2D.AngleBetween(headingDirection, otherHeadingVector);
                float directHeadingDifference = Vector2D.AngleBetween(headingDirection, closestVehicleVector);

//                if (closestVehicleVector.Length() > 0 && closestVehicleVector.Length() < 1.3 * possibleCollisionRadius && directHeadingDifference < 60) {
//                    slowDown = true;
//                    detectPossibleCollision = 1;
//                }
                if (closestVehicleVector.Length() > 0 && closestVehicleVector.Length() < possibleCollisionRadius && directHeadingDifference < 30) {
                    stopNow = true;
                    detectPossibleCollision += 1;
                }


                //float dotProduct = Vector2D.DotProduct(headingDirection, otherHeadingVector);

//                if (this instanceof StreetVehicle && closestVehicle instanceof Airplane) {
//                    if (closestVehicle.getSpeed() > 1 && closestVehicleVector.Length() > 0 && closestVehicleVector.Length() < possibleCollisionRadius * 2) {
//                        stopNow = true;
//                        detectPossibleCollision = 2;
//                    }
//                }

                if (this instanceof StreetVehicle && closestVehicle instanceof StreetVehicle) {
                    StreetVehicle streetVehicle = (StreetVehicle) this;
                    StreetVehicle closeStreetVehicle = (StreetVehicle) closestVehicle;
                    if (headingDifferenceTarget > 170) {
                        detectPossibleCollision -= 1;
                        slowDown = false;
                        stopNow = false;
                    }
                    if (directHeadingDifference > 20) {
                        detectPossibleCollision -= 1;
                        slowDown = false;
                        stopNow = false;
                    }
                }
                if (this instanceof Airplane) {
                    Airplane airplane = (Airplane) this;
                    if (airplane.getAltitude() > 1) {
                        detectPossibleCollision = 0;
                        stopNow = false;
                        slowDown = false;
                    }

                }
                if (closestVehicle instanceof Airplane) {
                    Airplane airplane = (Airplane) closestVehicle;
                    if (airplane.getAltitude() > 500) {
                        detectPossibleCollision = 0;
                        stopNow = false;
                        slowDown = false;
                    }
                }
            }

            if (slowDown) {
                targetSpeed = targetSpeed / 2;
            }

            if (stopNow) {
                timeWaitingBlockedWay += 1;
                targetSpeed = 0;
            } else {
                timeWaitingBlockedWay = 0;
            }

//            if (detectPossibleCollision > 0) {
//                targetSpeed = targetSpeed / 2;
//            }
//            if (detectPossibleCollision > 1) {
//                timeWaitingBlockedWay += 1;
//                targetSpeed = 0;
//            }
//            if (detectPossibleCollision == 0){
//                timeWaitingBlockedWay = 0;
//            }
            if (timeWaitingBlockedWay > 150) {
                if (closestVehicle.collidingWithVehicle(this)) {
                    IgnoreCollision();
                    timeWaitingBlockedWay = 0;
                }
            }
        }
        if (hasFailedToFoundPath) {
            targetSpeed = 0;
        }

        float diffX = (float) Math.cos(Math.toRadians(heading));
        float diffY = (float) Math.sin(Math.toRadians(heading));

        float differenzSpeed = targetSpeed - speed;
        if (differenzSpeed < -performance.deceleration) {
            speed -= performance.deceleration;
        } else if (differenzSpeed > performance.acceleration) {
            speed += performance.acceleration;
        }
        if (speed < 1 && targetSpeed == 0) speed = 0;
        Align_X += diffX * speed;
        Align_Y += diffY * speed;
    }

    public abstract void searchRoute(RoadIntersection nextIntersection);

    void updateSearch() {
        RoadIntersection lastIntersection;
        float calcSpeed = speed;
        if (calcSpeed < 9) {
            calcSpeed = 9;
        }

        if (toTarget.Length() < calcSpeed * 50) return;
        if (sameDirectionAsRoad) {
            lastIntersection = currentRoad.getNext();
        } else {
            lastIntersection = currentRoad.getLast();
        }
        if (lastIntersection.equals(targetIntersection)) return;
//        setReferencesOnRoadPath(false);
        pathfinding = new Dijkstra(lastIntersection, targetIntersection, this);
    }

    void setReferencesOnRoadPath(boolean add) {

        RoadIntersection nextIntersection;
        //set direction for first road
        if (sameDirectionAsRoad) {
            nextIntersection = currentRoad.getNext();
            currentRoad.setDirectionInUse(1);
        } else {
            nextIntersection = currentRoad.getLast();
            currentRoad.setDirectionInUse(2);
        }


        for (Road road : nextRoads) {
            if (road == null) continue;
            if (add) {
                if (this instanceof Airplane) {
                    AirplaneState state = ((Airplane) this).getState();
                    if (road instanceof ParkGate && state == AirplaneState.TaxiToGate) {
                        road.setBlocked(true);
                    }
                }
                if (nextIntersection != null) {
                    if (road.getLast().equals(nextIntersection)) {
                        nextIntersection = road.getNext();
                        road.setDirectionInUse(1);
                    } else {
                        nextIntersection = road.getLast();
                        road.setDirectionInUse(2);
                    }
                }

                road.addVehicle(this);
            } else {
                road.removeVehicle(this);
//                if (road instanceof ParkGate) {
//                    ParkGate gate = (ParkGate) road;
//                    gate.setBlocked(false);
//                }
            }
        }
    }

    public void setRoad(Road road) {
        if (road == null) return;
        PointFloat startPoint = road.getStartPosition();
        PointFloat endPoint = road.getEndPosition();
        PointFloat ownPos = new PointFloat(Align_X, Align_Y);
        double startDistanz = CommonMethods.getDistance(ownPos, startPoint);// Math.pow(ownPos.x - startPoint.getX(), 2) + Math.pow(ownPos.y - startPoint.getY(), 2);
        double endDistanz = CommonMethods.getDistance(ownPos, endPoint);// Math.pow(ownPos.x - endPoint.getX(), 2) + Math.pow(ownPos.y - endPoint.getY(), 2);
        sameDirectionAsRoad = startDistanz < endDistanz;
        currentRoad = road;
        if (sameDirectionAsRoad) {
            if (this instanceof Airplane) {
                targetPoint = endPoint;
            } else {
                float vehicleWidth = 100;
                int dx = (int) (Math.cos(Math.toRadians((road.getHeading() + 90) % 360)) * vehicleWidth);
                int dy = (int) (Math.sin(Math.toRadians((road.getHeading() + 90) % 360)) * vehicleWidth);
                targetPoint.set(endPoint.x + dx, endPoint.y + dy);
            }
        } else {
            if (this instanceof Airplane) {
                targetPoint = startPoint;
            } else {
                float vehicleWidth = 100;
                int dx = (int) (Math.cos(Math.toRadians((road.getHeading() + 270) % 360)) * vehicleWidth);
                int dy = (int) (Math.sin(Math.toRadians((road.getHeading() + 270) % 360)) * vehicleWidth);
                targetPoint.set(startPoint.x + dx, startPoint.y + dy);
            }
        }
//        System.out.println("new target " + targetPoint.toString());
//        road.addVehicle(index);
    }

    void reachedNextRoad() {

        //for removing unnecessary roads
        int currentRoadIndex = -1;
        for (int i = 0; i < nextRoads.size(); i++) {
            Road nextRoad = nextRoads.get(i);
            if (nextRoad.equals(currentRoad)) {
                currentRoadIndex = i;
                break;
            }
            if (i > 3) {
                break;
            }
        }
        if (currentRoadIndex == -1) return;

        for (int i = 0; i < currentRoadIndex; i++) {
            Road road = nextRoads.get(0);
            road.removeVehicle(this);
            nextRoads.remove(0);
        }

    }

    float getHeadingToTarget(Road currentRoad, double diffX, double diffY, int targetPointDistance) {

        float headingToTarget;
        if (currentRoad != null) {
            PointFloat ownPos = new PointFloat(Align_X, Align_Y);
            if (sameDirectionAsRoad) {
                headingToTarget = currentRoad.getHeading();
            } else {
                headingToTarget = (currentRoad.getHeading() + 180) % 360;
            }
            float headingReversed = (headingToTarget + 180) % 360;
            float roadX = (float) Math.cos(Math.toRadians(headingReversed));
            float roadY = (float) Math.sin(Math.toRadians(headingReversed));
            PointFloat roadStartPos = new PointFloat(targetPoint.x + roadX * currentRoad.getLength(), targetPoint.y + roadY * currentRoad.getLength());
            PointFloat roadEndPos = new PointFloat(targetPoint.x, targetPoint.y);

            double distanceToRoad;

            //return back on track(on the road)
            if (sameDirectionAsRoad) {
                distanceToRoad = CommonMethods.pointToLineDistance(roadStartPos, roadEndPos, ownPos);
//                whichSide = CommonMethods.whichSidePointToLine(roadStartPos, roadEndPos, ownPos);
            } else {
                distanceToRoad = CommonMethods.pointToLineDistance(roadEndPos, roadStartPos, ownPos);
//                whichSide = CommonMethods.whichSidePointToLine(roadEndPos, roadStartPos, ownPos);
            }

            float targetdistance = toTarget.Length();

            float beforePointFactor = (targetdistance - targetPointDistance) / targetdistance;
            if (beforePointFactor < 0) {
                beforePointFactor = 0f;
            }
            //target for aiming back to road
            roadTarget = new PointInt((int) (targetPoint.x + roadX * targetdistance * beforePointFactor), (int) (targetPoint.y + roadY * targetdistance * beforePointFactor));
            diffX = roadTarget.x - Align_X;
            diffY = roadTarget.y - Align_Y;
//            headingPoint = new Point((int) (Align_X + diffX * 0.8), (int) ( Align_Y + diffY * 0.8));
            float headingDirectToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;
            float headingShowLength = 200f;
            headingPoint = new PointInt((int) (Align_X + Math.cos(Math.toRadians(headingDirectToTarget)) * headingShowLength), (int) (Align_Y + Math.sin(Math.toRadians(headingDirectToTarget)) * headingShowLength));
            float headingDifferenceTarget = headingDirectToTarget - headingToTarget;
            headingDifferenceTarget = (headingDifferenceTarget + 180) % 360 - 180;
            if (Math.abs(headingDifferenceTarget + (-180)) < 10) headingDifferenceTarget = 180;
            if (headingDifferenceTarget < -180) headingDifferenceTarget += 360;
            distanceToRoad += 2;//to prevend driving on when passed target
            float correction = (float) ((distanceToRoad * headingDifferenceTarget) / 90);
            if (correction > 90) {
                correction = 90;
            } else if (correction < -90) {
                correction = -90;
            }
            headingToTarget += correction;

        } else {

            headingToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;

        }
        return headingToTarget;
    }

    float getHeadingdifferenzToNextRoad() {
        float headingDifference = 0;
        if (currentRoad != null && nextRoad != null) {
            PointFloat ownPos = new PointFloat(Align_X, Align_Y);
            float nextHeading = nextRoad.getHeading();

            double startDistance = CommonMethods.getDistance(ownPos, nextRoad.getStartPosition());
            double endDistance = CommonMethods.getDistance(ownPos, nextRoad.getEndPosition());
            if (endDistance < startDistance) {
                nextHeading = (nextHeading + 180) % 360;
            }
            headingDifference = nextHeading - heading;
            headingDifference = (headingDifference + 180) % 360 - 180;
            if (headingDifference < -180) headingDifference += 360;

        }
        return headingDifference;
    }


    boolean setNextRoad() {
        if (nextRoads.size() > 0) {

            Road road = nextRoads.remove(0);
            currentRoad.removeVehicle(this);
            setRoad(road);
            if (nextRoads.size() > 0) {
                nextRoad = nextRoads.get(0);
            }
            if (pathfinding == null && nextRoads.size() > 0) {
                updateSearch();
            }
            return true;
        } else {
            return false;
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        targetSpeed = speed;
    }


    public void setWaitTime(int time) {
        waitTime = time;
    }

    public PointInt getCenterPos() {
        return centerPos;
    }

    public void setTargetPoint(PointFloat targetPoint) {
        this.targetPoint = targetPoint;
    }

    public ArrayList<Road> getNextRoads() {
        return nextRoads;
    }

    public PointFloat getTargetPoint() {
        return targetPoint;
    }

    public float getSpeed() {
        return speed;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public RoadIntersection getTargetIntersection() {
        return targetIntersection;
    }

    RoadIntersection getNextIntersection() {
        if (sameDirectionAsRoad) {
            return currentRoad.getNext();
        } else {
            return currentRoad.getLast();
        }
    }

    public PointInt getRoadTarget() {
        return roadTarget;
    }

    public PointInt getHeadingPoint() {
        return headingPoint;
    }

    public void setCenterPos(PointInt centerPos) {
        this.centerPos = centerPos;
    }

    public Road getCurrentRoad() {
        return currentRoad;
    }

    public void addWarning(GameplayWarning warning) {
        warnings.add(warning);
    }

    public ArrayList<GameplayWarning> getWarnings() {
        return warnings;
    }

    public Vector2D getHeadingDirection() {
        return headingDirection;
    }

    public float getDistanceToNextVehicle() {
        return closestVehicleVector.Length();
    }

    public Vehicle getClosestVehicle() {
        return closestVehicle;
    }

    public void ClearReferences() {
        currentRoad = null;
        nextRoad = null;
        targetIntersection = null;
        nextRoads.clear();
        pathfinding = null;
    }

    public void IgnoreCollision() {
        ignoreCollisionTime = 80;
    }

    boolean collidingWithVehicle(Vehicle vehicle) {
        if (vehicle == null || closestVehicle == null) return false;
        if (ignoreCollisionTime > 0) return false;
        return closestVehicle.equals(vehicle);
    }

    public float getCollisionRadius() {
        return collisionRadius;
    }
}
