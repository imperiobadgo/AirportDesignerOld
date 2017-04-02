package com.pukekogames.airportdesigner;


import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.Buildings.*;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Roads.Street;
import com.pukekogames.airportdesigner.Objects.Roads.Taxiway;

/**
 * Created by Marko Rapka on 05.12.2015.
 */

//This class generates the different screens for the game
public class GameContent {

    public static void setNewGame(){
        switch (GameInstance.Settings().gameType) {
            case 0:
                GameInstance.Reset();

                generateStartAirport();
                GameInstance.Instance().setStartMoney();
                break;
            case 1:
                GameInstance.Reset();

                generateDebugAirport();
                for (int i = 0; i < 4; i++) {
                    GameInstance.AirlineManager().AddNewAirline();
                }
                for (int i = 0; i < GameInstance.AirlineManager().AirlinesCount(); i++) {
                    Airline airline = GameInstance.AirlineManager().getAirline(i);
                    for (int j = 0; j < airline.PlannedArivalsCount(); j++) {
                        PlannedArrival arrival = airline.getPlannedArival(j);
                        arrival.setAccepted(true);
                    }
                }

                GameInstance.Instance().addMoney(1000L);
                GameInstance.Settings().gameType = 0;
                GameInstance.Settings().level = 3;
                break;
        }
    }

    private static void generateStartAirport() {
        GameInstance.Airport().clear();

        //first Runway
        addRoadIntersection(0, 0);//0
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 0);//1

