package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
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
/* loaded from: classes3.dex */
public class ChatListItemAnimator extends DefaultItemAnimator {
    public static final long DEFAULT_DURATION = 250;
    public static final Interpolator DEFAULT_INTERPOLATOR = new CubicBezierInterpolator(0.19919472913616398d, 0.010644531250000006d, 0.27920937042459737d, 0.91025390625d);
    private final ChatActivity activity;
    long alphaEnterDelay;
    private ChatGreetingsView chatGreetingsView;
    private RecyclerView.ViewHolder greetingsSticker;
    private final RecyclerListView recyclerListView;
    boolean reset;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean reversePositions;
    private boolean shouldAnimateEnterFromBottom;
    private HashMap<Integer, MessageObject.GroupedMessages> willRemovedGroup = new HashMap<>();
    private ArrayList<MessageObject.GroupedMessages> willChangedGroups = new ArrayList<>();
    HashMap<RecyclerView.ViewHolder, Animator> animators = new HashMap<>();
    ArrayList<Runnable> runOnAnimationsEnd = new ArrayList<>();
    HashMap<Long, Long> groupIdToEnterDelay = new HashMap<>();

    public ChatListItemAnimator(ChatActivity activity, RecyclerListView listView, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.activity = activity;
        this.recyclerListView = listView;
        this.translationInterpolator = DEFAULT_INTERPOLATOR;
        this.alwaysCreateMoveAnimationIfPossible = true;
        setSupportsChangeAnimations(false);
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void runPendingAnimations() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return;
        }
        boolean runTranslationFromBottom = false;
        if (this.shouldAnimateEnterFromBottom) {
            for (int i = 0; i < this.mPendingAdditions.size(); i++) {
                if (this.reversePositions) {
                    int itemCount = this.recyclerListView.getAdapter() == null ? 0 : this.recyclerListView.getAdapter().getItemCount();
                    if (this.mPendingAdditions.get(i).getLayoutPosition() == itemCount - 1) {
                        runTranslationFromBottom = true;
                    }
                } else if (this.mPendingAdditions.get(i).getLayoutPosition() == 0) {
                    runTranslationFromBottom = true;
                }
            }
        }
        onAnimationStart();
        if (runTranslationFromBottom) {
            runMessageEnterTransition();
        } else {
            runAlphaEnterTransition();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: androidx.recyclerview.widget.ChatListItemAnimator$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatListItemAnimator.this.m3xc37af294(valueAnimator2);
            }
        });
        valueAnimator.setDuration(getRemoveDuration() + getMoveDuration());
        valueAnimator.start();
    }

    /* renamed from: lambda$runPendingAnimations$0$androidx-recyclerview-widget-ChatListItemAnimator */
    public /* synthetic */ void m3xc37af294(ValueAnimator animation) {
        ChatActivity chatActivity = this.activity;
        if (chatActivity != null) {
            chatActivity.onListItemAnimatorTick();
        } else {
            this.recyclerListView.invalidate();
        }
    }

    private void runAlphaEnterTransition() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return;
        }
        Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
        while (it.hasNext()) {
            RecyclerView.ViewHolder holder = it.next();
            animateRemoveImpl(holder);
        }
        this.mPendingRemovals.clear();
        if (movesPending) {
            final ArrayList<DefaultItemAnimator.MoveInfo> moves = new ArrayList<>();
            moves.addAll(this.mPendingMoves);
            this.mMovesList.add(moves);
            this.mPendingMoves.clear();
            Runnable mover = new Runnable() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.1
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it2 = moves.iterator();
                    while (it2.hasNext()) {
                        DefaultItemAnimator.MoveInfo moveInfo = (DefaultItemAnimator.MoveInfo) it2.next();
                        ChatListItemAnimator.this.animateMoveImpl(moveInfo.holder, moveInfo);
                    }
                    moves.clear();
                    ChatListItemAnimator.this.mMovesList.remove(moves);
                }
            };
            if (this.delayAnimations && removalsPending) {
                View view = moves.get(0).holder.itemView;
                ViewCompat.postOnAnimationDelayed(view, mover, getMoveAnimationDelay());
            } else {
                mover.run();
            }
        }
        if (changesPending) {
            final ArrayList<DefaultItemAnimator.ChangeInfo> changes = new ArrayList<>();
            changes.addAll(this.mPendingChanges);
            this.mChangesList.add(changes);
            this.mPendingChanges.clear();
            Runnable changer = new Runnable() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.2
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it2 = changes.iterator();
                    while (it2.hasNext()) {
                        DefaultItemAnimator.ChangeInfo change = (DefaultItemAnimator.ChangeInfo) it2.next();
                        ChatListItemAnimator.this.animateChangeImpl(change);
                    }
                    changes.clear();
                    ChatListItemAnimator.this.mChangesList.remove(changes);
                }
            };
            if (this.delayAnimations && removalsPending) {
                RecyclerView.ViewHolder holder2 = changes.get(0).oldHolder;
                ViewCompat.postOnAnimationDelayed(holder2.itemView, changer, 0L);
            } else {
                changer.run();
            }
        }
        if (additionsPending) {
            ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(this.mPendingAdditions);
            this.mPendingAdditions.clear();
            this.alphaEnterDelay = 0L;
            Collections.sort(additions, ChatListItemAnimator$$ExternalSyntheticLambda7.INSTANCE);
            Iterator<RecyclerView.ViewHolder> it2 = additions.iterator();
            while (it2.hasNext()) {
                RecyclerView.ViewHolder holder3 = it2.next();
                animateAddImpl(holder3);
            }
            additions.clear();
        }
    }

    public static /* synthetic */ int lambda$runAlphaEnterTransition$1(RecyclerView.ViewHolder i1, RecyclerView.ViewHolder i2) {
        return i2.itemView.getTop() - i1.itemView.getTop();
    }

    private void runMessageEnterTransition() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return;
        }
        int addedItemsHeight = 0;
        for (int i = 0; i < this.mPendingAdditions.size(); i++) {
            View view = this.mPendingAdditions.get(i).itemView;
            if (view instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) view;
                if (cell.getCurrentPosition() != null && (cell.getCurrentPosition().flags & 1) == 0) {
                }
            }
            addedItemsHeight += this.mPendingAdditions.get(i).itemView.getHeight();
        }
        Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
        while (it.hasNext()) {
            RecyclerView.ViewHolder holder = it.next();
            animateRemoveImpl(holder);
        }
        this.mPendingRemovals.clear();
        if (movesPending) {
            ArrayList<DefaultItemAnimator.MoveInfo> moves = new ArrayList<>();
            moves.addAll(this.mPendingMoves);
            this.mPendingMoves.clear();
            Iterator<DefaultItemAnimator.MoveInfo> it2 = moves.iterator();
            while (it2.hasNext()) {
                DefaultItemAnimator.MoveInfo moveInfo = it2.next();
                animateMoveImpl(moveInfo.holder, moveInfo);
            }
            moves.clear();
        }
        if (additionsPending) {
            ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(this.mPendingAdditions);
            this.mPendingAdditions.clear();
            Iterator<RecyclerView.ViewHolder> it3 = additions.iterator();
            while (it3.hasNext()) {
                RecyclerView.ViewHolder holder2 = it3.next();
                animateAddImpl(holder2, addedItemsHeight);
            }
            additions.clear();
        }
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo preLayoutInfo, RecyclerView.ItemAnimator.ItemHolderInfo postLayoutInfo) {
        boolean res = super.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
        if (res && this.shouldAnimateEnterFromBottom) {
            boolean runTranslationFromBottom = false;
            for (int i = 0; i < this.mPendingAdditions.size(); i++) {
                if (this.mPendingAdditions.get(i).getLayoutPosition() == 0) {
                    runTranslationFromBottom = true;
                }
            }
            int addedItemsHeight = 0;
            if (runTranslationFromBottom) {
                for (int i2 = 0; i2 < this.mPendingAdditions.size(); i2++) {
                    addedItemsHeight += this.mPendingAdditions.get(i2).itemView.getHeight();
                }
            }
            for (int i3 = 0; i3 < this.mPendingAdditions.size(); i3++) {
                this.mPendingAdditions.get(i3).itemView.setTranslationY(addedItemsHeight);
            }
        }
        return res;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        holder.itemView.setAlpha(0.0f);
        if (!this.shouldAnimateEnterFromBottom) {
            holder.itemView.setScaleX(0.9f);
            holder.itemView.setScaleY(0.9f);
        } else if (holder.itemView instanceof ChatMessageCell) {
            ((ChatMessageCell) holder.itemView).getTransitionParams().messageEntering = true;
        }
        this.mPendingAdditions.add(holder);
        return true;
    }

    public void animateAddImpl(final RecyclerView.ViewHolder holder, int addedItemsHeight) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mAddAnimations.add(holder);
        view.setTranslationY(addedItemsHeight);
        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);
        ChatMessageCell chatMessageCell = holder.itemView instanceof ChatMessageCell ? (ChatMessageCell) holder.itemView : null;
        if (chatMessageCell == null || !chatMessageCell.getTransitionParams().ignoreAlpha) {
            holder.itemView.setAlpha(1.0f);
        }
        if (chatMessageCell != null && this.activity.animatingMessageObjects.contains(chatMessageCell.getMessageObject())) {
            this.activity.animatingMessageObjects.remove(chatMessageCell.getMessageObject());
            if (this.activity.getChatActivityEnterView().canShowMessageTransition()) {
                if (chatMessageCell.getMessageObject().isVoice()) {
                    if (Math.abs(view.getTranslationY()) < view.getMeasuredHeight() * 3.0f) {
                        VoiceMessageEnterTransition transition = new VoiceMessageEnterTransition(chatMessageCell, this.activity.getChatActivityEnterView(), this.recyclerListView, this.activity.messageEnterTransitionContainer, this.resourcesProvider);
                        transition.start();
                    }
                } else if (SharedConfig.getDevicePerformanceClass() != 0 && Math.abs(view.getTranslationY()) < this.recyclerListView.getMeasuredHeight()) {
                    ChatActivity chatActivity = this.activity;
                    TextMessageEnterTransition transition2 = new TextMessageEnterTransition(chatMessageCell, chatActivity, this.recyclerListView, chatActivity.messageEnterTransitionContainer, this.resourcesProvider);
                    transition2.start();
                }
                this.activity.getChatActivityEnterView().startMessageTransition();
            }
        }
        animation.translationY(0.0f).setDuration(getMoveDuration()).setInterpolator(this.translationInterpolator).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                ChatListItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setTranslationY(0.0f);
                View view2 = view;
                if (view2 instanceof ChatMessageCell) {
                    ((ChatMessageCell) view2).getTransitionParams().messageEntering = false;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                View view2 = view;
                if (view2 instanceof ChatMessageCell) {
                    ((ChatMessageCell) view2).getTransitionParams().messageEntering = false;
                }
                animation.setListener(null);
                if (ChatListItemAnimator.this.mAddAnimations.remove(holder)) {
                    ChatListItemAnimator.this.dispatchAddFinished(holder);
                    ChatListItemAnimator.this.dispatchFinishedWhenDone();
                }
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateRemove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate remove");
        }
        boolean rez = super.animateRemove(holder, info);
        if (rez && info != null) {
            int fromY = info.top;
            int toY = holder.itemView.getTop();
            int fromX = info.left;
            int toX = holder.itemView.getLeft();
            int deltaX = toX - fromX;
            int deltaY = toY - fromY;
            if (deltaY != 0) {
                holder.itemView.setTranslationY(-deltaY);
            }
            if (holder.itemView instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) holder.itemView;
                if (deltaX != 0) {
                    chatMessageCell.setAnimationOffsetX(-deltaX);
                }
                if (info instanceof ItemHolderInfoExtended) {
                    ItemHolderInfoExtended infoExtended = (ItemHolderInfoExtended) info;
                    chatMessageCell.setImageCoords(infoExtended.imageX, infoExtended.imageY, infoExtended.imageWidth, infoExtended.imageHeight);
                }
            } else if (deltaX != 0) {
                holder.itemView.setTranslationX(-deltaX);
            }
        }
        return rez;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateMove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        int fromX2;
        ChatMessageCell chatMessageCell;
        int fromY2;
        float imageX;
        float imageY;
        float imageW;
        float imageH;
        ChatMessageCell.TransitionParams params;
        int deltaX;
        View view;
        boolean z;
        ChatMessageCell.TransitionParams params2;
        ChatMessageCell.TransitionParams params3;
        int deltaX2;
        MessageObject.GroupedMessages group;
        int[] roundRadius;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate move");
        }
        View view2 = holder.itemView;
        if (!(holder.itemView instanceof ChatMessageCell)) {
            fromY2 = fromY;
            chatMessageCell = null;
            fromX2 = fromX + ((int) holder.itemView.getTranslationX());
        } else {
            ChatMessageCell chatMessageCell2 = (ChatMessageCell) holder.itemView;
            int fromX3 = fromX + ((int) chatMessageCell2.getAnimationOffsetX());
            if (chatMessageCell2.getTransitionParams().lastTopOffset == chatMessageCell2.getTopMediaOffset()) {
                fromY2 = fromY;
                chatMessageCell = chatMessageCell2;
                fromX2 = fromX3;
            } else {
                fromY2 = fromY + (chatMessageCell2.getTransitionParams().lastTopOffset - chatMessageCell2.getTopMediaOffset());
                chatMessageCell = chatMessageCell2;
                fromX2 = fromX3;
            }
        }
        int fromY3 = fromY2 + ((int) holder.itemView.getTranslationY());
        float imageH2 = 0.0f;
        int[] roundRadius2 = new int[4];
        if (chatMessageCell == null) {
            imageX = 0.0f;
            imageY = 0.0f;
            imageW = 0.0f;
        } else {
            float imageX2 = chatMessageCell.getPhotoImage().getImageX();
            float imageY2 = chatMessageCell.getPhotoImage().getImageY();
            float imageW2 = chatMessageCell.getPhotoImage().getImageWidth();
            imageH2 = chatMessageCell.getPhotoImage().getImageHeight();
            for (int i = 0; i < 4; i++) {
                roundRadius2[i] = chatMessageCell.getPhotoImage().getRoundRadius()[i];
            }
            imageX = imageX2;
            imageY = imageY2;
            imageW = imageW2;
        }
        resetAnimation(holder);
        int deltaX3 = toX - fromX2;
        int deltaY = toY - fromY3;
        if (deltaY != 0) {
            view2.setTranslationY(-deltaY);
        }
        int deltaX4 = deltaX3;
        float imageH3 = imageH2;
        int i2 = fromX2;
        float imageW3 = imageW;
        float imageY3 = imageY;
        float imageX3 = imageX;
        MoveInfoExtended moveInfo = new MoveInfoExtended(holder, i2, fromY3, toX, toY);
        if (chatMessageCell != null) {
            ChatMessageCell.TransitionParams params4 = chatMessageCell.getTransitionParams();
            if (!params4.supportChangeAnimation()) {
                if (deltaX4 == 0 && deltaY == 0) {
                    dispatchMoveFinished(holder);
                    return false;
                }
                if (deltaX4 != 0) {
                    view2.setTranslationX(-deltaX4);
                }
                this.mPendingMoves.add(moveInfo);
                checkIsRunning();
                return true;
            }
            MessageObject.GroupedMessages group2 = chatMessageCell.getCurrentMessagesGroup();
            if (deltaX4 != 0) {
                chatMessageCell.setAnimationOffsetX(-deltaX4);
            }
            if (!(info instanceof ItemHolderInfoExtended)) {
                imageH = imageH3;
            } else {
                ImageReceiver newImage = chatMessageCell.getPhotoImage();
                ItemHolderInfoExtended infoExtended = (ItemHolderInfoExtended) info;
                moveInfo.animateImage = (!params4.wasDraw || infoExtended.imageHeight == 0.0f || infoExtended.imageWidth == 0.0f) ? false : true;
                if (moveInfo.animateImage) {
                    this.recyclerListView.setClipChildren(false);
                    this.recyclerListView.invalidate();
                    params4.imageChangeBoundsTransition = true;
                    if (chatMessageCell.getMessageObject().isRoundVideo()) {
                        params4.animateToImageX = imageX3;
                        params4.animateToImageY = imageY3;
                        params4.animateToImageW = imageW3;
                        params4.animateToImageH = imageH3;
                        roundRadius = roundRadius2;
                        params4.animateToRadius = roundRadius;
                    } else {
                        roundRadius = roundRadius2;
                        params4.animateToImageX = newImage.getImageX();
                        params4.animateToImageY = newImage.getImageY();
                        params4.animateToImageW = newImage.getImageWidth();
                        params4.animateToImageH = newImage.getImageHeight();
                        params4.animateToRadius = newImage.getRoundRadius();
                    }
                    params4.animateRadius = false;
                    int i3 = 0;
                    while (true) {
                        int[] roundRadius3 = roundRadius;
                        if (i3 >= 4) {
                            imageH = imageH3;
                            break;
                        }
                        imageH = imageH3;
                        if (params4.imageRoundRadius[i3] == params4.animateToRadius[i3]) {
                            i3++;
                            roundRadius = roundRadius3;
                            imageH3 = imageH;
                        } else {
                            params4.animateRadius = true;
                            break;
                        }
                    }
                    if (params4.animateToImageX == infoExtended.imageX && params4.animateToImageY == infoExtended.imageY && params4.animateToImageH == infoExtended.imageHeight && params4.animateToImageW == infoExtended.imageWidth && !params4.animateRadius) {
                        params4.imageChangeBoundsTransition = false;
                        moveInfo.animateImage = false;
                    } else {
                        moveInfo.imageX = infoExtended.imageX;
                        moveInfo.imageY = infoExtended.imageY;
                        moveInfo.imageWidth = infoExtended.imageWidth;
                        moveInfo.imageHeight = infoExtended.imageHeight;
                        if (group2 != null && group2.hasCaption != group2.transitionParams.drawCaptionLayout) {
                            group2.transitionParams.captionEnterProgress = group2.transitionParams.drawCaptionLayout ? 1.0f : 0.0f;
                        }
                        if (params4.animateRadius) {
                            if (params4.animateToRadius == newImage.getRoundRadius()) {
                                params4.animateToRadius = new int[4];
                                for (int i4 = 0; i4 < 4; i4++) {
                                    params4.animateToRadius[i4] = newImage.getRoundRadius()[i4];
                                }
                            }
                            newImage.setRoundRadius(params4.imageRoundRadius);
                        }
                        chatMessageCell.setImageCoords(moveInfo.imageX, moveInfo.imageY, moveInfo.imageWidth, moveInfo.imageHeight);
                    }
                } else {
                    imageH = imageH3;
                }
                if (group2 == null && params4.wasDraw) {
                    boolean isOut = chatMessageCell.getMessageObject().isOutOwner();
                    boolean widthChanged = (isOut && params4.lastDrawingBackgroundRect.left != chatMessageCell.getBackgroundDrawableLeft()) || (!isOut && params4.lastDrawingBackgroundRect.right != chatMessageCell.getBackgroundDrawableRight());
                    if (widthChanged || params4.lastDrawingBackgroundRect.top != chatMessageCell.getBackgroundDrawableTop() || params4.lastDrawingBackgroundRect.bottom != chatMessageCell.getBackgroundDrawableBottom()) {
                        moveInfo.deltaBottom = chatMessageCell.getBackgroundDrawableBottom() - params4.lastDrawingBackgroundRect.bottom;
                        moveInfo.deltaTop = chatMessageCell.getBackgroundDrawableTop() - params4.lastDrawingBackgroundRect.top;
                        if (isOut) {
                            moveInfo.deltaLeft = chatMessageCell.getBackgroundDrawableLeft() - params4.lastDrawingBackgroundRect.left;
                        } else {
                            moveInfo.deltaRight = chatMessageCell.getBackgroundDrawableRight() - params4.lastDrawingBackgroundRect.right;
                        }
                        moveInfo.animateBackgroundOnly = true;
                        params4.animateBackgroundBoundsInner = true;
                        params4.animateBackgroundWidth = widthChanged;
                        params4.deltaLeft = -moveInfo.deltaLeft;
                        params4.deltaRight = -moveInfo.deltaRight;
                        params4.deltaTop = -moveInfo.deltaTop;
                        params4.deltaBottom = -moveInfo.deltaBottom;
                        this.recyclerListView.setClipChildren(false);
                        this.recyclerListView.invalidate();
                    }
                }
            }
            if (group2 == null) {
                params = params4;
                view = view2;
                deltaX = deltaX4;
            } else if (!this.willChangedGroups.contains(group2)) {
                params = params4;
                view = view2;
                deltaX = deltaX4;
            } else {
                this.willChangedGroups.remove(group2);
                RecyclerListView recyclerListView = (RecyclerListView) holder.itemView.getParent();
                int animateToLeft = 0;
                int animateToRight = 0;
                MessageObject.GroupedMessages.TransitionParams groupTransitionParams = group2.transitionParams;
                int animateToTop = 0;
                int animateToBottom = 0;
                boolean allVisibleItemsDeleted = true;
                int i5 = 0;
                while (true) {
                    view = view2;
                    if (i5 >= recyclerListView.getChildCount()) {
                        break;
                    }
                    View child = recyclerListView.getChildAt(i5);
                    if (!(child instanceof ChatMessageCell)) {
                        params3 = params4;
                        group = group2;
                        deltaX2 = deltaX4;
                    } else {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        if (cell.getCurrentMessagesGroup() != group2 || cell.getMessageObject().deleted) {
                            params3 = params4;
                            group = group2;
                            deltaX2 = deltaX4;
                        } else {
                            int left = cell.getLeft() + cell.getBackgroundDrawableLeft();
                            group = group2;
                            int right = cell.getLeft() + cell.getBackgroundDrawableRight();
                            deltaX2 = deltaX4;
                            int deltaX5 = cell.getTop() + cell.getBackgroundDrawableTop();
                            params3 = params4;
                            int bottom = cell.getTop() + cell.getBackgroundDrawableBottom();
                            if (animateToLeft == 0 || left < animateToLeft) {
                                animateToLeft = left;
                            }
                            if (animateToRight == 0 || right > animateToRight) {
                                animateToRight = right;
                            }
                            if (cell.getTransitionParams().wasDraw || groupTransitionParams.isNewGroup) {
                                allVisibleItemsDeleted = false;
                                if (animateToTop == 0 || deltaX5 < animateToTop) {
                                    animateToTop = deltaX5;
                                }
                                if (animateToBottom == 0 || bottom > animateToBottom) {
                                    animateToBottom = bottom;
                                }
                            }
                        }
                    }
                    i5++;
                    view2 = view;
                    group2 = group;
                    deltaX4 = deltaX2;
                    params4 = params3;
                }
                params = params4;
                deltaX = deltaX4;
                groupTransitionParams.isNewGroup = false;
                if (animateToTop == 0 && animateToBottom == 0 && animateToLeft == 0 && animateToRight == 0) {
                    moveInfo.animateChangeGroupBackground = false;
                    groupTransitionParams.backgroundChangeBounds = false;
                } else {
                    moveInfo.groupOffsetTop = (-animateToTop) + groupTransitionParams.top;
                    moveInfo.groupOffsetBottom = (-animateToBottom) + groupTransitionParams.bottom;
                    moveInfo.groupOffsetLeft = (-animateToLeft) + groupTransitionParams.left;
                    moveInfo.groupOffsetRight = (-animateToRight) + groupTransitionParams.right;
                    moveInfo.animateChangeGroupBackground = true;
                    groupTransitionParams.backgroundChangeBounds = true;
                    groupTransitionParams.offsetTop = moveInfo.groupOffsetTop;
                    groupTransitionParams.offsetBottom = moveInfo.groupOffsetBottom;
                    groupTransitionParams.offsetLeft = moveInfo.groupOffsetLeft;
                    groupTransitionParams.offsetRight = moveInfo.groupOffsetRight;
                    groupTransitionParams.captionEnterProgress = groupTransitionParams.drawCaptionLayout ? 1.0f : 0.0f;
                    recyclerListView.setClipChildren(false);
                    recyclerListView.invalidate();
                }
                groupTransitionParams.drawBackgroundForDeletedItems = allVisibleItemsDeleted;
            }
            MessageObject.GroupedMessages removedGroup = this.willRemovedGroup.get(Integer.valueOf(chatMessageCell.getMessageObject().getId()));
            if (removedGroup == null) {
                params2 = params;
                z = true;
            } else {
                MessageObject.GroupedMessages.TransitionParams groupTransitionParams2 = removedGroup.transitionParams;
                this.willRemovedGroup.remove(Integer.valueOf(chatMessageCell.getMessageObject().getId()));
                params2 = params;
                if (params2.wasDraw) {
                    int animateToLeft2 = chatMessageCell.getLeft() + chatMessageCell.getBackgroundDrawableLeft();
                    int animateToRight2 = chatMessageCell.getLeft() + chatMessageCell.getBackgroundDrawableRight();
                    int animateToTop2 = chatMessageCell.getTop() + chatMessageCell.getBackgroundDrawableTop();
                    int animateToBottom2 = chatMessageCell.getTop() + chatMessageCell.getBackgroundDrawableBottom();
                    moveInfo.animateRemoveGroup = true;
                    params2.animateBackgroundBoundsInner = true;
                    moveInfo.deltaLeft = animateToLeft2 - groupTransitionParams2.left;
                    moveInfo.deltaRight = animateToRight2 - groupTransitionParams2.right;
                    moveInfo.deltaTop = animateToTop2 - groupTransitionParams2.top;
                    moveInfo.deltaBottom = animateToBottom2 - groupTransitionParams2.bottom;
                    moveInfo.animateBackgroundOnly = false;
                    params2.deltaLeft = (int) ((-moveInfo.deltaLeft) - chatMessageCell.getAnimationOffsetX());
                    params2.deltaRight = (int) ((-moveInfo.deltaRight) - chatMessageCell.getAnimationOffsetX());
                    params2.deltaTop = (int) ((-moveInfo.deltaTop) - chatMessageCell.getTranslationY());
                    params2.deltaBottom = (int) ((-moveInfo.deltaBottom) - chatMessageCell.getTranslationY());
                    z = true;
                    params2.transformGroupToSingleMessage = true;
                    this.recyclerListView.setClipChildren(false);
                    this.recyclerListView.invalidate();
                } else {
                    z = true;
                    groupTransitionParams2.drawBackgroundForDeletedItems = true;
                }
            }
            boolean drawPinnedBottom = chatMessageCell.isDrawPinnedBottom();
            if (params2.drawPinnedBottomBackground != drawPinnedBottom) {
                moveInfo.animatePinnedBottom = z;
                params2.changePinnedBottomProgress = 0.0f;
            }
            moveInfo.animateChangeInternal = chatMessageCell.getTransitionParams().animateChange();
            if (moveInfo.animateChangeInternal) {
                chatMessageCell.getTransitionParams().animateChange = true;
                chatMessageCell.getTransitionParams().animateChangeProgress = 0.0f;
            }
            if (deltaX == 0 && deltaY == 0 && !moveInfo.animateImage && !moveInfo.animateRemoveGroup && !moveInfo.animateChangeGroupBackground && !moveInfo.animatePinnedBottom && !moveInfo.animateBackgroundOnly && !moveInfo.animateChangeInternal) {
                dispatchMoveFinished(holder);
                return false;
            }
        } else if (holder.itemView instanceof BotHelpCell) {
            BotHelpCell botInfo = (BotHelpCell) holder.itemView;
            botInfo.setAnimating(true);
        } else if (deltaX4 == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        } else if (deltaX4 != 0) {
            view2.setTranslationX(-deltaX4);
        }
        this.mPendingMoves.add(moveInfo);
        checkIsRunning();
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x01be  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x01c4  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0222  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0241  */
    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void animateMoveImpl(final androidx.recyclerview.widget.RecyclerView.ViewHolder r34, androidx.recyclerview.widget.DefaultItemAnimator.MoveInfo r35) {
        /*
            Method dump skipped, instructions count: 698
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.ChatListItemAnimator.animateMoveImpl(androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.DefaultItemAnimator$MoveInfo):void");
    }

    public static /* synthetic */ void lambda$animateMoveImpl$2(MoveInfoExtended moveInfoExtended, ChatMessageCell.TransitionParams params, boolean animateCaption, float captionEnterFrom, float captionEnterTo, ChatMessageCell chatMessageCell, int[] finalFromRoundRadius, RecyclerView.ViewHolder holder, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float x = (moveInfoExtended.imageX * (1.0f - v)) + (params.animateToImageX * v);
        float y = (moveInfoExtended.imageY * (1.0f - v)) + (params.animateToImageY * v);
        float width = (moveInfoExtended.imageWidth * (1.0f - v)) + (params.animateToImageW * v);
        float height = (moveInfoExtended.imageHeight * (1.0f - v)) + (params.animateToImageH * v);
        if (animateCaption) {
            float captionP = ((1.0f - v) * captionEnterFrom) + (captionEnterTo * v);
            params.captionEnterProgress = captionP;
            if (chatMessageCell.getCurrentMessagesGroup() != null) {
                chatMessageCell.getCurrentMessagesGroup().transitionParams.captionEnterProgress = captionP;
            }
        }
        if (params.animateRadius) {
            chatMessageCell.getPhotoImage().setRoundRadius((int) ((finalFromRoundRadius[0] * (1.0f - v)) + (params.animateToRadius[0] * v)), (int) ((finalFromRoundRadius[1] * (1.0f - v)) + (params.animateToRadius[1] * v)), (int) ((finalFromRoundRadius[2] * (1.0f - v)) + (params.animateToRadius[2] * v)), (int) ((finalFromRoundRadius[3] * (1.0f - v)) + (params.animateToRadius[3] * v)));
        }
        chatMessageCell.setImageCoords(x, y, width, height);
        holder.itemView.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$3(MoveInfoExtended moveInfoExtended, ChatMessageCell.TransitionParams params, ChatMessageCell chatMessageCell, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        if (moveInfoExtended.animateBackgroundOnly) {
            params.deltaLeft = (-moveInfoExtended.deltaLeft) * v;
            params.deltaRight = (-moveInfoExtended.deltaRight) * v;
            params.deltaTop = (-moveInfoExtended.deltaTop) * v;
            params.deltaBottom = (-moveInfoExtended.deltaBottom) * v;
        } else {
            params.deltaLeft = ((-moveInfoExtended.deltaLeft) * v) - chatMessageCell.getAnimationOffsetX();
            params.deltaRight = ((-moveInfoExtended.deltaRight) * v) - chatMessageCell.getAnimationOffsetX();
            params.deltaTop = ((-moveInfoExtended.deltaTop) * v) - chatMessageCell.getTranslationY();
            params.deltaBottom = ((-moveInfoExtended.deltaBottom) * v) - chatMessageCell.getTranslationY();
        }
        chatMessageCell.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$4(MessageObject.GroupedMessages.TransitionParams groupTransitionParams, MoveInfoExtended moveInfoExtended, boolean animateCaption, float captionEnterFrom, float captionEnterTo, RecyclerListView recyclerListView, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        groupTransitionParams.offsetTop = moveInfoExtended.groupOffsetTop * v;
        groupTransitionParams.offsetBottom = moveInfoExtended.groupOffsetBottom * v;
        groupTransitionParams.offsetLeft = moveInfoExtended.groupOffsetLeft * v;
        groupTransitionParams.offsetRight = moveInfoExtended.groupOffsetRight * v;
        if (animateCaption) {
            groupTransitionParams.captionEnterProgress = (captionEnterFrom * v) + ((1.0f - v) * captionEnterTo);
        }
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    public static /* synthetic */ void lambda$animateMoveImpl$5(ChatMessageCell.TransitionParams params, ChatMessageCell chatMessageCell, ValueAnimator animation) {
        params.changePinnedBottomProgress = ((Float) animation.getAnimatedValue()).floatValue();
        chatMessageCell.invalidate();
    }

    public static /* synthetic */ void lambda$animateMoveImpl$6(ChatMessageCell.TransitionParams params, ChatMessageCell chatMessageCell, ValueAnimator animation) {
        params.animateChangeProgress = ((Float) animation.getAnimatedValue()).floatValue();
        chatMessageCell.invalidate();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    public void resetAnimation(RecyclerView.ViewHolder holder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("reset animation");
        }
        this.reset = true;
        super.resetAnimation(holder);
        this.reset = false;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        float prevTranslationX;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate change");
        }
        if (oldHolder == newHolder) {
            return animateMove(oldHolder, info, fromX, fromY, toX, toY);
        }
        if (oldHolder.itemView instanceof ChatMessageCell) {
            prevTranslationX = ((ChatMessageCell) oldHolder.itemView).getAnimationOffsetX();
        } else {
            prevTranslationX = oldHolder.itemView.getTranslationX();
        }
        float prevTranslationY = oldHolder.itemView.getTranslationY();
        float prevAlpha = oldHolder.itemView.getAlpha();
        resetAnimation(oldHolder);
        int deltaX = (int) ((toX - fromX) - prevTranslationX);
        int deltaY = (int) ((toY - fromY) - prevTranslationY);
        if (oldHolder.itemView instanceof ChatMessageCell) {
            ((ChatMessageCell) oldHolder.itemView).setAnimationOffsetX(prevTranslationX);
        } else {
            oldHolder.itemView.setTranslationX(prevTranslationX);
        }
        oldHolder.itemView.setTranslationY(prevTranslationY);
        oldHolder.itemView.setAlpha(prevAlpha);
        if (newHolder != null) {
            resetAnimation(newHolder);
            if (newHolder.itemView instanceof ChatMessageCell) {
                ((ChatMessageCell) newHolder.itemView).setAnimationOffsetX(-deltaX);
            } else {
                newHolder.itemView.setTranslationX(-deltaX);
            }
            newHolder.itemView.setTranslationY(-deltaY);
            newHolder.itemView.setAlpha(0.0f);
        }
        this.mPendingChanges.add(new DefaultItemAnimator.ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
        checkIsRunning();
        return true;
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    void animateChangeImpl(final DefaultItemAnimator.ChangeInfo changeInfo) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate change impl");
        }
        RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        final View newView = null;
        final View view = holder == null ? null : holder.itemView;
        RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        if (newHolder != null) {
            newView = newHolder.itemView;
        }
        if (view != null) {
            final ViewPropertyAnimator oldViewAnim = view.animate().setDuration(getChangeDuration());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            oldViewAnim.translationX(changeInfo.toX - changeInfo.fromX);
            oldViewAnim.translationY(changeInfo.toY - changeInfo.fromY);
            oldViewAnim.alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ChatListItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    oldViewAnim.setListener(null);
                    view.setAlpha(1.0f);
                    view.setScaleX(1.0f);
                    view.setScaleX(1.0f);
                    View view2 = view;
                    if (view2 instanceof ChatMessageCell) {
                        ((ChatMessageCell) view2).setAnimationOffsetX(0.0f);
                    } else {
                        view2.setTranslationX(0.0f);
                    }
                    view.setTranslationY(0.0f);
                    if (ChatListItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder)) {
                        ChatListItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                        ChatListItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }
            }).start();
        }
        if (newView != null) {
            final ViewPropertyAnimator newViewAnimation = newView.animate();
            this.mChangeAnimations.add(changeInfo.newHolder);
            newViewAnimation.translationX(0.0f).translationY(0.0f).setDuration(getChangeDuration()).alpha(1.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ChatListItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    newViewAnimation.setListener(null);
                    newView.setAlpha(1.0f);
                    newView.setScaleX(1.0f);
                    newView.setScaleX(1.0f);
                    View view2 = newView;
                    if (view2 instanceof ChatMessageCell) {
                        ((ChatMessageCell) view2).setAnimationOffsetX(0.0f);
                    } else {
                        view2.setTranslationX(0.0f);
                    }
                    newView.setTranslationY(0.0f);
                    if (ChatListItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder)) {
                        ChatListItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                        ChatListItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }
            }).start();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public RecyclerView.ItemAnimator.ItemHolderInfo recordPreLayoutInformation(RecyclerView.State state, RecyclerView.ViewHolder viewHolder, int changeFlags, List<Object> payloads) {
        RecyclerView.ItemAnimator.ItemHolderInfo info = super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        if (viewHolder.itemView instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
            ItemHolderInfoExtended extended = new ItemHolderInfoExtended();
            extended.left = info.left;
            extended.top = info.top;
            extended.right = info.right;
            extended.bottom = info.bottom;
            ChatMessageCell.TransitionParams params = chatMessageCell.getTransitionParams();
            extended.imageX = params.lastDrawingImageX;
            extended.imageY = params.lastDrawingImageY;
            extended.imageWidth = params.lastDrawingImageW;
            extended.imageHeight = params.lastDrawingImageH;
            return extended;
        }
        return info;
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
        ArrayList<Animator> anim = new ArrayList<>(this.animators.values());
        this.animators.clear();
        Iterator<Animator> it = anim.iterator();
        while (it.hasNext()) {
            Animator animator = it.next();
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimation(RecyclerView.ViewHolder item) {
        Animator animator = this.animators.remove(item);
        if (animator != null) {
            animator.cancel();
        }
        super.endAnimation(item);
        restoreTransitionParams(item.itemView);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end animation");
        }
    }

    public void restoreTransitionParams(View view) {
        view.setAlpha(1.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setTranslationY(0.0f);
        if (!(view instanceof BotHelpCell)) {
            if (view instanceof ChatMessageCell) {
                ((ChatMessageCell) view).getTransitionParams().resetAnimation();
                ((ChatMessageCell) view).setAnimationOffsetX(0.0f);
                return;
            }
            view.setTranslationX(0.0f);
            return;
        }
        BotHelpCell botCell = (BotHelpCell) view;
        int top = (this.recyclerListView.getMeasuredHeight() / 2) - (view.getMeasuredHeight() / 2);
        botCell.setAnimating(false);
        if (view.getTop() > top) {
            view.setTranslationY(top - view.getTop());
        } else {
            view.setTranslationY(0.0f);
        }
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimations() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end animations");
        }
        Iterator<MessageObject.GroupedMessages> it = this.willChangedGroups.iterator();
        while (it.hasNext()) {
            MessageObject.GroupedMessages groupedMessages = it.next();
            groupedMessages.transitionParams.isNewGroup = false;
        }
        this.willChangedGroups.clear();
        cancelAnimators();
        ChatGreetingsView chatGreetingsView = this.chatGreetingsView;
        if (chatGreetingsView != null) {
            chatGreetingsView.stickerToSendView.setAlpha(1.0f);
        }
        this.greetingsSticker = null;
        this.chatGreetingsView = null;
        int count = this.mPendingMoves.size();
        for (int i = count - 1; i >= 0; i--) {
            DefaultItemAnimator.MoveInfo item = this.mPendingMoves.get(i);
            View view = item.holder.itemView;
            restoreTransitionParams(view);
            dispatchMoveFinished(item.holder);
            this.mPendingMoves.remove(i);
        }
        int count2 = this.mPendingRemovals.size();
        for (int i2 = count2 - 1; i2 >= 0; i2--) {
            RecyclerView.ViewHolder item2 = this.mPendingRemovals.get(i2);
            restoreTransitionParams(item2.itemView);
            dispatchRemoveFinished(item2);
            this.mPendingRemovals.remove(i2);
        }
        int count3 = this.mPendingAdditions.size();
        for (int i3 = count3 - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item3 = this.mPendingAdditions.get(i3);
            restoreTransitionParams(item3.itemView);
            dispatchAddFinished(item3);
            this.mPendingAdditions.remove(i3);
        }
        int count4 = this.mPendingChanges.size();
        for (int i4 = count4 - 1; i4 >= 0; i4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(i4));
        }
        this.mPendingChanges.clear();
        if (!isRunning()) {
            return;
        }
        int listCount = this.mMovesList.size();
        for (int i5 = listCount - 1; i5 >= 0; i5--) {
            ArrayList<DefaultItemAnimator.MoveInfo> moves = this.mMovesList.get(i5);
            int count5 = moves.size();
            for (int j = count5 - 1; j >= 0; j--) {
                DefaultItemAnimator.MoveInfo moveInfo = moves.get(j);
                restoreTransitionParams(moveInfo.holder.itemView);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if (moves.isEmpty()) {
                    this.mMovesList.remove(moves);
                }
            }
        }
        int listCount2 = this.mAdditionsList.size();
        for (int i6 = listCount2 - 1; i6 >= 0; i6--) {
            ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i6);
            int count6 = additions.size();
            for (int j2 = count6 - 1; j2 >= 0; j2--) {
                RecyclerView.ViewHolder item4 = additions.get(j2);
                restoreTransitionParams(item4.itemView);
                dispatchAddFinished(item4);
                additions.remove(j2);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(additions);
                }
            }
        }
        int listCount3 = this.mChangesList.size();
        for (int i7 = listCount3 - 1; i7 >= 0; i7--) {
            ArrayList<DefaultItemAnimator.ChangeInfo> changes = this.mChangesList.get(i7);
            int count7 = changes.size();
            for (int j3 = count7 - 1; j3 >= 0; j3--) {
                endChangeAnimationIfNecessary(changes.get(j3));
                if (changes.isEmpty()) {
                    this.mChangesList.remove(changes);
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
    protected boolean endChangeAnimationIfNecessary(DefaultItemAnimator.ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("end change if necessary");
        }
        Animator a = this.animators.remove(item);
        if (a != null) {
            a.cancel();
        }
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder == item) {
            changeInfo.oldHolder = null;
            oldItem = true;
        } else {
            return false;
        }
        restoreTransitionParams(item.itemView);
        dispatchChangeFinished(item, oldItem);
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
        if (groupedMessages.transitionParams.top == 0 && groupedMessages.transitionParams.bottom == 0 && groupedMessages.transitionParams.left == 0 && groupedMessages.transitionParams.right == 0) {
            int n = this.recyclerListView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= n) {
                    break;
                }
                View child = this.recyclerListView.getChildAt(i);
                if (child instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) child;
                    MessageObject messageObject = cell.getMessageObject();
                    if (cell.getTransitionParams().wasDraw && groupedMessages.messages.contains(messageObject)) {
                        groupedMessages.transitionParams.top = cell.getTop() + cell.getBackgroundDrawableTop();
                        groupedMessages.transitionParams.bottom = cell.getTop() + cell.getBackgroundDrawableBottom();
                        groupedMessages.transitionParams.left = cell.getLeft() + cell.getBackgroundDrawableLeft();
                        groupedMessages.transitionParams.right = cell.getLeft() + cell.getBackgroundDrawableRight();
                        groupedMessages.transitionParams.drawCaptionLayout = cell.hasCaptionLayout();
                        groupedMessages.transitionParams.pinnedTop = cell.isPinnedTop();
                        groupedMessages.transitionParams.pinnedBotton = cell.isPinnedBottom();
                        groupedMessages.transitionParams.isNewGroup = true;
                        break;
                    }
                }
                i++;
            }
        }
        this.willChangedGroups.add(groupedMessages);
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    public void animateAddImpl(final RecyclerView.ViewHolder holder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate add impl");
        }
        final View view = holder.itemView;
        this.mAddAnimations.add(holder);
        if (holder == this.greetingsSticker) {
            view.setAlpha(1.0f);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        if (view instanceof ChatMessageCell) {
            ChatMessageCell cell = (ChatMessageCell) view;
            if (cell.getAnimationOffsetX() != 0.0f) {
                animatorSet.playTogether(ObjectAnimator.ofFloat(cell, cell.ANIMATION_OFFSET_X, cell.getAnimationOffsetX(), 0.0f));
            }
            float pivotX = cell.getBackgroundDrawableLeft() + ((cell.getBackgroundDrawableRight() - cell.getBackgroundDrawableLeft()) / 2.0f);
            cell.setPivotX(pivotX);
            view.animate().translationY(0.0f).setDuration(getAddDuration()).start();
        } else {
            view.animate().translationX(0.0f).translationY(0.0f).setDuration(getAddDuration()).start();
        }
        boolean useScale = true;
        long currentDelay = (1.0f - Math.max(0.0f, Math.min(1.0f, view.getBottom() / this.recyclerListView.getMeasuredHeight()))) * 100.0f;
        if (view instanceof ChatMessageCell) {
            if (holder == this.greetingsSticker) {
                ChatGreetingsView chatGreetingsView = this.chatGreetingsView;
                if (chatGreetingsView != null) {
                    chatGreetingsView.stickerToSendView.setAlpha(0.0f);
                }
                this.recyclerListView.setClipChildren(false);
                final ChatMessageCell messageCell = (ChatMessageCell) view;
                View parentForGreetingsView = (View) this.chatGreetingsView.getParent();
                float fromX = this.chatGreetingsView.stickerToSendView.getX() + this.chatGreetingsView.getX() + parentForGreetingsView.getX();
                float fromY = this.chatGreetingsView.stickerToSendView.getY() + this.chatGreetingsView.getY() + parentForGreetingsView.getY();
                float toX = messageCell.getPhotoImage().getImageX() + this.recyclerListView.getX() + messageCell.getX();
                float toY = messageCell.getPhotoImage().getImageY() + this.recyclerListView.getY() + messageCell.getY();
                final float fromW = this.chatGreetingsView.stickerToSendView.getWidth();
                final float fromH = this.chatGreetingsView.stickerToSendView.getHeight();
                final float toW = messageCell.getPhotoImage().getImageWidth();
                final float toH = messageCell.getPhotoImage().getImageHeight();
                final float deltaX = fromX - toX;
                final float deltaY = fromY - toY;
                final float toX2 = messageCell.getPhotoImage().getImageX();
                final float toY2 = messageCell.getPhotoImage().getImageY();
                messageCell.getTransitionParams().imageChangeBoundsTransition = true;
                messageCell.getTransitionParams().animateDrawingTimeAlpha = true;
                messageCell.getPhotoImage().setImageCoords(toX2 + deltaX, toX2 + deltaY, fromW, fromH);
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: androidx.recyclerview.widget.ChatListItemAnimator$$ExternalSyntheticLambda6
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatListItemAnimator.lambda$animateAddImpl$7(ChatMessageCell.this, toX2, deltaX, toY2, deltaY, fromW, toW, fromH, toH, valueAnimator2);
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.9
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        messageCell.getTransitionParams().resetAnimation();
                        messageCell.getPhotoImage().setImageCoords(toX2, toY2, toW, toH);
                        if (ChatListItemAnimator.this.chatGreetingsView != null) {
                            ChatListItemAnimator.this.chatGreetingsView.stickerToSendView.setAlpha(1.0f);
                        }
                        messageCell.invalidate();
                    }
                });
                animatorSet.play(valueAnimator);
                useScale = false;
                currentDelay = currentDelay;
            } else {
                MessageObject.GroupedMessages groupedMessages = ((ChatMessageCell) view).getCurrentMessagesGroup();
                if (groupedMessages != null) {
                    Long groupDelay = this.groupIdToEnterDelay.get(Long.valueOf(groupedMessages.groupId));
                    if (groupDelay == null) {
                        this.groupIdToEnterDelay.put(Long.valueOf(groupedMessages.groupId), Long.valueOf(currentDelay));
                    } else {
                        currentDelay = groupDelay.longValue();
                        if (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds) {
                            animatorSet.setStartDelay(140L);
                        }
                    }
                }
                currentDelay = currentDelay;
                if (groupedMessages != null) {
                    animatorSet.setStartDelay(140L);
                }
            }
        }
        view.setAlpha(0.0f);
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 1.0f));
        if (useScale) {
            view.setScaleX(0.9f);
            view.setScaleY(0.9f);
            animatorSet.playTogether(ObjectAnimator.ofFloat(view, View.SCALE_Y, view.getScaleY(), 1.0f));
            animatorSet.playTogether(ObjectAnimator.ofFloat(view, View.SCALE_X, view.getScaleX(), 1.0f));
        } else {
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        }
        if (holder == this.greetingsSticker) {
            animatorSet.setDuration(350L);
            animatorSet.setInterpolator(new OvershootInterpolator());
        } else {
            animatorSet.setStartDelay(currentDelay);
            animatorSet.setDuration(250L);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.10
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                ChatListItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setTranslationY(0.0f);
                view.setTranslationY(0.0f);
                if (ChatListItemAnimator.this.mAddAnimations.remove(holder)) {
                    ChatListItemAnimator.this.dispatchAddFinished(holder);
                    ChatListItemAnimator.this.dispatchFinishedWhenDone();
                }
            }
        });
        this.animators.put(holder, animatorSet);
        animatorSet.start();
    }

    public static /* synthetic */ void lambda$animateAddImpl$7(ChatMessageCell messageCell, float finalToX, float deltaX, float finalToY, float deltaY, float fromW, float toW, float fromH, float toH, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        messageCell.getTransitionParams().animateChangeProgress = v;
        if (messageCell.getTransitionParams().animateChangeProgress > 1.0f) {
            messageCell.getTransitionParams().animateChangeProgress = 1.0f;
        }
        messageCell.getPhotoImage().setImageCoords(((1.0f - v) * deltaX) + finalToX, ((1.0f - v) * deltaY) + finalToY, ((1.0f - v) * fromW) + (toW * v), ((1.0f - v) * fromH) + (toH * v));
        messageCell.invalidate();
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("animate remove impl");
        }
        final View view = holder.itemView;
        this.mRemoveAnimations.add(holder);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f);
        dispatchRemoveStarting(holder);
        animator.setDuration(getRemoveDuration());
        animator.addListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.ChatListItemAnimator.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                animator2.removeAllListeners();
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                if (ChatListItemAnimator.this.mRemoveAnimations.remove(holder)) {
                    ChatListItemAnimator.this.dispatchRemoveFinished(holder);
                    ChatListItemAnimator.this.dispatchFinishedWhenDone();
                }
            }
        });
        this.animators.put(holder, animator);
        animator.start();
        this.recyclerListView.stopScroll();
    }

    public void setShouldAnimateEnterFromBottom(boolean shouldAnimateEnterFromBottom) {
        this.shouldAnimateEnterFromBottom = shouldAnimateEnterFromBottom;
    }

    public void onAnimationStart() {
    }

    @Override // androidx.recyclerview.widget.DefaultItemAnimator
    protected long getMoveAnimationDelay() {
        return 0L;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getMoveDuration() {
        return 250L;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getChangeDuration() {
        return 250L;
    }

    public void runOnAnimationEnd(Runnable runnable) {
        this.runOnAnimationsEnd.add(runnable);
    }

    public void onDestroy() {
        onAllAnimationsDone();
    }

    public boolean willRemoved(View view) {
        RecyclerView.ViewHolder holder = this.recyclerListView.getChildViewHolder(view);
        if (holder != null) {
            return this.mPendingRemovals.contains(holder) || this.mRemoveAnimations.contains(holder);
        }
        return false;
    }

    public boolean willAddedFromAlpha(View view) {
        RecyclerView.ViewHolder holder;
        if (!this.shouldAnimateEnterFromBottom && (holder = this.recyclerListView.getChildViewHolder(view)) != null) {
            return this.mPendingAdditions.contains(holder) || this.mAddAnimations.contains(holder);
        }
        return false;
    }

    public void onGreetingStickerTransition(RecyclerView.ViewHolder holder, ChatGreetingsView greetingsViewContainer) {
        this.greetingsSticker = holder;
        this.chatGreetingsView = greetingsViewContainer;
        this.shouldAnimateEnterFromBottom = false;
    }

    public void setReversePositions(boolean reversePositions) {
        this.reversePositions = reversePositions;
    }

    /* loaded from: classes3.dex */
    public class MoveInfoExtended extends DefaultItemAnimator.MoveInfo {
        public boolean animateBackgroundOnly;
        public boolean animateChangeGroupBackground;
        public boolean animateChangeInternal;
        boolean animateImage;
        public boolean animatePinnedBottom;
        boolean animateRemoveGroup;
        public float captionDeltaX;
        public float captionDeltaY;
        int deltaBottom;
        int deltaLeft;
        int deltaRight;
        int deltaTop;
        boolean drawBackground;
        public int groupOffsetBottom;
        public int groupOffsetLeft;
        public int groupOffsetRight;
        public int groupOffsetTop;
        float imageHeight;
        float imageWidth;
        float imageX;
        float imageY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        MoveInfoExtended(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            super(holder, fromX, fromY, toX, toY);
            ChatListItemAnimator.this = this$0;
        }
    }

    /* loaded from: classes3.dex */
    public class ItemHolderInfoExtended extends RecyclerView.ItemAnimator.ItemHolderInfo {
        int captionX;
        int captionY;
        float imageHeight;
        float imageWidth;
        float imageX;
        float imageY;

        ItemHolderInfoExtended() {
            ChatListItemAnimator.this = this$0;
        }
    }
}
