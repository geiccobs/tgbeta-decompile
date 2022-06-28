package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes5.dex */
public class GroupCallFullscreenAdapter extends RecyclerListView.SelectionAdapter {
    private final GroupCallActivity activity;
    private ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final int currentAccount;
    private ChatObject.Call groupCall;
    private GroupCallRenderersContainer renderersContainer;
    private final ArrayList<ChatObject.VideoParticipant> videoParticipants = new ArrayList<>();
    private final ArrayList<TLRPC.TL_groupCallParticipant> participants = new ArrayList<>();
    private boolean visible = false;

    public GroupCallFullscreenAdapter(ChatObject.Call groupCall, int currentAccount, GroupCallActivity activity) {
        this.groupCall = groupCall;
        this.currentAccount = currentAccount;
        this.activity = activity;
    }

    public void setRenderersPool(ArrayList<GroupCallMiniTextureView> attachedRenderers, GroupCallRenderersContainer renderersContainer) {
        this.attachedRenderers = attachedRenderers;
        this.renderersContainer = renderersContainer;
    }

    public void setGroupCall(ChatObject.Call groupCall) {
        this.groupCall = groupCall;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new GroupCallUserCell(parent.getContext()));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TLRPC.TL_groupCallParticipant participant;
        ChatObject.VideoParticipant videoParticipant;
        GroupCallUserCell view = (GroupCallUserCell) holder.itemView;
        ChatObject.VideoParticipant oldVideoParticipant = view.videoParticipant;
        if (position < this.videoParticipants.size()) {
            videoParticipant = this.videoParticipants.get(position);
            participant = this.videoParticipants.get(position).participant;
        } else if (position - this.videoParticipants.size() < this.participants.size()) {
            videoParticipant = null;
            participant = this.participants.get(position - this.videoParticipants.size());
        } else {
            return;
        }
        view.setParticipant(videoParticipant, participant);
        if (oldVideoParticipant != null && !oldVideoParticipant.equals(videoParticipant) && view.attached && view.getRenderer() != null) {
            view.attachRenderer(false);
            if (videoParticipant != null) {
                view.attachRenderer(true);
            }
        } else if (view.attached) {
            if (view.getRenderer() == null && videoParticipant != null && this.visible) {
                view.attachRenderer(true);
            } else if (view.getRenderer() != null && videoParticipant == null) {
                view.attachRenderer(false);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.videoParticipants.size() + this.participants.size();
    }

    public void setVisibility(RecyclerListView listView, boolean visibility) {
        this.visible = visibility;
        for (int i = 0; i < listView.getChildCount(); i++) {
            View view = listView.getChildAt(i);
            if (view instanceof GroupCallUserCell) {
                GroupCallUserCell cell = (GroupCallUserCell) view;
                if (cell.getVideoParticipant() != null) {
                    ((GroupCallUserCell) view).attachRenderer(visibility);
                }
            }
        }
    }

    public void scrollTo(ChatObject.VideoParticipant videoParticipant, RecyclerListView fullscreenUsersListView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) fullscreenUsersListView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        for (int i = 0; i < this.videoParticipants.size(); i++) {
            if (this.videoParticipants.get(i).equals(videoParticipant)) {
                layoutManager.scrollToPositionWithOffset(i, AndroidUtilities.dp(13.0f));
                return;
            }
        }
    }

