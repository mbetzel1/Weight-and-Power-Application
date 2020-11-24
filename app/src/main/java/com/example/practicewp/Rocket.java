package com.example.practicewp;

public class Rocket extends Weapon{
    private String name;
    private int weight;

    public Rocket(String name, int weight) {
        super(name, weight, 0);
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("%s: %d lbs each", name, weight);
    }
}
