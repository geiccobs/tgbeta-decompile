package org.telegram.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatsWidgetProvider;
import org.telegram.messenger.ContactsWidgetProvider;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.EditWidgetActivity;
/* loaded from: classes4.dex */
public class EditWidgetActivity extends BaseFragment {
    public static final int TYPE_CHATS = 0;
    public static final int TYPE_CONTACTS = 1;
    private static final int done_item = 1;
    private int chatsEndRow;
    private int chatsStartRow;
    private int currentWidgetId;
    private EditWidgetActivityDelegate delegate;
    private int infoRow;
    private ItemTouchHelper itemTouchHelper;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ImageView previewImageView;
    private int previewRow;
    private int rowCount;
    private int selectChatsRow;
    private ArrayList<Long> selectedDialogs = new ArrayList<>();
    private FrameLayout widgetPreview;
    private WidgetPreviewCell widgetPreviewCell;
    private int widgetType;

    /* loaded from: classes4.dex */
    public interface EditWidgetActivityDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    /* loaded from: classes4.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        private boolean moved;

        public TouchHelperCallback() {
            EditWidgetActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 3) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            boolean z = false;
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            int p1 = source.getAdapterPosition();
            int p2 = target.getAdapterPosition();
            if (EditWidgetActivity.this.listAdapter.swapElements(p1, p2)) {
                ((GroupCreateUserCell) source.itemView).setDrawDivider(p2 != EditWidgetActivity.this.chatsEndRow - 1);
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) target.itemView;
                if (p1 != EditWidgetActivity.this.chatsEndRow - 1) {
                    z = true;
                }
                groupCreateUserCell.setDrawDivider(z);
                this.moved = true;
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                EditWidgetActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else if (this.moved) {
                if (EditWidgetActivity.this.widgetPreviewCell != null) {
                    EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                }
                this.moved = false;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    /* loaded from: classes4.dex */
    public class WidgetPreviewCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        private Drawable oldBackgroundDrawable;
        private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
        private Drawable shadowDrawable;
        private Paint roundPaint = new Paint(1);
        private RectF bitmapRect = new RectF();
        private ViewGroup[] cells = new ViewGroup[2];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public WidgetPreviewCell(Context context) {
            super(context);
            EditWidgetActivity.this = this$0;
            setWillNotDraw(false);
            setPadding(0, AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
            ChatActionCell chatActionCell = new ChatActionCell(context);
            chatActionCell.setCustomText(LocaleController.getString("WidgetPreview", R.string.WidgetPreview));
            linearLayout.addView(chatActionCell, LayoutHelper.createLinear(-2, -2, 17, 0, 0, 0, 4));
            LinearLayout widgetPreview = new LinearLayout(context);
            widgetPreview.setOrientation(1);
            widgetPreview.setBackgroundResource(R.drawable.widget_bg);
            linearLayout.addView(widgetPreview, LayoutHelper.createLinear(-2, -2, 17, 10, 0, 10, 0));
            this$0.previewImageView = new ImageView(context);
            if (this$0.widgetType != 0) {
                if (this$0.widgetType == 1) {
                    for (int a = 0; a < 2; a++) {
                        this.cells[a] = (ViewGroup) this$0.getParentActivity().getLayoutInflater().inflate(R.layout.contacts_widget_item, (ViewGroup) null);
                        widgetPreview.addView(this.cells[a], LayoutHelper.createLinear(160, -2));
                    }
                    widgetPreview.addView(this$0.previewImageView, LayoutHelper.createLinear(160, 160, 17));
                    this$0.previewImageView.setImageResource(R.drawable.contacts_widget_preview);
                }
            } else {
                for (int a2 = 0; a2 < 2; a2++) {
                    this.cells[a2] = (ViewGroup) this$0.getParentActivity().getLayoutInflater().inflate(R.layout.shortcut_widget_item, (ViewGroup) null);
                    widgetPreview.addView(this.cells[a2], LayoutHelper.createLinear(-1, -2));
                }
                widgetPreview.addView(this$0.previewImageView, LayoutHelper.createLinear(218, 160, 17));
                this$0.previewImageView.setImageResource(R.drawable.chats_widget_preview);
            }
            updateDialogs();
            this.shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
        }

        /* JADX WARN: Code restructure failed: missing block: B:293:0x0853, code lost:
            if (r3 != 2) goto L296;
         */
        /* JADX WARN: Removed duplicated region for block: B:246:0x06c0  */
        /* JADX WARN: Removed duplicated region for block: B:250:0x0716  */
        /* JADX WARN: Removed duplicated region for block: B:337:0x0939  */
        /* JADX WARN: Removed duplicated region for block: B:338:0x093d  */
        /* JADX WARN: Removed duplicated region for block: B:348:0x0983  */
        /* JADX WARN: Removed duplicated region for block: B:358:0x09b4 A[Catch: all -> 0x0a28, TRY_ENTER, TRY_LEAVE, TryCatch #5 {all -> 0x0a28, blocks: (B:346:0x096e, B:358:0x09b4), top: B:418:0x096e }] */
        /* JADX WARN: Removed duplicated region for block: B:368:0x0a06  */
        /* JADX WARN: Removed duplicated region for block: B:369:0x0a0a  */
        /* JADX WARN: Removed duplicated region for block: B:383:0x0a37  */
        /* JADX WARN: Removed duplicated region for block: B:396:0x0a8e  */
        /* JADX WARN: Removed duplicated region for block: B:412:0x094c A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r11v0 */
        /* JADX WARN: Type inference failed for: r11v17, types: [int, boolean] */
        /* JADX WARN: Type inference failed for: r11v18 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void updateDialogs() {
            /*
                Method dump skipped, instructions count: 2773
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.updateDialogs():void");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(264.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int a;
            Drawable drawable;
            Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
            if (newDrawable != this.backgroundDrawable && newDrawable != null) {
                if (Theme.isAnimatingColor()) {
                    this.oldBackgroundDrawable = this.backgroundDrawable;
                    this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
                } else {
                    BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                    if (disposable != null) {
                        disposable.dispose();
                        this.backgroundGradientDisposable = null;
                    }
                }
                this.backgroundDrawable = newDrawable;
            }
            float themeAnimationValue = EditWidgetActivity.this.parentLayout.getThemeAnimationValue();
            int a2 = 0;
            while (a2 < 2) {
                Drawable drawable2 = a2 == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
                if (drawable2 == null) {
                    a = a2;
                } else {
                    if (a2 == 1 && this.oldBackgroundDrawable != null && EditWidgetActivity.this.parentLayout != null) {
                        drawable2.setAlpha((int) (255.0f * themeAnimationValue));
                    } else {
                        drawable2.setAlpha(255);
                    }
                    if ((drawable2 instanceof ColorDrawable) || (drawable2 instanceof GradientDrawable)) {
                        a = a2;
                    } else if (drawable2 instanceof MotionBackgroundDrawable) {
                        a = a2;
                    } else {
                        if (!(drawable2 instanceof BitmapDrawable)) {
                            a = a2;
                        } else {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
                            if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                                canvas.save();
                                float scale = 2.0f / AndroidUtilities.density;
                                canvas.scale(scale, scale);
                                drawable2.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getMeasuredHeight() / scale));
                                a = a2;
                            } else {
                                int viewHeight = getMeasuredHeight();
                                float scaleX = getMeasuredWidth() / drawable2.getIntrinsicWidth();
                                float scaleY = viewHeight / drawable2.getIntrinsicHeight();
                                float scale2 = Math.max(scaleX, scaleY);
                                int width = (int) Math.ceil(drawable2.getIntrinsicWidth() * scale2);
                                a = a2;
                                int height = (int) Math.ceil(drawable2.getIntrinsicHeight() * scale2);
                                int x = (getMeasuredWidth() - width) / 2;
                                int y = (viewHeight - height) / 2;
                                canvas.save();
                                canvas.clipRect(0, 0, width, getMeasuredHeight());
                                drawable2.setBounds(x, y, x + width, y + height);
                            }
                            drawable2.draw(canvas);
                            canvas.restore();
                        }
                        if (a != 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                            BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                            if (disposable2 == null) {
                                drawable = null;
                            } else {
                                disposable2.dispose();
                                drawable = null;
                                this.oldBackgroundGradientDisposable = null;
                            }
                            this.oldBackgroundDrawable = drawable;
                            invalidate();
                        }
                    }
                    drawable2.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (drawable2 instanceof BackgroundGradientDrawable) {
                        BackgroundGradientDrawable backgroundGradientDrawable = (BackgroundGradientDrawable) drawable2;
                        this.backgroundGradientDisposable = backgroundGradientDrawable.drawExactBoundsSize(canvas, this);
                    } else {
                        drawable2.draw(canvas);
                    }
                    if (a != 0) {
                    }
                }
                a2 = a + 1;
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
            BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
            if (disposable2 != null) {
                disposable2.dispose();
                this.oldBackgroundGradientDisposable = null;
            }
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchSetPressed(boolean pressed) {
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }
    }

    public EditWidgetActivity(int type, int widgetId) {
        this.widgetType = type;
        this.currentWidgetId = widgetId;
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, users, chats, true);
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        updateRows();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        DialogsActivity.loadDialogs(AccountInstance.getInstance(this.currentAccount));
        getMediaDataController().loadHints(true);
        return super.onFragmentCreate();
    }

    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        this.rowCount = i + 1;
        this.selectChatsRow = i;
        if (this.selectedDialogs.isEmpty()) {
            this.chatsStartRow = -1;
            this.chatsEndRow = -1;
        } else {
            int i2 = this.rowCount;
            this.chatsStartRow = i2;
            int size = i2 + this.selectedDialogs.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.infoRow = i3;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(EditWidgetActivityDelegate editWidgetActivityDelegate) {
        this.delegate = editWidgetActivityDelegate;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.widgetType == 0) {
            this.actionBar.setTitle(LocaleController.getString("WidgetChats", R.string.WidgetChats));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WidgetShortcuts", R.string.WidgetShortcuts));
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(1, LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.EditWidgetActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (EditWidgetActivity.this.delegate == null) {
                        EditWidgetActivity.this.finishActivity();
                    } else {
                        EditWidgetActivity.this.finishFragment();
                    }
                } else if (id == 1 && EditWidgetActivity.this.getParentActivity() != null) {
                    EditWidgetActivity.this.getMessagesStorage().putWidgetDialogs(EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.selectedDialogs);
                    SharedPreferences preferences = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount);
                    editor.putInt(CommonProperties.TYPE + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType);
                    editor.commit();
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), appWidgetManager, EditWidgetActivity.this.currentWidgetId);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), appWidgetManager, EditWidgetActivity.this.currentWidgetId);
                    }
                    if (EditWidgetActivity.this.delegate != null) {
                        EditWidgetActivity.this.delegate.didSelectDialogs(EditWidgetActivity.this.selectedDialogs);
                    } else {
                        EditWidgetActivity.this.finishActivity();
                    }
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.listView);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.EditWidgetActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                EditWidgetActivity.this.m3402lambda$createView$1$orgtelegramuiEditWidgetActivity(context, view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new AnonymousClass2());
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-EditWidgetActivity */
    public /* synthetic */ void m3402lambda$createView$1$orgtelegramuiEditWidgetActivity(Context context, View view, int position) {
        if (position == this.selectChatsRow) {
            InviteMembersBottomSheet bottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, null, 0L, this, null);
            bottomSheet.setDelegate(new InviteMembersBottomSheet.InviteMembersBottomSheetDelegate() { // from class: org.telegram.ui.EditWidgetActivity$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.InviteMembersBottomSheet.InviteMembersBottomSheetDelegate
                public final void didSelectDialogs(ArrayList arrayList) {
                    EditWidgetActivity.this.m3401lambda$createView$0$orgtelegramuiEditWidgetActivity(arrayList);
                }
            }, this.selectedDialogs);
            bottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(bottomSheet);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-EditWidgetActivity */
    public /* synthetic */ void m3401lambda$createView$0$orgtelegramuiEditWidgetActivity(ArrayList dids) {
        this.selectedDialogs.clear();
        this.selectedDialogs.addAll(dids);
        updateRows();
        WidgetPreviewCell widgetPreviewCell = this.widgetPreviewCell;
        if (widgetPreviewCell != null) {
            widgetPreviewCell.updateDialogs();
        }
    }

    /* renamed from: org.telegram.ui.EditWidgetActivity$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 implements RecyclerListView.OnItemLongClickListenerExtended {
        private Rect rect = new Rect();

        AnonymousClass2() {
            EditWidgetActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
        public boolean onItemClick(View view, final int position, float x, float y) {
            if (EditWidgetActivity.this.getParentActivity() == null || !(view instanceof GroupCreateUserCell)) {
                return false;
            }
            ImageView imageView = (ImageView) view.getTag(R.id.object_tag);
            imageView.getHitRect(this.rect);
            if (this.rect.contains((int) x, (int) y)) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(EditWidgetActivity.this.getParentActivity());
            CharSequence[] items = {LocaleController.getString("Delete", R.string.Delete)};
            builder.setItems(items, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.EditWidgetActivity$2$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    EditWidgetActivity.AnonymousClass2.this.m3403lambda$onItemClick$0$orgtelegramuiEditWidgetActivity$2(position, dialogInterface, i);
                }
            });
            EditWidgetActivity.this.showDialog(builder.create());
            return true;
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-EditWidgetActivity$2 */
        public /* synthetic */ void m3403lambda$onItemClick$0$orgtelegramuiEditWidgetActivity$2(int position, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                EditWidgetActivity.this.selectedDialogs.remove(position - EditWidgetActivity.this.chatsStartRow);
                EditWidgetActivity.this.updateRows();
                if (EditWidgetActivity.this.widgetPreviewCell != null) {
                    EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
        public void onMove(float dx, float dy) {
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
        public void onLongClickRelease() {
        }
    }

    public void finishActivity() {
        if (getParentActivity() == null) {
            return;
        }
        getParentActivity().finish();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.EditWidgetActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EditWidgetActivity.this.removeSelfFromStack();
            }
        }, 1000L);
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            EditWidgetActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return EditWidgetActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 1 || type == 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GroupCreateUserCell cell;
            switch (viewType) {
                case 0:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    cell = textInfoPrivacyCell;
                    break;
                case 1:
                    TextCell textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell = textCell;
                    break;
                case 2:
                    cell = EditWidgetActivity.this.widgetPreviewCell = new WidgetPreviewCell(this.mContext);
                    break;
                default:
                    final GroupCreateUserCell cell2 = new GroupCreateUserCell(this.mContext, 0, 0, false);
                    ImageView sortImageView = new ImageView(this.mContext);
                    sortImageView.setImageResource(R.drawable.list_reorder);
                    sortImageView.setScaleType(ImageView.ScaleType.CENTER);
                    cell2.setTag(R.id.object_tag, sortImageView);
                    cell2.addView(sortImageView, LayoutHelper.createFrame(40, -1.0f, (LocaleController.isRTL ? 3 : 5) | 16, 10.0f, 0.0f, 10.0f, 0.0f));
                    sortImageView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            return EditWidgetActivity.ListAdapter.this.m3404x37efab4f(cell2, view, motionEvent);
                        }
                    });
                    sortImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_pinnedIcon), PorterDuff.Mode.MULTIPLY));
                    cell = cell2;
                    break;
            }
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-EditWidgetActivity$ListAdapter */
        public /* synthetic */ boolean m3404x37efab4f(GroupCreateUserCell cell, View v, MotionEvent event) {
            if (event.getAction() == 0) {
                EditWidgetActivity.this.itemTouchHelper.startDrag(EditWidgetActivity.this.listView.getChildViewHolder(cell));
                return false;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == EditWidgetActivity.this.infoRow) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        if (EditWidgetActivity.this.widgetType != 0) {
                            if (EditWidgetActivity.this.widgetType == 1) {
                                builder.append((CharSequence) LocaleController.getString("EditWidgetContactsInfo", R.string.EditWidgetContactsInfo));
                            }
                        } else {
                            builder.append((CharSequence) LocaleController.getString("EditWidgetChatsInfo", R.string.EditWidgetChatsInfo));
                        }
                        if (SharedConfig.passcodeHash.length() > 0) {
                            builder.append((CharSequence) "\n\n").append((CharSequence) AndroidUtilities.replaceTags(LocaleController.getString("WidgetPasscode2", R.string.WidgetPasscode2)));
                        }
                        cell.setText(builder);
                        return;
                    }
                    return;
                case 1:
                    TextCell cell2 = (TextCell) holder.itemView;
                    cell2.setColors(null, Theme.key_windowBackgroundWhiteBlueText4);
                    Drawable drawable1 = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                    String string = LocaleController.getString("SelectChats", R.string.SelectChats);
                    if (EditWidgetActivity.this.chatsStartRow == -1) {
                        z = false;
                    }
                    cell2.setTextAndIcon(string, combinedDrawable, z);
                    cell2.getImageView().setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
                    return;
                case 2:
                default:
                    return;
                case 3:
                    GroupCreateUserCell cell3 = (GroupCreateUserCell) holder.itemView;
                    long did = ((Long) EditWidgetActivity.this.selectedDialogs.get(position - EditWidgetActivity.this.chatsStartRow)).longValue();
                    if (DialogObject.isUserDialog(did)) {
                        TLRPC.User user = EditWidgetActivity.this.getMessagesController().getUser(Long.valueOf(did));
                        if (position == EditWidgetActivity.this.chatsEndRow - 1) {
                            z = false;
                        }
                        cell3.setObject(user, null, null, z);
                        return;
                    }
                    TLRPC.Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Long.valueOf(-did));
                    if (position == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    cell3.setObject(chat, null, null, z);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 3 || type == 1) {
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != EditWidgetActivity.this.previewRow) {
                if (position != EditWidgetActivity.this.selectChatsRow) {
                    if (position == EditWidgetActivity.this.infoRow) {
                        return 0;
                    }
                    return 3;
                }
                return 1;
            }
            return 2;
        }

        public boolean swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - EditWidgetActivity.this.chatsStartRow;
            int idx2 = toIndex - EditWidgetActivity.this.chatsStartRow;
            int count = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < count && idx2 < count) {
                Long did1 = (Long) EditWidgetActivity.this.selectedDialogs.get(idx1);
                Long did2 = (Long) EditWidgetActivity.this.selectedDialogs.get(idx2);
                EditWidgetActivity.this.selectedDialogs.set(idx1, did2);
                EditWidgetActivity.this.selectedDialogs.set(idx2, did1);
                notifyItemMoved(fromIndex, toIndex);
                return true;
            }
            return false;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.delegate == null) {
            finishActivity();
            return false;
        }
        return super.onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        return themeDescriptions;
    }
}
