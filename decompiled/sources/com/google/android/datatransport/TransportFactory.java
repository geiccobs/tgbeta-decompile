package com.google.android.datatransport;
/* loaded from: classes.dex */
public interface TransportFactory {
    <T> Transport<T> getTransport(String str, Class<T> cls, Encoding encoding, Transformer<T, byte[]> transformer);
}
