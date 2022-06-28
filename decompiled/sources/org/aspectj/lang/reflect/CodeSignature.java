package org.aspectj.lang.reflect;
/* loaded from: classes3.dex */
public interface CodeSignature extends MemberSignature {
    Class[] getExceptionTypes();

    String[] getParameterNames();

    Class[] getParameterTypes();
}
