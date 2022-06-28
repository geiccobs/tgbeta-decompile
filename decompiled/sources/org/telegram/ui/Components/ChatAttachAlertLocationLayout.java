package org.telegram.ui.Components;

import android.animation.Animator;
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
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ChatAttachAlertLocationLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int LOCATION_TYPE_SEND = 0;
    public static final int LOCATION_TYPE_SEND_WITH_LIVE = 1;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private Paint backgroundPaint;
    private Bitmap[] bitmapCache;
    private CircleOptions circleOptions;
    private int clipSize;
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
    private boolean ignoreLayout;
    private Marker lastPressedMarker;
    private FrameLayout lastPressedMarkerView;
    private VenueLocation lastPressedVenue;
    private FillLastLinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private View loadingMapView;
    private ImageView locationButton;
    private boolean locationDenied;
    private int locationType;
    private int mapHeight;
    private ActionBarMenuItem mapTypeButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private ImageView markerImageView;
    private int markerTop;
    private Location myLocation;
    private int nonClipSize;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight;
    private MapOverlayView overlayView;
    private ArrayList<VenueLocation> placeMarkers;
    private boolean scrolling;
    private LocationActivitySearchAdapter searchAdapter;
    private SearchButton searchAreaButton;
    private boolean searchInProgress;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searchedForCustomLocations;
    private boolean searching;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;
    private float yOffset;
    private boolean checkGpsEnabled = true;
    private boolean isFirstLocation = true;
    private boolean firstFocus = true;
    private boolean checkPermission = true;
    private boolean checkBackgroundPermission = true;
    private boolean first = true;

    /* loaded from: classes5.dex */
    public static class LiveLocation {
        public TLRPC.Chat chat;
        public int id;
        public Marker marker;
        public TLRPC.Message object;
        public TLRPC.User user;
    }

    /* loaded from: classes5.dex */
    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    /* loaded from: classes5.dex */
    public static class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC.TL_messageMediaVenue venue;
    }

    static /* synthetic */ float access$2816(ChatAttachAlertLocationLayout x0, float x1) {
        float f = x0.yOffset + x1;
        x0.yOffset = f;
        return f;
    }

    /* loaded from: classes5.dex */
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

    /* loaded from: classes5.dex */
    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MapOverlayView(Context context) {
            super(context);
            ChatAttachAlertLocationLayout.this = this$0;
        }

        public void addInfoView(Marker marker) {
            final VenueLocation location = (VenueLocation) marker.getTag();
            if (ChatAttachAlertLocationLayout.this.lastPressedVenue != location) {
                ChatAttachAlertLocationLayout.this.showSearchPlacesButton(false);
                if (ChatAttachAlertLocationLayout.this.lastPressedMarker != null) {
                    removeInfoView(ChatAttachAlertLocationLayout.this.lastPressedMarker);
                    ChatAttachAlertLocationLayout.this.lastPressedMarker = null;
                }
                ChatAttachAlertLocationLayout.this.lastPressedVenue = location;
                ChatAttachAlertLocationLayout.this.lastPressedMarker = marker;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView = new FrameLayout(context);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setBackgroundResource(R.drawable.venue_tooltip);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setAlpha(0.0f);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatAttachAlertLocationLayout.MapOverlayView.this.m2474x73b338e0(location, view);
                    }
                });
                TextView nameTextView = new TextView(context);
                nameTextView.setTextSize(1, 16.0f);
                nameTextView.setMaxLines(1);
                nameTextView.setEllipsize(TextUtils.TruncateAt.END);
                nameTextView.setSingleLine(true);
                nameTextView.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
                nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                int i = 5;
                nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.addView(nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView addressTextView = new TextView(context);
                addressTextView.setTextSize(1, 14.0f);
                addressTextView.setMaxLines(1);
                addressTextView.setEllipsize(TextUtils.TruncateAt.END);
                addressTextView.setSingleLine(true);
                addressTextView.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
                addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout frameLayout2 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView;
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
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.MapOverlayView.1
                    private final float[] animatorValues = {0.0f, 1.0f};
                    private boolean startedInner;

                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale;
                        float value = AndroidUtilities.lerp(this.animatorValues, animation.getAnimatedFraction());
                        if (value >= 0.7f && !this.startedInner && ChatAttachAlertLocationLayout.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.playTogether(ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.ALPHA, 0.0f, 1.0f));
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
                ChatAttachAlertLocationLayout.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
            }
        }

        /* renamed from: lambda$addInfoView$1$org-telegram-ui-Components-ChatAttachAlertLocationLayout$MapOverlayView */
        public /* synthetic */ void m2474x73b338e0(final VenueLocation location, View v) {
            ChatActivity chatActivity = (ChatActivity) ChatAttachAlertLocationLayout.this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(ChatAttachAlertLocationLayout.this.getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.MapOverlayView.this.m2473xaca751df(location, z, i);
                    }
                }, ChatAttachAlertLocationLayout.this.resourcesProvider);
                return;
            }
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(location.venue, ChatAttachAlertLocationLayout.this.locationType, true, 0);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
        }

        /* renamed from: lambda$addInfoView$0$org-telegram-ui-Components-ChatAttachAlertLocationLayout$MapOverlayView */
        public /* synthetic */ void m2473xaca751df(VenueLocation location, boolean notify, int scheduleDate) {
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(location.venue, ChatAttachAlertLocationLayout.this.locationType, notify, scheduleDate);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
        }

        public void removeInfoView(Marker marker) {
            View view = this.views.get(marker);
            if (view != null) {
                removeView(view);
                this.views.remove(marker);
            }
        }

        public void updatePositions() {
            if (ChatAttachAlertLocationLayout.this.googleMap != null) {
                Projection projection = ChatAttachAlertLocationLayout.this.googleMap.getProjection();
                for (Map.Entry<Marker, View> entry : this.views.entrySet()) {
                    Marker marker = entry.getKey();
                    View view = entry.getValue();
                    android.graphics.Point point = projection.toScreenLocation(marker.getPosition());
                    view.setTranslationX(point.x - (view.getMeasuredWidth() / 2));
                    view.setTranslationY((point.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f));
                }
            }
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertLocationLayout(ChatAttachAlert alert, Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        Drawable drawable;
        Drawable drawable2;
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = this;
        chatAttachAlertLocationLayout.locationDenied = false;
        chatAttachAlertLocationLayout.backgroundPaint = new Paint();
        chatAttachAlertLocationLayout.placeMarkers = new ArrayList<>();
        int currentActionBarHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
        chatAttachAlertLocationLayout.overScrollHeight = currentActionBarHeight;
        chatAttachAlertLocationLayout.mapHeight = currentActionBarHeight;
        chatAttachAlertLocationLayout.bitmapCache = new Bitmap[7];
        AndroidUtilities.fixGoogleMapsBug();
        final ChatActivity chatActivity = (ChatActivity) chatAttachAlertLocationLayout.parentAlert.baseFragment;
        chatAttachAlertLocationLayout.dialogId = chatActivity.getDialogId();
        if (chatActivity.getCurrentEncryptedChat() == null && !chatActivity.isInScheduleMode() && !UserObject.isUserSelf(chatActivity.getCurrentUser())) {
            chatAttachAlertLocationLayout.locationType = 1;
        } else {
            chatAttachAlertLocationLayout.locationType = 0;
        }
        NotificationCenter.getGlobalInstance().addObserver(chatAttachAlertLocationLayout, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(chatAttachAlertLocationLayout, NotificationCenter.locationPermissionDenied);
        chatAttachAlertLocationLayout.searchWas = false;
        chatAttachAlertLocationLayout.searching = false;
        chatAttachAlertLocationLayout.searchInProgress = false;
        LocationActivityAdapter locationActivityAdapter = chatAttachAlertLocationLayout.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = chatAttachAlertLocationLayout.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        chatAttachAlertLocationLayout.locationDenied = (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        ActionBarMenu menu = chatAttachAlertLocationLayout.parentAlert.actionBar.createMenu();
        chatAttachAlertLocationLayout.overlayView = new MapOverlayView(context);
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.1
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChatAttachAlertLocationLayout.this.searching = true;
                ChatAttachAlertLocationLayout.this.parentAlert.makeFocusable(ChatAttachAlertLocationLayout.this.searchItem.getSearchField(), true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChatAttachAlertLocationLayout.this.searching = false;
                ChatAttachAlertLocationLayout.this.searchWas = false;
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(null, null);
                ChatAttachAlertLocationLayout.this.updateEmptyView();
                if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                    ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                }
                ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                if (ChatAttachAlertLocationLayout.this.searchAdapter == null) {
                    return;
                }
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ChatAttachAlertLocationLayout.this.searchWas = true;
                    ChatAttachAlertLocationLayout.this.searchItem.setShowSearchProgress(true);
                    if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                        ChatAttachAlertLocationLayout.this.otherItem.setVisibility(8);
                    }
                    ChatAttachAlertLocationLayout.this.listView.setVisibility(8);
                    ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(8);
                    if (ChatAttachAlertLocationLayout.this.searchListView.getAdapter() != ChatAttachAlertLocationLayout.this.searchAdapter) {
                        ChatAttachAlertLocationLayout.this.searchListView.setAdapter(ChatAttachAlertLocationLayout.this.searchAdapter);
                    }
                    ChatAttachAlertLocationLayout.this.searchListView.setVisibility(0);
                    ChatAttachAlertLocationLayout chatAttachAlertLocationLayout2 = ChatAttachAlertLocationLayout.this;
                    chatAttachAlertLocationLayout2.searchInProgress = chatAttachAlertLocationLayout2.searchAdapter.isEmpty();
                    ChatAttachAlertLocationLayout.this.updateEmptyView();
                } else {
                    if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                        ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                    }
                    ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                    ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                    ChatAttachAlertLocationLayout.this.searchListView.setAdapter(null);
                    ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                    ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
                }
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(text, ChatAttachAlertLocationLayout.this.userLocation);
            }
        });
        chatAttachAlertLocationLayout.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setVisibility(chatAttachAlertLocationLayout.locationDenied ? 8 : 0);
        chatAttachAlertLocationLayout.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        chatAttachAlertLocationLayout.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        EditTextBoldCursor editText = chatAttachAlertLocationLayout.searchItem.getSearchField();
        editText.setTextColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_dialogTextBlack));
        editText.setCursorColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_chat_messagePanelHint));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f));
        layoutParams.gravity = 83;
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.2
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (ChatAttachAlertLocationLayout.this.overlayView != null) {
                    ChatAttachAlertLocationLayout.this.overlayView.updatePositions();
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize);
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                ChatAttachAlertLocationLayout.this.backgroundPaint.setColor(ChatAttachAlertLocationLayout.this.getThemedColor(Theme.key_dialogBackground));
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize, ChatAttachAlertLocationLayout.this.backgroundPaint);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getY() > getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize) {
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getY() > getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize) {
                    return false;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        chatAttachAlertLocationLayout.mapViewClip = frameLayout;
        frameLayout.setWillNotDraw(false);
        View view = new View(context);
        chatAttachAlertLocationLayout.loadingMapView = view;
        view.setBackgroundDrawable(new MapPlaceholderDrawable());
        SearchButton searchButton = new SearchButton(context);
        chatAttachAlertLocationLayout.searchAreaButton = searchButton;
        searchButton.setTranslationX(-AndroidUtilities.dp(80.0f));
        chatAttachAlertLocationLayout.searchAreaButton.setVisibility(4);
        Drawable drawable3 = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionBackground), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.places_btn).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable3, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            combinedDrawable.setFullsize(true);
            drawable = combinedDrawable;
        } else {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.searchAreaButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.searchAreaButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            chatAttachAlertLocationLayout.searchAreaButton.setStateListAnimator(animator);
            chatAttachAlertLocationLayout.searchAreaButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view2, Outline outline) {
                    outline.setRoundRect(0, 0, view2.getMeasuredWidth(), view2.getMeasuredHeight(), view2.getMeasuredHeight() / 2);
                }
            });
            drawable = drawable3;
        }
        chatAttachAlertLocationLayout.searchAreaButton.setBackgroundDrawable(drawable);
        chatAttachAlertLocationLayout.searchAreaButton.setTextColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionActiveIcon));
        chatAttachAlertLocationLayout.searchAreaButton.setTextSize(1, 14.0f);
        chatAttachAlertLocationLayout.searchAreaButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        chatAttachAlertLocationLayout.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", R.string.PlacesInThisArea));
        chatAttachAlertLocationLayout.searchAreaButton.setGravity(17);
        chatAttachAlertLocationLayout.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
        chatAttachAlertLocationLayout.searchAreaButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.m2450x293a5bd2(view2);
            }
        });
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionIcon), resourcesProvider);
        chatAttachAlertLocationLayout.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        chatAttachAlertLocationLayout.mapTypeButton.setSubMenuOpenSide(2);
        chatAttachAlertLocationLayout.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        chatAttachAlertLocationLayout.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(2, R.drawable.msg_map, LocaleController.getString("Map", R.string.Map), resourcesProvider);
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(3, R.drawable.msg_satellite, LocaleController.getString("Satellite", R.string.Satellite), resourcesProvider);
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(4, R.drawable.msg_hybrid, LocaleController.getString("Hybrid", R.string.Hybrid), resourcesProvider);
        chatAttachAlertLocationLayout.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        Drawable drawable4 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionBackground), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable2, drawable4, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable4 = combinedDrawable2;
        } else {
            StateListAnimator animator2 = new StateListAnimator();
            animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.mapTypeButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            chatAttachAlertLocationLayout = this;
            animator2.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.mapTypeButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            chatAttachAlertLocationLayout.mapTypeButton.setStateListAnimator(animator2);
            chatAttachAlertLocationLayout.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.4
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view2, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        chatAttachAlertLocationLayout.mapTypeButton.setBackgroundDrawable(drawable4);
        chatAttachAlertLocationLayout.mapTypeButton.setIcon(R.drawable.msg_map_type);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        chatAttachAlertLocationLayout.mapTypeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.m2451xb62772f1(view2);
            }
        });
        chatAttachAlertLocationLayout.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                ChatAttachAlertLocationLayout.this.m2458x43148a10(i);
            }
        });
        chatAttachAlertLocationLayout.locationButton = new ImageView(context);
        Drawable drawable5 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionBackground), chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable3 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(shadowDrawable3, drawable5, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable2 = combinedDrawable3;
        } else {
            StateListAnimator animator3 = new StateListAnimator();
            animator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.locationButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator3.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.locationButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            chatAttachAlertLocationLayout.locationButton.setStateListAnimator(animator3);
            chatAttachAlertLocationLayout.locationButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.5
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view2, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
            drawable2 = drawable5;
        }
        chatAttachAlertLocationLayout.locationButton.setBackgroundDrawable(drawable2);
        chatAttachAlertLocationLayout.locationButton.setImageResource(R.drawable.msg_current_location);
        chatAttachAlertLocationLayout.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        chatAttachAlertLocationLayout.locationButton.setColorFilter(new PorterDuffColorFilter(chatAttachAlertLocationLayout.getThemedColor(Theme.key_location_actionActiveIcon), PorterDuff.Mode.MULTIPLY));
        chatAttachAlertLocationLayout.locationButton.setTag(Theme.key_location_actionActiveIcon);
        chatAttachAlertLocationLayout.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", R.string.AccDescrMyLocation));
        FrameLayout.LayoutParams layoutParams1 = LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.locationButton, layoutParams1);
        chatAttachAlertLocationLayout.locationButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.m2459xd001a12f(view2);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        chatAttachAlertLocationLayout.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        chatAttachAlertLocationLayout.emptyView.setGravity(1);
        chatAttachAlertLocationLayout.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        chatAttachAlertLocationLayout.emptyView.setVisibility(8);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        chatAttachAlertLocationLayout.emptyView.setOnTouchListener(ChatAttachAlertLocationLayout$$ExternalSyntheticLambda22.INSTANCE);
        ImageView imageView = new ImageView(context);
        chatAttachAlertLocationLayout.emptyImageView = imageView;
        imageView.setImageResource(R.drawable.location_empty);
        chatAttachAlertLocationLayout.emptyImageView.setColorFilter(new PorterDuffColorFilter(chatAttachAlertLocationLayout.getThemedColor(Theme.key_dialogEmptyImage), PorterDuff.Mode.MULTIPLY));
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        chatAttachAlertLocationLayout.emptyTitleTextView = textView;
        textView.setTextColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_dialogEmptyText));
        chatAttachAlertLocationLayout.emptyTitleTextView.setGravity(17);
        chatAttachAlertLocationLayout.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        chatAttachAlertLocationLayout.emptyTitleTextView.setTextSize(1, 17.0f);
        chatAttachAlertLocationLayout.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", R.string.NoPlacesFound));
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context);
        chatAttachAlertLocationLayout.emptySubtitleTextView = textView2;
        textView2.setTextColor(chatAttachAlertLocationLayout.getThemedColor(Theme.key_dialogEmptyText));
        chatAttachAlertLocationLayout.emptySubtitleTextView.setGravity(17);
        chatAttachAlertLocationLayout.emptySubtitleTextView.setTextSize(1, 15.0f);
        chatAttachAlertLocationLayout.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.6
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        };
        chatAttachAlertLocationLayout.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = chatAttachAlertLocationLayout.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, chatAttachAlertLocationLayout.locationType, chatAttachAlertLocationLayout.dialogId, true, resourcesProvider);
        chatAttachAlertLocationLayout.adapter = locationActivityAdapter2;
        recyclerListView2.setAdapter(locationActivityAdapter2);
        chatAttachAlertLocationLayout.adapter.setUpdateRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        });
        chatAttachAlertLocationLayout.adapter.setMyLocationDenied(chatAttachAlertLocationLayout.locationDenied);
        chatAttachAlertLocationLayout.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = chatAttachAlertLocationLayout.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 1, false, 0, chatAttachAlertLocationLayout.listView) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.7
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.7.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view2, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view2, snapPreference);
                        return dy - (ChatAttachAlertLocationLayout.this.listView.getPaddingTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 4;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        chatAttachAlertLocationLayout.layoutManager = fillLastLinearLayoutManager;
        recyclerListView3.setLayoutManager(fillLastLinearLayoutManager);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.listView, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                ChatAttachAlertLocationLayout.this.scrolling = newState != 0;
                if (!ChatAttachAlertLocationLayout.this.scrolling && ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.this.forceUpdate = null;
                }
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertLocationLayout.this.parentAlert.getBackgroundPaddingTop();
                    int top = (ChatAttachAlertLocationLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset;
                    if (top + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertLocationLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight) {
                        ChatAttachAlertLocationLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertLocationLayout.this.updateClipView();
                if (ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.access$2816(ChatAttachAlertLocationLayout.this, dy);
                }
                ChatAttachAlertLocationLayout.this.parentAlert.updateLayout(ChatAttachAlertLocationLayout.this, true, dy);
            }
        });
        chatAttachAlertLocationLayout.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                ChatAttachAlertLocationLayout.this.m2462x3b5fdab(chatActivity, resourcesProvider, view2, i);
            }
        });
        chatAttachAlertLocationLayout.adapter.setDelegate(chatAttachAlertLocationLayout.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda14
            @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.updatePlacesMarkers(arrayList);
            }
        });
        chatAttachAlertLocationLayout.adapter.setOverScrollHeight(chatAttachAlertLocationLayout.overScrollHeight);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.mapView = new MapView(context) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.9
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                MotionEvent eventToRecycle = null;
                if (ChatAttachAlertLocationLayout.this.yOffset != 0.0f) {
                    MotionEvent obtain = MotionEvent.obtain(ev);
                    eventToRecycle = obtain;
                    ev = obtain;
                    eventToRecycle.offsetLocation(0.0f, (-ChatAttachAlertLocationLayout.this.yOffset) / 2.0f);
                }
                boolean result = super.dispatchTouchEvent(ev);
                if (eventToRecycle != null) {
                    eventToRecycle.recycle();
                }
                return result;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0) {
                    if (ChatAttachAlertLocationLayout.this.animatorSet != null) {
                        ChatAttachAlertLocationLayout.this.animatorSet.cancel();
                    }
                    ChatAttachAlertLocationLayout.this.animatorSet = new AnimatorSet();
                    ChatAttachAlertLocationLayout.this.animatorSet.setDuration(200L);
                    ChatAttachAlertLocationLayout.this.animatorSet.playTogether(ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.markerImageView, View.TRANSLATION_Y, ChatAttachAlertLocationLayout.this.markerTop - AndroidUtilities.dp(10.0f)));
                    ChatAttachAlertLocationLayout.this.animatorSet.start();
                } else if (ev.getAction() == 1) {
                    if (ChatAttachAlertLocationLayout.this.animatorSet != null) {
                        ChatAttachAlertLocationLayout.this.animatorSet.cancel();
                    }
                    ChatAttachAlertLocationLayout.this.yOffset = 0.0f;
                    ChatAttachAlertLocationLayout.this.animatorSet = new AnimatorSet();
                    ChatAttachAlertLocationLayout.this.animatorSet.setDuration(200L);
                    ChatAttachAlertLocationLayout.this.animatorSet.playTogether(ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.markerImageView, View.TRANSLATION_Y, ChatAttachAlertLocationLayout.this.markerTop));
                    ChatAttachAlertLocationLayout.this.animatorSet.start();
                    ChatAttachAlertLocationLayout.this.adapter.fetchLocationAddress();
                }
                if (ev.getAction() == 2) {
                    if (!ChatAttachAlertLocationLayout.this.userLocationMoved) {
                        ChatAttachAlertLocationLayout.this.locationButton.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
                        ChatAttachAlertLocationLayout.this.locationButton.setTag(Theme.key_location_actionIcon);
                        ChatAttachAlertLocationLayout.this.userLocationMoved = true;
                    }
                    if (ChatAttachAlertLocationLayout.this.googleMap != null && ChatAttachAlertLocationLayout.this.userLocation != null) {
                        ChatAttachAlertLocationLayout.this.userLocation.setLatitude(ChatAttachAlertLocationLayout.this.googleMap.getCameraPosition().target.latitude);
                        ChatAttachAlertLocationLayout.this.userLocation.setLongitude(ChatAttachAlertLocationLayout.this.googleMap.getCameraPosition().target.longitude);
                    }
                    ChatAttachAlertLocationLayout.this.adapter.setCustomLocation(ChatAttachAlertLocationLayout.this.userLocation);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        final MapView map = chatAttachAlertLocationLayout.mapView;
        new Thread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.m2454xc8a66f1f(map);
            }
        }).start();
        ImageView imageView2 = new ImageView(context);
        chatAttachAlertLocationLayout.markerImageView = imageView2;
        imageView2.setImageResource(R.drawable.map_pin2);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.markerImageView, LayoutHelper.createFrame(28, 48, 49));
        RecyclerListView recyclerListView4 = new RecyclerListView(context, resourcesProvider);
        chatAttachAlertLocationLayout.searchListView = recyclerListView4;
        recyclerListView4.setVisibility(8);
        chatAttachAlertLocationLayout.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        LocationActivitySearchAdapter locationActivitySearchAdapter2 = new LocationActivitySearchAdapter(context) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.10
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void notifyDataSetChanged() {
                if (ChatAttachAlertLocationLayout.this.searchItem != null) {
                    ChatAttachAlertLocationLayout.this.searchItem.setShowSearchProgress(ChatAttachAlertLocationLayout.this.searchAdapter.isSearching());
                }
                if (ChatAttachAlertLocationLayout.this.emptySubtitleTextView != null) {
                    ChatAttachAlertLocationLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", R.string.NoPlacesFoundInfo, ChatAttachAlertLocationLayout.this.searchAdapter.getLastSearchString())));
                }
                super.notifyDataSetChanged();
            }
        };
        chatAttachAlertLocationLayout.searchAdapter = locationActivitySearchAdapter2;
        locationActivitySearchAdapter2.setDelegate(0L, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda13
            @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.m2455x5593863e(arrayList);
            }
        });
        chatAttachAlertLocationLayout.searchListView.setItemAnimator(null);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.11
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && ChatAttachAlertLocationLayout.this.searching && ChatAttachAlertLocationLayout.this.searchWas) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertLocationLayout.this.parentAlert.getCurrentFocus());
                }
            }
        });
        chatAttachAlertLocationLayout.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                ChatAttachAlertLocationLayout.this.m2457x6f6db47c(chatActivity, resourcesProvider, view2, i);
            }
        });
        updateEmptyView();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2450x293a5bd2(View v) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery(null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2451xb62772f1(View v) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2458x43148a10(int id) {
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

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2459xd001a12f(View v) {
        Activity activity;
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            return;
        }
        if (this.myLocation != null && this.googleMap != null) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_actionActiveIcon), PorterDuff.Mode.MULTIPLY));
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

    public static /* synthetic */ boolean lambda$new$4(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2462x3b5fdab(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int position) {
        if (position == 1) {
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
                if (!chatActivity.isInScheduleMode()) {
                    this.delegate.didSelectLocation(location, this.locationType, true, 0);
                    this.parentAlert.dismiss(true);
                    return;
                }
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda16
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.this.m2460xe9dbcf6d(location, z, i);
                    }
                }, resourcesProvider);
            } else if (this.locationDenied) {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            }
        } else if (position == 2 && this.locationType == 1) {
            if (getLocationController().isSharingLocation(this.dialogId)) {
                getLocationController().removeSharingLocation(this.dialogId);
                this.parentAlert.dismiss(true);
            } else if (this.myLocation == null && this.locationDenied) {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            } else {
                openShareLiveLocation();
            }
        } else {
            final Object object = this.adapter.getItem(position);
            if (object instanceof TLRPC.TL_messageMediaVenue) {
                if (!chatActivity.isInScheduleMode()) {
                    this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, true, 0);
                    this.parentAlert.dismiss(true);
                    return;
                }
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda15
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.this.m2461x76c8e68c(object, z, i);
                    }
                }, resourcesProvider);
            } else if (object instanceof LiveLocation) {
                LiveLocation liveLocation = (LiveLocation) object;
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2460xe9dbcf6d(TLRPC.TL_messageMediaGeo location, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(location, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2461x76c8e68c(Object object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2454xc8a66f1f(final MapView map) {
        try {
            map.onCreate(null);
        } catch (Exception e) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.m2453x3bb95800(map);
            }
        });
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2453x3bb95800(MapView map) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                map.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new OnMapReadyCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda1
                    @Override // com.google.android.gms.maps.OnMapReadyCallback
                    public final void onMapReady(GoogleMap googleMap) {
                        ChatAttachAlertLocationLayout.this.m2452xaecc40e1(googleMap);
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

    /* renamed from: lambda$new$10$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2452xaecc40e1(GoogleMap map1) {
        this.googleMap = map1;
        map1.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda25
            @Override // com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
            public final void onMapLoaded() {
                ChatAttachAlertLocationLayout.this.m2464x1d902be9();
            }
        });
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night);
            this.googleMap.setMapStyle(style);
        }
        onMapInit();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2464x1d902be9() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.m2463x90a314ca();
            }
        });
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2463x90a314ca() {
        this.loadingMapView.setTag(1);
        this.loadingMapView.animate().alpha(0.0f).setDuration(180L).start();
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2455x5593863e(ArrayList places) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2457x6f6db47c(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int position) {
        final TLRPC.TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (object != null && this.delegate != null) {
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.this.m2456xe2809d5d(object, z, i);
                    }
                }, resourcesProvider);
                return;
            }
            this.delegate.didSelectLocation(object, this.locationType, true, 0);
            this.parentAlert.dismiss(true);
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2456xe2809d5d(TLRPC.TL_messageMediaVenue object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(object, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean shouldHideBottomButtons() {
        return !this.locationDenied;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPause() {
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionDenied);
        try {
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MapView mapView = this.mapView;
        if (mapView != null) {
            mapView.setTranslationY((-AndroidUtilities.displaySize.y) * 3);
        }
        try {
            MapView mapView2 = this.mapView;
            if (mapView2 != null) {
                mapView2.onPause();
            }
        } catch (Exception e2) {
        }
        try {
            MapView mapView3 = this.mapView;
            if (mapView3 != null) {
                mapView3.onDestroy();
                this.mapView = null;
            }
        } catch (Exception e3) {
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        this.parentAlert.actionBar.closeSearchField();
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        menu.removeView(this.searchItem);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        this.searchItem.setVisibility(8);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onDismiss() {
        onDestroy();
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(0);
        int newOffset = 0;
        if (holder != null) {
            int top = ((int) holder.itemView.getY()) - this.nonClipSize;
            newOffset = Math.max(top, 0);
        }
        return AndroidUtilities.dp(56.0f) + newOffset;
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
        updateClipView();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.actionBar.isSearchFieldVisible() || this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = this.mapHeight - this.overScrollHeight;
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                padding2 = (int) (availableHeight / 3.5f);
            } else {
                padding2 = (availableHeight / 5) * 2;
            }
            padding = padding2 - AndroidUtilities.dp(52.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, 0);
            this.ignoreLayout = false;
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            fixLayoutInternal(this.first);
            this.first = false;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getButtonsHideOffset() {
        return AndroidUtilities.dp(56.0f);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    private boolean isActiveThemeDark() {
        Theme.ThemeInfo info = Theme.getActiveTheme();
        if (info.isDark()) {
            return true;
        }
        int color = getThemedColor(Theme.key_windowBackgroundWhite);
        return AndroidUtilities.computePerceivedBrightness(color) < 0.721f;
    }

    public void updateEmptyView() {
        if (this.searching) {
            if (this.searchInProgress) {
                this.searchListView.setEmptyView(null);
                this.emptyView.setVisibility(8);
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
            this.searchAreaButton.setVisibility(show ? 0 : 4);
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

    public void openShareLiveLocation() {
        Activity activity;
        if (this.delegate == null || getParentActivity() == null || this.myLocation == null) {
            return;
        }
        if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (activity = getParentActivity()) != null) {
            this.checkBackgroundPermission = false;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            int lastTime = preferences.getInt("backgroundloc", 0);
            if (Math.abs((System.currentTimeMillis() / 1000) - lastTime) > 86400 && activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                preferences.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                AlertsCreator.createBackgroundLocationPermissionDialog(activity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertLocationLayout.this.openShareLiveLocation();
                    }
                }, this.resourcesProvider).show();
                return;
            }
        }
        TLRPC.User user = null;
        if (DialogObject.isUserDialog(this.dialogId)) {
            user = this.parentAlert.baseFragment.getMessagesController().getUser(Long.valueOf(this.dialogId));
        }
        AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9
            @Override // org.telegram.messenger.MessagesStorage.IntCallback
            public final void run(int i) {
                ChatAttachAlertLocationLayout.this.m2472xf7564751(i);
            }
        }, this.resourcesProvider).show();
    }

    /* renamed from: lambda$openShareLiveLocation$16$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2472xf7564751(int param) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.period = param;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        this.parentAlert.dismiss(true);
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

    private MessagesController getMessagesController() {
        return this.parentAlert.baseFragment.getMessagesController();
    }

    private LocationController getLocationController() {
        return this.parentAlert.baseFragment.getLocationController();
    }

    private UserConfig getUserConfig() {
        return this.parentAlert.baseFragment.getUserConfig();
    }

    public Activity getParentActivity() {
        if (this.parentAlert == null || this.parentAlert.baseFragment == null) {
            return null;
        }
        return this.parentAlert.baseFragment.getParentActivity();
    }

    private void onMapInit() {
        if (this.googleMap == null) {
            return;
        }
        Location location = new Location("network");
        this.userLocation = location;
        location.setLatitude(20.659322d);
        this.userLocation.setLongitude(-11.40625d);
        try {
            this.googleMap.setMyLocationEnabled(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24
            @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
            public final void onCameraMoveStarted(int i) {
                ChatAttachAlertLocationLayout.this.m2465xeba4ab07(i);
            }
        });
        this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27
            @Override // com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
            public final void onMyLocationChange(Location location2) {
                ChatAttachAlertLocationLayout.this.m2466x7891c226(location2);
            }
        });
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26
            @Override // com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
            public final boolean onMarkerClick(Marker marker) {
                return ChatAttachAlertLocationLayout.this.m2467x57ed945(marker);
            }
        });
        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23
            @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
            public final void onCameraMove() {
                ChatAttachAlertLocationLayout.this.m2468x21ded5ef();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.m2469xaecbed0e();
            }
        }, 200L);
        Location lastLocation = getLastLocation();
        this.myLocation = lastLocation;
        positionMarker(lastLocation);
        if (this.checkGpsEnabled && getParentActivity() != null) {
            this.checkGpsEnabled = false;
            if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return;
            }
            try {
                LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
                if (!lm.isProviderEnabled("gps")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                    builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatAttachAlertLocationLayout.this.m2470x3bb9042d(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.show();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        updateClipView();
    }

    /* renamed from: lambda$onMapInit$17$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2465xeba4ab07(int reason) {
        View view;
        RecyclerView.ViewHolder holder;
        if (reason == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling && this.listView.getChildCount() > 0 && (view = this.listView.getChildAt(0)) != null && (holder = this.listView.findContainingViewHolder(view)) != null && holder.getAdapterPosition() == 0) {
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

    /* renamed from: lambda$onMapInit$18$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2466x7891c226(Location location) {
        if (this.parentAlert == null || this.parentAlert.baseFragment == null) {
            return;
        }
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* renamed from: lambda$onMapInit$19$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ boolean m2467x57ed945(Marker marker) {
        if (!(marker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_actionIcon), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag(Theme.key_location_actionIcon);
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(marker);
        return true;
    }

    /* renamed from: lambda$onMapInit$20$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2468x21ded5ef() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    /* renamed from: lambda$onMapInit$21$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2469xaecbed0e() {
        if (this.loadingMapView.getTag() == null) {
            this.loadingMapView.animate().alpha(0.0f).setDuration(180L).start();
        }
    }

    /* renamed from: lambda$onMapInit$22$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2470x3bb9042d(DialogInterface dialog, int id) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception e) {
        }
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

    public void updateClipView() {
        int height;
        int top;
        LatLng location;
        if (this.mapView == null || this.mapViewClip == null) {
            return;
        }
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            top = (int) holder.itemView.getY();
            height = this.overScrollHeight + Math.min(top, 0);
        } else {
            top = -this.mapViewClip.getMeasuredHeight();
            height = 0;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        if (height > 0) {
            if (this.mapView.getVisibility() == 4) {
                this.mapView.setVisibility(0);
                this.mapViewClip.setVisibility(0);
                MapOverlayView mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    mapOverlayView.setVisibility(0);
                }
            }
            int trY = Math.max(0, (-((top - this.mapHeight) + this.overScrollHeight)) / 2);
            int maxClipSize = this.mapHeight - this.overScrollHeight;
            int totalToMove = this.listView.getPaddingTop() - maxClipSize;
            float moveProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, (this.listView.getPaddingTop() - top) / totalToMove));
            int prevClipSize = this.clipSize;
            if (this.locationDenied && isTypeSend()) {
                maxClipSize += Math.min(top, this.listView.getPaddingTop());
            }
            this.clipSize = (int) (maxClipSize * moveProgress);
            this.mapView.setTranslationY(trY);
            this.nonClipSize = maxClipSize - this.clipSize;
            this.mapViewClip.invalidate();
            this.mapViewClip.setTranslationY(top - this.nonClipSize);
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                googleMap.setPadding(0, AndroidUtilities.dp(6.0f), 0, this.clipSize + AndroidUtilities.dp(6.0f));
            }
            MapOverlayView mapOverlayView2 = this.overlayView;
            if (mapOverlayView2 != null) {
                mapOverlayView2.setTranslationY(trY);
            }
            float translationY = Math.min(Math.max(this.nonClipSize - top, 0), (this.mapHeight - this.mapTypeButton.getMeasuredHeight()) - AndroidUtilities.dp(80.0f));
            this.mapTypeButton.setTranslationY(translationY);
            this.searchAreaButton.setTranslation(translationY);
            this.locationButton.setTranslationY(-this.clipSize);
            ImageView imageView = this.markerImageView;
            int dp = (((this.mapHeight - this.clipSize) / 2) - AndroidUtilities.dp(48.0f)) + trY;
            this.markerTop = dp;
            imageView.setTranslationY(dp);
            if (prevClipSize != this.clipSize) {
                Marker marker = this.lastPressedMarker;
                if (marker != null) {
                    location = marker.getPosition();
                } else if (this.userLocationMoved) {
                    location = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                } else if (this.myLocation != null) {
                    location = new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude());
                } else {
                    location = null;
                }
                if (location != null) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
            }
            if (this.locationDenied && isTypeSend()) {
                int count = this.adapter.getItemCount();
                for (int i = 1; i < count; i++) {
                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(i);
                    if (holder2 != null) {
                        holder2.itemView.setTranslationY(this.listView.getPaddingTop() - top);
                    }
                }
                return;
            }
            return;
        }
        if (this.mapView.getVisibility() == 0) {
            this.mapView.setVisibility(4);
            this.mapViewClip.setVisibility(4);
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setVisibility(4);
            }
        }
        this.mapView.setTranslationY(top);
    }

    private boolean isTypeSend() {
        int i = this.locationType;
        return i == 0 || i == 1;
    }

    private int buttonsHeight() {
        int buttonsHeight = AndroidUtilities.dp(66.0f);
        if (this.locationType == 1) {
            return buttonsHeight + AndroidUtilities.dp(66.0f);
        }
        return buttonsHeight;
    }

    private void fixLayoutInternal(boolean resume) {
        FrameLayout.LayoutParams layoutParams;
        int viewHeight = getMeasuredHeight();
        if (viewHeight == 0 || this.mapView == null) {
            return;
        }
        int height = ActionBar.getCurrentActionBarHeight();
        int maxMapHeight = ((AndroidUtilities.displaySize.y - height) - buttonsHeight()) - AndroidUtilities.dp(90.0f);
        int dp = AndroidUtilities.dp(189.0f);
        this.overScrollHeight = dp;
        this.mapHeight = Math.max(dp, (!this.locationDenied || !isTypeSend()) ? Math.min(AndroidUtilities.dp(310.0f), maxMapHeight) : maxMapHeight);
        if (this.locationDenied && isTypeSend()) {
            this.overScrollHeight = this.mapHeight;
        }
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        layoutParams2.topMargin = height;
        this.listView.setLayoutParams(layoutParams2);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
        layoutParams3.topMargin = height;
        layoutParams3.height = this.mapHeight;
        this.mapViewClip.setLayoutParams(layoutParams3);
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.searchListView.getLayoutParams();
        layoutParams4.topMargin = height;
        this.searchListView.setLayoutParams(layoutParams4);
        this.adapter.setOverScrollHeight((!this.locationDenied || !isTypeSend()) ? this.overScrollHeight : this.overScrollHeight - this.listView.getPaddingTop());
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
        if (layoutParams5 != null) {
            layoutParams5.height = this.mapHeight + AndroidUtilities.dp(10.0f);
            this.mapView.setLayoutParams(layoutParams5);
        }
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null && (layoutParams = (FrameLayout.LayoutParams) mapOverlayView.getLayoutParams()) != null) {
            layoutParams.height = this.mapHeight + AndroidUtilities.dp(10.0f);
            this.overlayView.setLayoutParams(layoutParams);
        }
        this.adapter.notifyDataSetChanged();
        updateClipView();
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
        Location location2 = new Location(location);
        this.myLocation = location2;
        if (this.googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                if (!this.searchedForCustomLocations) {
                    locationActivityAdapter.searchPlacesWithQuery(null, this.myLocation, true);
                }
                this.adapter.setGpsLocation(this.myLocation);
            }
            if (!this.userLocationMoved) {
                this.userLocation = new Location(location);
                if (this.firstWas) {
                    CameraUpdate position = CameraUpdateFactory.newLatLng(latLng);
                    this.googleMap.animateCamera(position);
                    return;
                }
                this.firstWas = true;
                CameraUpdate position2 = CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f);
                this.googleMap.moveCamera(position2);
                return;
            }
            return;
        }
        this.adapter.setGpsLocation(location2);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int i = 0;
        if (id == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                locationActivityAdapter.setMyLocationDenied(false);
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
            LocationActivityAdapter locationActivityAdapter2 = this.adapter;
            if (locationActivityAdapter2 != null) {
                locationActivityAdapter2.setMyLocationDenied(true);
            }
        }
        fixLayoutInternal(true);
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (this.locationDenied) {
            i = 8;
        }
        actionBarMenuItem.setVisibility(i);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onResume() {
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.parentAlert.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
        if (this.mapView.getParent() == null) {
            this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.loadingMapView, 2, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.searchItem.setVisibility(0);
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
        boolean keyboardVisible = this.parentAlert.delegate.needEnterComment();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.m2471x500ca7b5();
            }
        }, keyboardVisible ? 200L : 0L);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        updateClipView();
    }

    /* renamed from: lambda$onShow$23$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2471x500ca7b5() {
        Activity activity;
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
    }

    public void setDelegate(LocationActivityDelegate delegate) {
        this.delegate = delegate;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatAttachAlertLocationLayout.this.m2449x10876942();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.mapViewClip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_location_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_location_actionActiveIcon));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, 0, null, null, null, cellDelegate, Theme.key_location_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_location_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_location_actionPressedBackground));
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

    /* renamed from: lambda$getThemeDescriptions$24$org-telegram-ui-Components-ChatAttachAlertLocationLayout */
    public /* synthetic */ void m2449x10876942() {
        this.mapTypeButton.setIconColor(getThemedColor(Theme.key_location_actionIcon));
        this.mapTypeButton.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        this.mapTypeButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
        this.mapTypeButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        if (this.googleMap != null) {
            if (isActiveThemeDark()) {
                if (!this.currentMapStyleDark) {
                    this.currentMapStyleDark = true;
                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night);
                    this.googleMap.setMapStyle(style);
                }
            } else if (this.currentMapStyleDark) {
                this.currentMapStyleDark = false;
                this.googleMap.setMapStyle(null);
            }
        }
    }
}
