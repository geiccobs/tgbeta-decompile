package com.googlecode.mp4parser.h264;
/* loaded from: classes3.dex */
public class BTree {
    private BTree one;
    private Object value;
    private BTree zero;

    public void addString(String path, Object value) {
        BTree branch;
        if (path.length() == 0) {
            this.value = value;
            return;
        }
        char charAt = path.charAt(0);
        if (charAt == '0') {
            if (this.zero == null) {
                this.zero = new BTree();
            }
            branch = this.zero;
        } else {
            BTree branch2 = this.one;
            if (branch2 == null) {
                this.one = new BTree();
            }
            branch = this.one;
        }
        branch.addString(path.substring(1), value);
    }

    public BTree down(int b) {
        if (b == 0) {
            return this.zero;
        }
        return this.one;
    }

    public Object getValue() {
        return this.value;
    }
}
