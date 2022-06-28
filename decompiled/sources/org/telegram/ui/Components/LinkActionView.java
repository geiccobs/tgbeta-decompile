package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
/* loaded from: classes5.dex */
public class LinkActionView extends LinearLayout {
    private ActionBarPopupWindow actionBarPopupWindow;
    private final AvatarsContainer avatarsContainer;
    private final TextView copyView;
    private Delegate delegate;
    BaseFragment fragment;
    private final FrameLayout frameLayout;
    private boolean hideRevokeOption;
    private boolean isChannel;
    String link;
    TextView linkView;
    boolean loadingImporters;
    ImageView optionsView;
    private boolean permanent;
    private QRCodeBottomSheet qrCodeBottomSheet;
    private final TextView removeView;
    private boolean revoked;
    private final TextView shareView;
    private int usersCount;
    private boolean canEdit = true;
    float[] point = new float[2];

    public LinkActionView(final Context context, final BaseFragment fragment, final BottomSheet bottomSheet, long chatId, boolean permanent, boolean isChannel) {
        super(context);
        this.fragment = fragment;
        this.permanent = permanent;
        this.isChannel = isChannel;
        setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        TextView textView = new TextView(context);
        this.linkView = textView;
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(18.0f));
        this.linkView.setTextSize(1, 16.0f);
        this.linkView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.linkView.setSingleLine(true);
        frameLayout.addView(this.linkView);
        ImageView imageView = new ImageView(context);
        this.optionsView = imageView;
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ab_other));
        this.optionsView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.optionsView.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(this.optionsView, LayoutHelper.createFrame(40, 48, 21));
        addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0, 4, 0, 4, 0));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        TextView textView2 = new TextView(context);
        this.copyView = textView2;
        textView2.setGravity(1);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context, R.drawable.msg_copy_filled)), 0, 1, 0);
        spannableStringBuilder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder.append((CharSequence) LocaleController.getString("LinkActionCopy", R.string.LinkActionCopy));
        spannableStringBuilder.append((CharSequence) ".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        textView2.setText(spannableStringBuilder);
        textView2.setContentDescription(LocaleController.getString("LinkActionCopy", R.string.LinkActionCopy));
        textView2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView2.setSingleLine(true);
        linearLayout.addView(textView2, LayoutHelper.createLinear(0, 40, 1.0f, 0, 4, 0, 4, 0));
        TextView textView3 = new TextView(context);
        this.shareView = textView3;
        textView3.setGravity(1);
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
        spannableStringBuilder2.append((CharSequence) "..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context, R.drawable.msg_share_filled)), 0, 1, 0);
        spannableStringBuilder2.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder2.append((CharSequence) LocaleController.getString("LinkActionShare", R.string.LinkActionShare));
        spannableStringBuilder2.append((CharSequence) ".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder2.length() - 1, spannableStringBuilder2.length(), 0);
        textView3.setText(spannableStringBuilder2);
        textView3.setContentDescription(LocaleController.getString("LinkActionShare", R.string.LinkActionShare));
        textView3.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView3.setTextSize(1, 14.0f);
        textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView3.setSingleLine(true);
        linearLayout.addView(textView3, LayoutHelper.createLinear(0, 40, 1.0f, 4, 0, 4, 0));
        TextView textView4 = new TextView(context);
        this.removeView = textView4;
        textView4.setGravity(1);
        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
        spannableStringBuilder3.append((CharSequence) "..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context, R.drawable.msg_delete_filled)), 0, 1, 0);
        spannableStringBuilder3.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder3.append((CharSequence) LocaleController.getString("DeleteLink", R.string.DeleteLink));
        spannableStringBuilder3.append((CharSequence) ".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder3.length() - 1, spannableStringBuilder3.length(), 0);
        textView4.setText(spannableStringBuilder3);
        textView4.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView4.setTextSize(1, 14.0f);
        textView4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView4.setSingleLine(true);
        linearLayout.addView(textView4, LayoutHelper.createLinear(0, -2, 1.0f, 4, 0, 4, 0));
        textView4.setVisibility(8);
        addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        AvatarsContainer avatarsContainer = new AvatarsContainer(context);
        this.avatarsContainer = avatarsContainer;
        addView(avatarsContainer, LayoutHelper.createLinear(-1, 44, 0.0f, 12.0f, 0.0f, 0.0f));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkActionView.this.m2747lambda$new$0$orgtelegramuiComponentsLinkActionView(bottomSheet, fragment, view);
            }
        });
        if (permanent) {
            avatarsContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LinkActionView.this.m2748lambda$new$1$orgtelegramuiComponentsLinkActionView(view);
                }
            });
        }
        textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkActionView.this.m2749lambda$new$2$orgtelegramuiComponentsLinkActionView(fragment, view);
            }
        });
        textView4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkActionView.this.m2751lambda$new$4$orgtelegramuiComponentsLinkActionView(fragment, view);
            }
        });
        this.optionsView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkActionView.this.m2756lambda$new$9$orgtelegramuiComponentsLinkActionView(context, bottomSheet, fragment, view);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LinkActionView.this.copyView.callOnClick();
            }
        });
        updateColors();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2747lambda$new$0$orgtelegramuiComponentsLinkActionView(BottomSheet bottomSheet, BaseFragment fragment, View view) {
        try {
            if (this.link == null) {
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.link);
            clipboard.setPrimaryClip(clip);
            if (bottomSheet != null && bottomSheet.getContainer() != null) {
                BulletinFactory.createCopyLinkBulletin(bottomSheet.getContainer()).show();
            } else {
                BulletinFactory.createCopyLinkBulletin(fragment).show();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2748lambda$new$1$orgtelegramuiComponentsLinkActionView(View view) {
        this.delegate.showUsersForPermanentLink();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2749lambda$new$2$orgtelegramuiComponentsLinkActionView(BaseFragment fragment, View view) {
        try {
            if (this.link == null) {
                return;
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            intent.putExtra("android.intent.extra.TEXT", this.link);
            fragment.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2751lambda$new$4$orgtelegramuiComponentsLinkActionView(BaseFragment fragment, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLink", R.string.DeleteLink));
        builder.setMessage(LocaleController.getString("DeleteLinkHelp", R.string.DeleteLinkHelp));
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LinkActionView.this.m2750lambda$new$3$orgtelegramuiComponentsLinkActionView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        fragment.showDialog(builder.create());
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2750lambda$new$3$orgtelegramuiComponentsLinkActionView(DialogInterface dialogInterface2, int i2) {
        Delegate delegate = this.delegate;
        if (delegate != null) {
            delegate.removeLink();
        }
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2756lambda$new$9$orgtelegramuiComponentsLinkActionView(Context context, BottomSheet bottomSheet, BaseFragment fragment, View view) {
        FrameLayout container;
        if (this.actionBarPopupWindow != null) {
            return;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context);
        if (!this.permanent && this.canEdit) {
            ActionBarMenuSubItem subItem = new ActionBarMenuSubItem(context, true, false);
            subItem.setTextAndIcon(LocaleController.getString("Edit", R.string.Edit), R.drawable.msg_edit);
            layout.addView((View) subItem, LayoutHelper.createLinear(-1, 48));
            subItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LinkActionView.this.m2752lambda$new$5$orgtelegramuiComponentsLinkActionView(view2);
                }
            });
        }
        ActionBarMenuSubItem subItem2 = new ActionBarMenuSubItem(context, true, false);
        subItem2.setTextAndIcon(LocaleController.getString("GetQRCode", R.string.GetQRCode), R.drawable.msg_qrcode);
        layout.addView((View) subItem2, LayoutHelper.createLinear(-1, 48));
        subItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                LinkActionView.this.m2753lambda$new$6$orgtelegramuiComponentsLinkActionView(view2);
            }
        });
        if (!this.hideRevokeOption) {
            ActionBarMenuSubItem subItem3 = new ActionBarMenuSubItem(context, false, true);
            subItem3.setTextAndIcon(LocaleController.getString("RevokeLink", R.string.RevokeLink), R.drawable.msg_delete);
            subItem3.setColors(Theme.getColor(Theme.key_windowBackgroundWhiteRedText), Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            subItem3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LinkActionView.this.m2754lambda$new$7$orgtelegramuiComponentsLinkActionView(view2);
                }
            });
            layout.addView((View) subItem3, LayoutHelper.createLinear(-1, 48));
        }
        if (bottomSheet == null) {
            container = fragment.getParentLayout();
        } else {
            container = bottomSheet.getContainer();
        }
        if (container != null) {
            float x = 0.0f;
            getPointOnScreen(this.frameLayout, container, this.point);
            float y = this.point[1];
            final FrameLayout finalContainer = container;
            final View dimView = new View(context) { // from class: org.telegram.ui.Components.LinkActionView.1
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    canvas.drawColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
                    LinkActionView linkActionView = LinkActionView.this;
                    linkActionView.getPointOnScreen(linkActionView.frameLayout, finalContainer, LinkActionView.this.point);
                    canvas.save();
                    float clipTop = ((View) LinkActionView.this.frameLayout.getParent()).getY() + LinkActionView.this.frameLayout.getY();
                    if (clipTop < 1.0f) {
                        canvas.clipRect(0.0f, (LinkActionView.this.point[1] - clipTop) + 1.0f, getMeasuredWidth(), getMeasuredHeight());
                    }
                    canvas.translate(LinkActionView.this.point[0], LinkActionView.this.point[1]);
                    LinkActionView.this.frameLayout.draw(canvas);
                    canvas.restore();
                }
            };
            final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.LinkActionView.2
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    dimView.invalidate();
                    return true;
                }
            };
            finalContainer.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            container.addView(dimView, LayoutHelper.createFrame(-1, -1.0f));
            dimView.setAlpha(0.0f);
            dimView.animate().alpha(1.0f).setDuration(150L);
            layout.measure(View.MeasureSpec.makeMeasureSpec(container.getMeasuredWidth(), 0), View.MeasureSpec.makeMeasureSpec(container.getMeasuredHeight(), 0));
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
            this.actionBarPopupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.Components.LinkActionView.3
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    LinkActionView.this.actionBarPopupWindow = null;
                    dimView.animate().cancel();
                    dimView.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.LinkActionView.3.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (dimView.getParent() != null) {
                                finalContainer.removeView(dimView);
                            }
                            finalContainer.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        }
                    });
                }
            });
            this.actionBarPopupWindow.setOutsideTouchable(true);
            this.actionBarPopupWindow.setFocusable(true);
            this.actionBarPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            this.actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            this.actionBarPopupWindow.setInputMethodMode(2);
            this.actionBarPopupWindow.setSoftInputMode(0);
            layout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    LinkActionView.this.m2755lambda$new$8$orgtelegramuiComponentsLinkActionView(keyEvent);
                }
            });
            if (AndroidUtilities.isTablet()) {
                y += container.getPaddingTop();
                x = 0.0f - container.getPaddingLeft();
            }
            this.actionBarPopupWindow.showAtLocation(container, 0, (int) (((container.getMeasuredWidth() - layout.getMeasuredWidth()) - AndroidUtilities.dp(16.0f)) + container.getX() + x), (int) (this.frameLayout.getMeasuredHeight() + y + container.getY()));
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2752lambda$new$5$orgtelegramuiComponentsLinkActionView(View view12) {
        ActionBarPopupWindow actionBarPopupWindow = this.actionBarPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        this.delegate.editLink();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2753lambda$new$6$orgtelegramuiComponentsLinkActionView(View view12) {
        showQrCode();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2754lambda$new$7$orgtelegramuiComponentsLinkActionView(View view1) {
        ActionBarPopupWindow actionBarPopupWindow = this.actionBarPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        revokeLink();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2755lambda$new$8$orgtelegramuiComponentsLinkActionView(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && this.actionBarPopupWindow.isShowing()) {
            this.actionBarPopupWindow.dismiss(true);
        }
    }

    public void getPointOnScreen(FrameLayout frameLayout, FrameLayout finalContainer, float[] point) {
        float x = 0.0f;
        float y = 0.0f;
        View v = frameLayout;
        while (v != finalContainer) {
            y += v.getY();
            x += v.getX();
            if (v instanceof ScrollView) {
                y -= v.getScrollY();
            }
            v = (View) v.getParent();
            if (!(v instanceof ViewGroup)) {
                return;
            }
        }
        point[0] = x - finalContainer.getPaddingLeft();
        point[1] = y - finalContainer.getPaddingTop();
    }

    private void showQrCode() {
        String str;
        int i;
        Context context = getContext();
        String str2 = this.link;
        if (this.isChannel) {
            i = R.string.QRCodeLinkHelpChannel;
            str = "QRCodeLinkHelpChannel";
        } else {
            i = R.string.QRCodeLinkHelpGroup;
            str = "QRCodeLinkHelpGroup";
        }
        QRCodeBottomSheet qRCodeBottomSheet = new QRCodeBottomSheet(context, str2, LocaleController.getString(str, i)) { // from class: org.telegram.ui.Components.LinkActionView.5
            @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
            public void dismiss() {
                super.dismiss();
                LinkActionView.this.qrCodeBottomSheet = null;
            }
        };
        this.qrCodeBottomSheet = qRCodeBottomSheet;
        qRCodeBottomSheet.show();
        ActionBarPopupWindow actionBarPopupWindow = this.actionBarPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public void updateColors() {
        this.copyView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.shareView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.removeView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.copyView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.shareView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.removeView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_chat_attachAudioBackground), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
        this.frameLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_graySection), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 76)));
        this.linkView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.optionsView.setColorFilter(Theme.getColor(Theme.key_dialogTextGray3));
        this.avatarsContainer.countTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.avatarsContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), 76)));
        QRCodeBottomSheet qRCodeBottomSheet = this.qrCodeBottomSheet;
        if (qRCodeBottomSheet != null) {
            qRCodeBottomSheet.updateColors();
        }
    }

    public void setLink(String link) {
        this.link = link;
        if (link == null) {
            this.linkView.setText(LocaleController.getString("Loading", R.string.Loading));
        } else if (link.startsWith("https://")) {
            this.linkView.setText(link.substring("https://".length()));
        } else {
            this.linkView.setText(link);
        }
    }

    public void setRevoke(boolean revoked) {
        this.revoked = revoked;
        if (revoked) {
            this.optionsView.setVisibility(8);
            this.shareView.setVisibility(8);
            this.copyView.setVisibility(8);
            this.removeView.setVisibility(0);
            return;
        }
        this.optionsView.setVisibility(0);
        this.shareView.setVisibility(0);
        this.copyView.setVisibility(0);
        this.removeView.setVisibility(8);
    }

    public void showOptions(boolean b) {
        this.optionsView.setVisibility(b ? 0 : 8);
    }

    public void hideRevokeOption(boolean b) {
        if (this.hideRevokeOption != b) {
            this.hideRevokeOption = b;
            this.optionsView.setVisibility(0);
            ImageView imageView = this.optionsView;
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_ab_other));
        }
    }

    /* loaded from: classes5.dex */
    public class AvatarsContainer extends FrameLayout {
        AvatarsImageView avatarsImageView;
        TextView countTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AvatarsContainer(Context context) {
            super(context);
            LinkActionView.this = r7;
            this.avatarsImageView = new AvatarsImageView(context, false) { // from class: org.telegram.ui.Components.LinkActionView.AvatarsContainer.1
                @Override // org.telegram.ui.Components.AvatarsImageView, android.view.View
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int N = Math.min(3, LinkActionView.this.usersCount);
                    int x = N == 0 ? 0 : ((N - 1) * 20) + 24 + 8;
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(x), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                }
            };
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
            TextView textView = new TextView(context);
            this.countTextView = textView;
            textView.setTextSize(1, 14.0f);
            this.countTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(this.avatarsImageView, LayoutHelper.createLinear(-2, -1));
            linearLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -2, 16));
            setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            this.avatarsImageView.commitTransition(false);
        }
    }

    private void revokeLink() {
        if (this.fragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.fragment.getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
        builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LinkActionView.this.m2757lambda$revokeLink$10$orgtelegramuiComponentsLinkActionView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }

    /* renamed from: lambda$revokeLink$10$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2757lambda$revokeLink$10$orgtelegramuiComponentsLinkActionView(DialogInterface dialogInterface, int i) {
        Delegate delegate = this.delegate;
        if (delegate != null) {
            delegate.revokeLink();
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setUsers(int usersCount, ArrayList<TLRPC.User> importers) {
        this.usersCount = usersCount;
        if (usersCount == 0) {
            this.avatarsContainer.setVisibility(8);
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f));
        } else {
            this.avatarsContainer.setVisibility(0);
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(10.0f));
            this.avatarsContainer.countTextView.setText(LocaleController.formatPluralString("PeopleJoined", usersCount, new Object[0]));
            this.avatarsContainer.requestLayout();
        }
        if (importers != null) {
            for (int i = 0; i < 3; i++) {
                if (i < importers.size()) {
                    MessagesController.getInstance(UserConfig.selectedAccount).putUser(importers.get(i), false);
                    this.avatarsContainer.avatarsImageView.setObject(i, UserConfig.selectedAccount, importers.get(i));
                } else {
                    this.avatarsContainer.avatarsImageView.setObject(i, UserConfig.selectedAccount, null);
                }
            }
            this.avatarsContainer.avatarsImageView.commitTransition(false);
        }
    }

    public void loadUsers(final TLRPC.TL_chatInviteExported invite, long chatId) {
        if (invite == null) {
            setUsers(0, null);
            return;
        }
        setUsers(invite.usage, invite.importers);
        if (invite.usage > 0 && invite.importers == null && !this.loadingImporters) {
            TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
            req.link = invite.link;
            req.peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(-chatId);
            req.offset_user = new TLRPC.TL_inputUserEmpty();
            req.limit = Math.min(invite.usage, 3);
            this.loadingImporters = true;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LinkActionView.this.m2746lambda$loadUsers$12$orgtelegramuiComponentsLinkActionView(invite, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$loadUsers$12$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2746lambda$loadUsers$12$orgtelegramuiComponentsLinkActionView(final TLRPC.TL_chatInviteExported invite, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkActionView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LinkActionView.this.m2745lambda$loadUsers$11$orgtelegramuiComponentsLinkActionView(error, response, invite);
            }
        });
    }

    /* renamed from: lambda$loadUsers$11$org-telegram-ui-Components-LinkActionView */
    public /* synthetic */ void m2745lambda$loadUsers$11$orgtelegramuiComponentsLinkActionView(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported invite) {
        this.loadingImporters = false;
        if (error == null) {
            TLRPC.TL_messages_chatInviteImporters inviteImporters = (TLRPC.TL_messages_chatInviteImporters) response;
            if (invite.importers == null) {
                invite.importers = new ArrayList<>(3);
            }
            invite.importers.clear();
            for (int i = 0; i < inviteImporters.users.size(); i++) {
                invite.importers.addAll(inviteImporters.users);
            }
            int i2 = invite.usage;
            setUsers(i2, invite.importers);
        }
    }

    /* loaded from: classes5.dex */
    public interface Delegate {
        void editLink();

        void removeLink();

        void revokeLink();

        void showUsersForPermanentLink();

        /* renamed from: org.telegram.ui.Components.LinkActionView$Delegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$editLink(Delegate _this) {
            }

            public static void $default$removeLink(Delegate _this) {
            }

            public static void $default$showUsersForPermanentLink(Delegate _this) {
            }
        }
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
