package me.aemo.addons;

// Class to represent a product entry
public class ProductEntry {
    private String itemName;
    private double quantity;
    private double length;
    private double height;
    private double surface;
    private double price;
    private double total;

    public ProductEntry(String itemName, double quantity, double length, double height, double surface, double price, double total) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.length = length;
        this.height = height;
        this.surface = surface;
        this.price = price;
        this.total = total;
    }

    public void setItemName(){
        this.itemName = itemName;
    }
    public String getItemName() { return itemName; }
    public void setQuantity(double quantity){
        this.quantity = quantity;
    }
    public double getQuantity() { return quantity; }
    public void setLength(double length){
        this.length = length;
    }
    public double getLength() { return length; }
    public void setHeight(double height){
        this.height = height;
    }
    public double getHeight() { return height; }
    public void setSurface(double surface){
        this.surface = surface;
    }
    public double getSurface() { return surface; }
    public void setPrice(double price){
        this.price = price;
    }
    public double getPrice() { return price; }
    public void setTotal(double total){
        this.total = total;
    }
    public double getTotal() { return total; }

    @Override
    public String toString() {
        return String.format("Item: %s, Quantity: %.2f, Length: %.2f, Height: %.2f, Surface: %.2f, Price: %.2f, Total: %.2f",
                itemName, quantity, length, height, surface, price, total);
    }
}