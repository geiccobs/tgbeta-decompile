package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatGreetingsView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.TextMessageEnterTransition;
import org.telegram.ui.VoiceMessageEnterTransition;
/* loaded from: classes.dex */
public class ChatListItemAnimator extends DefaultItemAnimator {
    public static final Interpolator DEFAULT_INTERPOLATOR = new CubicBezierInterpolator(0.19919472913616398d, 0.010644531250000006d, 0.27920937042459737d, 0.91025390625d);
    private final ChatActivity activity;
    private ChatGreetingsView chatGreetingsView;
    private RecyclerView.ViewHolder greetingsSticker;
    private final RecyclerListView recyclerListView;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean reversePositions;
    private boolean shouldAnimateEnterFromBottom;
    private HashMap<Integer, MessageObject.GroupedMessages> willRemovedGroup = new HashMap<>();
    private ArrayList<MessageObject.GroupedMessages> willChangedGroups = new ArrayList<>();
    HashMap<RecyclerView.ViewHolder, Animator> animators = new HashMap<>();
    ArrayList<Runnable> runOnAnimationsEnd = new ArrayList<>();
    HashMap<Long, Long> groupIdToEnterDelay = new HashMap<>();

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getChangeDuration() {
        return 250L;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    protected long getMoveAnimationDelay() {
        return 0L;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getMoveDuration() {
        return 250L;
    }

    public void onAnimationStart() {
    }

    public ChatListItemAnimator(ChatActivity chatActivity, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.activity = chatActivity;
        this.recyclerListView = recyclerListView;
        this.translationInterpolator = DEFAULT_INTERPOLATOR;
        this.alwaysCreateMoveAnimationIfPossible = true;
        setSupportsChangeAnimations(false);
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void runPendingAnimations() {
        boolean z = !this.mPendingRemovals.isEmpty();
        boolean z2 = !this.mPendingMoves.isEmpty();
        boolean z3 = !this.mPendingChanges.isEmpty();
        boolean z4 = !this.mPendingAdditions.isEmpty();
        if (z || z2 || z4 || z3) {
            boolean z5 = false;
            if (this.shouldAnimateEnterFromBottom) {
                boolean z6 = false;
                for (int i = 0; i < this.mPendingAdditions.size(); i++) {
                    if (this.reversePositions) {
                        if (this.mPendingAdditions.get(i).getLayoutPosition() != (this.recyclerListView.getAdapter() == null ? 0 : this.recyclerListView.getAdapter().getItemCount()) - 1) {
                        }
                        z6 = true;
                    } else {
                        if (this.mPendingAdditions.get(i).getLayoutPosition() != 0) {
                        }
                        z6 = true;
                    }
                }
                z5 = z6;
            }
            onAnimationStart();
            if (z5) {
                runMessageEnterTransition();
            } else {
                runAlphaEnterTransition();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: androidx.recyclerview.widget.ChatListItemAnimator$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatListItemAnimator.this.lambda$runPendingAnimations$0(valueAnimator);
                }
            });
            ofFloat.setDuration(getRemoveDuration() + getMoveDuration());
            ofFloat.start();
        }
    }

    public /* synthetic */ void lambda$runPendingAnimations$0(ValueAnimator valueAnimator) {
        ChatActivity chatActivity = this.activity;
        if (chatActivity != null) {
            chatActivity.onListItemAnimatorTick();
        } else {
            this.recyclerListView.invalidate();
        }
    }

    private void runAlphaEnterTransition() {
        boolean z = !this.mPendingRemovals.isEmpty();
        boolean z2 = !this.mPendingMoves.isEmpty();
        boolean z3 = !this.mPendingChanges.isEmpty();
        boolean z4 = !this.mPendingAdditions.isEmpty();
        if (z || z2 || z4 || z3) {
            Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl(it.next());
            }
            this.mPendingRemovals.clear();
            if (z2) {
                final ArrayList<DefaultItemAnimator.MoveInfo> arrayList = new ArrayList<>();
                arrayList.addAll(this.mPendingMoves);
                this.mMovesList.add(arrayList);
                this.mPendingMoves.clear();
                Runnable runnable = new Runnable() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Iterator it2 = arrayList.iterator();
                        while (it2.hasNext()) {
                            DefaultItemAnimator.MoveInfo moveInfo = (DefaultItemAnimator.MoveInfo) it2.next();
                            ChatListItemAnimator.this.animateMoveImpl(moveInfo.holder, moveInfo);
                        }
                        arrayList.clear();
                        ChatListItemAnimator.this.mMovesList.remove(arrayList);
                    }
                };
                if (this.delayAnimations && z) {
                    ViewCompat.postOnAnimationDelayed(arrayList.get(0).holder.itemView, runnable, getMoveAnimationDelay());
                } else {
                    runnable.run();
                }
            }
            if (z3) {
                final ArrayList<DefaultItemAnimator.ChangeInfo> arrayList2 = new ArrayList<>();
                arrayList2.addAll(this.mPendingChanges);
                this.mChangesList.add(arrayList2);
                this.mPendingChanges.clear();
                Runnable runnable2 = new Runnable() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.2
                    @Override // java.lang.Runnable
                    public void run() {
                        Iterator it2 = arrayList2.iterator();
                        while (it2.hasNext()) {
                            ChatListItemAnimator.this.animateChangeImpl((DefaultItemAnimator.ChangeInfo) it2.next());
                        }
                        arrayList2.clear();
                        ChatListItemAnimator.this.mChangesList.remove(arrayList2);
                    }
                };
                if (this.delayAnimations && z) {
                    ViewCompat.postOnAnimationDelayed(arrayList2.get(0).oldHolder.itemView, runnable2, 0L);
                } else {
                    runnable2.run();
                }
            }
            if (!z4) {
                return;
            }
            ArrayList arrayList3 = new ArrayList();
            arrayList3.addAll(this.mPendingAdditions);
            this.mPendingAdditions.clear();
            Collections.sort(arrayList3, ChatListItemAnimator$$ExternalSyntheticLambda7.INSTANCE);
            Iterator it2 = arrayList3.iterator();
            while (it2.hasNext()) {
                animateAddImpl((RecyclerView.ViewHolder) it2.next());
            }
            arrayList3.clear();
        }
    }

    public static /* synthetic */ int lambda$runAlphaEnterTransition$1(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        return viewHolder2.itemView.getTop() - viewHolder.itemView.getTop();
    }

    private void runMessageEnterTransition() {
        boolean z = !this.mPendingRemovals.isEmpty();
        boolean z2 = !this.mPendingMoves.isEmpty();
        boolean z3 = !this.mPendingChanges.isEmpty();
        boolean z4 = !this.mPendingAdditions.isEmpty();
        if (z || z2 || z4 || z3) {
            int i = 0;
            for (int i2 = 0; i2 < this.mPendingAdditions.size(); i2++) {
                View view = this.mPendingAdditions.get(i2).itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    if (chatMessageCell.getCurrentPosition() != null && (chatMessageCell.getCurrentPosition().flags & 1) == 0) {
                    }
                }
                i += this.mPendingAdditions.get(i2).itemView.getHeight();
            }
            Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl(it.next());
            }
            this.mPendingRemovals.clear();
            if (z2) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.mPendingMoves);
                this.mPendingMoves.clear();
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    DefaultItemAnimator.MoveInfo moveInfo = (DefaultItemAnimator.MoveInfo) it2.next();
                    animateMoveImpl(moveInfo.holder, moveInfo);
                }
                arrayList.clear();
            }
            if (!z4) {
                return;
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.addAll(this.mPendingAdditions);
            this.mPendingAdditions.clear();
            Iterator it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                animateAddImpl((RecyclerView.ViewHolder) it3.next(), i);
            }
            arrayList2.clear();
        }
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        int i;
        boolean animateAppearance = super.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
        if (animateAppearance && this.shouldAnimateEnterFromBottom) {
            boolean z = false;
            for (int i2 = 0; i2 < this.mPendingAdditions.size(); i2++) {
                if (this.mPendingAdditions.get(i2).getLayoutPosition() == 0) {
                    z = true;
                }
            }
            if (z) {
                i = 0;
                for (int i3 = 0; i3 < this.mPendingAdditions.size(); i3++) {
                    i += this.mPendingAdditions.get(i3).itemView.getHeight();
                }
            } else {
                i = 0;
            }
            for (int i4 = 0; i4 < this.mPendingAdditions.size(); i4++) {
                this.mPendingAdditions.get(i4).itemView.setTranslationY(i);
            }
        }
        return animateAppearance;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        viewHolder.itemView.setAlpha(0.0f);
        if (!this.shouldAnimateEnterFromBottom) {
            viewHolder.itemView.setScaleX(0.9f);
            viewHolder.itemView.setScaleY(0.9f);
        } else {
            View view = viewHolder.itemView;
            if (view instanceof ChatMessageCell) {
                ((ChatMessageCell) view).getTransitionParams().messageEntering = true;
            }
        }
        this.mPendingAdditions.add(viewHolder);
        return true;
    }

    public void animateAddImpl(final RecyclerView.ViewHolder viewHolder, int i) {
        final View view = viewHolder.itemView;
        final ViewPropertyAnimator animate = view.animate();
        this.mAddAnimations.add(viewHolder);
        view.setTranslationY(i);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
        View view2 = viewHolder.itemView;
        ChatMessageCell chatMessageCell = view2 instanceof ChatMessageCell ? (ChatMessageCell) view2 : null;
        if (chatMessageCell == null || !chatMessageCell.getTransitionParams().ignoreAlpha) {
            viewHolder.itemView.setAlpha(1.0f);
        }
        if (chatMessageCell != null && this.activity.animatingMessageObjects.contains(chatMessageCell.getMessageObject())) {
            this.activity.animatingMessageObjects.remove(chatMessageCell.getMessageObject());
            if (this.activity.getChatActivityEnterView().canShowMessageTransition()) {
                if (chatMessageCell.getMessageObject().isVoice()) {
                    if (Math.abs(view.getTranslationY()) < view.getMeasuredHeight() * 3.0f) {
                        new VoiceMessageEnterTransition(chatMessageCell, this.activity.getChatActivityEnterView(), this.recyclerListView, this.activity.messageEnterTransitionContainer, this.resourcesProvider).start();
                    }
                } else if (SharedConfig.getDevicePerformanceClass() != 0 && Math.abs(view.getTranslationY()) < this.recyclerListView.getMeasuredHeight()) {
                    ChatActivity chatActivity = this.activity;
                    new TextMessageEnterTransition(chatMessageCell, chatActivity, this.recyclerListView, chatActivity.messageEnterTransitionContainer, this.resourcesProvider).start();
                }
                this.activity.getChatActivityEnterView().startMessageTransition();
            }
        }
        animate.translationY(0.0f).setDuration(getMoveDuration()).setInterpolator(this.translationInterpolator).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                ChatListItemAnimator.this.dispatchAddStarting(viewHolder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setTranslationY(0.0f);
                View view3 = view;
                if (view3 instanceof ChatMessageCell) {
                    ((ChatMessageCell) view3).getTransitionParams().messageEntering = false;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                View view3 = view;
                if (view3 instanceof ChatMessageCell) {
                    ((ChatMessageCell) view3).getTransitionParams().messageEntering = false;
                }
                animate.setListener(null);
                if (ChatListItemAnimator.this.mAddAnimations.remove(viewHolder)) {
                    ChatListItemAnimator.this.dispatchAddFinished(viewHolder);
                    ChatListItemAnimator.this.dispatchFinishedWhenDone();
                }
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate remove");
        }
        boolean animateRemove = super.animateRemove(viewHolder, itemHolderInfo);
        if (animateRemove && itemHolderInfo != null) {
            int i = itemHolderInfo.top;
            int top = viewHolder.itemView.getTop();
            int left = viewHolder.itemView.getLeft() - itemHolderInfo.left;
            int i2 = top - i;
            if (i2 != 0) {
                viewHolder.itemView.setTranslationY(-i2);
            }
            View view = viewHolder.itemView;
            if (view instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                if (left != 0) {
                    chatMessageCell.setAnimationOffsetX(-left);
                }
                if (itemHolderInfo instanceof ItemHolderInfoExtended) {
                    ItemHolderInfoExtended itemHolderInfoExtended = (ItemHolderInfoExtended) itemHolderInfo;
                    chatMessageCell.setImageCoords(itemHolderInfoExtended.imageX, itemHolderInfoExtended.imageY, itemHolderInfoExtended.imageWidth, itemHolderInfoExtended.imageHeight);
                }
            } else if (left != 0) {
                view.setTranslationX(-left);
            }
        }
        return animateRemove;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x046c  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0099  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00ba  */
    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean animateMove(androidx.recyclerview.widget.RecyclerView.ViewHolder r24, androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemHolderInfo r25, int r26, int r27, int r28, int r29) {
        /*
            Method dump skipped, instructions count: 1179
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.ChatListItemAnimator.animateMove(androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.RecyclerView$ItemAnimator$ItemHolderInfo, int, int, int, int):boolean");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0232  */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v14 */
    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void animateMoveImpl(final androidx.recyclerview.widget.RecyclerView.ViewHolder r26, androidx.recyclerview.widget.DefaultItemAnimator.MoveInfo r27) {
        /*
            Method dump skipped, instructions count: 642
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.ChatListItemAnimator.animateMoveImpl(androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.DefaultItemAnimator$MoveInfo):void");
    }

    public static /* synthetic */ void lambda$animateMoveImpl$2(MoveInfoExtended moveInfoExtended, ChatMessageCell.TransitionParams transitionParams, boolean z, float f, float f2, ChatMessageCell chatMessageCell, int[] iArr, RecyclerView.ViewHolder viewHolder, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f3 = 1.0f - floatValue;
        float f4 = (moveInfoExtended.imageX * f3) + (transitionParams.animateToImageX * floatValue);
        float f5 = (moveInfoExtended.imageY * f3) + (transitionParams.animateToImageY * floatValue);
        float f6 = (moveInfoExtended.imageWidth * f3) + (transitionParams.animateToImageW * floatValue);
        float f7 = (moveInfoExtended.imageHeight * f3) + (transitionParams.animateToImageH * floatValue);
        if (z) {
            float f8 = (f * f3) + (f2 * floatValue);
            transitionParams.captionEnterProgress = f8;
            if (chatMessageCell.getCurrentMessagesGroup() != null) {
                chatMessageCell.getCurrentMessagesGroup().transitionParams.captionEnterProgress = f8;
            }
        }
        if (transitionParams.animateRadius) {
            ImageReceiver photoImage = chatMessageCell.getPhotoImage();
            int[] iArr2 = transitionParams.animateToRadius;
            photoImage.setRoundRadius((int) ((iArr[0] * f3) + (iArr2[0] * floatValue)), (int) ((iArr[1] * f3) + (iArr2[1] * floatValue)), (int) ((iArr[2] * f3) + (iArr2[2] * floatValue)), (int) ((iArr[3] * f3) + (iArr2[3] * floatValue)));
        }
        chatMessageCell.setImageCoords(f4, f5, f6, f7);
        viewHolder.itemView.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$3(MoveInfoExtended moveInfoExtended, ChatMessageCell.TransitionParams transitionParams, ChatMessageCell chatMessageCell, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (moveInfoExtended.animateBackgroundOnly) {
            transitionParams.deltaLeft = (-moveInfoExtended.deltaLeft) * floatValue;
            transitionParams.deltaRight = (-moveInfoExtended.deltaRight) * floatValue;
            transitionParams.deltaTop = (-moveInfoExtended.deltaTop) * floatValue;
            transitionParams.deltaBottom = (-moveInfoExtended.deltaBottom) * floatValue;
        } else {
            transitionParams.deltaLeft = ((-moveInfoExtended.deltaLeft) * floatValue) - chatMessageCell.getAnimationOffsetX();
            transitionParams.deltaRight = ((-moveInfoExtended.deltaRight) * floatValue) - chatMessageCell.getAnimationOffsetX();
            transitionParams.deltaTop = ((-moveInfoExtended.deltaTop) * floatValue) - chatMessageCell.getTranslationY();
            transitionParams.deltaBottom = ((-moveInfoExtended.deltaBottom) * floatValue) - chatMessageCell.getTranslationY();
        }
        chatMessageCell.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$4(MessageObject.GroupedMessages.TransitionParams transitionParams, MoveInfoExtended moveInfoExtended, boolean z, float f, float f2, RecyclerListView recyclerListView, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        transitionParams.offsetTop = moveInfoExtended.groupOffsetTop * floatValue;
        transitionParams.offsetBottom = moveInfoExtended.groupOffsetBottom * floatValue;
        transitionParams.offsetLeft = moveInfoExtended.groupOffsetLeft * floatValue;
        transitionParams.offsetRight = moveInfoExtended.groupOffsetRight * floatValue;
        if (z) {
            transitionParams.captionEnterProgress = (f * floatValue) + (f2 * (1.0f - floatValue));
        }
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    public static /* synthetic */ void lambda$animateMoveImpl$5(ChatMessageCell.TransitionParams transitionParams, ChatMessageCell chatMessageCell, ValueAnimator valueAnimator) {
        transitionParams.changePinnedBottomProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatMessageCell.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$6(ChatMessageCell.TransitionParams transitionParams, ChatMessageCell chatMessageCell, ValueAnimator valueAnimator) {
        transitionParams.animateChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatMessageCell.invalidate();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    public void resetAnimation(RecyclerView.ViewHolder viewHolder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("reset animation");
        }
        super.resetAnimation(viewHolder);
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, int i, int i2, int i3, int i4) {
        float f;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate change");
        }
        if (viewHolder == viewHolder2) {
            return animateMove(viewHolder, itemHolderInfo, i, i2, i3, i4);
        }
        View view = viewHolder.itemView;
        if (view instanceof ChatMessageCell) {
            f = ((ChatMessageCell) view).getAnimationOffsetX();
        } else {
            f = view.getTranslationX();
        }
        float translationY = viewHolder.itemView.getTranslationY();
        float alpha = viewHolder.itemView.getAlpha();
        resetAnimation(viewHolder);
        int i5 = (int) ((i3 - i) - f);
        int i6 = (int) ((i4 - i2) - translationY);
        View view2 = viewHolder.itemView;
        if (view2 instanceof ChatMessageCell) {
            ((ChatMessageCell) view2).setAnimationOffsetX(f);
        } else {
            view2.setTranslationX(f);
        }
        viewHolder.itemView.setTranslationY(translationY);
        viewHolder.itemView.setAlpha(alpha);
        if (viewHolder2 != null) {
            resetAnimation(viewHolder2);
            View view3 = viewHolder2.itemView;
            if (view3 instanceof ChatMessageCell) {
                ((ChatMessageCell) view3).setAnimationOffsetX(-i5);
            } else {
                view3.setTranslationX(-i5);
            }
            viewHolder2.itemView.setTranslationY(-i6);
            viewHolder2.itemView.setAlpha(0.0f);
        }
        this.mPendingChanges.add(new DefaultItemAnimator.ChangeInfo(viewHolder, viewHolder2, i, i2, i3, i4));
        checkIsRunning();
        return true;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    void animateChangeImpl(final DefaultItemAnimator.ChangeInfo changeInfo) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate change impl");
        }
        RecyclerView.ViewHolder viewHolder = changeInfo.oldHolder;
        final View view = null;
        final View view2 = viewHolder == null ? null : viewHolder.itemView;
        RecyclerView.ViewHolder viewHolder2 = changeInfo.newHolder;
        if (viewHolder2 != null) {
            view = viewHolder2.itemView;
        }
        if (view2 != null) {
            final ViewPropertyAnimator duration = view2.animate().setDuration(getChangeDuration());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            duration.translationX(changeInfo.toX - changeInfo.fromX);
            duration.translationY(changeInfo.toY - changeInfo.fromY);
            duration.alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ChatListItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    duration.setListener(null);
                    view2.setAlpha(1.0f);
                    view2.setScaleX(1.0f);
                    view2.setScaleX(1.0f);
                    View view3 = view2;
                    if (view3 instanceof ChatMessageCell) {
                        ((ChatMessageCell) view3).setAnimationOffsetX(0.0f);
                    } else {
                        view3.setTranslationX(0.0f);
                    }
                    view2.setTranslationY(0.0f);
                    if (ChatListItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder)) {
                        ChatListItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                        ChatListItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }
            }).start();
        }
        if (view != null) {
            final ViewPropertyAnimator animate = view.animate();
            this.mChangeAnimations.add(changeInfo.newHolder);
            animate.translationX(0.0f).translationY(0.0f).setDuration(getChangeDuration()).alpha(1.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ChatListItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    animate.setListener(null);
                    view.setAlpha(1.0f);
                    view.setScaleX(1.0f);
                    view.setScaleX(1.0f);
                    View view3 = view;
                    if (view3 instanceof ChatMessageCell) {
                        ((ChatMessageCell) view3).setAnimationOffsetX(0.0f);
                    } else {
                        view3.setTranslationX(0.0f);
                    }
                    view.setTranslationY(0.0f);
                    if (ChatListItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder)) {
                        ChatListItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                        ChatListItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }
            }).start();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public RecyclerView.ItemAnimator.ItemHolderInfo recordPreLayoutInformation(RecyclerView.State state, RecyclerView.ViewHolder viewHolder, int i, List<Object> list) {
        RecyclerView.ItemAnimator.ItemHolderInfo recordPreLayoutInformation = super.recordPreLayoutInformation(state, viewHolder, i, list);
        View view = viewHolder.itemView;
        if (view instanceof ChatMessageCell) {
            ItemHolderInfoExtended itemHolderInfoExtended = new ItemHolderInfoExtended(this);
            itemHolderInfoExtended.left = recordPreLayoutInformation.left;
            itemHolderInfoExtended.top = recordPreLayoutInformation.top;
            itemHolderInfoExtended.right = recordPreLayoutInformation.right;
            itemHolderInfoExtended.bottom = recordPreLayoutInformation.bottom;
            ChatMessageCell.TransitionParams transitionParams = ((ChatMessageCell) view).getTransitionParams();
            itemHolderInfoExtended.imageX = transitionParams.lastDrawingImageX;
            itemHolderInfoExtended.imageY = transitionParams.lastDrawingImageY;
            itemHolderInfoExtended.imageWidth = transitionParams.lastDrawingImageW;
            itemHolderInfoExtended.imageHeight = transitionParams.lastDrawingImageH;
            return itemHolderInfoExtended;
        }
        return recordPreLayoutInformation;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    public void onAllAnimationsDone() {
        super.onAllAnimationsDone();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("all animations done");
        }
        this.recyclerListView.setClipChildren(true);
        while (!this.runOnAnimationsEnd.isEmpty()) {
            this.runOnAnimationsEnd.remove(0).run();
        }
        cancelAnimators();
    }

    private void cancelAnimators() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cancel animations");
        }
        ArrayList arrayList = new ArrayList(this.animators.values());
        this.animators.clear();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Animator animator = (Animator) it.next();
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        Animator remove = this.animators.remove(viewHolder);
        if (remove != null) {
            remove.cancel();
        }
        super.endAnimation(viewHolder);
        restoreTransitionParams(viewHolder.itemView);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end animation");
        }
    }

    public void restoreTransitionParams(View view) {
        view.setAlpha(1.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setTranslationY(0.0f);
        if (view instanceof BotHelpCell) {
            int measuredHeight = (this.recyclerListView.getMeasuredHeight() / 2) - (view.getMeasuredHeight() / 2);
            ((BotHelpCell) view).setAnimating(false);
            if (view.getTop() > measuredHeight) {
                view.setTranslationY(measuredHeight - view.getTop());
            } else {
                view.setTranslationY(0.0f);
            }
        } else if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            chatMessageCell.getTransitionParams().resetAnimation();
            chatMessageCell.setAnimationOffsetX(0.0f);
        } else {
            view.setTranslationX(0.0f);
        }
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimations() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end animations");
        }
        Iterator<MessageObject.GroupedMessages> it = this.willChangedGroups.iterator();
        while (it.hasNext()) {
            it.next().transitionParams.isNewGroup = false;
        }
        this.willChangedGroups.clear();
        cancelAnimators();
        ChatGreetingsView chatGreetingsView = this.chatGreetingsView;
        if (chatGreetingsView != null) {
            chatGreetingsView.stickerToSendView.setAlpha(1.0f);
        }
        this.greetingsSticker = null;
        this.chatGreetingsView = null;
        for (int size = this.mPendingMoves.size() - 1; size >= 0; size--) {
            DefaultItemAnimator.MoveInfo moveInfo = this.mPendingMoves.get(size);
            restoreTransitionParams(moveInfo.holder.itemView);
            dispatchMoveFinished(moveInfo.holder);
            this.mPendingMoves.remove(size);
        }
        for (int size2 = this.mPendingRemovals.size() - 1; size2 >= 0; size2--) {
            RecyclerView.ViewHolder viewHolder = this.mPendingRemovals.get(size2);
            restoreTransitionParams(viewHolder.itemView);
            dispatchRemoveFinished(viewHolder);
            this.mPendingRemovals.remove(size2);
        }
        for (int size3 = this.mPendingAdditions.size() - 1; size3 >= 0; size3--) {
            RecyclerView.ViewHolder viewHolder2 = this.mPendingAdditions.get(size3);
            restoreTransitionParams(viewHolder2.itemView);
            dispatchAddFinished(viewHolder2);
            this.mPendingAdditions.remove(size3);
        }
        for (int size4 = this.mPendingChanges.size() - 1; size4 >= 0; size4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(size4));
        }
        this.mPendingChanges.clear();
        if (!isRunning()) {
            return;
        }
        for (int size5 = this.mMovesList.size() - 1; size5 >= 0; size5--) {
            ArrayList<DefaultItemAnimator.MoveInfo> arrayList = this.mMovesList.get(size5);
            for (int size6 = arrayList.size() - 1; size6 >= 0; size6--) {
                DefaultItemAnimator.MoveInfo moveInfo2 = arrayList.get(size6);
                restoreTransitionParams(moveInfo2.holder.itemView);
                dispatchMoveFinished(moveInfo2.holder);
                arrayList.remove(size6);
                if (arrayList.isEmpty()) {
                    this.mMovesList.remove(arrayList);
                }
            }
        }
        for (int size7 = this.mAdditionsList.size() - 1; size7 >= 0; size7--) {
            ArrayList<RecyclerView.ViewHolder> arrayList2 = this.mAdditionsList.get(size7);
            for (int size8 = arrayList2.size() - 1; size8 >= 0; size8--) {
                RecyclerView.ViewHolder viewHolder3 = arrayList2.get(size8);
                restoreTransitionParams(viewHolder3.itemView);
                dispatchAddFinished(viewHolder3);
                arrayList2.remove(size8);
                if (arrayList2.isEmpty()) {
                    this.mAdditionsList.remove(arrayList2);
                }
            }
        }
        for (int size9 = this.mChangesList.size() - 1; size9 >= 0; size9--) {
            ArrayList<DefaultItemAnimator.ChangeInfo> arrayList3 = this.mChangesList.get(size9);
            for (int size10 = arrayList3.size() - 1; size10 >= 0; size10--) {
                endChangeAnimationIfNecessary(arrayList3.get(size10));
                if (arrayList3.isEmpty()) {
                    this.mChangesList.remove(arrayList3);
                }
            }
        }
        cancelAll(this.mRemoveAnimations);
        cancelAll(this.mMoveAnimations);
        cancelAll(this.mAddAnimations);
        cancelAll(this.mChangeAnimations);
        dispatchAnimationsFinished();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    protected boolean endChangeAnimationIfNecessary(DefaultItemAnimator.ChangeInfo changeInfo, RecyclerView.ViewHolder viewHolder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end change if necessary");
        }
        Animator remove = this.animators.remove(viewHolder);
        if (remove != null) {
            remove.cancel();
        }
        boolean z = false;
        if (changeInfo.newHolder == viewHolder) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != viewHolder) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            z = true;
        }
        restoreTransitionParams(viewHolder.itemView);
        dispatchChangeFinished(viewHolder, z);
        return true;
    }

    public void groupWillTransformToSingleMessage(MessageObject.GroupedMessages groupedMessages) {
        this.willRemovedGroup.put(Integer.valueOf(groupedMessages.messages.get(0).getId()), groupedMessages);
    }

    public void groupWillChanged(MessageObject.GroupedMessages groupedMessages) {
        if (groupedMessages == null) {
            return;
        }
        if (groupedMessages.messages.size() == 0) {
            groupedMessages.transitionParams.drawBackgroundForDeletedItems = true;
            return;
        }
        MessageObject.GroupedMessages.TransitionParams transitionParams = groupedMessages.transitionParams;
        if (transitionParams.top == 0 && transitionParams.bottom == 0 && transitionParams.left == 0 && transitionParams.right == 0) {
            int childCount = this.recyclerListView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = this.recyclerListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    if (chatMessageCell.getTransitionParams().wasDraw && groupedMessages.messages.contains(messageObject)) {
                        groupedMessages.transitionParams.top = chatMessageCell.getTop() + chatMessageCell.getBackgroundDrawableTop();
                        groupedMessages.transitionParams.bottom = chatMessageCell.getTop() + chatMessageCell.getBackgroundDrawableBottom();
                        groupedMessages.transitionParams.left = chatMessageCell.getLeft() + chatMessageCell.getBackgroundDrawableLeft();
                        groupedMessages.transitionParams.right = chatMessageCell.getLeft() + chatMessageCell.getBackgroundDrawableRight();
                        groupedMessages.transitionParams.drawCaptionLayout = chatMessageCell.hasCaptionLayout();
                        groupedMessages.transitionParams.pinnedTop = chatMessageCell.isPinnedTop();
                        groupedMessages.transitionParams.pinnedBotton = chatMessageCell.isPinnedBottom();
                        groupedMessages.transitionParams.isNewGroup = true;
                        break;
                    }
                }
                i++;
            }
        }
        this.willChangedGroups.add(groupedMessages);
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x0203  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x023d  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0257  */
    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void animateAddImpl(final androidx.recyclerview.widget.RecyclerView.ViewHolder r29) {
        /*
            Method dump skipped, instructions count: 632
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.ChatListItemAnimator.animateAddImpl(androidx.recyclerview.widget.RecyclerView$ViewHolder):void");
    }

    public static /* synthetic */ void lambda$animateAddImpl$7(ChatMessageCell chatMessageCell, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatMessageCell.getTransitionParams().animateChangeProgress = floatValue;
        if (chatMessageCell.getTransitionParams().animateChangeProgress > 1.0f) {
            chatMessageCell.getTransitionParams().animateChangeProgress = 1.0f;
        }
        float f9 = 1.0f - floatValue;
        chatMessageCell.getPhotoImage().setImageCoords(f + (f2 * f9), f3 + (f4 * f9), (f5 * f9) + (f6 * floatValue), (f7 * f9) + (f8 * floatValue));
        chatMessageCell.invalidate();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    protected void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate remove impl");
        }
        final View view = viewHolder.itemView;
        this.mRemoveAnimations.add(viewHolder);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f);
        dispatchRemoveStarting(viewHolder);
        ofFloat.setDuration(getRemoveDuration());
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                if (ChatListItemAnimator.this.mRemoveAnimations.remove(viewHolder)) {
                    ChatListItemAnimator.this.dispatchRemoveFinished(viewHolder);
                    ChatListItemAnimator.this.dispatchFinishedWhenDone();
                }
            }
        });
        this.animators.put(viewHolder, ofFloat);
        ofFloat.start();
        this.recyclerListView.stopScroll();
    }

    public void setShouldAnimateEnterFromBottom(boolean z) {
        this.shouldAnimateEnterFromBottom = z;
    }

    public void onDestroy() {
        onAllAnimationsDone();
    }

    public boolean willRemoved(View view) {
        RecyclerView.ViewHolder childViewHolder = this.recyclerListView.getChildViewHolder(view);
        if (childViewHolder != null) {
            return this.mPendingRemovals.contains(childViewHolder) || this.mRemoveAnimations.contains(childViewHolder);
        }
        return false;
    }

    public boolean willAddedFromAlpha(View view) {
        RecyclerView.ViewHolder childViewHolder;
        if (!this.shouldAnimateEnterFromBottom && (childViewHolder = this.recyclerListView.getChildViewHolder(view)) != null) {
            return this.mPendingAdditions.contains(childViewHolder) || this.mAddAnimations.contains(childViewHolder);
        }
        return false;
    }

    public void onGreetingStickerTransition(RecyclerView.ViewHolder viewHolder, ChatGreetingsView chatGreetingsView) {
        this.greetingsSticker = viewHolder;
        this.chatGreetingsView = chatGreetingsView;
        this.shouldAnimateEnterFromBottom = false;
    }

    public void setReversePositions(boolean z) {
        this.reversePositions = z;
    }

    /* loaded from: classes.dex */
    public class MoveInfoExtended extends DefaultItemAnimator.MoveInfo {
        public boolean animateBackgroundOnly;
        public boolean animateChangeGroupBackground;
        public boolean animateChangeInternal;
        boolean animateImage;
        public boolean animatePinnedBottom;
        boolean animateRemoveGroup;
        int deltaBottom;
        int deltaLeft;
        int deltaRight;
        int deltaTop;
        public int groupOffsetBottom;
        public int groupOffsetLeft;
        public int groupOffsetRight;
        public int groupOffsetTop;
        float imageHeight;
        float imageWidth;
        float imageX;
        float imageY;

        MoveInfoExtended(ChatListItemAnimator chatListItemAnimator, RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            super(viewHolder, i, i2, i3, i4);
        }
    }

    /* loaded from: classes.dex */
    public class ItemHolderInfoExtended extends RecyclerView.ItemAnimator.ItemHolderInfo {
        float imageHeight;
        float imageWidth;
        float imageX;
        float imageY;

        ItemHolderInfoExtended(ChatListItemAnimator chatListItemAnimator) {
        }
    }
}
