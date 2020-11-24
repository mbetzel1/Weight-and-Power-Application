package com.example.practicewp;

public abstract class Weapon {
    private String name;
    private double drag;
    private int weight;

    public Weapon(String name, int weight, double drag) {
        this.name = name;
        this.drag = drag;
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }
    public double getDrag() {
        return drag;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return String.format("%s: %d lbs, %.2f drag", name, weight, drag);
    }
}
