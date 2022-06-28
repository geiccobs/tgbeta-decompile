package org.webrtc;

import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes5.dex */
class RefCountDelegate implements RefCounted {
    private final AtomicInteger refCount = new AtomicInteger(1);
    private final Runnable releaseCallback;

    public RefCountDelegate(Runnable releaseCallback) {
        this.releaseCallback = releaseCallback;
    }

    @Override // org.webrtc.RefCounted
    public void retain() {
        int updated_count = this.refCount.incrementAndGet();
        if (updated_count < 2) {
            throw new IllegalStateException("retain() called on an object with refcount < 1");
        }
    }

    @Override // org.webrtc.RefCounted
    public void release() {
        Runnable runnable;
        int updated_count = this.refCount.decrementAndGet();
        if (updated_count < 0) {
            throw new IllegalStateException("release() called on an object with refcount < 1");
        }
        if (updated_count == 0 && (runnable = this.releaseCallback) != null) {
            runnable.run();
        }
    }

    public boolean safeRetain() {
        int currentRefCount = this.refCount.get();
        while (currentRefCount != 0) {
            if (this.refCount.weakCompareAndSet(currentRefCount, currentRefCount + 1)) {
                return true;
            }
            currentRefCount = this.refCount.get();
        }
        return false;
    }
}
