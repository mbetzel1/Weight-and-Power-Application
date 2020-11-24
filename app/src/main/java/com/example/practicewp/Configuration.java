package com.example.practicewp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//need to create a column for each saved part of the class
@Entity(tableName = "saved")
public class Configuration {
    @PrimaryKey
    @NonNull
    public String configName;

    @ColumnInfo(name = "side")
    public int side;

    @ColumnInfo(name = "basicWt")
    public int basicWt;

    @ColumnInfo(name = "pilotWt")
    public int pilotWt;

    @ColumnInfo(name = "crewWt")
    public int crewWt;

    @ColumnInfo(name = "otherWt")
    public int otherWt;

    @ColumnInfo(name = "rRocketNum")
    public int rRocketNum;

    @ColumnInfo(name = "lRocketNum")
    public int lRocketNum;

    @ColumnInfo(name = "chaffNum")
    public int chaffNum;

    @ColumnInfo(name = "flareNum")
    public int flareNum;

    @ColumnInfo(name = "smNum")
    public int smNum;

    @ColumnInfo(name = "fiftyNum")
    public int fiftyNum;

    @ColumnInfo(name = "miniNum")
    public int miniNum;

    @ColumnInfo(name = "fuelWt")
    public int fuelWt;

    @ColumnInfo(name = "exfuelWt")
    public int exfuelWt;

    @ColumnInfo(name = "paxWt")
    public int paxWt;

    @ColumnInfo(name = "cargoWt")
    public int cargoWt;

    @ColumnInfo(name = "rDasWt")
    public int rDasWt;

    @ColumnInfo(name = "lDasWt")
    public int lDasWt;

    @ColumnInfo(name = "rPodWt")
    public int rPodWt;

    @ColumnInfo(name = "lPodWt")
    public int lPodWt;

    @ColumnInfo(name = "rGunWt")
    public int rGunWt;

    @ColumnInfo(name = "lGunWt")
    public int lGunWt;

    @ColumnInfo(name = "rRocketWt")
    public int rRocketWt;

    @ColumnInfo(name = "lRocketWt")
    public int lRocketWt;

    @ColumnInfo(name = "seatsWt")
    public int seatsWt;
    
    @ColumnInfo(name = "lDasDrag")
    public double lDasDrag;

    @ColumnInfo(name = "rDasDrag")
    public double rDasDrag;

    @ColumnInfo(name = "lPodDrag")
    public double lPodDrag;

    @ColumnInfo(name = "rPodDrag")
    public double rPodDrag;

    @ColumnInfo(name = "lGunDrag")
    public double lGunDrag;

    @ColumnInfo(name = "rGunDrag")
    public double rGunDrag;

    @ColumnInfo(name = "doorDrag")
    public double doorDrag;

    public Configuration(String configName, int side, int basicWt, int pilotWt, int crewWt,
                         int otherWt, int rRocketNum, int lRocketNum, int chaffNum,
                         int flareNum, int smNum, int fiftyNum, int miniNum,
                         int fuelWt, int exfuelWt, int paxWt, int cargoWt, int rDasWt, int lDasWt,
                         int rPodWt, int lPodWt, int rGunWt, int lGunWt,
                         int rRocketWt, int lRocketWt, int seatsWt, double lDasDrag, double rDasDrag,
                         double lPodDrag, double rPodDrag, double lGunDrag, double rGunDrag, double doorDrag) {
        this.configName = configName;
        this.side = side;
        this.basicWt = basicWt;
        this.pilotWt = pilotWt;
        this.crewWt = crewWt;
        this.otherWt = otherWt;
        this.rRocketNum = rRocketNum;
        this.lRocketNum = lRocketNum;
        this.chaffNum = chaffNum;
        this.flareNum = flareNum;
        this.smNum = smNum;
        this.fiftyNum = fiftyNum;
        this.miniNum = miniNum;
        this.fuelWt = fuelWt;
        this.exfuelWt = exfuelWt;
        this.paxWt = paxWt;
        this.cargoWt = cargoWt;
        this.rDasWt = rDasWt;
        this.lDasWt = lDasWt;
        this.lPodWt = lPodWt;
        this.rPodWt = rPodWt;
        this.rGunWt = rGunWt;
        this.lGunWt = lGunWt;
        this.rRocketWt = rRocketWt;
        this.lRocketWt = lRocketWt;
        this.seatsWt = seatsWt;
        this.rDasDrag = rDasDrag;
        this.lDasDrag = lDasDrag;
        this.rPodDrag = rPodDrag;
        this.lPodDrag = lPodDrag;
        this.rGunDrag = rGunDrag;
        this.lGunDrag = lGunDrag;
        this.doorDrag = doorDrag;
    }
    public int getSmWt() {
        return (int) (0.28 * smNum);
    }
    public int getFlareWt() {
        return (int) (0.88 * flareNum);
    }
    public int getChaffWt() {
        return (int) (0.4 * chaffNum);
    }
    public int getRRocketTotal() {
        return (rRocketWt * rRocketNum);
    }
    public int getLRocketTotal() {
        return (lRocketWt * lRocketNum);
    }
    public int getFiftyWt() {
        return (int) (0.35 * fiftyNum);
    }
    public int getMiniWt() {
        return (int) (0.064 * miniNum);
    }
    public int getMissionGrossWeight() {
        //need to include sm, flare, chaff, 50 ammo, 762 ammo
        return getGrossWeight() + paxWt + cargoWt;
    }
    public int getOpWeight() {
        return basicWt + pilotWt + crewWt + otherWt + seatsWt + rGunWt + lGunWt + rPodWt + lPodWt
                + rDasWt + lDasWt;
    }
    public int getGrossWeight() {
        return getZeroFuelWeight() + fuelWt + exfuelWt;
    }
    public int getZeroFuelWeight() {
        return getOpWeight() + getRRocketTotal() + getLRocketTotal() + getFiftyWt()
                + getMiniWt() + getChaffWt() + getFlareWt() + getSmWt();
    }
    public double getTotalDrag() {
        return rDasDrag + lDasDrag + rPodDrag + lPodDrag + rGunDrag + lGunDrag + doorDrag;
    }
}
