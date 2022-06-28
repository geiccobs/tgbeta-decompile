package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ShareLocationDrawable;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
/* loaded from: classes4.dex */
public class ActionIntroActivity extends BaseFragment implements LocationController.LocationFetchCallback {
    public static final int ACTION_TYPE_CHANGE_PHONE_NUMBER = 3;
    public static final int ACTION_TYPE_CHANNEL_CREATE = 0;
    public static final int ACTION_TYPE_NEARBY_GROUP_CREATE = 2;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ACCESS = 1;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ENABLED = 4;
    public static final int ACTION_TYPE_QR_LOGIN = 5;
    public static final int ACTION_TYPE_SET_PASSCODE = 6;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private TextView buttonTextView;
    private int[] colors;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    private int currentType;
    private LinearLayout descriptionLayout;
    private TextView descriptionText;
    private TextView descriptionText2;
    private TextView[] desctiptionLines = new TextView[6];
    private Drawable drawable1;
    private Drawable drawable2;
    private boolean flickerButton;
    private RLottieImageView imageView;
    private ActionIntroQRLoginDelegate qrLoginDelegate;
    private boolean showingAsBottomSheet;
    private TextView subtitleTextView;
    private TextView titleTextView;

    /* loaded from: classes4.dex */
    public interface ActionIntroQRLoginDelegate {
        void didFindQRCode(String str);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ActionType {
    }

