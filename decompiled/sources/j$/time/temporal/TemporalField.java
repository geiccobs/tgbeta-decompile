package j$.time.temporal;

import j$.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes2.dex */
public interface TemporalField {
    <R extends Temporal> R adjustInto(R r, long j);

    TemporalUnit getBaseUnit();

    String getDisplayName(Locale locale);

    long getFrom(TemporalAccessor temporalAccessor);

    TemporalUnit getRangeUnit();

    boolean isDateBased();

    boolean isSupportedBy(TemporalAccessor temporalAccessor);

    boolean isTimeBased();

    ValueRange range();

    ValueRange rangeRefinedBy(TemporalAccessor temporalAccessor);

    TemporalAccessor resolve(Map<TemporalField, Long> map, TemporalAccessor temporalAccessor, ResolverStyle resolverStyle);

    String toString();

    /* renamed from: j$.time.temporal.TemporalField$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static TemporalAccessor $default$resolve(TemporalField _this, Map map, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            return null;
        }
    }
}
