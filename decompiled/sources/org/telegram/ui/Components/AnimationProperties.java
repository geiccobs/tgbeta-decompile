package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Property;
import android.view.animation.OvershootInterpolator;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class AnimationProperties {
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1.9f);
    public static final Property<Paint, Integer> PAINT_ALPHA = new IntProperty<Paint>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.1
        public void setValue(Paint object, int value) {
            object.setAlpha(value);
        }

        public Integer get(Paint object) {
            return Integer.valueOf(object.getAlpha());
        }
    };
    public static final Property<Paint, Integer> PAINT_COLOR = new IntProperty<Paint>(TtmlNode.ATTR_TTS_COLOR) { // from class: org.telegram.ui.Components.AnimationProperties.2
        public void setValue(Paint object, int value) {
            object.setColor(value);
        }

        public Integer get(Paint object) {
            return Integer.valueOf(object.getColor());
        }
    };
    public static final Property<ColorDrawable, Integer> COLOR_DRAWABLE_ALPHA = new IntProperty<ColorDrawable>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.3
        public void setValue(ColorDrawable object, int value) {
            object.setAlpha(value);
        }

        public Integer get(ColorDrawable object) {
            return Integer.valueOf(object.getAlpha());
        }
    };
    public static final Property<ShapeDrawable, Integer> SHAPE_DRAWABLE_ALPHA = new IntProperty<ShapeDrawable>("alpha") { // from class: org.telegram.ui.Components.AnimationProperties.4
        public void setValue(ShapeDrawable object, int value) {
            object.getPaint().setAlpha(value);
        }

        public Integer get(ShapeDrawable object) {
            return Integer.valueOf(object.getPaint().getAlpha());
        }
    };
    public static final Property<ClippingImageView, Float> CLIPPING_IMAGE_VIEW_PROGRESS = new FloatProperty<ClippingImageView>("animationProgress") { // from class: org.telegram.ui.Components.AnimationProperties.5
        public void setValue(ClippingImageView object, float value) {
            object.setAnimationProgress(value);
        }

        public Float get(ClippingImageView object) {
            return Float.valueOf(object.getAnimationProgress());
        }
    };
    public static final Property<PhotoViewer, Float> PHOTO_VIEWER_ANIMATION_VALUE = new FloatProperty<PhotoViewer>("animationValue") { // from class: org.telegram.ui.Components.AnimationProperties.6
        public void setValue(PhotoViewer object, float value) {
            object.setAnimationValue(value);
        }

        public Float get(PhotoViewer object) {
            return Float.valueOf(object.getAnimationValue());
        }
    };
    public static final Property<DialogCell, Float> CLIP_DIALOG_CELL_PROGRESS = new FloatProperty<DialogCell>("clipProgress") { // from class: org.telegram.ui.Components.AnimationProperties.7
        public void setValue(DialogCell object, float value) {
            object.setClipProgress(value);
        }

        public Float get(DialogCell object) {
            return Float.valueOf(object.getClipProgress());
        }
    };

    /* loaded from: classes5.dex */
    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public abstract void setValue(T t, float f);

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.util.Property
        public /* bridge */ /* synthetic */ void set(Object obj, Float f) {
            set2((FloatProperty<T>) obj, f);
        }

        public FloatProperty(String name) {
            super(Float.class, name);
        }

        /* renamed from: set */
        public final void set2(T object, Float value) {
            setValue(object, value.floatValue());
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class IntProperty<T> extends Property<T, Integer> {
        public abstract void setValue(T t, int i);

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.util.Property
        public /* bridge */ /* synthetic */ void set(Object obj, Integer num) {
            set2((IntProperty<T>) obj, num);
        }

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        /* renamed from: set */
        public final void set2(T object, Integer value) {
            setValue(object, value.intValue());
        }
    }
}
