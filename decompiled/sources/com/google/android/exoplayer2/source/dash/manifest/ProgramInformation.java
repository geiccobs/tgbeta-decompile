package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public class ProgramInformation {
    public final String copyright;
    public final String lang;
    public final String moreInformationURL;
    public final String source;
    public final String title;

    public ProgramInformation(String title, String source, String copyright, String moreInformationURL, String lang) {
        this.title = title;
        this.source = source;
        this.copyright = copyright;
        this.moreInformationURL = moreInformationURL;
        this.lang = lang;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProgramInformation other = (ProgramInformation) obj;
        return Util.areEqual(this.title, other.title) && Util.areEqual(this.source, other.source) && Util.areEqual(this.copyright, other.copyright) && Util.areEqual(this.moreInformationURL, other.moreInformationURL) && Util.areEqual(this.lang, other.lang);
    }

    public int hashCode() {
        int i = 17 * 31;
        String str = this.title;
        int i2 = 0;
        int result = i + (str != null ? str.hashCode() : 0);
        int result2 = result * 31;
        String str2 = this.source;
        int result3 = (result2 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.copyright;
        int result4 = (result3 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.moreInformationURL;
        int result5 = (result4 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.lang;
        if (str5 != null) {
            i2 = str5.hashCode();
        }
        return result5 + i2;
    }
}
