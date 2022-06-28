package com.google.android.gms.wallet;

import java.util.ArrayList;
import java.util.Arrays;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class PaymentInstrumentType {
    public static final int AMEX = 3;
    public static final int DISCOVER = 4;
    public static final int JCB = 5;
    public static final int MASTER_CARD = 2;
    public static final int VISA = 1;

    public static ArrayList<Integer> getAll() {
        return new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    }
}
