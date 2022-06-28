package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.PointcutExpression;
/* loaded from: classes3.dex */
public class DeclareErrorOrWarningImpl implements DeclareErrorOrWarning {
    private AjType declaringType;
    private boolean isError;
    private String msg;
    private PointcutExpression pc;

    public DeclareErrorOrWarningImpl(String pointcut, String message, boolean isError, AjType decType) {
        this.pc = new PointcutExpressionImpl(pointcut);
        this.msg = message;
        this.isError = isError;
        this.declaringType = decType;
    }

    @Override // org.aspectj.lang.reflect.DeclareErrorOrWarning
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override // org.aspectj.lang.reflect.DeclareErrorOrWarning
    public PointcutExpression getPointcutExpression() {
        return this.pc;
    }

    @Override // org.aspectj.lang.reflect.DeclareErrorOrWarning
    public String getMessage() {
        return this.msg;
    }

    @Override // org.aspectj.lang.reflect.DeclareErrorOrWarning
    public boolean isError() {
        return this.isError;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare ");
        sb.append(isError() ? "error : " : "warning : ");
        sb.append(getPointcutExpression().asString());
        sb.append(" : ");
        sb.append("\"");
        sb.append(getMessage());
        sb.append("\"");
        return sb.toString();
    }
}
