package com.google.android.gms.wallet.callback;

import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class zza extends com.google.android.gms.internal.wallet.zzh {
    final /* synthetic */ zzd zza;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zza(zzd zzdVar, Looper looper) {
        super(looper);
        this.zza = zzdVar;
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        if (message.replyTo == null) {
            return;
        }
        byte[] byteArray = message.getData().getByteArray("extra_callback_input");
        Preconditions.checkNotNull(byteArray);
        CallbackInput callbackInput = (CallbackInput) SafeParcelableSerializer.deserializeFromBytes(byteArray, CallbackInput.CREATOR);
        String string = message.getData().getString("message_task_tag");
        Preconditions.checkNotNull(string);
        this.zza.zza.execute(new zzc(this.zza, callbackInput, message.replyTo, string, message.arg1));
    }
}
