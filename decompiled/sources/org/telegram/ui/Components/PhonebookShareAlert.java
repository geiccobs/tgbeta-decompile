package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.net.MailTo;
import androidx.core.widget.NestedScrollView;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
/* loaded from: classes5.dex */
public class PhonebookShareAlert extends BottomSheet {
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private Paint backgroundPaint;
    private TextView buttonTextView;
    private TLRPC.User currentUser;
    private ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate delegate;
    private boolean inLayout;
    private boolean isImport;
    private LinearLayout linearLayout;
    private ListAdapter listAdapter;
    private ArrayList<AndroidUtilities.VcardItem> other;
    private BaseFragment parentFragment;
    private int phoneEndRow;
    private int phoneStartRow;
    private ArrayList<AndroidUtilities.VcardItem> phones;
    private int rowCount;
    private int scrollOffsetY;
    private NestedScrollView scrollView;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private int userRow;
    private int vcardEndRow;
    private int vcardStartRow;

    /* loaded from: classes5.dex */
    public class UserCell extends LinearLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public UserCell(Context context) {
            super(context);
            String status;
            PhonebookShareAlert.this = this$0;
            setOrientation(1);
            boolean needPadding = true;
            if (this$0.phones.size() != 1 || this$0.other.size() != 0) {
                if (this$0.currentUser.status != null && this$0.currentUser.status.expires != 0) {
                    status = LocaleController.formatUserStatus(this$0.currentAccount, this$0.currentUser);
                } else {
                    status = null;
                }
            } else {
                status = ((AndroidUtilities.VcardItem) this$0.phones.get(0)).getValue(true);
                needPadding = false;
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(30.0f));
            avatarDrawable.setInfo(this$0.currentUser);
            BackupImageView avatarImageView = new BackupImageView(context);
            avatarImageView.setRoundRadius(AndroidUtilities.dp(40.0f));
            avatarImageView.setForUserOrChat(this$0.currentUser, avatarDrawable);
            addView(avatarImageView, LayoutHelper.createLinear(80, 80, 49, 0, 32, 0, 0));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setTextSize(1, 17.0f);
            textView.setTextColor(this$0.getThemedColor(Theme.key_dialogTextBlack));
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(ContactsController.formatName(this$0.currentUser.first_name, this$0.currentUser.last_name));
            addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, status != null ? 0 : 27));
            if (status != null) {
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(this$0.getThemedColor(Theme.key_dialogTextGray3));
                textView2.setSingleLine(true);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setText(status);
                addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 10, 3, 10, needPadding ? 27 : 11));
            }
        }
    }

    /* loaded from: classes5.dex */
    public class TextCheckBoxCell extends FrameLayout {
        private Switch checkBox;
        private ImageView imageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextCheckBoxCell(Context context) {
            super(context);
            float f;
            float f2;
            float f3;
            float f4;
            PhonebookShareAlert.this = this$0;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(this$0.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setSingleLine(false);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            TextView textView2 = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 17;
            if (LocaleController.isRTL) {
                f = this$0.isImport ? 17 : 64;
            } else {
                f = 72.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            } else {
                f2 = this$0.isImport ? 17 : 64;
            }
            addView(textView2, LayoutHelper.createFrame(-1, -1.0f, i2, f, 10.0f, f2, 0.0f));
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextColor(this$0.getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView4 = this.valueTextView;
            int i4 = LocaleController.isRTL ? 5 : 3;
            if (LocaleController.isRTL) {
                f3 = this$0.isImport ? 17 : 64;
            } else {
                f3 = 72.0f;
            }
            if (LocaleController.isRTL) {
                f4 = 72.0f;
            } else {
                f4 = !this$0.isImport ? 64 : i3;
            }
            addView(textView4, LayoutHelper.createFrame(-2, -2.0f, i4, f3, 35.0f, f4, 0.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 20.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
            if (!this$0.isImport) {
                Switch r3 = new Switch(context);
                this.checkBox = r3;
                r3.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.invalidate();
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            measureChildWithMargins(this.textView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.valueTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.imageView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            Switch r7 = this.checkBox;
            if (r7 != null) {
                measureChildWithMargins(r7, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(64.0f), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)) + (this.needDivider ? 1 : 0));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int y = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView = this.valueTextView;
            textView.layout(textView.getLeft(), y, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + y);
        }

        public void setVCardItem(AndroidUtilities.VcardItem item, int icon, boolean divider) {
            this.textView.setText(item.getValue(true));
            this.valueTextView.setText(item.getType());
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.setChecked(item.checked, false);
            }
            if (icon != 0) {
                this.imageView.setImageResource(icon);
            } else {
                this.imageView.setImageDrawable(null);
            }
            this.needDivider = divider;
            setWillNotDraw(!divider);
        }

        public void setChecked(boolean checked) {
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.setChecked(checked, true);
            }
        }

        public boolean isChecked() {
            Switch r0 = this.checkBox;
            return r0 != null && r0.isChecked();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public PhonebookShareAlert(BaseFragment parent, ContactsController.Contact contact, TLRPC.User user, Uri uri, File file, String firstName, String lastName) {
        this(parent, contact, user, uri, file, firstName, lastName, null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x01b5  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0303  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0310  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r27, org.telegram.messenger.ContactsController.Contact r28, org.telegram.tgnet.TLRPC.User r29, android.net.Uri r30, java.io.File r31, java.lang.String r32, java.lang.String r33, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        /*
            Method dump skipped, instructions count: 868
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ void m2808lambda$new$0$orgtelegramuiComponentsPhonebookShareAlert(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        updateLayout(!this.inLayout);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ void m2810lambda$new$2$orgtelegramuiComponentsPhonebookShareAlert(int position, View view, View v) {
        final AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position >= i && position < this.phoneEndRow) {
            item = this.phones.get(position - i);
        } else {
            int i2 = this.vcardStartRow;
            if (position >= i2 && position < this.vcardEndRow) {
                item = this.other.get(position - i2);
            } else {
                item = null;
            }
        }
        if (item == null) {
            return;
        }
        if (this.isImport) {
            if (item.type == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + item.getValue(false)));
                    intent.addFlags(268435456);
                    this.parentFragment.getParentActivity().startActivityForResult(intent, 500);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            } else if (item.type == 1) {
                Browser.openUrl(this.parentFragment.getParentActivity(), MailTo.MAILTO_SCHEME + item.getValue(false));
                return;
            } else if (item.type == 3) {
                String url = item.getValue(false);
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }
                Browser.openUrl(this.parentFragment.getParentActivity(), url);
                return;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentFragment.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        PhonebookShareAlert.this.m2809lambda$new$1$orgtelegramuiComponentsPhonebookShareAlert(item, dialogInterface, i3);
                    }
                });
                builder.show();
                return;
            }
        }
        item.checked = !item.checked;
        if (position >= this.phoneStartRow && position < this.phoneEndRow) {
            boolean hasChecked = false;
            int b = 0;
            while (true) {
                if (b >= this.phones.size()) {
                    break;
                } else if (!this.phones.get(b).checked) {
                    b++;
                } else {
                    hasChecked = true;
                    break;
                }
            }
            int color = getThemedColor(Theme.key_featuredStickers_buttonText);
            this.buttonTextView.setEnabled(hasChecked);
            this.buttonTextView.setTextColor(hasChecked ? color : Integer.MAX_VALUE & color);
        }
        TextCheckBoxCell cell = (TextCheckBoxCell) view;
        cell.setChecked(item.checked);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ void m2809lambda$new$1$orgtelegramuiComponentsPhonebookShareAlert(AndroidUtilities.VcardItem item, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, item.getValue(false));
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT < 31) {
                    Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ boolean m2811lambda$new$3$orgtelegramuiComponentsPhonebookShareAlert(int position, Theme.ResourcesProvider resourcesProvider, Context context, View v) {
        AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position >= i && position < this.phoneEndRow) {
            item = this.phones.get(position - i);
        } else {
            int i2 = this.vcardStartRow;
            if (position >= i2 && position < this.vcardEndRow) {
                item = this.other.get(position - i2);
            } else {
                item = null;
            }
        }
        if (item == null) {
            return false;
        }
        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, item.getValue(false));
        clipboard.setPrimaryClip(clip);
        if (BulletinFactory.canShowBulletin(this.parentFragment)) {
            if (item.type == 3) {
                BulletinFactory.of((FrameLayout) this.containerView, resourcesProvider).createCopyLinkBulletin().show();
            } else {
                Bulletin.SimpleLayout layout = new Bulletin.SimpleLayout(context, resourcesProvider);
                if (item.type == 0) {
                    layout.textView.setText(LocaleController.getString("PhoneCopied", R.string.PhoneCopied));
                    layout.imageView.setImageResource(R.drawable.msg_calls);
                } else if (item.type == 1) {
                    layout.textView.setText(LocaleController.getString("EmailCopied", R.string.EmailCopied));
                    layout.imageView.setImageResource(R.drawable.msg_mention);
                } else {
                    layout.textView.setText(LocaleController.getString("TextCopied", R.string.TextCopied));
                    layout.imageView.setImageResource(R.drawable.msg_info);
                }
                if (Build.VERSION.SDK_INT < 31) {
                    Bulletin.make((FrameLayout) this.containerView, layout, 1500).show();
                }
            }
        }
        return true;
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ void m2813lambda$new$5$orgtelegramuiComponentsPhonebookShareAlert(Theme.ResourcesProvider resourcesProvider, View v) {
        StringBuilder builder;
        if (this.isImport) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder2.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", R.string.CreateNewContact), LocaleController.getString("AddToExistingContact", R.string.AddToExistingContact)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.PhonebookShareAlert.5
                private void fillRowWithType(String type, ContentValues row) {
                    if (type.startsWith("X-")) {
                        row.put("data2", (Integer) 0);
                        row.put("data3", type.substring(2));
                    } else if ("PREF".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 12);
                    } else if ("HOME".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 1);
                    } else if ("MOBILE".equalsIgnoreCase(type) || "CELL".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 2);
                    } else if ("OTHER".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 7);
                    } else if ("WORK".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 3);
                    } else if ("RADIO".equalsIgnoreCase(type) || "VOICE".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 14);
                    } else if ("PAGER".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 6);
                    } else if ("CALLBACK".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 8);
                    } else if ("CAR".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 9);
                    } else if ("ASSISTANT".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 19);
                    } else if ("MMS".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 20);
                    } else if (type.startsWith("FAX")) {
                        row.put("data2", (Integer) 4);
                    } else {
                        row.put("data2", (Integer) 0);
                        row.put("data3", type);
                    }
                }

                private void fillUrlRowWithType(String type, ContentValues row) {
                    if (type.startsWith("X-")) {
                        row.put("data2", (Integer) 0);
                        row.put("data3", type.substring(2));
                    } else if ("HOMEPAGE".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 1);
                    } else if ("BLOG".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 2);
                    } else if ("PROFILE".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 3);
                    } else if ("HOME".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 4);
                    } else if ("WORK".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 5);
                    } else if ("FTP".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 6);
                    } else if ("OTHER".equalsIgnoreCase(type)) {
                        row.put("data2", (Integer) 7);
                    } else {
                        row.put("data2", (Integer) 0);
                        row.put("data3", type);
                    }
                }

                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    String str;
                    boolean z;
                    int a;
                    Intent intent2;
                    String str2;
                    boolean orgAdded;
                    AndroidUtilities.VcardItem item;
                    String str3;
                    AnonymousClass5 anonymousClass5 = this;
                    int i = 1;
                    if (which == 0) {
                        Intent intent3 = new Intent("android.intent.action.INSERT");
                        intent3.setType("vnd.android.cursor.dir/raw_contact");
                        intent = intent3;
                    } else if (which != 1) {
                        intent = null;
                    } else {
                        Intent intent4 = new Intent("android.intent.action.INSERT_OR_EDIT");
                        intent4.setType("vnd.android.cursor.item/contact");
                        intent = intent4;
                    }
                    intent.putExtra(CommonProperties.NAME, ContactsController.formatName(PhonebookShareAlert.this.currentUser.first_name, PhonebookShareAlert.this.currentUser.last_name));
                    ArrayList data = new ArrayList();
                    int a2 = 0;
                    while (true) {
                        str = "mimetype";
                        z = false;
                        if (a2 >= PhonebookShareAlert.this.phones.size()) {
                            break;
                        }
                        AndroidUtilities.VcardItem item2 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(a2);
                        ContentValues row = new ContentValues();
                        row.put(str, "vnd.android.cursor.item/phone_v2");
                        row.put("data1", item2.getValue(false));
                        anonymousClass5.fillRowWithType(item2.getRawType(false), row);
                        data.add(row);
                        a2++;
                    }
                    boolean orgAdded2 = false;
                    int a3 = 0;
                    while (a3 < PhonebookShareAlert.this.other.size()) {
                        AndroidUtilities.VcardItem item3 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(a3);
                        if (item3.type == i) {
                            ContentValues row2 = new ContentValues();
                            row2.put(str, "vnd.android.cursor.item/email_v2");
                            row2.put("data1", item3.getValue(z));
                            anonymousClass5.fillRowWithType(item3.getRawType(z), row2);
                            data.add(row2);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 3) {
                            ContentValues row3 = new ContentValues();
                            row3.put(str, "vnd.android.cursor.item/website");
                            row3.put("data1", item3.getValue(z));
                            anonymousClass5.fillUrlRowWithType(item3.getRawType(z), row3);
                            data.add(row3);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 4) {
                            ContentValues row4 = new ContentValues();
                            row4.put(str, "vnd.android.cursor.item/note");
                            row4.put("data1", item3.getValue(z));
                            data.add(row4);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 5) {
                            ContentValues row5 = new ContentValues();
                            row5.put(str, "vnd.android.cursor.item/contact_event");
                            row5.put("data1", item3.getValue(z));
                            row5.put("data2", (Integer) 3);
                            data.add(row5);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else {
                            intent2 = intent;
                            String str4 = "data5";
                            if (item3.type == 2) {
                                ContentValues row6 = new ContentValues();
                                row6.put(str, "vnd.android.cursor.item/postal-address_v2");
                                String[] args = item3.getRawValue();
                                a = a3;
                                int a4 = args.length;
                                if (a4 <= 0) {
                                    orgAdded = orgAdded2;
                                } else {
                                    orgAdded = orgAdded2;
                                    row6.put(str4, args[0]);
                                }
                                if (args.length > 1) {
                                    row6.put("data6", args[1]);
                                }
                                if (args.length > 2) {
                                    row6.put("data4", args[2]);
                                }
                                if (args.length > 3) {
                                    row6.put("data7", args[3]);
                                }
                                if (args.length > 4) {
                                    row6.put("data8", args[4]);
                                }
                                if (args.length > 5) {
                                    row6.put("data9", args[5]);
                                }
                                if (args.length > 6) {
                                    row6.put("data10", args[6]);
                                }
                                String type = item3.getRawType(false);
                                if ("HOME".equalsIgnoreCase(type)) {
                                    row6.put("data2", (Integer) 1);
                                } else if ("WORK".equalsIgnoreCase(type)) {
                                    row6.put("data2", (Integer) 2);
                                } else if ("OTHER".equalsIgnoreCase(type)) {
                                    row6.put("data2", (Integer) 3);
                                }
                                data.add(row6);
                                anonymousClass5 = this;
                                str2 = str;
                            } else {
                                a = a3;
                                orgAdded = orgAdded2;
                                int a5 = item3.type;
                                if (a5 == 20) {
                                    ContentValues row7 = new ContentValues();
                                    row7.put(str, "vnd.android.cursor.item/im");
                                    String imType = item3.getRawType(true);
                                    String type2 = item3.getRawType(false);
                                    row7.put("data1", item3.getValue(false));
                                    if ("AIM".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 0);
                                    } else if ("MSN".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 1);
                                    } else if ("YAHOO".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 2);
                                    } else if ("SKYPE".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 3);
                                    } else if ("QQ".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 4);
                                    } else if ("GOOGLE-TALK".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 5);
                                    } else if ("ICQ".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 6);
                                    } else if ("JABBER".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 7);
                                    } else if ("NETMEETING".equalsIgnoreCase(imType)) {
                                        row7.put(str4, (Integer) 8);
                                    } else {
                                        row7.put(str4, (Integer) (-1));
                                        row7.put("data6", item3.getRawType(true));
                                    }
                                    if ("HOME".equalsIgnoreCase(type2)) {
                                        row7.put("data2", (Integer) 1);
                                    } else if ("WORK".equalsIgnoreCase(type2)) {
                                        row7.put("data2", (Integer) 2);
                                    } else if ("OTHER".equalsIgnoreCase(type2)) {
                                        row7.put("data2", (Integer) 3);
                                    }
                                    data.add(row7);
                                    anonymousClass5 = this;
                                    str2 = str;
                                } else if (item3.type != 6) {
                                    anonymousClass5 = this;
                                    str2 = str;
                                } else if (orgAdded) {
                                    anonymousClass5 = this;
                                    str2 = str;
                                } else {
                                    boolean orgAdded3 = true;
                                    ContentValues row8 = new ContentValues();
                                    row8.put(str, "vnd.android.cursor.item/organization");
                                    int b = a;
                                    while (true) {
                                        String str5 = str4;
                                        anonymousClass5 = this;
                                        if (b >= PhonebookShareAlert.this.other.size()) {
                                            break;
                                        }
                                        AndroidUtilities.VcardItem orgItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(b);
                                        boolean orgAdded4 = orgAdded3;
                                        if (orgItem.type != 6) {
                                            str3 = str;
                                            item = item3;
                                        } else {
                                            String type3 = orgItem.getRawType(true);
                                            if ("ORG".equalsIgnoreCase(type3)) {
                                                String[] value = orgItem.getRawValue();
                                                str3 = str;
                                                if (value.length == 0) {
                                                    item = item3;
                                                } else {
                                                    item = item3;
                                                    if (value.length >= 1) {
                                                        row8.put("data1", value[0]);
                                                    }
                                                    if (value.length >= 2) {
                                                        row8.put(str5, value[1]);
                                                    }
                                                }
                                            } else {
                                                str3 = str;
                                                item = item3;
                                                if ("TITLE".equalsIgnoreCase(type3)) {
                                                    row8.put("data4", orgItem.getValue(false));
                                                } else if ("ROLE".equalsIgnoreCase(type3)) {
                                                    row8.put("data4", orgItem.getValue(false));
                                                }
                                            }
                                            String orgType = orgItem.getRawType(true);
                                            if ("WORK".equalsIgnoreCase(orgType)) {
                                                row8.put("data2", (Integer) 1);
                                            } else if ("OTHER".equalsIgnoreCase(orgType)) {
                                                row8.put("data2", (Integer) 2);
                                            }
                                        }
                                        b++;
                                        str4 = str5;
                                        orgAdded3 = orgAdded4;
                                        str = str3;
                                        item3 = item;
                                    }
                                    str2 = str;
                                    data.add(row8);
                                    orgAdded2 = orgAdded3;
                                    a3 = a + 1;
                                    str = str2;
                                    intent = intent2;
                                    i = 1;
                                    z = false;
                                }
                            }
                        }
                        orgAdded2 = orgAdded;
                        a3 = a + 1;
                        str = str2;
                        intent = intent2;
                        i = 1;
                        z = false;
                    }
                    Intent intent5 = intent;
                    intent5.putExtra("finishActivityOnSaveCompleted", true);
                    intent5.putParcelableArrayListExtra("data", data);
                    try {
                        PhonebookShareAlert.this.parentFragment.getParentActivity().startActivity(intent5);
                        PhonebookShareAlert.this.dismiss();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            builder2.show();
            return;
        }
        if (!this.currentUser.restriction_reason.isEmpty()) {
            builder = new StringBuilder(this.currentUser.restriction_reason.get(0).text);
        } else {
            builder = new StringBuilder(String.format(Locale.US, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
        }
        int idx = builder.lastIndexOf("END:VCARD");
        if (idx >= 0) {
            this.currentUser.phone = null;
            for (int a = this.phones.size() - 1; a >= 0; a--) {
                AndroidUtilities.VcardItem item = this.phones.get(a);
                if (item.checked) {
                    if (this.currentUser.phone == null) {
                        this.currentUser.phone = item.getValue(false);
                    }
                    for (int b = 0; b < item.vcardData.size(); b++) {
                        builder.insert(idx, item.vcardData.get(b) + "\n");
                    }
                }
            }
            for (int a2 = this.other.size() - 1; a2 >= 0; a2--) {
                AndroidUtilities.VcardItem item2 = this.other.get(a2);
                if (item2.checked) {
                    for (int b2 = item2.vcardData.size() - 1; b2 >= 0; b2 += -1) {
                        builder.insert(idx, item2.vcardData.get(b2) + "\n");
                    }
                }
            }
            this.currentUser.restriction_reason.clear();
            TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
            reason.text = builder.toString();
            reason.reason = "";
            reason.platform = "";
            this.currentUser.restriction_reason.add(reason);
        }
        BaseFragment baseFragment = this.parentFragment;
        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isInScheduleMode()) {
            ChatActivity chatActivity = (ChatActivity) this.parentFragment;
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i) {
                    PhonebookShareAlert.this.m2812lambda$new$4$orgtelegramuiComponentsPhonebookShareAlert(z, i);
                }
            }, resourcesProvider);
            return;
        }
        this.delegate.didSelectContact(this.currentUser, true, 0);
        dismiss();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhonebookShareAlert */
    public /* synthetic */ void m2812lambda$new$4$orgtelegramuiComponentsPhonebookShareAlert(boolean notify, int scheduleDate) {
        this.delegate.didSelectContact(this.currentUser, notify, scheduleDate);
        dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, new Bulletin.Delegate() { // from class: org.telegram.ui.Components.PhonebookShareAlert.6
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int tag) {
                return AndroidUtilities.dp(74.0f);
            }
        });
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        Bulletin.removeDelegate((FrameLayout) this.containerView);
    }

    public void setDelegate(ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    public void updateLayout(boolean animated) {
        View child = this.scrollView.getChildAt(0);
        int top = child.getTop() - this.scrollView.getScrollY();
        int newOffset = 0;
        if (top >= 0) {
            newOffset = top;
        }
        boolean show = newOffset <= 0;
        float f = 0.0f;
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimation = animatorSet2;
                animatorSet2.setDuration(180L);
                AnimatorSet animatorSet3 = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
                View view = this.actionBarShadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhonebookShareAlert.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhonebookShareAlert.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(show ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(show ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.containerView.invalidate();
        }
        child.getBottom();
        this.scrollView.getMeasuredHeight();
        boolean show2 = child.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight();
        if ((show2 && this.shadow.getTag() == null) || (!show2 && this.shadow.getTag() != null)) {
            this.shadow.setTag(show2 ? 1 : null);
            AnimatorSet animatorSet4 = this.shadowAnimation;
            if (animatorSet4 != null) {
                animatorSet4.cancel();
                this.shadowAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.shadowAnimation = animatorSet5;
                animatorSet5.setDuration(180L);
                AnimatorSet animatorSet6 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = show2 ? 1.0f : 0.0f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property3, fArr3);
                animatorSet6.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhonebookShareAlert.8
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhonebookShareAlert.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (show2) {
                f = 1.0f;
            }
            view3.setAlpha(f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    private void updateRows() {
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.userRow = 0;
        if (this.phones.size() <= 1 && this.other.isEmpty()) {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
            this.vcardStartRow = -1;
            this.vcardEndRow = -1;
            return;
        }
        if (this.phones.isEmpty()) {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
        } else {
            int i = this.rowCount;
            this.phoneStartRow = i;
            int size = i + this.phones.size();
            this.rowCount = size;
            this.phoneEndRow = size;
        }
        if (this.other.isEmpty()) {
            this.vcardStartRow = -1;
            this.vcardEndRow = -1;
            return;
        }
        int i2 = this.rowCount;
        this.vcardStartRow = i2;
        int size2 = i2 + this.other.size();
        this.rowCount = size2;
        this.vcardEndRow = size2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter {
        private ListAdapter() {
            PhonebookShareAlert.this = r1;
        }

        public int getItemCount() {
            return PhonebookShareAlert.this.rowCount;
        }

        public void onBindViewHolder(View itemView, int position, int type) {
            int icon;
            AndroidUtilities.VcardItem item;
            boolean z = true;
            if (type == 1) {
                TextCheckBoxCell cell = (TextCheckBoxCell) itemView;
                if (position < PhonebookShareAlert.this.phoneStartRow || position >= PhonebookShareAlert.this.phoneEndRow) {
                    item = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(position - PhonebookShareAlert.this.vcardStartRow);
                    if (item.type == 1) {
                        icon = R.drawable.msg_mention;
                    } else {
                        int icon2 = item.type;
                        if (icon2 == 2) {
                            icon = R.drawable.msg_location;
                        } else {
                            int icon3 = item.type;
                            if (icon3 == 3) {
                                icon = R.drawable.msg_link;
                            } else {
                                int icon4 = item.type;
                                if (icon4 == 4) {
                                    icon = R.drawable.msg_info;
                                } else {
                                    int icon5 = item.type;
                                    if (icon5 == 5) {
                                        icon = R.drawable.msg_calendar2;
                                    } else {
                                        int icon6 = item.type;
                                        if (icon6 == 6) {
                                            if ("ORG".equalsIgnoreCase(item.getRawType(true))) {
                                                icon = R.drawable.msg_work;
                                            } else {
                                                icon = R.drawable.msg_jobtitle;
                                            }
                                        } else {
                                            int icon7 = item.type;
                                            if (icon7 == 20) {
                                                icon = R.drawable.msg_info;
                                            } else {
                                                icon = R.drawable.msg_info;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(position - PhonebookShareAlert.this.phoneStartRow);
                    icon = R.drawable.msg_calls;
                }
                if (position == getItemCount() - 1) {
                    z = false;
                }
                cell.setVCardItem(item, icon, z);
            }
        }

        public View createView(Context context, int position) {
            View view;
            int viewType = getItemViewType(position);
            switch (viewType) {
                case 0:
                    view = new UserCell(context);
                    break;
                default:
                    view = new TextCheckBoxCell(context);
                    break;
            }
            onBindViewHolder(view, position, viewType);
            return view;
        }

        public int getItemViewType(int position) {
            if (position == PhonebookShareAlert.this.userRow) {
                return 0;
            }
            return 1;
        }
    }
}
