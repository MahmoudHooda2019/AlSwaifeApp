package me.aemo.addons;

public class Item {
    public String name;
    public Double price;
    public Item(String name, Double price){
        this.name = name;
        this.price = price;
    }
    public String getName(){
        return name;
    }
    public Double getPrice(){
        return price;
    }
}
