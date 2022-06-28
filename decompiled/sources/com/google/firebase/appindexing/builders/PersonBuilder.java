package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class PersonBuilder extends IndexableBuilder<PersonBuilder> {
    public PersonBuilder() {
        super("Person");
    }

    public PersonBuilder setEmail(String email) {
        put("email", email);
        return this;
    }

    public PersonBuilder setIsSelf(boolean isSelf) {
        put("isSelf", isSelf);
        return this;
    }

    public PersonBuilder setTelephone(String telephone) {
        put("telephone", telephone);
        return this;
    }
}