        //Taxiway around first Runway
        addRoadIntersection(0, GameInstance.Settings().buildMinRadius);//2
        addRoadIntersection(GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//3
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//4
        addRoadIntersection(3 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//5
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//6

        //Intersection for first 5 ParkGates
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//7
        addRoadIntersection(3 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//8
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//9


        addRunway(0, 1);

        //Taxiway around first Runway
        addTaxiWay(0, 2);
        addTaxiWay(1, 6);
        addTaxiWay(2, 3);
        addTaxiWay(3, 4);
        addTaxiWay(4, 5);
        addTaxiWay(5, 6);


        //first 3 ParkGates
        addParkGate(4, 7);
        addParkGate(5, 8);
        addParkGate(6, 9);


        //connect first 3 ParkGates
        addStreet(7, 8);
        addStreet(8, 9);

        //RoadIntersections for Depots
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//10
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, (int) Math.round(5.5 * GameInstance.Settings().buildMinRadius));//11

        addStreet(9, 10);
        addStreet(10, 11);

    }

    private static Runway addRunway(int first, int next) {
        RoadIntersection firstInter = GameInstance.Airport().getRoadIntersection(first);
        RoadIntersection nextInter = GameInstance.Airport().getRoadIntersection(next);
        Runway runway = new Runway();
        runway.setLast(firstInter);
        runway.setNext(nextInter);
        GameInstance.Airport().AddRoad(runway);
        return runway;
    }

    private static Taxiway addTaxiWay(int first, int next) {
        RoadIntersection firstInter = GameInstance.Airport().getRoadIntersection(first);
        RoadIntersection nextInter = GameInstance.Airport().getRoadIntersection(next);
        Taxiway taxiway = new Taxiway();
        taxiway.setLast(firstInter);
        taxiway.setNext(nextInter);
        GameInstance.Airport().AddRoad(taxiway);
        return taxiway;
    }

    private static ParkGate addParkGate(int first, int next) {
        RoadIntersection firstInter = GameInstance.Airport().getRoadIntersection(first);
        RoadIntersection nextInter = GameInstance.Airport().getRoadIntersection(next);
        ParkGate parkGate = new ParkGate();
        parkGate.setLast(firstInter);
        parkGate.setNext(nextInter);
        GameInstance.Airport().AddRoad(parkGate);
        return parkGate;
    }

    private static RoadIntersection addRoadIntersection(int x, int y) {
        RoadIntersection inter = new RoadIntersection(new PointFloat(x, y));
        GameInstance.Airport().AddRoadIntersection(inter);
        return inter;
    }


    private static Street addStreet(int first, int next) {
        RoadIntersection firstInter = GameInstance.Airport().getRoadIntersection(first);
        RoadIntersection nextInter = GameInstance.Airport().getRoadIntersection(next);
        Street street = new Street();
        street.setLast(firstInter);
        street.setNext(nextInter);
        GameInstance.Airport().AddRoad(street);
        return street;
    }


    private static void generateDebugAirport() {
        GameInstance.Airport().clear();

        //first Runway
        addRoadIntersection(0, 0);//0
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 0);//1

        //Taxiway around first Runway
        addRoadIntersection(0, GameInstance.Settings().buildMinRadius);//2
        addRoadIntersection(GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//3
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//4
        addRoadIntersection(3 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//5
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, GameInstance.Settings().buildMinRadius);//6

        //Intersection for first 5 ParkGates
        addRoadIntersection(0, 2 * GameInstance.Settings().buildMinRadius);//7
        addRoadIntersection(GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//8
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//9
        addRoadIntersection(3 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//10
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//11


        addRunway(0, 1);

        //Taxiway around first Runway
        addTaxiWay(0, 2);
        addTaxiWay(1, 6);
        addTaxiWay(2, 3);
        addTaxiWay(3, 4);
        addTaxiWay(4, 5);
        addTaxiWay(5, 6);


        //first 5 ParkGates
        addParkGate(2, 7);
        addParkGate(3, 8);
        addParkGate(4, 9);
        addParkGate(5, 10);
        addParkGate(6, 11);

        //connect first 5 ParkGates
        addStreet(7, 8);
        addStreet(8, 9);
        addStreet(9, 10);
        addStreet(10, 11);

        //RoadIntersections for Depots
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//12
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 5 * GameInstance.Settings().buildMinRadius);//13
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 6 * GameInstance.Settings().buildMinRadius);//14

        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//15
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, 5 * GameInstance.Settings().buildMinRadius);//16
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, 6 * GameInstance.Settings().buildMinRadius);//17
        addRoadIntersection(6 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//18
        addRoadIntersection(6 * GameInstance.Settings().buildMinRadius, 5 * GameInstance.Settings().buildMinRadius);//19

        addStreet(11, 12);
        addStreet(12, 13);
        addStreet(13, 14);


        Street busStreet = addStreet(12, 15);
        Street crewBusStreet = addStreet(13, 16);
        Street cateringStreet = addStreet(14, 17);
        Street tankStreet = addStreet(12, 18);
        Street towerStreet = addStreet(13, 19);

        BusDepot busDepot = new BusDepot(busStreet);
        CrewBusDepot crewBusDepot = new CrewBusDepot(crewBusStreet);
        CateringDepot cateringDepot = new CateringDepot(cateringStreet);
        TankDepot tankDepot = new TankDepot(tankStreet);
        Tower tower = new Tower(towerStreet);

        GameInstance.Airport().AddBuilding(busDepot);
        GameInstance.Airport().AddBuilding(crewBusDepot);
        GameInstance.Airport().AddBuilding(cateringDepot);
        GameInstance.Airport().AddBuilding(tankDepot);
        GameInstance.Airport().AddBuilding(tower);


        //Taxiway around first Runway in north
        addRoadIntersection(0, -1 * GameInstance.Settings().buildMinRadius);//20
        addRoadIntersection(GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//21
        addRoadIntersection(2 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//22
        addRoadIntersection(3 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//23
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//24

        //Taxiway around first Runway in north
        addTaxiWay(0, 20);
        addTaxiWay(1, 24);
        addTaxiWay(20, 21);
        addTaxiWay(21, 22);
        addTaxiWay(22, 23);
        addTaxiWay(23, 24);

        //connection to second runway
        addRoadIntersection(0, -2 * GameInstance.Settings().buildMinRadius);//25
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, -2 * GameInstance.Settings().buildMinRadius);//26
        addTaxiWay(20, 25);
        addTaxiWay(24, 26);
        addRunway(25, 26);

        //west part

        //Taxiway to third runway (west) /square
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//27
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//28
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius,  0);//29
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius,  GameInstance.Settings().buildMinRadius);//30
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius,  GameInstance.Settings().buildMinRadius);//31

