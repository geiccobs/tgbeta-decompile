package com.stripe.android.model;

import java.util.Date;
/* loaded from: classes.dex */
public class Token {
    private final Card mCard;
    private final String mId;
    private final String mType;

    public Token(String str, boolean z, Date date, Boolean bool, Card card, String str2) {
        this.mId = str;
        this.mType = str2;
        this.mCard = card;
        bool.booleanValue();
    }

    public String getId() {
        return this.mId;
    }

    public String getType() {
        return this.mType;
    }

    public Card getCard() {
        return this.mCard;
    }
}
