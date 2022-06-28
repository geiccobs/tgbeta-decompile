package j$.util;

import java.security.PrivilegedAction;
/* loaded from: classes2.dex */
public final /* synthetic */ class Tripwire$$ExternalSyntheticLambda0 implements PrivilegedAction {
    public static final /* synthetic */ Tripwire$$ExternalSyntheticLambda0 INSTANCE = new Tripwire$$ExternalSyntheticLambda0();

    private /* synthetic */ Tripwire$$ExternalSyntheticLambda0() {
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        Boolean valueOf;
        valueOf = Boolean.valueOf(Boolean.getBoolean(Tripwire.TRIPWIRE_PROPERTY));
        return valueOf;
    }
}
