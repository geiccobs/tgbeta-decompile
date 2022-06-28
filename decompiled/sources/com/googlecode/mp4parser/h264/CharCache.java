package com.googlecode.mp4parser.h264;
/* loaded from: classes3.dex */
public class CharCache {
    private char[] cache;
    private int pos;

    public CharCache(int capacity) {
        this.cache = new char[capacity];
    }

    public void append(String str) {
        char[] chars = str.toCharArray();
        char[] cArr = this.cache;
        int length = cArr.length;
        int i = this.pos;
        int available = length - i;
        int toWrite = chars.length < available ? chars.length : available;
        System.arraycopy(chars, 0, cArr, i, toWrite);
        this.pos += toWrite;
    }

    public String toString() {
        return new String(this.cache, 0, this.pos);
    }

    public void clear() {
        this.pos = 0;
    }

    public void append(char c) {
        int i = this.pos;
        char[] cArr = this.cache;
        if (i < cArr.length - 1) {
            cArr[i] = c;
            this.pos = i + 1;
        }
    }

    public int length() {
        return this.pos;
    }
}
