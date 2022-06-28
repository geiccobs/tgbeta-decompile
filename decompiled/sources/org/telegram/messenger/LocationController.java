package org.telegram.messenger;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.audio.SilenceSkippingAudioProcessor;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class LocationController extends BaseController implements NotificationCenter.NotificationCenterDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int BACKGROUD_UPDATE_TIME = 30000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SEND_NEW_LOCATION_TIME = 2000;
    private static final long UPDATE_INTERVAL = 1000;
    private static final int WATCH_LOCATION_TIMEOUT = 65000;
    private Location lastKnownLocation;
    private boolean lastLocationByGoogleMaps;
    private long lastLocationSendTime;
    private long lastLocationStartTime;
    private long locationEndWatchTime;
    private LocationRequest locationRequest;
    private boolean lookingForPeopleNearby;
    private Boolean playServicesAvailable;
    private boolean shareMyCurrentLocation;
    private boolean started;
    private boolean wasConnectedToPlayServices;
    private static volatile LocationController[] Instance = new LocationController[4];
    private static HashMap<LocationFetchCallback, Runnable> callbacks = new HashMap<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray<>();
    private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList<>();
    public LongSparseArray<ArrayList<TLRPC.Message>> locationsCache = new LongSparseArray<>();
    private LongSparseArray<Integer> lastReadLocationTime = new LongSparseArray<>();
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    private GpsLocationListener passiveLocationListener = new GpsLocationListener();
    private FusedLocationListener fusedLocationListener = new FusedLocationListener();
    private boolean locationSentSinceLastGoogleMapUpdate = true;
    private SparseIntArray requests = new SparseIntArray();
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray<>();
    public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyUsers = new ArrayList<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyChats = new ArrayList<>();
    private LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
    private GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ApplicationLoader.applicationContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    /* loaded from: classes4.dex */
    public interface LocationFetchCallback {
        void onLocationAddressAvailable(String str, String str2, Location location);
    }

    /* loaded from: classes4.dex */
    public static class SharingLocationInfo {
        public int account;
        public long did;
        public int lastSentProximityMeters;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int proximityMeters;
        public int stopTime;
    }

    public static LocationController getInstance(int num) {
        LocationController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (LocationController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    LocationController[] locationControllerArr = Instance;
                    LocationController locationController = new LocationController(num);
                    localInstance = locationController;
                    locationControllerArr[num] = locationController;
                }
            }
        }
        return localInstance;
    }

    /* loaded from: classes4.dex */
    public class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
            LocationController.this = r1;
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            if (location != null) {
                if (LocationController.this.lastKnownLocation == null || (this != LocationController.this.networkLocationListener && this != LocationController.this.passiveLocationListener)) {
                    LocationController.this.setLastKnownLocation(location);
                } else if (!LocationController.this.started && location.distanceTo(LocationController.this.lastKnownLocation) > 20.0f) {
                    LocationController.this.setLastKnownLocation(location);
                    LocationController.this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                }
            }
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
        }
    }

    /* loaded from: classes4.dex */
    public class FusedLocationListener implements com.google.android.gms.location.LocationListener {
        private FusedLocationListener() {
            LocationController.this = r1;
        }

        @Override // com.google.android.gms.location.LocationListener
        public void onLocationChanged(Location location) {
            if (location != null) {
                LocationController.this.setLastKnownLocation(location);
            }
        }
    }

    public LocationController(int instance) {
        super(instance);
        LocationRequest locationRequest = new LocationRequest();
        this.locationRequest = locationRequest;
        locationRequest.setPriority(100);
        this.locationRequest.setInterval(1000L);
        this.locationRequest.setFastestInterval(1000L);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m347lambda$new$0$orgtelegrammessengerLocationController();
            }
        });
        loadSharingLocations();
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-LocationController */
    public /* synthetic */ void m347lambda$new$0$orgtelegrammessengerLocationController() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ArrayList<TLRPC.Message> messages;
        ArrayList<TLRPC.Message> messages2;
        if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled) {
                return;
            }
            long did = ((Long) args[0]).longValue();
            if (!isSharingLocation(did) || (messages2 = this.locationsCache.get(did)) == null) {
                return;
            }
            ArrayList<MessageObject> arr = (ArrayList) args[1];
            boolean added = false;
            for (int a = 0; a < arr.size(); a++) {
                MessageObject messageObject = arr.get(a);
                if (messageObject.isLiveLocation()) {
                    added = true;
                    boolean replaced = false;
                    int b = 0;
                    while (true) {
                        if (b < messages2.size()) {
                            if (MessageObject.getFromChatId(messages2.get(b)) != messageObject.getFromChatId()) {
                                b++;
                            } else {
                                replaced = true;
                                messages2.set(b, messageObject.messageOwner);
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (!replaced) {
                        messages2.add(messageObject.messageOwner);
                    }
                } else if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) {
                    long dialogId = messageObject.getDialogId();
                    if (DialogObject.isUserDialog(dialogId)) {
                        setProximityLocation(dialogId, 0, false);
                    }
                }
            }
            if (added) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            boolean scheduled2 = ((Boolean) args[2]).booleanValue();
            if (!scheduled2 && !this.sharingLocationsUI.isEmpty()) {
                ArrayList<Integer> markAsDeletedMessages = (ArrayList) args[0];
                long channelId = ((Long) args[1]).longValue();
                ArrayList<Long> toRemove = null;
                for (int a2 = 0; a2 < this.sharingLocationsUI.size(); a2++) {
                    SharingLocationInfo info = this.sharingLocationsUI.get(a2);
                    long messageChannelId = info.messageObject != null ? info.messageObject.getChannelId() : 0L;
                    if (channelId == messageChannelId && markAsDeletedMessages.contains(Integer.valueOf(info.mid))) {
                        if (toRemove == null) {
                            toRemove = new ArrayList<>();
                        }
                        toRemove.add(Long.valueOf(info.did));
                    }
                }
                if (toRemove != null) {
                    for (int a3 = 0; a3 < toRemove.size(); a3++) {
                        removeSharingLocation(toRemove.get(a3).longValue());
                    }
                }
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            long did2 = ((Long) args[0]).longValue();
            if (!isSharingLocation(did2) || (messages = this.locationsCache.get(did2)) == null) {
                return;
            }
            boolean updated = false;
            ArrayList<MessageObject> messageObjects = (ArrayList) args[1];
            for (int a4 = 0; a4 < messageObjects.size(); a4++) {
                MessageObject messageObject2 = messageObjects.get(a4);
                int b2 = 0;
                while (true) {
                    if (b2 >= messages.size()) {
                        break;
                    } else if (MessageObject.getFromChatId(messages.get(b2)) != messageObject2.getFromChatId()) {
                        b2++;
                    } else {
                        if (!messageObject2.isLiveLocation()) {
                            messages.remove(b2);
                        } else {
                            messages.set(b2, messageObject2.messageOwner);
                        }
                        updated = true;
                    }
                }
            }
            if (updated) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did2), Integer.valueOf(this.currentAccount));
            }
        }
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        this.wasConnectedToPlayServices = true;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(this.locationRequest);
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda0
                    @Override // com.google.android.gms.common.api.ResultCallback
                    public final void onResult(Result result2) {
                        LocationController.this.m351lambda$onConnected$4$orgtelegrammessengerLocationController((LocationSettingsResult) result2);
                    }
                });
            } else {
                startFusedLocationRequest(true);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$onConnected$4$org-telegram-messenger-LocationController */
    public /* synthetic */ void m351lambda$onConnected$4$orgtelegrammessengerLocationController(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case 0:
                startFusedLocationRequest(true);
                return;
            case 6:
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocationController.this.m349lambda$onConnected$2$orgtelegrammessengerLocationController(status);
                    }
                });
                return;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE /* 8502 */:
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocationController.this.m350lambda$onConnected$3$orgtelegrammessengerLocationController();
                    }
                });
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$onConnected$2$org-telegram-messenger-LocationController */
    public /* synthetic */ void m349lambda$onConnected$2$orgtelegrammessengerLocationController(final Status status) {
        if (this.lookingForPeopleNearby || !this.sharingLocations.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LocationController.this.m348lambda$onConnected$1$orgtelegrammessengerLocationController(status);
                }
            });
        }
    }

    /* renamed from: lambda$onConnected$1$org-telegram-messenger-LocationController */
    public /* synthetic */ void m348lambda$onConnected$1$orgtelegrammessengerLocationController(Status status) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowPlayServicesAlert, status);
    }

    /* renamed from: lambda$onConnected$3$org-telegram-messenger-LocationController */
    public /* synthetic */ void m350lambda$onConnected$3$orgtelegrammessengerLocationController() {
        this.playServicesAvailable = false;
        try {
            this.googleApiClient.disconnect();
            start();
        } catch (Throwable th) {
        }
    }

    public void startFusedLocationRequest(final boolean permissionsGranted) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m361x23adf777(permissionsGranted);
            }
        });
    }

    /* renamed from: lambda$startFusedLocationRequest$5$org-telegram-messenger-LocationController */
    public /* synthetic */ void m361x23adf777(boolean permissionsGranted) {
        if (!permissionsGranted) {
            this.playServicesAvailable = false;
        }
        if (this.shareMyCurrentLocation || this.lookingForPeopleNearby || !this.sharingLocations.isEmpty()) {
            if (permissionsGranted) {
                try {
                    setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient));
                    LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this.fusedLocationListener);
                    return;
                } catch (Throwable e) {
                    FileLog.e(e);
                    return;
                }
            }
            start();
        }
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public void onConnectionSuspended(int i) {
    }

    @Override // com.google.android.gms.common.api.internal.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (this.wasConnectedToPlayServices) {
            return;
        }
        this.playServicesAvailable = false;
        if (this.started) {
            this.started = false;
            start();
        }
    }

    private boolean checkPlayServices() {
        if (this.playServicesAvailable == null) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(ApplicationLoader.applicationContext);
            this.playServicesAvailable = Boolean.valueOf(resultCode == 0);
        }
        return this.playServicesAvailable.booleanValue();
    }

    private void broadcastLastKnownLocation(boolean cancelCurrent) {
        if (this.lastKnownLocation == null) {
            return;
        }
        if (this.requests.size() != 0) {
            if (cancelCurrent) {
                for (int a = 0; a < this.requests.size(); a++) {
                    getConnectionsManager().cancelRequest(this.requests.keyAt(a), false);
                }
            }
            this.requests.clear();
        }
        if (!this.sharingLocations.isEmpty()) {
            int date = getConnectionsManager().getCurrentTime();
            float[] result = new float[1];
            for (int a2 = 0; a2 < this.sharingLocations.size(); a2++) {
                final SharingLocationInfo info = this.sharingLocations.get(a2);
                if (info.messageObject.messageOwner.media != null && info.messageObject.messageOwner.media.geo != null && info.lastSentProximityMeters == info.proximityMeters) {
                    int messageDate = info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date;
                    TLRPC.GeoPoint point = info.messageObject.messageOwner.media.geo;
                    if (Math.abs(date - messageDate) < 10) {
                        Location.distanceBetween(point.lat, point._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), result);
                        if (result[0] < 1.0f) {
                        }
                    }
                }
                final TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
                req.peer = getMessagesController().getInputPeer(info.did);
                req.id = info.mid;
                req.flags |= 16384;
                req.media = new TLRPC.TL_inputMediaGeoLive();
                req.media.stopped = false;
                req.media.geo_point = new TLRPC.TL_inputGeoPoint();
                req.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                req.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                req.media.geo_point.accuracy_radius = (int) this.lastKnownLocation.getAccuracy();
                if (req.media.geo_point.accuracy_radius != 0) {
                    req.media.geo_point.flags |= 1;
                }
                if (info.lastSentProximityMeters != info.proximityMeters) {
                    req.media.proximity_notification_radius = info.proximityMeters;
                    req.media.flags |= 8;
                }
                req.media.heading = getHeading(this.lastKnownLocation);
                req.media.flags |= 4;
                final int[] reqId = {getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda23
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocationController.this.m338xd8307cfb(info, reqId, req, tLObject, tL_error);
                    }
                })};
                this.requests.put(reqId[0], 0);
            }
        }
        if (this.shareMyCurrentLocation) {
            UserConfig userConfig = getUserConfig();
            userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
            userConfig.saveConfig(false);
            TLRPC.TL_contacts_getLocated req2 = new TLRPC.TL_contacts_getLocated();
            req2.geo_point = new TLRPC.TL_inputGeoPoint();
            req2.geo_point.lat = this.lastKnownLocation.getLatitude();
            req2.geo_point._long = this.lastKnownLocation.getLongitude();
            req2.background = true;
            getConnectionsManager().sendRequest(req2, LocationController$$ExternalSyntheticLambda24.INSTANCE);
        }
        getConnectionsManager().resumeNetworkMaybe();
        if (shouldStopGps() || this.shareMyCurrentLocation) {
            this.shareMyCurrentLocation = false;
            stop(false);
        }
    }

    /* renamed from: lambda$broadcastLastKnownLocation$7$org-telegram-messenger-LocationController */
    public /* synthetic */ void m338xd8307cfb(final SharingLocationInfo info, int[] reqId, TLRPC.TL_messages_editMessage req, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            if (error.text.equals("MESSAGE_ID_INVALID")) {
                this.sharingLocations.remove(info);
                this.sharingLocationsMap.remove(info.did);
                saveSharingLocation(info, 1);
                this.requests.delete(reqId[0]);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocationController.this.m337xf2ef0e3a(info);
                    }
                });
                return;
            }
            return;
        }
        if ((req.flags & 8) != 0) {
            info.lastSentProximityMeters = req.media.proximity_notification_radius;
        }
        TLRPC.Updates updates = (TLRPC.Updates) response;
        boolean updated = false;
        for (int a1 = 0; a1 < updates.updates.size(); a1++) {
            TLRPC.Update update = updates.updates.get(a1);
            if (update instanceof TLRPC.TL_updateEditMessage) {
                updated = true;
                info.messageObject.messageOwner = ((TLRPC.TL_updateEditMessage) update).message;
            } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                updated = true;
                info.messageObject.messageOwner = ((TLRPC.TL_updateEditChannelMessage) update).message;
            }
        }
        if (updated) {
            saveSharingLocation(info, 0);
        }
        getMessagesController().processUpdates(updates, false);
    }

    /* renamed from: lambda$broadcastLastKnownLocation$6$org-telegram-messenger-LocationController */
    public /* synthetic */ void m337xf2ef0e3a(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public static /* synthetic */ void lambda$broadcastLastKnownLocation$8(TLObject response, TLRPC.TL_error error) {
    }

    private boolean shouldStopGps() {
        return SystemClock.elapsedRealtime() > this.locationEndWatchTime;
    }

    public void setNewLocationEndWatchTime() {
        if (this.sharingLocations.isEmpty()) {
            return;
        }
        this.locationEndWatchTime = SystemClock.elapsedRealtime() + 65000;
        start();
    }

    public void update() {
        UserConfig userConfig = getUserConfig();
        boolean z = true;
        if (ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePaused && !this.shareMyCurrentLocation && userConfig.isClientActivated() && userConfig.isConfigLoaded() && userConfig.sharingMyLocationUntil != 0 && Math.abs((System.currentTimeMillis() / 1000) - userConfig.lastMyLocationShareTime) >= 3600) {
            this.shareMyCurrentLocation = true;
        }
        if (!this.sharingLocations.isEmpty()) {
            int a = 0;
            while (a < this.sharingLocations.size()) {
                final SharingLocationInfo info = this.sharingLocations.get(a);
                int currentTime = getConnectionsManager().getCurrentTime();
                if (info.stopTime <= currentTime) {
                    this.sharingLocations.remove(a);
                    this.sharingLocationsMap.remove(info.did);
                    saveSharingLocation(info, 1);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            LocationController.this.m363lambda$update$9$orgtelegrammessengerLocationController(info);
                        }
                    });
                    a--;
                }
                a++;
            }
        }
        if (this.started) {
            long newTime = SystemClock.elapsedRealtime();
            if (this.lastLocationByGoogleMaps || Math.abs(this.lastLocationStartTime - newTime) > 10000 || shouldSendLocationNow()) {
                this.lastLocationByGoogleMaps = false;
                this.locationSentSinceLastGoogleMapUpdate = true;
                if (SystemClock.elapsedRealtime() - this.lastLocationSendTime <= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                    z = false;
                }
                boolean cancelAll = z;
                this.lastLocationStartTime = newTime;
                this.lastLocationSendTime = SystemClock.elapsedRealtime();
                broadcastLastKnownLocation(cancelAll);
            }
        } else if (!this.sharingLocations.isEmpty() || this.shareMyCurrentLocation) {
            if (this.shareMyCurrentLocation || Math.abs(this.lastLocationSendTime - SystemClock.elapsedRealtime()) > 30000) {
                this.lastLocationStartTime = SystemClock.elapsedRealtime();
                start();
            }
        }
    }

    /* renamed from: lambda$update$9$org-telegram-messenger-LocationController */
    public /* synthetic */ void m363lambda$update$9$orgtelegrammessengerLocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private boolean shouldSendLocationNow() {
        return shouldStopGps() && Math.abs(this.lastLocationSendTime - SystemClock.elapsedRealtime()) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
    }

    public void cleanup() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        this.locationsCache.clear();
        this.cacheRequests.clear();
        this.cachedNearbyUsers.clear();
        this.cachedNearbyChats.clear();
        this.lastReadLocationTime.clear();
        stopService();
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m339lambda$cleanup$10$orgtelegrammessengerLocationController();
            }
        });
    }

    /* renamed from: lambda$cleanup$10$org-telegram-messenger-LocationController */
    public /* synthetic */ void m339lambda$cleanup$10$orgtelegrammessengerLocationController() {
        this.locationEndWatchTime = 0L;
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        setLastKnownLocation(null);
        stop(true);
    }

    public void setLastKnownLocation(Location location) {
        if (location != null && Build.VERSION.SDK_INT >= 17 && (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / C.NANOS_PER_SECOND > 300) {
            return;
        }
        this.lastKnownLocation = location;
        if (location != null) {
            AndroidUtilities.runOnUIThread(LocationController$$ExternalSyntheticLambda17.INSTANCE);
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TLRPC.TL_peerLocated> u, ArrayList<TLRPC.TL_peerLocated> c) {
        this.cachedNearbyUsers = new ArrayList<>(u);
        this.cachedNearbyChats = new ArrayList<>(c);
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    public void addSharingLocation(TLRPC.Message message) {
        final SharingLocationInfo info = new SharingLocationInfo();
        info.did = message.dialog_id;
        info.mid = message.id;
        info.period = message.media.period;
        int i = message.media.proximity_notification_radius;
        info.proximityMeters = i;
        info.lastSentProximityMeters = i;
        info.account = this.currentAccount;
        info.messageObject = new MessageObject(this.currentAccount, message, false, false);
        info.stopTime = getConnectionsManager().getCurrentTime() + info.period;
        final SharingLocationInfo old = this.sharingLocationsMap.get(info.did);
        this.sharingLocationsMap.put(info.did, info);
        if (old != null) {
            this.sharingLocations.remove(old);
        }
        this.sharingLocations.add(info);
        saveSharingLocation(info, 0);
        this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m336x90698a22(old, info);
            }
        });
    }

    /* renamed from: lambda$addSharingLocation$12$org-telegram-messenger-LocationController */
    public /* synthetic */ void m336x90698a22(SharingLocationInfo old, SharingLocationInfo info) {
        if (old != null) {
            this.sharingLocationsUI.remove(old);
        }
        this.sharingLocationsUI.add(info);
        this.sharingLocationsMapUI.put(info.did, info);
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public boolean isSharingLocation(long did) {
        return this.sharingLocationsMapUI.indexOfKey(did) >= 0;
    }

    public SharingLocationInfo getSharingLocationInfo(long did) {
        return this.sharingLocationsMapUI.get(did);
    }

    public boolean setProximityLocation(final long did, final int meters, boolean broadcast) {
        SharingLocationInfo info = this.sharingLocationsMapUI.get(did);
        if (info != null) {
            info.proximityMeters = meters;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m359x2ff08821(meters, did);
            }
        });
        if (broadcast) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    LocationController.this.m360x1531f6e2();
                }
            });
        }
        return info != null;
    }

    /* renamed from: lambda$setProximityLocation$13$org-telegram-messenger-LocationController */
    public /* synthetic */ void m359x2ff08821(int meters, long did) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("UPDATE sharing_locations SET proximity = ? WHERE uid = ?");
            state.requery();
            state.bindInteger(1, meters);
            state.bindLong(2, did);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$setProximityLocation$14$org-telegram-messenger-LocationController */
    public /* synthetic */ void m360x1531f6e2() {
        broadcastLastKnownLocation(true);
    }

    public static int getHeading(Location location) {
        float val = location.getBearing();
        if (val > 0.0f && val < 1.0f) {
            if (val < 0.5f) {
                return 360;
            }
            return 1;
        }
        return (int) val;
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m345x8b5ab3d0();
            }
        });
    }

    /* renamed from: lambda$loadSharingLocations$18$org-telegram-messenger-LocationController */
    public /* synthetic */ void m345x8b5ab3d0() {
        final ArrayList<SharingLocationInfo> result = new ArrayList<>();
        final ArrayList<TLRPC.User> users = new ArrayList<>();
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT uid, mid, date, period, message, proximity FROM sharing_locations WHERE 1", new Object[0]);
            while (cursor.next()) {
                SharingLocationInfo info = new SharingLocationInfo();
                info.did = cursor.longValue(0);
                info.mid = cursor.intValue(1);
                info.stopTime = cursor.intValue(2);
                info.period = cursor.intValue(3);
                info.proximityMeters = cursor.intValue(5);
                info.account = this.currentAccount;
                NativeByteBuffer data = cursor.byteBufferValue(4);
                if (data != null) {
                    info.messageObject = new MessageObject(this.currentAccount, TLRPC.Message.TLdeserialize(data, data.readInt32(false), false), false, false);
                    MessagesStorage.addUsersAndChatsFromMessage(info.messageObject.messageOwner, usersToLoad, chatsToLoad);
                    data.reuse();
                }
                result.add(info);
                if (DialogObject.isChatDialog(info.did)) {
                    if (!chatsToLoad.contains(Long.valueOf(-info.did))) {
                        chatsToLoad.add(Long.valueOf(-info.did));
                    }
                } else if (DialogObject.isUserDialog(info.did) && !usersToLoad.contains(Long.valueOf(info.did))) {
                    usersToLoad.add(Long.valueOf(info.did));
                }
            }
            cursor.dispose();
            if (!chatsToLoad.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!result.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LocationController.this.m344xa619450f(users, chats, result);
                }
            });
        }
    }

    /* renamed from: lambda$loadSharingLocations$17$org-telegram-messenger-LocationController */
    public /* synthetic */ void m344xa619450f(ArrayList users, ArrayList chats, final ArrayList result) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m343xc0d7d64e(result);
            }
        });
    }

    /* renamed from: lambda$loadSharingLocations$16$org-telegram-messenger-LocationController */
    public /* synthetic */ void m343xc0d7d64e(final ArrayList result) {
        this.sharingLocations.addAll(result);
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            this.sharingLocationsMap.put(info.did, info);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m342xdb96678d(result);
            }
        });
    }

    /* renamed from: lambda$loadSharingLocations$15$org-telegram-messenger-LocationController */
    public /* synthetic */ void m342xdb96678d(ArrayList result) {
        this.sharingLocationsUI.addAll(result);
        for (int a = 0; a < result.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) result.get(a);
            this.sharingLocationsMapUI.put(info.did, info);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(final SharingLocationInfo info, final int remove) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m358xea365e5f(remove, info);
            }
        });
    }

    /* renamed from: lambda$saveSharingLocation$19$org-telegram-messenger-LocationController */
    public /* synthetic */ void m358xea365e5f(int remove, SharingLocationInfo info) {
        try {
            if (remove == 2) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } else if (remove == 1) {
                if (info == null) {
                    return;
                }
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                database.executeFast("DELETE FROM sharing_locations WHERE uid = " + info.did).stepThis().dispose();
            } else if (info == null) {
            } else {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(info.messageObject.messageOwner.getObjectSize());
                info.messageObject.messageOwner.serializeToStream(data);
                state.bindLong(1, info.did);
                state.bindInteger(2, info.mid);
                state.bindInteger(3, info.stopTime);
                state.bindInteger(4, info.period);
                state.bindByteBuffer(5, data);
                state.bindInteger(6, info.proximityMeters);
                state.step();
                state.dispose();
                data.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void removeSharingLocation(final long did) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m357xf9667fbe(did);
            }
        });
    }

    /* renamed from: lambda$removeSharingLocation$22$org-telegram-messenger-LocationController */
    public /* synthetic */ void m357xf9667fbe(long did) {
        final SharingLocationInfo info = this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.remove(did);
        if (info != null) {
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer(info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda20
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.m355x2ee3a23c(tLObject, tL_error);
                }
            });
            this.sharingLocations.remove(info);
            saveSharingLocation(info, 1);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LocationController.this.m356x142510fd(info);
                }
            });
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    /* renamed from: lambda$removeSharingLocation$20$org-telegram-messenger-LocationController */
    public /* synthetic */ void m355x2ee3a23c(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
    }

    /* renamed from: lambda$removeSharingLocation$21$org-telegram-messenger-LocationController */
    public /* synthetic */ void m356x142510fd(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void startService() {
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void stopService() {
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
    }

    public void removeAllLocationSharings() {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m354x31cdd005();
            }
        });
    }

    /* renamed from: lambda$removeAllLocationSharings$25$org-telegram-messenger-LocationController */
    public /* synthetic */ void m354x31cdd005() {
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer(info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda19
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.m352x674af283(tLObject, tL_error);
                }
            });
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation(null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m353x4c8c6144();
            }
        });
    }

    /* renamed from: lambda$removeAllLocationSharings$23$org-telegram-messenger-LocationController */
    public /* synthetic */ void m352x674af283(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
    }

    /* renamed from: lambda$removeAllLocationSharings$24$org-telegram-messenger-LocationController */
    public /* synthetic */ void m353x4c8c6144() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setGoogleMapLocation(Location location, boolean first) {
        Location location2;
        if (location == null) {
            return;
        }
        this.lastLocationByGoogleMaps = true;
        if (first || ((location2 = this.lastKnownLocation) != null && location2.distanceTo(location) >= 20.0f)) {
            this.lastLocationSendTime = SystemClock.elapsedRealtime() - 30000;
            this.locationSentSinceLastGoogleMapUpdate = false;
        } else if (this.locationSentSinceLastGoogleMapUpdate) {
            this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + SilenceSkippingAudioProcessor.DEFAULT_PADDING_SILENCE_US;
            this.locationSentSinceLastGoogleMapUpdate = false;
        }
        setLastKnownLocation(location);
    }

    private void start() {
        if (this.started) {
            return;
        }
        this.lastLocationStartTime = SystemClock.elapsedRealtime();
        this.started = true;
        boolean ok = false;
        if (checkPlayServices()) {
            try {
                this.googleApiClient.connect();
                ok = true;
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (!ok) {
            try {
                this.locationManager.requestLocationUpdates("gps", 1L, 0.0f, this.gpsLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1L, 0.0f, this.networkLocationListener);
            } catch (Exception e3) {
                FileLog.e(e3);
            }
            try {
                this.locationManager.requestLocationUpdates("passive", 1L, 0.0f, this.passiveLocationListener);
            } catch (Exception e4) {
                FileLog.e(e4);
            }
            if (this.lastKnownLocation == null) {
                try {
                    setLastKnownLocation(this.locationManager.getLastKnownLocation("gps"));
                    if (this.lastKnownLocation == null) {
                        setLastKnownLocation(this.locationManager.getLastKnownLocation("network"));
                    }
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
        }
    }

    private void stop(boolean empty) {
        if (this.lookingForPeopleNearby || this.shareMyCurrentLocation) {
            return;
        }
        this.started = false;
        if (checkPlayServices()) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this.fusedLocationListener);
                this.googleApiClient.disconnect();
            } catch (Throwable e) {
                FileLog.e(e, false);
            }
        }
        this.locationManager.removeUpdates(this.gpsLocationListener);
        if (empty) {
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.locationManager.removeUpdates(this.passiveLocationListener);
        }
    }

    public void startLocationLookupForPeopleNearby(final boolean stop) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m362x93ad6f8f(stop);
            }
        });
    }

    /* renamed from: lambda$startLocationLookupForPeopleNearby$26$org-telegram-messenger-LocationController */
    public /* synthetic */ void m362x93ad6f8f(boolean stop) {
        boolean z = !stop;
        this.lookingForPeopleNearby = z;
        if (z) {
            start();
        } else if (this.sharingLocations.isEmpty()) {
            stop(true);
        }
    }

    public Location getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void loadLiveLocations(final long did) {
        if (this.cacheRequests.indexOfKey(did) >= 0) {
            return;
        }
        this.cacheRequests.put(did, true);
        TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
        req.peer = getMessagesController().getInputPeer(did);
        req.limit = 100;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda21
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationController.this.m341x22591025(did, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadLiveLocations$28$org-telegram-messenger-LocationController */
    public /* synthetic */ void m341x22591025(final long did, final TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.this.m340x3d17a164(did, response);
            }
        });
    }

    /* renamed from: lambda$loadLiveLocations$27$org-telegram-messenger-LocationController */
    public /* synthetic */ void m340x3d17a164(long did, TLObject response) {
        this.cacheRequests.delete(did);
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        int a = 0;
        while (a < res.messages.size()) {
            if (!(res.messages.get(a).media instanceof TLRPC.TL_messageMediaGeoLive)) {
                res.messages.remove(a);
                a--;
            }
            a++;
        }
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        getMessagesController().putUsers(res.users, false);
        getMessagesController().putChats(res.chats, false);
        this.locationsCache.put(did, res.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void markLiveLoactionsAsRead(long dialogId) {
        ArrayList<TLRPC.Message> messages;
        TLObject request;
        if (DialogObject.isEncryptedDialog(dialogId) || (messages = this.locationsCache.get(dialogId)) == null || messages.isEmpty()) {
            return;
        }
        Integer date = this.lastReadLocationTime.get(dialogId);
        int currentDate = (int) (SystemClock.elapsedRealtime() / 1000);
        if (date != null && date.intValue() + 60 > currentDate) {
            return;
        }
        this.lastReadLocationTime.put(dialogId, Integer.valueOf(currentDate));
        if (DialogObject.isChatDialog(dialogId) && ChatObject.isChannel(-dialogId, this.currentAccount)) {
            TLRPC.TL_channels_readMessageContents req = new TLRPC.TL_channels_readMessageContents();
            int N = messages.size();
            for (int a = 0; a < N; a++) {
                req.id.add(Integer.valueOf(messages.get(a).id));
            }
            req.channel = getMessagesController().getInputChannel(-dialogId);
            request = req;
        } else {
            TLRPC.TL_messages_readMessageContents req2 = new TLRPC.TL_messages_readMessageContents();
            int N2 = messages.size();
            for (int a2 = 0; a2 < N2; a2++) {
                req2.id.add(Integer.valueOf(messages.get(a2).id));
            }
            request = req2;
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda18
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationController.this.m346x777703c9(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$markLiveLoactionsAsRead$29$org-telegram-messenger-LocationController */
    public /* synthetic */ void m346x777703c9(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_affectedMessages) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            getMessagesController().processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    public static int getLocationsCount() {
        int count = 0;
        for (int a = 0; a < 4; a++) {
            count += getInstance(a).sharingLocationsUI.size();
        }
        return count;
    }

    public static void fetchLocationAddress(final Location location, final LocationFetchCallback callback) {
        if (callback == null) {
            return;
        }
        Runnable fetchLocationRunnable = callbacks.get(callback);
        if (fetchLocationRunnable != null) {
            Utilities.globalQueue.cancelRunnable(fetchLocationRunnable);
            callbacks.remove(callback);
        }
        if (location == null) {
            callback.onLocationAddressAvailable(null, null, null);
            return;
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable fetchLocationRunnable2 = new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.lambda$fetchLocationAddress$31(location, callback);
            }
        };
        dispatchQueue.postRunnable(fetchLocationRunnable2, 300L);
        callbacks.put(callback, fetchLocationRunnable2);
    }

    public static /* synthetic */ void lambda$fetchLocationAddress$31(final Location location, final LocationFetchCallback callback) {
        String name;
        String displayName;
        try {
            Geocoder gcd = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getSystemDefaultLocale());
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() <= 0) {
                displayName = String.format(Locale.US, "Unknown address (%f,%f)", Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
                name = displayName;
            } else {
                Address address = addresses.get(0);
                boolean hasAny = false;
                StringBuilder nameBuilder = new StringBuilder();
                StringBuilder displayNameBuilder = new StringBuilder();
                String arg = address.getSubThoroughfare();
                if (!TextUtils.isEmpty(arg)) {
                    nameBuilder.append(arg);
                    hasAny = true;
                }
                String arg2 = address.getThoroughfare();
                if (!TextUtils.isEmpty(arg2)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(" ");
                    }
                    nameBuilder.append(arg2);
                    hasAny = true;
                }
                if (!hasAny) {
                    String arg3 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg3)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg3);
                    }
                    String arg4 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg4)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg4);
                    }
                }
                String arg5 = address.getLocality();
                if (!TextUtils.isEmpty(arg5)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg5);
                }
                String arg6 = address.getCountryName();
                if (!TextUtils.isEmpty(arg6)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg6);
                }
                String arg7 = address.getCountryName();
                if (!TextUtils.isEmpty(arg7)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg7);
                }
                String arg8 = address.getLocality();
                if (!TextUtils.isEmpty(arg8)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg8);
                }
                if (!hasAny) {
                    String arg9 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg9)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg9);
                    }
                    String arg10 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg10)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg10);
                    }
                }
                name = nameBuilder.toString();
                displayName = displayNameBuilder.toString();
            }
        } catch (Exception e) {
            displayName = String.format(Locale.US, "Unknown address (%f,%f)", Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
            name = displayName;
        }
        final String displayName2 = name;
        final String displayNameFinal = displayName;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocationController$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                LocationController.lambda$fetchLocationAddress$30(LocationController.LocationFetchCallback.this, displayName2, displayNameFinal, location);
            }
        });
    }

    public static /* synthetic */ void lambda$fetchLocationAddress$30(LocationFetchCallback callback, String nameFinal, String displayNameFinal, Location location) {
        callbacks.remove(callback);
        callback.onLocationAddressAvailable(nameFinal, displayNameFinal, location);
    }
}
