package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class HlsMasterPlaylist extends HlsPlaylist {
    public static final HlsMasterPlaylist EMPTY = new HlsMasterPlaylist("", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, Collections.emptyList(), false, Collections.emptyMap(), Collections.emptyList());
    public static final int GROUP_INDEX_AUDIO = 1;
    public static final int GROUP_INDEX_SUBTITLE = 2;
    public static final int GROUP_INDEX_VARIANT = 0;
    public final List<Rendition> audios;
    public final List<Rendition> closedCaptions;
    public final List<Uri> mediaPlaylistUrls;
    public final Format muxedAudioFormat;
    public final List<Format> muxedCaptionFormats;
    public final List<DrmInitData> sessionKeyDrmInitData;
    public final List<Rendition> subtitles;
    public final Map<String, String> variableDefinitions;
    public final List<Variant> variants;
    public final List<Rendition> videos;

    /* loaded from: classes3.dex */
    public static final class Variant {
        public final String audioGroupId;
        public final String captionGroupId;
        public final Format format;
        public final String subtitleGroupId;
        public final Uri url;
        public final String videoGroupId;

        public Variant(Uri url, Format format, String videoGroupId, String audioGroupId, String subtitleGroupId, String captionGroupId) {
            this.url = url;
            this.format = format;
            this.videoGroupId = videoGroupId;
            this.audioGroupId = audioGroupId;
            this.subtitleGroupId = subtitleGroupId;
            this.captionGroupId = captionGroupId;
        }

        public static Variant createMediaPlaylistVariantUrl(Uri url) {
            Format format = Format.createContainerFormat("0", null, MimeTypes.APPLICATION_M3U8, null, null, -1, 0, 0, null);
            return new Variant(url, format, null, null, null, null);
        }

        public Variant copyWithFormat(Format format) {
            return new Variant(this.url, format, this.videoGroupId, this.audioGroupId, this.subtitleGroupId, this.captionGroupId);
        }
    }

    /* loaded from: classes3.dex */
    public static final class Rendition {
        public final Format format;
        public final String groupId;
        public final String name;
        public final Uri url;

        public Rendition(Uri url, Format format, String groupId, String name) {
            this.url = url;
            this.format = format;
            this.groupId = groupId;
            this.name = name;
        }
    }

    public HlsMasterPlaylist(String baseUri, List<String> tags, List<Variant> variants, List<Rendition> videos, List<Rendition> audios, List<Rendition> subtitles, List<Rendition> closedCaptions, Format muxedAudioFormat, List<Format> muxedCaptionFormats, boolean hasIndependentSegments, Map<String, String> variableDefinitions, List<DrmInitData> sessionKeyDrmInitData) {
        super(baseUri, tags, hasIndependentSegments);
        this.mediaPlaylistUrls = Collections.unmodifiableList(getMediaPlaylistUrls(variants, videos, audios, subtitles, closedCaptions));
        this.variants = Collections.unmodifiableList(variants);
        this.videos = Collections.unmodifiableList(videos);
        this.audios = Collections.unmodifiableList(audios);
        this.subtitles = Collections.unmodifiableList(subtitles);
        this.closedCaptions = Collections.unmodifiableList(closedCaptions);
        this.muxedAudioFormat = muxedAudioFormat;
        this.muxedCaptionFormats = muxedCaptionFormats != null ? Collections.unmodifiableList(muxedCaptionFormats) : null;
        this.variableDefinitions = Collections.unmodifiableMap(variableDefinitions);
        this.sessionKeyDrmInitData = Collections.unmodifiableList(sessionKeyDrmInitData);
    }

    @Override // com.google.android.exoplayer2.offline.FilterableManifest
    public HlsPlaylist copy(List<StreamKey> streamKeys) {
        return new HlsMasterPlaylist(this.baseUri, this.tags, copyStreams(this.variants, 0, streamKeys), Collections.emptyList(), copyStreams(this.audios, 1, streamKeys), copyStreams(this.subtitles, 2, streamKeys), Collections.emptyList(), this.muxedAudioFormat, this.muxedCaptionFormats, this.hasIndependentSegments, this.variableDefinitions, this.sessionKeyDrmInitData);
    }

    public static HlsMasterPlaylist createSingleVariantMasterPlaylist(String variantUrl) {
        List<Variant> variant = Collections.singletonList(Variant.createMediaPlaylistVariantUrl(Uri.parse(variantUrl)));
        return new HlsMasterPlaylist("", Collections.emptyList(), variant, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, null, false, Collections.emptyMap(), Collections.emptyList());
    }

    private static List<Uri> getMediaPlaylistUrls(List<Variant> variants, List<Rendition> videos, List<Rendition> audios, List<Rendition> subtitles, List<Rendition> closedCaptions) {
        ArrayList<Uri> mediaPlaylistUrls = new ArrayList<>();
        for (int i = 0; i < variants.size(); i++) {
            Uri uri = variants.get(i).url;
            if (!mediaPlaylistUrls.contains(uri)) {
                mediaPlaylistUrls.add(uri);
            }
        }
        addMediaPlaylistUrls(videos, mediaPlaylistUrls);
        addMediaPlaylistUrls(audios, mediaPlaylistUrls);
        addMediaPlaylistUrls(subtitles, mediaPlaylistUrls);
        addMediaPlaylistUrls(closedCaptions, mediaPlaylistUrls);
        return mediaPlaylistUrls;
    }

    private static void addMediaPlaylistUrls(List<Rendition> renditions, List<Uri> out) {
        for (int i = 0; i < renditions.size(); i++) {
            Uri uri = renditions.get(i).url;
            if (uri != null && !out.contains(uri)) {
                out.add(uri);
            }
        }
    }

    private static <T> List<T> copyStreams(List<T> streams, int groupIndex, List<StreamKey> streamKeys) {
        List<T> copiedStreams = new ArrayList<>(streamKeys.size());
        for (int i = 0; i < streams.size(); i++) {
            T stream = streams.get(i);
            int j = 0;
            while (true) {
                if (j < streamKeys.size()) {
                    StreamKey streamKey = streamKeys.get(j);
                    if (streamKey.groupIndex != groupIndex || streamKey.trackIndex != i) {
                        j++;
                    } else {
                        copiedStreams.add(stream);
                        break;
                    }
                }
            }
        }
        return copiedStreams;
    }
}
