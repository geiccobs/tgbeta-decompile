package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionRenderer;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public class DefaultRenderersFactory implements RenderersFactory {
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;
    public static final int EXTENSION_RENDERER_MODE_OFF = 0;
    public static final int EXTENSION_RENDERER_MODE_ON = 1;
    public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
    protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
    private static final String TAG = "DefaultRenderersFactory";
    private long allowedVideoJoiningTimeMs;
    private final Context context;
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private boolean enableDecoderFallback;
    private int extensionRendererMode;
    private MediaCodecSelector mediaCodecSelector;
    private boolean playClearSamplesWithoutKeys;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ExtensionRendererMode {
    }

    public DefaultRenderersFactory(Context context) {
        this.context = context;
        this.extensionRendererMode = 0;
        this.allowedVideoJoiningTimeMs = DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.mediaCodecSelector = MediaCodecSelector.DEFAULT;
    }

    @Deprecated
    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        this(context, drmSessionManager, 0);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context, int extensionRendererMode) {
        this(context, extensionRendererMode, (long) DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
        this(context, drmSessionManager, extensionRendererMode, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        this(context, null, extensionRendererMode, allowedVideoJoiningTimeMs);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        this.context = context;
        this.extensionRendererMode = extensionRendererMode;
        this.allowedVideoJoiningTimeMs = allowedVideoJoiningTimeMs;
        this.drmSessionManager = drmSessionManager;
        this.mediaCodecSelector = MediaCodecSelector.DEFAULT;
    }

    public DefaultRenderersFactory setExtensionRendererMode(int extensionRendererMode) {
        this.extensionRendererMode = extensionRendererMode;
        return this;
    }

    public DefaultRenderersFactory setPlayClearSamplesWithoutKeys(boolean playClearSamplesWithoutKeys) {
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        return this;
    }

    public DefaultRenderersFactory setEnableDecoderFallback(boolean enableDecoderFallback) {
        this.enableDecoderFallback = enableDecoderFallback;
        return this;
    }

    public DefaultRenderersFactory setMediaCodecSelector(MediaCodecSelector mediaCodecSelector) {
        this.mediaCodecSelector = mediaCodecSelector;
        return this;
    }

    public DefaultRenderersFactory setAllowedVideoJoiningTimeMs(long allowedVideoJoiningTimeMs) {
        this.allowedVideoJoiningTimeMs = allowedVideoJoiningTimeMs;
        return this;
    }

    @Override // com.google.android.exoplayer2.RenderersFactory
    public Renderer[] createRenderers(Handler eventHandler, VideoRendererEventListener videoRendererEventListener, AudioRendererEventListener audioRendererEventListener, TextOutput textRendererOutput, MetadataOutput metadataRendererOutput, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2;
        if (drmSessionManager != null) {
            drmSessionManager2 = drmSessionManager;
        } else {
            drmSessionManager2 = this.drmSessionManager;
        }
        ArrayList<Renderer> renderersList = new ArrayList<>();
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager3 = drmSessionManager2;
        buildVideoRenderers(this.context, this.extensionRendererMode, this.mediaCodecSelector, drmSessionManager3, this.playClearSamplesWithoutKeys, this.enableDecoderFallback, eventHandler, videoRendererEventListener, this.allowedVideoJoiningTimeMs, renderersList);
        buildAudioRenderers(this.context, this.extensionRendererMode, this.mediaCodecSelector, drmSessionManager3, this.playClearSamplesWithoutKeys, this.enableDecoderFallback, buildAudioProcessors(), eventHandler, audioRendererEventListener, renderersList);
        buildTextRenderers(this.context, textRendererOutput, eventHandler.getLooper(), this.extensionRendererMode, renderersList);
        buildMetadataRenderers(this.context, metadataRendererOutput, eventHandler.getLooper(), this.extensionRendererMode, renderersList);
        buildCameraMotionRenderers(this.context, this.extensionRendererMode, renderersList);
        buildMiscellaneousRenderers(this.context, eventHandler, this.extensionRendererMode, renderersList);
        return (Renderer[]) renderersList.toArray(new Renderer[0]);
    }

    protected void buildVideoRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        int extensionRendererIndex;
        int extensionRendererIndex2;
        Exception e;
        Exception e2;
        out.add(new MediaCodecVideoRenderer(context, mediaCodecSelector, allowedVideoJoiningTimeMs, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, eventHandler, eventListener, 50));
        if (extensionRendererMode == 0) {
            return;
        }
        int extensionRendererIndex3 = out.size();
        if (extensionRendererMode != 2) {
            extensionRendererIndex = extensionRendererIndex3;
        } else {
            extensionRendererIndex = extensionRendererIndex3 - 1;
        }
        try {
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer");
            Constructor<?> constructor = clazz.getConstructor(Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE);
            Renderer renderer = (Renderer) constructor.newInstance(Long.valueOf(allowedVideoJoiningTimeMs), eventHandler, eventListener, 50);
            extensionRendererIndex2 = extensionRendererIndex + 1;
            try {
                out.add(extensionRendererIndex, renderer);
                Log.i(TAG, "Loaded LibvpxVideoRenderer.");
            } catch (ClassNotFoundException e3) {
                extensionRendererIndex = extensionRendererIndex2;
                extensionRendererIndex2 = extensionRendererIndex;
                Class<?> clazz2 = Class.forName("com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer");
                Constructor<?> constructor2 = clazz2.getConstructor(Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE);
                Renderer renderer2 = (Renderer) constructor2.newInstance(Long.valueOf(allowedVideoJoiningTimeMs), eventHandler, eventListener, 50);
                int extensionRendererIndex4 = extensionRendererIndex2 + 1;
                try {
                    out.add(extensionRendererIndex2, renderer2);
                    Log.i(TAG, "Loaded Libgav1VideoRenderer.");
                } catch (ClassNotFoundException e4) {
                    extensionRendererIndex2 = extensionRendererIndex4;
                    return;
                } catch (Exception e5) {
                    e = e5;
                    throw new RuntimeException("Error instantiating AV1 extension", e);
                }
            } catch (Exception e6) {
                e2 = e6;
                throw new RuntimeException("Error instantiating VP9 extension", e2);
            }
        } catch (ClassNotFoundException e7) {
        } catch (Exception e8) {
            e2 = e8;
        }
        try {
            Class<?> clazz22 = Class.forName("com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer");
            Constructor<?> constructor22 = clazz22.getConstructor(Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE);
            Renderer renderer22 = (Renderer) constructor22.newInstance(Long.valueOf(allowedVideoJoiningTimeMs), eventHandler, eventListener, 50);
            int extensionRendererIndex42 = extensionRendererIndex2 + 1;
            out.add(extensionRendererIndex2, renderer22);
            Log.i(TAG, "Loaded Libgav1VideoRenderer.");
        } catch (ClassNotFoundException e9) {
        } catch (Exception e10) {
            e = e10;
        }
    }

    public void buildAudioRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
        int extensionRendererIndex;
        int extensionRendererIndex2;
        int extensionRendererIndex3;
        Exception e;
        int extensionRendererIndex4;
        Exception e2;
        Exception e3;
        Renderer renderer;
        out.add(new MediaCodecAudioRenderer(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, eventHandler, eventListener, new DefaultAudioSink(AudioCapabilities.getCapabilities(context), audioProcessors)));
        if (extensionRendererMode == 0) {
            return;
        }
        int extensionRendererIndex5 = out.size();
        if (extensionRendererMode != 2) {
            extensionRendererIndex = extensionRendererIndex5;
        } else {
            extensionRendererIndex = extensionRendererIndex5 - 1;
        }
        try {
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer");
            Constructor<?> constructor = clazz.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
            renderer = (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            extensionRendererIndex2 = extensionRendererIndex + 1;
        } catch (ClassNotFoundException e4) {
        } catch (Exception e5) {
            e3 = e5;
        }
        try {
            out.add(extensionRendererIndex, renderer);
            Log.i(TAG, "Loaded LibopusAudioRenderer.");
        } catch (ClassNotFoundException e6) {
            extensionRendererIndex = extensionRendererIndex2;
            extensionRendererIndex2 = extensionRendererIndex;
            Class<?> clazz2 = Class.forName("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer");
            Constructor<?> constructor2 = clazz2.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
            Renderer renderer2 = (Renderer) constructor2.newInstance(eventHandler, eventListener, audioProcessors);
            extensionRendererIndex3 = extensionRendererIndex2 + 1;
            try {
                try {
                    out.add(extensionRendererIndex2, renderer2);
                    Log.i(TAG, "Loaded LibflacAudioRenderer.");
                } catch (ClassNotFoundException e7) {
                    extensionRendererIndex2 = extensionRendererIndex3;
                    extensionRendererIndex3 = extensionRendererIndex2;
                    Class<?> clazz3 = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
                    Constructor<?> constructor3 = clazz3.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
                    Renderer renderer3 = (Renderer) constructor3.newInstance(eventHandler, eventListener, audioProcessors);
                    extensionRendererIndex4 = extensionRendererIndex3 + 1;
                    out.add(extensionRendererIndex3, renderer3);
                    Log.i(TAG, "Loaded FfmpegAudioRenderer.");
                } catch (Exception e8) {
                    e2 = e8;
                    throw new RuntimeException("Error instantiating FLAC extension", e2);
                }
                out.add(extensionRendererIndex3, renderer3);
                Log.i(TAG, "Loaded FfmpegAudioRenderer.");
            } catch (ClassNotFoundException e9) {
                extensionRendererIndex3 = extensionRendererIndex4;
                return;
            } catch (Exception e10) {
                e = e10;
                throw new RuntimeException("Error instantiating FFmpeg extension", e);
            }
            Class<?> clazz32 = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
            Constructor<?> constructor32 = clazz32.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
            Renderer renderer32 = (Renderer) constructor32.newInstance(eventHandler, eventListener, audioProcessors);
            extensionRendererIndex4 = extensionRendererIndex3 + 1;
        } catch (Exception e11) {
            e3 = e11;
            throw new RuntimeException("Error instantiating Opus extension", e3);
        }
        try {
            Class<?> clazz22 = Class.forName("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer");
            Constructor<?> constructor22 = clazz22.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
            Renderer renderer22 = (Renderer) constructor22.newInstance(eventHandler, eventListener, audioProcessors);
            extensionRendererIndex3 = extensionRendererIndex2 + 1;
            out.add(extensionRendererIndex2, renderer22);
            Log.i(TAG, "Loaded LibflacAudioRenderer.");
        } catch (ClassNotFoundException e12) {
        } catch (Exception e13) {
            e2 = e13;
        }
        try {
            Class<?> clazz322 = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
            Constructor<?> constructor322 = clazz322.getConstructor(Handler.class, AudioRendererEventListener.class, AudioProcessor[].class);
            Renderer renderer322 = (Renderer) constructor322.newInstance(eventHandler, eventListener, audioProcessors);
            extensionRendererIndex4 = extensionRendererIndex3 + 1;
            out.add(extensionRendererIndex3, renderer322);
            Log.i(TAG, "Loaded FfmpegAudioRenderer.");
        } catch (ClassNotFoundException e14) {
        } catch (Exception e15) {
            e = e15;
        }
    }

    protected void buildTextRenderers(Context context, TextOutput output, Looper outputLooper, int extensionRendererMode, ArrayList<Renderer> out) {
        out.add(new TextRenderer(output, outputLooper));
    }

    protected void buildMetadataRenderers(Context context, MetadataOutput output, Looper outputLooper, int extensionRendererMode, ArrayList<Renderer> out) {
        out.add(new MetadataRenderer(output, outputLooper));
    }

    protected void buildCameraMotionRenderers(Context context, int extensionRendererMode, ArrayList<Renderer> out) {
        out.add(new CameraMotionRenderer());
    }

    protected void buildMiscellaneousRenderers(Context context, Handler eventHandler, int extensionRendererMode, ArrayList<Renderer> out) {
    }

    protected AudioProcessor[] buildAudioProcessors() {
        return new AudioProcessor[0];
    }
}
