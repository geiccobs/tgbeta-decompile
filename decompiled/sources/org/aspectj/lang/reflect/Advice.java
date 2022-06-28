package org.aspectj.lang.reflect;

import java.lang.reflect.Type;
/* loaded from: classes3.dex */
public interface Advice {
    AjType getDeclaringType();

    AjType<?>[] getExceptionTypes();

    Type[] getGenericParameterTypes();

    AdviceKind getKind();

    String getName();

    AjType<?>[] getParameterTypes();

    PointcutExpression getPointcutExpression();
}
