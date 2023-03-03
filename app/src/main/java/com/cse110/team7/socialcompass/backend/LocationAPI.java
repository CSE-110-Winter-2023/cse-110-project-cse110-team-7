package com.cse110.team7.socialcompass.backend;

import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.WorkerThread;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.ServerHouseAdapter;

import java.util.List;

import okhttp3.MediaType;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LocationAPI {
    private volatile static LocationAPI instance = null;

    private OkHttpClient client;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public LocationAPI() {
        this.client = new OkHttpClient();
    }

    public static LocationAPI provide() {
        if (instance == null) {
            instance = new LocationAPI();
        }
        return instance;
    }

    /**
     * An example of sending a GET request to the server.
     * <p>
     * The /echo/{msg} endpoint always just returns {"message": msg}.
     * <p>
     * This method should can be called on a background thread (Android
     * disallows network requests on the main thread).
     */
    @WorkerThread
    public House getHouse(String publicID) {

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("GET", null)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            System.err.println("GET: " + body);
            return ServerHouseAdapter.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @AnyThread
    public Future<House> getHouseAsync(String msg) {
        var executor = Executors.newSingleThreadExecutor();
        var future = executor.submit(() -> getHouse(msg));

        // We can use future.get(1, SECONDS) to wait for the result.
        return future;
    }

    @WorkerThread
    public List<House> getAllHouses() {

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/locations")
                .method("GET", null)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            return ServerHouseAdapter.listFromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @AnyThread
    public Future<List<House>> getAllAsync() {
        var executor = Executors.newSingleThreadExecutor();
        var future = executor.submit(() -> getAllHouses());

        // We can use future.get(1, SECONDS) to wait for the result.
        return future;
    }

    @WorkerThread
    public void putLocation(House house) {
        ServerHouseAdapter severHouse = new ServerHouseAdapter(house);
        String locationJSON = severHouse.toJSON();
        System.err.println(locationJSON);
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = house.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PUT", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            System.err.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AnyThread
    public void putLocationAsync(House house) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> putLocation(house));

    }

    @WorkerThread
    public void deleteHouse(House house) {
        ServerHouseAdapter severHouse = new ServerHouseAdapter(house);
        String deleteJSON = severHouse.deleteJSON();
        RequestBody reqBody = RequestBody.create(deleteJSON, JSON);
        String publicID = house.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("DELETE", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            System.err.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AnyThread
    public void deleteAsync(House house) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> deleteHouse(house));

    }

    @WorkerThread
    public void updateLocation(House house) {
        ServerHouseAdapter severHouse = new ServerHouseAdapter(house);
        String locationJSON = severHouse.patchLocationJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = house.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PATCH", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AnyThread
    public void updateLocationAsync(House house) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> updateLocation(house));

    }

    @WorkerThread
    public void updateName(House house) {
        ServerHouseAdapter severHouse = new ServerHouseAdapter(house);
        String locationJSON = severHouse.patchRenameJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = house.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PATCH", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AnyThread
    public void updateNameAsync(House house) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> updateName(house));

    }

    @WorkerThread
    public void publish(House house, boolean publishStatus) {
        ServerHouseAdapter severHouse = new ServerHouseAdapter(house);
        String locationJSON = severHouse.patchPublishJSON(publishStatus);
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = house.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PATCH", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("PUT NOTE", body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AnyThread
    public void publishAsync(House house, boolean publishStatus) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> publish(house, publishStatus));

    }
}
