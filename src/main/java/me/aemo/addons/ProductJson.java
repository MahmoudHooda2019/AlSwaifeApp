package me.aemo.addons;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProductJson {
    private static final List<Item> items = new ArrayList<>();
    private File jsonFile;
    private long lastModified;

    public ProductJson() throws URISyntaxException {
        URL productsUrl = ProductJson.class.getClassLoader().getResource("products.json");
        if (productsUrl != null) {
            jsonFile = Paths.get(productsUrl.toURI()).toFile();
            if (jsonFile.exists()) {
                lastModified = jsonFile.lastModified();
                loadItemsFromJson();
                startFileWatcher();
            }
        }
    }

    private void loadItemsFromJson() {
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            StringBuilder jsonDataBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonDataBuilder.append(line);
            }
            parseJson(jsonDataBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String jsonData) {
        items.clear(); // Clear existing items before loading
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("ProductItems");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemObject = jsonArray.getJSONObject(i);
            String name = itemObject.getString("name");
            double price = itemObject.getDouble("price");
            items.add(new Item(name, price));
        }
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(String name, double price) {
        Item newItem = new Item(name, price);
        items.add(newItem);
        writeJson();
    }

    public void addItem(Item newItem) {
        items.add(newItem);
        writeJson();
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            writeJson();
        }
    }

    private void writeJson() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Item item : items) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("name", item.getName());
            itemObject.put("price", item.getPrice());
            jsonArray.put(itemObject);
        }

        jsonObject.put("ProductItems", jsonArray);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
            writer.write(jsonObject.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startFileWatcher() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (jsonFile.lastModified() != lastModified) {
                lastModified = jsonFile.lastModified();
                loadItemsFromJson(); // Reload items if file has changed
                System.out.println("Products updated from JSON file.");
            }
        }, 0, 2, TimeUnit.SECONDS); // Check every 2 seconds
    }
}
