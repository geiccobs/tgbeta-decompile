package com.google.android.exoplayer2.util;
/* loaded from: classes3.dex */
public final class ConditionVariable {
    private final Clock clock;
    private boolean isOpen;

    public ConditionVariable() {
        this(Clock.DEFAULT);
    }

    public ConditionVariable(Clock clock) {
        this.clock = clock;
    }

    public synchronized boolean open() {
        if (this.isOpen) {
            return false;
        }
        this.isOpen = true;
        notifyAll();
        return true;
    }

    public synchronized boolean close() {
        boolean wasOpen;
        wasOpen = this.isOpen;
        this.isOpen = false;
        return wasOpen;
    }

    public synchronized void block() throws InterruptedException {
        while (!this.isOpen) {
            wait();
        }
    }

    public synchronized boolean block(long timeoutMs) throws InterruptedException {
        if (timeoutMs <= 0) {
            return this.isOpen;
        }
        long nowMs = this.clock.elapsedRealtime();
        long endMs = nowMs + timeoutMs;
        if (endMs < nowMs) {
            block();
        } else {
            while (!this.isOpen && nowMs < endMs) {
                wait(endMs - nowMs);
                nowMs = this.clock.elapsedRealtime();
            }
        }
        return this.isOpen;
    }

    public synchronized boolean isOpen() {
        return this.isOpen;
    }
}
