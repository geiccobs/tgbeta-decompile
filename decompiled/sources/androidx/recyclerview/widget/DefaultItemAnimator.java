package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.BuildVars;
/* loaded from: classes3.dex */
public class DefaultItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = BuildVars.DEBUG_VERSION;
    private static TimeInterpolator sDefaultInterpolator;
    protected Interpolator translationInterpolator;
    protected ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    protected ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    protected ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    ArrayList<MoveInfo> currentMoves = new ArrayList<>();
    ArrayList<ChangeInfo> currentChanges = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();
    protected boolean delayAnimations = true;

    /* loaded from: classes3.dex */
    public static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        public MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    /* loaded from: classes3.dex */
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

        public ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
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
            final ArrayList<MoveInfo> moves = new ArrayList<>();
            moves.addAll(this.mPendingMoves);
            this.mMovesList.add(moves);
            this.mPendingMoves.clear();
            Runnable mover = new Runnable() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.1
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it2 = moves.iterator();
                    while (it2.hasNext()) {
                        MoveInfo moveInfo = (MoveInfo) it2.next();
                        DefaultItemAnimator.this.animateMoveImpl(moveInfo.holder, moveInfo);
                        DefaultItemAnimator.this.currentMoves.add(moveInfo);
                    }
                    moves.clear();
                    DefaultItemAnimator.this.mMovesList.remove(moves);
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
            final ArrayList<ChangeInfo> changes = new ArrayList<>();
            changes.addAll(this.mPendingChanges);
            this.mChangesList.add(changes);
            this.mPendingChanges.clear();
            Runnable changer = new Runnable() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.2
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it2 = changes.iterator();
                    while (it2.hasNext()) {
                        ChangeInfo change = (ChangeInfo) it2.next();
                        DefaultItemAnimator.this.animateChangeImpl(change);
                        DefaultItemAnimator.this.currentChanges.add(change);
                    }
                    changes.clear();
                    DefaultItemAnimator.this.mChangesList.remove(changes);
                }
            };
            if (this.delayAnimations && removalsPending) {
                RecyclerView.ViewHolder holder2 = changes.get(0).oldHolder;
                ViewCompat.postOnAnimationDelayed(holder2.itemView, changer, getRemoveDuration());
            } else {
                changer.run();
            }
        }
        if (additionsPending) {
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(this.mPendingAdditions);
            this.mAdditionsList.add(additions);
            this.mPendingAdditions.clear();
            Runnable adder = new Runnable() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.3
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it2 = additions.iterator();
                    while (it2.hasNext()) {
                        RecyclerView.ViewHolder holder3 = (RecyclerView.ViewHolder) it2.next();
                        DefaultItemAnimator.this.animateAddImpl(holder3);
                    }
                    additions.clear();
                    DefaultItemAnimator.this.mAdditionsList.remove(additions);
                }
            };
            if (this.delayAnimations && (removalsPending || movesPending || changesPending)) {
                long changeDuration = 0;
                long removeDuration = removalsPending ? getRemoveDuration() : 0L;
                long moveDuration = movesPending ? getMoveDuration() : 0L;
                if (changesPending) {
                    changeDuration = getChangeDuration();
                }
                long totalDelay = getAddAnimationDelay(removeDuration, moveDuration, changeDuration);
                View view2 = additions.get(0).itemView;
                ViewCompat.postOnAnimationDelayed(view2, adder, totalDelay);
                return;
            }
            adder.run();
        }
    }

    protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
        return Math.max(moveDuration, changeDuration) + removeDuration;
    }

    protected long getMoveAnimationDelay() {
        return getRemoveDuration();
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateRemove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        checkIsRunning();
        return true;
    }

    public void setDelayAnimations(boolean value) {
        this.delayAnimations = value;
    }

    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mRemoveAnimations.add(holder);
        if (getRemoveDelay() > 0) {
            ((ViewGroup) view.getParent()).bringChildToFront(view);
        }
        animation.setDuration(getRemoveDuration()).setStartDelay(getRemoveDelay()).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DefaultItemAnimator.this.dispatchRemoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                view.setAlpha(1.0f);
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                DefaultItemAnimator.this.dispatchRemoveFinished(holder);
                DefaultItemAnimator.this.mRemoveAnimations.remove(holder);
                DefaultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        holder.itemView.setAlpha(0.0f);
        this.mPendingAdditions.add(holder);
        checkIsRunning();
        return true;
    }

    public void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mAddAnimations.add(holder);
        animation.alpha(1.0f).setDuration(getAddDuration()).setStartDelay(getAddDelay()).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DefaultItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                DefaultItemAnimator.this.dispatchAddFinished(holder);
                DefaultItemAnimator.this.mAddAnimations.remove(holder);
                DefaultItemAnimator.this.dispatchFinishedWhenDone();
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
        this.mPendingMoves.add(new MoveInfo(holder, fromX2, fromY2, toX, toY));
        checkIsRunning();
        return true;
    }

    protected void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
    }

    public void animateMoveImpl(final RecyclerView.ViewHolder holder, MoveInfo moveInfo) {
        int fromX = moveInfo.fromX;
        int fromY = moveInfo.fromY;
        int toX = moveInfo.toX;
        int toY = moveInfo.toY;
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            view.animate().translationX(0.0f);
        }
        if (deltaY != 0) {
            view.animate().translationY(0.0f);
        }
        final ViewPropertyAnimator animation = view.animate();
        this.mMoveAnimations.add(holder);
        if (Build.VERSION.SDK_INT >= 19) {
            animation.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: androidx.recyclerview.widget.DefaultItemAnimator$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DefaultItemAnimator.this.m4x2d19633a(holder, valueAnimator);
                }
            });
        }
        Interpolator interpolator = this.translationInterpolator;
        if (interpolator != null) {
            animation.setInterpolator(interpolator);
        }
        animation.setDuration(getMoveDuration()).setStartDelay(getMoveDelay()).setInterpolator(getMoveInterpolator()).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DefaultItemAnimator.this.dispatchMoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (deltaX != 0) {
                    view.setTranslationX(0.0f);
                }
                if (deltaY != 0) {
                    view.setTranslationY(0.0f);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                DefaultItemAnimator.this.dispatchMoveFinished(holder);
                DefaultItemAnimator.this.mMoveAnimations.remove(holder);
                DefaultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    /* renamed from: lambda$animateMoveImpl$0$androidx-recyclerview-widget-DefaultItemAnimator */
    public /* synthetic */ void m4x2d19633a(RecyclerView.ViewHolder holder, ValueAnimator animation1) {
        onMoveAnimationUpdate(holder);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        if (oldHolder == newHolder) {
            return animateMove(oldHolder, info, fromX, fromY, toX, toY);
        }
        float prevTranslationX = oldHolder.itemView.getTranslationX();
        float prevTranslationY = oldHolder.itemView.getTranslationY();
        float prevAlpha = oldHolder.itemView.getAlpha();
        resetAnimation(oldHolder);
        int deltaX = (int) ((toX - fromX) - prevTranslationX);
        int deltaY = (int) ((toY - fromY) - prevTranslationY);
        oldHolder.itemView.setTranslationX(prevTranslationX);
        oldHolder.itemView.setTranslationY(prevTranslationY);
        oldHolder.itemView.setAlpha(prevAlpha);
        if (newHolder != null) {
            resetAnimation(newHolder);
            newHolder.itemView.setTranslationX(-deltaX);
            newHolder.itemView.setTranslationY(-deltaY);
            newHolder.itemView.setAlpha(0.0f);
        }
        this.mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
        checkIsRunning();
        return true;
    }

    void animateChangeImpl(final ChangeInfo changeInfo) {
        RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        final View newView = null;
        final View view = holder == null ? null : holder.itemView;
        RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        if (newHolder != null) {
            newView = newHolder.itemView;
        }
        if (view != null) {
            final ViewPropertyAnimator oldViewAnim = view.animate().setDuration(getChangeRemoveDuration()).setStartDelay(getChangeDelay());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            oldViewAnim.translationX(changeInfo.toX - changeInfo.fromX);
            oldViewAnim.translationY(changeInfo.toY - changeInfo.fromY);
            oldViewAnim.alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    DefaultItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    oldViewAnim.setListener(null);
                    view.setAlpha(1.0f);
                    view.setTranslationX(0.0f);
                    view.setTranslationY(0.0f);
                    DefaultItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                    DefaultItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                    DefaultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
        if (newView != null) {
            final ViewPropertyAnimator newViewAnimation = newView.animate();
            this.mChangeAnimations.add(changeInfo.newHolder);
            newViewAnimation.translationX(0.0f).translationY(0.0f).setDuration(getChangeAddDuration()).setStartDelay(getChangeDelay() + (getChangeDuration() - getChangeAddDuration())).alpha(1.0f).setListener(new AnimatorListenerAdapter() { // from class: androidx.recyclerview.widget.DefaultItemAnimator.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    DefaultItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    newViewAnimation.setListener(null);
                    newView.setAlpha(1.0f);
                    newView.setTranslationX(0.0f);
                    newView.setTranslationY(0.0f);
                    DefaultItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                    DefaultItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                    DefaultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                infoList.remove(changeInfo);
            }
        }
    }

    public void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    protected boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
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
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            dispatchRemoveFinished(item);
        }
        if (this.mPendingAdditions.remove(item)) {
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
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
                view.setAlpha(1.0f);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(i4);
                }
            }
        }
        if (this.mRemoveAnimations.remove(item) && BuildVars.DEBUG_VERSION) {
            throw new IllegalStateException("after animation is cancelled, item should not be in mRemoveAnimations list");
        }
        if (this.mAddAnimations.remove(item) && BuildVars.DEBUG_VERSION) {
            throw new IllegalStateException("after animation is cancelled, item should not be in mAddAnimations list");
        }
        if (this.mChangeAnimations.remove(item) && BuildVars.DEBUG_VERSION) {
            throw new IllegalStateException("after animation is cancelled, item should not be in mChangeAnimations list");
        }
        if (this.mMoveAnimations.remove(item) && BuildVars.DEBUG_VERSION) {
            throw new IllegalStateException("after animation is cancelled, item should not be in mMoveAnimations list");
        }
        dispatchFinishedWhenDone();
    }

    public void resetAnimation(RecyclerView.ViewHolder holder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(holder);
    }

    public float getTargetY(View view) {
        for (int i = this.currentMoves.size() - 1; i >= 0; i--) {
            MoveInfo moveInfo = this.currentMoves.get(i);
            if (moveInfo.holder.itemView == view) {
                return Math.min(Math.min(moveInfo.toY, moveInfo.fromY), view.getY());
            }
        }
        for (int i2 = this.currentChanges.size() - 1; i2 >= 0; i2--) {
            ChangeInfo changeInfo = this.currentChanges.get(i2);
            if (changeInfo.oldHolder.itemView == view || changeInfo.newHolder.itemView == view) {
                return Math.min(Math.min(changeInfo.toY, changeInfo.fromY), view.getY());
            }
        }
        return view.getY();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() || !this.mPendingChanges.isEmpty() || !this.mPendingMoves.isEmpty() || !this.mPendingRemovals.isEmpty() || !this.mMoveAnimations.isEmpty() || !this.mRemoveAnimations.isEmpty() || !this.mAddAnimations.isEmpty() || !this.mChangeAnimations.isEmpty() || !this.mMovesList.isEmpty() || !this.mAdditionsList.isEmpty() || !this.mChangesList.isEmpty();
    }

    public void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            onAllAnimationsDone();
            this.currentMoves.clear();
            this.currentChanges.clear();
        }
    }

    public void onAllAnimationsDone() {
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
            dispatchRemoveFinished(this.mPendingRemovals.get(i2));
            this.mPendingRemovals.remove(i2);
        }
        int count3 = this.mPendingAdditions.size();
        for (int i3 = count3 - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item2 = this.mPendingAdditions.get(i3);
            item2.itemView.setAlpha(1.0f);
            dispatchAddFinished(item2);
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
                View view2 = moveInfo.holder.itemView;
                view2.setTranslationY(0.0f);
                view2.setTranslationX(0.0f);
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
                RecyclerView.ViewHolder item3 = additions.get(j2);
                item3.itemView.setAlpha(1.0f);
                dispatchAddFinished(item3);
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

    public void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            viewHolders.get(i).itemView.animate().cancel();
        }
    }

    public boolean isHolderRemoving(RecyclerView.ViewHolder holder) {
        return this.mRemoveAnimations.contains(holder);
    }

    public boolean isHolderAdding(RecyclerView.ViewHolder holder) {
        return this.mAddAnimations.contains(holder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }

    public void setTranslationInterpolator(Interpolator translationInterpolator) {
        this.translationInterpolator = translationInterpolator;
    }

    public void checkIsRunning() {
    }
}
