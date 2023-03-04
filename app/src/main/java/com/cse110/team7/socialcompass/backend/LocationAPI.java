package com.cse110.team7.socialcompass.backend;

import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.WorkerThread;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.ServerFriendAdapter;

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
     *
     * The /echo/{msg} endpoint always just returns {"message": msg}.
     *
     * This method should can be called on a background thread (Android
     * disallows network requests on the main thread).
     */
    @WorkerThread
    public FriendAccount getFriend(String publicID) {

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("GET", null)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("GET",  body);
            return ServerFriendAdapter.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @AnyThread
    public Future<FriendAccount> getFriendAsync(String msg) {
        var executor = Executors.newSingleThreadExecutor();
        var future = executor.submit(() -> getFriend(msg));

        // We can use future.get(1, SECONDS) to wait for the result.
        return future;
    }

    /**
     * Calls the API's GET Function @ in order to pull
     * all of the friend accounts which have been publically published @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     *
     * (This Function Likely Won't Be Used)
     *
     * @return List of all Friends
     */
    @WorkerThread
    public List<FriendAccount> getAllFriends() {

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/locations")
                .method("GET", null)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            return ServerFriendAdapter.listFromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @AnyThread
    public Future<List<FriendAccount>> getAllAsync() {
        var executor = Executors.newSingleThreadExecutor();
        var future = executor.submit(() -> getAllFriends());

        // We can use future.get(1, SECONDS) to wait for the result.
        return future;
    }

    /**
     * Calls the PUT Function of the API @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     * and allows the user to add their associated label to the server.
     *
     * Uses private code to input the information and authorize the owner of the location to update
     * their own user information.
     *
     * @param friendAccount - The friend account to be added to the server.
     */
    @WorkerThread
    public void putLocation(FriendAccount friendAccount) {
        ServerFriendAdapter serverFriend = new ServerFriendAdapter(friendAccount);
        String locationJSON = serverFriend.toJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = friendAccount.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PUT", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("PUT",  body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AnyThread
    public void putLocationAsync(FriendAccount friendAccount) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> putLocation(friendAccount));

    }


    /**
     * Calls the DELETE Function of the API @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     * and allows for the deletion of a friend (this would primarily be used for testing).
     *
     * Uses private code to input the information and authorize the owner of the location to update
     * their own user information.
     *
     * @param friendAccount - The friend account to be deleted.
     */
    @WorkerThread
    public void deleteFriend(FriendAccount friendAccount) {
        ServerFriendAdapter serverFriend = new ServerFriendAdapter(friendAccount);
        String deleteJSON = serverFriend.deleteJSON();
        RequestBody reqBody = RequestBody.create(deleteJSON, JSON);
        String publicID = friendAccount.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("DELETE", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("DELETE",  body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AnyThread
    public void deleteAsync(FriendAccount friendAccount) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> deleteFriend(friendAccount));

    }

    /**
     * Calls the Patch Function of the API @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     * and allows the user to update their location to the server.
     *
     * Uses private code to input the information and authorize the owner of the location to update
     * their own user information.
     *
     * @param friendAccount - The friend account to have its location updated.
     */
    @WorkerThread
    public void updateLocation(FriendAccount friendAccount) {
        ServerFriendAdapter serverFriend = new ServerFriendAdapter(friendAccount);
        String locationJSON = serverFriend.patchLocationJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = friendAccount.getPublicID();
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + publicID)
                .method("PATCH", reqBody)
                .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("UPDATE LOC", body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AnyThread
    public void updateLocationAsync(FriendAccount friendAccount) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> updateLocation(friendAccount));

    }

    /**
     * Calls the Patch Function on the API @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     * and allows the user to rename their label.
     *
     * Uses private code to input the information and authorize the owner of the location to update
     * their own user information.
     *
     * @param friendAccount - The friend account to have its name updated.
     */
    @WorkerThread
    public void updateName(FriendAccount friendAccount) {
        ServerFriendAdapter serverFriend = new ServerFriendAdapter(friendAccount);
        String locationJSON = serverFriend.patchRenameJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = friendAccount.getPublicID();
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
    public void updateNameAsync(FriendAccount friendAccount) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> updateName(friendAccount));

    }

    /**
     * Calls the API's Patch Function, as described @
     * https://socialcompass.goto.ucsd.edu/docs#/default/put_location_location__public_code__put
     * and allows the user to "publish a location at a public code," so that it is publically
     * available.
     *
     * Uses private code to input the information and authorize the owner of the location to update
     * their own user information.
     *
     * @param friendAccount - The friend account to be published.
     */
    @WorkerThread
    public void publish(FriendAccount friendAccount) {
        ServerFriendAdapter serverFriend = new ServerFriendAdapter(friendAccount);
        String locationJSON = serverFriend.patchPublishJSON();
        RequestBody reqBody = RequestBody.create(locationJSON, JSON);
        String publicID = friendAccount.getPublicID();
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
    public void publishAsync(FriendAccount friendAccount) {
        var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> publish(friendAccount));

    }
}
