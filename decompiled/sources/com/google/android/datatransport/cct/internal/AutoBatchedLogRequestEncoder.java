package com.google.android.datatransport.cct.internal;

import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class AutoBatchedLogRequestEncoder implements Configurator {
    public static final int CODEGEN_VERSION = 2;
    public static final Configurator CONFIG = new AutoBatchedLogRequestEncoder();

    private AutoBatchedLogRequestEncoder() {
    }

    @Override // com.google.firebase.encoders.config.Configurator
    public void configure(EncoderConfig<?> cfg) {
        cfg.registerEncoder(BatchedLogRequest.class, BatchedLogRequestEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_BatchedLogRequest.class, BatchedLogRequestEncoder.INSTANCE);
        cfg.registerEncoder(LogRequest.class, LogRequestEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_LogRequest.class, LogRequestEncoder.INSTANCE);
        cfg.registerEncoder(ClientInfo.class, ClientInfoEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_ClientInfo.class, ClientInfoEncoder.INSTANCE);
        cfg.registerEncoder(AndroidClientInfo.class, AndroidClientInfoEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_AndroidClientInfo.class, AndroidClientInfoEncoder.INSTANCE);
        cfg.registerEncoder(LogEvent.class, LogEventEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_LogEvent.class, LogEventEncoder.INSTANCE);
        cfg.registerEncoder(NetworkConnectionInfo.class, NetworkConnectionInfoEncoder.INSTANCE);
        cfg.registerEncoder(AutoValue_NetworkConnectionInfo.class, NetworkConnectionInfoEncoder.INSTANCE);
    }

    /* loaded from: classes3.dex */
    private static final class BatchedLogRequestEncoder implements ObjectEncoder<BatchedLogRequest> {
        static final BatchedLogRequestEncoder INSTANCE = new BatchedLogRequestEncoder();
        private static final FieldDescriptor LOGREQUEST_DESCRIPTOR = FieldDescriptor.of("logRequest");

        private BatchedLogRequestEncoder() {
        }

        public void encode(BatchedLogRequest value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(LOGREQUEST_DESCRIPTOR, value.getLogRequests());
        }
    }

    /* loaded from: classes3.dex */
    private static final class LogRequestEncoder implements ObjectEncoder<LogRequest> {
        static final LogRequestEncoder INSTANCE = new LogRequestEncoder();
        private static final FieldDescriptor REQUESTTIMEMS_DESCRIPTOR = FieldDescriptor.of("requestTimeMs");
        private static final FieldDescriptor REQUESTUPTIMEMS_DESCRIPTOR = FieldDescriptor.of("requestUptimeMs");
        private static final FieldDescriptor CLIENTINFO_DESCRIPTOR = FieldDescriptor.of("clientInfo");
        private static final FieldDescriptor LOGSOURCE_DESCRIPTOR = FieldDescriptor.of("logSource");
        private static final FieldDescriptor LOGSOURCENAME_DESCRIPTOR = FieldDescriptor.of("logSourceName");
        private static final FieldDescriptor LOGEVENT_DESCRIPTOR = FieldDescriptor.of("logEvent");
        private static final FieldDescriptor QOSTIER_DESCRIPTOR = FieldDescriptor.of("qosTier");

        private LogRequestEncoder() {
        }

        public void encode(LogRequest value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(REQUESTTIMEMS_DESCRIPTOR, value.getRequestTimeMs());
            ctx.add(REQUESTUPTIMEMS_DESCRIPTOR, value.getRequestUptimeMs());
            ctx.add(CLIENTINFO_DESCRIPTOR, value.getClientInfo());
            ctx.add(LOGSOURCE_DESCRIPTOR, value.getLogSource());
            ctx.add(LOGSOURCENAME_DESCRIPTOR, value.getLogSourceName());
            ctx.add(LOGEVENT_DESCRIPTOR, value.getLogEvents());
            ctx.add(QOSTIER_DESCRIPTOR, value.getQosTier());
        }
    }

    /* loaded from: classes3.dex */
    private static final class ClientInfoEncoder implements ObjectEncoder<ClientInfo> {
        static final ClientInfoEncoder INSTANCE = new ClientInfoEncoder();
        private static final FieldDescriptor CLIENTTYPE_DESCRIPTOR = FieldDescriptor.of("clientType");
        private static final FieldDescriptor ANDROIDCLIENTINFO_DESCRIPTOR = FieldDescriptor.of("androidClientInfo");

        private ClientInfoEncoder() {
        }

        public void encode(ClientInfo value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(CLIENTTYPE_DESCRIPTOR, value.getClientType());
            ctx.add(ANDROIDCLIENTINFO_DESCRIPTOR, value.getAndroidClientInfo());
        }
    }

    /* loaded from: classes3.dex */
    private static final class AndroidClientInfoEncoder implements ObjectEncoder<AndroidClientInfo> {
        static final AndroidClientInfoEncoder INSTANCE = new AndroidClientInfoEncoder();
        private static final FieldDescriptor SDKVERSION_DESCRIPTOR = FieldDescriptor.of(RemoteConfigConstants.RequestFieldKey.SDK_VERSION);
        private static final FieldDescriptor MODEL_DESCRIPTOR = FieldDescriptor.of("model");
        private static final FieldDescriptor HARDWARE_DESCRIPTOR = FieldDescriptor.of("hardware");
        private static final FieldDescriptor DEVICE_DESCRIPTOR = FieldDescriptor.of("device");
        private static final FieldDescriptor PRODUCT_DESCRIPTOR = FieldDescriptor.of("product");
        private static final FieldDescriptor OSBUILD_DESCRIPTOR = FieldDescriptor.of("osBuild");
        private static final FieldDescriptor MANUFACTURER_DESCRIPTOR = FieldDescriptor.of("manufacturer");
        private static final FieldDescriptor FINGERPRINT_DESCRIPTOR = FieldDescriptor.of("fingerprint");
        private static final FieldDescriptor LOCALE_DESCRIPTOR = FieldDescriptor.of("locale");
        private static final FieldDescriptor COUNTRY_DESCRIPTOR = FieldDescriptor.of("country");
        private static final FieldDescriptor MCCMNC_DESCRIPTOR = FieldDescriptor.of("mccMnc");
        private static final FieldDescriptor APPLICATIONBUILD_DESCRIPTOR = FieldDescriptor.of("applicationBuild");

        private AndroidClientInfoEncoder() {
        }

        public void encode(AndroidClientInfo value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(SDKVERSION_DESCRIPTOR, value.getSdkVersion());
            ctx.add(MODEL_DESCRIPTOR, value.getModel());
            ctx.add(HARDWARE_DESCRIPTOR, value.getHardware());
            ctx.add(DEVICE_DESCRIPTOR, value.getDevice());
            ctx.add(PRODUCT_DESCRIPTOR, value.getProduct());
            ctx.add(OSBUILD_DESCRIPTOR, value.getOsBuild());
            ctx.add(MANUFACTURER_DESCRIPTOR, value.getManufacturer());
            ctx.add(FINGERPRINT_DESCRIPTOR, value.getFingerprint());
            ctx.add(LOCALE_DESCRIPTOR, value.getLocale());
            ctx.add(COUNTRY_DESCRIPTOR, value.getCountry());
            ctx.add(MCCMNC_DESCRIPTOR, value.getMccMnc());
            ctx.add(APPLICATIONBUILD_DESCRIPTOR, value.getApplicationBuild());
        }
    }

    /* loaded from: classes3.dex */
    private static final class LogEventEncoder implements ObjectEncoder<LogEvent> {
        static final LogEventEncoder INSTANCE = new LogEventEncoder();
        private static final FieldDescriptor EVENTTIMEMS_DESCRIPTOR = FieldDescriptor.of("eventTimeMs");
        private static final FieldDescriptor EVENTCODE_DESCRIPTOR = FieldDescriptor.of("eventCode");
        private static final FieldDescriptor EVENTUPTIMEMS_DESCRIPTOR = FieldDescriptor.of("eventUptimeMs");
        private static final FieldDescriptor SOURCEEXTENSION_DESCRIPTOR = FieldDescriptor.of("sourceExtension");
        private static final FieldDescriptor SOURCEEXTENSIONJSONPROTO3_DESCRIPTOR = FieldDescriptor.of("sourceExtensionJsonProto3");
        private static final FieldDescriptor TIMEZONEOFFSETSECONDS_DESCRIPTOR = FieldDescriptor.of("timezoneOffsetSeconds");
        private static final FieldDescriptor NETWORKCONNECTIONINFO_DESCRIPTOR = FieldDescriptor.of("networkConnectionInfo");

        private LogEventEncoder() {
        }

        public void encode(LogEvent value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(EVENTTIMEMS_DESCRIPTOR, value.getEventTimeMs());
            ctx.add(EVENTCODE_DESCRIPTOR, value.getEventCode());
            ctx.add(EVENTUPTIMEMS_DESCRIPTOR, value.getEventUptimeMs());
            ctx.add(SOURCEEXTENSION_DESCRIPTOR, value.getSourceExtension());
            ctx.add(SOURCEEXTENSIONJSONPROTO3_DESCRIPTOR, value.getSourceExtensionJsonProto3());
            ctx.add(TIMEZONEOFFSETSECONDS_DESCRIPTOR, value.getTimezoneOffsetSeconds());
            ctx.add(NETWORKCONNECTIONINFO_DESCRIPTOR, value.getNetworkConnectionInfo());
        }
    }

    /* loaded from: classes3.dex */
    private static final class NetworkConnectionInfoEncoder implements ObjectEncoder<NetworkConnectionInfo> {
        static final NetworkConnectionInfoEncoder INSTANCE = new NetworkConnectionInfoEncoder();
        private static final FieldDescriptor NETWORKTYPE_DESCRIPTOR = FieldDescriptor.of("networkType");
        private static final FieldDescriptor MOBILESUBTYPE_DESCRIPTOR = FieldDescriptor.of("mobileSubtype");

        private NetworkConnectionInfoEncoder() {
        }

        public void encode(NetworkConnectionInfo value, ObjectEncoderContext ctx) throws IOException {
            ctx.add(NETWORKTYPE_DESCRIPTOR, value.getNetworkType());
            ctx.add(MOBILESUBTYPE_DESCRIPTOR, value.getMobileSubtype());
        }
    }
}
