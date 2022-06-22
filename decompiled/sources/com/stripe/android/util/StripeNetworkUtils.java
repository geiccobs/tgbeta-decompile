package com.stripe.android.util;

import com.stripe.android.model.Card;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
public class StripeNetworkUtils {
    public static Map<String, Object> hashMapFromCard(Card card) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        hashMap2.put("number", StripeTextUtils.nullIfBlank(card.getNumber()));
        hashMap2.put("cvc", StripeTextUtils.nullIfBlank(card.getCVC()));
        hashMap2.put("exp_month", card.getExpMonth());
        hashMap2.put("exp_year", card.getExpYear());
        hashMap2.put("name", StripeTextUtils.nullIfBlank(card.getName()));
        hashMap2.put("currency", StripeTextUtils.nullIfBlank(card.getCurrency()));
        hashMap2.put("address_line1", StripeTextUtils.nullIfBlank(card.getAddressLine1()));
        hashMap2.put("address_line2", StripeTextUtils.nullIfBlank(card.getAddressLine2()));
        hashMap2.put("address_city", StripeTextUtils.nullIfBlank(card.getAddressCity()));
        hashMap2.put("address_zip", StripeTextUtils.nullIfBlank(card.getAddressZip()));
        hashMap2.put("address_state", StripeTextUtils.nullIfBlank(card.getAddressState()));
        hashMap2.put("address_country", StripeTextUtils.nullIfBlank(card.getAddressCountry()));
        Iterator it = new HashSet(hashMap2.keySet()).iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (hashMap2.get(str) == null) {
                hashMap2.remove(str);
            }
        }
        hashMap.put("card", hashMap2);
        return hashMap;
    }
}
