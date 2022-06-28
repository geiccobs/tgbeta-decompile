package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
/* loaded from: classes5.dex */
public class DialogsItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = false;
    private static final int changeDuration = 180;
    private static final int deleteDuration = 180;
    private static TimeInterpolator sDefaultInterpolator = new DecelerateInterpolator();
    private int bottomClip;
    private final RecyclerListView listView;
    private DialogCell removingDialog;
    private int topClip;
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();

    /* loaded from: classes5.dex */
    public static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    public DialogsItemAnimator(RecyclerListView listView) {
        this.listView = listView;
    }

    /* loaded from: classes5.dex */
    public static class ChangeInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder newHolder;
        public RecyclerView.ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void runPendingAnimations() {
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
            final ArrayList<MoveInfo> moves = new ArrayList<>(this.mPendingMoves);
            this.mMovesList.add(moves);
            this.mPendingMoves.clear();
            Runnable mover = new Runnable() { // from class: org.telegram.ui.Components.DialogsItemAnimator$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsItemAnimator.this.m2556x2251e704(moves);
                }
            };
            mover.run();
        }
        if (changesPending) {
            final ArrayList<ChangeInfo> changes = new ArrayList<>(this.mPendingChanges);
            this.mChangesList.add(changes);
            this.mPendingChanges.clear();
            Runnable changer = new Runnable() { // from class: org.telegram.ui.Components.DialogsItemAnimator$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsItemAnimator.this.m2557x5c1c88e3(changes);
                }
            };
            changer.run();
        }
        if (additionsPending) {
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>(this.mPendingAdditions);
            this.mAdditionsList.add(additions);
            this.mPendingAdditions.clear();
            Runnable adder = new Runnable() { // from class: org.telegram.ui.Components.DialogsItemAnimator$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsItemAnimator.this.m2558x95e72ac2(additions);
                }
            };
            adder.run();
        }
    }

    /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-Components-DialogsItemAnimator */
    public /* synthetic */ void m2556x2251e704(ArrayList moves) {
        Iterator it = moves.iterator();
        while (it.hasNext()) {
            MoveInfo moveInfo = (MoveInfo) it.next();
            animateMoveImpl(moveInfo.holder, null, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
        }
        moves.clear();
        this.mMovesList.remove(moves);
    }

    /* renamed from: lambda$runPendingAnimations$1$org-telegram-ui-Components-DialogsItemAnimator */
    public /* synthetic */ void m2557x5c1c88e3(ArrayList changes) {
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            ChangeInfo change = (ChangeInfo) it.next();
            animateChangeImpl(change);
        }
        changes.clear();
        this.mChangesList.remove(changes);
    }

    /* renamed from: lambda$runPendingAnimations$2$org-telegram-ui-Components-DialogsItemAnimator */
    public /* synthetic */ void m2558x95e72ac2(ArrayList additions) {
        Iterator it = additions.iterator();
        while (it.hasNext()) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) it.next();
            animateAddImpl(holder);
        }
        additions.clear();
        this.mAdditionsList.remove(additions);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateRemove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        DialogCell bottomView = null;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View view = this.listView.getChildAt(i);
            if (view.getTop() > Integer.MIN_VALUE && (view instanceof DialogCell)) {
                bottomView = (DialogCell) view;
            }
        }
        if (holder.itemView == bottomView) {
            this.removingDialog = bottomView;
            return true;
        }
        return true;
    }

    public void prepareForRemove() {
        this.topClip = Integer.MAX_VALUE;
        this.bottomClip = Integer.MAX_VALUE;
        this.removingDialog = null;
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        this.mRemoveAnimations.add(holder);
        if (view instanceof DialogCell) {
            final DialogCell dialogCell = (DialogCell) view;
            DialogCell dialogCell2 = this.removingDialog;
            if (view == dialogCell2) {
                if (this.topClip != Integer.MAX_VALUE) {
                    int measuredHeight = dialogCell2.getMeasuredHeight();
                    int i = this.topClip;
                    this.bottomClip = measuredHeight - i;
                    this.removingDialog.setTopClip(i);
                    this.removingDialog.setBottomClip(this.bottomClip);
                } else if (this.bottomClip != Integer.MAX_VALUE) {
                    int measuredHeight2 = dialogCell2.getMeasuredHeight() - this.bottomClip;
                    this.topClip = measuredHeight2;
                    this.removingDialog.setTopClip(measuredHeight2);
                    this.removingDialog.setBottomClip(this.bottomClip);
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    dialogCell.setElevation(-1.0f);
                    dialogCell.setOutlineProvider(null);
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(dialogCell, AnimationProperties.CLIP_DIALOG_CELL_PROGRESS, 1.0f).setDuration(180L);
                animator.setInterpolator(sDefaultInterpolator);
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator2) {
                        DialogsItemAnimator.this.dispatchRemoveStarting(holder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        animator2.removeAllListeners();
                        dialogCell.setClipProgress(0.0f);
                        if (Build.VERSION.SDK_INT >= 21) {
                            dialogCell.setElevation(0.0f);
                        }
                        DialogsItemAnimator.this.dispatchRemoveFinished(holder);
                        DialogsItemAnimator.this.mRemoveAnimations.remove(holder);
                        DialogsItemAnimator.this.dispatchFinishedWhenDone();
                    }
                });
                animator.start();
                return;
            }
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(dialogCell, View.ALPHA, 1.0f).setDuration(180L);
            animator2.setInterpolator(sDefaultInterpolator);
            animator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator3) {
                    DialogsItemAnimator.this.dispatchRemoveStarting(holder);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator3) {
                    animator3.removeAllListeners();
                    dialogCell.setClipProgress(0.0f);
                    if (Build.VERSION.SDK_INT >= 21) {
                        dialogCell.setElevation(0.0f);
                    }
                    DialogsItemAnimator.this.dispatchRemoveFinished(holder);
                    DialogsItemAnimator.this.mRemoveAnimations.remove(holder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            animator2.start();
            return;
        }
        final ViewPropertyAnimator animation = view.animate();
        animation.setDuration(180L).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator3) {
                DialogsItemAnimator.this.dispatchRemoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator3) {
                animation.setListener(null);
                view.setAlpha(1.0f);
                DialogsItemAnimator.this.dispatchRemoveFinished(holder);
                DialogsItemAnimator.this.mRemoveAnimations.remove(holder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        if (!(holder.itemView instanceof DialogCell)) {
            holder.itemView.setAlpha(0.0f);
        }
        this.mPendingAdditions.add(holder);
        if (this.mPendingAdditions.size() > 2) {
            for (int i = 0; i < this.mPendingAdditions.size(); i++) {
                this.mPendingAdditions.get(i).itemView.setAlpha(0.0f);
                if (this.mPendingAdditions.get(i).itemView instanceof DialogCell) {
                    ((DialogCell) this.mPendingAdditions.get(i).itemView).setMoving(true);
                }
            }
        }
        return true;
    }

    void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        this.mAddAnimations.add(holder);
        final ViewPropertyAnimator animation = view.animate();
        animation.alpha(1.0f).setDuration(180L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                DialogsItemAnimator.this.dispatchAddFinished(holder);
                DialogsItemAnimator.this.mAddAnimations.remove(holder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
                if (holder.itemView instanceof DialogCell) {
                    ((DialogCell) holder.itemView).setMoving(false);
                }
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateMove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        int fromX2 = fromX + ((int) holder.itemView.getTranslationX());
        int fromY2 = fromY + ((int) holder.itemView.getTranslationY());
        resetAnimation(holder);
        int deltaX = toX - fromX2;
        int deltaY = toY - fromY2;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            view.setTranslationX(-deltaX);
        }
        if (deltaY != 0) {
            view.setTranslationY(-deltaY);
        }
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).setMoving(true);
        } else if (holder.itemView instanceof DialogsAdapter.LastEmptyView) {
            ((DialogsAdapter.LastEmptyView) holder.itemView).moving = true;
        }
        this.mPendingMoves.add(new MoveInfo(holder, fromX2, fromY2, toX, toY));
        return true;
    }

    public void onListScroll(int dy) {
        if (!this.mPendingRemovals.isEmpty()) {
            int N = this.mPendingRemovals.size();
            for (int a = 0; a < N; a++) {
                RecyclerView.ViewHolder holder = this.mPendingRemovals.get(a);
                holder.itemView.setTranslationY(holder.itemView.getTranslationY() + dy);
            }
        }
        if (!this.mRemoveAnimations.isEmpty()) {
            int N2 = this.mRemoveAnimations.size();
            for (int a2 = 0; a2 < N2; a2++) {
                RecyclerView.ViewHolder holder2 = this.mRemoveAnimations.get(a2);
                holder2.itemView.setTranslationY(holder2.itemView.getTranslationY() + dy);
            }
        }
    }

    void animateMoveImpl(final RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            view.animate().translationX(0.0f);
        }
        if (deltaY != 0) {
            view.animate().translationY(0.0f);
        }
        if (fromY > toY) {
            this.bottomClip = fromY - toY;
        } else {
            this.topClip = toY - fromY;
        }
        DialogCell dialogCell = this.removingDialog;
        if (dialogCell != null) {
            if (this.topClip != Integer.MAX_VALUE) {
                int measuredHeight = dialogCell.getMeasuredHeight();
                int i = this.topClip;
                this.bottomClip = measuredHeight - i;
                this.removingDialog.setTopClip(i);
                this.removingDialog.setBottomClip(this.bottomClip);
            } else if (this.bottomClip != Integer.MAX_VALUE) {
                int measuredHeight2 = dialogCell.getMeasuredHeight() - this.bottomClip;
                this.topClip = measuredHeight2;
                this.removingDialog.setTopClip(measuredHeight2);
                this.removingDialog.setBottomClip(this.bottomClip);
            }
        }
        final ViewPropertyAnimator animation = view.animate();
        this.mMoveAnimations.add(holder);
        animation.setDuration(180L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchMoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (deltaX != 0) {
                    view.setTranslationX(0.0f);
                }
                if (deltaY != 0) {
                    view.setTranslationY(0.0f);
                }
                if (holder.itemView instanceof DialogCell) {
                    ((DialogCell) holder.itemView).setMoving(false);
                } else if (holder.itemView instanceof DialogsAdapter.LastEmptyView) {
                    ((DialogsAdapter.LastEmptyView) holder.itemView).moving = false;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                DialogsItemAnimator.this.dispatchMoveFinished(holder);
                DialogsItemAnimator.this.mMoveAnimations.remove(holder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
                if (holder.itemView instanceof DialogCell) {
                    ((DialogCell) holder.itemView).setMoving(false);
                } else if (holder.itemView instanceof DialogsAdapter.LastEmptyView) {
                    ((DialogsAdapter.LastEmptyView) holder.itemView).moving = false;
                }
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        if (oldHolder.itemView instanceof DialogCell) {
            resetAnimation(oldHolder);
            resetAnimation(newHolder);
            oldHolder.itemView.setAlpha(1.0f);
            newHolder.itemView.setAlpha(0.0f);
            newHolder.itemView.setTranslationX(0.0f);
            this.mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
            return true;
        }
        return false;
    }

    void animateChangeImpl(final ChangeInfo changeInfo) {
        final RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        if (holder == null || newHolder == null) {
            return;
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180L);
        animatorSet.playTogether(ObjectAnimator.ofFloat(holder.itemView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(newHolder.itemView, View.ALPHA, 1.0f));
        this.mChangeAnimations.add(changeInfo.oldHolder);
        this.mChangeAnimations.add(changeInfo.newHolder);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.DialogsItemAnimator.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                holder.itemView.setAlpha(1.0f);
                animatorSet.removeAllListeners();
                DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
                DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        });
        animatorSet.start();
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                infoList.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder == item) {
            changeInfo.oldHolder = null;
            oldItem = true;
        } else {
            return false;
        }
        item.itemView.setAlpha(1.0f);
        item.itemView.setTranslationX(0.0f);
        item.itemView.setTranslationY(0.0f);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimation(RecyclerView.ViewHolder item) {
        View view = item.itemView;
        view.animate().cancel();
        int i = this.mPendingMoves.size();
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            MoveInfo moveInfo = this.mPendingMoves.get(i);
            if (moveInfo.holder == item) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                dispatchMoveFinished(item);
                this.mPendingMoves.remove(i);
            }
        }
        endChangeAnimation(this.mPendingChanges, item);
        if (this.mPendingRemovals.remove(item)) {
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
            dispatchRemoveFinished(item);
        }
        if (this.mPendingAdditions.remove(item)) {
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
            dispatchAddFinished(item);
        }
        for (int i2 = this.mChangesList.size() - 1; i2 >= 0; i2--) {
            ArrayList<ChangeInfo> changes = this.mChangesList.get(i2);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                this.mChangesList.remove(i2);
            }
        }
        for (int i3 = this.mMovesList.size() - 1; i3 >= 0; i3--) {
            ArrayList<MoveInfo> moves = this.mMovesList.get(i3);
            int j = moves.size() - 1;
            while (true) {
                if (j >= 0) {
                    MoveInfo moveInfo2 = moves.get(j);
                    if (moveInfo2.holder != item) {
                        j--;
                    } else {
                        view.setTranslationY(0.0f);
                        view.setTranslationX(0.0f);
                        dispatchMoveFinished(item);
                        moves.remove(j);
                        if (moves.isEmpty()) {
                            this.mMovesList.remove(i3);
                        }
                    }
                }
            }
        }
        for (int i4 = this.mAdditionsList.size() - 1; i4 >= 0; i4--) {
            ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i4);
            if (additions.remove(item)) {
                if (view instanceof DialogCell) {
                    ((DialogCell) view).setClipProgress(1.0f);
                } else {
                    view.setAlpha(1.0f);
                }
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(i4);
                }
            }
        }
        this.mRemoveAnimations.remove(item);
        this.mAddAnimations.remove(item);
        this.mChangeAnimations.remove(item);
        this.mMoveAnimations.remove(item);
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        holder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(holder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() || !this.mPendingChanges.isEmpty() || !this.mPendingMoves.isEmpty() || !this.mPendingRemovals.isEmpty() || !this.mMoveAnimations.isEmpty() || !this.mRemoveAnimations.isEmpty() || !this.mAddAnimations.isEmpty() || !this.mChangeAnimations.isEmpty() || !this.mMovesList.isEmpty() || !this.mAdditionsList.isEmpty() || !this.mChangesList.isEmpty();
    }

    void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            onAllAnimationsDone();
        }
    }

    protected void onAllAnimationsDone() {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimations() {
        int count = this.mPendingMoves.size();
        for (int i = count - 1; i >= 0; i--) {
            MoveInfo item = this.mPendingMoves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(item.holder);
            this.mPendingMoves.remove(i);
        }
        int count2 = this.mPendingRemovals.size();
        for (int i2 = count2 - 1; i2 >= 0; i2--) {
            RecyclerView.ViewHolder item2 = this.mPendingRemovals.get(i2);
            View view2 = item2.itemView;
            view2.setTranslationY(0.0f);
            view2.setTranslationX(0.0f);
            dispatchRemoveFinished(item2);
            this.mPendingRemovals.remove(i2);
        }
        int count3 = this.mPendingAdditions.size();
        for (int i3 = count3 - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item3 = this.mPendingAdditions.get(i3);
            if (item3.itemView instanceof DialogCell) {
                ((DialogCell) item3.itemView).setClipProgress(0.0f);
            } else {
                item3.itemView.setAlpha(1.0f);
            }
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
            ArrayList<MoveInfo> moves = this.mMovesList.get(i5);
            int count5 = moves.size();
            for (int j = count5 - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                View view3 = moveInfo.holder.itemView;
                view3.setTranslationY(0.0f);
                view3.setTranslationX(0.0f);
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
                View view4 = item4.itemView;
                if (view4 instanceof DialogCell) {
                    ((DialogCell) view4).setClipProgress(0.0f);
                } else {
                    view4.setAlpha(1.0f);
                }
                dispatchAddFinished(item4);
                additions.remove(j2);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(additions);
                }
            }
        }
        int listCount3 = this.mChangesList.size();
        for (int i7 = listCount3 - 1; i7 >= 0; i7--) {
            ArrayList<ChangeInfo> changes = this.mChangesList.get(i7);
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

    void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            viewHolders.get(i).itemView.animate().cancel();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        return viewHolder.itemView instanceof DialogsEmptyCell;
    }
}