    /* loaded from: classes5.dex */
    public class GroupCallUserCell extends FrameLayout implements GroupCallStatusIcon.Callback {
        boolean attached;
        private BackupImageView avatarImageView;
        ValueAnimator colorAnimator;
        private TLRPC.Chat currentChat;
        private TLRPC.User currentUser;
        String drawingName;
        boolean hasAvatar;
        int lastColor;
        private boolean lastMuted;
        private boolean lastRaisedHand;
        int lastWavesColor;
        RLottieImageView muteButton;
        String name;
        int nameWidth;
        TLRPC.TL_groupCallParticipant participant;
        long peerId;
        GroupCallMiniTextureView renderer;
        boolean selected;
        float selectionProgress;
        boolean skipInvalidate;
        GroupCallStatusIcon statusIcon;
        ChatObject.VideoParticipant videoParticipant;
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        Paint backgroundPaint = new Paint(1);
        Paint selectionPaint = new Paint(1);
        float progress = 1.0f;
        TextPaint textPaint = new TextPaint(1);
        GroupCallUserCell.AvatarWavesDrawable avatarWavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GroupCallUserCell(Context context) {
            super(context);
            GroupCallFullscreenAdapter.this = this$0;
            this.avatarDrawable.setTextSize((int) (AndroidUtilities.dp(18.0f) / 1.15f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, 1, 0.0f, 9.0f, 0.0f, 9.0f));
            setWillNotDraw(false);
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_voipgroup_listViewBackground));
            this.selectionPaint.setColor(Theme.getColor(Theme.key_voipgroup_speakingText));
            this.selectionPaint.setStyle(Paint.Style.STROKE);
            this.selectionPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.textPaint.setColor(-1);
            RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.1
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    GroupCallUserCell.this.invalidate();
                }
            };
            this.muteButton = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.muteButton, LayoutHelper.createFrame(24, 24.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            if (this.name != null) {
                float maxWidth = AndroidUtilities.dp(46.0f);
                float textWidth = this.textPaint.measureText(this.name);
                int min = (int) Math.min(maxWidth, textWidth);
                this.nameWidth = min;
                this.drawingName = TextUtils.ellipsize(this.name, this.textPaint, min, TextUtils.TruncateAt.END).toString();
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setParticipant(ChatObject.VideoParticipant videoParticipant, TLRPC.TL_groupCallParticipant participant) {
            this.videoParticipant = videoParticipant;
            this.participant = participant;
            long lastPeerId = this.peerId;
            long peerId = MessageObject.getPeerId(participant.peer);
            this.peerId = peerId;
            boolean z = false;
            if (peerId > 0) {
                TLRPC.User user = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getUser(Long.valueOf(this.peerId));
                this.currentUser = user;
                this.currentChat = null;
                this.avatarDrawable.setInfo(user);
                this.name = UserObject.getFirstName(this.currentUser);
                this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                ImageLocation imageLocation = ImageLocation.getForUser(this.currentUser, 1);
                this.hasAvatar = imageLocation != null;
                this.avatarImageView.setImage(imageLocation, "50_50", this.avatarDrawable, this.currentUser);
            } else {
                TLRPC.Chat chat = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getChat(Long.valueOf(-this.peerId));
                this.currentChat = chat;
                this.currentUser = null;
                this.avatarDrawable.setInfo(chat);
                TLRPC.Chat chat2 = this.currentChat;
                if (chat2 != null) {
                    this.name = chat2.title;
                    this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                    ImageLocation imageLocation2 = ImageLocation.getForChat(this.currentChat, 1);
                    this.hasAvatar = imageLocation2 != null;
                    this.avatarImageView.setImage(imageLocation2, "50_50", this.avatarDrawable, this.currentChat);
                }
            }
            boolean animated = lastPeerId == this.peerId;
            if (videoParticipant == null) {
                if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenPeerId == MessageObject.getPeerId(participant.peer)) {
                    z = true;
                }
                this.selected = z;
            } else if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant != null) {
                this.selected = GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant.equals(videoParticipant);
            } else {
                this.selected = false;
            }
            if (!animated) {
                setSelectedProgress(this.selected ? 1.0f : 0.0f);
            }
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setParticipant(participant, animated);
                updateState(animated);
            }
        }

        @Override // android.view.View
        public void setAlpha(float alpha) {
            super.setAlpha(alpha);
        }

        public void setProgressToFullscreen(float progress) {
            if (this.progress == progress) {
                return;
            }
            this.progress = progress;
            if (progress == 1.0f) {
                this.avatarImageView.setTranslationY(0.0f);
                this.avatarImageView.setScaleX(1.0f);
                this.avatarImageView.setScaleY(1.0f);
                this.backgroundPaint.setAlpha(255);
                invalidate();
                GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.invalidate();
                    return;
                }
                return;
            }
            float moveToCenter = (this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2.0f)) - (getMeasuredHeight() / 2.0f);
            float scaleFrom = AndroidUtilities.dp(46.0f) / AndroidUtilities.dp(40.0f);
            float s = ((1.0f - progress) * scaleFrom) + (progress * 1.0f);
            this.avatarImageView.setTranslationY((-moveToCenter) * (1.0f - progress));
            this.avatarImageView.setScaleX(s);
            this.avatarImageView.setScaleY(s);
            this.backgroundPaint.setAlpha((int) (255.0f * progress));
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView2 = this.renderer;
            if (groupCallMiniTextureView2 != null) {
                groupCallMiniTextureView2.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            if (groupCallMiniTextureView != null && groupCallMiniTextureView.isFullyVisible() && !GroupCallFullscreenAdapter.this.activity.drawingForBlur) {
                drawSelection(canvas);
                return;
            }
            if (this.progress > 0.0f) {
                float p = (getMeasuredWidth() / 2.0f) * (1.0f - this.progress);
                AndroidUtilities.rectTmp.set(p, p, getMeasuredWidth() - p, getMeasuredHeight() - p);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), this.backgroundPaint);
                drawSelection(canvas);
            }
            float cx = this.avatarImageView.getX() + (this.avatarImageView.getMeasuredWidth() / 2);
            float cy = this.avatarImageView.getY() + (this.avatarImageView.getMeasuredHeight() / 2);
            this.avatarWavesDrawable.update();
            this.avatarWavesDrawable.draw(canvas, cx, cy, this);
            float scaleFrom = AndroidUtilities.dp(46.0f) / AndroidUtilities.dp(40.0f);
            float f = this.progress;
            float s = ((1.0f - f) * scaleFrom) + (f * 1.0f);
            this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale() * s);
            this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale() * s);
            super.dispatchDraw(canvas);
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x003c  */
        /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void drawSelection(android.graphics.Canvas r7) {
            /*
                r6 = this;
                boolean r0 = r6.selected
                r1 = 1037726734(0x3dda740e, float:0.10666667)
                r2 = 0
                r3 = 1065353216(0x3f800000, float:1.0)
                if (r0 == 0) goto L1f
                float r4 = r6.selectionProgress
                int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
                if (r5 == 0) goto L1f
                float r4 = r4 + r1
                int r0 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
                if (r0 <= 0) goto L18
                r4 = 1065353216(0x3f800000, float:1.0)
                goto L1b
            L18:
                r6.invalidate()
            L1b:
                r6.setSelectedProgress(r4)
                goto L35
            L1f:
                if (r0 != 0) goto L35
                float r0 = r6.selectionProgress
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 == 0) goto L35
                float r0 = r0 - r1
                int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r1 >= 0) goto L2e
                r0 = 0
                goto L31
            L2e:
                r6.invalidate()
            L31:
                r6.setSelectedProgress(r0)
                goto L36
            L35:
            L36:
                float r0 = r6.selectionProgress
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L80
                int r0 = r6.getMeasuredWidth()
                float r0 = (float) r0
                r1 = 1073741824(0x40000000, float:2.0)
                float r0 = r0 / r1
                float r2 = r6.progress
                float r3 = r3 - r2
                float r0 = r0 * r3
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                int r3 = r6.getMeasuredWidth()
                float r3 = (float) r3
                float r3 = r3 - r0
                int r4 = r6.getMeasuredHeight()
                float r4 = (float) r4
                float r4 = r4 - r0
                r2.set(r0, r0, r3, r4)
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                android.graphics.Paint r3 = r6.selectionPaint
                float r3 = r3.getStrokeWidth()
                float r3 = r3 / r1
                android.graphics.Paint r4 = r6.selectionPaint
                float r4 = r4.getStrokeWidth()
                float r4 = r4 / r1
                r2.inset(r3, r4)
                android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
                r2 = 1094713344(0x41400000, float:12.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r3 = (float) r3
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Paint r4 = r6.selectionPaint
                r7.drawRoundRect(r1, r3, r2, r4)
            L80:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.drawSelection(android.graphics.Canvas):void");
        }

        private void setSelectedProgress(float p) {
            if (this.selectionProgress != p) {
                this.selectionProgress = p;
                this.selectionPaint.setAlpha((int) (255.0f * p));
            }
        }

        public long getPeerId() {
            return this.peerId;
        }

        public BackupImageView getAvatarImageView() {
            return this.avatarImageView;
        }

        public TLRPC.TL_groupCallParticipant getParticipant() {
            return this.participant;
        }

        public ChatObject.VideoParticipant getVideoParticipant() {
            return this.videoParticipant;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (GroupCallFullscreenAdapter.this.visible && this.videoParticipant != null) {
                attachRenderer(true);
            }
            this.attached = true;
            if (GroupCallFullscreenAdapter.this.activity.statusIconPool.size() > 0) {
                this.statusIcon = GroupCallFullscreenAdapter.this.activity.statusIconPool.remove(GroupCallFullscreenAdapter.this.activity.statusIconPool.size() - 1);
            } else {
                this.statusIcon = new GroupCallStatusIcon();
            }
            this.statusIcon.setCallback(this);
            this.statusIcon.setImageView(this.muteButton);
            this.statusIcon.setParticipant(this.participant, false);
            updateState(false);
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            if (!this.statusIcon.isSpeaking()) {
                this.avatarWavesDrawable.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            attachRenderer(false);
            this.attached = false;
            if (this.statusIcon != null) {
                GroupCallFullscreenAdapter.this.activity.statusIconPool.add(this.statusIcon);
                this.statusIcon.setImageView(null);
                this.statusIcon.setCallback(null);
            }
            this.statusIcon = null;
        }

        public void attachRenderer(boolean attach) {
            if (GroupCallFullscreenAdapter.this.activity.isDismissed()) {
                return;
            }
            if (attach && this.renderer == null) {
                this.renderer = GroupCallMiniTextureView.getOrCreate(GroupCallFullscreenAdapter.this.attachedRenderers, GroupCallFullscreenAdapter.this.renderersContainer, null, this, null, this.videoParticipant, GroupCallFullscreenAdapter.this.groupCall, GroupCallFullscreenAdapter.this.activity);
            } else if (!attach) {
                GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.setSecondaryView(null);
                }
                this.renderer = null;
            }
        }

        public void setRenderer(GroupCallMiniTextureView renderer) {
            this.renderer = renderer;
        }

        public void drawOverlays(Canvas canvas) {
            if (this.drawingName != null) {
                canvas.save();
                int paddingStart = ((getMeasuredWidth() - this.nameWidth) - AndroidUtilities.dp(24.0f)) / 2;
                this.textPaint.setAlpha((int) (this.progress * 255.0f * getAlpha()));
                canvas.drawText(this.drawingName, AndroidUtilities.dp(22.0f) + paddingStart, AndroidUtilities.dp(69.0f), this.textPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(paddingStart, AndroidUtilities.dp(53.0f));
                if (this.muteButton.getDrawable() != null) {
                    this.muteButton.getDrawable().setAlpha((int) (this.progress * 255.0f * getAlpha()));
                    this.muteButton.draw(canvas);
                    this.muteButton.getDrawable().setAlpha(255);
                }
                canvas.restore();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == this.muteButton) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        public float getProgressToFullscreen() {
            return this.progress;
        }

        public GroupCallMiniTextureView getRenderer() {
            return this.renderer;
        }

        public void setAmplitude(double value) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setAmplitude(value);
            }
            this.avatarWavesDrawable.setAmplitude(value);
        }

        public void updateState(boolean animated) {
            final int newColor;
            final int newWavesColor;
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon == null) {
                return;
            }
            groupCallStatusIcon.updateIcon(animated);
            if (this.statusIcon.isMutedByMe()) {
                newWavesColor = Theme.getColor(Theme.key_voipgroup_mutedByAdminIcon);
                newColor = newWavesColor;
            } else if (this.statusIcon.isSpeaking()) {
                newWavesColor = Theme.getColor(Theme.key_voipgroup_speakingText);
                newColor = newWavesColor;
            } else {
                newColor = Theme.getColor(Theme.key_voipgroup_nameText);
                newWavesColor = Theme.getColor(Theme.key_voipgroup_listeningText);
            }
            if (!animated) {
                ValueAnimator valueAnimator = this.colorAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.colorAnimator.cancel();
                }
                this.lastColor = newColor;
                this.lastWavesColor = newWavesColor;
                this.muteButton.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.MULTIPLY));
                this.textPaint.setColor(this.lastColor);
                this.selectionPaint.setColor(newWavesColor);
                this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(newWavesColor, 38));
                invalidate();
                return;
            }
            final int colorFrom = this.lastColor;
            final int colorWavesFrom = this.lastWavesColor;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.colorAnimator = ofFloat;
            final int i = newColor;
            final int i2 = newWavesColor;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCallFullscreenAdapter.GroupCallUserCell.this.m2654x4c7d1511(colorFrom, i, colorWavesFrom, i2, valueAnimator2);
                }
            });
            this.colorAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallUserCell.this.lastColor = newColor;
                    GroupCallUserCell.this.lastWavesColor = newWavesColor;
                    GroupCallUserCell.this.muteButton.setColorFilter(new PorterDuffColorFilter(GroupCallUserCell.this.lastColor, PorterDuff.Mode.MULTIPLY));
                    GroupCallUserCell.this.textPaint.setColor(GroupCallUserCell.this.lastColor);
                    GroupCallUserCell.this.selectionPaint.setColor(GroupCallUserCell.this.lastWavesColor);
                    GroupCallUserCell.this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(GroupCallUserCell.this.lastWavesColor, 38));
                }
            });
            this.colorAnimator.start();
        }

        /* renamed from: lambda$updateState$0$org-telegram-ui-Components-GroupCallFullscreenAdapter$GroupCallUserCell */
        public /* synthetic */ void m2654x4c7d1511(int colorFrom, int newColor, int colorWavesFrom, int newWavesColor, ValueAnimator valueAnimator) {
            this.lastColor = ColorUtils.blendARGB(colorFrom, newColor, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.lastWavesColor = ColorUtils.blendARGB(colorWavesFrom, newWavesColor, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.muteButton.setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.MULTIPLY));
            this.textPaint.setColor(this.lastColor);
            this.selectionPaint.setColor(this.lastWavesColor);
            this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(this.lastWavesColor, 38));
            invalidate();
        }

        @Override // android.view.View
        public void invalidate() {
            if (this.skipInvalidate) {
                return;
            }
            this.skipInvalidate = true;
            super.invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            if (groupCallMiniTextureView == null) {
                GroupCallFullscreenAdapter.this.renderersContainer.invalidate();
            } else {
                groupCallMiniTextureView.invalidate();
            }
            this.skipInvalidate = false;
        }

        public boolean hasImage() {
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            return groupCallMiniTextureView != null && groupCallMiniTextureView.hasImage();
        }

        @Override // org.telegram.ui.Components.voip.GroupCallStatusIcon.Callback
        public void onStatusChanged() {
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            updateState(true);
        }

        public boolean isRemoving(RecyclerListView listView) {
            return listView.getChildAdapterPosition(this) == -1;
        }
    }

    public void update(boolean animated, RecyclerListView listView) {
        if (this.groupCall == null) {
            return;
        }
        if (animated) {
            final ArrayList<TLRPC.TL_groupCallParticipant> oldParticipants = new ArrayList<>(this.participants);
            final ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>(this.videoParticipants);
            this.participants.clear();
            if (!this.groupCall.call.rtmp_stream) {
                this.participants.addAll(this.groupCall.visibleParticipants);
            }
            this.videoParticipants.clear();
            if (!this.groupCall.call.rtmp_stream) {
                this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
            }
            DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.Components.GroupCallFullscreenAdapter.1
                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getOldListSize() {
                    return oldVideoParticipants.size() + oldParticipants.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getNewListSize() {
                    return GroupCallFullscreenAdapter.this.videoParticipants.size() + GroupCallFullscreenAdapter.this.participants.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    TLRPC.TL_groupCallParticipant oldParticipant;
                    if (oldItemPosition < oldVideoParticipants.size() && newItemPosition < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                        return ((ChatObject.VideoParticipant) oldVideoParticipants.get(oldItemPosition)).equals(GroupCallFullscreenAdapter.this.videoParticipants.get(newItemPosition));
                    }
                    int oldItemPosition2 = oldItemPosition - oldVideoParticipants.size();
                    int newItemPosition2 = newItemPosition - GroupCallFullscreenAdapter.this.videoParticipants.size();
                    if (newItemPosition2 >= 0 && newItemPosition2 < GroupCallFullscreenAdapter.this.participants.size() && oldItemPosition2 >= 0 && oldItemPosition2 < oldParticipants.size()) {
                        return MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) oldParticipants.get(oldItemPosition2)).peer) == MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(newItemPosition2)).peer);
                    }
                    if (oldItemPosition < oldVideoParticipants.size()) {
                        oldParticipant = ((ChatObject.VideoParticipant) oldVideoParticipants.get(oldItemPosition)).participant;
                    } else {
                        oldParticipant = (TLRPC.TL_groupCallParticipant) oldParticipants.get(oldItemPosition2);
                    }
                    TLRPC.TL_groupCallParticipant newParticipant = newItemPosition < GroupCallFullscreenAdapter.this.videoParticipants.size() ? ((ChatObject.VideoParticipant) GroupCallFullscreenAdapter.this.videoParticipants.get(newItemPosition)).participant : (TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(newItemPosition2);
                    return MessageObject.getPeerId(oldParticipant.peer) == MessageObject.getPeerId(newParticipant.peer);
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return true;
                }
            }).dispatchUpdatesTo(this);
            AndroidUtilities.updateVisibleRows(listView);
            return;
        }
        this.participants.clear();
        if (!this.groupCall.call.rtmp_stream) {
            this.participants.addAll(this.groupCall.visibleParticipants);
        }
        this.videoParticipants.clear();
        if (!this.groupCall.call.rtmp_stream) {
            this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
        }
        notifyDataSetChanged();
    }
}
