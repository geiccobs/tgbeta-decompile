package com.google.android.gms.internal.firebase_messaging;

import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.messaging.reporting.MessagingClientEvent;
import java.io.IOException;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes.dex */
final class zza implements ObjectEncoder<MessagingClientEvent> {
    static final zza zza = new zza();
    private static final FieldDescriptor zzb;
    private static final FieldDescriptor zzc;
    private static final FieldDescriptor zzd;
    private static final FieldDescriptor zze;
    private static final FieldDescriptor zzf;
    private static final FieldDescriptor zzg;
    private static final FieldDescriptor zzh;
    private static final FieldDescriptor zzi;
    private static final FieldDescriptor zzj;
    private static final FieldDescriptor zzk;
    private static final FieldDescriptor zzl;
    private static final FieldDescriptor zzm;
    private static final FieldDescriptor zzn;
    private static final FieldDescriptor zzo;
    private static final FieldDescriptor zzp;

    static {
        FieldDescriptor.Builder builder = FieldDescriptor.builder("projectNumber");
        zzv zzvVar = new zzv();
        zzvVar.zza(1);
        zzb = builder.withProperty(zzvVar.zzb()).build();
        FieldDescriptor.Builder builder2 = FieldDescriptor.builder("messageId");
        zzv zzvVar2 = new zzv();
        zzvVar2.zza(2);
        zzc = builder2.withProperty(zzvVar2.zzb()).build();
        FieldDescriptor.Builder builder3 = FieldDescriptor.builder("instanceId");
        zzv zzvVar3 = new zzv();
        zzvVar3.zza(3);
        zzd = builder3.withProperty(zzvVar3.zzb()).build();
        FieldDescriptor.Builder builder4 = FieldDescriptor.builder("messageType");
        zzv zzvVar4 = new zzv();
        zzvVar4.zza(4);
        zze = builder4.withProperty(zzvVar4.zzb()).build();
        FieldDescriptor.Builder builder5 = FieldDescriptor.builder("sdkPlatform");
        zzv zzvVar5 = new zzv();
        zzvVar5.zza(5);
        zzf = builder5.withProperty(zzvVar5.zzb()).build();
        FieldDescriptor.Builder builder6 = FieldDescriptor.builder("packageName");
        zzv zzvVar6 = new zzv();
        zzvVar6.zza(6);
        zzg = builder6.withProperty(zzvVar6.zzb()).build();
        FieldDescriptor.Builder builder7 = FieldDescriptor.builder("collapseKey");
        zzv zzvVar7 = new zzv();
        zzvVar7.zza(7);
        zzh = builder7.withProperty(zzvVar7.zzb()).build();
        FieldDescriptor.Builder builder8 = FieldDescriptor.builder("priority");
        zzv zzvVar8 = new zzv();
        zzvVar8.zza(8);
        zzi = builder8.withProperty(zzvVar8.zzb()).build();
        FieldDescriptor.Builder builder9 = FieldDescriptor.builder("ttl");
        zzv zzvVar9 = new zzv();
        zzvVar9.zza(9);
        zzj = builder9.withProperty(zzvVar9.zzb()).build();
        FieldDescriptor.Builder builder10 = FieldDescriptor.builder("topic");
        zzv zzvVar10 = new zzv();
        zzvVar10.zza(10);
        zzk = builder10.withProperty(zzvVar10.zzb()).build();
        FieldDescriptor.Builder builder11 = FieldDescriptor.builder("bulkId");
        zzv zzvVar11 = new zzv();
        zzvVar11.zza(11);
        zzl = builder11.withProperty(zzvVar11.zzb()).build();
        FieldDescriptor.Builder builder12 = FieldDescriptor.builder("event");
        zzv zzvVar12 = new zzv();
        zzvVar12.zza(12);
        zzm = builder12.withProperty(zzvVar12.zzb()).build();
        FieldDescriptor.Builder builder13 = FieldDescriptor.builder("analyticsLabel");
        zzv zzvVar13 = new zzv();
        zzvVar13.zza(13);
        zzn = builder13.withProperty(zzvVar13.zzb()).build();
        FieldDescriptor.Builder builder14 = FieldDescriptor.builder("campaignId");
        zzv zzvVar14 = new zzv();
        zzvVar14.zza(14);
        zzo = builder14.withProperty(zzvVar14.zzb()).build();
        FieldDescriptor.Builder builder15 = FieldDescriptor.builder("composerLabel");
        zzv zzvVar15 = new zzv();
        zzvVar15.zza(15);
        zzp = builder15.withProperty(zzvVar15.zzb()).build();
    }

    private zza() {
    }

    @Override // com.google.firebase.encoders.ObjectEncoder
    public final /* bridge */ /* synthetic */ void encode(Object obj, Object obj2) throws IOException {
        MessagingClientEvent messagingClientEvent = (MessagingClientEvent) obj;
        ObjectEncoderContext objectEncoderContext = (ObjectEncoderContext) obj2;
        objectEncoderContext.add(zzb, messagingClientEvent.getProjectNumber());
        objectEncoderContext.add(zzc, messagingClientEvent.getMessageId());
        objectEncoderContext.add(zzd, messagingClientEvent.getInstanceId());
        objectEncoderContext.add(zze, messagingClientEvent.getMessageType());
        objectEncoderContext.add(zzf, messagingClientEvent.getSdkPlatform());
        objectEncoderContext.add(zzg, messagingClientEvent.getPackageName());
        objectEncoderContext.add(zzh, messagingClientEvent.getCollapseKey());
        objectEncoderContext.add(zzi, messagingClientEvent.getPriority());
        objectEncoderContext.add(zzj, messagingClientEvent.getTtl());
        objectEncoderContext.add(zzk, messagingClientEvent.getTopic());
        objectEncoderContext.add(zzl, messagingClientEvent.getBulkId());
        objectEncoderContext.add(zzm, messagingClientEvent.getEvent());
        objectEncoderContext.add(zzn, messagingClientEvent.getAnalyticsLabel());
        objectEncoderContext.add(zzo, messagingClientEvent.getCampaignId());
        objectEncoderContext.add(zzp, messagingClientEvent.getComposerLabel());
    }
}
