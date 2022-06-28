package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProxyListActivity;
/* loaded from: classes4.dex */
public class ProxyListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int callsDetailRow;
    private int callsRow;
    private int connectionsHeaderRow;
    private int currentConnectionState;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int proxyAddRow;
    private int proxyDetailRow;
    private int proxyEndRow;
    private int proxyStartRow;
    private int rowCount;
    private int useProxyDetailRow;
    private boolean useProxyForCalls;
    private int useProxyRow;
    private boolean useProxySettings;

    /* loaded from: classes4.dex */
    public class TextDetailProxyCell extends FrameLayout {
        private Drawable checkDrawable;
        private ImageView checkImageView;
        private int color;
        private SharedConfig.ProxyInfo currentInfo;
        private TextView textView;
        private TextView valueTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextDetailProxyCell(Context context) {
            super(context);
            ProxyListActivity.this = this$0;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            int i2 = 56;
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 56 : 21, 10.0f, LocaleController.isRTL ? 21 : 56, 0.0f));
            TextView textView2 = new TextView(context);
            this.valueTextView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 56 : 21, 35.0f, LocaleController.isRTL ? 21 : i2, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImageView = imageView;
            imageView.setImageResource(R.drawable.msg_info);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), PorterDuff.Mode.MULTIPLY));
            this.checkImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.checkImageView.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
            addView(this.checkImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : i) | 48, 8.0f, 8.0f, 8.0f, 0.0f));
            this.checkImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProxyListActivity$TextDetailProxyCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ProxyListActivity.TextDetailProxyCell.this.m4509xe9540bfe(view);
                }
            });
            setWillNotDraw(false);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProxyListActivity$TextDetailProxyCell */
        public /* synthetic */ void m4509xe9540bfe(View v) {
            ProxyListActivity.this.presentFragment(new ProxySettingsActivity(this.currentInfo));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setProxy(SharedConfig.ProxyInfo proxyInfo) {
            TextView textView = this.textView;
            textView.setText(proxyInfo.address + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + proxyInfo.port);
            this.currentInfo = proxyInfo;
        }

        public void updateStatus() {
            String colorKey;
            if (SharedConfig.currentProxy == this.currentInfo && ProxyListActivity.this.useProxySettings) {
                if (ProxyListActivity.this.currentConnectionState == 3 || ProxyListActivity.this.currentConnectionState == 5) {
                    colorKey = Theme.key_windowBackgroundWhiteBlueText6;
                    if (this.currentInfo.ping != 0) {
                        TextView textView = this.valueTextView;
                        textView.setText(LocaleController.getString("Connected", R.string.Connected) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                    } else {
                        this.valueTextView.setText(LocaleController.getString("Connected", R.string.Connected));
                    }
                    if (!this.currentInfo.checking && !this.currentInfo.available) {
                        this.currentInfo.availableCheckTime = 0L;
                    }
                } else {
                    colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                    this.valueTextView.setText(LocaleController.getString("Connecting", R.string.Connecting));
                }
            } else if (this.currentInfo.checking) {
                this.valueTextView.setText(LocaleController.getString("Checking", R.string.Checking));
                colorKey = Theme.key_windowBackgroundWhiteGrayText2;
            } else if (this.currentInfo.available) {
                if (this.currentInfo.ping != 0) {
                    TextView textView2 = this.valueTextView;
                    textView2.setText(LocaleController.getString("Available", R.string.Available) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                } else {
                    this.valueTextView.setText(LocaleController.getString("Available", R.string.Available));
                }
                colorKey = Theme.key_windowBackgroundWhiteGreenText;
            } else {
                this.valueTextView.setText(LocaleController.getString("Unavailable", R.string.Unavailable));
                colorKey = Theme.key_windowBackgroundWhiteRedText4;
            }
            this.color = Theme.getColor(colorKey);
            this.valueTextView.setTag(colorKey);
            this.valueTextView.setTextColor(this.color);
            Drawable drawable = this.checkDrawable;
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(this.color, PorterDuff.Mode.MULTIPLY));
            }
        }

        public void setChecked(boolean checked) {
            if (!checked) {
                this.valueTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                return;
            }
            if (this.checkDrawable == null) {
                this.checkDrawable = getResources().getDrawable(R.drawable.proxy_check).mutate();
            }
            Drawable drawable = this.checkDrawable;
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(this.color, PorterDuff.Mode.MULTIPLY));
            }
            if (LocaleController.isRTL) {
                this.valueTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.checkDrawable, (Drawable) null);
            } else {
                this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(this.checkDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateStatus();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedConfig.loadProxyList();
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.useProxySettings = preferences.getBoolean("proxy_enabled", false) && !SharedConfig.proxyList.isEmpty();
        this.useProxyForCalls = preferences.getBoolean("proxy_enabled_calls", false);
        updateRows(true);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", R.string.ProxySettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ProxyListActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ProxyListActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ProxyListActivity.this.m4506lambda$createView$0$orgtelegramuiProxyListActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ProxyListActivity.this.m4508lambda$createView$2$orgtelegramuiProxyListActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ProxyListActivity */
    public /* synthetic */ void m4506lambda$createView$0$orgtelegramuiProxyListActivity(View view, int position) {
        if (position != this.useProxyRow) {
            if (position != this.callsRow) {
                if (position >= this.proxyStartRow && position < this.proxyEndRow) {
                    SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(position - this.proxyStartRow);
                    this.useProxySettings = true;
                    SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                    editor.putString("proxy_ip", info.address);
                    editor.putString("proxy_pass", info.password);
                    editor.putString("proxy_user", info.username);
                    editor.putInt("proxy_port", info.port);
                    editor.putString("proxy_secret", info.secret);
                    editor.putBoolean("proxy_enabled", this.useProxySettings);
                    if (!info.secret.isEmpty()) {
                        this.useProxyForCalls = false;
                        editor.putBoolean("proxy_enabled_calls", false);
                    }
                    editor.commit();
                    SharedConfig.currentProxy = info;
                    for (int a = this.proxyStartRow; a < this.proxyEndRow; a++) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a);
                        if (holder != null) {
                            TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                            cell.setChecked(cell.currentInfo == info);
                            cell.updateStatus();
                        }
                    }
                    updateRows(false);
                    RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.useProxyRow);
                    if (holder2 != null) {
                        TextCheckCell textCheckCell = (TextCheckCell) holder2.itemView;
                        textCheckCell.setChecked(true);
                    }
                    ConnectionsManager.setProxySettings(this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
                    return;
                } else if (position == this.proxyAddRow) {
                    presentFragment(new ProxySettingsActivity());
                    return;
                } else {
                    return;
                }
            }
            boolean z = !this.useProxyForCalls;
            this.useProxyForCalls = z;
            TextCheckCell textCheckCell2 = (TextCheckCell) view;
            textCheckCell2.setChecked(z);
            SharedPreferences.Editor editor2 = MessagesController.getGlobalMainSettings().edit();
            editor2.putBoolean("proxy_enabled_calls", this.useProxyForCalls);
            editor2.commit();
            return;
        }
        if (SharedConfig.currentProxy == null) {
            if (SharedConfig.proxyList.isEmpty()) {
                presentFragment(new ProxySettingsActivity());
                return;
            }
            SharedConfig.currentProxy = SharedConfig.proxyList.get(0);
            if (!this.useProxySettings) {
                MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor editor3 = MessagesController.getGlobalMainSettings().edit();
                editor3.putString("proxy_ip", SharedConfig.currentProxy.address);
                editor3.putString("proxy_pass", SharedConfig.currentProxy.password);
                editor3.putString("proxy_user", SharedConfig.currentProxy.username);
                editor3.putInt("proxy_port", SharedConfig.currentProxy.port);
                editor3.putString("proxy_secret", SharedConfig.currentProxy.secret);
                editor3.commit();
            }
        }
        this.useProxySettings = !this.useProxySettings;
        MessagesController.getGlobalMainSettings();
        TextCheckCell textCheckCell3 = (TextCheckCell) view;
        textCheckCell3.setChecked(this.useProxySettings);
        if (!this.useProxySettings) {
            RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.callsRow);
            if (holder3 != null) {
                TextCheckCell textCheckCell4 = (TextCheckCell) holder3.itemView;
                textCheckCell4.setChecked(false);
            }
            this.useProxyForCalls = false;
        }
        SharedPreferences.Editor editor4 = MessagesController.getGlobalMainSettings().edit();
        editor4.putBoolean("proxy_enabled", this.useProxySettings);
        editor4.commit();
        ConnectionsManager.setProxySettings(this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        for (int a2 = this.proxyStartRow; a2 < this.proxyEndRow; a2++) {
            RecyclerListView.Holder holder4 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a2);
            if (holder4 != null) {
                ((TextDetailProxyCell) holder4.itemView).updateStatus();
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProxyListActivity */
    public /* synthetic */ boolean m4508lambda$createView$2$orgtelegramuiProxyListActivity(View view, final int position) {
        if (position >= this.proxyStartRow && position < this.proxyEndRow) {
            final SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(position - this.proxyStartRow);
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("DeleteProxy", R.string.DeleteProxy));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProxyListActivity.this.m4507lambda$createView$1$orgtelegramuiProxyListActivity(info, position, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ProxyListActivity */
    public /* synthetic */ void m4507lambda$createView$1$orgtelegramuiProxyListActivity(SharedConfig.ProxyInfo info, int position, DialogInterface dialog, int which) {
        SharedConfig.deleteProxy(info);
        if (SharedConfig.currentProxy == null) {
            this.useProxyForCalls = false;
            this.useProxySettings = false;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        updateRows(false);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemRemoved(position);
            if (SharedConfig.currentProxy == null) {
                this.listAdapter.notifyItemChanged(this.useProxyRow, 0);
                this.listAdapter.notifyItemChanged(this.callsRow, 0);
            }
        }
    }

    private void updateRows(boolean notify) {
        ListAdapter listAdapter;
        boolean change = false;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.useProxyRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.useProxyDetailRow = i;
        this.rowCount = i2 + 1;
        this.connectionsHeaderRow = i2;
        if (!SharedConfig.proxyList.isEmpty()) {
            int i3 = this.rowCount;
            this.proxyStartRow = i3;
            int size = i3 + SharedConfig.proxyList.size();
            this.rowCount = size;
            this.proxyEndRow = size;
        } else {
            this.proxyStartRow = -1;
            this.proxyEndRow = -1;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.proxyAddRow = i4;
        this.rowCount = i5 + 1;
        this.proxyDetailRow = i5;
        if (SharedConfig.currentProxy == null || SharedConfig.currentProxy.secret.isEmpty()) {
            if (this.callsRow == -1) {
                change = true;
            }
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.callsRow = i6;
            this.rowCount = i7 + 1;
            this.callsDetailRow = i7;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeInserted(this.proxyDetailRow + 1, 2);
            }
        } else {
            if (this.callsRow != -1) {
                change = true;
            }
            this.callsRow = -1;
            this.callsDetailRow = -1;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeRemoved(this.proxyDetailRow + 1, 2);
            }
        }
        checkProxyList();
        if (notify && (listAdapter = this.listAdapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void checkProxyList() {
        int count = SharedConfig.proxyList.size();
        for (int a = 0; a < count; a++) {
            final SharedConfig.ProxyInfo proxyInfo = SharedConfig.proxyList.get(a);
            if (!proxyInfo.checking && SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime >= 120000) {
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(this.currentAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, new RequestTimeDelegate() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda2
                    @Override // org.telegram.tgnet.RequestTimeDelegate
                    public final void run(long j) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                ProxyListActivity.lambda$checkProxyList$3(SharedConfig.ProxyInfo.this, j);
                            }
                        });
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$checkProxyList$3(SharedConfig.ProxyInfo proxyInfo, long time) {
        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
        proxyInfo.checking = false;
        if (time == -1) {
            proxyInfo.available = false;
            proxyInfo.ping = 0L;
        } else {
            proxyInfo.ping = time;
            proxyInfo.available = true;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, proxyInfo);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView.Holder holder;
        int idx;
        RecyclerListView.Holder holder2;
        if (id == NotificationCenter.proxySettingsChanged) {
            updateRows(true);
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                if (this.listView != null && SharedConfig.currentProxy != null && (idx = SharedConfig.proxyList.indexOf(SharedConfig.currentProxy)) >= 0 && (holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx)) != null) {
                    TextDetailProxyCell cell = (TextDetailProxyCell) holder2.itemView;
                    cell.updateStatus();
                }
            }
        } else if (id == NotificationCenter.proxyCheckDone && this.listView != null) {
            SharedConfig.ProxyInfo proxyInfo = (SharedConfig.ProxyInfo) args[0];
            int idx2 = SharedConfig.proxyList.indexOf(proxyInfo);
            if (idx2 >= 0 && (holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx2)) != null) {
                TextDetailProxyCell cell2 = (TextDetailProxyCell) holder.itemView;
                cell2.updateStatus();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        public static final int PAYLOAD_CHECKED_CHANGED = 0;
        private Context mContext;

        public ListAdapter(Context context) {
            ProxyListActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ProxyListActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == ProxyListActivity.this.proxyDetailRow && ProxyListActivity.this.callsRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == ProxyListActivity.this.proxyAddRow) {
                        textCell.setText(LocaleController.getString("AddProxy", R.string.AddProxy), false);
                        return;
                    }
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ProxyListActivity.this.connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProxyConnections", R.string.ProxyConnections));
                        return;
                    }
                    return;
                case 3:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == ProxyListActivity.this.useProxyRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxySettings", R.string.UseProxySettings), ProxyListActivity.this.useProxySettings, false);
                        return;
                    } else if (position == ProxyListActivity.this.callsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), ProxyListActivity.this.useProxyForCalls, false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ProxyListActivity.this.callsDetailRow) {
                        cell.setText(LocaleController.getString("UseProxyForCallsInfo", R.string.UseProxyForCallsInfo));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    return;
                case 5:
                    TextDetailProxyCell cell2 = (TextDetailProxyCell) holder.itemView;
                    SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(position - ProxyListActivity.this.proxyStartRow);
                    cell2.setProxy(info);
                    if (SharedConfig.currentProxy == info) {
                        z = true;
                    }
                    cell2.setChecked(z);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            if (holder.getItemViewType() == 3 && payloads.contains(0)) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                if (position == ProxyListActivity.this.useProxyRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxySettings);
                    return;
                } else if (position == ProxyListActivity.this.callsRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                    return;
                } else {
                    return;
                }
            }
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == ProxyListActivity.this.useProxyRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxySettings);
                } else if (position == ProxyListActivity.this.callsRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ProxyListActivity.this.useProxyRow || position == ProxyListActivity.this.callsRow || position == ProxyListActivity.this.proxyAddRow || (position >= ProxyListActivity.this.proxyStartRow && position < ProxyListActivity.this.proxyEndRow);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 2:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 3:
                    View view4 = new TextCheckCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    View view5 = new TextDetailProxyCell(this.mContext);
                    view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != ProxyListActivity.this.useProxyDetailRow && position != ProxyListActivity.this.proxyDetailRow) {
                if (position != ProxyListActivity.this.proxyAddRow) {
                    if (position != ProxyListActivity.this.useProxyRow && position != ProxyListActivity.this.callsRow) {
                        if (position != ProxyListActivity.this.connectionsHeaderRow) {
                            if (position >= ProxyListActivity.this.proxyStartRow && position < ProxyListActivity.this.proxyEndRow) {
                                return 5;
                            }
                            return 4;
                        }
                        return 2;
                    }
                    return 3;
                }
                return 1;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText6));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return themeDescriptions;
    }
}
