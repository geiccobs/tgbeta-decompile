package j$.util;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.function.DoubleConsumer;
/* loaded from: classes2.dex */
public class DoubleSummaryStatistics implements DoubleConsumer {
    private long count;
    private double simpleSum;
    private double sum;
    private double sumCompensation;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return doubleConsumer.getClass();
    }

    @Override // j$.util.function.DoubleConsumer
    public void accept(double value) {
        this.count++;
        this.simpleSum += value;
        sumWithCompensation(value);
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
    }

    public void combine(DoubleSummaryStatistics other) {
        this.count += other.count;
        this.simpleSum += other.simpleSum;
        sumWithCompensation(other.sum);
        sumWithCompensation(other.sumCompensation);
        this.min = Math.min(this.min, other.min);
        this.max = Math.max(this.max, other.max);
    }

    private void sumWithCompensation(double value) {
        double tmp = value - this.sumCompensation;
        double d = this.sum;
        double velvel = d + tmp;
        this.sumCompensation = (velvel - d) - tmp;
        this.sum = velvel;
    }

    public final long getCount() {
        return this.count;
    }

    public final double getSum() {
        double tmp = this.sum + this.sumCompensation;
        if (Double.isNaN(tmp) && Double.isInfinite(this.simpleSum)) {
            return this.simpleSum;
        }
        return tmp;
    }

    public final double getMin() {
        return this.min;
    }

    public final double getMax() {
        return this.max;
    }

    public final double getAverage() {
        if (getCount() > 0) {
            double sum = getSum();
            double count = getCount();
            Double.isNaN(count);
            return sum / count;
        }
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public String toString() {
        return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", getClass().getSimpleName(), Long.valueOf(getCount()), Double.valueOf(getSum()), Double.valueOf(getMin()), Double.valueOf(getAverage()), Double.valueOf(getMax()));
    }
}
