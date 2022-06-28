package org.telegram.ui;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public class MessageEnterTransitionContainer extends View {
    private final int currentAccount;
    private final ViewGroup parent;
    private ArrayList<Transition> transitions = new ArrayList<>();
    Runnable hideRunnable = new Runnable() { // from class: org.telegram.ui.MessageEnterTransitionContainer$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            MessageEnterTransitionContainer.this.m3905lambda$new$0$orgtelegramuiMessageEnterTransitionContainer();
        }
    };

    /* loaded from: classes4.dex */
    public interface Transition {
        void onDraw(Canvas canvas);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-MessageEnterTransitionContainer */
    public /* synthetic */ void m3905lambda$new$0$orgtelegramuiMessageEnterTransitionContainer() {
        setVisibility(8);
    }

    public MessageEnterTransitionContainer(ViewGroup parent, int currentAccount) {
        super(parent.getContext());
        this.parent = parent;
        this.currentAccount = currentAccount;
    }

    public void addTransition(Transition transition) {
        this.transitions.add(transition);
        checkVisibility();
        this.parent.invalidate();
    }

    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        checkVisibility();
        this.parent.invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.transitions.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.transitions.size(); i++) {
            this.transitions.get(i).onDraw(canvas);
        }
    }

    private void checkVisibility() {
        if (this.transitions.isEmpty() && getVisibility() != 8) {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(this.hideRunnable);
        } else if (!this.transitions.isEmpty() && getVisibility() != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            setVisibility(0);
        }
    }

    public boolean isRunning() {
        return this.transitions.size() > 0;
    }
}
