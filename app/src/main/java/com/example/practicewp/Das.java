package com.example.practicewp;

import java.util.ArrayList;

public class Das extends Weapon{
    int weight = 0;
    public Das(String name, int weight, double drag) {
        super(name, weight, drag);
        this.weight = weight;
    }
    public ArrayList<Weapon> getAllowablePods() {
        ArrayList<Weapon> al = new ArrayList<Weapon>();
        al.add(new Pod("None", 0, 0.0));
        if (weight >= 70) {
            al.add(new Pod("LAU-68 C/A", 79, 0.8));
            al.add(new Pod("LAU-68 F/A", 92, 0.8));
            al.add(new Pod("LAU-61 C/A", 155, 1.55));
            al.add(new Pod("LAU-130", 133, 1.55));
            al.add(new Pod("LAU-131/A", 62, 0.8));
            al.add(new Pod("LAU-7 C/A", 117, 0.48));
            al.add(new Pod("Aux Tank", 81, 1.5));
            al.add(new Pod("IT II", 286, 0.34));
            al.add(new Pod("SUU-25F/A", 260, 1.0));
        }
        return al;
    }
    public ArrayList<Weapon> getAllowableGuns() {
        ArrayList<Weapon> al = new ArrayList<Weapon>();
        al.add(new Gun("None", 0, 0.0));
        if (weight != 0) {
            al.add(new Gun("GAU-17/A", 119, 1.5));
            al.add(new Gun("GAU-21", 140, 1.3));
            al.add(new Gun("M-240D", 28, 1.1));
        }
        return al;
    }
}
