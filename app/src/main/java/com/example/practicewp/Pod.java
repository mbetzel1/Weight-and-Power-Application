package com.example.practicewp;

import java.util.ArrayList;

public class Pod extends Weapon{
    int weight, max;
    public Pod(String name, int weight, double drag) {
        super(name, weight, drag);
        this.weight = weight;
    }
    public ArrayList<Weapon> getAllowableRockets() {
        ArrayList<Weapon> al = new ArrayList<Weapon>();
        al.add(new Rocket("None", 0));
        if (weight == 260) {
            al.add(new Rocket("LUU-19", 36));
            al.add(new Rocket("LUU-2B/B", 30));
        }
        else if (weight > 0 && weight < 259 && weight != 81) {
            al.add(new Rocket("M-151/Mk152/WTU", 23));
            al.add(new Rocket("M-229", 31));
            al.add(new Rocket("Mk149 Mod 0", 28));
            al.add(new Rocket("Mk146 Mod 0", 30));
            al.add(new Rocket("WDU-4A/A", 23));
            al.add(new Rocket("Mk67 Mod 1", 23));
            al.add(new Rocket("M-156", 24));
            al.add(new Rocket("M-257", 25));
            al.add(new Rocket("M-278", 25));
            al.add(new Rocket("M-282", 27));
            al.add(new Rocket("AGR-19A", 33));
            al.add(new Rocket("AGR-19B", 37));
        }
        return al;
    }
    public int getMaxRockets() {
        if (weight < 1) {
            return 0;
        }
        else if (weight < 100) {
            return 7;
        }
        else {
            return 19;
        }
    }

}
