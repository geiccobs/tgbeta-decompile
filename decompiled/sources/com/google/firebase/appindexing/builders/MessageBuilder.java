package com.google.firebase.appindexing.builders;

import com.google.android.gms.common.internal.Preconditions;
import java.util.Date;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class MessageBuilder extends IndexableBuilder<MessageBuilder> {
    public MessageBuilder() {
        super("Message");
    }

    public MessageBuilder setDateRead(Date dateRead) {
        Preconditions.checkNotNull(dateRead);
        put("dateRead", dateRead.getTime());
        return this;
    }

    public MessageBuilder setDateReceived(Date dateReceived) {
        Preconditions.checkNotNull(dateReceived);
        put("dateReceived", dateReceived.getTime());
        return this;
    }

    public MessageBuilder setDateSent(Date dateSent) {
        Preconditions.checkNotNull(dateSent);
        put("dateSent", dateSent.getTime());
        return this;
    }

    public MessageBuilder setMessageAttachment(IndexableBuilder<?>... indexableBuilderArr) {
        put("messageAttachment", indexableBuilderArr);
        return this;
    }

    public MessageBuilder setRecipient(PersonBuilder... recipients) {
        put("recipient", recipients);
        return this;
    }

    public MessageBuilder setSender(PersonBuilder sender) {
        put("sender", sender);
        return this;
    }

    public MessageBuilder setText(String text) {
        put("text", text);
        return this;
    }

    public MessageBuilder(String str) {
        super("EmailMessage");
    }
}
