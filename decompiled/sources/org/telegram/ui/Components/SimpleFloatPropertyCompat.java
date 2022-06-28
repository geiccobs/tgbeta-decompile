package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
/* loaded from: classes5.dex */
public class SimpleFloatPropertyCompat<T> extends FloatPropertyCompat<T> {
    private Getter<T> getter;
    private float multiplier = 1.0f;
    private Setter<T> setter;

    /* loaded from: classes5.dex */
    public interface Getter<T> {
        float get(T t);
    }

    /* loaded from: classes5.dex */
    public interface Setter<T> {
        void set(T t, float f);
    }

    public SimpleFloatPropertyCompat(String name, Getter<T> getter, Setter<T> setter) {
        super(name);
        this.getter = getter;
        this.setter = setter;
    }

    public SimpleFloatPropertyCompat<T> setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
    public float getValue(T object) {
        return this.getter.get(object) * this.multiplier;
    }

    @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
    public void setValue(T object, float value) {
        this.setter.set(object, value / this.multiplier);
    }
}
