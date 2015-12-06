package com.nexb.shopr4.dataModel;

/**
 * Created by Christian on 08-11-2015.
 */
public class ListItem {
    private double amount;
    private String unit;
    private String name;



    public enum ListItemState{DEFAULT,FOUND,NOT_FOUND}
    private ListItemState state = ListItemState.DEFAULT;

    public ListItem(){}

    public ListItem(double amount, String unit, String name) {
        this.amount = amount;
        this.unit = unit;
        this.name = name;
    }
    public ListItemState getState() {
        return state;
    }

    public void setState(ListItemState state) {
        this.state = state;
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "amount=" + amount +
                ", unit='" + unit + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }
}
