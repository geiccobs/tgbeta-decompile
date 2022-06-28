package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Locale;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class WebFile extends TLObject {
    public ArrayList<TLRPC.DocumentAttribute> attributes;
    public TLRPC.InputGeoPoint geo_point;
    public int h;
    public TLRPC.InputWebFileLocation location;
    public String mime_type;
    public int msg_id;
    public TLRPC.InputPeer peer;
    public int scale;
    public int size;
    public String url;
    public int w;
    public int zoom;

    public static WebFile createWithGeoPoint(TLRPC.GeoPoint point, int w, int h, int zoom, int scale) {
        return createWithGeoPoint(point.lat, point._long, point.access_hash, w, h, zoom, scale);
    }

    public static WebFile createWithGeoPoint(double lat, double _long, long access_hash, int w, int h, int zoom, int scale) {
        WebFile webFile = new WebFile();
        TLRPC.TL_inputWebFileGeoPointLocation location = new TLRPC.TL_inputWebFileGeoPointLocation();
        webFile.location = location;
        TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
        webFile.geo_point = tL_inputGeoPoint;
        location.geo_point = tL_inputGeoPoint;
        location.access_hash = access_hash;
        webFile.geo_point.lat = lat;
        webFile.geo_point._long = _long;
        webFile.w = w;
        location.w = w;
        webFile.h = h;
        location.h = h;
        webFile.zoom = zoom;
        location.zoom = zoom;
        webFile.scale = scale;
        location.scale = scale;
        webFile.mime_type = "image/png";
        webFile.url = String.format(Locale.US, "maps_%.6f_%.6f_%d_%d_%d_%d.png", Double.valueOf(lat), Double.valueOf(_long), Integer.valueOf(w), Integer.valueOf(h), Integer.valueOf(zoom), Integer.valueOf(scale));
        webFile.attributes = new ArrayList<>();
        return webFile;
    }

    public static WebFile createWithWebDocument(TLRPC.WebDocument webDocument) {
        if (!(webDocument instanceof TLRPC.TL_webDocument)) {
            return null;
        }
        WebFile webFile = new WebFile();
        TLRPC.TL_webDocument document = (TLRPC.TL_webDocument) webDocument;
        TLRPC.TL_inputWebFileLocation location = new TLRPC.TL_inputWebFileLocation();
        webFile.location = location;
        String str = webDocument.url;
        webFile.url = str;
        location.url = str;
        location.access_hash = document.access_hash;
        webFile.size = document.size;
        webFile.mime_type = document.mime_type;
        webFile.attributes = document.attributes;
        return webFile;
    }
}
