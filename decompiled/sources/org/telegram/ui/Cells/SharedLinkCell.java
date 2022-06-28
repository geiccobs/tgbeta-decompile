package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
/* loaded from: classes4.dex */
public class SharedLinkCell extends FrameLayout {
    private static final int SPOILER_TYPE_DESCRIPTION = 1;
    private static final int SPOILER_TYPE_DESCRIPTION2 = 2;
    private static final int SPOILER_TYPE_LINK = 0;
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
    private StaticLayout captionLayout;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    private boolean checkingForLongPress;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    private SharedLinkCellDelegate delegate;
    private TextPaint description2TextPaint;
    private int description2Y;
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private List<SpoilerEffect> descriptionLayout2Spoilers;
    private List<SpoilerEffect> descriptionLayoutSpoilers;
    private TextPaint descriptionTextPaint;
    private int descriptionY;
    private boolean drawLinkImageView;
    private StaticLayout fromInfoLayout;
    private int fromInfoLayoutY;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private SparseArray<List<SpoilerEffect>> linkSpoilers;
    private int linkY;
    ArrayList<CharSequence> links;
    private MessageObject message;
    private boolean needDivider;
    private AtomicReference<Layout> patchedDescriptionLayout;
    private AtomicReference<Layout> patchedDescriptionLayout2;
    private Path path;
    private CheckForLongPress pendingCheckForLongPress;
    private CheckForTap pendingCheckForTap;
    private int pressCount;
    private int pressedLink;
    private Theme.ResourcesProvider resourcesProvider;
    private SpoilerEffect spoilerPressed;
    private int spoilerTypePressed;
    private Stack<SpoilerEffect> spoilersPool;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY;
    private Paint urlPaint;
    private LinkPath urlPath;
    private int viewType;

