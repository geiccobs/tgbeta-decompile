package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.BaseLocationAdapter;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.ProximitySheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LocationActivity;
/* loaded from: classes4.dex */
public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final double EARTHRADIUS = 6366198.0d;
    public static final int LOCATION_TYPE_GROUP = 4;
    public static final int LOCATION_TYPE_GROUP_VIEW = 5;
    public static final int LOCATION_TYPE_LIVE_VIEW = 6;
    public static final int LOCATION_TYPE_SEND = 0;
    public static final int LOCATION_TYPE_SEND_WITH_LIVE = 1;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int open_in = 1;
    private static final int share_live_location = 5;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private int askWithRadius;
    private AvatarDrawable avatarDrawable;
    private boolean canUndo;
    private TLRPC.TL_channelLocation chatLocation;
    private CircleOptions circleOptions;
    private boolean currentMapStyleDark;
    private LocationActivityDelegate delegate;
    private long dialogId;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean firstWas;
    private CameraUpdate forceUpdate;
    private GoogleMap googleMap;
    private HintView hintView;
    private TLRPC.TL_channelLocation initialLocation;
    private Marker lastPressedMarker;
    private FrameLayout lastPressedMarkerView;
    private VenueLocation lastPressedVenue;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ImageView locationButton;
    private int locationType;
    private ActionBarMenuItem mapTypeButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private Runnable markAsReadRunnable;
    private View markerImageView;
    private int markerTop;
    private MessageObject messageObject;
    private CameraUpdate moveToBounds;
    private Location myLocation;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private MapOverlayView overlayView;
    private ChatActivity parentFragment;
    private double previousRadius;
    private boolean proximityAnimationInProgress;
    private ImageView proximityButton;
    private Circle proximityCircle;
    private ProximitySheet proximitySheet;
    private boolean scrolling;
    private LocationActivitySearchAdapter searchAdapter;
    private SearchButton searchAreaButton;
    private boolean searchInProgress;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searchedForCustomLocations;
    private boolean searching;
    private View shadow;
    private Drawable shadowDrawable;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;
    private float yOffset;
    private UndoView[] undoView = new UndoView[2];
    private boolean checkGpsEnabled = true;
    private boolean locationDenied = false;
    private boolean isFirstLocation = true;
    private boolean firstFocus = true;
    private ArrayList<LiveLocation> markers = new ArrayList<>();
    private LongSparseArray<LiveLocation> markersMap = new LongSparseArray<>();
    private ArrayList<VenueLocation> placeMarkers = new ArrayList<>();
    private boolean checkPermission = true;
    private boolean checkBackgroundPermission = true;
    private int overScrollHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
    private Bitmap[] bitmapCache = new Bitmap[7];

    /* loaded from: classes4.dex */
    public static class LiveLocation {
        public TLRPC.Chat chat;
        public Marker directionMarker;
        public boolean hasRotation;
        public long id;
        public Marker marker;
        public TLRPC.Message object;
        public TLRPC.User user;
    }

    /* loaded from: classes4.dex */
    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    /* loaded from: classes4.dex */
    public static class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC.TL_messageMediaVenue venue;
    }

    static /* synthetic */ float access$3316(LocationActivity x0, float x1) {
        float f = x0.yOffset + x1;
        x0.yOffset = f;
        return f;
    }

    /* loaded from: classes4.dex */
    public static class SearchButton extends TextView {
        private float additionanTranslationY;
        private float currentTranslationY;

        public SearchButton(Context context) {
            super(context);
        }

        @Override // android.view.View
        public float getTranslationX() {
            return this.additionanTranslationY;
        }

        @Override // android.view.View
        public void setTranslationX(float translationX) {
            this.additionanTranslationY = translationX;
            updateTranslationY();
        }

        public void setTranslation(float value) {
            this.currentTranslationY = value;
            updateTranslationY();
        }

        private void updateTranslationY() {
            setTranslationY(this.currentTranslationY + this.additionanTranslationY);
        }
    }

    /* loaded from: classes4.dex */
    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MapOverlayView(Context context) {
            super(context);
            LocationActivity.this = this$0;
        }

        public void addInfoView(Marker marker) {
            final VenueLocation location = (VenueLocation) marker.getTag();
            if (location != null && LocationActivity.this.lastPressedVenue != location) {
                LocationActivity.this.showSearchPlacesButton(false);
                if (LocationActivity.this.lastPressedMarker != null) {
                    removeInfoView(LocationActivity.this.lastPressedMarker);
                    LocationActivity.this.lastPressedMarker = null;
                }
                LocationActivity.this.lastPressedVenue = location;
                LocationActivity.this.lastPressedMarker = marker;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                LocationActivity.this.lastPressedMarkerView = new FrameLayout(context);
                LocationActivity.this.lastPressedMarkerView.setBackgroundResource(R.drawable.venue_tooltip);
                LocationActivity.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(LocationActivity.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                LocationActivity.this.lastPressedMarkerView.setAlpha(0.0f);
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LocationActivity$MapOverlayView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LocationActivity.MapOverlayView.this.m3729x40891783(location, view);
                    }
                });
                TextView nameTextView = new TextView(context);
                nameTextView.setTextSize(1, 16.0f);
                nameTextView.setMaxLines(1);
                nameTextView.setEllipsize(TextUtils.TruncateAt.END);
                nameTextView.setSingleLine(true);
                nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                int i = 5;
                nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                LocationActivity.this.lastPressedMarkerView.addView(nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView addressTextView = new TextView(context);
                addressTextView.setTextSize(1, 14.0f);
                addressTextView.setMaxLines(1);
                addressTextView.setEllipsize(TextUtils.TruncateAt.END);
                addressTextView.setSingleLine(true);
                addressTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout frameLayout2 = LocationActivity.this.lastPressedMarkerView;
                if (!LocaleController.isRTL) {
                    i = 3;
                }
                frameLayout2.addView(addressTextView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 18.0f, 32.0f, 18.0f, 0.0f));
                nameTextView.setText(location.venue.title);
                addressTextView.setText(LocaleController.getString("TapToSendLocation", R.string.TapToSendLocation));
                final FrameLayout iconLayout = new FrameLayout(context);
                iconLayout.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(36.0f), LocationCell.getColorForIndex(location.num)));
                frameLayout.addView(iconLayout, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 4.0f));
                BackupImageView imageView = new BackupImageView(context);
                imageView.setImage("https://ss3.4sqi.net/img/categories_v2/" + location.venue.venue_type + "_64.png", null, null);
                iconLayout.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LocationActivity.MapOverlayView.1
                    private final float[] animatorValues = {0.0f, 1.0f};
                    private boolean startedInner;

                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale;
                        float value = AndroidUtilities.lerp(this.animatorValues, animation.getAnimatedFraction());
                        if (value >= 0.7f && !this.startedInner && LocationActivity.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.playTogether(ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.ALPHA, 0.0f, 1.0f));
                            animatorSet1.setInterpolator(new OvershootInterpolator(1.02f));
                            animatorSet1.setDuration(250L);
                            animatorSet1.start();
                            this.startedInner = true;
                        }
                        if (value <= 0.5f) {
                            scale = CubicBezierInterpolator.EASE_OUT.getInterpolation(value / 0.5f) * 1.1f;
                        } else {
                            scale = value <= 0.75f ? 1.1f - (CubicBezierInterpolator.EASE_OUT.getInterpolation((value - 0.5f) / 0.25f) * 0.2f) : (CubicBezierInterpolator.EASE_OUT.getInterpolation((value - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                        }
                        iconLayout.setScaleX(scale);
                        iconLayout.setScaleY(scale);
                    }
                });
                animator.setDuration(360L);
                animator.start();
                this.views.put(marker, frameLayout);
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
            }
        }

        /* renamed from: lambda$addInfoView$1$org-telegram-ui-LocationActivity$MapOverlayView */
        public /* synthetic */ void m3729x40891783(final VenueLocation location, View v) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(location.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.LocationActivity$MapOverlayView$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.MapOverlayView.this.m3728x40ff7d82(location, z, i);
                }
            });
        }

        /* renamed from: lambda$addInfoView$0$org-telegram-ui-LocationActivity$MapOverlayView */
        public /* synthetic */ void m3728x40ff7d82(VenueLocation location, boolean notify, int scheduleDate) {
            LocationActivity.this.delegate.didSelectLocation(location.venue, LocationActivity.this.locationType, notify, scheduleDate);
            LocationActivity.this.finishFragment();
        }

        public void removeInfoView(Marker marker) {
            View view = this.views.get(marker);
            if (view != null) {
                removeView(view);
                this.views.remove(marker);
            }
        }

        public void updatePositions() {
            if (LocationActivity.this.googleMap != null) {
                Projection projection = LocationActivity.this.googleMap.getProjection();
                for (Map.Entry<Marker, View> entry : this.views.entrySet()) {
                    Marker marker = entry.getKey();
                    View view = entry.getValue();
                    Point point = projection.toScreenLocation(marker.getPosition());
                    view.setTranslationX(point.x - (view.getMeasuredWidth() / 2));
                    view.setTranslationY((point.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f));
                }
            }
        }
    }

    public LocationActivity(int type) {
        this.locationType = type;
        AndroidUtilities.fixGoogleMapsBug();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionDenied);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        MessageObject messageObject = this.messageObject;
        if (messageObject != null && messageObject.isLiveLocation()) {
            getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
            getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
            return true;
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionDenied);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            MapView mapView = this.mapView;
            if (mapView != null) {
                mapView.onDestroy();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
        Runnable runnable2 = this.markAsReadRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.markAsReadRunnable = null;
        }
    }

    private UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            this.mapViewClip.removeView(this.undoView[0]);
            this.mapViewClip.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        FrameLayout.LayoutParams layoutParams;
        ActionBarMenu menu;
        FrameLayout.LayoutParams layoutParams2;
        CombinedDrawable drawable;
        TLRPC.Chat chat;
        int i;
        this.searchWas = false;
        this.searching = false;
        this.searchInProgress = false;
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        if (this.chatLocation != null) {
            Location location = new Location("network");
            this.userLocation = location;
            location.setLatitude(this.chatLocation.geo_point.lat);
            this.userLocation.setLongitude(this.chatLocation.geo_point._long);
        } else if (this.messageObject != null) {
            Location location2 = new Location("network");
            this.userLocation = location2;
            location2.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
        this.locationDenied = (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.LocationActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    LocationActivity.this.finishFragment();
                } else if (id == 1) {
                    try {
                        double lat = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double lon = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (id == 5) {
                    LocationActivity.this.openShareLiveLocation(0);
                }
            }
        });
        ActionBarMenu menu2 = this.actionBar.createMenu();
        if (this.chatLocation != null) {
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
        } else {
            MessageObject messageObject = this.messageObject;
            if (messageObject != null) {
                if (messageObject.isLiveLocation()) {
                    this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation));
                } else {
                    if (this.messageObject.messageOwner.media.title != null && this.messageObject.messageOwner.media.title.length() > 0) {
                        this.actionBar.setTitle(LocaleController.getString("SharedPlace", R.string.SharedPlace));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
                    }
                    ActionBarMenuItem addItem = menu2.addItem(0, R.drawable.ic_ab_other);
                    this.otherItem = addItem;
                    addItem.addSubItem(1, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
                    if (!getLocationController().isSharingLocation(this.dialogId)) {
                        this.otherItem.addSubItem(5, R.drawable.msg_location, LocaleController.getString("SendLiveLocationMenu", R.string.SendLiveLocationMenu));
                    }
                    this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
                }
            } else {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
                if (this.locationType != 4) {
                    this.overlayView = new MapOverlayView(context);
                    ActionBarMenuItem actionBarMenuItemSearchListener = menu2.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.LocationActivity.2
                        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                        public void onSearchExpand() {
                            LocationActivity.this.searching = true;
                        }

                        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                        public void onSearchCollapse() {
                            LocationActivity.this.searching = false;
                            LocationActivity.this.searchWas = false;
                            LocationActivity.this.searchAdapter.searchDelayed(null, null);
                            LocationActivity.this.updateEmptyView();
                        }

                        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                        public void onTextChanged(EditText editText) {
                            if (LocationActivity.this.searchAdapter == null) {
                                return;
                            }
                            String text = editText.getText().toString();
                            boolean z = false;
                            if (text.length() != 0) {
                                LocationActivity.this.searchWas = true;
                                LocationActivity.this.searchItem.setShowSearchProgress(true);
                                if (LocationActivity.this.otherItem != null) {
                                    LocationActivity.this.otherItem.setVisibility(8);
                                }
                                LocationActivity.this.listView.setVisibility(8);
                                LocationActivity.this.mapViewClip.setVisibility(8);
                                if (LocationActivity.this.searchListView.getAdapter() != LocationActivity.this.searchAdapter) {
                                    LocationActivity.this.searchListView.setAdapter(LocationActivity.this.searchAdapter);
                                }
                                LocationActivity.this.searchListView.setVisibility(0);
                                LocationActivity locationActivity = LocationActivity.this;
                                if (locationActivity.searchAdapter.getItemCount() == 0) {
                                    z = true;
                                }
                                locationActivity.searchInProgress = z;
                            } else {
                                if (LocationActivity.this.otherItem != null) {
                                    LocationActivity.this.otherItem.setVisibility(0);
                                }
                                LocationActivity.this.listView.setVisibility(0);
                                LocationActivity.this.mapViewClip.setVisibility(0);
                                LocationActivity.this.searchListView.setAdapter(null);
                                LocationActivity.this.searchListView.setVisibility(8);
                            }
                            LocationActivity.this.updateEmptyView();
                            LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
                        }
                    });
                    this.searchItem = actionBarMenuItemSearchListener;
                    actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
                    this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
                    EditTextBoldCursor editText = this.searchItem.getSearchField();
                    editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    editText.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
                    editText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
                }
            }
        }
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.LocationActivity.3
            private boolean first = true;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                    return;
                }
                LocationActivity.this.updateClipView(true);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == LocationActivity.this.actionBar && LocationActivity.this.parentLayout != null) {
                    LocationActivity.this.parentLayout.drawHeaderShadow(canvas, LocationActivity.this.actionBar.getMeasuredHeight());
                }
                return result;
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        final Rect padding = new Rect();
        this.shadowDrawable.getPadding(padding);
        int i2 = this.locationType;
        if (i2 == 0 || i2 == 1) {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f) + padding.top);
        } else {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(6.0f) + padding.top);
        }
        layoutParams.gravity = 83;
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.LocationActivity.4
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip = frameLayout2;
        frameLayout2.setBackgroundDrawable(new MapPlaceholderDrawable());
        if (this.messageObject == null && ((i = this.locationType) == 0 || i == 1)) {
            SearchButton searchButton = new SearchButton(context);
            this.searchAreaButton = searchButton;
            searchButton.setTranslationX(-AndroidUtilities.dp(80.0f));
            Drawable drawable2 = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(Theme.key_location_actionBackground), Theme.getColor(Theme.key_location_actionPressedBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.places_btn).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable2, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                combinedDrawable.setFullsize(true);
                drawable2 = combinedDrawable;
            } else {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.searchAreaButton.setStateListAnimator(animator);
                this.searchAreaButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.LocationActivity.5
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), view.getMeasuredHeight() / 2);
                    }
                });
            }
            this.searchAreaButton.setBackgroundDrawable(drawable2);
            this.searchAreaButton.setTextColor(Theme.getColor(Theme.key_location_actionActiveIcon));
            this.searchAreaButton.setTextSize(1, 14.0f);
            this.searchAreaButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", R.string.PlacesInThisArea));
            this.searchAreaButton.setGravity(17);
            this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
            this.searchAreaButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda33
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LocationActivity.this.m3691lambda$createView$0$orgtelegramuiLocationActivity(view);
                }
            });
        }
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_location_actionIcon));
        this.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, R.drawable.msg_map, LocaleController.getString("Map", R.string.Map));
        this.mapTypeButton.addSubItem(3, R.drawable.msg_satellite, LocaleController.getString("Satellite", R.string.Satellite));
        this.mapTypeButton.addSubItem(4, R.drawable.msg_hybrid, LocaleController.getString("Hybrid", R.string.Hybrid));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        Drawable drawable3 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(Theme.key_location_actionBackground), Theme.getColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable2, drawable3, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable3 = combinedDrawable2;
        } else {
            StateListAnimator animator2 = new StateListAnimator();
            animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator2.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.mapTypeButton.setStateListAnimator(animator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.LocationActivity.6
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(drawable3);
        this.mapTypeButton.setIcon(R.drawable.msg_map_type);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LocationActivity.this.m3692lambda$createView$1$orgtelegramuiLocationActivity(view);
            }
        });
        this.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda20
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i3) {
                LocationActivity.this.m3703lambda$createView$2$orgtelegramuiLocationActivity(i3);
            }
        });
        this.locationButton = new ImageView(context);
        Drawable drawable4 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(Theme.key_location_actionBackground), Theme.getColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable3 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(shadowDrawable3, drawable4, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable4 = combinedDrawable3;
            menu = menu2;
        } else {
            StateListAnimator animator3 = new StateListAnimator();
            menu = menu2;
            animator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator3.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.locationButton.setStateListAnimator(animator3);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.LocationActivity.7
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.locationButton.setBackgroundDrawable(drawable4);
        this.locationButton.setImageResource(R.drawable.msg_current_location);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionActiveIcon), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag(Theme.key_location_actionActiveIcon);
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", R.string.AccDescrMyLocation));
        FrameLayout.LayoutParams layoutParams1 = LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        layoutParams1.bottomMargin += layoutParams.height - padding.top;
        this.mapViewClip.addView(this.locationButton, layoutParams1);
        this.locationButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LocationActivity.this.m3705lambda$createView$3$orgtelegramuiLocationActivity(view);
            }
        });
        this.proximityButton = new ImageView(context);
        Drawable drawable5 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(Theme.key_location_actionBackground), Theme.getColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable4 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable4 = new CombinedDrawable(shadowDrawable4, drawable5, 0, 0);
            combinedDrawable4.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            layoutParams2 = layoutParams;
            drawable = combinedDrawable4;
        } else {
            StateListAnimator animator4 = new StateListAnimator();
            layoutParams2 = layoutParams;
            animator4.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.proximityButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator4.addState(new int[0], ObjectAnimator.ofFloat(this.proximityButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.proximityButton.setStateListAnimator(animator4);
            this.proximityButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.LocationActivity.8
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
            drawable = drawable5;
        }
        this.proximityButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.proximityButton.setBackgroundDrawable(drawable);
        this.proximityButton.setScaleType(ImageView.ScaleType.CENTER);
        this.proximityButton.setContentDescription(LocaleController.getString("AccDescrLocationNotify", R.string.AccDescrLocationNotify));
        this.mapViewClip.addView(this.proximityButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 62.0f, 12.0f, 0.0f));
        this.proximityButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda36
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LocationActivity.this.m3708lambda$createView$6$orgtelegramuiLocationActivity(view);
            }
        });
        if (DialogObject.isChatDialog(this.dialogId)) {
            chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        } else {
            chat = null;
        }
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 != null && messageObject2.isLiveLocation() && !this.messageObject.isExpiredLiveLocation(getConnectionsManager().getCurrentTime()) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (myInfo != null && myInfo.proximityMeters > 0) {
                this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
            } else {
                if (DialogObject.isUserDialog(this.dialogId) && this.messageObject.getFromChatId() == getUserConfig().getClientUserId()) {
                    this.proximityButton.setVisibility(4);
                    this.proximityButton.setAlpha(0.0f);
                    this.proximityButton.setScaleX(0.4f);
                    this.proximityButton.setScaleY(0.4f);
                }
                this.proximityButton.setImageResource(R.drawable.msg_location_alert);
            }
        } else {
            this.proximityButton.setVisibility(8);
            this.proximityButton.setImageResource(R.drawable.msg_location_alert);
        }
        HintView hintView = new HintView(context, 6, true);
        this.hintView = hintView;
        hintView.setVisibility(4);
        this.hintView.setShowingDuration(4000L);
        this.mapViewClip.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(LocationActivity$$ExternalSyntheticLambda37.INSTANCE);
        ImageView imageView = new ImageView(context);
        this.emptyImageView = imageView;
        imageView.setImageResource(R.drawable.location_empty);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogEmptyImage), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        this.emptyTitleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogEmptyText));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", R.string.NoPlacesFound));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_dialogEmptyText));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        TLRPC.Chat chat2 = chat;
        FrameLayout.LayoutParams layoutParams3 = layoutParams2;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, this.locationType, this.dialogId, false, null) { // from class: org.telegram.ui.LocationActivity.9
            @Override // org.telegram.ui.Adapters.LocationActivityAdapter
            protected void onDirectionClick() {
                Intent intent;
                Activity activity;
                if (Build.VERSION.SDK_INT < 23 || (activity = LocationActivity.this.getParentActivity()) == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    if (LocationActivity.this.myLocation != null) {
                        try {
                            if (LocationActivity.this.messageObject != null) {
                                intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long))));
                            } else {
                                intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.chatLocation.geo_point.lat), Double.valueOf(LocationActivity.this.chatLocation.geo_point._long))));
                            }
                            LocationActivity.this.getParentActivity().startActivity(intent);
                            return;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    return;
                }
                LocationActivity.this.showPermissionAlert(true);
            }
        };
        this.adapter = locationActivityAdapter2;
        recyclerListView.setAdapter(locationActivityAdapter2);
        this.adapter.setMyLocationDenied(this.locationDenied);
        this.adapter.setUpdateRunnable(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                LocationActivity.this.m3709lambda$createView$8$orgtelegramuiLocationActivity();
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.LocationActivity.10
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LocationActivity.this.scrolling = newState != 0;
                if (!LocationActivity.this.scrolling && LocationActivity.this.forceUpdate != null) {
                    LocationActivity.this.forceUpdate = null;
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LocationActivity.this.updateClipView(false);
                if (LocationActivity.this.forceUpdate != null) {
                    LocationActivity.access$3316(LocationActivity.this, dy);
                }
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda30
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i3) {
                LocationActivity.this.m3697lambda$createView$14$orgtelegramuiLocationActivity(view, i3);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda24
            @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
            public final void didLoadSearchResult(ArrayList arrayList) {
                LocationActivity.this.updatePlacesMarkers(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new AnonymousClass11(context);
        final MapView map = this.mapView;
        new Thread(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                LocationActivity.this.m3700lambda$createView$17$orgtelegramuiLocationActivity(map);
            }
        }).start();
        MessageObject messageObject3 = this.messageObject;
        if (messageObject3 == null && this.chatLocation == null) {
            if (chat2 != null && this.locationType == 4 && this.dialogId != 0) {
                FrameLayout frameLayout1 = new FrameLayout(context);
                frameLayout1.setBackgroundResource(R.drawable.livepin);
                this.mapViewClip.addView(frameLayout1, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setForUserOrChat(chat2, new AvatarDrawable(chat2));
                frameLayout1.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout1;
                frameLayout1.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView2 = new ImageView(context);
                imageView2.setImageResource(R.drawable.map_pin2);
                this.mapViewClip.addView(imageView2, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView2;
            }
            RecyclerListView recyclerListView3 = new RecyclerListView(context);
            this.searchListView = recyclerListView3;
            recyclerListView3.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
            LocationActivitySearchAdapter locationActivitySearchAdapter2 = new LocationActivitySearchAdapter(context) { // from class: org.telegram.ui.LocationActivity.12
                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyDataSetChanged() {
                    if (LocationActivity.this.searchItem != null) {
                        LocationActivity.this.searchItem.setShowSearchProgress(LocationActivity.this.searchAdapter.isSearching());
                    }
                    if (LocationActivity.this.emptySubtitleTextView != null) {
                        LocationActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", R.string.NoPlacesFoundInfo, LocationActivity.this.searchAdapter.getLastSearchString())));
                    }
                    super.notifyDataSetChanged();
                }
            };
            this.searchAdapter = locationActivitySearchAdapter2;
            locationActivitySearchAdapter2.setDelegate(0L, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda23
                @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
                public final void didLoadSearchResult(ArrayList arrayList) {
                    LocationActivity.this.m3701lambda$createView$18$orgtelegramuiLocationActivity(arrayList);
                }
            });
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.LocationActivity.13
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda31
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i3) {
                    LocationActivity.this.m3704lambda$createView$20$orgtelegramuiLocationActivity(view, i3);
                }
            });
        } else if ((messageObject3 != null && !messageObject3.isLiveLocation()) || this.chatLocation != null) {
            TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                this.adapter.setChatLocation(tL_channelLocation);
            } else {
                MessageObject messageObject4 = this.messageObject;
                if (messageObject4 != null) {
                    this.adapter.setMessageObject(messageObject4);
                }
            }
        }
        MessageObject messageObject5 = this.messageObject;
        if (messageObject5 != null && this.locationType == 6) {
            this.adapter.setMessageObject(messageObject5);
        }
        for (int a = 0; a < 2; a++) {
            this.undoView[a] = new UndoView(context);
            this.undoView[a].setAdditionalTranslationY(AndroidUtilities.dp(10.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[a].setTranslationZ(AndroidUtilities.dp(5.0f));
            }
            this.mapViewClip.addView(this.undoView[a], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        this.shadow = new View(context) { // from class: org.telegram.ui.LocationActivity.14
            private RectF rect = new RectF();

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                LocationActivity.this.shadowDrawable.setBounds(-padding.left, 0, getMeasuredWidth() + padding.right, getMeasuredHeight());
                LocationActivity.this.shadowDrawable.draw(canvas);
                if (LocationActivity.this.locationType == 0 || LocationActivity.this.locationType == 1) {
                    int w = AndroidUtilities.dp(36.0f);
                    int y = padding.top + AndroidUtilities.dp(10.0f);
                    this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                    int color = Theme.getColor(Theme.key_sheet_scrollUp);
                    Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 21) {
            this.shadow.setTranslationZ(AndroidUtilities.dp(6.0f));
        }
        this.mapViewClip.addView(this.shadow, layoutParams3);
        if (this.messageObject == null && this.chatLocation == null && this.initialLocation != null) {
            this.userLocationMoved = true;
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag(Theme.key_location_actionIcon);
        }
        frameLayout.addView(this.actionBar);
        updateEmptyView();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3691lambda$createView$0$orgtelegramuiLocationActivity(View v) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery(null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3692lambda$createView$1$orgtelegramuiLocationActivity(View v) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3703lambda$createView$2$orgtelegramuiLocationActivity(int id) {
        GoogleMap googleMap = this.googleMap;
        if (googleMap == null) {
            return;
        }
        if (id == 2) {
            googleMap.setMapType(1);
        } else if (id == 3) {
            googleMap.setMapType(2);
        } else if (id == 4) {
            googleMap.setMapType(4);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3705lambda$createView$3$orgtelegramuiLocationActivity(View v) {
        GoogleMap googleMap;
        Activity activity;
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            showPermissionAlert(false);
        } else if (!checkGpsEnabled()) {
        } else {
            if (this.messageObject != null || this.chatLocation != null) {
                if (this.myLocation != null && (googleMap = this.googleMap) != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else if (this.myLocation != null && this.googleMap != null) {
                this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionActiveIcon), PorterDuff.Mode.MULTIPLY));
                this.locationButton.setTag(Theme.key_location_actionActiveIcon);
                this.adapter.setCustomLocation(null);
                this.userLocationMoved = false;
                showSearchPlacesButton(false);
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
                if (this.searchedForCustomLocations) {
                    Location location = this.myLocation;
                    if (location != null) {
                        this.adapter.searchPlacesWithQuery(null, location, true, true);
                    }
                    this.searchedForCustomLocations = false;
                    showResults();
                }
            }
            removeInfoView();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3708lambda$createView$6$orgtelegramuiLocationActivity(View v) {
        if (getParentActivity() == null || this.myLocation == null || !checkGpsEnabled() || this.googleMap == null) {
            return;
        }
        HintView hintView = this.hintView;
        if (hintView != null) {
            hintView.hide();
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        preferences.edit().putInt("proximityhint", 3).commit();
        final LocationController.SharingLocationInfo info = getLocationController().getSharingLocationInfo(this.dialogId);
        if (this.canUndo) {
            this.undoView[0].hide(true, 1);
        }
        if (info != null && info.proximityMeters > 0) {
            this.proximityButton.setImageResource(R.drawable.msg_location_alert);
            Circle circle = this.proximityCircle;
            if (circle != null) {
                circle.remove();
                this.proximityCircle = null;
            }
            this.canUndo = true;
            getUndoView().showWithAction(0L, 25, (Object) 0, (Object) null, new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LocationActivity.this.m3706lambda$createView$4$orgtelegramuiLocationActivity();
                }
            }, new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    LocationActivity.this.m3707lambda$createView$5$orgtelegramuiLocationActivity(info);
                }
            });
            return;
        }
        openProximityAlert();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3706lambda$createView$4$orgtelegramuiLocationActivity() {
        getLocationController().setProximityLocation(this.dialogId, 0, true);
        this.canUndo = false;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3707lambda$createView$5$orgtelegramuiLocationActivity(LocationController.SharingLocationInfo info) {
        this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
        createCircle(info.proximityMeters);
        this.canUndo = false;
    }

    public static /* synthetic */ boolean lambda$createView$7(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3709lambda$createView$8$orgtelegramuiLocationActivity() {
        updateClipView(false);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3697lambda$createView$14$orgtelegramuiLocationActivity(View view, int position) {
        MessageObject messageObject;
        final TLRPC.TL_messageMediaVenue venue;
        int i = this.locationType;
        if (i == 4) {
            if (position != 1 || (venue = (TLRPC.TL_messageMediaVenue) this.adapter.getItem(position)) == null) {
                return;
            }
            if (this.dialogId == 0) {
                this.delegate.didSelectLocation(venue, 4, true, 0);
                finishFragment();
                return;
            }
            final AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
            TLRPC.TL_channels_editLocation req = new TLRPC.TL_channels_editLocation();
            req.address = venue.address;
            req.channel = getMessagesController().getInputChannel(-this.dialogId);
            req.geo_point = new TLRPC.TL_inputGeoPoint();
            req.geo_point.lat = venue.geo.lat;
            req.geo_point._long = venue.geo._long;
            final int requestId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda19
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationActivity.this.m3693lambda$createView$10$orgtelegramuiLocationActivity(progressDialog, venue, tLObject, tL_error);
                }
            });
            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    LocationActivity.this.m3694lambda$createView$11$orgtelegramuiLocationActivity(requestId, dialogInterface);
                }
            });
            showDialog(progressDialog[0]);
        } else if (i == 5) {
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.chatLocation.geo_point.lat, this.chatLocation.geo_point._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (position == 1 && (messageObject = this.messageObject) != null && (!messageObject.isLiveLocation() || this.locationType == 6)) {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.messageObject.messageOwner.media.geo.lat, this.messageObject.messageOwner.media.geo._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (position == 1 && this.locationType != 2) {
            if (this.delegate != null && this.userLocation != null) {
                FrameLayout frameLayout = this.lastPressedMarkerView;
                if (frameLayout != null) {
                    frameLayout.callOnClick();
                    return;
                }
                final TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
                location.geo = new TLRPC.TL_geoPoint();
                location.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                location.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                ChatActivity chatActivity = this.parentFragment;
                if (chatActivity != null && chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda26
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            LocationActivity.this.m3695lambda$createView$12$orgtelegramuiLocationActivity(location, z, i2);
                        }
                    });
                    return;
                }
                this.delegate.didSelectLocation(location, this.locationType, true, 0);
                finishFragment();
            }
        } else if ((position == 2 && this.locationType == 1) || ((position == 1 && this.locationType == 2) || (position == 3 && this.locationType == 3))) {
            if (getLocationController().isSharingLocation(this.dialogId)) {
                getLocationController().removeSharingLocation(this.dialogId);
                finishFragment();
                return;
            }
            openShareLiveLocation(0);
        } else {
            final Object object = this.adapter.getItem(position);
            if (object instanceof TLRPC.TL_messageMediaVenue) {
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null && chatActivity2.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda25
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            LocationActivity.this.m3696lambda$createView$13$orgtelegramuiLocationActivity(object, z, i2);
                        }
                    });
                    return;
                }
                this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, true, 0);
                finishFragment();
            } else if (object instanceof LiveLocation) {
                LiveLocation liveLocation = (LiveLocation) object;
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3693lambda$createView$10$orgtelegramuiLocationActivity(final AlertDialog[] progressDialog, final TLRPC.TL_messageMediaVenue venue, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                LocationActivity.this.m3710lambda$createView$9$orgtelegramuiLocationActivity(progressDialog, venue);
            }
        });
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3710lambda$createView$9$orgtelegramuiLocationActivity(AlertDialog[] progressDialog, TLRPC.TL_messageMediaVenue venue) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.delegate.didSelectLocation(venue, 4, true, 0);
        finishFragment();
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3694lambda$createView$11$orgtelegramuiLocationActivity(int requestId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(requestId, true);
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3695lambda$createView$12$orgtelegramuiLocationActivity(TLRPC.TL_messageMediaGeo location, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(location, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3696lambda$createView$13$orgtelegramuiLocationActivity(Object object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    /* renamed from: org.telegram.ui.LocationActivity$11 */
    /* loaded from: classes4.dex */
    public class AnonymousClass11 extends MapView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass11(Context arg0) {
            super(arg0);
            LocationActivity.this = this$0;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            MotionEvent eventToRecycle = null;
            if (LocationActivity.this.yOffset != 0.0f) {
                MotionEvent obtain = MotionEvent.obtain(ev);
                eventToRecycle = obtain;
                ev = obtain;
                eventToRecycle.offsetLocation(0.0f, (-LocationActivity.this.yOffset) / 2.0f);
            }
            boolean result = super.dispatchTouchEvent(ev);
            if (eventToRecycle != null) {
                eventToRecycle.recycle();
            }
            return result;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (LocationActivity.this.messageObject == null && LocationActivity.this.chatLocation == null) {
                if (ev.getAction() == 0) {
                    if (LocationActivity.this.animatorSet != null) {
                        LocationActivity.this.animatorSet.cancel();
                    }
                    LocationActivity.this.animatorSet = new AnimatorSet();
                    LocationActivity.this.animatorSet.setDuration(200L);
                    LocationActivity.this.animatorSet.playTogether(ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, LocationActivity.this.markerTop - AndroidUtilities.dp(10.0f)));
                    LocationActivity.this.animatorSet.start();
                } else if (ev.getAction() == 1) {
                    if (LocationActivity.this.animatorSet != null) {
                        LocationActivity.this.animatorSet.cancel();
                    }
                    LocationActivity.this.yOffset = 0.0f;
                    LocationActivity.this.animatorSet = new AnimatorSet();
                    LocationActivity.this.animatorSet.setDuration(200L);
                    LocationActivity.this.animatorSet.playTogether(ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, LocationActivity.this.markerTop));
                    LocationActivity.this.animatorSet.start();
                    LocationActivity.this.adapter.fetchLocationAddress();
                }
                if (ev.getAction() == 2) {
                    if (!LocationActivity.this.userLocationMoved) {
                        LocationActivity.this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
                        LocationActivity.this.locationButton.setTag(Theme.key_location_actionIcon);
                        LocationActivity.this.userLocationMoved = true;
                    }
                    if (LocationActivity.this.googleMap != null && LocationActivity.this.userLocation != null) {
                        LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                        LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                    }
                    LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
                }
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LocationActivity$11$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LocationActivity.AnonymousClass11.this.m3727lambda$onLayout$0$orgtelegramuiLocationActivity$11();
                }
            });
        }

        /* renamed from: lambda$onLayout$0$org-telegram-ui-LocationActivity$11 */
        public /* synthetic */ void m3727lambda$onLayout$0$orgtelegramuiLocationActivity$11() {
            if (LocationActivity.this.moveToBounds != null) {
                LocationActivity.this.googleMap.moveCamera(LocationActivity.this.moveToBounds);
                LocationActivity.this.moveToBounds = null;
            }
        }
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3700lambda$createView$17$orgtelegramuiLocationActivity(final MapView map) {
        try {
            map.onCreate(null);
        } catch (Exception e) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                LocationActivity.this.m3699lambda$createView$16$orgtelegramuiLocationActivity(map);
            }
        });
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3699lambda$createView$16$orgtelegramuiLocationActivity(MapView map) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                map.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new OnMapReadyCallback() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda4
                    @Override // com.google.android.gms.maps.OnMapReadyCallback
                    public final void onMapReady(GoogleMap googleMap) {
                        LocationActivity.this.m3698lambda$createView$15$orgtelegramuiLocationActivity(googleMap);
                    }
                });
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3698lambda$createView$15$orgtelegramuiLocationActivity(GoogleMap map1) {
        this.googleMap = map1;
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night);
            this.googleMap.setMapStyle(style);
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3701lambda$createView$18$orgtelegramuiLocationActivity(ArrayList places) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3704lambda$createView$20$orgtelegramuiLocationActivity(View view, int position) {
        final TLRPC.TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (object != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda27
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        LocationActivity.this.m3702lambda$createView$19$orgtelegramuiLocationActivity(object, z, i);
                    }
                });
                return;
            }
            this.delegate.didSelectLocation(object, this.locationType, true, 0);
            finishFragment();
        }
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3702lambda$createView$19$orgtelegramuiLocationActivity(TLRPC.TL_messageMediaVenue object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    private boolean isActiveThemeDark() {
        Theme.ThemeInfo info = Theme.getActiveTheme();
        if (info.isDark()) {
            return true;
        }
        int color = Theme.getColor(Theme.key_windowBackgroundWhite);
        return AndroidUtilities.computePerceivedBrightness(color) < 0.721f;
    }

    public void updateEmptyView() {
        if (this.searching) {
            if (this.searchInProgress) {
                this.searchListView.setEmptyView(null);
                this.emptyView.setVisibility(8);
                this.searchListView.setVisibility(8);
                return;
            }
            this.searchListView.setEmptyView(this.emptyView);
            return;
        }
        this.emptyView.setVisibility(8);
    }

    public void showSearchPlacesButton(boolean show) {
        SearchButton searchButton;
        Location location;
        Location location2;
        if (show && (searchButton = this.searchAreaButton) != null && searchButton.getTag() == null && ((location = this.myLocation) == null || (location2 = this.userLocation) == null || location2.distanceTo(location) < 300.0f)) {
            show = false;
        }
        SearchButton searchButton2 = this.searchAreaButton;
        if (searchButton2 != null) {
            if (show && searchButton2.getTag() != null) {
                return;
            }
            if (!show && this.searchAreaButton.getTag() == null) {
                return;
            }
            this.searchAreaButton.setTag(show ? 1 : null);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            SearchButton searchButton3 = this.searchAreaButton;
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = show ? 0.0f : -AndroidUtilities.dp(80.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(searchButton3, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(180L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.start();
        }
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        TLRPC.FileLocation photo;
        Bitmap result = null;
        try {
            if (liveLocation.user != null && liveLocation.user.photo != null) {
                photo = liveLocation.user.photo.photo_small;
            } else if (liveLocation.chat != null && liveLocation.chat.photo != null) {
                photo = liveLocation.chat.photo.photo_small;
            } else {
                photo = null;
            }
            result = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f), Bitmap.Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.map_pin_photo);
            drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f));
            drawable.draw(canvas);
            Paint roundPaint = new Paint(1);
            RectF bitmapRect = new RectF();
            canvas.save();
            if (photo != null) {
                File path = getFileLoader().getPathToAttach(photo, true);
                Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
                if (bitmap != null) {
                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    float scale = AndroidUtilities.dp(50.0f) / bitmap.getWidth();
                    matrix.postTranslate(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                    matrix.postScale(scale, scale);
                    roundPaint.setShader(shader);
                    shader.setLocalMatrix(matrix);
                    bitmapRect.set(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    canvas.drawRoundRect(bitmapRect, AndroidUtilities.dp(25.0f), AndroidUtilities.dp(25.0f), roundPaint);
                }
            } else {
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                if (liveLocation.user != null) {
                    avatarDrawable.setInfo(liveLocation.user);
                } else if (liveLocation.chat != null) {
                    avatarDrawable.setInfo(liveLocation.chat);
                }
                canvas.translate(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                avatarDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
                avatarDrawable.draw(canvas);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        return result;
    }

    private long getMessageId(TLRPC.Message message) {
        if (message.from_id != null) {
            return MessageObject.getFromChatId(message);
        }
        return MessageObject.getDialogId(message);
    }

    private void openProximityAlert() {
        final TLRPC.User user;
        Circle circle = this.proximityCircle;
        if (circle == null) {
            createCircle(500);
        } else {
            this.previousRadius = circle.getRadius();
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            user = null;
        }
        this.proximitySheet = new ProximitySheet(getParentActivity(), user, new ProximitySheet.onRadiusPickerChange() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.Components.ProximitySheet.onRadiusPickerChange
            public final boolean run(boolean z, int i) {
                return LocationActivity.this.m3720lambda$openProximityAlert$21$orgtelegramuiLocationActivity(z, i);
            }
        }, new ProximitySheet.onRadiusPickerChange() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda29
            @Override // org.telegram.ui.Components.ProximitySheet.onRadiusPickerChange
            public final boolean run(boolean z, int i) {
                return LocationActivity.this.m3722lambda$openProximityAlert$23$orgtelegramuiLocationActivity(user, z, i);
            }
        }, new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                LocationActivity.this.m3723lambda$openProximityAlert$24$orgtelegramuiLocationActivity();
            }
        });
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.addView(this.proximitySheet, LayoutHelper.createFrame(-1, -1.0f));
        this.proximitySheet.show();
    }

    /* renamed from: lambda$openProximityAlert$21$org-telegram-ui-LocationActivity */
    public /* synthetic */ boolean m3720lambda$openProximityAlert$21$orgtelegramuiLocationActivity(boolean move, int radius) {
        Circle circle = this.proximityCircle;
        if (circle != null) {
            circle.setRadius(radius);
            if (move) {
                moveToBounds(radius, true, true);
            }
        }
        if (DialogObject.isChatDialog(this.dialogId)) {
            return true;
        }
        int N = this.markers.size();
        for (int a = 0; a < N; a++) {
            LiveLocation location = this.markers.get(a);
            if (location.object != null && !UserObject.isUserSelf(location.user)) {
                TLRPC.GeoPoint point = location.object.media.geo;
                Location loc = new Location("network");
                loc.setLatitude(point.lat);
                loc.setLongitude(point._long);
                if (this.myLocation.distanceTo(loc) > radius) {
                    return true;
                }
            }
        }
        return false;
    }

    /* renamed from: lambda$openProximityAlert$23$org-telegram-ui-LocationActivity */
    public /* synthetic */ boolean m3722lambda$openProximityAlert$23$orgtelegramuiLocationActivity(final TLRPC.User user, boolean move, final int radius) {
        LocationController.SharingLocationInfo info = getLocationController().getSharingLocationInfo(this.dialogId);
        if (info == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("ShareLocationAlertTitle", R.string.ShareLocationAlertTitle));
            builder.setMessage(LocaleController.getString("ShareLocationAlertText", R.string.ShareLocationAlertText));
            builder.setPositiveButton(LocaleController.getString("ShareLocationAlertButton", R.string.ShareLocationAlertButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda32
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LocationActivity.this.m3721lambda$openProximityAlert$22$orgtelegramuiLocationActivity(user, radius, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
            return false;
        }
        this.proximitySheet.setRadiusSet();
        this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
        getUndoView().showWithAction(0L, 24, Integer.valueOf(radius), user, (Runnable) null, (Runnable) null);
        getLocationController().setProximityLocation(this.dialogId, radius, true);
        return true;
    }

    /* renamed from: lambda$openProximityAlert$22$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3721lambda$openProximityAlert$22$orgtelegramuiLocationActivity(TLRPC.User user, int radius, DialogInterface dialog, int id) {
        m3725lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(user, 900, radius);
    }

    /* renamed from: lambda$openProximityAlert$24$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3723lambda$openProximityAlert$24$orgtelegramuiLocationActivity() {
        GoogleMap googleMap = this.googleMap;
        if (googleMap != null) {
            googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        }
        if (!this.proximitySheet.getRadiusSet()) {
            double d = this.previousRadius;
            if (d > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                this.proximityCircle.setRadius(d);
            } else {
                Circle circle = this.proximityCircle;
                if (circle != null) {
                    circle.remove();
                    this.proximityCircle = null;
                }
            }
        }
        this.proximitySheet = null;
    }

    public void openShareLiveLocation(final int proximityRadius) {
        final TLRPC.User user;
        Activity activity;
        if (this.delegate == null || getParentActivity() == null || this.myLocation == null || !checkGpsEnabled()) {
            return;
        }
        if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (activity = getParentActivity()) != null) {
            this.askWithRadius = proximityRadius;
            this.checkBackgroundPermission = false;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            int lastTime = preferences.getInt("backgroundloc", 0);
            if (Math.abs((System.currentTimeMillis() / 1000) - lastTime) > 86400 && activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                preferences.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                AlertsCreator.createBackgroundLocationPermissionDialog(activity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocationActivity.this.m3724lambda$openShareLiveLocation$25$orgtelegramuiLocationActivity();
                    }
                }, null).show();
                return;
            }
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            user = null;
        }
        showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda17
            @Override // org.telegram.messenger.MessagesStorage.IntCallback
            public final void run(int i) {
                LocationActivity.this.m3725lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(user, proximityRadius, i);
            }
        }, null));
    }

    /* renamed from: lambda$openShareLiveLocation$25$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3724lambda$openShareLiveLocation$25$orgtelegramuiLocationActivity() {
        openShareLiveLocation(this.askWithRadius);
    }

    /* renamed from: shareLiveLocation */
    public void m3725lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(TLRPC.User user, int period, int radius) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.heading = LocationController.getHeading(this.myLocation);
        location.flags |= 1;
        location.period = period;
        location.proximity_notification_radius = radius;
        location.flags |= 8;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        if (radius > 0) {
            this.proximitySheet.setRadiusSet();
            this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
            ProximitySheet proximitySheet = this.proximitySheet;
            if (proximitySheet != null) {
                proximitySheet.dismiss();
            }
            getUndoView().showWithAction(0L, 24, Integer.valueOf(radius), user, (Runnable) null, (Runnable) null);
            return;
        }
        finishFragment();
    }

    private Bitmap createPlaceBitmap(int num) {
        Bitmap[] bitmapArr = this.bitmapCache;
        if (bitmapArr[num % 7] != null) {
            return bitmapArr[num % 7];
        }
        try {
            Paint paint = new Paint(1);
            paint.setColor(-1);
            Bitmap bitmap = Bitmap.createBitmap(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint);
            paint.setColor(LocationCell.getColorForIndex(num));
            canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), paint);
            canvas.setBitmap(null);
            this.bitmapCache[num % 7] = bitmap;
            return bitmap;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }

    public void updatePlacesMarkers(ArrayList<TLRPC.TL_messageMediaVenue> places) {
        if (places == null) {
            return;
        }
        int N = this.placeMarkers.size();
        for (int a = 0; a < N; a++) {
            this.placeMarkers.get(a).marker.remove();
        }
        this.placeMarkers.clear();
        int N2 = places.size();
        for (int a2 = 0; a2 < N2; a2++) {
            TLRPC.TL_messageMediaVenue venue = places.get(a2);
            try {
                MarkerOptions options = new MarkerOptions().position(new LatLng(venue.geo.lat, venue.geo._long));
                options.icon(BitmapDescriptorFactory.fromBitmap(createPlaceBitmap(a2)));
                options.anchor(0.5f, 0.5f);
                options.title(venue.title);
                options.snippet(venue.address);
                VenueLocation venueLocation = new VenueLocation();
                venueLocation.num = a2;
                venueLocation.marker = this.googleMap.addMarker(options);
                venueLocation.venue = venue;
                venueLocation.marker.setTag(venueLocation);
                this.placeMarkers.add(venueLocation);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private LiveLocation addUserMarker(TLRPC.Message message) {
        LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
        LiveLocation liveLocation = this.markersMap.get(MessageObject.getFromChatId(message));
        LiveLocation liveLocation2 = liveLocation;
        if (liveLocation == null) {
            liveLocation2 = new LiveLocation();
            liveLocation2.object = message;
            if (liveLocation2.object.from_id instanceof TLRPC.TL_peerUser) {
                liveLocation2.user = getMessagesController().getUser(Long.valueOf(liveLocation2.object.from_id.user_id));
                liveLocation2.id = liveLocation2.object.from_id.user_id;
            } else {
                long did = MessageObject.getDialogId(message);
                if (DialogObject.isUserDialog(did)) {
                    liveLocation2.user = getMessagesController().getUser(Long.valueOf(did));
                } else {
                    liveLocation2.chat = getMessagesController().getChat(Long.valueOf(-did));
                }
                liveLocation2.id = did;
            }
            try {
                MarkerOptions options = new MarkerOptions().position(latLng);
                Bitmap bitmap = createUserBitmap(liveLocation2);
                if (bitmap != null) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    liveLocation2.marker = this.googleMap.addMarker(options);
                    if (!UserObject.isUserSelf(liveLocation2.user)) {
                        MarkerOptions dirOptions = new MarkerOptions().position(latLng).flat(true);
                        dirOptions.anchor(0.5f, 0.5f);
                        liveLocation2.directionMarker = this.googleMap.addMarker(dirOptions);
                        if (message.media.heading != 0) {
                            liveLocation2.directionMarker.setRotation(message.media.heading);
                            liveLocation2.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_cone2));
                            liveLocation2.hasRotation = true;
                        } else {
                            liveLocation2.directionMarker.setRotation(0.0f);
                            liveLocation2.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_circle));
                            liveLocation2.hasRotation = false;
                        }
                    }
                    this.markers.add(liveLocation2);
                    this.markersMap.put(liveLocation2.id, liveLocation2);
                    LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation2.id == getUserConfig().getClientUserId() && myInfo != null && liveLocation2.object.id == myInfo.mid && this.myLocation != null) {
                        LatLng latLng1 = new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude());
                        liveLocation2.marker.setPosition(latLng1);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            liveLocation2.object = message;
            liveLocation2.marker.setPosition(latLng);
        }
        ProximitySheet proximitySheet = this.proximitySheet;
        if (proximitySheet != null) {
            proximitySheet.updateText(true, true);
        }
        return liveLocation2;
    }

    private LiveLocation addUserMarker(TLRPC.TL_channelLocation location) {
        LatLng latLng = new LatLng(location.geo_point.lat, location.geo_point._long);
        LiveLocation liveLocation = new LiveLocation();
        if (DialogObject.isUserDialog(this.dialogId)) {
            liveLocation.user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            liveLocation.chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        }
        liveLocation.id = this.dialogId;
        try {
            MarkerOptions options = new MarkerOptions().position(latLng);
            Bitmap bitmap = createUserBitmap(liveLocation);
            if (bitmap != null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                options.anchor(0.5f, 0.907f);
                liveLocation.marker = this.googleMap.addMarker(options);
                if (!UserObject.isUserSelf(liveLocation.user)) {
                    MarkerOptions dirOptions = new MarkerOptions().position(latLng).flat(true);
                    dirOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_circle));
                    dirOptions.anchor(0.5f, 0.5f);
                    liveLocation.directionMarker = this.googleMap.addMarker(dirOptions);
                }
                this.markers.add(liveLocation);
                this.markersMap.put(liveLocation.id, liveLocation);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return liveLocation;
    }

    private void onMapInit() {
        LocationController.SharingLocationInfo myInfo;
        if (this.googleMap == null) {
            return;
        }
        TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
        if (tL_channelLocation != null) {
            LiveLocation liveLocation = addUserMarker(tL_channelLocation);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
        } else {
            MessageObject messageObject = this.messageObject;
            if (messageObject != null) {
                if (messageObject.isLiveLocation()) {
                    LiveLocation liveLocation2 = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLocation2.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    CameraUpdate position = CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f);
                    this.googleMap.moveCamera(position);
                    this.firstFocus = false;
                    getRecentLocations();
                }
            } else {
                Location location = new Location("network");
                this.userLocation = location;
                if (this.initialLocation != null) {
                    LatLng latLng2 = new LatLng(this.initialLocation.geo_point.lat, this.initialLocation.geo_point._long);
                    GoogleMap googleMap = this.googleMap;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, googleMap.getMaxZoomLevel() - 4.0f));
                    this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                    this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                    this.adapter.setCustomLocation(this.userLocation);
                } else {
                    location.setLatitude(20.659322d);
                    this.userLocation.setLongitude(-11.40625d);
                }
            }
        }
        try {
            this.googleMap.setMyLocationEnabled(true);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2, false);
        }
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda1
            @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
            public final void onCameraMoveStarted(int i) {
                LocationActivity.this.m3716lambda$onMapInit$27$orgtelegramuiLocationActivity(i);
            }
        });
        this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda3
            @Override // com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
            public final void onMyLocationChange(Location location2) {
                LocationActivity.this.m3717lambda$onMapInit$28$orgtelegramuiLocationActivity(location2);
            }
        });
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda2
            @Override // com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
            public final boolean onMarkerClick(Marker marker) {
                return LocationActivity.this.m3718lambda$onMapInit$29$orgtelegramuiLocationActivity(marker);
            }
        });
        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda38
            @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
            public final void onCameraMove() {
                LocationActivity.this.m3719lambda$onMapInit$30$orgtelegramuiLocationActivity();
            }
        });
        Location lastLocation = getLastLocation();
        this.myLocation = lastLocation;
        positionMarker(lastLocation);
        if (this.checkGpsEnabled && getParentActivity() != null) {
            this.checkGpsEnabled = false;
            checkGpsEnabled();
        }
        ImageView imageView = this.proximityButton;
        if (imageView != null && imageView.getVisibility() == 0 && (myInfo = getLocationController().getSharingLocationInfo(this.dialogId)) != null && myInfo.proximityMeters > 0) {
            createCircle(myInfo.proximityMeters);
        }
    }

    /* renamed from: lambda$onMapInit$27$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3716lambda$onMapInit$27$orgtelegramuiLocationActivity(int reason) {
        View view;
        RecyclerView.ViewHolder holder;
        if (reason == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling) {
                int i = this.locationType;
                if ((i == 0 || i == 1) && this.listView.getChildCount() > 0 && (view = this.listView.getChildAt(0)) != null && (holder = this.listView.findContainingViewHolder(view)) != null && holder.getAdapterPosition() == 0) {
                    int min = this.locationType == 0 ? 0 : AndroidUtilities.dp(66.0f);
                    int top = view.getTop();
                    if (top < (-min)) {
                        CameraPosition cameraPosition = this.googleMap.getCameraPosition();
                        this.forceUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
                        this.listView.smoothScrollBy(0, top + min);
                    }
                }
            }
        }
    }

    /* renamed from: lambda$onMapInit$28$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3717lambda$onMapInit$28$orgtelegramuiLocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* renamed from: lambda$onMapInit$29$org-telegram-ui-LocationActivity */
    public /* synthetic */ boolean m3718lambda$onMapInit$29$orgtelegramuiLocationActivity(Marker marker) {
        if (!(marker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag(Theme.key_location_actionIcon);
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(marker);
        return true;
    }

    /* renamed from: lambda$onMapInit$30$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3719lambda$onMapInit$30$orgtelegramuiLocationActivity() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    private boolean checkGpsEnabled() {
        if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
            return true;
        }
        try {
            LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            if (!lm.isProviderEnabled("gps")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda11
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LocationActivity.this.m3690lambda$checkGpsEnabled$31$orgtelegramuiLocationActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
                return false;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return true;
    }

    /* renamed from: lambda$checkGpsEnabled$31$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3690lambda$checkGpsEnabled$31$orgtelegramuiLocationActivity(DialogInterface dialog, int id) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception e) {
        }
    }

    private void createCircle(int meters) {
        if (this.googleMap == null) {
            return;
        }
        List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(new Gap(20.0f), new Dash(20.0f));
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        circleOptions.radius(meters);
        if (isActiveThemeDark()) {
            circleOptions.strokeColor(-1771658281);
            circleOptions.fillColor(476488663);
        } else {
            circleOptions.strokeColor(-1774024971);
            circleOptions.fillColor(474121973);
        }
        circleOptions.strokePattern(PATTERN_POLYGON_ALPHA);
        circleOptions.strokeWidth(2.0f);
        this.proximityCircle = this.googleMap.addCircle(circleOptions);
    }

    private void removeInfoView() {
        if (this.lastPressedMarker != null) {
            this.markerImageView.setVisibility(0);
            this.overlayView.removeInfoView(this.lastPressedMarker);
            this.lastPressedMarker = null;
            this.lastPressedVenue = null;
            this.lastPressedMarkerView = null;
        }
    }

    public void showPermissionAlert(boolean byButton) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
        if (byButton) {
            builder.setMessage(LocaleController.getString("PermissionNoLocationNavigation", R.string.PermissionNoLocationNavigation));
        } else {
            builder.setMessage(LocaleController.getString("PermissionNoLocationFriends", R.string.PermissionNoLocationFriends));
        }
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LocationActivity.this.m3726lambda$showPermissionAlert$32$orgtelegramuiLocationActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showPermissionAlert$32$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3726lambda$showPermissionAlert$32$orgtelegramuiLocationActivity(DialogInterface dialog, int which) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) this.mapView.getParent();
                    viewGroup.removeView(this.mapView);
                }
            } catch (Exception e) {
            }
            FrameLayout frameLayout = this.mapViewClip;
            if (frameLayout != null) {
                frameLayout.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                MapOverlayView mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    try {
                        if (mapOverlayView.getParent() instanceof ViewGroup) {
                            ViewGroup viewGroup2 = (ViewGroup) this.overlayView.getParent();
                            viewGroup2.removeView(this.overlayView);
                        }
                    } catch (Exception e2) {
                    }
                    this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                }
                updateClipView(false);
                maybeShowProximityHint();
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    public void maybeShowProximityHint() {
        SharedPreferences preferences;
        int val;
        ImageView imageView = this.proximityButton;
        if (imageView != null && imageView.getVisibility() == 0 && !this.proximityAnimationInProgress && (val = (preferences = MessagesController.getGlobalMainSettings()).getInt("proximityhint", 0)) < 3) {
            preferences.edit().putInt("proximityhint", val + 1).commit();
            if (DialogObject.isUserDialog(this.dialogId)) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
                this.hintView.setOverrideText(LocaleController.formatString("ProximityTooltioUser", R.string.ProximityTooltioUser, UserObject.getFirstName(user)));
            } else {
                this.hintView.setOverrideText(LocaleController.getString("ProximityTooltioGroup", R.string.ProximityTooltioGroup));
            }
            this.hintView.showForView(this.proximityButton, true);
        }
    }

    private void showResults() {
        if (this.adapter.getItemCount() == 0) {
            return;
        }
        int position = this.layoutManager.findFirstVisibleItemPosition();
        if (position != 0) {
            return;
        }
        View child = this.listView.getChildAt(0);
        int offset = AndroidUtilities.dp(258.0f) + child.getTop();
        if (offset < 0 || offset > AndroidUtilities.dp(258.0f)) {
            return;
        }
        this.listView.smoothScrollBy(0, offset);
    }

    public void updateClipView(boolean fromLayout) {
        int top;
        FrameLayout.LayoutParams layoutParams;
        int height = 0;
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            top = (int) holder.itemView.getY();
            height = this.overScrollHeight + Math.min(top, 0);
        } else {
            top = -this.mapViewClip.getMeasuredHeight();
        }
        if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
            if (height > 0) {
                if (this.mapView.getVisibility() == 4) {
                    this.mapView.setVisibility(0);
                    this.mapViewClip.setVisibility(0);
                    MapOverlayView mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setVisibility(0);
                    }
                }
            } else if (this.mapView.getVisibility() == 0) {
                this.mapView.setVisibility(4);
                this.mapViewClip.setVisibility(4);
                MapOverlayView mapOverlayView2 = this.overlayView;
                if (mapOverlayView2 != null) {
                    mapOverlayView2.setVisibility(4);
                }
            }
            this.mapViewClip.setTranslationY(Math.min(0, top));
            this.mapView.setTranslationY(Math.max(0, (-top) / 2));
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setTranslationY(Math.max(0, (-top) / 2));
            }
            int measuredHeight = this.overScrollHeight - this.mapTypeButton.getMeasuredHeight();
            int i = this.locationType;
            float translationY = Math.min(measuredHeight - AndroidUtilities.dp(64 + ((i == 0 || i == 1) ? 30 : 10)), -top);
            this.mapTypeButton.setTranslationY(translationY);
            this.proximityButton.setTranslationY(translationY);
            HintView hintView = this.hintView;
            if (hintView != null) {
                hintView.setExtraTranslationY(translationY);
            }
            SearchButton searchButton = this.searchAreaButton;
            if (searchButton != null) {
                searchButton.setTranslation(translationY);
            }
            View view = this.markerImageView;
            if (view != null) {
                int dp = ((-top) - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (height / 2);
                this.markerTop = dp;
                view.setTranslationY(dp);
            }
            if (!fromLayout) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams2 != null && layoutParams2.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    GoogleMap googleMap = this.googleMap;
                    if (googleMap != null) {
                        googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams2);
                }
                MapOverlayView mapOverlayView4 = this.overlayView;
                if (mapOverlayView4 != null && (layoutParams = (FrameLayout.LayoutParams) mapOverlayView4.getLayoutParams()) != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    this.overlayView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void fixLayoutInternal(boolean resume) {
        final int top;
        FrameLayout.LayoutParams layoutParams;
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight == 0) {
                return;
            }
            int i = this.locationType;
            if (i == 6) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
            } else if (i == 2) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(73.0f)) - height;
            } else {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            layoutParams2.topMargin = height;
            this.listView.setLayoutParams(layoutParams2);
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
            layoutParams3.topMargin = height;
            layoutParams3.height = this.overScrollHeight;
            this.mapViewClip.setLayoutParams(layoutParams3);
            RecyclerListView recyclerListView = this.searchListView;
            if (recyclerListView != null) {
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
                layoutParams4.topMargin = height;
                this.searchListView.setLayoutParams(layoutParams4);
            }
            this.adapter.setOverScrollHeight(this.overScrollHeight);
            FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
            if (layoutParams5 != null) {
                layoutParams5.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                GoogleMap googleMap = this.googleMap;
                if (googleMap != null) {
                    googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                }
                this.mapView.setLayoutParams(layoutParams5);
            }
            MapOverlayView mapOverlayView = this.overlayView;
            if (mapOverlayView != null && (layoutParams = (FrameLayout.LayoutParams) mapOverlayView.getLayoutParams()) != null) {
                layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                this.overlayView.setLayoutParams(layoutParams);
            }
            this.adapter.notifyDataSetChanged();
            if (resume) {
                int i2 = this.locationType;
                if (i2 == 3) {
                    top = 73;
                } else if (i2 == 1 || i2 == 2) {
                    top = 66;
                } else {
                    top = 0;
                }
                this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp(top));
                updateClipView(false);
                this.listView.post(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocationActivity.this.m3711lambda$fixLayoutInternal$33$orgtelegramuiLocationActivity(top);
                    }
                });
                return;
            }
            updateClipView(false);
        }
    }

    /* renamed from: lambda$fixLayoutInternal$33$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3711lambda$fixLayoutInternal$33$orgtelegramuiLocationActivity(int top) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp(top));
        updateClipView(false);
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) {
                break;
            }
        }
        return l;
    }

    private void positionMarker(Location location) {
        if (location == null) {
            return;
        }
        this.myLocation = new Location(location);
        LiveLocation liveLocation = this.markersMap.get(getUserConfig().getClientUserId());
        LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
        if (liveLocation != null && myInfo != null && liveLocation.object.id == myInfo.mid) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            liveLocation.marker.setPosition(latLng);
            if (liveLocation.directionMarker != null) {
                liveLocation.directionMarker.setPosition(latLng);
            }
        }
        if (this.messageObject == null && this.chatLocation == null && this.googleMap != null) {
            LatLng latLng2 = new LatLng(location.getLatitude(), location.getLongitude());
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                if (!this.searchedForCustomLocations && this.locationType != 4) {
                    locationActivityAdapter.searchPlacesWithQuery(null, this.myLocation, true);
                }
                this.adapter.setGpsLocation(this.myLocation);
            }
            if (!this.userLocationMoved) {
                this.userLocation = new Location(location);
                if (this.firstWas) {
                    CameraUpdate position = CameraUpdateFactory.newLatLng(latLng2);
                    this.googleMap.animateCamera(position);
                } else {
                    this.firstWas = true;
                    CameraUpdate position2 = CameraUpdateFactory.newLatLngZoom(latLng2, this.googleMap.getMaxZoomLevel() - 4.0f);
                    this.googleMap.moveCamera(position2);
                }
            }
        } else {
            this.adapter.setGpsLocation(this.myLocation);
        }
        ProximitySheet proximitySheet = this.proximitySheet;
        if (proximitySheet != null) {
            proximitySheet.updateText(true, true);
        }
        Circle circle = this.proximityCircle;
        if (circle != null) {
            circle.setCenter(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        }
    }

    public void setMessageObject(MessageObject message) {
        this.messageObject = message;
        this.dialogId = message.getDialogId();
    }

    public void setChatLocation(long chatId, TLRPC.TL_channelLocation location) {
        this.dialogId = -chatId;
        this.chatLocation = location;
    }

    public void setDialogId(long did) {
        this.dialogId = did;
    }

    public void setInitialLocation(TLRPC.TL_channelLocation location) {
        this.initialLocation = location;
    }

    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    private void fetchRecentLocations(ArrayList<TLRPC.Message> messages) {
        LatLngBounds.Builder builder = null;
        if (this.firstFocus) {
            builder = new LatLngBounds.Builder();
        }
        int date = getConnectionsManager().getCurrentTime();
        for (int a = 0; a < messages.size(); a++) {
            TLRPC.Message message = messages.get(a);
            if (message.date + message.media.period > date) {
                if (builder != null) {
                    LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
                    builder.include(latLng);
                }
                addUserMarker(message);
                if (this.proximityButton.getVisibility() != 8 && MessageObject.getFromChatId(message) != getUserConfig().getClientUserId()) {
                    this.proximityButton.setVisibility(0);
                    this.proximityAnimationInProgress = true;
                    this.proximityButton.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LocationActivity.15
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            LocationActivity.this.proximityAnimationInProgress = false;
                            LocationActivity.this.maybeShowProximityHint();
                        }
                    }).start();
                }
            }
        }
        if (builder != null) {
            if (this.firstFocus) {
                this.listView.smoothScrollBy(0, AndroidUtilities.dp(99.0f));
            }
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLngBounds bounds = builder.build();
                    LatLng center = bounds.getCenter();
                    LatLng northEast = move(center, 100.0d, 100.0d);
                    LatLng southWest = move(center, -100.0d, -100.0d);
                    builder.include(southWest);
                    builder.include(northEast);
                    LatLngBounds bounds2 = builder.build();
                    if (messages.size() > 1) {
                        try {
                            CameraUpdate newLatLngBounds = CameraUpdateFactory.newLatLngBounds(bounds2, AndroidUtilities.dp(113.0f));
                            this.moveToBounds = newLatLngBounds;
                            this.googleMap.moveCamera(newLatLngBounds);
                            this.moveToBounds = null;
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:12:0x0087 -> B:29:0x0116). Please submit an issue!!! */
    private void moveToBounds(int radius, boolean self, boolean animated) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        if (self) {
            try {
                int radius2 = Math.max(radius, (int) ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
                LatLngBounds bounds = builder.build();
                LatLng center = bounds.getCenter();
                LatLng northEast = move(center, radius2, radius2);
                LatLng southWest = move(center, -radius2, -radius2);
                builder.include(southWest);
                builder.include(northEast);
                LatLngBounds bounds2 = builder.build();
                try {
                    int height = (int) ((this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(40.0f)) + this.mapViewClip.getTranslationY());
                    this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), height);
                    if (animated) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds2, 0), 500, null);
                    } else {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds2, 0));
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return;
            } catch (Exception e2) {
                return;
            }
        }
        int date = getConnectionsManager().getCurrentTime();
        int N = this.markers.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Message message = this.markers.get(a).object;
            if (message.date + message.media.period > date) {
                LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
                builder.include(latLng);
            }
        }
        try {
            LatLngBounds bounds3 = builder.build();
            LatLng center2 = bounds3.getCenter();
            LatLng northEast2 = move(center2, 100.0d, 100.0d);
            LatLng southWest2 = move(center2, -100.0d, -100.0d);
            builder.include(southWest2);
            builder.include(northEast2);
            LatLngBounds bounds4 = builder.build();
            try {
                int height2 = this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(100.0f);
                this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), height2);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds4, 0));
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        } catch (Exception e4) {
        }
    }

    private boolean getRecentLocations() {
        ArrayList<TLRPC.Message> messages = getLocationController().locationsCache.get(this.messageObject.getDialogId());
        if (messages != null && messages.isEmpty()) {
            fetchRecentLocations(messages);
        } else {
            messages = null;
        }
        if (DialogObject.isChatDialog(this.dialogId)) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
        final long dialog_id = this.messageObject.getDialogId();
        req.peer = getMessagesController().getInputPeer(dialog_id);
        req.limit = 100;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda18
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationActivity.this.m3714lambda$getRecentLocations$36$orgtelegramuiLocationActivity(dialog_id, tLObject, tL_error);
            }
        });
        return messages != null;
    }

    /* renamed from: lambda$getRecentLocations$36$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3714lambda$getRecentLocations$36$orgtelegramuiLocationActivity(final long dialog_id, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    LocationActivity.this.m3713lambda$getRecentLocations$35$orgtelegramuiLocationActivity(response, dialog_id);
                }
            });
        }
    }

    /* renamed from: lambda$getRecentLocations$35$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3713lambda$getRecentLocations$35$orgtelegramuiLocationActivity(TLObject response, long dialog_id) {
        if (this.googleMap == null) {
            return;
        }
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
        getLocationController().locationsCache.put(dialog_id, res.messages);
        getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
        fetchRecentLocations(res.messages);
        getLocationController().markLiveLoactionsAsRead(this.dialogId);
        if (this.markAsReadRunnable == null) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LocationActivity.this.m3712lambda$getRecentLocations$34$orgtelegramuiLocationActivity();
                }
            };
            this.markAsReadRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }

    /* renamed from: lambda$getRecentLocations$34$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3712lambda$getRecentLocations$34$orgtelegramuiLocationActivity() {
        Runnable runnable;
        getLocationController().markLiveLoactionsAsRead(this.dialogId);
        if (this.isPaused || (runnable = this.markAsReadRunnable) == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(runnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double lat1 = (latLng1.latitude * 3.141592653589793d) / 180.0d;
        double long1 = (latLng1.longitude * 3.141592653589793d) / 180.0d;
        double lat2 = (latLng2.latitude * 3.141592653589793d) / 180.0d;
        double long2 = (latLng2.longitude * 3.141592653589793d) / 180.0d;
        double dLon = long2 - long1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = (Math.cos(lat1) * Math.sin(lat2)) - ((Math.sin(lat1) * Math.cos(lat2)) * Math.cos(dLon));
        double brng = Math.atan2(y, x);
        return (Math.toDegrees(brng) + 360.0d) % 360.0d;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        LocationActivityAdapter locationActivityAdapter;
        LiveLocation liveLocation;
        LocationActivityAdapter locationActivityAdapter2;
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter3 = this.adapter;
            if (locationActivityAdapter3 != null) {
                locationActivityAdapter3.setMyLocationDenied(false);
            }
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } else if (id == NotificationCenter.locationPermissionDenied) {
            this.locationDenied = true;
            LocationActivityAdapter locationActivityAdapter4 = this.adapter;
            if (locationActivityAdapter4 != null) {
                locationActivityAdapter4.setMyLocationDenied(true);
            }
        } else if (id == NotificationCenter.liveLocationsChanged) {
            LocationActivityAdapter locationActivityAdapter5 = this.adapter;
            if (locationActivityAdapter5 != null) {
                locationActivityAdapter5.updateLiveLocationCell();
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled || ((Long) args[0]).longValue() != this.dialogId || this.messageObject == null) {
                return;
            }
            ArrayList<MessageObject> arr = (ArrayList) args[1];
            boolean added = false;
            for (int a = 0; a < arr.size(); a++) {
                MessageObject messageObject = arr.get(a);
                if (messageObject.isLiveLocation()) {
                    addUserMarker(messageObject.messageOwner);
                    added = true;
                } else if ((messageObject.messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) && DialogObject.isUserDialog(messageObject.getDialogId())) {
                    this.proximityButton.setImageResource(R.drawable.msg_location_alert);
                    Circle circle = this.proximityCircle;
                    if (circle != null) {
                        circle.remove();
                        this.proximityCircle = null;
                    }
                }
            }
            if (added && (locationActivityAdapter2 = this.adapter) != null) {
                locationActivityAdapter2.setLiveLocations(this.markers);
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            long did = ((Long) args[0]).longValue();
            if (did != this.dialogId || this.messageObject == null) {
                return;
            }
            boolean updated = false;
            ArrayList<MessageObject> messageObjects = (ArrayList) args[1];
            for (int a2 = 0; a2 < messageObjects.size(); a2++) {
                MessageObject messageObject2 = messageObjects.get(a2);
                if (messageObject2.isLiveLocation() && (liveLocation = this.markersMap.get(getMessageId(messageObject2.messageOwner))) != null) {
                    LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(did);
                    if (myInfo == null || myInfo.mid != messageObject2.getId()) {
                        liveLocation.object = messageObject2.messageOwner;
                        LatLng latLng = new LatLng(messageObject2.messageOwner.media.geo.lat, messageObject2.messageOwner.media.geo._long);
                        liveLocation.marker.setPosition(latLng);
                        if (liveLocation.directionMarker != null) {
                            liveLocation.directionMarker.getPosition();
                            liveLocation.directionMarker.setPosition(latLng);
                            if (messageObject2.messageOwner.media.heading != 0) {
                                liveLocation.directionMarker.setRotation(messageObject2.messageOwner.media.heading);
                                if (!liveLocation.hasRotation) {
                                    liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_cone2));
                                    liveLocation.hasRotation = true;
                                }
                            } else if (liveLocation.hasRotation) {
                                liveLocation.directionMarker.setRotation(0.0f);
                                liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_circle));
                                liveLocation.hasRotation = false;
                            }
                        }
                    }
                    updated = true;
                }
            }
            if (updated && (locationActivityAdapter = this.adapter) != null) {
                locationActivityAdapter.updateLiveLocations();
                ProximitySheet proximitySheet = this.proximitySheet;
                if (proximitySheet != null) {
                    proximitySheet.updateText(true, true);
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        this.onResumeCalled = false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        ProximitySheet proximitySheet = this.proximitySheet;
        if (proximitySheet != null) {
            proximitySheet.dismiss();
            return false;
        }
        return super.onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        Activity activity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
        GoogleMap googleMap = this.googleMap;
        if (googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
        Runnable runnable = this.markAsReadRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            AndroidUtilities.runOnUIThread(this.markAsReadRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 30) {
            openShareLiveLocation(this.askWithRadius);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            mapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    private void updateSearchInterface() {
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.LocationActivity$$ExternalSyntheticLambda21
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                LocationActivity.this.m3715lambda$getThemeDescriptions$37$orgtelegramuiLocationActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        for (int a = 0; a < this.undoView.length; a++) {
            themeDescriptions.add(new ThemeDescription(this.undoView[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "BODY", Theme.key_undo_background));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big", Theme.key_undo_background));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big 3", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Small", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Main.**", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Top.**", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Line.**", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Big.**", Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Small.**", Theme.key_undo_infoColor));
        }
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, cellDelegate, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_dialogButtonSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_chat_messagePanelHint));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, cellDelegate, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, cellDelegate, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, cellDelegate, Theme.key_actionBarDefaultSubmenuItemIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.shadow, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_location_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_location_actionActiveIcon));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, 0, null, null, null, cellDelegate, Theme.key_location_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, 0, null, null, null, cellDelegate, Theme.key_location_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_location_actionActiveIcon));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, cellDelegate, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_liveLocationProgress));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_placeLocationBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialog_liveLocationProgress));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLocationIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLiveLocationIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLocationBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLiveLocationBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLiveLocationText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_sendLocationText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButtonPressed));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlue2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogEmptyText));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$37$org-telegram-ui-LocationActivity */
    public /* synthetic */ void m3715lambda$getThemeDescriptions$37$orgtelegramuiLocationActivity() {
        this.mapTypeButton.setIconColor(Theme.getColor(Theme.key_location_actionIcon));
        this.mapTypeButton.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.shadow.invalidate();
        if (this.googleMap != null) {
            if (isActiveThemeDark()) {
                if (!this.currentMapStyleDark) {
                    this.currentMapStyleDark = true;
                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night);
                    this.googleMap.setMapStyle(style);
                    Circle circle = this.proximityCircle;
                    if (circle != null) {
                        circle.setStrokeColor(-1);
                        this.proximityCircle.setFillColor(553648127);
                    }
                }
            } else if (this.currentMapStyleDark) {
                this.currentMapStyleDark = false;
                this.googleMap.setMapStyle(null);
                Circle circle2 = this.proximityCircle;
                if (circle2 != null) {
                    circle2.setStrokeColor(-16777216);
                    this.proximityCircle.setFillColor(536870912);
                }
            }
        }
    }
}
