package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class DigitalDocumentPermissionBuilder extends IndexableBuilder<DigitalDocumentPermissionBuilder> {
    public static final String COMMENT_PERMISSION = "CommentPermission";
    public static final String READ_PERMISSION = "ReadPermission";
    public static final String WRITE_PERMISSION = "WritePermission";

    public DigitalDocumentPermissionBuilder() {
        super("DigitalDocumentPermission");
    }

    public DigitalDocumentPermissionBuilder setGrantee(PersonBuilder... grantees) {
        put("grantee", grantees);
        return this;
    }

    public DigitalDocumentPermissionBuilder setPermissionType(String type) {
        put("permissionType", type);
        return this;
    }
}
