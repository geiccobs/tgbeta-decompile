package j$.time.temporal;

import java.util.List;
/* loaded from: classes2.dex */
public interface TemporalAmount {
    Temporal addTo(Temporal temporal);

    long get(TemporalUnit temporalUnit);

    List<TemporalUnit> getUnits();

    Temporal subtractFrom(Temporal temporal);
}
