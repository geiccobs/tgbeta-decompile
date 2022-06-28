package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;
/* loaded from: classes3.dex */
public class DeclarePrecedenceImpl implements DeclarePrecedence {
    private AjType<?> declaringType;
    private TypePattern[] precedenceList;
    private String precedenceString;

    public DeclarePrecedenceImpl(String precedenceList, AjType declaring) {
        this.declaringType = declaring;
        this.precedenceString = precedenceList;
        String toTokenize = precedenceList;
        StringTokenizer strTok = new StringTokenizer(toTokenize.startsWith("(") ? toTokenize.substring(1, toTokenize.length() - 1) : toTokenize, ",");
        this.precedenceList = new TypePattern[strTok.countTokens()];
        int i = 0;
        while (true) {
            TypePattern[] typePatternArr = this.precedenceList;
            if (i < typePatternArr.length) {
                typePatternArr[i] = new TypePatternImpl(strTok.nextToken().trim());
                i++;
            } else {
                return;
            }
        }
    }

    @Override // org.aspectj.lang.reflect.DeclarePrecedence
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override // org.aspectj.lang.reflect.DeclarePrecedence
    public TypePattern[] getPrecedenceOrder() {
        return this.precedenceList;
    }

    public String toString() {
        return "declare precedence : " + this.precedenceString;
    }
}
