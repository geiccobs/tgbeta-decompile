package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;
/* loaded from: classes4.dex */
public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int delete = 4;
    private static final int forward = 3;
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private ColorWallpaper catsWallpaper;
    private Paint colorFramePaint;
    private Paint colorPaint;
    private int currentType;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingWallpapers;
    private AlertDialog progressDialog;
    private int resetInfoRow;
    private int resetRow;
    private int resetSectionRow;
    private int rowCount;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchItem;
    private int sectionRow;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    private int selectedColor;
    private int selectedGradientColor1;
    private int selectedGradientColor2;
    private int selectedGradientColor3;
    private int selectedGradientRotation;
    private float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    private int setColorRow;
    private FileWallpaper themeWallpaper;
    private int totalWallpaperRows;
    private WallpaperUpdater updater;
    private int uploadImageRow;
    private int wallPaperStartRow;
    private static final int[][] defaultColorsLight = {new int[]{-2368069, -9722489, -2762611, -7817084}, new int[]{-7487253, -4599318, -3755537, -1320977}, new int[]{-6832405, -5117462, -3755537, -1067044}, new int[]{-7676942, -7827988, -1859606, -9986835}, new int[]{-5190165, -6311702, -4461867, -5053475}, new int[]{-2430264, -6114049, -1258497, -4594945}, new int[]{-2298990, -7347754, -9985038, -8006011}, new int[]{-1399954, -990074, -876865, -1523602}, new int[]{-15438, -1916673, -6222, -471346}, new int[]{-2891798}, new int[]{-5913125}, new int[]{-9463352}, new int[]{-2956375}, new int[]{-5974898}, new int[]{-8537234}, new int[]{-1647186}, new int[]{-2769263}, new int[]{-3431303}, new int[]{-1326919}, new int[]{-2054243}, new int[]{-3573648}, new int[]{-1328696}, new int[]{-2056777}, new int[]{-2984557}, new int[]{-2440467}, new int[]{-2906649}, new int[]{-4880430}, new int[]{-4013331}, new int[]{-5921305}, new int[]{-8421424}, new int[]{-4005139}, new int[]{-5908761}, new int[]{-8406320}, new int[]{-2702663}, new int[]{-6518654}, new int[]{-16777216}};
    private static final int[][] defaultColorsDark = {new int[]{-14797481, -15394250, -14924974, -14006975}, new int[]{-14867905, -14870478, -14997181, -15460815}, new int[]{-14666695, -15720408, -14861254, -15260107}, new int[]{-14932175, -15066075, -14208965, -15000799}, new int[]{-12968902, -14411460, -13029826, -15067598}, new int[]{-13885157, -12307670, -14542561, -12899018}, new int[]{-14797481, -15196106, -14924974, -15325638}, new int[]{-15658442, -15449521, -16047308, -12897955}, new int[]{-13809610, -15258855, -13221071, -15715791}, new int[]{-14865092}, new int[]{-15656154}, new int[]{-16051170}, new int[]{-14731745}, new int[]{-15524075}, new int[]{-15853808}, new int[]{-13685209}, new int[]{-14014945}, new int[]{-15132649}, new int[]{-12374480}, new int[]{-13755362}, new int[]{-14740716}, new int[]{-12374468}, new int[]{-13755352}, new int[]{-14740709}, new int[]{-12833213}, new int[]{-14083026}, new int[]{-14872031}, new int[]{-13554109}, new int[]{-14803922}, new int[]{-15461855}, new int[]{-13680833}, new int[]{-14602960}, new int[]{-15458784}, new int[]{-14211804}, new int[]{-15132906}, new int[]{-16777216}};
    private static final int[] searchColors = {-16746753, SupportMenu.CATEGORY_MASK, -30208, -13824, -16718798, -14702165, -9240406, -409915, -9224159, -16777216, -10725281, -1};
    private static final String[] searchColorsNames = {"Blue", "Red", "Orange", "Yellow", "Green", "Teal", "Purple", "Pink", "Brown", "Black", "Gray", "White"};
    private static final int[] searchColorsNamesR = {R.string.Blue, R.string.Red, R.string.Orange, R.string.Yellow, R.string.Green, R.string.Teal, R.string.Purple, R.string.Pink, R.string.Brown, R.string.Black, R.string.Gray, R.string.White};
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private int columnsCount = 3;
    private String selectedBackgroundSlug = "";
    private ArrayList<Object> allWallPapers = new ArrayList<>();
    private HashMap<String, Object> allWallPapersDict = new HashMap<>();
    private HashMap<String, Object> localDict = new HashMap<>();
    private ArrayList<Object> wallPapers = new ArrayList<>();
    private ArrayList<ColorWallpaper> localWallPapers = new ArrayList<>();
    private ArrayList<Object> patterns = new ArrayList<>();
    private HashMap<Long, Object> patternsDict = new HashMap<>();
    private LongSparseArray<Object> selectedWallPapers = new LongSparseArray<>();

    /* loaded from: classes4.dex */
    public static class ColorWallpaper {
        public int color;
        public Bitmap defaultCache;
        public int gradientColor1;
        public int gradientColor2;
        public int gradientColor3;
        public int gradientRotation;
        public float intensity;
        public boolean isGradient;
        public boolean motion;
        public TLRPC.WallPaper parentWallpaper;
        public File path;
        public TLRPC.TL_wallPaper pattern;
        public long patternId;
        public String slug;

        public String getHash() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(this.color));
            sb.append(this.gradientColor1);
            sb.append(this.gradientColor2);
            sb.append(this.gradientColor3);
            sb.append(this.gradientRotation);
            sb.append(this.intensity);
            String str = this.slug;
            if (str == null) {
                str = "";
            }
            sb.append(str);
            String string = sb.toString();
            return Utilities.MD5(string);
        }

        public ColorWallpaper(String s, int c, int gc, int r) {
            this.slug = s;
            this.color = c | (-16777216);
            int i = 0;
            int i2 = gc == 0 ? 0 : (-16777216) | gc;
            this.gradientColor1 = i2;
            this.gradientRotation = i2 != 0 ? r : i;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String s, int c, int gc1, int gc2, int gc3) {
            this.slug = s;
            this.color = c | (-16777216);
            int i = 0;
            this.gradientColor1 = gc1 == 0 ? 0 : gc1 | (-16777216);
            this.gradientColor2 = gc2 == 0 ? 0 : gc2 | (-16777216);
            this.gradientColor3 = gc3 != 0 ? gc3 | (-16777216) : i;
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String s, int c) {
            this.slug = s;
            int i = (-16777216) | c;
            this.color = i;
            float[] hsv = new float[3];
            Color.colorToHSV(i, hsv);
            if (hsv[0] > 180.0f) {
                hsv[0] = hsv[0] - 60.0f;
            } else {
                hsv[0] = hsv[0] + 60.0f;
            }
            this.gradientColor1 = Color.HSVToColor(255, hsv);
            this.gradientColor2 = ColorPicker.generateGradientColors(this.color);
            this.gradientColor3 = ColorPicker.generateGradientColors(this.gradientColor1);
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String s, int c, int gc1, int gc2, int gc3, int r, float in, boolean m, File ph) {
            this.slug = s;
            this.color = c | (-16777216);
            int i = 0;
            int i2 = gc1 == 0 ? 0 : gc1 | (-16777216);
            this.gradientColor1 = i2;
            this.gradientColor2 = gc2 == 0 ? 0 : gc2 | (-16777216);
            this.gradientColor3 = gc3 != 0 ? gc3 | (-16777216) : i;
            this.gradientRotation = i2 != 0 ? r : 45;
            this.intensity = in;
            this.path = ph;
            this.motion = m;
        }

        public String getUrl() {
            int i = this.gradientColor1;
            String color4 = null;
            String color2 = i != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (i >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor1 & 255))).toLowerCase() : null;
            String color1 = String.format("%02x%02x%02x", Integer.valueOf(((byte) (this.color >> 16)) & 255), Integer.valueOf(((byte) (this.color >> 8)) & 255), Byte.valueOf((byte) (this.color & 255))).toLowerCase();
            int i2 = this.gradientColor2;
            String color3 = i2 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (i2 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor2 & 255))).toLowerCase() : null;
            int i3 = this.gradientColor3;
            if (i3 != 0) {
                color4 = String.format("%02x%02x%02x", Integer.valueOf(((byte) (i3 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor3 & 255))).toLowerCase();
            }
            if (color2 == null || color3 == null) {
                if (color2 != null) {
                    String color12 = color1 + "-" + color2;
                    if (this.pattern != null) {
                        color1 = color12 + "&rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    } else {
                        color1 = color12 + "?rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    }
                }
            } else if (color4 != null) {
                color1 = color1 + "~" + color2 + "~" + color3 + "~" + color4;
            } else {
                color1 = color1 + "~" + color2 + "~" + color3;
            }
            if (this.pattern != null) {
                String link = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + this.pattern.slug + "?intensity=" + ((int) (this.intensity * 100.0f)) + "&bg_color=" + color1;
                if (this.motion) {
                    return link + "&mode=motion";
                }
                return link;
            }
            return "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + color1;
        }
    }

    /* loaded from: classes4.dex */
    public static class FileWallpaper {
        public File originalPath;
        public File path;
        public int resId;
        public String slug;
        public int thumbResId;

        public FileWallpaper(String s, File f, File of) {
            this.slug = s;
            this.path = f;
            this.originalPath = of;
        }

        public FileWallpaper(String s, String f) {
            this.slug = s;
            this.path = new File(f);
        }

        public FileWallpaper(String s, int r, int t) {
            this.slug = s;
            this.resId = r;
            this.thumbResId = t;
        }
    }

    public WallpapersListActivity(int type) {
        this.currentType = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            getMessagesStorage().getWallpapers();
        } else {
            boolean darkTheme = Theme.isCurrentThemeDark();
            int[][] defaultColors = darkTheme ? defaultColorsDark : defaultColorsLight;
            for (int a = 0; a < defaultColors.length; a++) {
                if (defaultColors[a].length == 1) {
                    this.wallPapers.add(new ColorWallpaper(Theme.COLOR_BACKGROUND_SLUG, defaultColors[a][0], 0, 45));
                } else {
                    this.wallPapers.add(new ColorWallpaper(Theme.COLOR_BACKGROUND_SLUG, defaultColors[a][0], defaultColors[a][1], defaultColors[a][2], defaultColors[a][3]));
                }
            }
            int a2 = this.currentType;
            if (a2 == 1 && this.patterns.isEmpty()) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
                getMessagesStorage().getWallpapers();
            }
        }
        boolean darkTheme2 = super.onFragmentCreate();
        return darkTheme2;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        int i = this.currentType;
        if (i == 0) {
            this.searchAdapter.onDestroy();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        this.updater.cleanup();
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.colorPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.colorFramePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.colorFramePaint.setStyle(Paint.Style.STROKE);
        this.colorFramePaint.setColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
        this.updater = new WallpaperUpdater(getParentActivity(), this, new WallpaperUpdater.WallpaperUpdaterDelegate() { // from class: org.telegram.ui.WallpapersListActivity.1
            @Override // org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate
            public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(new FileWallpaper("", file, file), bitmap), gallery);
            }

            @Override // org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate
            public void needOpenColorPicker() {
            }
        });
        this.hasOwnBackground = true;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatBackground", R.string.ChatBackground));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("SelectColorTitle", R.string.SelectColorTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass2());
        if (this.currentType == 0) {
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.WallpapersListActivity.3
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchExpand() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.searchAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchCollapse() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.listAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                    WallpapersListActivity.this.searchAdapter.processSearch(null, true);
                    WallpapersListActivity.this.searchItem.setSearchFieldCaption(null);
                    onCaptionCleared();
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    WallpapersListActivity.this.searchAdapter.processSearch(editText.getText().toString(), false);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onCaptionCleared() {
                    WallpapersListActivity.this.searchAdapter.clearColor();
                    WallpapersListActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", R.string.SearchBackgrounds));
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", R.string.SearchBackgrounds));
            ActionBarMenu actionMode = this.actionBar.createActionMode(false, null);
            actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), true);
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedMessagesCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            this.selectedMessagesCountTextView.setOnTouchListener(WallpapersListActivity$$ExternalSyntheticLambda1.INSTANCE);
            actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.msg_forward, AndroidUtilities.dp(54.0f), LocaleController.getString("Forward", R.string.Forward)));
            this.actionModeViews.add(actionMode.addItemWithWidth(4, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete)));
            this.selectedWallPapers.clear();
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.WallpapersListActivity.4
            private Paint paint = new Paint();

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onDraw(Canvas c) {
                RecyclerView.ViewHolder holder;
                int bottom;
                if (getAdapter() == WallpapersListActivity.this.listAdapter && WallpapersListActivity.this.resetInfoRow != -1) {
                    holder = findViewHolderForAdapterPosition(WallpapersListActivity.this.resetInfoRow);
                } else {
                    holder = null;
                }
                int height = getMeasuredHeight();
                if (holder != null) {
                    bottom = holder.itemView.getBottom();
                    if (holder.itemView.getBottom() >= height) {
                        bottom = height;
                    }
                } else {
                    bottom = height;
                }
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                c.drawRect(0.0f, 0.0f, getMeasuredWidth(), bottom, this.paint);
                if (bottom != height) {
                    this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    c.drawRect(0.0f, bottom, getMeasuredWidth(), height, this.paint);
                }
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.WallpapersListActivity.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                WallpapersListActivity.this.m4803lambda$createView$4$orgtelegramuiWallpapersListActivity(view, i2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.WallpapersListActivity.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (newState == 0) {
                    z = false;
                }
                wallpapersListActivity.scrolling = z;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (WallpapersListActivity.this.listView.getAdapter() == WallpapersListActivity.this.searchAdapter) {
                    int firstVisibleItem = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = WallpapersListActivity.this.layoutManager.getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2) {
                            WallpapersListActivity.this.searchAdapter.loadMoreResults();
                        }
                    }
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.listView.setEmptyView(this.searchEmptyView);
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.WallpapersListActivity$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass2() {
            WallpapersListActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            if (id == -1) {
                if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                    WallpapersListActivity.this.selectedWallPapers.clear();
                    WallpapersListActivity.this.actionBar.hideActionMode();
                    WallpapersListActivity.this.updateRowsSelection();
                    return;
                }
                WallpapersListActivity.this.finishFragment();
            } else if (id == 4) {
                if (WallpapersListActivity.this.getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(WallpapersListActivity.this.getParentActivity());
                builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size(), new Object[0]));
                builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", R.string.DeleteChatBackgroundsAlert, new Object[0]));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.WallpapersListActivity$2$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        WallpapersListActivity.AnonymousClass2.this.m4809lambda$onItemClick$2$orgtelegramuiWallpapersListActivity$2(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                WallpapersListActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            } else if (id == 3) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.WallpapersListActivity$2$$ExternalSyntheticLambda3
                    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                        WallpapersListActivity.AnonymousClass2.this.m4810lambda$onItemClick$3$orgtelegramuiWallpapersListActivity$2(dialogsActivity, arrayList, charSequence, z);
                    }
                });
                WallpapersListActivity.this.presentFragment(fragment);
            }
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-WallpapersListActivity$2 */
        public /* synthetic */ void m4809lambda$onItemClick$2$orgtelegramuiWallpapersListActivity$2(DialogInterface dialogInterface, int i) {
            WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
            WallpapersListActivity.this.progressDialog.setCanCancel(false);
            WallpapersListActivity.this.progressDialog.show();
            new ArrayList();
            final int[] deleteCount = {0};
            for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                Object object = WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                boolean z = object instanceof ColorWallpaper;
                Object object2 = object;
                if (z) {
                    ColorWallpaper colorWallpaper = (ColorWallpaper) object;
                    if (colorWallpaper.parentWallpaper != null && colorWallpaper.parentWallpaper.id < 0) {
                        WallpapersListActivity.this.getMessagesStorage().deleteWallpaper(colorWallpaper.parentWallpaper.id);
                        WallpapersListActivity.this.localWallPapers.remove(colorWallpaper);
                        WallpapersListActivity.this.localDict.remove(colorWallpaper.getHash());
                        object2 = object;
                    } else {
                        object2 = colorWallpaper.parentWallpaper;
                    }
                }
                if (object2 instanceof TLRPC.WallPaper) {
                    deleteCount[0] = deleteCount[0] + 1;
                    TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object2;
                    TLRPC.TL_account_saveWallPaper req = new TLRPC.TL_account_saveWallPaper();
                    req.settings = new TLRPC.TL_wallPaperSettings();
                    req.unsave = true;
                    if (object2 instanceof TLRPC.TL_wallPaperNoFile) {
                        TLRPC.TL_inputWallPaperNoFile inputWallPaper = new TLRPC.TL_inputWallPaperNoFile();
                        inputWallPaper.id = wallPaper.id;
                        req.wallpaper = inputWallPaper;
                    } else {
                        TLRPC.TL_inputWallPaper inputWallPaper2 = new TLRPC.TL_inputWallPaper();
                        inputWallPaper2.id = wallPaper.id;
                        inputWallPaper2.access_hash = wallPaper.access_hash;
                        req.wallpaper = inputWallPaper2;
                    }
                    if (wallPaper.slug != null && wallPaper.slug.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                        WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? Theme.THEME_BACKGROUND_SLUG : Theme.DEFAULT_BACKGROUND_SLUG;
                        Theme.getActiveTheme().setOverrideWallpaper(null);
                        Theme.reloadWallpaper();
                    }
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WallpapersListActivity$2$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            WallpapersListActivity.AnonymousClass2.this.m4808lambda$onItemClick$1$orgtelegramuiWallpapersListActivity$2(deleteCount, tLObject, tL_error);
                        }
                    });
                }
            }
            if (deleteCount[0] == 0) {
                WallpapersListActivity.this.loadWallpapers(true);
            }
            WallpapersListActivity.this.selectedWallPapers.clear();
            WallpapersListActivity.this.actionBar.hideActionMode();
            WallpapersListActivity.this.actionBar.closeSearchField();
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-WallpapersListActivity$2 */
        public /* synthetic */ void m4808lambda$onItemClick$1$orgtelegramuiWallpapersListActivity$2(final int[] deleteCount, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WallpapersListActivity.AnonymousClass2.this.m4807lambda$onItemClick$0$orgtelegramuiWallpapersListActivity$2(deleteCount);
                }
            });
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-WallpapersListActivity$2 */
        public /* synthetic */ void m4807lambda$onItemClick$0$orgtelegramuiWallpapersListActivity$2(int[] deleteCount) {
            deleteCount[0] = deleteCount[0] - 1;
            if (deleteCount[0] == 0) {
                WallpapersListActivity.this.loadWallpapers(true);
            }
        }

        /* renamed from: lambda$onItemClick$3$org-telegram-ui-WallpapersListActivity$2 */
        public /* synthetic */ void m4810lambda$onItemClick$3$orgtelegramuiWallpapersListActivity$2(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
            String link;
            StringBuilder fmessage = new StringBuilder();
            for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                Object object = WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                if (object instanceof TLRPC.TL_wallPaper) {
                    link = AndroidUtilities.getWallPaperUrl(object);
                } else if (object instanceof ColorWallpaper) {
                    link = ((ColorWallpaper) object).getUrl();
                }
                if (!TextUtils.isEmpty(link)) {
                    if (fmessage.length() > 0) {
                        fmessage.append('\n');
                    }
                    fmessage.append(link);
                }
            }
            WallpapersListActivity.this.selectedWallPapers.clear();
            WallpapersListActivity.this.actionBar.hideActionMode();
            WallpapersListActivity.this.actionBar.closeSearchField();
            if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId() || message != null) {
                WallpapersListActivity.this.updateRowsSelection();
                for (int a = 0; a < dids.size(); a++) {
                    long did = ((Long) dids.get(a)).longValue();
                    if (message != null) {
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(message.toString(), did, null, null, null, true, null, null, null, true, 0, null);
                    }
                    if (!TextUtils.isEmpty(fmessage)) {
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did, null, null, null, true, null, null, null, true, 0, null);
                    }
                }
                fragment1.finishFragment();
                return;
            }
            long did2 = ((Long) dids.get(0)).longValue();
            Bundle args1 = new Bundle();
            args1.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(did2)) {
                args1.putInt("enc_id", DialogObject.getEncryptedChatId(did2));
            } else {
                if (DialogObject.isUserDialog(did2)) {
                    args1.putLong("user_id", did2);
                } else if (DialogObject.isChatDialog(did2)) {
                    args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did2);
                }
                if (!MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                    return;
                }
            }
            NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(args1);
            WallpapersListActivity.this.presentFragment(chatActivity, true);
            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did2, null, null, null, true, null, null, null, true, 0, null);
        }
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4803lambda$createView$4$orgtelegramuiWallpapersListActivity(View view, int position) {
        if (getParentActivity() == null || this.listView.getAdapter() == this.searchAdapter) {
            return;
        }
        if (position == this.uploadImageRow) {
            this.updater.openGallery();
        } else if (position == this.setColorRow) {
            WallpapersListActivity activity = new WallpapersListActivity(1);
            activity.patterns = this.patterns;
            presentFragment(activity);
        } else if (position == this.resetRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("ResetChatBackgroundsAlertTitle", R.string.ResetChatBackgroundsAlertTitle));
            builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", R.string.ResetChatBackgroundsAlert));
            builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    WallpapersListActivity.this.m4802lambda$createView$3$orgtelegramuiWallpapersListActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4802lambda$createView$3$orgtelegramuiWallpapersListActivity(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
        TLRPC.TL_account_resetWallPapers req = new TLRPC.TL_account_resetWallPapers();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.m4801lambda$createView$2$orgtelegramuiWallpapersListActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4800lambda$createView$1$orgtelegramuiWallpapersListActivity() {
        loadWallpapers(false);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4801lambda$createView$2$orgtelegramuiWallpapersListActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                WallpapersListActivity.this.m4800lambda$createView$1$orgtelegramuiWallpapersListActivity();
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        MessagesController.getGlobalMainSettings();
        Theme.ThemeInfo themeInfo = Theme.getActiveTheme();
        Theme.OverrideWallpaperInfo overrideWallpaper = themeInfo.overrideWallpaper;
        if (overrideWallpaper != null) {
            this.selectedBackgroundSlug = overrideWallpaper.slug;
            this.selectedColor = overrideWallpaper.color;
            this.selectedGradientColor1 = overrideWallpaper.gradientColor1;
            this.selectedGradientColor2 = overrideWallpaper.gradientColor2;
            this.selectedGradientColor3 = overrideWallpaper.gradientColor3;
            this.selectedGradientRotation = overrideWallpaper.rotation;
            this.selectedIntensity = overrideWallpaper.intensity;
            this.selectedBackgroundMotion = overrideWallpaper.isMotion;
            this.selectedBackgroundBlurred = overrideWallpaper.isBlurred;
        } else {
            this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? Theme.THEME_BACKGROUND_SLUG : Theme.DEFAULT_BACKGROUND_SLUG;
            this.selectedColor = 0;
            this.selectedGradientColor1 = 0;
            this.selectedGradientColor2 = 0;
            this.selectedGradientColor3 = 0;
            this.selectedGradientRotation = 45;
            this.selectedIntensity = 1.0f;
            this.selectedBackgroundMotion = false;
            this.selectedBackgroundBlurred = false;
        }
        fillWallpapersWithCustom();
        fixLayout();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.updater.onActivityResult(requestCode, resultCode, data);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle args) {
        String currentPicturePath = this.updater.getCurrentPicturePath();
        if (currentPicturePath != null) {
            args.putString("path", currentPicturePath);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle args) {
        this.updater.setCurrentPicturePath(args.getString("path"));
    }

    public boolean onItemLongClick(WallpaperCell view, Object object, int index) {
        if (object instanceof ColorWallpaper) {
            ColorWallpaper colorWallpaper = (ColorWallpaper) object;
            object = colorWallpaper.parentWallpaper;
        }
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(object instanceof TLRPC.WallPaper)) {
            return false;
        }
        TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(wallPaper.id, object);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250L);
        animatorSet.start();
        this.scrolling = false;
        this.actionBar.showActionMode();
        view.setChecked(index, true, true);
        return true;
    }

    public void onItemClick(WallpaperCell view, Object object, int index) {
        Object object2 = object;
        boolean z = false;
        if (!this.actionBar.isActionModeShowed()) {
            String slug = getWallPaperSlug(object2);
            if (object2 instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object2;
                if (wallPaper.pattern) {
                    ColorWallpaper colorWallpaper = new ColorWallpaper(wallPaper.slug, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color, AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, false), wallPaper.settings.intensity / 100.0f, wallPaper.settings.motion, null);
                    colorWallpaper.pattern = wallPaper;
                    colorWallpaper.parentWallpaper = wallPaper;
                    object2 = colorWallpaper;
                }
            }
            ThemePreviewActivity wallpaperActivity = new ThemePreviewActivity(object2, null, true, false);
            if (this.currentType == 1) {
                wallpaperActivity.setDelegate(new ThemePreviewActivity.WallpaperActivityDelegate() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda8
                    @Override // org.telegram.ui.ThemePreviewActivity.WallpaperActivityDelegate
                    public final void didSetNewBackground() {
                        WallpapersListActivity.this.removeSelfFromStack();
                    }
                });
            }
            if (this.selectedBackgroundSlug.equals(slug)) {
                wallpaperActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion);
            }
            wallpaperActivity.setPatterns(this.patterns);
            presentFragment(wallpaperActivity);
            return;
        }
        if (object2 instanceof ColorWallpaper) {
            object2 = ((ColorWallpaper) object2).parentWallpaper;
        }
        if (!(object2 instanceof TLRPC.WallPaper)) {
            return;
        }
        TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) object2;
        if (this.selectedWallPapers.indexOfKey(wallPaper2.id) < 0) {
            this.selectedWallPapers.put(wallPaper2.id, object);
        } else {
            this.selectedWallPapers.remove(wallPaper2.id);
        }
        if (this.selectedWallPapers.size() != 0) {
            this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
        } else {
            this.actionBar.hideActionMode();
        }
        this.scrolling = false;
        if (this.selectedWallPapers.indexOfKey(wallPaper2.id) >= 0) {
            z = true;
        }
        view.setChecked(index, z, true);
    }

    private String getWallPaperSlug(Object object) {
        if (object instanceof TLRPC.TL_wallPaper) {
            return ((TLRPC.TL_wallPaper) object).slug;
        }
        if (object instanceof ColorWallpaper) {
            return ((ColorWallpaper) object).slug;
        }
        if (object instanceof FileWallpaper) {
            return ((FileWallpaper) object).slug;
        }
        return null;
    }

    public void updateRowsSelection() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof WallpaperCell) {
                WallpaperCell cell = (WallpaperCell) child;
                for (int b = 0; b < 5; b++) {
                    cell.setChecked(b, false, true);
                }
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ColorWallpaper colorWallpaper;
        if (id != NotificationCenter.wallpapersDidLoad) {
            if (id == NotificationCenter.didSetNewWallpapper) {
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    recyclerListView.invalidateViews();
                }
                if (this.actionBar != null) {
                    this.actionBar.closeSearchField();
                    return;
                }
                return;
            } else if (id == NotificationCenter.wallpapersNeedReload) {
                getMessagesStorage().getWallpapers();
                return;
            } else {
                return;
            }
        }
        ArrayList<TLRPC.WallPaper> arrayList = (ArrayList) args[0];
        this.patterns.clear();
        this.patternsDict.clear();
        if (this.currentType != 1) {
            this.wallPapers.clear();
            this.localWallPapers.clear();
            this.localDict.clear();
            this.allWallPapers.clear();
            this.allWallPapersDict.clear();
            this.allWallPapers.addAll(arrayList);
        }
        ArrayList<TLRPC.WallPaper> wallPapersToDelete = null;
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            TLRPC.WallPaper wallPaper = arrayList.get(a);
            if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                if ((wallPaper instanceof TLRPC.TL_wallPaper) && !(wallPaper.document instanceof TLRPC.TL_documentEmpty)) {
                    if (wallPaper.pattern && wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                        this.patterns.add(wallPaper);
                        this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                    }
                    this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                    if (this.currentType != 1 && ((!wallPaper.pattern || (wallPaper.settings != null && wallPaper.settings.background_color != 0)) && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0))) {
                        this.wallPapers.add(wallPaper);
                    }
                } else if (wallPaper.settings.background_color != 0) {
                    if (wallPaper.settings.second_background_color != 0 && wallPaper.settings.third_background_color != 0) {
                        colorWallpaper = new ColorWallpaper(null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                    } else {
                        colorWallpaper = new ColorWallpaper(null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.rotation);
                    }
                    colorWallpaper.slug = wallPaper.slug;
                    colorWallpaper.intensity = wallPaper.settings.intensity / 100.0f;
                    colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, false);
                    colorWallpaper.parentWallpaper = wallPaper;
                    if (wallPaper.id < 0) {
                        String hash = colorWallpaper.getHash();
                        if (this.localDict.containsKey(hash)) {
                            if (wallPapersToDelete == null) {
                                wallPapersToDelete = new ArrayList<>();
                            }
                            wallPapersToDelete.add(wallPaper);
                        } else {
                            this.localWallPapers.add(colorWallpaper);
                            this.localDict.put(hash, colorWallpaper);
                        }
                    }
                    if (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0) {
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
        }
        if (wallPapersToDelete != null) {
            int N2 = wallPapersToDelete.size();
            for (int a2 = 0; a2 < N2; a2++) {
                getMessagesStorage().deleteWallpaper(wallPapersToDelete.get(a2).id);
            }
        }
        this.selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
        fillWallpapersWithCustom();
        loadWallpapers(false);
    }

    public void loadWallpapers(final boolean force) {
        long acc = 0;
        if (!force) {
            int N = this.allWallPapers.size();
            for (int a = 0; a < N; a++) {
                Object object = this.allWallPapers.get(a);
                if (object instanceof TLRPC.WallPaper) {
                    TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object;
                    if (wallPaper.id >= 0) {
                        acc = MediaDataController.calcHash(acc, wallPaper.id);
                    }
                }
            }
        }
        TLRPC.TL_account_getWallPapers req = new TLRPC.TL_account_getWallPapers();
        req.hash = acc;
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.m4806lambda$loadWallpapers$6$orgtelegramuiWallpapersListActivity(force, tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$loadWallpapers$6$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4806lambda$loadWallpapers$6$orgtelegramuiWallpapersListActivity(final boolean force, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                WallpapersListActivity.this.m4805lambda$loadWallpapers$5$orgtelegramuiWallpapersListActivity(response, force);
            }
        });
    }

    /* renamed from: lambda$loadWallpapers$5$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ void m4805lambda$loadWallpapers$5$orgtelegramuiWallpapersListActivity(TLObject response, boolean force) {
        ColorWallpaper colorWallpaper;
        if (response instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers res = (TLRPC.TL_account_wallPapers) response;
            this.patterns.clear();
            this.patternsDict.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(res.wallpapers);
                this.wallPapers.addAll(this.localWallPapers);
            }
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                TLRPC.WallPaper wallPaper = res.wallpapers.get(a);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                    if ((wallPaper instanceof TLRPC.TL_wallPaper) && !(wallPaper.document instanceof TLRPC.TL_documentEmpty)) {
                        this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                        if (wallPaper.pattern && wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        if (this.currentType != 1 && ((!wallPaper.pattern || (wallPaper.settings != null && wallPaper.settings.background_color != 0)) && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0))) {
                            this.wallPapers.add(wallPaper);
                        }
                    } else if (wallPaper.settings.background_color != 0 && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0)) {
                        if (wallPaper.settings.second_background_color != 0 && wallPaper.settings.third_background_color != 0) {
                            colorWallpaper = new ColorWallpaper(null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                        } else {
                            colorWallpaper = new ColorWallpaper(null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.rotation);
                        }
                        colorWallpaper.slug = wallPaper.slug;
                        colorWallpaper.intensity = wallPaper.settings.intensity / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, false);
                        colorWallpaper.parentWallpaper = wallPaper;
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
            fillWallpapersWithCustom();
            getMessagesStorage().putWallpapers(res.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            if (!force) {
                this.listView.smoothScrollToPosition(0);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x00b7, code lost:
        r0 = r6;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void fillWallpapersWithCustom() {
        /*
            Method dump skipped, instructions count: 931
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    /* renamed from: lambda$fillWallpapersWithCustom$7$org-telegram-ui-WallpapersListActivity */
    public /* synthetic */ int m4804x891910bd(long idFinal, String slugFinal, boolean currentThemeDark, Object o1, Object o2) {
        if (o1 instanceof ColorWallpaper) {
            o1 = ((ColorWallpaper) o1).parentWallpaper;
        }
        if (o2 instanceof ColorWallpaper) {
            o2 = ((ColorWallpaper) o2).parentWallpaper;
        }
        if (!(o1 instanceof TLRPC.WallPaper) || !(o2 instanceof TLRPC.WallPaper)) {
            return 0;
        }
        TLRPC.WallPaper wallPaper1 = (TLRPC.WallPaper) o1;
        TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) o2;
        if (idFinal != 0) {
            if (wallPaper1.id == idFinal) {
                return -1;
            }
            if (wallPaper2.id == idFinal) {
                return 1;
            }
        } else if (slugFinal.equals(wallPaper1.slug)) {
            return -1;
        } else {
            if (slugFinal.equals(wallPaper2.slug)) {
                return 1;
            }
        }
        if (!currentThemeDark) {
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper1.slug)) {
                return -1;
            }
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper2.slug)) {
                return 1;
            }
        }
        int index1 = this.allWallPapers.indexOf(wallPaper1);
        int index2 = this.allWallPapers.indexOf(wallPaper2);
        if ((!wallPaper1.dark || !wallPaper2.dark) && (wallPaper1.dark || wallPaper2.dark)) {
            return (!wallPaper1.dark || wallPaper2.dark) ? currentThemeDark ? 1 : -1 : currentThemeDark ? -1 : 1;
        } else if (index1 > index2) {
            return 1;
        } else {
            return index1 < index2 ? -1 : 0;
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = 0 + 1;
            this.rowCount = i;
            this.uploadImageRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.setColorRow = i;
            this.rowCount = i2 + 1;
            this.sectionRow = i2;
        } else {
            this.uploadImageRow = -1;
            this.setColorRow = -1;
            this.sectionRow = -1;
        }
        if (!this.wallPapers.isEmpty()) {
            int ceil = (int) Math.ceil(this.wallPapers.size() / this.columnsCount);
            this.totalWallpaperRows = ceil;
            int i3 = this.rowCount;
            this.wallPaperStartRow = i3;
            this.rowCount = i3 + ceil;
        } else {
            this.wallPaperStartRow = -1;
        }
        if (this.currentType == 0) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.resetSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.resetRow = i5;
            this.rowCount = i6 + 1;
            this.resetInfoRow = i6;
        } else {
            this.resetSectionRow = -1;
            this.resetRow = -1;
            this.resetInfoRow = -1;
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            this.scrolling = true;
            listAdapter.notifyDataSetChanged();
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            ViewTreeObserver obs = recyclerListView.getViewTreeObserver();
            obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.WallpapersListActivity.7
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    WallpapersListActivity.this.fixLayoutInternal();
                    if (WallpapersListActivity.this.listView != null) {
                        WallpapersListActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    public void fixLayoutInternal() {
        if (getParentActivity() == null) {
            return;
        }
        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        int rotation = manager.getDefaultDisplay().getRotation();
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 3;
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 5;
        } else {
            this.columnsCount = 3;
        }
        updateRows();
    }

    /* loaded from: classes4.dex */
    private class ColorCell extends View {
        private int color;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ColorCell(Context context) {
            super(context);
            WallpapersListActivity.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int value) {
            this.color = value;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            WallpapersListActivity.this.colorPaint.setColor(this.color);
            canvas.drawCircle(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(31.0f), AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorPaint);
            if (this.color == Theme.getColor(Theme.key_windowBackgroundWhite)) {
                canvas.drawCircle(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(31.0f), AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorFramePaint);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int imageReqId;
        private RecyclerListView innerListView;
        private String lastSearchImageString;
        private String lastSearchString;
        private int lastSearchToken;
        private Context mContext;
        private String nextImagesSearchOffset;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private String selectedColor;
        private ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
        private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
        private boolean bingSearchEndReached = true;

        /* loaded from: classes4.dex */
        private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
            private CategoryAdapterRecycler() {
                SearchAdapter.this = r1;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = new ColorCell(SearchAdapter.this.mContext);
                return new RecyclerListView.Holder(view);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ColorCell cell = (ColorCell) holder.itemView;
                cell.setColor(WallpapersListActivity.searchColors[position]);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return WallpapersListActivity.searchColors.length;
            }
        }

        public SearchAdapter(Context context) {
            WallpapersListActivity.this = r1;
            this.mContext = context;
        }

        public RecyclerListView getInnerListView() {
            return this.innerListView;
        }

        public void onDestroy() {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }

        public void clearColor() {
            this.selectedColor = null;
            processSearch(null, true);
        }

        public void processSearch(String text, boolean now) {
            if (text != null && this.selectedColor != null) {
                text = "#color" + this.selectedColor + " " + text;
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (!TextUtils.isEmpty(text)) {
                WallpapersListActivity.this.searchEmptyView.showProgress();
                final String textFinal = text;
                if (now) {
                    doSearch(textFinal);
                } else {
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            WallpapersListActivity.SearchAdapter.this.m4812xd2243515(textFinal);
                        }
                    };
                    this.searchRunnable = runnable2;
                    AndroidUtilities.runOnUIThread(runnable2, 500L);
                }
            } else {
                this.searchResult.clear();
                this.searchResultKeys.clear();
                this.bingSearchEndReached = true;
                this.lastSearchString = null;
                if (this.imageReqId != 0) {
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                    this.imageReqId = 0;
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        /* renamed from: lambda$processSearch$0$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4812xd2243515(String textFinal) {
            doSearch(textFinal);
            this.searchRunnable = null;
        }

        private void doSearch(String textFinal) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(textFinal, "", true);
            this.lastSearchString = textFinal;
            notifyDataSetChanged();
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
            ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WallpapersListActivity.SearchAdapter.this.m4814x7955a680(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$searchBotUser$2$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4814x7955a680(final TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        WallpapersListActivity.SearchAdapter.this.m4813x5f3a27e1(response);
                    }
                });
            }
        }

        /* renamed from: lambda$searchBotUser$1$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4813x5f3a27e1(TLObject response) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(res.chats, false);
            WallpapersListActivity.this.getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            searchImages(str, "", false);
        }

        public void loadMoreResults() {
            if (this.bingSearchEndReached || this.imageReqId != 0) {
                return;
            }
            searchImages(this.lastSearchString, this.nextImagesSearchOffset, true);
        }

        private void searchImages(String query, String offset, boolean searchUser) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            this.lastSearchImageString = query;
            TLObject object = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getUserOrChat(MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot);
            if (!(object instanceof TLRPC.User)) {
                if (searchUser) {
                    searchBotUser();
                    return;
                }
                return;
            }
            TLRPC.User user = (TLRPC.User) object;
            TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
            req.query = "#wallpaper " + query;
            req.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser(user);
            req.offset = offset;
            req.peer = new TLRPC.TL_inputPeerEmpty();
            final int token = this.lastSearchToken + 1;
            this.lastSearchToken = token;
            this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WallpapersListActivity.SearchAdapter.this.m4816xe82ce30e(token, tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
        }

        /* renamed from: lambda$searchImages$4$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4816xe82ce30e(final int token, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WallpapersListActivity.SearchAdapter.this.m4815xce11646f(token, response);
                }
            });
        }

        /* renamed from: lambda$searchImages$3$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4815xce11646f(int token, TLObject response) {
            if (token != this.lastSearchToken) {
                return;
            }
            boolean z = false;
            this.imageReqId = 0;
            int oldCount = this.searchResult.size();
            if (response != null) {
                TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                this.nextImagesSearchOffset = res.next_offset;
                int count = res.results.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.BotInlineResult result = res.results.get(a);
                    if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                        MediaController.SearchImage bingImage = new MediaController.SearchImage();
                        if (result.photo != null) {
                            TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, GroupCallActivity.TABLET_LIST_SIZE);
                            if (size != null) {
                                bingImage.width = size.w;
                                bingImage.height = size.h;
                                bingImage.photoSize = size;
                                bingImage.photo = result.photo;
                                bingImage.size = size.size;
                                bingImage.thumbPhotoSize = size2;
                                bingImage.id = result.id;
                                bingImage.type = 0;
                                this.searchResult.add(bingImage);
                                this.searchResultKeys.put(bingImage.id, bingImage);
                            }
                        } else if (result.content != null) {
                            int b = 0;
                            while (true) {
                                if (b >= result.content.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute = result.content.attributes.get(b);
                                if (!(attribute instanceof TLRPC.TL_documentAttributeImageSize)) {
                                    b++;
                                } else {
                                    bingImage.width = attribute.w;
                                    bingImage.height = attribute.h;
                                    break;
                                }
                            }
                            if (result.thumb != null) {
                                bingImage.thumbUrl = result.thumb.url;
                            } else {
                                bingImage.thumbUrl = null;
                            }
                            bingImage.imageUrl = result.content.url;
                            bingImage.size = result.content.size;
                            bingImage.id = result.id;
                            bingImage.type = 0;
                            this.searchResult.add(bingImage);
                            this.searchResultKeys.put(bingImage.id, bingImage);
                        }
                    }
                }
                if (oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null) {
                    z = true;
                }
                this.bingSearchEndReached = z;
            }
            if (oldCount != this.searchResult.size()) {
                int prevLastRow = oldCount % WallpapersListActivity.this.columnsCount;
                int oldRowCount = (int) Math.ceil(oldCount / WallpapersListActivity.this.columnsCount);
                if (prevLastRow != 0) {
                    notifyItemChanged(((int) Math.ceil(oldCount / WallpapersListActivity.this.columnsCount)) - 1);
                }
                int newRowCount = (int) Math.ceil(this.searchResult.size() / WallpapersListActivity.this.columnsCount);
                WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(oldRowCount, newRowCount - oldRowCount);
            }
            WallpapersListActivity.this.searchEmptyView.showTextView();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!TextUtils.isEmpty(this.lastSearchString)) {
                return (int) Math.ceil(this.searchResult.size() / WallpapersListActivity.this.columnsCount);
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 2;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new WallpaperCell(this.mContext) { // from class: org.telegram.ui.WallpapersListActivity.SearchAdapter.1
                        @Override // org.telegram.ui.Cells.WallpaperCell
                        protected void onWallpaperClick(Object wallPaper, int index) {
                            WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(wallPaper, null, true, false));
                        }
                    };
                    break;
                case 1:
                    RecyclerListView horizontalListView = new RecyclerListView(this.mContext) { // from class: org.telegram.ui.WallpapersListActivity.SearchAdapter.2
                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    horizontalListView.setItemAnimator(null);
                    horizontalListView.setLayoutAnimation(null);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) { // from class: org.telegram.ui.WallpapersListActivity.SearchAdapter.3
                        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    horizontalListView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    horizontalListView.setClipToPadding(false);
                    layoutManager.setOrientation(0);
                    horizontalListView.setLayoutManager(layoutManager);
                    horizontalListView.setAdapter(new CategoryAdapterRecycler());
                    horizontalListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view2, int i) {
                            WallpapersListActivity.SearchAdapter.this.m4811x5c085961(view2, i);
                        }
                    });
                    view = horizontalListView;
                    this.innerListView = horizontalListView;
                    break;
                case 2:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            if (viewType == 1) {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            } else {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-WallpapersListActivity$SearchAdapter */
        public /* synthetic */ void m4811x5c085961(View view1, int position) {
            String color = LocaleController.getString("BackgroundSearchColor", R.string.BackgroundSearchColor);
            Spannable spannable = new SpannableString(color + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[position], WallpapersListActivity.searchColorsNamesR[position]));
            spannable.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_actionBarDefaultSubtitle)), color.length(), spannable.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannable);
            WallpapersListActivity.this.searchItem.setSearchFieldHint(null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[position];
            processSearch("", true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    WallpaperCell wallpaperCell = (WallpaperCell) holder.itemView;
                    int position2 = position * WallpapersListActivity.this.columnsCount;
                    int totalRows = (int) Math.ceil(this.searchResult.size() / WallpapersListActivity.this.columnsCount);
                    int i = WallpapersListActivity.this.columnsCount;
                    boolean z = false;
                    boolean z2 = position2 == 0;
                    if (position2 / WallpapersListActivity.this.columnsCount == totalRows - 1) {
                        z = true;
                    }
                    wallpaperCell.setParams(i, z2, z);
                    for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                        int p = position2 + a;
                        Object wallPaper = p < this.searchResult.size() ? this.searchResult.get(p) : null;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, wallPaper, "", null, false);
                    }
                    return;
                case 1:
                default:
                    return;
                case 2:
                    GraySectionCell cell = (GraySectionCell) holder.itemView;
                    cell.setText(LocaleController.getString("SearchByColor", R.string.SearchByColor));
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            WallpapersListActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = R.drawable.greydivider_bottom;
            switch (viewType) {
                case 0:
                    view = new TextCell(this.mContext);
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    Context context = this.mContext;
                    if (WallpapersListActivity.this.wallPaperStartRow != -1) {
                        i = R.drawable.greydivider;
                    }
                    Drawable drawable = Theme.getThemedDrawable(context, i, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 2:
                default:
                    view = new WallpaperCell(this.mContext) { // from class: org.telegram.ui.WallpapersListActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.WallpaperCell
                        protected void onWallpaperClick(Object wallPaper, int index) {
                            WallpapersListActivity.this.onItemClick(this, wallPaper, index);
                        }

                        @Override // org.telegram.ui.Cells.WallpaperCell
                        protected boolean onWallpaperLongClick(Object wallPaper, int index) {
                            return WallpapersListActivity.this.onItemLongClick(this, wallPaper, index);
                        }
                    };
                    break;
                case 3:
                    view = new TextInfoPrivacyCell(this.mContext);
                    Drawable drawable2 = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable2);
                    combinedDrawable2.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable2);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Object selectedWallpaper;
            long id;
            Object selectedWallpaper2;
            Object selectedWallpaper3;
            long id2;
            Object selectedWallpaper4;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position != WallpapersListActivity.this.uploadImageRow) {
                        if (position != WallpapersListActivity.this.setColorRow) {
                            if (position == WallpapersListActivity.this.resetRow) {
                                textCell.setText(LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), false);
                                return;
                            }
                            return;
                        }
                        textCell.setTextAndIcon(LocaleController.getString("SetColor", R.string.SetColor), R.drawable.msg_palette, true);
                        return;
                    }
                    textCell.setTextAndIcon(LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), R.drawable.msg_photos, true);
                    return;
                case 1:
                default:
                    return;
                case 2:
                    WallpaperCell wallpaperCell = (WallpaperCell) holder.itemView;
                    int position2 = (position - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                    wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position2 == 0, position2 / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows - 1);
                    int a = 0;
                    while (a < WallpapersListActivity.this.columnsCount) {
                        int p = position2 + a;
                        Object object = p < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(p) : null;
                        if (object instanceof TLRPC.TL_wallPaper) {
                            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                            Theme.OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
                            if (!WallpapersListActivity.this.selectedBackgroundSlug.equals(wallPaper.slug) || (WallpapersListActivity.this.selectedBackgroundSlug.equals(wallPaper.slug) && wallPaper.settings != null && (WallpapersListActivity.this.selectedColor != Theme.getWallpaperColor(wallPaper.settings.background_color) || WallpapersListActivity.this.selectedGradientColor1 != Theme.getWallpaperColor(wallPaper.settings.second_background_color) || WallpapersListActivity.this.selectedGradientColor2 != Theme.getWallpaperColor(wallPaper.settings.third_background_color) || WallpapersListActivity.this.selectedGradientColor3 != Theme.getWallpaperColor(wallPaper.settings.fourth_background_color) || (WallpapersListActivity.this.selectedGradientColor1 != 0 && WallpapersListActivity.this.selectedGradientColor2 == 0 && WallpapersListActivity.this.selectedGradientRotation != AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, z) && wallPaper.pattern && Math.abs(Theme.getThemeIntensity(wallPaper.settings.intensity / 100.0f) - WallpapersListActivity.this.selectedIntensity) > 0.001f)))) {
                                selectedWallpaper4 = null;
                            } else {
                                selectedWallpaper4 = wallPaper;
                            }
                            long id3 = wallPaper.id;
                            selectedWallpaper = selectedWallpaper4;
                            id = id3;
                        } else if (object instanceof ColorWallpaper) {
                            ColorWallpaper colorWallpaper = (ColorWallpaper) object;
                            if (!Theme.DEFAULT_BACKGROUND_SLUG.equals(colorWallpaper.slug) || !WallpapersListActivity.this.selectedBackgroundSlug.equals(colorWallpaper.slug)) {
                                if (colorWallpaper.color == WallpapersListActivity.this.selectedColor && colorWallpaper.gradientColor1 == WallpapersListActivity.this.selectedGradientColor1 && colorWallpaper.gradientColor2 == WallpapersListActivity.this.selectedGradientColor2 && colorWallpaper.gradientColor3 == WallpapersListActivity.this.selectedGradientColor3 && (WallpapersListActivity.this.selectedGradientColor1 == 0 || colorWallpaper.gradientRotation == WallpapersListActivity.this.selectedGradientRotation)) {
                                    if ((Theme.COLOR_BACKGROUND_SLUG.equals(WallpapersListActivity.this.selectedBackgroundSlug) && colorWallpaper.slug != null) || (!Theme.COLOR_BACKGROUND_SLUG.equals(WallpapersListActivity.this.selectedBackgroundSlug) && (!TextUtils.equals(WallpapersListActivity.this.selectedBackgroundSlug, colorWallpaper.slug) || ((int) (colorWallpaper.intensity * 100.0f)) != ((int) (WallpapersListActivity.this.selectedIntensity * 100.0f))))) {
                                        selectedWallpaper3 = null;
                                    } else {
                                        selectedWallpaper3 = object;
                                    }
                                } else {
                                    selectedWallpaper3 = null;
                                }
                            } else {
                                selectedWallpaper3 = object;
                            }
                            if (colorWallpaper.parentWallpaper != null) {
                                id2 = colorWallpaper.parentWallpaper.id;
                            } else {
                                id2 = 0;
                            }
                            selectedWallpaper = selectedWallpaper3;
                            id = id2;
                        } else if (object instanceof FileWallpaper) {
                            FileWallpaper fileWallpaper = (FileWallpaper) object;
                            if (WallpapersListActivity.this.selectedBackgroundSlug.equals(fileWallpaper.slug)) {
                                selectedWallpaper2 = object;
                            } else {
                                selectedWallpaper2 = null;
                            }
                            selectedWallpaper = selectedWallpaper2;
                            id = 0;
                        } else {
                            selectedWallpaper = null;
                            id = 0;
                        }
                        long id4 = id;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, object, selectedWallpaper, null, false);
                        if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                            wallpaperCell.setChecked(a, WallpapersListActivity.this.selectedWallPapers.indexOfKey(id4) >= 0, !WallpapersListActivity.this.scrolling);
                        } else {
                            wallpaperCell.setChecked(a, false, !WallpapersListActivity.this.scrolling);
                        }
                        a++;
                        z = false;
                    }
                    return;
                case 3:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == WallpapersListActivity.this.resetInfoRow) {
                        cell.setText(LocaleController.getString("ResetChatBackgroundsInfo", R.string.ResetChatBackgroundsInfo));
                        return;
                    }
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != WallpapersListActivity.this.uploadImageRow && position != WallpapersListActivity.this.setColorRow && position != WallpapersListActivity.this.resetRow) {
                if (position != WallpapersListActivity.this.sectionRow && position != WallpapersListActivity.this.resetSectionRow) {
                    if (position == WallpapersListActivity.this.resetInfoRow) {
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        return themeDescriptions;
    }
}
