package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes3.dex */
public final class SpliceNullCommand extends SpliceCommand {
    public static final Parcelable.Creator<SpliceNullCommand> CREATOR = new Parcelable.Creator<SpliceNullCommand>() { // from class: com.google.android.exoplayer2.metadata.scte35.SpliceNullCommand.1
        @Override // android.os.Parcelable.Creator
        public SpliceNullCommand createFromParcel(Parcel in) {
            return new SpliceNullCommand();
        }

        @Override // android.os.Parcelable.Creator
        public SpliceNullCommand[] newArray(int size) {
            return new SpliceNullCommand[size];
        }
    };

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
    }
}
