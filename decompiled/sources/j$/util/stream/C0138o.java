package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.wrappers.C0219j0;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.o */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0138o implements Consumer, j$.util.function.y {
    public final /* synthetic */ int a = 5;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ C0138o(BiConsumer biConsumer, Object obj) {
        this.b = biConsumer;
        this.c = obj;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.a) {
            case 0:
                AtomicBoolean atomicBoolean = (AtomicBoolean) this.b;
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) this.c;
                if (obj == null) {
                    atomicBoolean.set(true);
                    return;
                } else {
                    concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
                    return;
                }
            case 5:
                ((BiConsumer) this.b).accept(this.c, obj);
                return;
            default:
                ((C0131m4) this.b).f((Consumer) this.c, obj);
                return;
        }
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return consumer.getClass();
            case 5:
                return consumer.getClass();
            default:
                return consumer.getClass();
        }
    }

    @Override // j$.util.function.y
    public Object get() {
        switch (this.a) {
            case 1:
                return new C0104i1((EnumC0116k1) this.b, (j$.wrappers.E) this.c);
            case 2:
                return new C0092g1((EnumC0116k1) this.b, (j$.wrappers.V) this.c);
            case 3:
                return new C0098h1((EnumC0116k1) this.b, (C0219j0) this.c);
            default:
                return new C0086f1((EnumC0116k1) this.b, (Predicate) this.c);
        }
    }

    public /* synthetic */ C0138o(EnumC0116k1 enumC0116k1, Predicate predicate) {
        this.b = enumC0116k1;
        this.c = predicate;
    }

    public /* synthetic */ C0138o(EnumC0116k1 enumC0116k1, j$.wrappers.E e) {
        this.b = enumC0116k1;
        this.c = e;
    }

    public /* synthetic */ C0138o(EnumC0116k1 enumC0116k1, j$.wrappers.V v) {
        this.b = enumC0116k1;
        this.c = v;
    }

    public /* synthetic */ C0138o(EnumC0116k1 enumC0116k1, C0219j0 c0219j0) {
        this.b = enumC0116k1;
        this.c = c0219j0;
    }

    public /* synthetic */ C0138o(C0131m4 c0131m4, Consumer consumer) {
        this.b = c0131m4;
        this.c = consumer;
    }

    public /* synthetic */ C0138o(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.b = atomicBoolean;
        this.c = concurrentHashMap;
    }
}