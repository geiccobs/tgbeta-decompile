package com.google.firebase.appindexing.builders;

import com.google.firebase.appindexing.Action;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class Actions {
    public static Action newView(String name, String url) {
        Action.Builder builder = new Action.Builder(Action.Builder.VIEW_ACTION);
        builder.setObject(name, url);
        return builder.build();
    }
}
