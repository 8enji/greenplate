package com.example.greenplate.model;

public class Ingredient {
    private String name;
    private double quantity;
    private String units;
    private double calories;

    public Ingredient(String name, double quantity, String units, double calories) {
        this.name = name;
        this.quantity = quantity;
        this.units = units;
        this.calories = calories;
    }

    public double getCalories() { return calories;}
    public String getName() {
        return name;
    }
    public double getQuantity() {
        return quantity;
    }
    public String getUnits() {
        return units;
    }


}
