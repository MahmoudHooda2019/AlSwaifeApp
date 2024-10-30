package me.aemo.addons;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ProductsUtils {
    private static ProductJson productJson;
    //private static final List<Item> items = new ArrayList<>();

    public ProductsUtils() throws URISyntaxException {
        productJson = new ProductJson();
    }

    public Item[] getAllItems() {
        return productJson.getItems().toArray(new Item[0]);
    }
    public static void addItem(Item item){
        productJson.addItem(item);
    }
    public static void addItem(String name, double price){
        productJson.addItem(name, price);
    }
    public static void removeItem(int index){
        productJson.removeItem(index);
    }

}
