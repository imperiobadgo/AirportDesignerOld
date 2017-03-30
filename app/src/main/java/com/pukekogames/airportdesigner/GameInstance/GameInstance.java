package com.pukekogames.airportdesigner.GameInstance;

import com.pukekogames.airportdesigner.Helper.MoneyChange;
import com.pukekogames.airportdesigner.Helper.TimeStamp;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 30.10.2016.
 */
public class GameInstance implements Serializable {

    private static final long serialVersionUID = 1593547318845530706L;
    private Airport airport;
    private CommonSettings settings;
    private AirlineManager airlineManager;
    private Random random;
    private String name = "";
    private long money;
    private TimeStamp time;

    private ArrayList<String> messages;
    private int messageShowTime = 0;

    private static GameInstance ourInstance;
    private boolean newAirplanesAdded;
    private boolean updateFlightPlan;

    GameInstance() {
        airport = new Airport();
        settings = new CommonSettings();
        lastMoneyChange = new CopyOnWriteArrayList<>();
        airlineManager = new AirlineManager();
        random = new Random();
        messages = new ArrayList<>();
    }

    public static GameInstance Instance() {
        if (ourInstance == null) {
            Reset();
        }
        return ourInstance;
    }

    public static void Reset() {
        ourInstance = new GameInstance();
        ourInstance.CreateTimeStamp();
        ourInstance.setTime(ourInstance.settings.maxTime / 6);
    }

    private void CreateTimeStamp() {
        time = new TimeStamp(0);
    }

    public static void setGameInstance(GameInstance instance) {
        ourInstance = instance;
    }

    public static Airport Airport() {
        return Instance().airport;
    }

    public static CommonSettings Settings() {
        return Instance().settings;
    }

    public static AirlineManager AirlineManager() {
        return Instance().airlineManager;
    }

    public void reset() {
        airport.clear();
        airlineManager.clear();
        money = 0L;
    }

    public void tick() {
        for (int i = 0; i < GameInstance.Settings().gameSpeed; i++) {

            if (!airport.isPauseSimulation()) updateTime();

            if (time.getMinute() == 0 && !newAirplanesAdded) {
                ArrayList<Airplane> nextAirplanes = new ArrayList<>();


                int amountOfAllGates = airport.getAllGates().size();
                int amountOfFreeGates = airport.getAmountOfFreeGates();


                Random random = new Random();


                if (settings.level > 2) {
                    airlineManager.getNextAirplanes(time.getHour(), nextAirplanes);

                    int r = random.nextInt(amountOfAllGates + 4 - amountOfFreeGates);
                    if (r == 0) {
                        nextAirplanes.add(airlineManager.generateNewPrivateAirplane());
                    }
                } else {
                    if (airport.getAirplaneCount() == 0 && nextAirplanes.size() == 0) {
                        nextAirplanes.add(airlineManager.generateNewPrivateAirplane());
                    }

                    if (nextAirplanes.size() == 0 && amountOfFreeGates > 0) {
                        int r = random.nextInt(amountOfAllGates + 2 - amountOfFreeGates);
                        if (r == 0) {
                            nextAirplanes.add(airlineManager.generateNewPrivateAirplane());
                        }
                    }

                }

                airport.AddAllNextAirplane(nextAirplanes);
                newAirplanesAdded = true;

            }

            if (time.getHour() == 20 && time.getMinute() == 0 && !updateFlightPlan){
                updateFlightPlan = true;
                if (airlineManager.updateFlightPlan()){
                    AddMessage("Flightplan updated!");
                }
            }

            if (time.getMinute() == 1){
                newAirplanesAdded = false;
                updateFlightPlan = false;
            }

            GameInstance.Airport().tick();


            for (MoneyChange change : lastMoneyChange) {

                change.time++;
                if (change.amount < 0) change.time++;
            }
            while (lastMoneyChange.size() > 0) {
                if (lastMoneyChange.get(0).time > 255) {
                    lastMoneyChange.remove(0);
                } else {
                    break;
                }
            }

            if (messageShowTime >= 0) {
                messageShowTime--;
                if (messageShowTime < 1 && messages.size() > 0) {
                    messages.remove(0);
                    messageShowTime = settings.MessageShowTime;
                }
            }
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) return;
        this.name = name;
    }

    private int ticksPerTime = 0;

    private void updateTime() {
        ticksPerTime += 1;
        if (ticksPerTime == 3) {
            time.increase();
            ticksPerTime = 0;

            if (settings.level < 4) {
                if (time.getHour() > 21) {
                    time.setTime(settings.maxTime / 6);
                }
            }
        }
    }

    public int getTime() {
        return time.getTime();
    }

    public int getMinuteDifferenceFromCurrentTime(TimeStamp otherTime) {
        return time.getMinuteDifference(otherTime);
    }

    public void setTime(int time) {
        this.time.setTime(time);
    }

    public int getHour() {
        return time.getHour();
    }

    public int getMinute() {
        return time.getMinute();
    }

    private CopyOnWriteArrayList<MoneyChange> lastMoneyChange;

    public long getMoney() {
        return money;
    }

    public CopyOnWriteArrayList<MoneyChange> getLastMoneyChange() {
        return lastMoneyChange;
    }

    public void addMoney(long newMoney) {
        money += newMoney;
        lastMoneyChange.add(new MoneyChange(newMoney));
    }

    public boolean removeMoney(long remove) {
        if (money >= remove) {
            money -= remove;
            lastMoneyChange.add(new MoneyChange(-remove));
            return true;
        }
        return false;
    }

    public void setStartMoney() {
        money = Prices.StartMoney;
    }

    public String getCurrentMessage() {
        if (messages.size() == 0) return null;
        return messages.get(0);
    }

    public boolean isCurrentMessageNew() {
        return settings.MessageShowTime - messageShowTime < settings.MessageShowTime / 3;
    }

    public void AddMessage(String message) {
        if (messages.size() == 0) {
            messageShowTime = settings.MessageShowTime;
        }
        messages.add(message);
    }

}