    /* loaded from: classes4.dex */
    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC.WebPage webPage, MessageObject messageObject);

        void onLinkPress(String str, boolean z);
    }

    static /* synthetic */ int access$104(SharedLinkCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    /* loaded from: classes4.dex */
    public final class CheckForTap implements Runnable {
        private CheckForTap() {
            SharedLinkCell.this = r1;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                SharedLinkCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell = SharedLinkCell.this;
            sharedLinkCell.postDelayed(sharedLinkCell.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
        }
    }

    /* loaded from: classes4.dex */
    public class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
            SharedLinkCell.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCell.this.delegate.onLinkPress(SharedLinkCell.this.links.get(SharedLinkCell.this.pressedLink).toString(), true);
                }
                MotionEvent event = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    protected void startCheckLongPress() {
        if (this.checkingForLongPress) {
            return;
        }
        this.checkingForLongPress = true;
        if (this.pendingCheckForTap == null) {
            this.pendingCheckForTap = new CheckForTap();
        }
        postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    public SharedLinkCell(Context context) {
        this(context, 0, null);
    }

    public SharedLinkCell(Context context, int viewType) {
        this(context, viewType, null);
    }

    public SharedLinkCell(Context context, int viewType, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.checkingForLongPress = false;
        this.pendingCheckForLongPress = null;
        this.pressCount = 0;
        this.pendingCheckForTap = null;
        this.links = new ArrayList<>();
        this.linkLayout = new ArrayList<>();
        this.linkSpoilers = new SparseArray<>();
        this.descriptionLayoutSpoilers = new ArrayList();
        this.descriptionLayout2Spoilers = new ArrayList();
        this.spoilersPool = new Stack<>();
        this.path = new Path();
        this.spoilerTypePressed = -1;
        this.titleY = AndroidUtilities.dp(10.0f);
        this.descriptionY = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout = new AtomicReference<>();
        this.description2Y = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout2 = new AtomicReference<>();
        this.captionY = AndroidUtilities.dp(30.0f);
        this.fromInfoLayoutY = AndroidUtilities.dp(30.0f);
        this.resourcesProvider = resourcesProvider;
        this.viewType = viewType;
        setFocusable(true);
        LinkPath linkPath = new LinkPath();
        this.urlPath = linkPath;
        linkPath.setUseRoundRect(true);
        TextPaint textPaint = new TextPaint(1);
        this.titleTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable(resourcesProvider);
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
        if (viewType == 1) {
            TextPaint textPaint2 = new TextPaint(1);
            this.description2TextPaint = textPaint2;
            textPaint2.setTextSize(AndroidUtilities.dp(13.0f));
        }
        TextPaint textPaint3 = new TextPaint(1);
        this.captionTextPaint = textPaint3;
        textPaint3.setTextSize(AndroidUtilities.dp(13.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0304  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01a1 A[Catch: Exception -> 0x0313, TryCatch #9 {Exception -> 0x0313, blocks: (B:51:0x012b, B:53:0x012f, B:56:0x0134, B:59:0x013a, B:61:0x0140, B:70:0x019d, B:72:0x01a1, B:73:0x01b4, B:75:0x01b9, B:77:0x01bf, B:99:0x023b, B:101:0x024b, B:103:0x025b), top: B:293:0x012b }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01b4 A[Catch: Exception -> 0x0313, TryCatch #9 {Exception -> 0x0313, blocks: (B:51:0x012b, B:53:0x012f, B:56:0x0134, B:59:0x013a, B:61:0x0140, B:70:0x019d, B:72:0x01a1, B:73:0x01b4, B:75:0x01b9, B:77:0x01bf, B:99:0x023b, B:101:0x024b, B:103:0x025b), top: B:293:0x012b }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01ce A[Catch: Exception -> 0x0310, TryCatch #0 {Exception -> 0x0310, blocks: (B:62:0x0174, B:64:0x0178, B:68:0x018a, B:78:0x01c3, B:80:0x01ce, B:82:0x01d5, B:84:0x01de, B:86:0x01ea, B:87:0x01f1, B:88:0x020d, B:90:0x0211, B:92:0x021f), top: B:275:0x0174 }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x01ea A[Catch: Exception -> 0x0310, TryCatch #0 {Exception -> 0x0310, blocks: (B:62:0x0174, B:64:0x0178, B:68:0x018a, B:78:0x01c3, B:80:0x01ce, B:82:0x01d5, B:84:0x01de, B:86:0x01ea, B:87:0x01f1, B:88:0x020d, B:90:0x0211, B:92:0x021f), top: B:275:0x0174 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0232  */
    /* JADX WARN: Type inference failed for: r4v52, types: [android.text.SpannableStringBuilder, android.text.Spannable] */
    @Override // android.widget.FrameLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r39, int r40) {
        /*
            Method dump skipped, instructions count: 1967
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public ImageReceiver getLinkImageView() {
        return this.linkImageView;
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        SharedLinkCellDelegate sharedLinkCellDelegate;
        int i;
        int i2;
        boolean result = false;
        boolean z = true;
        z = true;
        z = true;
        int i3 = 1;
        if (this.message != null && !this.linkLayout.isEmpty() && (sharedLinkCellDelegate = this.delegate) != null && sharedLinkCellDelegate.canPerformActions()) {
            if (event.getAction() == 0 || ((this.linkPreviewPressed || this.spoilerPressed != null) && event.getAction() == 1)) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                boolean ok = false;
                int a = 0;
                int offset = 0;
                while (true) {
                    if (a >= this.linkLayout.size()) {
                        break;
                    }
                    StaticLayout layout = this.linkLayout.get(a);
                    if (layout.getLineCount() > 0) {
                        int height = layout.getLineBottom(layout.getLineCount() - i3);
                        int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline);
                        if (x >= linkPosX + layout.getLineLeft(0) && x <= linkPosX + layout.getLineWidth(0)) {
                            int i4 = this.linkY;
                            if (y >= i4 + offset && y <= i4 + offset + height) {
                                ok = true;
                                TLRPC.WebPage webPage = null;
                                if (event.getAction() == 0) {
                                    resetPressedLink();
                                    this.spoilerPressed = null;
                                    if (this.linkSpoilers.get(a, null) != null) {
                                        Iterator<SpoilerEffect> it = this.linkSpoilers.get(a).iterator();
                                        while (true) {
                                            if (!it.hasNext()) {
                                                break;
                                            }
                                            SpoilerEffect eff = it.next();
                                            if (eff.getBounds().contains(x - linkPosX, (y - this.linkY) - offset)) {
                                                this.spoilerPressed = eff;
                                                this.spoilerTypePressed = 0;
                                                break;
                                            }
                                        }
                                    }
                                    if (this.spoilerPressed != null) {
                                        result = true;
                                    } else {
                                        this.pressedLink = a;
                                        this.linkPreviewPressed = true;
                                        startCheckLongPress();
                                        try {
                                            this.urlPath.setCurrentLayout(layout, 0, 0.0f);
                                            layout.getSelectionPath(0, layout.getText().length(), this.urlPath);
                                        } catch (Exception e) {
                                            FileLog.e(e);
                                        }
                                        result = true;
                                    }
                                } else if (this.linkPreviewPressed) {
                                    try {
                                        if (this.pressedLink == 0 && this.message.messageOwner.media != null) {
                                            webPage = this.message.messageOwner.media.webpage;
                                        }
                                        TLRPC.WebPage webPage2 = webPage;
                                        if (webPage2 == null || webPage2.embed_url == null || webPage2.embed_url.length() == 0) {
                                            this.delegate.onLinkPress(this.links.get(this.pressedLink).toString(), false);
                                        } else {
                                            this.delegate.needOpenWebView(webPage2, this.message);
                                        }
                                    } catch (Exception e2) {
                                        FileLog.e(e2);
                                    }
                                    resetPressedLink();
                                    result = true;
                                } else if (this.spoilerPressed != null) {
                                    startSpoilerRipples(x, y, offset);
                                    result = true;
                                }
                            }
                        }
                        offset += height;
                    }
                    a++;
                    i3 = 1;
                }
                if (event.getAction() != 0) {
                    z = true;
                    z = true;
                    z = true;
                    if (event.getAction() == 1 && this.spoilerPressed != null) {
                        startSpoilerRipples(x, y, 0);
                        ok = true;
                        result = true;
                    }
                } else {
                    int offX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline);
                    StaticLayout staticLayout = this.descriptionLayout;
                    if (staticLayout != null && x >= offX && x <= staticLayout.getWidth() + offX && y >= (i2 = this.descriptionY) && y <= i2 + this.descriptionLayout.getHeight()) {
                        Iterator<SpoilerEffect> it2 = this.descriptionLayoutSpoilers.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            SpoilerEffect eff2 = it2.next();
                            if (eff2.getBounds().contains(x - offX, y - this.descriptionY)) {
                                this.spoilerPressed = eff2;
                                this.spoilerTypePressed = 1;
                                ok = true;
                                result = true;
                                break;
                            }
                        }
                    }
                    StaticLayout staticLayout2 = this.descriptionLayout2;
                    if (staticLayout2 != null && x >= offX && x <= staticLayout2.getWidth() + offX && y >= (i = this.description2Y) && y <= i + this.descriptionLayout2.getHeight()) {
                        Iterator<SpoilerEffect> it3 = this.descriptionLayout2Spoilers.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            SpoilerEffect eff3 = it3.next();
                            if (eff3.getBounds().contains(x - offX, y - this.description2Y)) {
                                this.spoilerPressed = eff3;
                                this.spoilerTypePressed = 2;
                                result = true;
                                ok = true;
                                break;
                            }
                        }
                    }
                    z = true;
                }
                if (!ok) {
                    resetPressedLink();
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        } else {
            resetPressedLink();
        }
        if (result || super.onTouchEvent(event)) {
            return z;
        }
        return false;
    }

    private void startSpoilerRipples(int x, int y, int offset) {
        int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline);
        resetPressedLink();
        SpoilerEffect eff = this.spoilerPressed;
        eff.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.SharedLinkCell$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SharedLinkCell.this.m1669xd74f5b04();
            }
        });
        int nx = x - linkPosX;
        float rad = (float) Math.sqrt(Math.pow(getWidth(), 2.0d) + Math.pow(getHeight(), 2.0d));
        float offY = 0.0f;
        switch (this.spoilerTypePressed) {
            case 0:
                for (int i = 0; i < this.linkLayout.size(); i++) {
                    Layout lt = this.linkLayout.get(i);
                    offY += lt.getLineBottom(lt.getLineCount() - 1);
                    for (SpoilerEffect e : this.linkSpoilers.get(i)) {
                        e.startRipple(nx, ((y - getYOffsetForType(0)) - offset) + offY, rad);
                    }
                }
                break;
            case 1:
                for (SpoilerEffect sp : this.descriptionLayoutSpoilers) {
                    sp.startRipple(nx, y - getYOffsetForType(1), rad);
                }
                break;
            case 2:
                for (SpoilerEffect sp2 : this.descriptionLayout2Spoilers) {
                    sp2.startRipple(nx, y - getYOffsetForType(2), rad);
                }
                break;
        }
        for (int i2 = 0; i2 <= 2; i2++) {
            if (i2 != this.spoilerTypePressed) {
                switch (i2) {
                    case 0:
                        for (int j = 0; j < this.linkLayout.size(); j++) {
                            Layout lt2 = this.linkLayout.get(j);
                            offY += lt2.getLineBottom(lt2.getLineCount() - 1);
                            for (SpoilerEffect e2 : this.linkSpoilers.get(j)) {
                                e2.startRipple(e2.getBounds().centerX(), e2.getBounds().centerY(), rad);
                            }
                        }
                        continue;
                    case 1:
                        for (SpoilerEffect sp3 : this.descriptionLayoutSpoilers) {
                            sp3.startRipple(sp3.getBounds().centerX(), sp3.getBounds().centerY(), rad);
                        }
                        continue;
                    case 2:
                        for (SpoilerEffect sp4 : this.descriptionLayout2Spoilers) {
                            sp4.startRipple(sp4.getBounds().centerX(), sp4.getBounds().centerY(), rad);
                        }
                        continue;
                }
            }
        }
        this.spoilerTypePressed = -1;
        this.spoilerPressed = null;
    }

    /* renamed from: lambda$startSpoilerRipples$1$org-telegram-ui-Cells-SharedLinkCell */
    public /* synthetic */ void m1669xd74f5b04() {
        post(new Runnable() { // from class: org.telegram.ui.Cells.SharedLinkCell$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SharedLinkCell.this.m1668x75fcbe65();
            }
        });
    }

    /* renamed from: lambda$startSpoilerRipples$0$org-telegram-ui-Cells-SharedLinkCell */
    public /* synthetic */ void m1668x75fcbe65() {
        this.message.isSpoilersRevealed = true;
        this.linkSpoilers.clear();
        this.descriptionLayoutSpoilers.clear();
        this.descriptionLayout2Spoilers.clear();
        invalidate();
    }

    private int getYOffsetForType(int type) {
        switch (type) {
            case 1:
                return this.descriptionY;
            case 2:
                return this.description2Y;
            default:
                return this.linkY;
        }
    }

    public String getLink(int num) {
        if (num < 0 || num >= this.links.size()) {
            return null;
        }
        return this.links.get(num).toString();
    }

    protected void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        StaticLayout staticLayout;
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3, this.resourcesProvider));
        }
        if (this.dateLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX), this.titleY);
            this.dateLayout.draw(canvas);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            float x = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL) {
                x += this.dateLayout == null ? 0.0f : staticLayout.getWidth() + AndroidUtilities.dp(4.0f);
            }
            canvas.translate(x, this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.captionY);
            this.captionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.descriptionY);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout, this.descriptionLayout, this.descriptionLayoutSpoilers, canvas, false);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.description2Y);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout2, this.descriptionLayout2, this.descriptionLayout2Spoilers, canvas, false);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, this.resourcesProvider));
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = this.linkLayout.get(a);
                List<SpoilerEffect> spoilers = this.linkSpoilers.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.linkY + offset);
                    this.path.rewind();
                    if (spoilers != null) {
                        for (SpoilerEffect eff : spoilers) {
                            Rect b = eff.getBounds();
                            this.path.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
                        }
                    }
                    if (this.urlPaint == null) {
                        Paint paint = new Paint(1);
                        this.urlPaint = paint;
                        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(4.0f)));
                    }
                    this.urlPaint.setColor(Theme.getColor(Theme.key_chat_linkSelectBackground, this.resourcesProvider));
                    canvas.save();
                    canvas.clipPath(this.path, Region.Op.DIFFERENCE);
                    if (this.pressedLink == a) {
                        canvas.drawPath(this.urlPath, this.urlPaint);
                    }
                    layout.draw(canvas);
                    canvas.restore();
                    canvas.save();
                    canvas.clipPath(this.path);
                    this.path.rewind();
                    if (spoilers != null && !spoilers.isEmpty()) {
                        spoilers.get(0).getRipplePath(this.path);
                    }
                    canvas.clipPath(this.path);
                    if (this.pressedLink == a) {
                        canvas.drawPath(this.urlPath, this.urlPaint);
                    }
                    layout.draw(canvas);
                    canvas.restore();
                    if (spoilers != null) {
                        for (SpoilerEffect eff2 : spoilers) {
                            eff2.draw(canvas);
                        }
                    }
                    canvas.restore();
                    offset += layout.getLineBottom(layout.getLineCount() - 1);
                }
            }
        }
        if (this.fromInfoLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.fromInfoLayoutY);
            this.fromInfoLayout.draw(canvas);
            canvas.restore();
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (this.needDivider) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
            } else {
                canvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        info.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            info.setChecked(true);
            info.setCheckable(true);
        }
    }
}
