package org.telegram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.telegram.messenger.audioinfo.util.PositionInputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;
/* loaded from: classes4.dex */
public class MP4Box<I extends PositionInputStream> {
    protected static final String ASCII = "ISO8859_1";
    private MP4Atom child;
    protected final DataInput data;
    private final I input;
    private final MP4Box<?> parent;
    private final String type;

    public MP4Box(I input, MP4Box<?> parent, String type) {
        this.input = input;
        this.parent = parent;
        this.type = type;
        this.data = new DataInputStream(input);
    }

    public String getType() {
        return this.type;
    }

    public MP4Box<?> getParent() {
        return this.parent;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public I getInput() {
        return this.input;
    }

    public MP4Atom getChild() {
        return this.child;
    }

    public MP4Atom nextChild() throws IOException {
        RangeInputStream atomInput;
        MP4Atom mP4Atom = this.child;
        if (mP4Atom != null) {
            mP4Atom.skip();
        }
        int atomLength = this.data.readInt();
        byte[] typeBytes = new byte[4];
        this.data.readFully(typeBytes);
        String atomType = new String(typeBytes, ASCII);
        if (atomLength == 1) {
            atomInput = new RangeInputStream(this.input, 16L, this.data.readLong() - 16);
        } else {
            atomInput = new RangeInputStream(this.input, 8L, atomLength - 8);
        }
        MP4Atom mP4Atom2 = new MP4Atom(atomInput, this, atomType);
        this.child = mP4Atom2;
        return mP4Atom2;
    }

    public MP4Atom nextChild(String expectedTypeExpression) throws IOException {
        MP4Atom atom = nextChild();
        if (atom.getType().matches(expectedTypeExpression)) {
            return atom;
        }
        throw new IOException("atom type mismatch, expected " + expectedTypeExpression + ", got " + atom.getType());
    }
}