    public ActionIntroActivity(int type) {
        this.currentType = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        int i = 0;
        if (this.actionBar != null) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
            this.actionBar.setCastShadows(false);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ActionIntroActivity.1
                @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
                public void onItemClick(int id) {
                    if (id == -1) {
                        ActionIntroActivity.this.finishFragment();
                    }
                }
            });
        }
        this.fragmentView = new ViewGroup(context) { // from class: org.telegram.ui.ActionIntroActivity.2
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (ActionIntroActivity.this.actionBar != null) {
                    ActionIntroActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                }
                switch (ActionIntroActivity.this.currentType) {
                    case 0:
                        if (width > height) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.68f), C.BUFFER_FLAG_ENCRYPTED));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.399f), C.BUFFER_FLAG_ENCRYPTED));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(86.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        }
                    case 1:
                    case 4:
                    case 6:
                        if (ActionIntroActivity.this.currentType == 6) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(140.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(140.0f), C.BUFFER_FLAG_ENCRYPTED));
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                        if (width > height) {
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        } else {
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            if (ActionIntroActivity.this.currentType == 6) {
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
                                break;
                            } else {
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                                break;
                            }
                        }
                    case 2:
                        if (width > height) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.44f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        }
                    case 3:
                        ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(150.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(150.0f), C.BUFFER_FLAG_ENCRYPTED));
                        if (width > height) {
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        } else {
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        }
                    case 5:
                        if (ActionIntroActivity.this.showingAsBottomSheet) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.32f), C.BUFFER_FLAG_ENCRYPTED));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            height = ActionIntroActivity.this.imageView.getMeasuredHeight() + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f) + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + ActionIntroActivity.this.buttonTextView.getMeasuredHeight();
                            break;
                        } else if (width > height) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.68f), C.BUFFER_FLAG_ENCRYPTED));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.399f), C.BUFFER_FLAG_ENCRYPTED));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                            break;
                        }
                }
                setMeasuredDimension(width, height);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int y;
                if (ActionIntroActivity.this.actionBar != null) {
                    ActionIntroActivity.this.actionBar.layout(0, 0, r, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                }
                int width = r - l;
                int height = b - t;
                switch (ActionIntroActivity.this.currentType) {
                    case 0:
                        if (r > b) {
                            int y2 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            ActionIntroActivity.this.imageView.layout(0, y2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y2);
                            int x = (int) (width * 0.4f);
                            int y3 = (int) (height * 0.22f);
                            ActionIntroActivity.this.titleTextView.layout(x, y3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y3);
                            int x2 = (int) (width * 0.4f);
                            int y4 = (int) (height * 0.39f);
                            ActionIntroActivity.this.descriptionText.layout(x2, y4, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y4);
                            int x3 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y5 = (int) (height * 0.69f);
                            ActionIntroActivity.this.buttonTextView.layout(x3, y5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x3, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y5);
                            return;
                        }
                        int y6 = (int) (height * 0.188f);
                        ActionIntroActivity.this.imageView.layout(0, y6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y6);
                        int y7 = (int) (height * 0.651f);
                        ActionIntroActivity.this.titleTextView.layout(0, y7, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y7);
                        int y8 = (int) (height * 0.731f);
                        ActionIntroActivity.this.descriptionText.layout(0, y8, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y8);
                        int x4 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y9 = (int) (height * 0.853f);
                        ActionIntroActivity.this.buttonTextView.layout(x4, y9, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x4, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y9);
                        return;
                    case 1:
                    case 4:
                        if (r > b) {
                            int y10 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            int x5 = ((int) ((width * 0.5f) - ActionIntroActivity.this.imageView.getMeasuredWidth())) / 2;
                            ActionIntroActivity.this.imageView.layout(x5, y10, ActionIntroActivity.this.imageView.getMeasuredWidth() + x5, ActionIntroActivity.this.imageView.getMeasuredHeight() + y10);
                            int x6 = (int) (width * 0.4f);
                            int y11 = (int) (height * 0.14f);
                            ActionIntroActivity.this.titleTextView.layout(x6, y11, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x6, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y11);
                            int x7 = (int) (width * 0.4f);
                            int y12 = (int) (height * 0.31f);
                            ActionIntroActivity.this.descriptionText.layout(x7, y12, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x7, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y12);
                            int x8 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y13 = (int) (height * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(x8, y13, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x8, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y13);
                            return;
                        }
                        int y14 = (int) (height * 0.214f);
                        int x9 = (width - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(x9, y14, ActionIntroActivity.this.imageView.getMeasuredWidth() + x9, ActionIntroActivity.this.imageView.getMeasuredHeight() + y14);
                        int y15 = (int) (height * 0.414f);
                        ActionIntroActivity.this.titleTextView.layout(0, y15, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y15);
                        int y16 = (int) (height * 0.493f);
                        ActionIntroActivity.this.descriptionText.layout(0, y16, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y16);
                        int x10 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y17 = (int) (height * 0.71f);
                        ActionIntroActivity.this.buttonTextView.layout(x10, y17, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x10, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y17);
                        return;
                    case 2:
                        if (r > b) {
                            int y18 = ((int) ((height * 0.9f) - ActionIntroActivity.this.imageView.getMeasuredHeight())) / 2;
                            ActionIntroActivity.this.imageView.layout(0, y18, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y18);
                            int y19 = y18 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, y19, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y19);
                            int x11 = (int) (width * 0.4f);
                            int y20 = (int) (height * 0.12f);
                            ActionIntroActivity.this.titleTextView.layout(x11, y20, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x11, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y20);
                            int x12 = (int) (width * 0.4f);
                            int y21 = (int) (height * 0.26f);
                            ActionIntroActivity.this.descriptionText.layout(x12, y21, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x12, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y21);
                            int x13 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y22 = (int) (height * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(x13, y22, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x13, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y22);
                            int x14 = (int) (width * 0.4f);
                            int y23 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(x14, y23, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + x14, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + y23);
                            return;
                        }
                        int y24 = (int) (height * 0.197f);
                        ActionIntroActivity.this.imageView.layout(0, y24, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y24);
                        int y25 = (int) (height * 0.421f);
                        ActionIntroActivity.this.titleTextView.layout(0, y25, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y25);
                        int y26 = (int) (height * 0.477f);
                        ActionIntroActivity.this.subtitleTextView.layout(0, y26, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y26);
                        int y27 = (int) (height * 0.537f);
                        ActionIntroActivity.this.descriptionText.layout(0, y27, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y27);
                        int x15 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y28 = (int) (height * 0.71f);
                        ActionIntroActivity.this.buttonTextView.layout(x15, y28, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x15, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y28);
                        int y29 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                        ActionIntroActivity.this.descriptionText2.layout(0, y29, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + y29);
                        return;
                    case 3:
                        if (r > b) {
                            int y30 = ((int) ((height * 0.95f) - ActionIntroActivity.this.imageView.getMeasuredHeight())) / 2;
                            int x16 = (int) ((getWidth() * 0.35f) - ActionIntroActivity.this.imageView.getMeasuredWidth());
                            ActionIntroActivity.this.imageView.layout(x16, y30, ActionIntroActivity.this.imageView.getMeasuredWidth() + x16, ActionIntroActivity.this.imageView.getMeasuredHeight() + y30);
                            int x17 = (int) (width * 0.4f);
                            int y31 = (int) (height * 0.12f);
                            ActionIntroActivity.this.titleTextView.layout(x17, y31, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x17, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y31);
                            int x18 = (int) (width * 0.4f);
                            int y32 = (int) (height * 0.24f);
                            ActionIntroActivity.this.descriptionText.layout(x18, y32, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x18, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y32);
                            int x19 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y33 = (int) (height * 0.8f);
                            ActionIntroActivity.this.buttonTextView.layout(x19, y33, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x19, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y33);
                            int x20 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.subtitleTextView.getMeasuredWidth()) / 2.0f));
                            int y34 = y33 - (ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + AndroidUtilities.dp(16.0f));
                            ActionIntroActivity.this.subtitleTextView.layout(x20, y34, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth() + x20, ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y34);
                            return;
                        }
                        int y35 = (int) (height * 0.3f);
                        int x21 = (width - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(x21, y35, ActionIntroActivity.this.imageView.getMeasuredWidth() + x21, ActionIntroActivity.this.imageView.getMeasuredHeight() + y35);
                        int y36 = y35 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                        ActionIntroActivity.this.titleTextView.layout(0, y36, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y36);
                        int y37 = (int) (y36 + ActionIntroActivity.this.titleTextView.getTextSize() + AndroidUtilities.dp(16.0f));
                        ActionIntroActivity.this.descriptionText.layout(0, y37, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y37);
                        int x22 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y38 = (height - ActionIntroActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        ActionIntroActivity.this.buttonTextView.layout(x22, y38, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x22, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y38);
                        int x23 = (width - ActionIntroActivity.this.subtitleTextView.getMeasuredWidth()) / 2;
                        int y39 = y38 - (ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + AndroidUtilities.dp(32.0f));
                        ActionIntroActivity.this.subtitleTextView.layout(x23, y39, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth() + x23, ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y39);
                        return;
                    case 5:
                        if (ActionIntroActivity.this.showingAsBottomSheet) {
                            ActionIntroActivity.this.imageView.layout(0, 0, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + 0);
                            int y40 = (int) (height * 0.403f);
                            ActionIntroActivity.this.titleTextView.layout(0, y40, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y40);
                            int y41 = (int) (height * 0.631f);
                            int x24 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                            ActionIntroActivity.this.descriptionLayout.layout(x24, y41, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + x24, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + y41);
                            int x25 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int y42 = (int) (height * 0.853f);
                            ActionIntroActivity.this.buttonTextView.layout(x25, y42, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x25, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y42);
                            return;
                        } else if (r > b) {
                            int y43 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            ActionIntroActivity.this.imageView.layout(0, y43, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y43);
                            int x26 = (int) (width * 0.4f);
                            int y44 = (int) (height * 0.08f);
                            ActionIntroActivity.this.titleTextView.layout(x26, y44, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x26, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y44);
                            int x27 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2.0f));
                            int y45 = (int) (height * 0.25f);
                            ActionIntroActivity.this.descriptionLayout.layout(x27, y45, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + x27, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + y45);
                            int x28 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y46 = (int) (height * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(x28, y46, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x28, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y46);
                            return;
                        } else {
                            if (AndroidUtilities.displaySize.y < 1800) {
                                int y47 = (int) (height * 0.06f);
                                ActionIntroActivity.this.imageView.layout(0, y47, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y47);
                                int y48 = (int) (height * 0.463f);
                                ActionIntroActivity.this.titleTextView.layout(0, y48, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y48);
                                y = (int) (height * 0.543f);
                            } else {
                                int y49 = (int) (height * 0.148f);
                                ActionIntroActivity.this.imageView.layout(0, y49, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y49);
                                int y50 = (int) (height * 0.551f);
                                ActionIntroActivity.this.titleTextView.layout(0, y50, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y50);
                                y = (int) (height * 0.631f);
                            }
                            int x29 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                            ActionIntroActivity.this.descriptionLayout.layout(x29, y, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + x29, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + y);
                            int x30 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int y51 = (int) (height * 0.853f);
                            ActionIntroActivity.this.buttonTextView.layout(x30, y51, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x30, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y51);
                            return;
                        }
                    case 6:
                        if (r > b) {
                            int y52 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            int x31 = ((int) ((width * 0.5f) - ActionIntroActivity.this.imageView.getMeasuredWidth())) / 2;
                            ActionIntroActivity.this.imageView.layout(x31, y52, ActionIntroActivity.this.imageView.getMeasuredWidth() + x31, ActionIntroActivity.this.imageView.getMeasuredHeight() + y52);
                            int x32 = (int) (width * 0.4f);
                            int y53 = (int) (height * 0.14f);
                            ActionIntroActivity.this.titleTextView.layout(x32, y53, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x32, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y53);
                            int x33 = (int) (width * 0.4f);
                            int y54 = (int) (height * 0.31f);
                            ActionIntroActivity.this.descriptionText.layout(x33, y54, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x33, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y54);
                            int x34 = (int) ((width * 0.4f) + (((width * 0.6f) - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y55 = (int) (height * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(x34, y55, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x34, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y55);
                            return;
                        }
                        int y56 = (int) (height * 0.3f);
                        int x35 = (width - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(x35, y56, ActionIntroActivity.this.imageView.getMeasuredWidth() + x35, ActionIntroActivity.this.imageView.getMeasuredHeight() + y56);
                        int y57 = y56 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                        ActionIntroActivity.this.titleTextView.layout(0, y57, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y57);
                        int y58 = (int) (y57 + ActionIntroActivity.this.titleTextView.getTextSize() + AndroidUtilities.dp(16.0f));
                        ActionIntroActivity.this.descriptionText.layout(0, y58, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y58);
                        int x36 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y59 = (height - ActionIntroActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        ActionIntroActivity.this.buttonTextView.layout(x36, y59, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x36, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y59);
                        return;
                    default:
                        return;
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(ActionIntroActivity$$ExternalSyntheticLambda6.INSTANCE);
        if (this.actionBar != null) {
            viewGroup.addView(this.actionBar);
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        viewGroup.addView(rLottieImageView);
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        int i2 = 1;
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        TextView textView2 = new TextView(context);
        this.subtitleTextView = textView2;
        int i3 = 3;
        textView2.setTextColor(Theme.getColor(this.currentType == 3 ? Theme.key_featuredStickers_addButton : Theme.key_windowBackgroundWhiteBlackText));
        this.subtitleTextView.setGravity(1);
        float f = 15.0f;
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        int i4 = 2;
        if (this.currentType == 2) {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        this.subtitleTextView.setVisibility(8);
        viewGroup.addView(this.subtitleTextView);
        TextView textView3 = new TextView(context);
        this.descriptionText = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        int i5 = this.currentType;
        if (i5 == 6 || i5 == 3) {
            this.descriptionText.setPadding(AndroidUtilities.dp(48.0f), 0, AndroidUtilities.dp(48.0f), 0);
        } else if (i5 == 2) {
            this.descriptionText.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText);
        String str = "";
        if (this.currentType == 5) {
            LinearLayout linearLayout = new LinearLayout(context);
            this.descriptionLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.descriptionLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
            this.descriptionLayout.setGravity(LocaleController.isRTL ? 5 : 3);
            viewGroup.addView(this.descriptionLayout);
            int a = 0;
            while (a < i3) {
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(i);
                this.descriptionLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, a != i4 ? 7.0f : 0.0f));
                this.desctiptionLines[a * 2] = new TextView(context);
                this.desctiptionLines[a * 2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.desctiptionLines[a * 2].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[a * 2].setTextSize(i2, f);
                TextView textView4 = this.desctiptionLines[a * 2];
                String str2 = LocaleController.isRTL ? ".%d" : "%d.";
                Object[] objArr = new Object[i2];
                objArr[i] = Integer.valueOf(a + 1);
                textView4.setText(String.format(str2, objArr));
                this.desctiptionLines[a * 2].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.desctiptionLines[(a * 2) + i2] = new TextView(context);
                this.desctiptionLines[(a * 2) + i2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.desctiptionLines[(a * 2) + i2].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[(a * 2) + i2].setTextSize(i2, f);
                if (a == 0) {
                    this.desctiptionLines[(a * 2) + i2].setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    this.desctiptionLines[(a * 2) + i2].setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
                    String text = LocaleController.getString("AuthAnotherClientInfo1", R.string.AuthAnotherClientInfo1);
                    SpannableStringBuilder spanned = new SpannableStringBuilder(text);
                    int index1 = text.indexOf(42);
                    int index2 = text.lastIndexOf(42);
                    if (index1 != -1 && index2 != -1 && index1 != index2) {
                        this.desctiptionLines[(a * 2) + 1].setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(index2, index2 + 1, (CharSequence) str);
                        spanned.replace(index1, index1 + 1, (CharSequence) str);
                        spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", R.string.AuthAnotherClientDownloadClientUrl)), index1, index2 - 1, 33);
                    }
                    this.desctiptionLines[(a * 2) + 1].setText(spanned);
                } else if (a == 1) {
                    this.desctiptionLines[(a * 2) + 1].setText(LocaleController.getString("AuthAnotherClientInfo2", R.string.AuthAnotherClientInfo2));
                } else {
                    this.desctiptionLines[(a * 2) + 1].setText(LocaleController.getString("AuthAnotherClientInfo3", R.string.AuthAnotherClientInfo3));
                }
                if (!LocaleController.isRTL) {
                    linearLayout2.addView(this.desctiptionLines[a * 2], LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 4.0f, 0.0f));
                    linearLayout2.addView(this.desctiptionLines[(a * 2) + 1], LayoutHelper.createLinear(-2, -2));
                } else {
                    linearLayout2.setGravity(5);
                    linearLayout2.addView(this.desctiptionLines[(a * 2) + 1], LayoutHelper.createLinear(0, -2, 1.0f));
                    linearLayout2.addView(this.desctiptionLines[a * 2], LayoutHelper.createLinear(-2, -2, 4.0f, 0.0f, 0.0f, 0.0f));
                }
                a++;
                i = 0;
                i2 = 1;
                f = 15.0f;
                i3 = 3;
                i4 = 2;
            }
            this.descriptionText.setVisibility(8);
        }
        TextView textView5 = new TextView(context);
        this.descriptionText2 = textView5;
        textView5.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setTextSize(1, 13.0f);
        this.descriptionText2.setVisibility(8);
        if (this.currentType == 2) {
            this.descriptionText2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        } else {
            this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText2);
        TextView textView6 = new TextView(context) { // from class: org.telegram.ui.ActionIntroActivity.3
            CellFlickerDrawable cellFlickerDrawable;

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (ActionIntroActivity.this.flickerButton) {
                    if (this.cellFlickerDrawable == null) {
                        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
                        this.cellFlickerDrawable = cellFlickerDrawable;
                        cellFlickerDrawable.drawFrame = false;
                        this.cellFlickerDrawable.repeatProgress = 2.0f;
                    }
                    this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), null);
                    invalidate();
                }
            }
        };
        this.buttonTextView = textView6;
        textView6.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int i6 = this.currentType;
        int buttonRadiusDp = (i6 == 6 || i6 == 3) ? 6 : 4;
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, buttonRadiusDp));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionIntroActivity.this.m1438lambda$createView$2$orgtelegramuiActionIntroActivity(view);
            }
        });
        switch (this.currentType) {
            case 0:
                this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.imageView.setAnimation(R.raw.channel_create, 200, 200);
                this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", R.string.ChannelAlertTitle));
                this.descriptionText.setText(LocaleController.getString("ChannelAlertText", R.string.ChannelAlertText));
                this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", R.string.ChannelAlertCreate2));
                this.imageView.playAnimation();
                this.flickerButton = true;
                break;
            case 1:
                this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
                this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
                this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", R.string.PeopleNearbyAccessInfo));
                this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", R.string.PeopleNearbyAllowAccess));
                break;
            case 2:
                this.subtitleTextView.setVisibility(0);
                this.descriptionText2.setVisibility(0);
                this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? R.drawable.groupsintro2 : R.drawable.groupsintro);
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                TextView textView7 = this.subtitleTextView;
                String str3 = this.currentGroupCreateDisplayAddress;
                if (str3 != null) {
                    str = str3;
                }
                textView7.setText(str);
                this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", R.string.NearbyCreateGroup));
                this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", R.string.NearbyCreateGroupInfo));
                this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", R.string.NearbyCreateGroupInfo2));
                this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", R.string.NearbyStartGroup));
                break;
            case 3:
                this.subtitleTextView.setVisibility(0);
                this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.imageView.setAnimation(R.raw.utyan_change_number, 200, 200);
                this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ActionIntroActivity.this.m1440lambda$createView$4$orgtelegramuiActionIntroActivity(view);
                    }
                });
                UserConfig userConfig = getUserConfig();
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                if (user == null) {
                    user = userConfig.getCurrentUser();
                }
                if (user != null) {
                    TextView textView8 = this.subtitleTextView;
                    PhoneFormat phoneFormat = PhoneFormat.getInstance();
                    textView8.setText(LocaleController.formatString("PhoneNumberKeepButton", R.string.PhoneNumberKeepButton, phoneFormat.format("+" + user.phone)));
                }
                this.subtitleTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ActionIntroActivity.this.m1441lambda$createView$5$orgtelegramuiActionIntroActivity(view);
                    }
                });
                this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", R.string.PhoneNumberChange2));
                this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp)));
                this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", R.string.PhoneNumberChange2));
                this.imageView.playAnimation();
                this.flickerButton = true;
                break;
            case 4:
                this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
                this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
                this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", R.string.PeopleNearbyGpsInfo));
                this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", R.string.PeopleNearbyGps));
                break;
            case 5:
                this.colors = new int[8];
                updateColors();
                this.imageView.setAnimation(R.raw.qr_login, 334, 334, this.colors);
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient));
                this.buttonTextView.setText(LocaleController.getString("AuthAnotherClientScan", R.string.AuthAnotherClientScan));
                this.imageView.playAnimation();
                break;
            case 6:
                this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.imageView.setAnimation(R.raw.utyan_passcode, 200, 200);
                this.imageView.setFocusable(false);
                this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ActionIntroActivity.this.m1439lambda$createView$3$orgtelegramuiActionIntroActivity(view);
                    }
                });
                this.titleTextView.setText(LocaleController.getString("Passcode", R.string.Passcode));
                this.descriptionText.setText(LocaleController.getString("ChangePasscodeInfoShort", R.string.ChangePasscodeInfoShort));
                this.buttonTextView.setText(LocaleController.getString("EnablePasscode", R.string.EnablePasscode));
                this.imageView.playAnimation();
                this.flickerButton = true;
                break;
        }
        if (this.flickerButton) {
            this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(34.0f), AndroidUtilities.dp(8.0f));
            this.buttonTextView.setTextSize(1, 15.0f);
        }
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1438lambda$createView$2$orgtelegramuiActionIntroActivity(View v) {
        if (getParentActivity() == null) {
            return;
        }
        switch (this.currentType) {
            case 0:
                Bundle args = new Bundle();
                args.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(args), true);
                return;
            case 1:
                getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                return;
            case 2:
                if (this.currentGroupCreateAddress == null || this.currentGroupCreateLocation == null) {
                    return;
                }
                Bundle args2 = new Bundle();
                long[] array = {getUserConfig().getClientUserId()};
                args2.putLongArray("result", array);
                args2.putInt("chatType", 4);
                args2.putString("address", this.currentGroupCreateAddress);
                args2.putParcelable("location", this.currentGroupCreateLocation);
                presentFragment(new GroupCreateFinalActivity(args2), true);
                return;
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", R.string.PhoneNumberChangeTitle));
                builder.setMessage(LocaleController.getString("PhoneNumberAlert", R.string.PhoneNumberAlert));
                builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ActionIntroActivity.this.m1437lambda$createView$1$orgtelegramuiActionIntroActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
                return;
            case 4:
                try {
                    getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            case 5:
                if (getParentActivity() == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
                    return;
                } else {
                    processOpenQrReader();
                    return;
                }
            case 6:
                presentFragment(new PasscodeActivity(1), true);
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1437lambda$createView$1$orgtelegramuiActionIntroActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new LoginActivity().changePhoneNumber(), true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1439lambda$createView$3$orgtelegramuiActionIntroActivity(View v) {
        if (!this.imageView.getAnimatedDrawable().isRunning()) {
            this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.imageView.playAnimation();
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1440lambda$createView$4$orgtelegramuiActionIntroActivity(View v) {
        if (!this.imageView.getAnimatedDrawable().isRunning()) {
            this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.imageView.playAnimation();
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1441lambda$createView$5$orgtelegramuiActionIntroActivity(View v) {
        getParentLayout().closeLastFragment(true);
    }

    @Override // org.telegram.messenger.LocationController.LocationFetchCallback
    public void onLocationAddressAvailable(String address, String displayAddress, Location location) {
        TextView textView = this.subtitleTextView;
        if (textView == null) {
            return;
        }
        textView.setText(address);
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        if (this.currentType == 4) {
            boolean enabled = true;
            if (Build.VERSION.SDK_INT >= 28) {
                LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
                enabled = lm.isLocationEnabled();
            } else if (Build.VERSION.SDK_INT >= 19) {
                try {
                    boolean z = false;
                    int mode = Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0);
                    if (mode != 0) {
                        z = true;
                    }
                    enabled = z;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (enabled) {
                presentFragment(new PeopleNearbyActivity(), true);
            }
        }
    }

    public void updateColors() {
        int[] iArr = this.colors;
        if (iArr == null || this.imageView == null) {
            return;
        }
        iArr[0] = 3355443;
        iArr[1] = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
        int[] iArr2 = this.colors;
        iArr2[2] = 16777215;
        iArr2[3] = Theme.getColor(Theme.key_windowBackgroundWhite);
        int[] iArr3 = this.colors;
        iArr3[4] = 5285866;
        iArr3[5] = Theme.getColor(Theme.key_featuredStickers_addButton);
        int[] iArr4 = this.colors;
        iArr4[6] = 2170912;
        iArr4[7] = Theme.getColor(Theme.key_windowBackgroundWhite);
        this.imageView.replaceColors(this.colors);
    }

    public void setGroupCreateAddress(String address, String displayAddress, Location location) {
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location;
        if (location != null && address == null) {
            LocationController.fetchLocationAddress(location, this);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (getParentActivity() == null) {
            return;
        }
        if (requestCode == 2) {
            if (grantResults != null && grantResults.length != 0) {
                if (grantResults[0] != 0) {
                    showDialog(AlertsCreator.createLocationRequiredDialog(getParentActivity(), false));
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActionIntroActivity.this.m1442x15419b5b();
                        }
                    });
                }
            }
        } else if (requestCode == 34) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                processOpenQrReader();
            } else {
                new AlertDialog.Builder(getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString("QRCodePermissionNoCameraWithHint", R.string.QRCodePermissionNoCameraWithHint))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ActionIntroActivity.this.m1443x28e96edc(dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).setTopAnimation(R.raw.permission_request_camera, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).show();
            }
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$6$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1442x15419b5b() {
        presentFragment(new PeopleNearbyActivity(), true);
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$7$org-telegram-ui-ActionIntroActivity */
    public /* synthetic */ void m1443x28e96edc(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setQrLoginDelegate(ActionIntroQRLoginDelegate actionIntroQRLoginDelegate) {
        this.qrLoginDelegate = actionIntroQRLoginDelegate;
    }

    private void processOpenQrReader() {
        CameraScanActivity.showAsSheet(this, false, 1, new CameraScanActivity.CameraScanActivityDelegate() { // from class: org.telegram.ui.ActionIntroActivity.4
            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
            }

            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public /* synthetic */ boolean processQr(String str, Runnable runnable) {
                return CameraScanActivity.CameraScanActivityDelegate.CC.$default$processQr(this, str, runnable);
            }

            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public void didFindQr(String text) {
                ActionIntroActivity.this.finishFragment(false);
                ActionIntroActivity.this.qrLoginDelegate.didFindQRCode(text);
            }
        });
    }

    public int getType() {
        return this.currentType;
    }

    public void setShowingAsBottomSheet(boolean showingAsBottomSheet) {
        this.showingAsBottomSheet = showingAsBottomSheet;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ActionIntroActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ActionIntroActivity.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, delegate, Theme.key_windowBackgroundWhite));
        if (this.actionBar != null) {
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector));
        }
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, delegate, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, delegate, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[0], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[3], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[4], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.desctiptionLines[5], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable1}, null, Theme.key_changephoneinfo_image));
        themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable2}, null, Theme.key_changephoneinfo_image2));
        return themeDescriptions;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
