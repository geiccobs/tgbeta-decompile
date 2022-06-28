package com.google.android.datatransport.runtime;

import com.google.android.datatransport.runtime.firebase.transport.ClientMetrics;
import com.google.android.datatransport.runtime.firebase.transport.GlobalMetrics;
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;
import com.google.android.datatransport.runtime.firebase.transport.LogSourceMetrics;
import com.google.android.datatransport.runtime.firebase.transport.StorageMetrics;
import com.google.android.datatransport.runtime.firebase.transport.TimeWindow;
import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;
import com.google.firebase.encoders.proto.AtProtobuf;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class AutoProtoEncoderDoNotUseEncoder implements Configurator {
    public static final int CODEGEN_VERSION = 2;
    public static final Configurator CONFIG = new AutoProtoEncoderDoNotUseEncoder();

    private AutoProtoEncoderDoNotUseEncoder() {
    }

    @Override // com.google.firebase.encoders.config.Configurator
    public void configure(EncoderConfig<?> cfg) {
        cfg.registerEncoder(ProtoEncoderDoNotUse.class, ProtoEncoderDoNotUseEncoder.INSTANCE);
        cfg.registerEncoder(ClientMetrics.class, ClientMetricsEncoder.INSTANCE);
        cfg.registerEncoder(TimeWindow.class, TimeWindowEncoder.INSTANCE);
        cfg.registerEncoder(LogSourceMetrics.class, LogSourceMetricsEncoder.INSTANCE);
        cfg.registerEncoder(LogEventDropped.class, LogEventDroppedEncoder.INSTANCE);
        cfg.registerEncoder(GlobalMetrics.class, GlobalMetricsEncoder.INSTANCE);
        cfg.registerEncoder(StorageMetrics.class, StorageMetricsEncoder.INSTANCE);
    }

    /* loaded from: classes3.dex */
    private static final class ProtoEncoderDoNotUseEncoder implements ObjectEncoder<ProtoEncoderDoNotUse> {
        static final ProtoEncoderDoNotUseEncoder INSTANCE = new ProtoEncoderDoNotUseEncoder();
        private static final FieldDescriptor CLIENTMETRICS_DESCRIPTOR = FieldDescriptor.of("clientMetrics");

        private ProtoEncoderDoNotUseEncoder() {
        }

        public void encode(ProtoEncoderDoNotUse value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(CLIENTMETRICS_DESCRIPTOR, value.getClientMetrics());
        }
    }

    /* loaded from: classes3.dex */
    private static final class ClientMetricsEncoder implements ObjectEncoder<ClientMetrics> {
        static final ClientMetricsEncoder INSTANCE = new ClientMetricsEncoder();
        private static final FieldDescriptor WINDOW_DESCRIPTOR = FieldDescriptor.builder("window").withProperty(AtProtobuf.builder().tag(1).build()).build();
        private static final FieldDescriptor LOGSOURCEMETRICS_DESCRIPTOR = FieldDescriptor.builder("logSourceMetrics").withProperty(AtProtobuf.builder().tag(2).build()).build();
        private static final FieldDescriptor GLOBALMETRICS_DESCRIPTOR = FieldDescriptor.builder("globalMetrics").withProperty(AtProtobuf.builder().tag(3).build()).build();
        private static final FieldDescriptor APPNAMESPACE_DESCRIPTOR = FieldDescriptor.builder("appNamespace").withProperty(AtProtobuf.builder().tag(4).build()).build();

        private ClientMetricsEncoder() {
        }

        public void encode(ClientMetrics value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(WINDOW_DESCRIPTOR, value.getWindowInternal());
            ctx.add(LOGSOURCEMETRICS_DESCRIPTOR, value.getLogSourceMetricsList());
            ctx.add(GLOBALMETRICS_DESCRIPTOR, value.getGlobalMetricsInternal());
            ctx.add(APPNAMESPACE_DESCRIPTOR, value.getAppNamespace());
        }
    }

    /* loaded from: classes3.dex */
    private static final class TimeWindowEncoder implements ObjectEncoder<TimeWindow> {
        static final TimeWindowEncoder INSTANCE = new TimeWindowEncoder();
        private static final FieldDescriptor STARTMS_DESCRIPTOR = FieldDescriptor.builder("startMs").withProperty(AtProtobuf.builder().tag(1).build()).build();
        private static final FieldDescriptor ENDMS_DESCRIPTOR = FieldDescriptor.builder("endMs").withProperty(AtProtobuf.builder().tag(2).build()).build();

        private TimeWindowEncoder() {
        }

        public void encode(TimeWindow value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(STARTMS_DESCRIPTOR, value.getStartMs());
            ctx.add(ENDMS_DESCRIPTOR, value.getEndMs());
        }
    }

    /* loaded from: classes3.dex */
    private static final class LogSourceMetricsEncoder implements ObjectEncoder<LogSourceMetrics> {
        static final LogSourceMetricsEncoder INSTANCE = new LogSourceMetricsEncoder();
        private static final FieldDescriptor LOGSOURCE_DESCRIPTOR = FieldDescriptor.builder("logSource").withProperty(AtProtobuf.builder().tag(1).build()).build();
        private static final FieldDescriptor LOGEVENTDROPPED_DESCRIPTOR = FieldDescriptor.builder("logEventDropped").withProperty(AtProtobuf.builder().tag(2).build()).build();

        private LogSourceMetricsEncoder() {
        }

        public void encode(LogSourceMetrics value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(LOGSOURCE_DESCRIPTOR, value.getLogSource());
            ctx.add(LOGEVENTDROPPED_DESCRIPTOR, value.getLogEventDroppedList());
        }
    }

    /* loaded from: classes3.dex */
    private static final class LogEventDroppedEncoder implements ObjectEncoder<LogEventDropped> {
        static final LogEventDroppedEncoder INSTANCE = new LogEventDroppedEncoder();
        private static final FieldDescriptor EVENTSDROPPEDCOUNT_DESCRIPTOR = FieldDescriptor.builder("eventsDroppedCount").withProperty(AtProtobuf.builder().tag(1).build()).build();
        private static final FieldDescriptor REASON_DESCRIPTOR = FieldDescriptor.builder("reason").withProperty(AtProtobuf.builder().tag(3).build()).build();

        private LogEventDroppedEncoder() {
        }

        public void encode(LogEventDropped value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(EVENTSDROPPEDCOUNT_DESCRIPTOR, value.getEventsDroppedCount());
            ctx.add(REASON_DESCRIPTOR, value.getReason());
        }
    }

    /* loaded from: classes3.dex */
    private static final class GlobalMetricsEncoder implements ObjectEncoder<GlobalMetrics> {
        static final GlobalMetricsEncoder INSTANCE = new GlobalMetricsEncoder();
        private static final FieldDescriptor STORAGEMETRICS_DESCRIPTOR = FieldDescriptor.builder("storageMetrics").withProperty(AtProtobuf.builder().tag(1).build()).build();

        private GlobalMetricsEncoder() {
        }

        public void encode(GlobalMetrics value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(STORAGEMETRICS_DESCRIPTOR, value.getStorageMetricsInternal());
        }
    }

    /* loaded from: classes3.dex */
    private static final class StorageMetricsEncoder implements ObjectEncoder<StorageMetrics> {
        static final StorageMetricsEncoder INSTANCE = new StorageMetricsEncoder();
        private static final FieldDescriptor CURRENTCACHESIZEBYTES_DESCRIPTOR = FieldDescriptor.builder("currentCacheSizeBytes").withProperty(AtProtobuf.builder().tag(1).build()).build();
        private static final FieldDescriptor MAXCACHESIZEBYTES_DESCRIPTOR = FieldDescriptor.builder("maxCacheSizeBytes").withProperty(AtProtobuf.builder().tag(2).build()).build();

        private StorageMetricsEncoder() {
        }

        public void encode(StorageMetrics value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(CURRENTCACHESIZEBYTES_DESCRIPTOR, value.getCurrentCacheSizeBytes());
            ctx.add(MAXCACHESIZEBYTES_DESCRIPTOR, value.getMaxCacheSizeBytes());
        }
    }
}
