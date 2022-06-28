package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.microsoft.appcenter.Constants;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/* loaded from: classes3.dex */
public class SMPTETTTrackImpl extends AbstractTrack {
    public static final String SMPTE_TT_NAMESPACE = "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt";
    boolean containsImages;
    private long[] sampleDurations;
    TrackMetaData trackMetaData = new TrackMetaData();
    SampleDescriptionBox sampleDescriptionBox = new SampleDescriptionBox();
    XMLSubtitleSampleEntry XMLSubtitleSampleEntry = new XMLSubtitleSampleEntry();
    List<Sample> samples = new ArrayList();
    SubSampleInformationBox subSampleInformationBox = new SubSampleInformationBox();

    static long toTime(String expr) {
        Pattern p = Pattern.compile("([0-9][0-9]):([0-9][0-9]):([0-9][0-9])([\\.:][0-9][0-9]?[0-9]?)?");
        Matcher m = p.matcher(expr);
        if (m.matches()) {
            String hours = m.group(1);
            String minutes = m.group(2);
            String seconds = m.group(3);
            String fraction = m.group(4);
            if (fraction == null) {
                fraction = ".000";
            }
            String fraction2 = fraction.replace(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR, ".");
            long ms = Long.parseLong(hours) * 60 * 60 * 1000;
            double parseLong = ms + (Long.parseLong(minutes) * 60 * 1000) + (Long.parseLong(seconds) * 1000);
            Double.isNaN(parseLong);
            long ms2 = (long) (parseLong + (Double.parseDouble("0" + fraction2) * 1000.0d));
            return ms2;
        }
        throw new RuntimeException("Cannot match " + expr + " to time expression");
    }

    public static String getLanguage(Document document) {
        return document.getDocumentElement().getAttribute("xml:lang");
    }

    public static long earliestTimestamp(Document document) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        NamespaceContext ctx = new TextTrackNamespaceContext(null);
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        try {
            XPathExpression timedNodesXpath = xpath.compile("//*[@begin]");
            NodeList timedNodes = (NodeList) timedNodesXpath.evaluate(document, XPathConstants.NODESET);
            long earliestTimestamp = 0;
            for (int i = 0; i < timedNodes.getLength(); i++) {
                Node n = timedNodes.item(i);
                String begin = n.getAttributes().getNamedItem("begin").getNodeValue();
                earliestTimestamp = Math.min(toTime(begin), earliestTimestamp);
            }
            return earliestTimestamp;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static long latestTimestamp(Document document) {
        XPathExpressionException e;
        long end;
        XPathFactory xPathfactory = XPathFactory.newInstance();
        NamespaceContext ctx = new TextTrackNamespaceContext(null);
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        try {
            XPathExpression timedNodesXpath = xpath.compile("//*[@begin]");
            try {
                NodeList timedNodes = (NodeList) timedNodesXpath.evaluate(document, XPathConstants.NODESET);
                long lastTimeStamp = 0;
                for (int i = 0; i < timedNodes.getLength(); i++) {
                    Node n = timedNodes.item(i);
                    String begin = n.getAttributes().getNamedItem("begin").getNodeValue();
                    if (n.getAttributes().getNamedItem("dur") == null) {
                        if (n.getAttributes().getNamedItem(TtmlNode.END) != null) {
                            end = toTime(n.getAttributes().getNamedItem(TtmlNode.END).getNodeValue());
                        } else {
                            throw new RuntimeException("neither end nor dur attribute is present");
                        }
                    } else {
                        end = toTime(begin) + toTime(n.getAttributes().getNamedItem("dur").getNodeValue());
                    }
                    lastTimeStamp = Math.max(end, lastTimeStamp);
                }
                return lastTimeStamp;
            } catch (XPathExpressionException e2) {
                e = e2;
                throw new RuntimeException(e);
            }
        } catch (XPathExpressionException e3) {
            e = e3;
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SMPTETTTrackImpl(java.io.File... r32) throws java.io.IOException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, javax.xml.xpath.XPathExpressionException {
        /*
            Method dump skipped, instructions count: 602
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl.<init>(java.io.File[]):void");
    }

    public byte[] streamToByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8096];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int n = input.read(buffer);
            if (-1 != n) {
                output.write(buffer, 0, n);
            } else {
                return output.toByteArray();
            }
        }
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        long[] adoptedSampleDuration = new long[this.sampleDurations.length];
        for (int i = 0; i < adoptedSampleDuration.length; i++) {
            adoptedSampleDuration[i] = (this.sampleDurations[i] * this.trackMetaData.getTimescale()) / 1000;
        }
        return adoptedSampleDuration;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "subt";
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    /* loaded from: classes3.dex */
    public static class TextTrackNamespaceContext implements NamespaceContext {
        private TextTrackNamespaceContext() {
        }

        /* synthetic */ TextTrackNamespaceContext(TextTrackNamespaceContext textTrackNamespaceContext) {
            this();
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("ttml")) {
                return "http://www.w3.org/ns/ttml";
            }
            if (prefix.equals("smpte")) {
                return SMPTETTTrackImpl.SMPTE_TT_NAMESPACE;
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public Iterator getPrefixes(String val) {
            return Arrays.asList("ttml", "smpte").iterator();
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getPrefix(String uri) {
            if (uri.equals("http://www.w3.org/ns/ttml")) {
                return "ttml";
            }
            if (uri.equals(SMPTETTTrackImpl.SMPTE_TT_NAMESPACE)) {
                return "smpte";
            }
            return null;
        }
    }
}
