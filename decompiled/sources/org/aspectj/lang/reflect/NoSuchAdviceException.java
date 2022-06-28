package org.aspectj.lang.reflect;
/* loaded from: classes3.dex */
public class NoSuchAdviceException extends Exception {
    private static final long serialVersionUID = 3256444698657634352L;
    private String name;

    public NoSuchAdviceException(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
