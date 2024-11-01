package me.aemo.addons.product;


import me.aemo.addons.data.Item;

import java.io.IOException;

public class ProductsUtils {
    private static ProductJson productJson;

    public ProductsUtils() throws IOException {
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
