package com.googlecode.mp4parser.boxes.microsoft;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
import org.telegram.ui.BasePermissionsActivity;
/* loaded from: classes3.dex */
public class XtraBox extends AbstractBox {
    private static final long FILETIME_EPOCH_DIFF = 11644473600000L;
    private static final long FILETIME_ONE_MILLISECOND = 10000;
    public static final int MP4_XTRA_BT_FILETIME = 21;
    public static final int MP4_XTRA_BT_GUID = 72;
    public static final int MP4_XTRA_BT_INT64 = 19;
    public static final int MP4_XTRA_BT_UNICODE = 8;
    public static final String TYPE = "Xtra";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
    ByteBuffer data;
    private boolean successfulParse = false;
    Vector<XtraTag> tags = new Vector<>();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("XtraBox.java", XtraBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "toString", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "", "", "", "java.lang.String"), 88);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getAllTagNames", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "", "", "", "[Ljava.lang.String;"), BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:long", "name:value", "", "void"), 289);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getFirstStringValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", CommonProperties.NAME, "", "java.lang.String"), 166);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getFirstDateValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", CommonProperties.NAME, "", "java.util.Date"), 183);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getFirstLongValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", CommonProperties.NAME, "", "java.lang.Long"), 200);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getValues", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", CommonProperties.NAME, "", "[Ljava.lang.Object;"), 216);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "removeTag", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String", CommonProperties.NAME, "", "void"), 236);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTagValues", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:[Ljava.lang.String;", "name:values", "", "void"), 249);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:java.lang.String", "name:value", "", "void"), 265);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTagValue", "com.googlecode.mp4parser.boxes.microsoft.XtraBox", "java.lang.String:java.util.Date", "name:date", "", "void"), 276);
    }

    public XtraBox() {
        super(TYPE);
    }

    public XtraBox(String type) {
        super(type);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        if (this.successfulParse) {
            return detailSize();
        }
        return this.data.limit();
    }

    private int detailSize() {
        int size = 0;
        for (int i = 0; i < this.tags.size(); i++) {
            size += this.tags.elementAt(i).getContentSize();
        }
        return size;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        if (!isParsed()) {
            parseDetails();
        }
        StringBuffer b = new StringBuffer();
        b.append("XtraBox[");
        Iterator<XtraTag> it = this.tags.iterator();
        while (it.hasNext()) {
            XtraTag tag = it.next();
            Iterator it2 = tag.values.iterator();
            while (it2.hasNext()) {
                XtraValue value = (XtraValue) it2.next();
                b.append(tag.tagName);
                b.append("=");
                b.append(value.toString());
                b.append(";");
            }
        }
        b.append("]");
        return b.toString();
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public void _parseDetails(ByteBuffer content) {
        int calcSize;
        int boxSize = content.remaining();
        this.data = content.slice();
        this.successfulParse = false;
        try {
            try {
                this.tags.clear();
                while (content.remaining() > 0) {
                    XtraTag tag = new XtraTag((XtraTag) null);
                    tag.parse(content);
                    this.tags.addElement(tag);
                }
                calcSize = detailSize();
            } catch (Exception e) {
                this.successfulParse = false;
                PrintStream printStream = System.err;
                printStream.println("Malformed Xtra Tag detected: " + e.toString());
                e.printStackTrace();
                content.position(content.position() + content.remaining());
            }
            if (boxSize != calcSize) {
                throw new RuntimeException("Improperly handled Xtra tag: Calculated sizes don't match ( " + boxSize + "/" + calcSize + ")");
            }
            this.successfulParse = true;
        } finally {
            content.order(ByteOrder.BIG_ENDIAN);
        }
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        if (this.successfulParse) {
            for (int i = 0; i < this.tags.size(); i++) {
                this.tags.elementAt(i).getContent(byteBuffer);
            }
            return;
        }
        this.data.rewind();
        byteBuffer.put(this.data);
    }

    public String[] getAllTagNames() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        String[] names = new String[this.tags.size()];
        for (int i = 0; i < this.tags.size(); i++) {
            XtraTag tag = this.tags.elementAt(i);
            names[i] = tag.tagName;
        }
        return names;
    }

    public String getFirstStringValue(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this, name));
        Object[] objs = getValues(name);
        for (Object obj : objs) {
            if (obj instanceof String) {
                return (String) obj;
            }
        }
        return null;
    }

    public Date getFirstDateValue(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this, name));
        Object[] objs = getValues(name);
        for (Object obj : objs) {
            if (obj instanceof Date) {
                return (Date) obj;
            }
        }
        return null;
    }

    public Long getFirstLongValue(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this, name));
        Object[] objs = getValues(name);
        for (Object obj : objs) {
            if (obj instanceof Long) {
                return (Long) obj;
            }
        }
        return null;
    }

    public Object[] getValues(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this, name));
        XtraTag tag = getTagByName(name);
        if (tag == null) {
            return new Object[0];
        }
        Object[] values = new Object[tag.values.size()];
        for (int i = 0; i < tag.values.size(); i++) {
            values[i] = ((XtraValue) tag.values.elementAt(i)).getValueAsObject();
        }
        return values;
    }

    public void removeTag(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this, name));
        XtraTag tag = getTagByName(name);
        if (tag != null) {
            this.tags.remove(tag);
        }
    }

    public void setTagValues(String name, String[] values) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this, name, values));
        removeTag(name);
        XtraTag tag = new XtraTag(name, null);
        for (String str : values) {
            tag.values.addElement(new XtraValue(str, (XtraValue) null));
        }
        this.tags.addElement(tag);
    }

    public void setTagValue(String name, String value) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this, name, value));
        setTagValues(name, new String[]{value});
    }

    public void setTagValue(String name, Date date) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this, name, date));
        removeTag(name);
        XtraTag tag = new XtraTag(name, null);
        tag.values.addElement(new XtraValue(date, (XtraValue) null));
        this.tags.addElement(tag);
    }

    public void setTagValue(String name, long value) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this, name, Conversions.longObject(value)));
        removeTag(name);
        XtraTag tag = new XtraTag(name, null);
        tag.values.addElement(new XtraValue(value, (XtraValue) null));
        this.tags.addElement(tag);
    }

    private XtraTag getTagByName(String name) {
        Iterator<XtraTag> it = this.tags.iterator();
        while (it.hasNext()) {
            XtraTag tag = it.next();
            if (tag.tagName.equals(name)) {
                return tag;
            }
        }
        return null;
    }

    /* loaded from: classes3.dex */
    public static class XtraTag {
        private int inputSize;
        private String tagName;
        private Vector<XtraValue> values;

        private XtraTag() {
            this.values = new Vector<>();
        }

        /* synthetic */ XtraTag(XtraTag xtraTag) {
            this();
        }

        /* synthetic */ XtraTag(String str, XtraTag xtraTag) {
            this(str);
        }

        private XtraTag(String name) {
            this();
            this.tagName = name;
        }

        public void parse(ByteBuffer content) {
            this.inputSize = content.getInt();
            int tagLength = content.getInt();
            this.tagName = XtraBox.readAsciiString(content, tagLength);
            int count = content.getInt();
            for (int i = 0; i < count; i++) {
                XtraValue val = new XtraValue((XtraValue) null);
                val.parse(content);
                this.values.addElement(val);
            }
            int i2 = this.inputSize;
            if (i2 != getContentSize()) {
                throw new RuntimeException("Improperly handled Xtra tag: Sizes don't match ( " + this.inputSize + "/" + getContentSize() + ") on " + this.tagName);
            }
        }

        public void getContent(ByteBuffer b) {
            b.putInt(getContentSize());
            b.putInt(this.tagName.length());
            XtraBox.writeAsciiString(b, this.tagName);
            b.putInt(this.values.size());
            for (int i = 0; i < this.values.size(); i++) {
                this.values.elementAt(i).getContent(b);
            }
        }

        public int getContentSize() {
            int size = this.tagName.length() + 12;
            for (int i = 0; i < this.values.size(); i++) {
                size += this.values.elementAt(i).getContentSize();
            }
            return size;
        }

        public String toString() {
            StringBuffer b = new StringBuffer();
            b.append(this.tagName);
            b.append(" [");
            b.append(this.inputSize);
            b.append("/");
            b.append(this.values.size());
            b.append("]:\n");
            for (int i = 0; i < this.values.size(); i++) {
                b.append("  ");
                b.append(this.values.elementAt(i).toString());
                b.append("\n");
            }
            return b.toString();
        }
    }

    /* loaded from: classes3.dex */
    public static class XtraValue {
        public Date fileTimeValue;
        public long longValue;
        public byte[] nonParsedValue;
        public String stringValue;
        public int type;

        private XtraValue() {
        }

        /* synthetic */ XtraValue(XtraValue xtraValue) {
            this();
        }

        private XtraValue(String val) {
            this.type = 8;
            this.stringValue = val;
        }

        /* synthetic */ XtraValue(String str, XtraValue xtraValue) {
            this(str);
        }

        private XtraValue(long longVal) {
            this.type = 19;
            this.longValue = longVal;
        }

        /* synthetic */ XtraValue(long j, XtraValue xtraValue) {
            this(j);
        }

        private XtraValue(Date time) {
            this.type = 21;
            this.fileTimeValue = time;
        }

        /* synthetic */ XtraValue(Date date, XtraValue xtraValue) {
            this(date);
        }

        public Object getValueAsObject() {
            switch (this.type) {
                case 8:
                    return this.stringValue;
                case 19:
                    return new Long(this.longValue);
                case 21:
                    return this.fileTimeValue;
                default:
                    return this.nonParsedValue;
            }
        }

        public void parse(ByteBuffer content) {
            int length = content.getInt() - 6;
            this.type = content.getShort();
            content.order(ByteOrder.LITTLE_ENDIAN);
            switch (this.type) {
                case 8:
                    this.stringValue = XtraBox.readUtf16String(content, length);
                    break;
                case 19:
                    this.longValue = content.getLong();
                    break;
                case 21:
                    this.fileTimeValue = new Date(XtraBox.filetimeToMillis(content.getLong()));
                    break;
                default:
                    byte[] bArr = new byte[length];
                    this.nonParsedValue = bArr;
                    content.get(bArr);
                    break;
            }
            content.order(ByteOrder.BIG_ENDIAN);
        }

        public void getContent(ByteBuffer b) {
            try {
                int length = getContentSize();
                b.putInt(length);
                b.putShort((short) this.type);
                b.order(ByteOrder.LITTLE_ENDIAN);
                switch (this.type) {
                    case 8:
                        XtraBox.writeUtf16String(b, this.stringValue);
                        break;
                    case 19:
                        b.putLong(this.longValue);
                        break;
                    case 21:
                        b.putLong(XtraBox.millisToFiletime(this.fileTimeValue.getTime()));
                        break;
                    default:
                        b.put(this.nonParsedValue);
                        break;
                }
            } finally {
                b.order(ByteOrder.BIG_ENDIAN);
            }
        }

        public int getContentSize() {
            switch (this.type) {
                case 8:
                    int size = 6 + (this.stringValue.length() * 2) + 2;
                    return size;
                case 19:
                case 21:
                    int size2 = 6 + 8;
                    return size2;
                default:
                    int size3 = 6 + this.nonParsedValue.length;
                    return size3;
            }
        }

        public String toString() {
            switch (this.type) {
                case 8:
                    return "[string]" + this.stringValue;
                case 19:
                    return "[long]" + String.valueOf(this.longValue);
                case 21:
                    return "[filetime]" + this.fileTimeValue.toString();
                default:
                    return "[GUID](nonParsed)";
            }
        }
    }

    public static long filetimeToMillis(long filetime) {
        return (filetime / FILETIME_ONE_MILLISECOND) - FILETIME_EPOCH_DIFF;
    }

    public static long millisToFiletime(long millis) {
        return (FILETIME_EPOCH_DIFF + millis) * FILETIME_ONE_MILLISECOND;
    }

    public static void writeAsciiString(ByteBuffer dest, String s) {
        try {
            dest.put(s.getBytes(C.ASCII_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    public static String readAsciiString(ByteBuffer content, int length) {
        byte[] s = new byte[length];
        content.get(s);
        try {
            return new String(s, C.ASCII_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    public static String readUtf16String(ByteBuffer content, int length) {
        char[] s = new char[(length / 2) - 1];
        for (int i = 0; i < (length / 2) - 1; i++) {
            s[i] = content.getChar();
        }
        content.getChar();
        return new String(s);
    }

    public static void writeUtf16String(ByteBuffer dest, String s) {
        char[] ar = s.toCharArray();
        for (char c : ar) {
            dest.putChar(c);
        }
        dest.putChar((char) 0);
    }
}