        addTaxiWay(20, 27);
        addTaxiWay(27, 28);
        addTaxiWay(28, 29);
        addTaxiWay(29, 30);
        addTaxiWay(30, 31);
        addTaxiWay(31, 2);

        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//32
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 3 * GameInstance.Settings().buildMinRadius);//33
        addRoadIntersection(-3 * GameInstance.Settings().buildMinRadius, -1 * GameInstance.Settings().buildMinRadius);//34
        addRoadIntersection(-3 * GameInstance.Settings().buildMinRadius, 3 * GameInstance.Settings().buildMinRadius);//35

        addTaxiWay(30, 32);
        addTaxiWay(32, 33);
        addTaxiWay(28, 34);
        addTaxiWay(33, 35);

        //west runway
        addRunway(34, 35);

        //west gates taxiway
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 2 * GameInstance.Settings().buildMinRadius);//36
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 3 * GameInstance.Settings().buildMinRadius);//37
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//38
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 5 * GameInstance.Settings().buildMinRadius);//39

        //west gates streets
        addRoadIntersection(0, 3 * GameInstance.Settings().buildMinRadius);//40
        addRoadIntersection(0, 4 * GameInstance.Settings().buildMinRadius);//41
        addRoadIntersection(0, 5 * GameInstance.Settings().buildMinRadius);//42

        addTaxiWay(31,36);
        addTaxiWay(36,37);
        addTaxiWay(37,38);
        addTaxiWay(38,39);

        addParkGate(37,40);
        addParkGate(38,41);
        addParkGate(39,42);

        Street street1 = addStreet(7,40);
        Street street2 = addStreet(40,41);
        Street street3 = addStreet(41,42);

        Terminal terminal1 = new Terminal(street1);
        Terminal terminal2 = new Terminal(street2);
        Terminal terminal3 = new Terminal(street3);

        GameInstance.Airport().AddBuilding(terminal1);
        GameInstance.Airport().AddBuilding(terminal2);
        GameInstance.Airport().AddBuilding(terminal3);


        //taxiways
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 6 * GameInstance.Settings().buildMinRadius);//43
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 7 * GameInstance.Settings().buildMinRadius);//44
        addRoadIntersection(-1 * GameInstance.Settings().buildMinRadius, 8 * GameInstance.Settings().buildMinRadius);//45

        //streets
        addRoadIntersection(0, 6 * GameInstance.Settings().buildMinRadius);//46
        addRoadIntersection(0, 7 * GameInstance.Settings().buildMinRadius);//47
        addRoadIntersection(0, 8 * GameInstance.Settings().buildMinRadius);//48

        addTaxiWay(39,43);
        addTaxiWay(43,44);
        addTaxiWay(44,45);

        addParkGate(43,46);
        addParkGate(44,47);
        addParkGate(45,48);

        addStreet(42,46);
        addStreet(46,47);
        addStreet(47,48);

        //connect depots with west gates
        addRoadIntersection(4 * GameInstance.Settings().buildMinRadius, 7 * GameInstance.Settings().buildMinRadius);//49
        addStreet(14,49);
        addStreet(47,49);

        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 4 * GameInstance.Settings().buildMinRadius);//50
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 5 * GameInstance.Settings().buildMinRadius);//51
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 6 * GameInstance.Settings().buildMinRadius);//52
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 7 * GameInstance.Settings().buildMinRadius);//53
        addRoadIntersection(-2 * GameInstance.Settings().buildMinRadius, 8 * GameInstance.Settings().buildMinRadius);//54

        addTaxiWay(33,50);
        addTaxiWay(50,51);
        addTaxiWay(51,52);
        addTaxiWay(52,53);
        addTaxiWay(53,54);
        addTaxiWay(54,45);
        addTaxiWay(51,39);
    }
}
