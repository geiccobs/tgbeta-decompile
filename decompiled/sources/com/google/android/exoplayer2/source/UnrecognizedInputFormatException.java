package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.ParserException;
/* loaded from: classes.dex */
public class UnrecognizedInputFormatException extends ParserException {
    public UnrecognizedInputFormatException(String str, Uri uri) {
        super(str);
    }
}
