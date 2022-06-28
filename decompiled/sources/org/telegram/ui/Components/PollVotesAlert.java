package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.PollVotesAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new AnimationProperties.FloatProperty<UserCell>("placeholderAlpha") { // from class: org.telegram.ui.Components.PollVotesAlert.1
        public void setValue(UserCell object, float value) {
            object.setPlaceholderAlpha(value);
        }

        public Float get(UserCell object) {
            return Float.valueOf(object.getPlaceholderAlpha());
        }
    };
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private ChatActivity chatActivity;
    private float gradientWidth;
    private Adapter listAdapter;
    private RecyclerListView listView;
    private MessageObject messageObject;
    private TLRPC.InputPeer peer;
    private LinearGradient placeholderGradient;
    private Matrix placeholderMatrix;
    private TLRPC.Poll poll;
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private TextView titleTextView;
    private int topBeforeSwitch;
    private float totalTranslation;
    private HashSet<VotesList> loadingMore = new HashSet<>();
    private HashMap<VotesList, Button> votesPercents = new HashMap<>();
    private ArrayList<VotesList> voters = new ArrayList<>();
    private ArrayList<Integer> queries = new ArrayList<>();
    private Paint placeholderPaint = new Paint(1);
    private boolean loadingResults = true;
    private RectF rect = new RectF();

    static /* synthetic */ float access$3016(PollVotesAlert x0, float x1) {
        float f = x0.totalTranslation + x1;
        x0.totalTranslation = f;
        return f;
    }

    static /* synthetic */ float access$3024(PollVotesAlert x0, float x1) {
        float f = x0.totalTranslation - x1;
        x0.totalTranslation = f;
        return f;
    }

    /* loaded from: classes5.dex */
    public static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<TLRPC.User> users;
        public ArrayList<TLRPC.MessageUserVote> votes;

        public VotesList(TLRPC.TL_messages_votesList votesList, byte[] o) {
            this.count = votesList.count;
            this.votes = votesList.votes;
            this.users = votesList.users;
            this.next_offset = votesList.next_offset;
            this.option = o;
        }

        public int getCount() {
            if (this.collapsed) {
                return Math.min(this.collapsedCount, this.votes.size());
            }
            return this.votes.size();
        }

        public int getCollapsed() {
            if (this.votes.size() <= 15) {
                return 0;
            }
            if (this.collapsed) {
                return 1;
            }
            return 2;
        }
    }

    /* loaded from: classes5.dex */
    public class SectionCell extends FrameLayout {
        private TextView middleTextView;
        private TextView righTextView;
        private TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SectionCell(Context context) {
            super(context);
            PollVotesAlert.this = this$0;
            setBackgroundColor(Theme.getColor(Theme.key_graySection));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textView.setTextColor(Theme.getColor(Theme.key_graySectionText));
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            int i2 = 16;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView2 = new TextView(getContext());
            this.middleTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.middleTextView.setTextColor(Theme.getColor(Theme.key_graySectionText));
            this.middleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = new TextView(getContext()) { // from class: org.telegram.ui.Components.PollVotesAlert.SectionCell.1
                @Override // android.view.View
                public boolean post(Runnable action) {
                    return PollVotesAlert.this.containerView.post(action);
                }

                @Override // android.view.View
                public boolean postDelayed(Runnable action, long delayMillis) {
                    return PollVotesAlert.this.containerView.postDelayed(action, delayMillis);
                }
            };
            this.righTextView = textView3;
            textView3.setTextSize(1, 14.0f);
            this.righTextView.setTextColor(Theme.getColor(Theme.key_graySectionText));
            this.righTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
            this.righTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PollVotesAlert$SectionCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PollVotesAlert.SectionCell.this.m2875xe45e52c1(view);
                }
            });
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0 : 16, 0.0f, !LocaleController.isRTL ? 0 : i2, 0.0f));
            addView(this.middleTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(this.righTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-PollVotesAlert$SectionCell */
        public /* synthetic */ void m2875xe45e52c1(View v) {
            onCollapseClick();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED);
            measureChildWithMargins(this.middleTextView, widthMeasureSpec, 0, heightMeasureSpec2, 0);
            measureChildWithMargins(this.righTextView, widthMeasureSpec, 0, heightMeasureSpec2, 0);
            measureChildWithMargins(this.textView, widthMeasureSpec, this.middleTextView.getMeasuredWidth() + this.righTextView.getMeasuredWidth() + AndroidUtilities.dp(32.0f), heightMeasureSpec2, 0);
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(32.0f));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if (LocaleController.isRTL) {
                int l = this.textView.getLeft() - this.middleTextView.getMeasuredWidth();
                TextView textView = this.middleTextView;
                textView.layout(l, textView.getTop(), this.middleTextView.getMeasuredWidth() + l, this.middleTextView.getBottom());
                return;
            }
            int l2 = this.textView.getRight();
            TextView textView2 = this.middleTextView;
            textView2.layout(l2, textView2.getTop(), this.middleTextView.getMeasuredWidth() + l2, this.middleTextView.getBottom());
        }

        protected void onCollapseClick() {
        }

        public void setText(String left, int percent, int votesCount, int collapsed) {
            SpannableStringBuilder builder;
            TextView textView = this.textView;
            textView.setText(Emoji.replaceEmoji(left, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            String p = String.format("%d", Integer.valueOf(percent));
            if (LocaleController.isRTL) {
                builder = new SpannableStringBuilder(String.format("%s%% – ", Integer.valueOf(percent)));
            } else {
                builder = new SpannableStringBuilder(String.format(" – %s%%", Integer.valueOf(percent)));
            }
            builder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 3, p.length() + 3, 33);
            this.middleTextView.setText(builder);
            if (collapsed == 0) {
                if (PollVotesAlert.this.poll.quiz) {
                    this.righTextView.setText(LocaleController.formatPluralString("Answer", votesCount, new Object[0]));
                } else {
                    this.righTextView.setText(LocaleController.formatPluralString("Vote", votesCount, new Object[0]));
                }
            } else if (collapsed == 1) {
                this.righTextView.setText(LocaleController.getString("PollExpand", R.string.PollExpand));
            } else {
                this.righTextView.setText(LocaleController.getString("PollCollapse", R.string.PollCollapse));
            }
        }
    }

    /* loaded from: classes5.dex */
    public class UserCell extends FrameLayout {
        private ArrayList<Animator> animators;
        private BackupImageView avatarImageView;
        private TLRPC.User currentUser;
        private boolean drawPlaceholder;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private int placeholderNum;
        private int currentAccount = UserConfig.selectedAccount;
        private float placeholderAlpha = 1.0f;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public UserCell(Context context) {
            super(context);
            PollVotesAlert.this = this$0;
            setWillNotDraw(false);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(36, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 6.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 28.0f : 65.0f, 14.0f, LocaleController.isRTL ? 65.0f : 28.0f, 0.0f));
        }

        public void setData(TLRPC.User user, int num, boolean divider) {
            this.currentUser = user;
            this.needDivider = divider;
            this.drawPlaceholder = user == null;
            this.placeholderNum = num;
            if (user == null) {
                this.nameTextView.setText("");
                this.avatarImageView.setImageDrawable(null);
            } else {
                update(0);
            }
            ArrayList<Animator> arrayList = this.animators;
            if (arrayList != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.avatarImageView, View.ALPHA, 0.0f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, 0.0f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this, PollVotesAlert.USER_CELL_PROPERTY, 1.0f, 0.0f));
            } else if (!this.drawPlaceholder) {
                this.placeholderAlpha = 0.0f;
            }
        }

        public void setPlaceholderAlpha(float value) {
            this.placeholderAlpha = value;
            invalidate();
        }

        public float getPlaceholderAlpha() {
            return this.placeholderAlpha;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void update(int mask) {
            TLRPC.FileLocation fileLocation;
            TLRPC.FileLocation photo = null;
            String newName = null;
            TLRPC.User user = this.currentUser;
            if (user != null && user.photo != null) {
                photo = this.currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || (fileLocation != null && photo != null && (fileLocation.volume_id != photo.volume_id || this.lastAvatar.local_id != photo.local_id))))) {
                    continueUpdate = true;
                }
                if (this.currentUser != null && !continueUpdate && (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    int newStatus = 0;
                    if (this.currentUser.status != null) {
                        newStatus = this.currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                    TLRPC.User user2 = this.currentUser;
                    if (user2 != null) {
                        newName = UserObject.getUserName(user2);
                    }
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            this.avatarDrawable.setInfo(this.currentUser);
            if (this.currentUser.status != null) {
                this.lastStatus = this.currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.lastName = newName == null ? UserObject.getUserName(user3) : newName;
            } else {
                this.lastName = "";
            }
            this.nameTextView.setText(this.lastName);
            this.lastAvatar = photo;
            TLRPC.User user4 = this.currentUser;
            if (user4 != null) {
                this.avatarImageView.setForUserOrChat(user4, this.avatarDrawable);
            } else {
                this.avatarImageView.setImageDrawable(this.avatarDrawable);
            }
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int w;
            int cx;
            int w2;
            int cx2;
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int cx3 = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
                int cy = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle(cx3, cy, this.avatarImageView.getMeasuredWidth() / 2, PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    cx = AndroidUtilities.dp(65.0f);
                    w = AndroidUtilities.dp(48.0f);
                } else {
                    cx = AndroidUtilities.dp(65.0f);
                    w = AndroidUtilities.dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    cx = (getMeasuredWidth() - cx) - w;
                }
                PollVotesAlert.this.rect.set(cx, cy - AndroidUtilities.dp(4.0f), cx + w, AndroidUtilities.dp(4.0f) + cy);
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    cx2 = AndroidUtilities.dp(119.0f);
                    w2 = AndroidUtilities.dp(60.0f);
                } else {
                    cx2 = AndroidUtilities.dp(131.0f);
                    w2 = AndroidUtilities.dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    cx2 = (getMeasuredWidth() - cx2) - w2;
                }
                PollVotesAlert.this.rect.set(cx2, cy - AndroidUtilities.dp(4.0f), cx2 + w2, AndroidUtilities.dp(4.0f) + cy);
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public static void showForPoll(ChatActivity parentFragment, MessageObject messageObject) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return;
        }
        PollVotesAlert alert = new PollVotesAlert(parentFragment, messageObject);
        parentFragment.showDialog(alert);
    }

    /* loaded from: classes5.dex */
    public static class Button {
        private float decimal;
        private int percent;
        private int votesCount;

        private Button() {
        }

        static /* synthetic */ float access$3924(Button x0, float x1) {
            float f = x0.decimal - x1;
            x0.decimal = f;
            return f;
        }

        static /* synthetic */ int access$4012(Button x0, int x1) {
            int i = x0.percent + x1;
            x0.percent = i;
            return i;
        }
    }

    public PollVotesAlert(final ChatActivity parentFragment, MessageObject message) {
        super(parentFragment.getParentActivity(), true);
        int count;
        fixNavigationBar();
        this.messageObject = message;
        this.chatActivity = parentFragment;
        TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) message.messageOwner.media;
        this.poll = mediaPoll.poll;
        Context context = parentFragment.getParentActivity();
        this.peer = parentFragment.getMessagesController().getInputPeer((int) message.getDialogId());
        final ArrayList<VotesList> loadedVoters = new ArrayList<>();
        int count2 = mediaPoll.results.results.size();
        final Integer[] reqIds = new Integer[count2];
        int a = 0;
        while (a < count2) {
            final TLRPC.TL_pollAnswerVoters answerVoters = mediaPoll.results.results.get(a);
            if (answerVoters.voters == 0) {
                count = count2;
            } else {
                TLRPC.TL_messages_votesList votesList = new TLRPC.TL_messages_votesList();
                int i = 15;
                int N = answerVoters.voters <= 15 ? answerVoters.voters : 10;
                for (int b = 0; b < N; b++) {
                    votesList.votes.add(new TLRPC.TL_messageUserVoteInputOption());
                }
                votesList.next_offset = N < answerVoters.voters ? "empty" : null;
                votesList.count = answerVoters.voters;
                VotesList list = new VotesList(votesList, answerVoters.option);
                this.voters.add(list);
                TLRPC.TL_messages_getPollVotes req = new TLRPC.TL_messages_getPollVotes();
                req.peer = this.peer;
                req.id = this.messageObject.getId();
                req.limit = answerVoters.voters > 15 ? 10 : i;
                req.flags |= 1;
                req.option = answerVoters.option;
                final int num = a;
                count = count2;
                reqIds[a] = Integer.valueOf(parentFragment.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PollVotesAlert.this.m2871lambda$new$1$orgtelegramuiComponentsPollVotesAlert(reqIds, num, parentFragment, loadedVoters, answerVoters, tLObject, tL_error);
                    }
                }));
                this.queries.add(reqIds[a]);
            }
            a++;
            count2 = count;
        }
        updateButtons();
        Collections.sort(this.voters, new Comparator<VotesList>() { // from class: org.telegram.ui.Components.PollVotesAlert.2
            private int getIndex(VotesList votesList2) {
                int N2 = PollVotesAlert.this.poll.answers.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    TLRPC.TL_pollAnswer answer = PollVotesAlert.this.poll.answers.get(a2);
                    if (Arrays.equals(answer.option, votesList2.option)) {
                        return a2;
                    }
                }
                return 0;
            }

            public int compare(VotesList o1, VotesList o2) {
                int i1 = getIndex(o1);
                int i2 = getIndex(o2);
                if (i1 > i2) {
                    return 1;
                }
                if (i1 < i2) {
                    return -1;
                }
                return 0;
            }
        });
        updatePlaceholder();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.PollVotesAlert.3
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21 && !PollVotesAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PollVotesAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PollVotesAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int availableHeight = totalHeight - getPaddingTop();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PollVotesAlert.this.listView.getLayoutParams();
                layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) PollVotesAlert.this.actionBarShadow.getLayoutParams();
                layoutParams2.topMargin = ActionBar.getCurrentActionBarHeight();
                int contentSize = PollVotesAlert.this.backgroundPaddingTop + AndroidUtilities.dp(15.0f) + AndroidUtilities.statusBarHeight;
                int sectionCount = PollVotesAlert.this.listAdapter.getSectionCount();
                for (int a2 = 0; a2 < sectionCount; a2++) {
                    if (a2 == 0) {
                        PollVotesAlert.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec - (PollVotesAlert.this.backgroundPaddingLeft * 2)), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                        contentSize += PollVotesAlert.this.titleTextView.getMeasuredHeight();
                    } else {
                        int count3 = PollVotesAlert.this.listAdapter.getCountForSection(a2);
                        contentSize += AndroidUtilities.dp(32.0f) + (AndroidUtilities.dp(50.0f) * (count3 - 1));
                    }
                }
                int padding = (contentSize < availableHeight ? availableHeight - contentSize : availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                if (PollVotesAlert.this.listView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    PollVotesAlert.this.listView.setPinnedSectionOffsetY(-padding);
                    PollVotesAlert.this.listView.setPadding(0, padding, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b2) {
                super.onLayout(changed, l, t, r, b2);
                PollVotesAlert.this.updateLayout(false);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && PollVotesAlert.this.scrollOffsetY != 0 && ev.getY() < PollVotesAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f) && PollVotesAlert.this.actionBar.getAlpha() == 0.0f) {
                    PollVotesAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !PollVotesAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int offset = AndroidUtilities.dp(13.0f);
                int top = (PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - offset;
                if (PollVotesAlert.this.currentSheetAnimationType == 1) {
                    top = (int) (top + PollVotesAlert.this.listView.getTranslationY());
                }
                int y = AndroidUtilities.dp(20.0f) + top;
                int height = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + PollVotesAlert.this.backgroundPaddingTop;
                float rad = 1.0f;
                if (PollVotesAlert.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight()) {
                    float toMove = AndroidUtilities.dp(4.0f) + offset;
                    float moveProgress = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - top) - PollVotesAlert.this.backgroundPaddingTop) / toMove);
                    float availableToMove = ActionBar.getCurrentActionBarHeight() - toMove;
                    int diff = (int) (availableToMove * moveProgress);
                    top -= diff;
                    y -= diff;
                    height += diff;
                    rad = 1.0f - moveProgress;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                }
                PollVotesAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                PollVotesAlert.this.shadowDrawable.draw(canvas);
                if (rad != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
                    this.rect.set(PollVotesAlert.this.backgroundPaddingLeft, PollVotesAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft, PollVotesAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                }
                if (rad != 0.0f) {
                    int w = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                    int color = Theme.getColor(Theme.key_sheet_scrollUp);
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * 1.0f * rad));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                int color1 = Theme.getColor(Theme.key_dialogBackground);
                int finalColor = Color.argb((int) (PollVotesAlert.this.actionBar.getAlpha() * 255.0f), (int) (Color.red(color1) * 0.8f), (int) (Color.green(color1) * 0.8f), (int) (Color.blue(color1) * 0.8f));
                Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                canvas.drawRect(PollVotesAlert.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - PollVotesAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.PollVotesAlert.4
            long lastUpdateTime;

            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (PollVotesAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (PollVotesAlert.this.loadingResults) {
                    long newUpdateTime = SystemClock.elapsedRealtime();
                    long dt = Math.abs(this.lastUpdateTime - newUpdateTime);
                    if (dt > 17) {
                        dt = 16;
                    }
                    this.lastUpdateTime = newUpdateTime;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                    PollVotesAlert.access$3016(pollVotesAlert, (((float) dt) * pollVotesAlert.gradientWidth) / 1800.0f);
                    while (PollVotesAlert.this.totalTranslation >= PollVotesAlert.this.gradientWidth * 2.0f) {
                        PollVotesAlert pollVotesAlert2 = PollVotesAlert.this;
                        PollVotesAlert.access$3024(pollVotesAlert2, pollVotesAlert2.gradientWidth * 2.0f);
                    }
                    PollVotesAlert.this.placeholderMatrix.setTranslate(PollVotesAlert.this.totalTranslation, 0.0f);
                    PollVotesAlert.this.placeholderGradient.setLocalMatrix(PollVotesAlert.this.placeholderMatrix);
                    invalidateViews();
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSectionsType(2);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter = new Adapter(context);
        this.listAdapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                PollVotesAlert.this.m2874lambda$new$4$orgtelegramuiComponentsPollVotesAlert(parentFragment, view, i2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.PollVotesAlert.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (PollVotesAlert.this.listView.getChildCount() > 0) {
                    PollVotesAlert.this.updateLayout(true);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int top = (PollVotesAlert.this.scrollOffsetY - PollVotesAlert.this.backgroundPaddingTop) - offset;
                    if (PollVotesAlert.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight() && PollVotesAlert.this.listView.canScrollVertically(1)) {
                        PollVotesAlert.this.listView.getChildAt(0);
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) PollVotesAlert.this.listView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            PollVotesAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                }
            }
        });
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextSize(1, 18.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(21.0f));
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        this.titleTextView.setText(Emoji.replaceEmoji(this.poll.question, this.titleTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(18.0f), false));
        ActionBar actionBar = new ActionBar(context) { // from class: org.telegram.ui.Components.PollVotesAlert.6
            @Override // android.view.View
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                PollVotesAlert.this.containerView.invalidate();
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle(LocaleController.getString("PollResults", R.string.PollResults));
        if (this.poll.quiz) {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Answer", mediaPoll.results.total_voters, new Object[0]));
        } else {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Vote", mediaPoll.results.total_voters, new Object[0]));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.PollVotesAlert.7
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PollVotesAlert.this.dismiss();
                }
            }
        });
        View view = new View(context);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PollVotesAlert */
    public /* synthetic */ void m2871lambda$new$1$orgtelegramuiComponentsPollVotesAlert(final Integer[] reqIds, final int num, final ChatActivity parentFragment, final ArrayList loadedVoters, final TLRPC.TL_pollAnswerVoters answerVoters, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PollVotesAlert.this.m2870lambda$new$0$orgtelegramuiComponentsPollVotesAlert(reqIds, num, response, parentFragment, loadedVoters, answerVoters);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PollVotesAlert */
    public /* synthetic */ void m2870lambda$new$0$orgtelegramuiComponentsPollVotesAlert(Integer[] reqIds, int num, TLObject response, ChatActivity parentFragment, ArrayList loadedVoters, TLRPC.TL_pollAnswerVoters answerVoters) {
        RecyclerView.ViewHolder holder;
        this.queries.remove(reqIds[num]);
        if (response != null) {
            TLRPC.TL_messages_votesList res = (TLRPC.TL_messages_votesList) response;
            parentFragment.getMessagesController().putUsers(res.users, false);
            if (!res.votes.isEmpty()) {
                loadedVoters.add(new VotesList(res, answerVoters.option));
            }
            if (this.queries.isEmpty()) {
                boolean countChanged = false;
                int N2 = loadedVoters.size();
                for (int b = 0; b < N2; b++) {
                    VotesList votesList1 = (VotesList) loadedVoters.get(b);
                    int c = 0;
                    int N3 = this.voters.size();
                    while (true) {
                        if (c < N3) {
                            VotesList votesList2 = this.voters.get(c);
                            if (!Arrays.equals(votesList1.option, votesList2.option)) {
                                c++;
                            } else {
                                votesList2.next_offset = votesList1.next_offset;
                                if (votesList2.count != votesList1.count || votesList2.votes.size() != votesList1.votes.size()) {
                                    countChanged = true;
                                }
                                votesList2.count = votesList1.count;
                                votesList2.users = votesList1.users;
                                votesList2.votes = votesList1.votes;
                            }
                        }
                    }
                }
                this.loadingResults = false;
                if (this.listView != null) {
                    if (this.currentSheetAnimationType != 0 || this.startAnimationRunnable != null || countChanged) {
                        if (countChanged) {
                            updateButtons();
                        }
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    int c2 = this.listView.getChildCount();
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int b2 = 0; b2 < c2; b2++) {
                        View child = this.listView.getChildAt(b2);
                        if ((child instanceof UserCell) && (holder = this.listView.findContainingViewHolder(child)) != null) {
                            UserCell cell = (UserCell) child;
                            cell.animators = animators;
                            cell.setEnabled(true);
                            this.listAdapter.onViewAttachedToWindow(holder);
                            cell.animators = null;
                        }
                    }
                    if (!animators.isEmpty()) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(animators);
                        animatorSet.setDuration(180L);
                        animatorSet.start();
                    }
                    this.loadingResults = false;
                    return;
                }
                return;
            }
            return;
        }
        dismiss();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PollVotesAlert */
    public /* synthetic */ void m2874lambda$new$4$orgtelegramuiComponentsPollVotesAlert(final ChatActivity parentFragment, View view, int position) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return;
        }
        ArrayList<Integer> arrayList = this.queries;
        if (arrayList != null && !arrayList.isEmpty()) {
            return;
        }
        int i = 0;
        if (view instanceof TextCell) {
            int section = this.listAdapter.getSectionForPosition(position) - 1;
            int row = this.listAdapter.getPositionInSectionForPosition(position) - 1;
            if (row <= 0 || section < 0) {
                return;
            }
            final VotesList votesList = this.voters.get(section);
            if (row != votesList.getCount() || this.loadingMore.contains(votesList)) {
                return;
            }
            if (votesList.collapsed && votesList.collapsedCount < votesList.votes.size()) {
                votesList.collapsedCount = Math.min(votesList.collapsedCount + 50, votesList.votes.size());
                if (votesList.collapsedCount == votesList.votes.size()) {
                    votesList.collapsed = false;
                }
                this.listAdapter.notifyDataSetChanged();
                return;
            }
            this.loadingMore.add(votesList);
            TLRPC.TL_messages_getPollVotes req = new TLRPC.TL_messages_getPollVotes();
            req.peer = this.peer;
            req.id = this.messageObject.getId();
            req.limit = 50;
            req.flags = 1 | req.flags;
            req.option = votesList.option;
            req.flags |= 2;
            req.offset = votesList.next_offset;
            this.chatActivity.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PollVotesAlert.this.m2873lambda$new$3$orgtelegramuiComponentsPollVotesAlert(votesList, parentFragment, tLObject, tL_error);
                }
            });
        } else if (view instanceof UserCell) {
            UserCell userCell = (UserCell) view;
            if (userCell.currentUser == null) {
                return;
            }
            TLRPC.User currentUser = parentFragment.getCurrentUser();
            Bundle args = new Bundle();
            args.putLong("user_id", userCell.currentUser.id);
            dismiss();
            ProfileActivity fragment = new ProfileActivity(args);
            if (currentUser != null && currentUser.id == userCell.currentUser.id) {
                i = 1;
            }
            fragment.setPlayProfileAnimation(i);
            parentFragment.presentFragment(fragment);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PollVotesAlert */
    public /* synthetic */ void m2873lambda$new$3$orgtelegramuiComponentsPollVotesAlert(final VotesList votesList, final ChatActivity parentFragment, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PollVotesAlert.this.m2872lambda$new$2$orgtelegramuiComponentsPollVotesAlert(votesList, response, parentFragment);
            }
        });
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PollVotesAlert */
    public /* synthetic */ void m2872lambda$new$2$orgtelegramuiComponentsPollVotesAlert(VotesList votesList, TLObject response, ChatActivity parentFragment) {
        if (!isShowing()) {
            return;
        }
        this.loadingMore.remove(votesList);
        if (response != null) {
            TLRPC.TL_messages_votesList res = (TLRPC.TL_messages_votesList) response;
            parentFragment.getMessagesController().putUsers(res.users, false);
            votesList.votes.addAll(res.votes);
            votesList.next_offset = res.next_offset;
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
            return -1000;
        }
        return -1000;
    }

    private void updateButtons() {
        this.votesPercents.clear();
        int restPercent = 100;
        boolean hasDifferent = false;
        int previousPercent = 0;
        TLRPC.TL_messageMediaPoll media = (TLRPC.TL_messageMediaPoll) this.messageObject.messageOwner.media;
        ArrayList<Button> sortedPollButtons = new ArrayList<>();
        int maxVote = 0;
        int N = this.voters.size();
        for (int a = 0; a < N; a++) {
            VotesList list = this.voters.get(a);
            Button button = new Button();
            sortedPollButtons.add(button);
            this.votesPercents.put(list, button);
            if (!media.results.results.isEmpty()) {
                int b = 0;
                int N2 = media.results.results.size();
                while (true) {
                    if (b < N2) {
                        TLRPC.TL_pollAnswerVoters answer = media.results.results.get(b);
                        if (Arrays.equals(list.option, answer.option)) {
                            button.votesCount = answer.voters;
                            button.decimal = (answer.voters / media.results.total_voters) * 100.0f;
                            button.percent = (int) button.decimal;
                            Button.access$3924(button, button.percent);
                            if (previousPercent == 0) {
                                previousPercent = button.percent;
                            } else if (button.percent != 0 && previousPercent != button.percent) {
                                hasDifferent = true;
                            }
                            restPercent -= button.percent;
                            maxVote = Math.max(button.percent, maxVote);
                        } else {
                            b++;
                        }
                    }
                }
            }
        }
        if (hasDifferent && restPercent != 0) {
            Collections.sort(sortedPollButtons, PollVotesAlert$$ExternalSyntheticLambda2.INSTANCE);
            int N3 = Math.min(restPercent, sortedPollButtons.size());
            for (int a2 = 0; a2 < N3; a2++) {
                Button.access$4012(sortedPollButtons.get(a2), 1);
            }
        }
    }

    public static /* synthetic */ int lambda$updateButtons$5(Button o1, Button o2) {
        if (o1.decimal <= o2.decimal) {
            if (o1.decimal < o2.decimal) {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        int N = this.queries.size();
        for (int a = 0; a < N; a++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.queries.get(a).intValue(), true);
        }
        super.dismissInternal();
    }

    public void updateLayout(boolean animated) {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        boolean show = newOffset <= AndroidUtilities.dp(12.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PollVotesAlert.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int newOffset2 = newOffset + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    public void updatePlaceholder() {
        if (this.placeholderPaint == null) {
            return;
        }
        int color0 = Theme.getColor(Theme.key_dialogBackground);
        int color1 = Theme.getColor(Theme.key_dialogBackgroundGray);
        int color02 = AndroidUtilities.getAverageColor(color1, color0);
        this.placeholderPaint.setColor(color1);
        float dp = AndroidUtilities.dp(500.0f);
        this.gradientWidth = dp;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color1, color02, color1}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
        this.placeholderGradient = linearGradient;
        this.placeholderPaint.setShader(linearGradient);
        Matrix matrix = new Matrix();
        this.placeholderMatrix = matrix;
        this.placeholderGradient.setLocalMatrix(matrix);
    }

    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public Adapter(Context context) {
            PollVotesAlert.this = this$0;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (section != 0 && row != 0) {
                if (PollVotesAlert.this.queries != null && !PollVotesAlert.this.queries.isEmpty()) {
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            return PollVotesAlert.this.voters.size() + 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            int i = 1;
            if (section != 0) {
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                int count = votesList.getCount() + 1;
                if (TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    i = 0;
                }
                return count + i;
            }
            return 1;
        }

        private SectionCell createSectionCell() {
            return new SectionCell(this.mContext) { // from class: org.telegram.ui.Components.PollVotesAlert.Adapter.1
                {
                    Adapter.this = this;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                }

                @Override // org.telegram.ui.Components.PollVotesAlert.SectionCell
                protected void onCollapseClick() {
                    VotesList list = (VotesList) getTag(R.id.object_tag);
                    if (list.votes.size() <= 15) {
                        return;
                    }
                    list.collapsed = !list.collapsed;
                    if (list.collapsed) {
                        list.collapsedCount = 10;
                    }
                    PollVotesAlert.this.listAdapter.notifyDataSetChanged();
                }
            };
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = createSectionCell();
            }
            SectionCell sectionCell = (SectionCell) view;
            if (section == 0) {
                sectionCell.setAlpha(0.0f);
            } else {
                view.setAlpha(1.0f);
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                votesList.votes.get(0);
                int a = 0;
                int N = PollVotesAlert.this.poll.answers.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_pollAnswer answer = PollVotesAlert.this.poll.answers.get(a);
                    if (Arrays.equals(answer.option, votesList.option)) {
                        Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                        sectionCell.setText(answer.text, button.percent, button.votesCount, votesList.getCollapsed());
                        sectionCell.setTag(R.id.object_tag, votesList);
                        break;
                    }
                    a++;
                }
            }
            return view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext);
                    break;
                case 1:
                    if (PollVotesAlert.this.titleTextView.getParent() != null) {
                        ViewGroup p = (ViewGroup) PollVotesAlert.this.titleTextView.getParent();
                        p.removeView(PollVotesAlert.this.titleTextView);
                    }
                    view = PollVotesAlert.this.titleTextView;
                    break;
                case 2:
                    view = createSectionCell();
                    break;
                default:
                    TextCell textCell = new TextCell(this.mContext, 23, true);
                    textCell.setOffsetFromImage(65);
                    textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                    view = textCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 2:
                    SectionCell sectionCell = (SectionCell) holder.itemView;
                    VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                    votesList.votes.get(0);
                    int N = PollVotesAlert.this.poll.answers.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_pollAnswer answer = PollVotesAlert.this.poll.answers.get(a);
                        if (Arrays.equals(answer.option, votesList.option)) {
                            Button button = (Button) PollVotesAlert.this.votesPercents.get(votesList);
                            sectionCell.setText(answer.text, button.percent, button.votesCount, votesList.getCollapsed());
                            sectionCell.setTag(R.id.object_tag, votesList);
                            return;
                        }
                    }
                    return;
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    VotesList votesList2 = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                    textCell.setTextAndIcon(LocaleController.formatPluralString("ShowVotes", votesList2.count - votesList2.getCount(), new Object[0]), R.drawable.arrow_more, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            TLRPC.User user;
            if (holder.getItemViewType() == 0) {
                int position = holder.getAdapterPosition();
                int section = getSectionForPosition(position);
                int position2 = getPositionInSectionForPosition(position) - 1;
                UserCell userCell = (UserCell) holder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
                TLRPC.MessageUserVote vote = votesList.votes.get(position2);
                if (vote.user_id != 0) {
                    user = PollVotesAlert.this.chatActivity.getMessagesController().getUser(Long.valueOf(vote.user_id));
                } else {
                    user = null;
                }
                boolean z = true;
                if (position2 == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(user, position2, z);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (position == 0) {
                return 2;
            }
            int position2 = position - 1;
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(section - 1);
            if (position2 < votesList.getCount()) {
                return 0;
            }
            return 3;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                PollVotesAlert.this.updatePlaceholder();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_player_actionBarSubtitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, Theme.key_dialogBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        return themeDescriptions;
    }
}
