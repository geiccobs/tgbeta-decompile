package com.googlecode.mp4parser.srt;

import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.microsoft.appcenter.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
/* loaded from: classes3.dex */
public class SrtParser {
    public static TextTrackImpl parse(InputStream is) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        TextTrackImpl track = new TextTrackImpl();
        while (r.readLine() != null) {
            String timeString = r.readLine();
            String lineString = "";
            while (true) {
                String s = r.readLine();
                if (s != null && !s.trim().equals("")) {
                    lineString = String.valueOf(lineString) + s + "\n";
                }
            }
            long startTime = parse(timeString.split("-->")[0]);
            long endTime = parse(timeString.split("-->")[1]);
            track.getSubs().add(new TextTrackImpl.Line(startTime, endTime, lineString));
            r = r;
            track = track;
        }
        return track;
    }

    private static long parse(String in) {
        long hours = Long.parseLong(in.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR)[0].trim());
        long minutes = Long.parseLong(in.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR)[1].trim());
        long seconds = Long.parseLong(in.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR)[2].split(",")[0].trim());
        long millies = Long.parseLong(in.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR)[2].split(",")[1].trim());
        return (hours * 60 * 60 * 1000) + (60 * minutes * 1000) + (1000 * seconds) + millies;
    }
}
