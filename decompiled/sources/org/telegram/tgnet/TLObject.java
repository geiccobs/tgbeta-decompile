package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLObject {
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new ThreadLocal<NativeByteBuffer>() { // from class: org.telegram.tgnet.TLObject.1
        @Override // java.lang.ThreadLocal
        public NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    };
    public boolean disableFree = false;
    public int networkType;

    public void readParams(AbstractSerializedData stream, boolean exception) {
    }

    public void serializeToStream(AbstractSerializedData stream) {
    }

    public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
        return null;
    }

    public void freeResources() {
    }

    public int getObjectSize() {
        ThreadLocal<NativeByteBuffer> threadLocal = sizeCalculator;
        NativeByteBuffer byteBuffer = threadLocal.get();
        byteBuffer.rewind();
        serializeToStream(threadLocal.get());
        return byteBuffer.length();
    }
}
