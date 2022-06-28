package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PollCreateActivity;
/* loaded from: classes4.dex */
public class PollCreateActivity extends BaseFragment {
    private static final int done_button = 1;
    private int addAnswerRow;
    private int anonymousRow;
    private int answerHeaderRow;
    private int answerSectionRow;
    private int answerStartRow;
    private PollCreateActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private boolean hintShowed;
    private HintView hintView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean multipleChoise;
    private int multipleRow;
    private ChatActivity parentFragment;
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
    private String[] answers = new String[10];
    private boolean[] answersChecks = new boolean[10];
    private int answersCount = 1;
    private boolean anonymousPoll = true;
    private int requestFieldFocusAtPosition = -1;

    /* loaded from: classes4.dex */
    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
    }

    static /* synthetic */ int access$2210(PollCreateActivity x0) {
        int i = x0.answersCount;
        x0.answersCount = i - 1;
        return i;
    }

    /* loaded from: classes4.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
            PollCreateActivity.this = this$0;
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
                PollCreateActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
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
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
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

    public PollCreateActivity(ChatActivity chatActivity, Boolean quiz) {
        int i = 1;
        this.parentFragment = chatActivity;
        if (quiz != null) {
            boolean booleanValue = quiz.booleanValue();
            this.quizPoll = booleanValue;
            this.quizOnly = !booleanValue ? 2 : i;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.quizOnly == 1) {
            this.actionBar.setTitle(LocaleController.getString("NewQuiz", R.string.NewQuiz));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewPoll", R.string.NewPoll));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        ActionBarMenu menu = this.actionBar.createMenu();
        this.doneItem = menu.addItem(1, LocaleController.getString("Create", R.string.Create).toUpperCase());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.PollCreateActivity.2
            @Override // androidx.recyclerview.widget.RecyclerView
            public void requestChildOnScreen(View child, View focused) {
                if (!(child instanceof PollEditTextCell)) {
                    return;
                }
                super.requestChildOnScreen(child, focused);
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PollCreateActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                PollCreateActivity.this.m4306lambda$createView$0$orgtelegramuiPollCreateActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PollCreateActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0 && PollCreateActivity.this.hintView != null) {
                    PollCreateActivity.this.hintView.hide();
                }
            }
        });
        HintView hintView = new HintView(context, 4);
        this.hintView = hintView;
        hintView.setText(LocaleController.getString("PollTapToSelect", R.string.PollTapToSelect));
        this.hintView.setAlpha(0.0f);
        this.hintView.setVisibility(4);
        frameLayout.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        checkDoneButton();
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.PollCreateActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
            PollCreateActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            if (id == -1) {
                if (PollCreateActivity.this.checkDiscard()) {
                    PollCreateActivity.this.finishFragment();
                }
            } else if (id == 1) {
                if (PollCreateActivity.this.quizPoll && PollCreateActivity.this.doneItem.getAlpha() != 1.0f) {
                    int checksCount = 0;
                    for (int a = 0; a < PollCreateActivity.this.answersChecks.length; a++) {
                        if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a])) && PollCreateActivity.this.answersChecks[a]) {
                            checksCount++;
                        }
                    }
                    if (checksCount <= 0) {
                        PollCreateActivity.this.showQuizHint();
                        return;
                    }
                    return;
                }
                final TLRPC.TL_messageMediaPoll poll = new TLRPC.TL_messageMediaPoll();
                poll.poll = new TLRPC.TL_poll();
                poll.poll.multiple_choice = PollCreateActivity.this.multipleChoise;
                poll.poll.quiz = PollCreateActivity.this.quizPoll;
                poll.poll.public_voters = !PollCreateActivity.this.anonymousPoll;
                poll.poll.question = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.questionString).toString();
                SerializedData serializedData = new SerializedData(10);
                for (int a2 = 0; a2 < PollCreateActivity.this.answers.length; a2++) {
                    if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a2]))) {
                        TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
                        answer.text = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a2]).toString();
                        answer.option = new byte[1];
                        answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                        poll.poll.answers.add(answer);
                        if ((PollCreateActivity.this.multipleChoise || PollCreateActivity.this.quizPoll) && PollCreateActivity.this.answersChecks[a2]) {
                            serializedData.writeByte(answer.option[0]);
                        }
                    }
                }
                final HashMap<String, String> params = new HashMap<>();
                params.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
                poll.results = new TLRPC.TL_pollResults();
                CharSequence solution = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.solutionString);
                if (solution != null) {
                    poll.results.solution = solution.toString();
                    CharSequence[] message = {solution};
                    ArrayList<TLRPC.MessageEntity> entities = PollCreateActivity.this.getMediaDataController().getEntities(message, true);
                    if (entities != null && !entities.isEmpty()) {
                        poll.results.solution_entities = entities;
                    }
                    if (!TextUtils.isEmpty(poll.results.solution)) {
                        poll.results.flags |= 16;
                    }
                }
                if (PollCreateActivity.this.parentFragment.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(PollCreateActivity.this.getParentActivity(), PollCreateActivity.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.PollCreateActivity$1$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i) {
                            PollCreateActivity.AnonymousClass1.this.m4307lambda$onItemClick$0$orgtelegramuiPollCreateActivity$1(poll, params, z, i);
                        }
                    });
                    return;
                }
                PollCreateActivity.this.delegate.sendPoll(poll, params, true, 0);
                PollCreateActivity.this.finishFragment();
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-PollCreateActivity$1 */
        public /* synthetic */ void m4307lambda$onItemClick$0$orgtelegramuiPollCreateActivity$1(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
            PollCreateActivity.this.delegate.sendPoll(poll, params, notify, scheduleDate);
            PollCreateActivity.this.finishFragment();
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PollCreateActivity */
    public /* synthetic */ void m4306lambda$createView$0$orgtelegramuiPollCreateActivity(View view, int position) {
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
                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                    if (holder != null) {
                        ((TextCheckCell) holder.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.quizRow);
                    }
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow, 2);
                }
                checked = checked2;
            } else if (this.quizOnly != 0) {
                return;
            } else {
                checked = !this.quizPoll;
                this.quizPoll = checked;
                int prevSolutionRow2 = this.solutionRow;
                updateRows();
                if (this.quizPoll) {
                    this.listAdapter.notifyItemRangeInserted(this.solutionRow, 2);
                } else {
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow2, 2);
                }
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void showQuizHint() {
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
                if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.answers[a])) && this.answersChecks[a]) {
                    checksCount++;
                }
            }
        }
        boolean z = true;
        if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.solutionString)) && this.solutionString.length() > 200) {
            enabled = false;
        } else if (TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        } else {
            int count = 0;
            int a2 = 0;
            while (true) {
                String[] strArr = this.answers;
                if (a2 >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(strArr[a2]))) {
                    if (this.answers[a2].length() > 100) {
                        count = 0;
                        break;
                    }
                    count++;
                }
                a2++;
            }
            if (count < 2 || (this.quizPoll && checksCount < 1)) {
                enabled = false;
            }
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if ((!this.quizPoll || checksCount != 0) && !enabled) {
            z = false;
        }
        actionBarMenuItem.setEnabled(z);
        this.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.questionHeaderRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.questionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.questionSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.answerHeaderRow = i3;
        int i5 = this.answersCount;
        if (i5 != 0) {
            this.answerStartRow = i4;
            this.rowCount = i4 + i5;
        } else {
            this.answerStartRow = -1;
        }
        if (i5 != this.answers.length) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.addAnswerRow = i6;
        } else {
            this.addAnswerRow = -1;
        }
        int i7 = this.rowCount;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.answerSectionRow = i7;
        this.rowCount = i8 + 1;
        this.settingsHeaderRow = i8;
        TLRPC.Chat chat = this.parentFragment.getCurrentChat();
        if (!ChatObject.isChannel(chat) || chat.megagroup) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.anonymousRow = i9;
        } else {
            this.anonymousRow = -1;
        }
        int i10 = this.quizOnly;
        if (i10 != 1) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.multipleRow = i11;
        } else {
            this.multipleRow = -1;
        }
        if (i10 == 0) {
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.quizRow = i12;
        } else {
            this.quizRow = -1;
        }
        int i13 = this.rowCount;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.settingsSectionRow = i13;
        if (this.quizPoll) {
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.solutionRow = i14;
            this.rowCount = i15 + 1;
            this.solutionInfoRow = i15;
            return;
        }
        this.solutionRow = -1;
        this.solutionInfoRow = -1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    public boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.questionString));
        if (allowDiscard) {
            for (int a = 0; a < this.answersCount && (allowDiscard = TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.answers[a]))); a++) {
            }
        }
        if (!allowDiscard) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", R.string.CancelPollAlertTitle));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", R.string.CancelPollAlertText));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PollCreateActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PollCreateActivity.this.m4305lambda$checkDiscard$1$orgtelegramuiPollCreateActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
        return allowDiscard;
    }

    /* renamed from: lambda$checkDiscard$1$org-telegram-ui-PollCreateActivity */
    public /* synthetic */ void m4305lambda$checkDiscard$1$orgtelegramuiPollCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
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
            textView.setTextColor(Theme.getColor(key));
            textView.setTag(key);
            return;
        }
        textCell.setText2("");
    }

    public void addNewField() {
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
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PollCreateActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            boolean z2 = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position != PollCreateActivity.this.questionHeaderRow) {
                        if (position == PollCreateActivity.this.answerHeaderRow) {
                            if (PollCreateActivity.this.quizOnly == 1) {
                                cell.setText(LocaleController.getString("QuizAnswers", R.string.QuizAnswers));
                                return;
                            } else {
                                cell.setText(LocaleController.getString("AnswerOptions", R.string.AnswerOptions));
                                return;
                            }
                        } else if (position == PollCreateActivity.this.settingsHeaderRow) {
                            cell.setText(LocaleController.getString("Settings", R.string.Settings));
                            return;
                        } else {
                            return;
                        }
                    }
                    cell.setText(LocaleController.getString("PollQuestion", R.string.PollQuestion));
                    return;
                case 1:
                case 4:
                case 5:
                default:
                    return;
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    cell2.setFixedSize(0);
                    cell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    if (position != PollCreateActivity.this.solutionInfoRow) {
                        if (position == PollCreateActivity.this.settingsSectionRow) {
                            if (PollCreateActivity.this.quizOnly != 0) {
                                cell2.setFixedSize(12);
                                cell2.setText(null);
                                return;
                            }
                            cell2.setText(LocaleController.getString("QuizInfo", R.string.QuizInfo));
                            return;
                        } else if (10 - PollCreateActivity.this.answersCount <= 0) {
                            cell2.setText(LocaleController.getString("AddAnOptionInfoMax", R.string.AddAnOptionInfoMax));
                            return;
                        } else {
                            cell2.setText(LocaleController.formatString("AddAnOptionInfo", R.string.AddAnOptionInfo, LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount, new Object[0])));
                            return;
                        }
                    }
                    cell2.setText(LocaleController.getString("AddAnExplanationInfo", R.string.AddAnExplanationInfo));
                    return;
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors(null, Theme.key_windowBackgroundWhiteBlueText4);
                    Drawable drawable1 = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", R.string.AddAnOption), (Drawable) combinedDrawable, false);
                    return;
                case 6:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position != PollCreateActivity.this.anonymousRow) {
                        if (position != PollCreateActivity.this.multipleRow) {
                            if (position == PollCreateActivity.this.quizRow) {
                                checkCell.setTextAndCheck(LocaleController.getString("PollQuiz", R.string.PollQuiz), PollCreateActivity.this.quizPoll, false);
                                if (PollCreateActivity.this.quizOnly != 0) {
                                    z = false;
                                }
                                checkCell.setEnabled(z, null);
                                return;
                            }
                            return;
                        }
                        String string = LocaleController.getString("PollMultiple", R.string.PollMultiple);
                        boolean z3 = PollCreateActivity.this.multipleChoise;
                        if (PollCreateActivity.this.quizRow != -1) {
                            z2 = true;
                        }
                        checkCell.setTextAndCheck(string, z3, z2);
                        checkCell.setEnabled(true, null);
                        return;
                    }
                    String string2 = LocaleController.getString("PollAnonymous", R.string.PollAnonymous);
                    boolean z4 = PollCreateActivity.this.anonymousPoll;
                    if (PollCreateActivity.this.multipleRow != -1 || PollCreateActivity.this.quizRow != -1) {
                        z2 = true;
                    }
                    checkCell.setTextAndCheck(string2, z4, z2);
                    checkCell.setEnabled(true, null);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            CharSequence charSequence = "";
            if (viewType == 4) {
                PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                textCell.setTag(1);
                if (PollCreateActivity.this.questionString != null) {
                    charSequence = PollCreateActivity.this.questionString;
                }
                textCell.setTextAndHint(charSequence, LocaleController.getString("QuestionHint", R.string.QuestionHint), false);
                textCell.setTag(null);
                PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            } else if (viewType == 5) {
                int position = holder.getAdapterPosition();
                PollEditTextCell textCell2 = (PollEditTextCell) holder.itemView;
                textCell2.setTag(1);
                int index = position - PollCreateActivity.this.answerStartRow;
                textCell2.setTextAndHint(PollCreateActivity.this.answers[index], LocaleController.getString("OptionHint", R.string.OptionHint), true);
                textCell2.setTag(null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == position) {
                    EditTextBoldCursor editText = textCell2.getTextView();
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity.this.setTextLeft(holder.itemView, position);
            } else if (viewType == 7) {
                PollEditTextCell textCell3 = (PollEditTextCell) holder.itemView;
                textCell3.setTag(1);
                if (PollCreateActivity.this.solutionString != null) {
                    charSequence = PollCreateActivity.this.solutionString;
                }
                textCell3.setTextAndHint(charSequence, LocaleController.getString("AddAnExplanation", R.string.AddAnExplanation), false);
                textCell3.setTag(null);
                PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
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
            return position == PollCreateActivity.this.addAnswerRow || position == PollCreateActivity.this.anonymousRow || position == PollCreateActivity.this.multipleRow || (PollCreateActivity.this.quizOnly == 0 && position == PollCreateActivity.this.quizRow);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 3:
                    View view3 = new TextCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 4:
                    final PollEditTextCell cell = new PollEditTextCell(this.mContext, null);
                    cell.createErrorTextView();
                    cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.PollCreateActivity.ListAdapter.1
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
                            PollCreateActivity.this.questionString = s.toString();
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.questionRow);
                            if (holder != null) {
                                PollCreateActivity.this.setTextLeft(holder.itemView, PollCreateActivity.this.questionRow);
                            }
                            PollCreateActivity.this.checkDoneButton();
                        }
                    });
                    view = cell;
                    break;
                case 5:
                default:
                    final PollEditTextCell cell2 = new PollEditTextCell(this.mContext, new View.OnClickListener() { // from class: org.telegram.ui.PollCreateActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view4) {
                            PollCreateActivity.ListAdapter.this.m4308xf224277c(view4);
                        }
                    }) { // from class: org.telegram.ui.PollCreateActivity.ListAdapter.4
                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean drawDivider() {
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(this);
                            if (holder != null) {
                                int position = holder.getAdapterPosition();
                                if (PollCreateActivity.this.answersCount == 10 && position == (PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount) - 1) {
                                    return false;
                                }
                            }
                            return true;
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean shouldShowCheckBox() {
                            return PollCreateActivity.this.quizPoll;
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        public void onCheckBoxClick(PollEditTextCell editText, boolean checked) {
                            int position;
                            if (checked && PollCreateActivity.this.quizPoll) {
                                Arrays.fill(PollCreateActivity.this.answersChecks, false);
                                PollCreateActivity.this.listView.getChildCount();
                                for (int a = PollCreateActivity.this.answerStartRow; a < PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount; a++) {
                                    RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(a);
                                    if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                                        PollEditTextCell pollEditTextCell = (PollEditTextCell) holder.itemView;
                                        pollEditTextCell.setChecked(false, true);
                                    }
                                }
                            }
                            super.onCheckBoxClick(editText, checked);
                            RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findContainingViewHolder(editText);
                            if (holder2 != null && (position = holder2.getAdapterPosition()) != -1) {
                                int index = position - PollCreateActivity.this.answerStartRow;
                                PollCreateActivity.this.answersChecks[index] = checked;
                            }
                            PollCreateActivity.this.checkDoneButton();
                        }

                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected boolean isChecked(PollEditTextCell editText) {
                            int position;
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(editText);
                            if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                                int index = position - PollCreateActivity.this.answerStartRow;
                                return PollCreateActivity.this.answersChecks[index];
                            }
                            return false;
                        }
                    };
                    cell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell2.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.PollCreateActivity.ListAdapter.5
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell2);
                            if (holder != null) {
                                int position = holder.getAdapterPosition();
                                int index = position - PollCreateActivity.this.answerStartRow;
                                if (index >= 0 && index < PollCreateActivity.this.answers.length) {
                                    PollCreateActivity.this.answers[index] = s.toString();
                                    PollCreateActivity.this.setTextLeft(cell2, index);
                                    PollCreateActivity.this.checkDoneButton();
                                }
                            }
                        }
                    });
                    cell2.setShowNextButton(true);
                    EditTextBoldCursor editText = cell2.getTextView();
                    editText.setImeOptions(editText.getImeOptions() | 5);
                    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PollCreateActivity$ListAdapter$$ExternalSyntheticLambda2
                        @Override // android.widget.TextView.OnEditorActionListener
                        public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            return PollCreateActivity.ListAdapter.this.m4309x2beec95b(cell2, textView, i, keyEvent);
                        }
                    });
                    editText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.PollCreateActivity$ListAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnKeyListener
                        public final boolean onKey(View view4, int i, KeyEvent keyEvent) {
                            return PollCreateActivity.ListAdapter.lambda$onCreateViewHolder$2(PollEditTextCell.this, view4, i, keyEvent);
                        }
                    });
                    view = cell2;
                    break;
                case 6:
                    View view4 = new TextCheckCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 7:
                    final PollEditTextCell cell3 = new PollEditTextCell(this.mContext, true, null) { // from class: org.telegram.ui.PollCreateActivity.ListAdapter.2
                        @Override // org.telegram.ui.Cells.PollEditTextCell
                        protected void onActionModeStart(EditTextBoldCursor editText2, ActionMode actionMode) {
                            if (editText2.isFocused() && editText2.hasSelection()) {
                                Menu menu = actionMode.getMenu();
                                if (menu.findItem(16908321) != null) {
                                    PollCreateActivity.this.parentFragment.fillActionModeMenu(menu);
                                }
                            }
                        }
                    };
                    cell3.createErrorTextView();
                    cell3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell3.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.PollCreateActivity.ListAdapter.3
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (cell3.getTag() == null) {
                                PollCreateActivity.this.solutionString = s;
                                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.solutionRow);
                                if (holder != null) {
                                    PollCreateActivity.this.setTextLeft(holder.itemView, PollCreateActivity.this.solutionRow);
                                }
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    });
                    view = cell3;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PollCreateActivity$ListAdapter */
        public /* synthetic */ void m4308xf224277c(View v) {
            int position;
            if (v.getTag() != null) {
                return;
            }
            v.setTag(1);
            PollEditTextCell p = (PollEditTextCell) v.getParent();
            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(p);
            if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                int index = position - PollCreateActivity.this.answerStartRow;
                PollCreateActivity.this.listAdapter.notifyItemRemoved(position);
                System.arraycopy(PollCreateActivity.this.answers, index + 1, PollCreateActivity.this.answers, index, (PollCreateActivity.this.answers.length - 1) - index);
                System.arraycopy(PollCreateActivity.this.answersChecks, index + 1, PollCreateActivity.this.answersChecks, index, (PollCreateActivity.this.answersChecks.length - 1) - index);
                PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                PollCreateActivity.this.answersChecks[PollCreateActivity.this.answersChecks.length - 1] = false;
                PollCreateActivity.access$2210(PollCreateActivity.this);
                if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                    PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                }
                RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position - 1);
                EditTextBoldCursor editText = p.getTextView();
                if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                    PollEditTextCell editTextCell = (PollEditTextCell) holder2.itemView;
                    editTextCell.getTextView().requestFocus();
                } else if (editText.isFocused()) {
                    AndroidUtilities.hideKeyboard(editText);
                }
                editText.clearFocus();
                PollCreateActivity.this.checkDoneButton();
                PollCreateActivity.this.updateRows();
                PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-PollCreateActivity$ListAdapter */
        public /* synthetic */ boolean m4309x2beec95b(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            int position;
            if (actionId == 5) {
                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell);
                if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                    int index = position - PollCreateActivity.this.answerStartRow;
                    if (index == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                        PollCreateActivity.this.addNewField();
                    } else if (index != PollCreateActivity.this.answersCount - 1) {
                        RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position + 1);
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
            if (position != PollCreateActivity.this.questionHeaderRow && position != PollCreateActivity.this.answerHeaderRow && position != PollCreateActivity.this.settingsHeaderRow) {
                if (position != PollCreateActivity.this.questionSectionRow) {
                    if (position != PollCreateActivity.this.answerSectionRow && position != PollCreateActivity.this.settingsSectionRow && position != PollCreateActivity.this.solutionInfoRow) {
                        if (position != PollCreateActivity.this.addAnswerRow) {
                            if (position != PollCreateActivity.this.questionRow) {
                                if (position != PollCreateActivity.this.solutionRow) {
                                    if (position == PollCreateActivity.this.anonymousRow || position == PollCreateActivity.this.multipleRow || position == PollCreateActivity.this.quizRow) {
                                        return 6;
                                    }
                                    return 5;
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
            int idx1 = fromIndex - PollCreateActivity.this.answerStartRow;
            int idx2 = toIndex - PollCreateActivity.this.answerStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < PollCreateActivity.this.answersCount && idx2 < PollCreateActivity.this.answersCount) {
                String from = PollCreateActivity.this.answers[idx1];
                PollCreateActivity.this.answers[idx1] = PollCreateActivity.this.answers[idx2];
                PollCreateActivity.this.answers[idx2] = from;
                boolean temp = PollCreateActivity.this.answersChecks[idx1];
                PollCreateActivity.this.answersChecks[idx1] = PollCreateActivity.this.answersChecks[idx2];
                PollCreateActivity.this.answersChecks[idx2] = temp;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
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
