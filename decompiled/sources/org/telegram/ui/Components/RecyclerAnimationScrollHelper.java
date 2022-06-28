package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class RecyclerAnimationScrollHelper {
    public static final int SCROLL_DIRECTION_DOWN = 0;
    public static final int SCROLL_DIRECTION_UNSET = -1;
    public static final int SCROLL_DIRECTION_UP = 1;
    private AnimationCallback animationCallback;
    private ValueAnimator animator;
    private LinearLayoutManager layoutManager;
    private RecyclerListView recyclerView;
    private int scrollDirection;
    private ScrollListener scrollListener;
    public SparseArray<View> positionToOldView = new SparseArray<>();
    private HashMap<Long, View> oldStableIds = new HashMap<>();

    /* loaded from: classes5.dex */
    public interface ScrollListener {
        void onScroll();
    }

    public RecyclerAnimationScrollHelper(RecyclerListView recyclerView, LinearLayoutManager layoutManager) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
    }

    public void scrollToPosition(int position, int offset) {
        scrollToPosition(position, offset, this.layoutManager.getReverseLayout(), false);
    }

    public void scrollToPosition(int position, int offset, boolean bottom) {
        scrollToPosition(position, offset, bottom, false);
    }

    public void scrollToPosition(int position, int offset, boolean bottom, boolean smooth) {
        AnimatableAdapter animatableAdapter;
        if (!this.recyclerView.fastScrollAnimationRunning) {
            if (this.recyclerView.getItemAnimator() != null && this.recyclerView.getItemAnimator().isRunning()) {
                return;
            }
            if (!smooth || this.scrollDirection == -1) {
                this.layoutManager.scrollToPositionWithOffset(position, offset, bottom);
                return;
            }
            int n = this.recyclerView.getChildCount();
            if (n != 0 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
                boolean scrollDown = this.scrollDirection == 0;
                this.recyclerView.setScrollEnabled(false);
                ArrayList<View> oldViews = new ArrayList<>();
                this.positionToOldView.clear();
                RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
                this.oldStableIds.clear();
                for (int i = 0; i < n; i++) {
                    View child = this.recyclerView.getChildAt(i);
                    oldViews.add(child);
                    int childPosition = this.layoutManager.getPosition(child);
                    this.positionToOldView.put(childPosition, child);
                    if (adapter != null && adapter.hasStableIds()) {
                        long itemId = ((RecyclerView.LayoutParams) child.getLayoutParams()).mViewHolder.getItemId();
                        this.oldStableIds.put(Long.valueOf(itemId), child);
                    }
                    if (child instanceof ChatMessageCell) {
                        ((ChatMessageCell) child).setAnimationRunning(true, true);
                    }
                }
                this.recyclerView.prepareForFastScroll();
                if (!(adapter instanceof AnimatableAdapter)) {
                    animatableAdapter = null;
                } else {
                    AnimatableAdapter animatableAdapter2 = (AnimatableAdapter) adapter;
                    animatableAdapter = animatableAdapter2;
                }
                this.layoutManager.scrollToPositionWithOffset(position, offset, bottom);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                AnimatableAdapter finalAnimatableAdapter = animatableAdapter;
                this.recyclerView.stopScroll();
                this.recyclerView.setVerticalScrollBarEnabled(false);
                AnimationCallback animationCallback = this.animationCallback;
                if (animationCallback != null) {
                    animationCallback.onStartAnimation();
                }
                this.recyclerView.fastScrollAnimationRunning = true;
                if (finalAnimatableAdapter != null) {
                    finalAnimatableAdapter.onAnimationStart();
                }
                this.recyclerView.addOnLayoutChangeListener(new AnonymousClass1(adapter, oldViews, scrollDown, finalAnimatableAdapter));
                return;
            }
            this.layoutManager.scrollToPositionWithOffset(position, offset, bottom);
        }
    }

    /* renamed from: org.telegram.ui.Components.RecyclerAnimationScrollHelper$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements View.OnLayoutChangeListener {
        final /* synthetic */ RecyclerView.Adapter val$adapter;
        final /* synthetic */ AnimatableAdapter val$finalAnimatableAdapter;
        final /* synthetic */ ArrayList val$oldViews;
        final /* synthetic */ boolean val$scrollDown;

        AnonymousClass1(RecyclerView.Adapter adapter, ArrayList arrayList, boolean z, AnimatableAdapter animatableAdapter) {
            RecyclerAnimationScrollHelper.this = this$0;
            this.val$adapter = adapter;
            this.val$oldViews = arrayList;
            this.val$scrollDown = z;
            this.val$finalAnimatableAdapter = animatableAdapter;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
            int oldT;
            int scrollLength;
            long duration;
            View view;
            final ArrayList<View> incomingViews = new ArrayList<>();
            RecyclerAnimationScrollHelper.this.recyclerView.stopScroll();
            int n = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
            int top = 0;
            int bottom = 0;
            int scrollDiff = 0;
            boolean hasSameViews = false;
            for (int i = 0; i < n; i++) {
                View child = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                incomingViews.add(child);
                if (child.getTop() < top) {
                    top = child.getTop();
                }
                if (child.getBottom() > bottom) {
                    bottom = child.getBottom();
                }
                if (child instanceof ChatMessageCell) {
                    ((ChatMessageCell) child).setAnimationRunning(true, false);
                }
                RecyclerView.Adapter adapter = this.val$adapter;
                if (adapter != null && adapter.hasStableIds()) {
                    long stableId = this.val$adapter.getItemId(RecyclerAnimationScrollHelper.this.recyclerView.getChildAdapterPosition(child));
                    if (RecyclerAnimationScrollHelper.this.oldStableIds.containsKey(Long.valueOf(stableId)) && (view = (View) RecyclerAnimationScrollHelper.this.oldStableIds.get(Long.valueOf(stableId))) != null) {
                        if (view instanceof ChatMessageCell) {
                            ((ChatMessageCell) view).setAnimationRunning(false, false);
                        }
                        this.val$oldViews.remove(view);
                        if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                            RecyclerAnimationScrollHelper.this.animationCallback.recycleView(view);
                        }
                        int dif = child.getTop() - view.getTop();
                        if (dif != 0) {
                            hasSameViews = true;
                            scrollDiff = dif;
                        } else {
                            hasSameViews = true;
                        }
                    }
                }
            }
            RecyclerAnimationScrollHelper.this.oldStableIds.clear();
            int oldT2 = Integer.MAX_VALUE;
            Iterator it = this.val$oldViews.iterator();
            int oldH = 0;
            while (it.hasNext()) {
                View view2 = (View) it.next();
                int bot = view2.getBottom();
                int topl = view2.getTop();
                if (bot > oldH) {
                    oldH = bot;
                }
                if (topl < oldT2) {
                    oldT2 = topl;
                }
                if (view2.getParent() == null) {
                    RecyclerAnimationScrollHelper.this.recyclerView.addView(view2);
                    RecyclerAnimationScrollHelper.this.layoutManager.ignoreView(view2);
                }
                if (view2 instanceof ChatMessageCell) {
                    ((ChatMessageCell) view2).setAnimationRunning(true, true);
                }
            }
            if (oldT2 != Integer.MAX_VALUE) {
                oldT = oldT2;
            } else {
                oldT = 0;
            }
            if (this.val$oldViews.isEmpty()) {
                scrollLength = Math.abs(scrollDiff);
            } else {
                int finalHeight = this.val$scrollDown ? oldH : RecyclerAnimationScrollHelper.this.recyclerView.getHeight() - oldT;
                scrollLength = (this.val$scrollDown ? -top : bottom - RecyclerAnimationScrollHelper.this.recyclerView.getHeight()) + finalHeight;
            }
            if (RecyclerAnimationScrollHelper.this.animator != null) {
                RecyclerAnimationScrollHelper.this.animator.removeAllListeners();
                RecyclerAnimationScrollHelper.this.animator.cancel();
            }
            RecyclerAnimationScrollHelper.this.animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            ValueAnimator valueAnimator = RecyclerAnimationScrollHelper.this.animator;
            final ArrayList arrayList = this.val$oldViews;
            final boolean z = this.val$scrollDown;
            final int i2 = scrollLength;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    RecyclerAnimationScrollHelper.AnonymousClass1.this.m2954xb4285cce(arrayList, z, i2, incomingViews, valueAnimator2);
                }
            });
            RecyclerAnimationScrollHelper.this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.RecyclerAnimationScrollHelper.1.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (RecyclerAnimationScrollHelper.this.animator != null) {
                        RecyclerAnimationScrollHelper.this.recyclerView.fastScrollAnimationRunning = false;
                        Iterator it2 = AnonymousClass1.this.val$oldViews.iterator();
                        while (it2.hasNext()) {
                            View view3 = (View) it2.next();
                            if (view3 instanceof ChatMessageCell) {
                                ((ChatMessageCell) view3).setAnimationRunning(false, true);
                            }
                            view3.setTranslationY(0.0f);
                            RecyclerAnimationScrollHelper.this.layoutManager.stopIgnoringView(view3);
                            RecyclerAnimationScrollHelper.this.recyclerView.removeView(view3);
                            if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                                RecyclerAnimationScrollHelper.this.animationCallback.recycleView(view3);
                            }
                        }
                        RecyclerAnimationScrollHelper.this.recyclerView.setScrollEnabled(true);
                        RecyclerAnimationScrollHelper.this.recyclerView.setVerticalScrollBarEnabled(true);
                        if (BuildVars.DEBUG_PRIVATE_VERSION) {
                            if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getChildCount() == RecyclerAnimationScrollHelper.this.recyclerView.getChildCount()) {
                                if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getHiddenChildCount() != 0) {
                                    throw new RuntimeException("hidden child count must be 0");
                                }
                            } else {
                                throw new RuntimeException("views count in child helper must be quals views count in recycler view");
                            }
                        }
                        int n2 = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                        for (int i3 = 0; i3 < n2; i3++) {
                            View child2 = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i3);
                            if (child2 instanceof ChatMessageCell) {
                                ((ChatMessageCell) child2).setAnimationRunning(false, false);
                            }
                            child2.setTranslationY(0.0f);
                        }
                        Iterator it3 = incomingViews.iterator();
                        while (it3.hasNext()) {
                            View v2 = (View) it3.next();
                            if (v2 instanceof ChatMessageCell) {
                                ((ChatMessageCell) v2).setAnimationRunning(false, false);
                            }
                            v2.setTranslationY(0.0f);
                        }
                        if (AnonymousClass1.this.val$finalAnimatableAdapter != null) {
                            AnonymousClass1.this.val$finalAnimatableAdapter.onAnimationEnd();
                        }
                        if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                            RecyclerAnimationScrollHelper.this.animationCallback.onEndAnimation();
                        }
                        RecyclerAnimationScrollHelper.this.positionToOldView.clear();
                        RecyclerAnimationScrollHelper.this.animator = null;
                    }
                }
            });
            RecyclerAnimationScrollHelper.this.recyclerView.removeOnLayoutChangeListener(this);
            if (!hasSameViews) {
                long duration2 = ((scrollLength / RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight()) + 1.0f) * 200.0f;
                if (duration2 < 300) {
                    duration2 = 300;
                }
                duration = Math.min(duration2, 1300L);
            } else {
                duration = 600;
            }
            RecyclerAnimationScrollHelper.this.animator.setDuration(duration);
            RecyclerAnimationScrollHelper.this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            RecyclerAnimationScrollHelper.this.animator.start();
        }

        /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-RecyclerAnimationScrollHelper$1 */
        public /* synthetic */ void m2954xb4285cce(ArrayList oldViews, boolean scrollDown, int scrollLength, ArrayList incomingViews, ValueAnimator animation) {
            float value = ((Float) animation.getAnimatedValue()).floatValue();
            int size = oldViews.size();
            for (int i = 0; i < size; i++) {
                View view = (View) oldViews.get(i);
                float viewTop = view.getY();
                float viewBottom = view.getY() + view.getMeasuredHeight();
                if (viewBottom >= 0.0f && viewTop <= RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight()) {
                    if (scrollDown) {
                        view.setTranslationY((-scrollLength) * value);
                    } else {
                        view.setTranslationY(scrollLength * value);
                    }
                }
            }
            int size2 = incomingViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                View view2 = (View) incomingViews.get(i2);
                if (scrollDown) {
                    view2.setTranslationY(scrollLength * (1.0f - value));
                } else {
                    view2.setTranslationY((-scrollLength) * (1.0f - value));
                }
            }
            RecyclerAnimationScrollHelper.this.recyclerView.invalidate();
            if (RecyclerAnimationScrollHelper.this.scrollListener != null) {
                RecyclerAnimationScrollHelper.this.scrollListener.onScroll();
            }
        }
    }

    public void cancel() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        clear();
    }

    private void clear() {
        this.recyclerView.setVerticalScrollBarEnabled(true);
        this.recyclerView.fastScrollAnimationRunning = false;
        RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
        if (adapter instanceof AnimatableAdapter) {
            ((AnimatableAdapter) adapter).onAnimationEnd();
        }
        this.animator = null;
        int n = this.recyclerView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = this.recyclerView.getChildAt(i);
            child.setTranslationY(0.0f);
            if (child instanceof ChatMessageCell) {
                ((ChatMessageCell) child).setAnimationRunning(false, false);
            }
        }
    }

    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public void setScrollListener(ScrollListener listener) {
        this.scrollListener = listener;
    }

    public void setAnimationCallback(AnimationCallback animationCallback) {
        this.animationCallback = animationCallback;
    }

    public int getScrollDirection() {
        return this.scrollDirection;
    }

    /* loaded from: classes5.dex */
    public static class AnimationCallback {
        public void onStartAnimation() {
        }

        public void onEndAnimation() {
        }

        public void recycleView(View view) {
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class AnimatableAdapter extends RecyclerListView.SelectionAdapter {
        public boolean animationRunning;
        private ArrayList<Integer> rangeInserted = new ArrayList<>();
        private ArrayList<Integer> rangeRemoved = new ArrayList<>();
        private boolean shouldNotifyDataSetChanged;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            if (!this.animationRunning) {
                super.notifyDataSetChanged();
            } else {
                this.shouldNotifyDataSetChanged = true;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int position) {
            if (!this.animationRunning) {
                super.notifyItemInserted(position);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(position));
            this.rangeInserted.add(1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeInserted(positionStart, itemCount);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(positionStart));
            this.rangeInserted.add(Integer.valueOf(itemCount));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            if (!this.animationRunning) {
                super.notifyItemRemoved(position);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(position));
            this.rangeRemoved.add(1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeRemoved(positionStart, itemCount);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(positionStart));
            this.rangeRemoved.add(Integer.valueOf(itemCount));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position) {
            if (!this.animationRunning) {
                super.notifyItemChanged(position);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeChanged(positionStart, itemCount);
            }
        }

        public void onAnimationStart() {
            this.animationRunning = true;
            this.shouldNotifyDataSetChanged = false;
            this.rangeInserted.clear();
            this.rangeRemoved.clear();
        }

        public void onAnimationEnd() {
            this.animationRunning = false;
            if (this.shouldNotifyDataSetChanged || !this.rangeInserted.isEmpty() || !this.rangeRemoved.isEmpty()) {
                notifyDataSetChanged();
            }
        }
    }
}
