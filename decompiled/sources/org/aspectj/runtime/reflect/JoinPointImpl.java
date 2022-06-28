package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
/* loaded from: classes3.dex */
public class JoinPointImpl implements ProceedingJoinPoint {
    Object _this;
    private AroundClosure arc;
    Object[] args;
    JoinPoint.StaticPart staticPart;
    Object target;

    /* loaded from: classes3.dex */
    public static class StaticPartImpl implements JoinPoint.StaticPart {
        private int id;
        String kind;
        Signature signature;
        SourceLocation sourceLocation;

        public StaticPartImpl(int id, String kind, Signature signature, SourceLocation sourceLocation) {
            this.kind = kind;
            this.signature = signature;
            this.sourceLocation = sourceLocation;
            this.id = id;
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public int getId() {
            return this.id;
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public String getKind() {
            return this.kind;
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public Signature getSignature() {
            return this.signature;
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public SourceLocation getSourceLocation() {
            return this.sourceLocation;
        }

        String toString(StringMaker sm) {
            StringBuffer buf = new StringBuffer();
            buf.append(sm.makeKindName(getKind()));
            buf.append("(");
            buf.append(((SignatureImpl) getSignature()).toString(sm));
            buf.append(")");
            return buf.toString();
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public final String toString() {
            return toString(StringMaker.middleStringMaker);
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public final String toShortString() {
            return toString(StringMaker.shortStringMaker);
        }

        @Override // org.aspectj.lang.JoinPoint.StaticPart
        public final String toLongString() {
            return toString(StringMaker.longStringMaker);
        }
    }

    /* loaded from: classes3.dex */
    static class EnclosingStaticPartImpl extends StaticPartImpl implements JoinPoint.EnclosingStaticPart {
        public EnclosingStaticPartImpl(int count, String kind, Signature signature, SourceLocation sourceLocation) {
            super(count, kind, signature, sourceLocation);
        }
    }

    public JoinPointImpl(JoinPoint.StaticPart staticPart, Object _this, Object target, Object[] args) {
        this.staticPart = staticPart;
        this._this = _this;
        this.target = target;
        this.args = args;
    }

    @Override // org.aspectj.lang.JoinPoint
    public Object getThis() {
        return this._this;
    }

    @Override // org.aspectj.lang.JoinPoint
    public Object getTarget() {
        return this.target;
    }

    @Override // org.aspectj.lang.JoinPoint
    public Object[] getArgs() {
        if (this.args == null) {
            this.args = new Object[0];
        }
        Object[] objArr = this.args;
        Object[] argsCopy = new Object[objArr.length];
        System.arraycopy(objArr, 0, argsCopy, 0, objArr.length);
        return argsCopy;
    }

    @Override // org.aspectj.lang.JoinPoint
    public JoinPoint.StaticPart getStaticPart() {
        return this.staticPart;
    }

    @Override // org.aspectj.lang.JoinPoint
    public String getKind() {
        return this.staticPart.getKind();
    }

    @Override // org.aspectj.lang.JoinPoint
    public Signature getSignature() {
        return this.staticPart.getSignature();
    }

    @Override // org.aspectj.lang.JoinPoint
    public SourceLocation getSourceLocation() {
        return this.staticPart.getSourceLocation();
    }

    @Override // org.aspectj.lang.JoinPoint
    public final String toString() {
        return this.staticPart.toString();
    }

    @Override // org.aspectj.lang.JoinPoint
    public final String toShortString() {
        return this.staticPart.toShortString();
    }

    @Override // org.aspectj.lang.JoinPoint
    public final String toLongString() {
        return this.staticPart.toLongString();
    }

    @Override // org.aspectj.lang.ProceedingJoinPoint
    public void set$AroundClosure(AroundClosure arc) {
        this.arc = arc;
    }

    @Override // org.aspectj.lang.ProceedingJoinPoint
    public Object proceed() throws Throwable {
        AroundClosure aroundClosure = this.arc;
        if (aroundClosure == null) {
            return null;
        }
        return aroundClosure.run(aroundClosure.getState());
    }

    @Override // org.aspectj.lang.ProceedingJoinPoint
    public Object proceed(Object[] adviceBindings) throws Throwable {
        AroundClosure aroundClosure = this.arc;
        if (aroundClosure == null) {
            return null;
        }
        int flags = aroundClosure.getFlags();
        char c = 0;
        char c2 = 1;
        if ((1048576 & flags) != 0) {
        }
        boolean thisTargetTheSame = (65536 & flags) != 0;
        boolean hasThis = (flags & 4096) != 0;
        boolean bindsThis = (flags & 256) != 0;
        boolean hasTarget = (flags & 16) != 0;
        boolean bindsTarget = (flags & 1) != 0;
        Object[] state = this.arc.getState();
        int firstArgumentIndexIntoAdviceBindings = 0;
        int firstArgumentIndexIntoState = 0 + (hasThis ? 1 : 0);
        int firstArgumentIndexIntoState2 = firstArgumentIndexIntoState + ((!hasTarget || thisTargetTheSame) ? 0 : 1);
        if (hasThis && bindsThis) {
            firstArgumentIndexIntoAdviceBindings = 1;
            state[0] = adviceBindings[0];
        }
        if (hasTarget && bindsTarget) {
            if (thisTargetTheSame) {
                firstArgumentIndexIntoAdviceBindings = (bindsThis ? 1 : 0) + 1;
                if (!bindsThis) {
                    c2 = 0;
                }
                state[0] = adviceBindings[c2];
            } else {
                firstArgumentIndexIntoAdviceBindings = (hasThis ? 1 : 0) + 1;
                char c3 = hasThis ? (char) 1 : (char) 0;
                if (hasThis) {
                    c = 1;
                }
                state[c3] = adviceBindings[c];
            }
        }
        for (int i = firstArgumentIndexIntoAdviceBindings; i < adviceBindings.length; i++) {
            state[(i - firstArgumentIndexIntoAdviceBindings) + firstArgumentIndexIntoState2] = adviceBindings[i];
        }
        return this.arc.run(state);
    }
}
