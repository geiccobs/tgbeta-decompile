package j$.time.chrono;

import java.io.Serializable;
import java.util.Comparator;
/* loaded from: classes2.dex */
public final /* synthetic */ class AbstractChronology$$ExternalSyntheticLambda0 implements Comparator, Serializable {
    public static final /* synthetic */ AbstractChronology$$ExternalSyntheticLambda0 INSTANCE = new AbstractChronology$$ExternalSyntheticLambda0();

    private /* synthetic */ AbstractChronology$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return AbstractChronology.lambda$static$7f2d2d5b$1((ChronoLocalDate) obj, (ChronoLocalDate) obj2);
    }
}
