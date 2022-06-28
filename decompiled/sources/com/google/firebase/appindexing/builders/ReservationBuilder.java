package com.google.firebase.appindexing.builders;

import com.google.android.gms.common.internal.Preconditions;
import java.util.Date;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class ReservationBuilder extends IndexableBuilder<ReservationBuilder> {
    public ReservationBuilder() {
        super("Reservation");
    }

    public ReservationBuilder setPartySize(long partySize) {
        put("partySize", partySize);
        return this;
    }

    public ReservationBuilder setReservationFor(LocalBusinessBuilder localBusiness) {
        put("reservationFor", localBusiness);
        return this;
    }

    public ReservationBuilder setStartDate(Date startDate) {
        Preconditions.checkNotNull(startDate);
        put("startDate", startDate.getTime());
        return this;
    }
}
