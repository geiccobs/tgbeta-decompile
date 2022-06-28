package com.google.android.exoplayer2.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
/* loaded from: classes3.dex */
final class SystemHandlerWrapper implements HandlerWrapper {
    private final Handler handler;

    public SystemHandlerWrapper(Handler handler) {
        this.handler = handler;
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public Looper getLooper() {
        return this.handler.getLooper();
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public Message obtainMessage(int what) {
        return this.handler.obtainMessage(what);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public Message obtainMessage(int what, Object obj) {
        return this.handler.obtainMessage(what, obj);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public Message obtainMessage(int what, int arg1, int arg2) {
        return this.handler.obtainMessage(what, arg1, arg2);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return this.handler.obtainMessage(what, arg1, arg2, obj);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public boolean sendEmptyMessage(int what) {
        return this.handler.sendEmptyMessage(what);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public boolean sendEmptyMessageAtTime(int what, long uptimeMs) {
        return this.handler.sendEmptyMessageAtTime(what, uptimeMs);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public void removeMessages(int what) {
        this.handler.removeMessages(what);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public void removeCallbacksAndMessages(Object token) {
        this.handler.removeCallbacksAndMessages(token);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public boolean post(Runnable runnable) {
        return this.handler.post(runnable);
    }

    @Override // com.google.android.exoplayer2.util.HandlerWrapper
    public boolean postDelayed(Runnable runnable, long delayMs) {
        return this.handler.postDelayed(runnable, delayMs);
    }
}
