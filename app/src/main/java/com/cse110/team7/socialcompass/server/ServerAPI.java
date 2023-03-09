package com.cse110.team7.socialcompass.server;


import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.utils.LabeledLocationDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Handles the communication with the server for the application
 */
public class ServerAPI {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LabeledLocation.class, new LabeledLocationDeserializer())
            .create();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String SERVER_ENDPOINT_LOCATION = "https://socialcompass.goto.ucsd.edu/location";
    private static final String SERVER_ENDPOINT_LOCATIONS = "https://socialcompass.goto.ucsd.edu/locations";
    private volatile static ServerAPI INSTANCE = null;
    private final OkHttpClient client;

    private ServerAPI() {
        this.client = new OkHttpClient();
    }

    public synchronized static ServerAPI getInstance() {
        if (INSTANCE == null) INSTANCE = new ServerAPI();
        return INSTANCE;
    }

    /**
     * Get all published locations that are listed publicly from the server
     *
     * @return all the published locations that are listed publicly
     */
    @NonNull
    @WorkerThread
    public List<LabeledLocation> getLabeledLocations() {
        String endpoint = SERVER_ENDPOINT_LOCATIONS;

        var request = new Request.Builder()
                .url(endpoint)
                .get()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) {
                Log.w(ServerAPI.class.getName(), "response body is null, endpoint = " + endpoint);

                return Collections.emptyList();
            }

            var body = response.body().string();
            List<LabeledLocation> result = GSON.fromJson(body, new TypeToken<List<LabeledLocation>>() {}.getType());

            Log.d(ServerAPI.class.getName(), "got result from server, endpoint = " + endpoint);
            Log.d(ServerAPI.class.getName(), "body = " + body);
            Log.d(ServerAPI.class.getName(), "result = " + result.stream()
                    .map(LabeledLocation::toString)
                    .collect(Collectors.joining(", "))
            );

            return result;
        } catch (Exception exception) {
            Log.e(ServerAPI.class.getName(), "exception occurred, endpoint = " + endpoint);

            exception.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Get the location corresponding to the given public code from the server
     *
     * @param publicCode the public code of the location
     * @return the location stored on server
     */
    @Nullable
    @WorkerThread
    public LabeledLocation getLabeledLocation(@NonNull String publicCode) {
        String endpoint = SERVER_ENDPOINT_LOCATION + "/" + publicCode;

        var request = new Request.Builder()
                .url(endpoint)
                .get()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) {
                Log.w(ServerAPI.class.getName(), "response body is null, endpoint = " + endpoint);

                return null;
            }

            var body = response.body().string();

            var result = GSON.fromJson(body, LabeledLocation.class);

            Log.d(ServerAPI.class.getName(), "got result from server, endpoint = " + endpoint);
            Log.d(ServerAPI.class.getName(), "body = " + body);
            Log.d(ServerAPI.class.getName(), "result = " + result);

            return result;
        } catch (Exception exception) {
            Log.e(ServerAPI.class.getName(), "exception occurred, endpoint = " + endpoint);

            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Upsert the location to server
     *
     * @param labeledLocation the location to be upsert to the server
     * @return the location that is upsert into the server
     */
    @Nullable
    @WorkerThread
    public LabeledLocation putLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        String endpoint = SERVER_ENDPOINT_LOCATION + "/" + labeledLocation.getPublicCode();

        Log.d(ServerAPI.class.getName(), "put with labeled location: " + GSON.toJson(labeledLocation, LabeledLocation.class));

        var request = new Request.Builder()
                .url(endpoint)
                .put(RequestBody.create(GSON.toJson(labeledLocation, LabeledLocation.class), JSON))
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) {
                Log.w(ServerAPI.class.getName(), "response body is null, endpoint = " + endpoint);

                return null;
            }

            var body = response.body().string();
            var result = GSON.fromJson(body, LabeledLocation.class);

            Log.d(ServerAPI.class.getName(), "got result from server, endpoint = " + endpoint);
            Log.d(ServerAPI.class.getName(), "body = " + body);
            Log.d(ServerAPI.class.getName(), "result = " + result);

            return result;
        } catch (Exception exception) {
            Log.e(ServerAPI.class.getName(), "exception occurred, endpoint = " + endpoint);

            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Delete the location corresponding to the public code from the server
     *
     * @param publicCode the public code of the location
     * @return the delete succeeded or not
     */
    @WorkerThread
    public boolean deleteLabeledLocation(@NonNull String publicCode) {
        String endpoint = SERVER_ENDPOINT_LOCATION + "/" + publicCode;

        var request = new Request.Builder()
                .url(endpoint)
                .delete()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) {
                Log.w(ServerAPI.class.getName(), "response body is null, endpoint = " + endpoint);

                return false;
            }

            var body = response.body().string();
            var result = response.isSuccessful();

            Log.d(ServerAPI.class.getName(), "got result from server, endpoint = " + endpoint);
            Log.d(ServerAPI.class.getName(), "body = " + body);
            Log.d(ServerAPI.class.getName(), "result = " + result);

            return result;
        } catch (Exception exception) {
            Log.e(ServerAPI.class.getName(), "exception occurred, endpoint = " + endpoint);

            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Rename, update, or (un)publish location to the server
     *
     * @param labeledLocation the location to be renamed, updated, or (un)published to the server
     * @return the location that is renamed, updated, or (un)published to the server
     */
    @Nullable
    @WorkerThread
    public LabeledLocation patchLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        String endpoint = SERVER_ENDPOINT_LOCATION + "/" + labeledLocation.getPublicCode();

        Log.d(ServerAPI.class.getName(), "patch with labeled location: " + GSON.toJson(labeledLocation, LabeledLocation.class));

        var request = new Request.Builder()
                .url(endpoint)
                .patch(RequestBody.create(GSON.toJson(labeledLocation, LabeledLocation.class), JSON))
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) {
                Log.w(ServerAPI.class.getName(), "response body is null, endpoint = " + endpoint);

                return null;
            }

            var body = response.body().string();
            var result = GSON.fromJson(body, LabeledLocation.class);

            Log.d(ServerAPI.class.getName(), "got result from server, endpoint = " + endpoint);
            Log.d(ServerAPI.class.getName(), "body = " + body);
            Log.d(ServerAPI.class.getName(), "result = " + result);

            return result;
        } catch (Exception exception) {
            Log.e(ServerAPI.class.getName(), "exception occurred, endpoint = " + endpoint);

            exception.printStackTrace();
            return null;
        }
    }

    @AnyThread
    public Future<List<LabeledLocation>> asyncGetLabeledLocations() {
        return EXECUTOR_SERVICE.submit(this::getLabeledLocations);
    }

    @AnyThread
    public Future<LabeledLocation> asyncGetLabeledLocation(@NonNull String publicCode) {
        return EXECUTOR_SERVICE.submit(() -> getLabeledLocation(publicCode));
    }

    @AnyThread
    public Future<LabeledLocation> asyncPutLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        return EXECUTOR_SERVICE.submit(() -> putLabeledLocation(labeledLocation));
    }

    @AnyThread
    public Future<Boolean> asyncDeleteLabeledLocation(@NonNull String publicCode) {
        return EXECUTOR_SERVICE.submit(() -> deleteLabeledLocation(publicCode));
    }

    @AnyThread
    public Future<LabeledLocation> asyncPatchLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        return EXECUTOR_SERVICE.submit(() -> patchLabeledLocation(labeledLocation));
    }
}
