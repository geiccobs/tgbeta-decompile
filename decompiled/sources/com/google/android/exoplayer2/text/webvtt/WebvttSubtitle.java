package com.google.android.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.webvtt.WebvttCue;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
final class WebvttSubtitle implements Subtitle {
    private final long[] cueTimesUs;
    private final List<WebvttCue> cues;
    private final int numCues;
    private final long[] sortedCueTimesUs;

    public WebvttSubtitle(List<WebvttCue> cues) {
        this.cues = cues;
        int size = cues.size();
        this.numCues = size;
        this.cueTimesUs = new long[size * 2];
        for (int cueIndex = 0; cueIndex < this.numCues; cueIndex++) {
            WebvttCue cue = cues.get(cueIndex);
            int arrayIndex = cueIndex * 2;
            this.cueTimesUs[arrayIndex] = cue.startTime;
            this.cueTimesUs[arrayIndex + 1] = cue.endTime;
        }
        long[] jArr = this.cueTimesUs;
        long[] copyOf = Arrays.copyOf(jArr, jArr.length);
        this.sortedCueTimesUs = copyOf;
        Arrays.sort(copyOf);
    }

    @Override // com.google.android.exoplayer2.text.Subtitle
    public int getNextEventTimeIndex(long timeUs) {
        int index = Util.binarySearchCeil(this.sortedCueTimesUs, timeUs, false, false);
        if (index < this.sortedCueTimesUs.length) {
            return index;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.text.Subtitle
    public int getEventTimeCount() {
        return this.sortedCueTimesUs.length;
    }

    @Override // com.google.android.exoplayer2.text.Subtitle
    public long getEventTime(int index) {
        boolean z = true;
        Assertions.checkArgument(index >= 0);
        if (index >= this.sortedCueTimesUs.length) {
            z = false;
        }
        Assertions.checkArgument(z);
        return this.sortedCueTimesUs[index];
    }

    @Override // com.google.android.exoplayer2.text.Subtitle
    public List<Cue> getCues(long timeUs) {
        List<Cue> list = new ArrayList<>();
        WebvttCue firstNormalCue = null;
        SpannableStringBuilder normalCueTextBuilder = null;
        for (int i = 0; i < this.numCues; i++) {
            long[] jArr = this.cueTimesUs;
            if (jArr[i * 2] <= timeUs && timeUs < jArr[(i * 2) + 1]) {
                WebvttCue cue = this.cues.get(i);
                if (cue.isNormalCue()) {
                    if (firstNormalCue == null) {
                        firstNormalCue = cue;
                    } else if (normalCueTextBuilder == null) {
                        normalCueTextBuilder = new SpannableStringBuilder();
                        normalCueTextBuilder.append((CharSequence) Assertions.checkNotNull(firstNormalCue.text)).append((CharSequence) "\n").append((CharSequence) Assertions.checkNotNull(cue.text));
                    } else {
                        normalCueTextBuilder.append((CharSequence) "\n").append((CharSequence) Assertions.checkNotNull(cue.text));
                    }
                } else {
                    list.add(cue);
                }
            }
        }
        if (normalCueTextBuilder != null) {
            list.add(new WebvttCue.Builder().setText(normalCueTextBuilder).build());
        } else if (firstNormalCue != null) {
            list.add(firstNormalCue);
        }
        return list;
    }
}
