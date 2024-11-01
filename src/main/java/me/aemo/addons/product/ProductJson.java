package me.aemo.addons.product;

import me.aemo.addons.utils.Constants;
import me.aemo.addons.data.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class ProductJson {
    private static final List<Item> items = Collections.synchronizedList(new ArrayList<>());
    private final URL jsonFileUrl;
    private long lastModified;

    public ProductJson() throws IOException {
        jsonFileUrl = ProductJson.class.getClassLoader().getResource(Constants.JSON_FILE_NAME);
        if (jsonFileUrl != null) {
            lastModified = jsonFileUrl.openConnection().getLastModified();
            loadItemsFromJson();
            startFileWatcher();
        } else {
            System.err.println(Constants.JSON_FILE_NAME + " file not found!");
        }
    }

    private void loadItemsFromJson() {
        try (InputStream inputStream = jsonFileUrl.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder jsonDataBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonDataBuilder.append(line);
            }
            parseJson(jsonDataBuilder.toString());
            //JOptionPane.showMessageDialog(null, jsonDataBuilder.toString());
        } catch (IOException e) {
            System.err.println("Failed to load items from JSON: " + e.getMessage());
        }
    }


    private void parseJson(String jsonData) {
        items.clear();
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
        addItem(new Item(name, price));
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
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(jsonFileUrl.getPath())), StandardCharsets.UTF_8))) {
            writer.write(jsonObject.toString(4));
        } catch (IOException e) {
            System.err.println("Failed to write items to JSON: " + e.getMessage());
        }
    }


    private void startFileWatcher() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                long currentModified = jsonFileUrl.openConnection().getLastModified();
                if (currentModified != lastModified) {
                    lastModified = currentModified;
                    loadItemsFromJson();
                    System.out.println("Products updated from JSON file.");
                }
            } catch (IOException e) {
                System.err.println("File watcher encountered an error: " + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
