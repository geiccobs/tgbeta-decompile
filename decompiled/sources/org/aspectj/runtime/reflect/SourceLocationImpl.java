package org.aspectj.runtime.reflect;

import com.microsoft.appcenter.Constants;
import org.aspectj.lang.reflect.SourceLocation;
/* loaded from: classes3.dex */
class SourceLocationImpl implements SourceLocation {
    String fileName;
    int line;
    Class withinType;

    public SourceLocationImpl(Class withinType, String fileName, int line) {
        this.withinType = withinType;
        this.fileName = fileName;
        this.line = line;
    }

    @Override // org.aspectj.lang.reflect.SourceLocation
    public Class getWithinType() {
        return this.withinType;
    }

    @Override // org.aspectj.lang.reflect.SourceLocation
    public String getFileName() {
        return this.fileName;
    }

    @Override // org.aspectj.lang.reflect.SourceLocation
    public int getLine() {
        return this.line;
    }

    @Override // org.aspectj.lang.reflect.SourceLocation
    public int getColumn() {
        return -1;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getFileName());
        stringBuffer.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        stringBuffer.append(getLine());
        return stringBuffer.toString();
    }
}
