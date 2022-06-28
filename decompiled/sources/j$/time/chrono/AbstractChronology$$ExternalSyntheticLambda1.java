package j$.time.chrono;

import java.io.Serializable;
import java.util.Comparator;
/* loaded from: classes2.dex */
public final /* synthetic */ class AbstractChronology$$ExternalSyntheticLambda1 implements Comparator, Serializable {
    public static final /* synthetic */ AbstractChronology$$ExternalSyntheticLambda1 INSTANCE = new AbstractChronology$$ExternalSyntheticLambda1();

    private /* synthetic */ AbstractChronology$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return AbstractChronology.lambda$static$b5a61975$1((ChronoLocalDateTime) obj, (ChronoLocalDateTime) obj2);
    }
}
