package com.google.firebase.appindexing.builders;

import java.util.Date;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class DigitalDocumentBuilder extends IndexableBuilder<DigitalDocumentBuilder> {
    public DigitalDocumentBuilder() {
        super("DigitalDocument");
    }

    public DigitalDocumentBuilder setAuthor(PersonBuilder... personBuilders) {
        put("author", personBuilders);
        return this;
    }

    public DigitalDocumentBuilder setDateCreated(Date dateCreated) {
        put("dateCreated", dateCreated.getTime());
        return this;
    }

    public DigitalDocumentBuilder setDateModified(Date dateModified) {
        put("dateModified", dateModified.getTime());
        return this;
    }

    public DigitalDocumentBuilder setHasDigitalDocumentPermission(DigitalDocumentPermissionBuilder... permissions) {
        put("hasDigitalDocumentPermission", permissions);
        return this;
    }

    public DigitalDocumentBuilder setText(String text) {
        put("text", text);
        return this;
    }

    public DigitalDocumentBuilder(String str) {
        super(str);
    }
}
