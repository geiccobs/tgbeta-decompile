package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ChatAttachAlertPollLayout extends ChatAttachAlert.AttachAlertLayout {
    public static final int MAX_ANSWER_LENGTH = 100;
    public static final int MAX_QUESTION_LENGTH = 255;
    public static final int MAX_SOLUTION_LENGTH = 200;
    private static final int done_button = 40;
    private int addAnswerRow;
    private boolean allowNesterScroll;
    private int anonymousRow;
    private int answerHeaderRow;
    private int answerSectionRow;
    private int answerStartRow;
    private PollCreateActivityDelegate delegate;
    private int emptyRow;
    private boolean hintShowed;
    private HintView hintView;
    private boolean ignoreLayout;
    private SimpleItemAnimator itemAnimator;
    private FillLastLinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean multipleChoise;
    private int multipleRow;
    private int paddingRow;
    private int questionHeaderRow;
    private int questionRow;
    private int questionSectionRow;
    private String questionString;
    private int quizOnly;
    private boolean quizPoll;
    private int quizRow;
    private int rowCount;
    private int settingsHeaderRow;
    private int settingsSectionRow;
    private int solutionInfoRow;
    private int solutionRow;
    private CharSequence solutionString;
    private int topPadding;
    private String[] answers = new String[10];
    private boolean[] answersChecks = new boolean[10];
    private int answersCount = 1;
    private boolean anonymousPoll = true;
    private int requestFieldFocusAtPosition = -1;

    /* loaded from: classes5.dex */
    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
    }

    static /* synthetic */ int access$1210(ChatAttachAlertPollLayout x0) {
        int i = x0.answersCount;
        x0.answersCount = i - 1;
        return i;
    }

    /* loaded from: classes5.dex */
    private static class EmptyView extends View {
        public EmptyView(Context context) {
            super(context);
        }
    }

    /* loaded from: classes5.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
            ChatAttachAlertPollLayout.this = this$0;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() == target.getItemViewType()) {
                ChatAttachAlertPollLayout.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                ChatAttachAlertPollLayout.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_dialogBackground));
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
            viewHolder.itemView.setBackground(null);
        }
    }

    public ChatAttachAlertPollLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        updateRows();
        this.listAdapter = new ListAdapter(context);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.1
            @Override // androidx.recyclerview.widget.RecyclerView
            public void requestChildOnScreen(View child, View focused) {
                if (!(child instanceof PollEditTextCell)) {
                    return;
                }
                super.requestChildOnScreen(child, focused);
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View child, android.graphics.Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.listView = recyclerListView;
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.2
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
                if (holder.getAdapterPosition() == 0) {
                    ChatAttachAlertPollLayout.this.parentAlert.updateLayout(ChatAttachAlertPollLayout.this, true, 0);
                }
            }
        };
        this.itemAnimator = defaultItemAnimator;
        recyclerListView.setItemAnimator(defaultItemAnimator);
        this.listView.setClipToPadding(false);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(53.0f), this.listView) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.3
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.3.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (ChatAttachAlertPollLayout.this.topPadding - AndroidUtilities.dp(7.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
            protected int[] getChildRectangleOnScreenScrollAmount(View child, android.graphics.Rect rect) {
                int[] out = new int[2];
                int parentBottom = getHeight() - getPaddingBottom();
                int childTop = (child.getTop() + rect.top) - child.getScrollY();
                int childBottom = rect.height() + childTop;
                int offScreenTop = Math.min(0, childTop + 0);
                int offScreenBottom = Math.max(0, childBottom - parentBottom);
                int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop + 0, offScreenBottom);
                out[0] = 0;
                out[1] = dy;
                return out;
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView2.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setSkipFirstItem();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(this.listView);
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setPreserveFocusAfterLayout(true);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertPollLayout.this.m2506x3180e5dc(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertPollLayout.this.parentAlert.updateLayout(ChatAttachAlertPollLayout.this, true, dy);
                if (dy != 0 && ChatAttachAlertPollLayout.this.hintView != null) {
                    ChatAttachAlertPollLayout.this.hintView.hide();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertPollLayout.this.parentAlert.getBackgroundPaddingTop();
                    int top = (ChatAttachAlertPollLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset;
                    if (top + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(1)) != null && holder.itemView.getTop() > AndroidUtilities.dp(53.0f)) {
                        ChatAttachAlertPollLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(53.0f));
                    }
                }
            }
        });
        HintView hintView = new HintView(context, 4);
        this.hintView = hintView;
        hintView.setText(LocaleController.getString("PollTapToSelect", R.string.PollTapToSelect));
        this.hintView.setAlpha(0.0f);
        this.hintView.setVisibility(4);
        addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        checkDoneButton();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertPollLayout */
    public /* synthetic */ void m2506x3180e5dc(View view, int position) {
        boolean checked;
        if (position == this.addAnswerRow) {
            addNewField();
        } else if (view instanceof TextCheckCell) {
            TextCheckCell cell = (TextCheckCell) view;
            boolean wasChecksBefore = this.quizPoll;
            if (position == this.anonymousRow) {
                checked = !this.anonymousPoll;
                this.anonymousPoll = checked;
            } else if (position == this.multipleRow) {
                boolean checked2 = !this.multipleChoise;
                this.multipleChoise = checked2;
                if (checked2 && this.quizPoll) {
                    int prevSolutionRow = this.solutionRow;
                    this.quizPoll = false;
                    updateRows();
                    this.listView.setItemAnimator(this.itemAnimator);
                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                    if (holder != null) {
                        ((TextCheckCell) holder.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.quizRow);
                    }
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow, 2);
                    this.listAdapter.notifyItemChanged(this.emptyRow);
                }
                checked = checked2;
            } else if (this.quizOnly != 0) {
                return;
            } else {
                this.listView.setItemAnimator(this.itemAnimator);
                checked = !this.quizPoll;
                this.quizPoll = checked;
                int prevSolutionRow2 = this.solutionRow;
                updateRows();
                if (this.quizPoll) {
                    this.listAdapter.notifyItemRangeInserted(this.solutionRow, 2);
                } else {
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow2, 2);
                }
                this.listAdapter.notifyItemChanged(this.emptyRow);
                if (this.quizPoll && this.multipleChoise) {
                    this.multipleChoise = false;
                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.multipleRow);
                    if (holder2 != null) {
                        ((TextCheckCell) holder2.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.multipleRow);
                    }
                }
                if (this.quizPoll) {
                    boolean was = false;
                    int a = 0;
                    while (true) {
                        boolean[] zArr = this.answersChecks;
                        if (a >= zArr.length) {
                            break;
                        }
                        if (was) {
                            zArr[a] = false;
                        } else if (zArr[a]) {
                            was = true;
                        }
                        a++;
                    }
                }
            }
            boolean was2 = this.hintShowed;
            if (was2 && !this.quizPoll) {
                this.hintView.hide();
            }
            this.listView.getChildCount();
            for (int a2 = this.answerStartRow; a2 < this.answerStartRow + this.answersCount; a2++) {
                RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(a2);
                if (holder3 != null && (holder3.itemView instanceof PollEditTextCell)) {
                    PollEditTextCell pollEditTextCell = (PollEditTextCell) holder3.itemView;
                    pollEditTextCell.setShowCheckBox(this.quizPoll, true);
                    pollEditTextCell.setChecked(this.answersChecks[a2 - this.answerStartRow], wasChecksBefore);
                    if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f) && position == this.quizRow && !this.hintShowed) {
                        this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                        this.hintShowed = true;
                    }
                }
            }
            cell.setChecked(checked);
            checkDoneButton();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onResume() {
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onHideShowProgress(float progress) {
        this.parentAlert.doneItem.setAlpha((this.parentAlert.doneItem.isEnabled() ? 1.0f : 0.5f) * progress);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onMenuItemClick(int id) {
        if (id == 40) {
            if (this.quizPoll && this.parentAlert.doneItem.getAlpha() != 1.0f) {
                int checksCount = 0;
                for (int a = 0; a < this.answersChecks.length; a++) {
                    if (!TextUtils.isEmpty(getFixedString(this.answers[a])) && this.answersChecks[a]) {
                        checksCount++;
                    }
                }
                if (checksCount <= 0) {
                    showQuizHint();
                    return;
                }
                return;
            }
            final TLRPC.TL_messageMediaPoll poll = new TLRPC.TL_messageMediaPoll();
            poll.poll = new TLRPC.TL_poll();
            poll.poll.multiple_choice = this.multipleChoise;
            poll.poll.quiz = this.quizPoll;
            poll.poll.public_voters = !this.anonymousPoll;
            poll.poll.question = getFixedString(this.questionString).toString();
            SerializedData serializedData = new SerializedData(10);
            int a2 = 0;
            while (true) {
                String[] strArr = this.answers;
                if (a2 >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(getFixedString(strArr[a2]))) {
                    TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
                    answer.text = getFixedString(this.answers[a2]).toString();
                    answer.option = new byte[1];
                    answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                    poll.poll.answers.add(answer);
                    if ((this.multipleChoise || this.quizPoll) && this.answersChecks[a2]) {
                        serializedData.writeByte(answer.option[0]);
                    }
                }
                a2++;
            }
            final HashMap<String, String> params = new HashMap<>();
            params.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
            poll.results = new TLRPC.TL_pollResults();
            CharSequence solution = getFixedString(this.solutionString);
            if (solution != null) {
                poll.results.solution = solution.toString();
                CharSequence[] message = {solution};
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.parentAlert.currentAccount).getEntities(message, true);
                if (entities != null && !entities.isEmpty()) {
                    poll.results.solution_entities = entities;
                }
                if (!TextUtils.isEmpty(poll.results.solution)) {
                    poll.results.flags |= 16;
                }
            }
            ChatActivity chatActivity = (ChatActivity) this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(chatActivity.getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertPollLayout.this.m2507x54f80cc4(poll, params, z, i);
                    }
                });
                return;
            }
            this.delegate.sendPoll(poll, params, true, 0);
            this.parentAlert.dismiss(true);
        }
    }

    /* renamed from: lambda$onMenuItemClick$1$org-telegram-ui-Components-ChatAttachAlertPollLayout */
    public /* synthetic */ void m2507x54f80cc4(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
        this.delegate.sendPoll(poll, params, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        View child;
        if (this.listView.getChildCount() > 1 && (child = this.listView.getChildAt(1)) != null) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            int top = ((int) child.getY()) - AndroidUtilities.dp(8.0f);
            int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 1) ? 0 : top;
            if (top >= 0 && holder != null && holder.getAdapterPosition() == 1) {
                newOffset = top;
            }
            return AndroidUtilities.dp(25.0f) + newOffset;
        }
        return Integer.MAX_VALUE;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(17.0f);
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.topPadding;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(52.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                padding2 = (int) (availableHeight / 3.5f);
            } else {
                padding2 = (availableHeight / 5) * 2;
            }
            padding = padding2 - AndroidUtilities.dp(13.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(this.allowNesterScroll);
        }
        this.ignoreLayout = true;
        if (this.topPadding != padding) {
            this.topPadding = padding;
            this.listView.setItemAnimator(null);
            this.listAdapter.notifyItemChanged(this.paddingRow);
        }
        this.ignoreLayout = false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getButtonsHideOffset() {
        return AndroidUtilities.dp(70.0f);
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
        this.listView.smoothScrollToPosition(1);
    }

    public static CharSequence getFixedString(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        while (TextUtils.indexOf(text2, "\n\n\n") >= 0) {
            text2 = TextUtils.replace(text2, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        while (TextUtils.indexOf(text2, "\n\n\n") == 0) {
            text2 = TextUtils.replace(text2, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        return text2;
    }

    private void showQuizHint() {
        this.listView.getChildCount();
        for (int a = this.answerStartRow; a < this.answerStartRow + this.answersCount; a++) {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(a);
            if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) holder.itemView;
                if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f)) {
                    this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                    return;
                }
            }
        }
    }

    public void checkDoneButton() {
        boolean enabled = true;
        int checksCount = 0;
        if (this.quizPoll) {
            for (int a = 0; a < this.answersChecks.length; a++) {
                if (!TextUtils.isEmpty(getFixedString(this.answers[a])) && this.answersChecks[a]) {
                    checksCount++;
                }
            }
        }
        int count = 0;
        if (!TextUtils.isEmpty(getFixedString(this.solutionString)) && this.solutionString.length() > 200) {
            enabled = false;
        } else if (TextUtils.isEmpty(getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        }
        boolean hasAnswers = false;
        int a2 = 0;
        while (true) {
            String[] strArr = this.answers;
            if (a2 >= strArr.length) {
                break;
            }
            if (!TextUtils.isEmpty(getFixedString(strArr[a2]))) {
                hasAnswers = true;
                if (this.answers[a2].length() > 100) {
                    count = 0;
                    break;
                }
                count++;
            }
            a2++;
        }
        boolean z = true;
        if (count < 2 || (this.quizPoll && checksCount < 1)) {
            enabled = false;
        }
        if (!TextUtils.isEmpty(this.solutionString) || !TextUtils.isEmpty(this.questionString) || hasAnswers) {
            this.allowNesterScroll = false;
        } else {
            this.allowNesterScroll = true;
        }
        this.parentAlert.setAllowNestedScroll(this.allowNesterScroll);
        ActionBarMenuItem actionBarMenuItem = this.parentAlert.doneItem;
        if ((!this.quizPoll || checksCount != 0) && !enabled) {
            z = false;
        }
        actionBarMenuItem.setEnabled(z);
        this.parentAlert.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.paddingRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.questionHeaderRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.questionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.questionSectionRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.answerHeaderRow = i4;
        int i6 = this.answersCount;
        if (i6 != 0) {
            this.answerStartRow = i5;
            this.rowCount = i5 + i6;
        } else {
            this.answerStartRow = -1;
        }
        if (i6 != this.answers.length) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.addAnswerRow = i7;
        } else {
            this.addAnswerRow = -1;
        }
        int i8 = this.rowCount;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.answerSectionRow = i8;
        this.rowCount = i9 + 1;
        this.settingsHeaderRow = i9;
        TLRPC.Chat chat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat();
        if (!ChatObject.isChannel(chat) || chat.megagroup) {
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.anonymousRow = i10;
        } else {
            this.anonymousRow = -1;
        }
        int i11 = this.quizOnly;
        if (i11 != 1) {
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.multipleRow = i12;
        } else {
            this.multipleRow = -1;
        }
        if (i11 == 0) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.quizRow = i13;
        } else {
            this.quizRow = -1;
        }
        int i14 = this.rowCount;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.settingsSectionRow = i14;
        if (this.quizPoll) {
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.solutionRow = i15;
            this.rowCount = i16 + 1;
            this.solutionInfoRow = i16;
        } else {
            this.solutionRow = -1;
            this.solutionInfoRow = -1;
        }
        int i17 = this.rowCount;
        this.rowCount = i17 + 1;
        this.emptyRow = i17;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        if (this.quizOnly == 1) {
            this.parentAlert.actionBar.setTitle(LocaleController.getString("NewQuiz", R.string.NewQuiz));
        } else {
            this.parentAlert.actionBar.setTitle(LocaleController.getString("NewPoll", R.string.NewPoll));
        }
        this.parentAlert.doneItem.setVisibility(0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHidden() {
        this.parentAlert.doneItem.setVisibility(4);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return true;
        }
        return super.onBackPressed();
    }

    private boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(getFixedString(this.questionString));
        if (allowDiscard) {
            for (int a = 0; a < this.answersCount && (allowDiscard = TextUtils.isEmpty(getFixedString(this.answers[a]))); a++) {
            }
        }
        if (!allowDiscard) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.parentAlert.baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", R.string.CancelPollAlertTitle));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", R.string.CancelPollAlertText));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlertPollLayout.this.m2505x9c69802a(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.show();
        }
        return allowDiscard;
    }

    /* renamed from: lambda$checkDiscard$2$org-telegram-ui-Components-ChatAttachAlertPollLayout */
    public /* synthetic */ void m2505x9c69802a(DialogInterface dialogInterface, int i) {
        this.parentAlert.dismiss();
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    public void setTextLeft(View cell, int index) {
        int left;
        int max;
        if (!(cell instanceof PollEditTextCell)) {
            return;
        }
        PollEditTextCell textCell = (PollEditTextCell) cell;
        if (index == this.questionRow) {
            max = 255;
            String str = this.questionString;
            left = 255 - (str != null ? str.length() : 0);
        } else if (index == this.solutionRow) {
            max = 200;
            CharSequence charSequence = this.solutionString;
            left = 200 - (charSequence != null ? charSequence.length() : 0);
        } else {
            int max2 = this.answerStartRow;
            if (index >= max2 && index < this.answersCount + max2) {
                int index2 = index - max2;
                max = 100;
                String[] strArr = this.answers;
                left = 100 - (strArr[index2] != null ? strArr[index2].length() : 0);
            } else {
                return;
            }
        }
        if (left <= max - (max * 0.7f)) {
            textCell.setText2(String.format("%d", Integer.valueOf(left)));
            SimpleTextView textView = textCell.getTextView2();
            String key = left < 0 ? Theme.key_windowBackgroundWhiteRedText5 : Theme.key_windowBackgroundWhiteGrayText3;
            textView.setTextColor(getThemedColor(key));
            textView.setTag(key);
            return;
        }
        textCell.setText2("");
    }

    public void addNewField() {
        this.listView.setItemAnimator(this.itemAnimator);
        boolean[] zArr = this.answersChecks;
        int i = this.answersCount;
        zArr[i] = false;
        int i2 = i + 1;
        this.answersCount = i2;
        if (i2 == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
        this.listAdapter.notifyItemChanged(this.emptyRow);
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ChatAttachAlertPollLayout.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ChatAttachAlertPollLayout.this.rowCount;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == ChatAttachAlertPollLayout.this.questionHeaderRow) {
                        cell.getTextView().setGravity(19);
                        cell.setText(LocaleController.getString("PollQuestion", R.string.PollQuestion));
                        return;
                    }
                    cell.getTextView().setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    if (position == ChatAttachAlertPollLayout.this.answerHeaderRow) {
                        if (ChatAttachAlertPollLayout.this.quizOnly == 1) {
                            cell.setText(LocaleController.getString("QuizAnswers", R.string.QuizAnswers));
                            return;
                        } else {
                            cell.setText(LocaleController.getString("AnswerOptions", R.string.AnswerOptions));
                            return;
                        }
                    } else if (position == ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                        cell.setText(LocaleController.getString("Settings", R.string.Settings));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    cell2.setBackgroundDrawable(combinedDrawable);
                    if (position != ChatAttachAlertPollLayout.this.solutionInfoRow) {
                        if (position == ChatAttachAlertPollLayout.this.settingsSectionRow) {
                            if (ChatAttachAlertPollLayout.this.quizOnly != 0) {
                                cell2.setText(null);
                                return;
                            } else {
                                cell2.setText(LocaleController.getString("QuizInfo", R.string.QuizInfo));
                                return;
                            }
                        } else if (10 - ChatAttachAlertPollLayout.this.answersCount <= 0) {
                            cell2.setText(LocaleController.getString("AddAnOptionInfoMax", R.string.AddAnOptionInfoMax));
                            return;
                        } else {
                            cell2.setText(LocaleController.formatString("AddAnOptionInfo", R.string.AddAnOptionInfo, LocaleController.formatPluralString("Option", 10 - ChatAttachAlertPollLayout.this.answersCount, new Object[0])));
                            return;
                        }
                    }
                    cell2.setText(LocaleController.getString("AddAnExplanationInfo", R.string.AddAnExplanationInfo));
                    return;
                case 3:
                    View view = holder.itemView;
                    TextCell textCell = (TextCell) view;
                    textCell.setColors(null, Theme.key_windowBackgroundWhiteBlueText4);
                    Drawable drawable1 = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                    drawable1.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", R.string.AddAnOption), (Drawable) new CombinedDrawable(drawable1, drawable2), false);
                    return;
                case 6:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position != ChatAttachAlertPollLayout.this.anonymousRow) {
                        if (position != ChatAttachAlertPollLayout.this.multipleRow) {
                            if (position == ChatAttachAlertPollLayout.this.quizRow) {
                                checkCell.setTextAndCheck(LocaleController.getString("PollQuiz", R.string.PollQuiz), ChatAttachAlertPollLayout.this.quizPoll, false);
                                if (ChatAttachAlertPollLayout.this.quizOnly == 0) {
                                    z = true;
                                }
                                checkCell.setEnabled(z, null);
                                break;
                            }
                        } else {
                            String string = LocaleController.getString("PollMultiple", R.string.PollMultiple);
                            boolean z2 = ChatAttachAlertPollLayout.this.multipleChoise;
                            if (ChatAttachAlertPollLayout.this.quizRow != -1) {
                                z = true;
                            }
                            checkCell.setTextAndCheck(string, z2, z);
                            checkCell.setEnabled(true, null);
                            break;
                        }
                    } else {
                        String string2 = LocaleController.getString("PollAnonymous", R.string.PollAnonymous);
                        boolean z3 = ChatAttachAlertPollLayout.this.anonymousPoll;
                        if (ChatAttachAlertPollLayout.this.multipleRow != -1 || ChatAttachAlertPollLayout.this.quizRow != -1) {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string2, z3, z);
                        checkCell.setEnabled(true, null);
                        break;
                    }
                    break;
                case 9:
                    break;
                default:
                    return;
            }
            View view2 = holder.itemView;
            view2.requestLayout();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            CharSequence charSequence = "";
            if (viewType == 4) {
                PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                textCell.setTag(1);
                if (ChatAttachAlertPollLayout.this.questionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.questionString;
                }
                textCell.setTextAndHint(charSequence, LocaleController.getString("QuestionHint", R.string.QuestionHint), false);
                textCell.setTag(null);
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            } else if (viewType == 5) {
                int position = holder.getAdapterPosition();
                PollEditTextCell textCell2 = (PollEditTextCell) holder.itemView;
                textCell2.setTag(1);
                int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                textCell2.setTextAndHint(ChatAttachAlertPollLayout.this.answers[index], LocaleController.getString("OptionHint", R.string.OptionHint), true);
                textCell2.setTag(null);
                if (ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition == position) {
                    EditTextBoldCursor editText = textCell2.getTextView();
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition = -1;
                }
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, position);
            } else if (viewType == 7) {
                PollEditTextCell textCell3 = (PollEditTextCell) holder.itemView;
                textCell3.setTag(1);
                if (ChatAttachAlertPollLayout.this.solutionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.solutionString;
                }
                textCell3.setTextAndHint(charSequence, LocaleController.getString("AddAnExplanation", R.string.AddAnExplanation), false);
                textCell3.setTag(null);
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 4) {
                PollEditTextCell editTextCell = (PollEditTextCell) holder.itemView;
                EditTextBoldCursor editText = editTextCell.getTextView();
                if (editText.isFocused()) {
                    editText.clearFocus();
                    AndroidUtilities.hideKeyboard(editText);
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ChatAttachAlertPollLayout.this.addAnswerRow || position == ChatAttachAlertPollLayout.this.anonymousRow || position == ChatAttachAlertPollLayout.this.multipleRow || (ChatAttachAlertPollLayout.this.quizOnly == 0 && position == ChatAttachAlertPollLayout.this.quizRow);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false);
                    break;
                case 1:
                    View view2 = new ShadowSectionCell(this.mContext);
                    Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view2.setBackgroundDrawable(combinedDrawable);
                    view = view2;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 3:
                    view = new TextCell(this.mContext);
                    break;
                case 4:
                    final PollEditTextCell cell = new PollEditTextCell(this.mContext, null) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.1
                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected void onFieldTouchUp(EditTextBoldCursor editText) {
                            ChatAttachAlertPollLayout.this.parentAlert.makeFocusable(editText, true);
                        }
                    };
                    cell.createErrorTextView();
                    cell.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.2
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (cell.getTag() != null) {
                                return;
                            }
                            ChatAttachAlertPollLayout.this.questionString = s.toString();
                            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(ChatAttachAlertPollLayout.this.questionRow);
                            if (holder != null) {
                                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, ChatAttachAlertPollLayout.this.questionRow);
                            }
                            ChatAttachAlertPollLayout.this.checkDoneButton();
                        }
                    });
                    view = cell;
                    break;
                case 5:
                default:
                    final PollEditTextCell cell2 = new PollEditTextCell(this.mContext, new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            ChatAttachAlertPollLayout.ListAdapter.this.m2508xd77380a3(view3);
                        }
                    }) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.6
                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean drawDivider() {
                            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(this);
                            if (holder != null) {
                                int position = holder.getAdapterPosition();
                                if (ChatAttachAlertPollLayout.this.answersCount == 10 && position == (ChatAttachAlertPollLayout.this.answerStartRow + ChatAttachAlertPollLayout.this.answersCount) - 1) {
                                    return false;
                                }
                            }
                            return true;
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean shouldShowCheckBox() {
                            return ChatAttachAlertPollLayout.this.quizPoll;
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected void onFieldTouchUp(EditTextBoldCursor editText) {
                            ChatAttachAlertPollLayout.this.parentAlert.makeFocusable(editText, true);
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        public void onCheckBoxClick(PollEditTextCell editText, boolean checked) {
                            int position;
                            if (checked && ChatAttachAlertPollLayout.this.quizPoll) {
                                Arrays.fill(ChatAttachAlertPollLayout.this.answersChecks, false);
                                ChatAttachAlertPollLayout.this.listView.getChildCount();
                                for (int a = ChatAttachAlertPollLayout.this.answerStartRow; a < ChatAttachAlertPollLayout.this.answerStartRow + ChatAttachAlertPollLayout.this.answersCount; a++) {
                                    RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(a);
                                    if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                                        PollEditTextCell pollEditTextCell = (PollEditTextCell) holder.itemView;
                                        pollEditTextCell.setChecked(false, true);
                                    }
                                }
                            }
                            super.onCheckBoxClick(editText, checked);
                            RecyclerView.ViewHolder holder2 = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(editText);
                            if (holder2 != null && (position = holder2.getAdapterPosition()) != -1) {
                                int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                                ChatAttachAlertPollLayout.this.answersChecks[index] = checked;
                            }
                            ChatAttachAlertPollLayout.this.checkDoneButton();
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean isChecked(PollEditTextCell editText) {
                            int position;
                            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(editText);
                            if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                                int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                                return ChatAttachAlertPollLayout.this.answersChecks[index];
                            }
                            return false;
                        }
                    };
                    cell2.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.7
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            int position;
                            int index;
                            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(cell2);
                            if (holder != null && (index = (position = holder.getAdapterPosition()) - ChatAttachAlertPollLayout.this.answerStartRow) >= 0 && index < ChatAttachAlertPollLayout.this.answers.length) {
                                ChatAttachAlertPollLayout.this.answers[index] = s.toString();
                                ChatAttachAlertPollLayout.this.setTextLeft(cell2, position);
                                ChatAttachAlertPollLayout.this.checkDoneButton();
                            }
                        }
                    });
                    cell2.setShowNextButton(true);
                    EditTextBoldCursor editText = cell2.getTextView();
                    editText.setImeOptions(editText.getImeOptions() | 5);
                    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda2
                        @Override // android.widget.TextView.OnEditorActionListener
                        public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            return ChatAttachAlertPollLayout.ListAdapter.this.m2509x41a308c2(cell2, textView, i, keyEvent);
                        }
                    });
                    editText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnKeyListener
                        public final boolean onKey(View view3, int i, KeyEvent keyEvent) {
                            return ChatAttachAlertPollLayout.ListAdapter.lambda$onCreateViewHolder$2(PollEditTextCell.this, view3, i, keyEvent);
                        }
                    });
                    view = cell2;
                    break;
                case 6:
                    view = new TextCheckCell(this.mContext);
                    break;
                case 7:
                    final PollEditTextCell cell3 = new PollEditTextCell(this.mContext, true, null) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.3
                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected void onFieldTouchUp(EditTextBoldCursor editText2) {
                            ChatAttachAlertPollLayout.this.parentAlert.makeFocusable(editText2, true);
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected void onActionModeStart(EditTextBoldCursor editText2, ActionMode actionMode) {
                            if (editText2.isFocused() && editText2.hasSelection()) {
                                Menu menu = actionMode.getMenu();
                                if (menu.findItem(16908321) == null) {
                                    return;
                                }
                                ((ChatActivity) ChatAttachAlertPollLayout.this.parentAlert.baseFragment).fillActionModeMenu(menu);
                            }
                        }
                    };
                    cell3.createErrorTextView();
                    cell3.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.4
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (cell3.getTag() == null) {
                                ChatAttachAlertPollLayout.this.solutionString = s;
                                RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(ChatAttachAlertPollLayout.this.solutionRow);
                                if (holder != null) {
                                    ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, ChatAttachAlertPollLayout.this.solutionRow);
                                }
                                ChatAttachAlertPollLayout.this.checkDoneButton();
                            }
                        }
                    });
                    view = cell3;
                    break;
                case 8:
                    View emptyView = new EmptyView(this.mContext);
                    emptyView.setBackgroundColor(ChatAttachAlertPollLayout.this.getThemedColor(Theme.key_windowBackgroundGray));
                    view = emptyView;
                    break;
                case 9:
                    view = new View(this.mContext) { // from class: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.5
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), ChatAttachAlertPollLayout.this.topPadding);
                        }
                    };
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-ChatAttachAlertPollLayout$ListAdapter */
        public /* synthetic */ void m2508xd77380a3(View v) {
            int position;
            if (v.getTag() != null) {
                return;
            }
            v.setTag(1);
            PollEditTextCell p = (PollEditTextCell) v.getParent();
            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(p);
            if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                ChatAttachAlertPollLayout.this.listAdapter.notifyItemRemoved(position);
                System.arraycopy(ChatAttachAlertPollLayout.this.answers, index + 1, ChatAttachAlertPollLayout.this.answers, index, (ChatAttachAlertPollLayout.this.answers.length - 1) - index);
                System.arraycopy(ChatAttachAlertPollLayout.this.answersChecks, index + 1, ChatAttachAlertPollLayout.this.answersChecks, index, (ChatAttachAlertPollLayout.this.answersChecks.length - 1) - index);
                ChatAttachAlertPollLayout.this.answers[ChatAttachAlertPollLayout.this.answers.length - 1] = null;
                ChatAttachAlertPollLayout.this.answersChecks[ChatAttachAlertPollLayout.this.answersChecks.length - 1] = false;
                ChatAttachAlertPollLayout.access$1210(ChatAttachAlertPollLayout.this);
                if (ChatAttachAlertPollLayout.this.answersCount == ChatAttachAlertPollLayout.this.answers.length - 1) {
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemInserted((ChatAttachAlertPollLayout.this.answerStartRow + ChatAttachAlertPollLayout.this.answers.length) - 1);
                }
                RecyclerView.ViewHolder holder2 = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(position - 1);
                EditTextBoldCursor editText = p.getTextView();
                if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                    PollEditTextCell editTextCell = (PollEditTextCell) holder2.itemView;
                    editTextCell.getTextView().requestFocus();
                } else if (editText.isFocused()) {
                    AndroidUtilities.hideKeyboard(editText);
                }
                editText.clearFocus();
                ChatAttachAlertPollLayout.this.checkDoneButton();
                ChatAttachAlertPollLayout.this.updateRows();
                ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.answerSectionRow);
                ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.emptyRow);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-ChatAttachAlertPollLayout$ListAdapter */
        public /* synthetic */ boolean m2509x41a308c2(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            int position;
            if (actionId == 5) {
                RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(cell);
                if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                    int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                    if (index == ChatAttachAlertPollLayout.this.answersCount - 1 && ChatAttachAlertPollLayout.this.answersCount < 10) {
                        ChatAttachAlertPollLayout.this.addNewField();
                    } else if (index != ChatAttachAlertPollLayout.this.answersCount - 1) {
                        RecyclerView.ViewHolder holder2 = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(position + 1);
                        if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                            PollEditTextCell editTextCell = (PollEditTextCell) holder2.itemView;
                            editTextCell.getTextView().requestFocus();
                        }
                    } else {
                        AndroidUtilities.hideKeyboard(cell.getTextView());
                    }
                }
                return true;
            }
            return false;
        }

        public static /* synthetic */ boolean lambda$onCreateViewHolder$2(PollEditTextCell cell, View v, int keyCode, KeyEvent event) {
            EditTextBoldCursor field = (EditTextBoldCursor) v;
            if (keyCode == 67 && event.getAction() == 0 && field.length() == 0) {
                cell.callOnDelete();
                return true;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != ChatAttachAlertPollLayout.this.questionHeaderRow && position != ChatAttachAlertPollLayout.this.answerHeaderRow && position != ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                if (position != ChatAttachAlertPollLayout.this.questionSectionRow) {
                    if (position != ChatAttachAlertPollLayout.this.answerSectionRow && position != ChatAttachAlertPollLayout.this.settingsSectionRow && position != ChatAttachAlertPollLayout.this.solutionInfoRow) {
                        if (position != ChatAttachAlertPollLayout.this.addAnswerRow) {
                            if (position != ChatAttachAlertPollLayout.this.questionRow) {
                                if (position != ChatAttachAlertPollLayout.this.solutionRow) {
                                    if (position != ChatAttachAlertPollLayout.this.anonymousRow && position != ChatAttachAlertPollLayout.this.multipleRow && position != ChatAttachAlertPollLayout.this.quizRow) {
                                        if (position != ChatAttachAlertPollLayout.this.emptyRow) {
                                            if (position == ChatAttachAlertPollLayout.this.paddingRow) {
                                                return 9;
                                            }
                                            return 5;
                                        }
                                        return 8;
                                    }
                                    return 6;
                                }
                                return 7;
                            }
                            return 4;
                        }
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - ChatAttachAlertPollLayout.this.answerStartRow;
            int idx2 = toIndex - ChatAttachAlertPollLayout.this.answerStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < ChatAttachAlertPollLayout.this.answersCount && idx2 < ChatAttachAlertPollLayout.this.answersCount) {
                String from = ChatAttachAlertPollLayout.this.answers[idx1];
                ChatAttachAlertPollLayout.this.answers[idx1] = ChatAttachAlertPollLayout.this.answers[idx2];
                ChatAttachAlertPollLayout.this.answers[idx2] = from;
                boolean temp = ChatAttachAlertPollLayout.this.answersChecks[idx1];
                ChatAttachAlertPollLayout.this.answersChecks[idx1] = ChatAttachAlertPollLayout.this.answersChecks[idx2];
                ChatAttachAlertPollLayout.this.answersChecks[idx2] = temp;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyView.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        return themeDescriptions;
    }
}
