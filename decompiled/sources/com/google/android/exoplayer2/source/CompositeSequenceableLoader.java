package com.google.android.exoplayer2.source;
/* loaded from: classes3.dex */
public class CompositeSequenceableLoader implements SequenceableLoader {
    protected final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] loaders) {
        this.loaders = loaders;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public final long getBufferedPositionUs() {
        SequenceableLoader[] sequenceableLoaderArr;
        long bufferedPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderBufferedPositionUs = loader.getBufferedPositionUs();
            if (loaderBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, loaderBufferedPositionUs);
            }
        }
        if (bufferedPositionUs == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return bufferedPositionUs;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public final long getNextLoadPositionUs() {
        SequenceableLoader[] sequenceableLoaderArr;
        long nextLoadPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
            if (loaderNextLoadPositionUs != Long.MIN_VALUE) {
                nextLoadPositionUs = Math.min(nextLoadPositionUs, loaderNextLoadPositionUs);
            }
        }
        if (nextLoadPositionUs == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return nextLoadPositionUs;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public final void reevaluateBuffer(long positionUs) {
        SequenceableLoader[] sequenceableLoaderArr;
        for (SequenceableLoader loader : this.loaders) {
            loader.reevaluateBuffer(positionUs);
        }
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        boolean madeProgressThisIteration;
        SequenceableLoader[] sequenceableLoaderArr;
        boolean madeProgress = false;
        do {
            madeProgressThisIteration = false;
            long nextLoadPositionUs = getNextLoadPositionUs();
            if (nextLoadPositionUs == Long.MIN_VALUE) {
                break;
            }
            for (SequenceableLoader loader : this.loaders) {
                long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
                boolean isLoaderBehind = loaderNextLoadPositionUs != Long.MIN_VALUE && loaderNextLoadPositionUs <= positionUs;
                if (loaderNextLoadPositionUs == nextLoadPositionUs || isLoaderBehind) {
                    madeProgressThisIteration |= loader.continueLoading(positionUs);
                }
            }
            madeProgress |= madeProgressThisIteration;
        } while (madeProgressThisIteration);
        return madeProgress;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        SequenceableLoader[] sequenceableLoaderArr;
        for (SequenceableLoader loader : this.loaders) {
            if (loader.isLoading()) {
                return true;
            }
        }
        return false;
    }
}
