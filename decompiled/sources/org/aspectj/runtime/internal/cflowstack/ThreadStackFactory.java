package org.aspectj.runtime.internal.cflowstack;
/* loaded from: classes3.dex */
public interface ThreadStackFactory {
    ThreadCounter getNewThreadCounter();

    ThreadStack getNewThreadStack();
}
