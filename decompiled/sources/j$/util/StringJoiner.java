package j$.util;
/* loaded from: classes2.dex */
public final class StringJoiner {
    private final String delimiter;
    private String emptyValue;
    private final String prefix;
    private final String suffix;
    private StringBuilder value;

    public StringJoiner(CharSequence delimiter) {
        this(delimiter, "", "");
    }

    public StringJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        Objects.requireNonNull(prefix, "The prefix must not be null");
        Objects.requireNonNull(delimiter, "The delimiter must not be null");
        Objects.requireNonNull(suffix, "The suffix must not be null");
        String charSequence = prefix.toString();
        this.prefix = charSequence;
        this.delimiter = delimiter.toString();
        String charSequence2 = suffix.toString();
        this.suffix = charSequence2;
        this.emptyValue = charSequence + charSequence2;
    }

    public StringJoiner setEmptyValue(CharSequence emptyValue) {
        this.emptyValue = ((CharSequence) Objects.requireNonNull(emptyValue, "The empty value must not be null")).toString();
        return this;
    }

    public String toString() {
        if (this.value == null) {
            return this.emptyValue;
        }
        if (this.suffix.equals("")) {
            return this.value.toString();
        }
        int initialLength = this.value.length();
        StringBuilder sb = this.value;
        sb.append(this.suffix);
        String result = sb.toString();
        this.value.setLength(initialLength);
        return result;
    }

    public StringJoiner add(CharSequence newElement) {
        prepareBuilder().append(newElement);
        return this;
    }

    public StringJoiner merge(StringJoiner other) {
        other.getClass();
        StringBuilder sb = other.value;
        if (sb != null) {
            int length = sb.length();
            StringBuilder builder = prepareBuilder();
            builder.append((CharSequence) other.value, other.prefix.length(), length);
        }
        return this;
    }

    private StringBuilder prepareBuilder() {
        StringBuilder sb = this.value;
        if (sb != null) {
            sb.append(this.delimiter);
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.prefix);
            this.value = sb2;
        }
        return this.value;
    }

    public int length() {
        StringBuilder sb = this.value;
        return sb != null ? sb.length() + this.suffix.length() : this.emptyValue.length();
    }
}
