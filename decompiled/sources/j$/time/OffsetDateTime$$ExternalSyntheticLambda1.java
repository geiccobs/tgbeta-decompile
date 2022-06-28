package j$.time;

import java.util.Comparator;
/* loaded from: classes2.dex */
public final /* synthetic */ class OffsetDateTime$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ OffsetDateTime$$ExternalSyntheticLambda1 INSTANCE = new OffsetDateTime$$ExternalSyntheticLambda1();

    private /* synthetic */ OffsetDateTime$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareInstant;
        compareInstant = OffsetDateTime.compareInstant((OffsetDateTime) obj, (OffsetDateTime) obj2);
        return compareInstant;
    }
}
