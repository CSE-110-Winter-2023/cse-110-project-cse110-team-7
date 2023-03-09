package com.cse110.team7.socialcompass.server;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.models.LabeledLocation;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Sync local database and server changes for the application
 */
public class LabeledLocationRepository {
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final LabeledLocationDao dao;
    private final Map<String, MutableLiveData<LabeledLocation>> cache;
    private final Map<String, ScheduledFuture<?>> scheduledUpdate;

    public LabeledLocationRepository(LabeledLocationDao dao) {
        this.dao = dao;
        this.cache = new HashMap<>();
        this.scheduledUpdate = new HashMap<>();
    }

    /**
     * Select the labeled location corresponding to the given public code from local database
     *
     * @param publicCode the public code of the labeled location
     * @return labeled location
     */
    public LabeledLocation selectLocalLabeledLocationWithoutLiveData(String publicCode) {
        return dao.selectLabeledLocationWithoutLiveData(publicCode);
    }

    /**
     * Select the labeled location corresponding to the given public code from local database
     *
     * @param publicCode the public code of the labeled location
     * @return labeled location subject
     */
    public LiveData<LabeledLocation> selectLocalLabeledLocation(String publicCode) {
        return dao.selectLabeledLocation(publicCode);
    }

    /**
     * Select all local labeled locations from local database
     *
     * @return labeled locations subject
     */
    public LiveData<List<LabeledLocation>> selectLocalLabeledLocations() {
        return dao.selectLabeledLocations();
    }

    /**
     * Upsert the given labeled location to local database
     *
     * @param labeledLocation the labeled location to be upsert to local database
     */
    public void upsertLocalLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        labeledLocation.setUpdatedAt(Instant.now().getEpochSecond());
        dao.upsertLabeledLocation(labeledLocation);
    }

    /**
     * Delete the given labeled location from local database
     *
     * @param labeledLocation the labeled location to be deleted
     */
    public void deleteLocalLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        dao.deleteLabeledLocation(labeledLocation);
    }

    /**
     * Determine whether a labeled location corresponding to the given public code exists in local database or not
     *
     * @param publicCode the public code of the labeled location
     * @return whether a labeled location corresponding to the given public code exists in local database or not
     */
    public boolean isLocalLabeledLocationExists(@NonNull String publicCode) {
        return dao.isLabeledLocationExists(publicCode);
    }

    /**
     * Select the labeled location corresponding to the given public code from remote server,
     * update the location every fixed interval
     *
     * @param publicCode the public code of the labeled location
     * @return labeled location subject
     */
    public LiveData<LabeledLocation> selectRemoteLabeledLocation(String publicCode) {
        var labeledLocationLiveData = cache.getOrDefault(publicCode, null);

        if (labeledLocationLiveData != null) return labeledLocationLiveData;

        LabeledLocation labeledLocation = null;

        try {
            labeledLocation = ServerAPI.getInstance().asyncGetLabeledLocation(publicCode).get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (labeledLocation == null) {
            labeledLocation = new LabeledLocation.Builder()
                    .setPublicCode(publicCode)
                    .build();
        }

        labeledLocationLiveData = new MutableLiveData<>(labeledLocation);
        cache.put(publicCode, labeledLocationLiveData);

        var scheduledLabeledLocationUpdate = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            var cachedLabeledLocationLiveData = cache.getOrDefault(publicCode, null);
            if (cachedLabeledLocationLiveData == null) return;
            cachedLabeledLocationLiveData.postValue(ServerAPI.getInstance().getLabeledLocation(publicCode));
        }, 0, 3000, TimeUnit.MILLISECONDS);

        scheduledUpdate.put(publicCode, scheduledLabeledLocationUpdate);

        return labeledLocationLiveData;
    }

    /**
     * Upsert the labeled location to remote server
     *
     * @param labeledLocation the labeledLocation to be upsert to remote server
     */
    public void upsertRemoteLabeledLocation(@NonNull LabeledLocation labeledLocation) {
        ServerAPI.getInstance().asyncPutLabeledLocation(labeledLocation);
    }

    /**
     * Delete the labeled location corresponding to the given public code to remote server
     *
     * @param publicCode the public code of the labeled location
     */
    public void deleteRemoteLabeledLocation(@NonNull String publicCode) {
        ServerAPI.getInstance().asyncDeleteLabeledLocation(publicCode);
    }

    /**
     * Select the newest labeled location corresponding to the given public code
     *
     * @param publicCode the public code of the labeled location
     * @return labeled location subject
     */
    public LiveData<LabeledLocation> syncedSelectLabeledLocation(@NonNull String publicCode) {
        var labeledLocation = new MediatorLiveData<LabeledLocation>();

        Observer<LabeledLocation> updateFromRemote = (remoteLabeledLocation) -> {
            var currentLabeledLocation = labeledLocation.getValue();

            if (remoteLabeledLocation == null) return;

            if (currentLabeledLocation == null || currentLabeledLocation.getUpdatedAt() < remoteLabeledLocation.getUpdatedAt()) {
                upsertLocalLabeledLocation(remoteLabeledLocation);
            }
        };

        labeledLocation.addSource(selectLocalLabeledLocation(publicCode), labeledLocation::postValue);
        labeledLocation.addSource(selectRemoteLabeledLocation(publicCode), updateFromRemote);

        return labeledLocation;
    }

    /**
     * Upsert the labeled location to local database and server
     *
     * @param labeledLocation the labeled location to be upsert to local database and server
     */
    public void syncedUpsert(@NonNull LabeledLocation labeledLocation) {
        upsertLocalLabeledLocation(labeledLocation);
        upsertRemoteLabeledLocation(labeledLocation);
    }

    /**
     * Delete the labeled location from local database and server,
     * cancels scheduled update for the given labeled location
     *
     * @param labeledLocation the labeled location to be deleted
     */
    public void syncedDelete(@NonNull LabeledLocation labeledLocation) {
        deleteLocalLabeledLocation(labeledLocation);
        deleteRemoteLabeledLocation(labeledLocation.getPublicCode());

        var scheduledLabeledLocationUpdate = scheduledUpdate.getOrDefault(labeledLocation.getPublicCode(), null);

        if (scheduledLabeledLocationUpdate != null) {
            scheduledLabeledLocationUpdate.cancel(false);
        }
    }
}
