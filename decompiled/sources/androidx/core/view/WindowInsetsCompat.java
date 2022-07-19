package androidx.core.view;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import androidx.core.graphics.Insets;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.core.util.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
public class WindowInsetsCompat {
    private final Impl mImpl;

    static {
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsCompat windowInsetsCompat = Impl30.CONSUMED;
        } else {
            WindowInsetsCompat windowInsetsCompat2 = Impl.CONSUMED;
        }
    }

    private WindowInsetsCompat(WindowInsets insets) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            this.mImpl = new Impl30(this, insets);
        } else if (i >= 29) {
            this.mImpl = new Impl29(this, insets);
        } else if (i >= 28) {
            this.mImpl = new Impl28(this, insets);
        } else if (i >= 21) {
            this.mImpl = new Impl21(this, insets);
        } else if (i >= 20) {
            this.mImpl = new Impl20(this, insets);
        } else {
            this.mImpl = new Impl(this);
        }
    }

    public WindowInsetsCompat(final WindowInsetsCompat src) {
        if (src != null) {
            Impl impl = src.mImpl;
            int i = Build.VERSION.SDK_INT;
            if (i >= 30 && (impl instanceof Impl30)) {
                this.mImpl = new Impl30(this, (Impl30) impl);
            } else if (i >= 29 && (impl instanceof Impl29)) {
                this.mImpl = new Impl29(this, (Impl29) impl);
            } else if (i >= 28 && (impl instanceof Impl28)) {
                this.mImpl = new Impl28(this, (Impl28) impl);
            } else if (i >= 21 && (impl instanceof Impl21)) {
                this.mImpl = new Impl21(this, (Impl21) impl);
            } else if (i >= 20 && (impl instanceof Impl20)) {
                this.mImpl = new Impl20(this, (Impl20) impl);
            } else {
                this.mImpl = new Impl(this);
            }
            impl.copyWindowDataInto(this);
            return;
        }
        this.mImpl = new Impl(this);
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets insets) {
        return toWindowInsetsCompat(insets, null);
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets insets, View view) {
        WindowInsetsCompat windowInsetsCompat = new WindowInsetsCompat((WindowInsets) Preconditions.checkNotNull(insets));
        if (view != null && view.isAttachedToWindow()) {
            windowInsetsCompat.setRootWindowInsets(ViewCompat.getRootWindowInsets(view));
            windowInsetsCompat.copyRootViewBounds(view.getRootView());
        }
        return windowInsetsCompat;
    }

    @Deprecated
    public int getSystemWindowInsetLeft() {
        return this.mImpl.getSystemWindowInsets().left;
    }

    @Deprecated
    public int getSystemWindowInsetTop() {
        return this.mImpl.getSystemWindowInsets().top;
    }

    @Deprecated
    public int getSystemWindowInsetRight() {
        return this.mImpl.getSystemWindowInsets().right;
    }

    @Deprecated
    public int getSystemWindowInsetBottom() {
        return this.mImpl.getSystemWindowInsets().bottom;
    }

    public boolean isConsumed() {
        return this.mImpl.isConsumed();
    }

    @Deprecated
    public WindowInsetsCompat consumeSystemWindowInsets() {
        return this.mImpl.consumeSystemWindowInsets();
    }

    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return new Builder(this).setSystemWindowInsets(Insets.of(left, top, right, bottom)).build();
    }

    @Deprecated
    public WindowInsetsCompat consumeStableInsets() {
        return this.mImpl.consumeStableInsets();
    }

    public DisplayCutoutCompat getDisplayCutout() {
        return this.mImpl.getDisplayCutout();
    }

    @Deprecated
    public WindowInsetsCompat consumeDisplayCutout() {
        return this.mImpl.consumeDisplayCutout();
    }

    @Deprecated
    public Insets getStableInsets() {
        return this.mImpl.getStableInsets();
    }

    public Insets getInsets(int typeMask) {
        return this.mImpl.getInsets(typeMask);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof WindowInsetsCompat) {
            return ObjectsCompat.equals(this.mImpl, ((WindowInsetsCompat) o).mImpl);
        }
        return false;
    }

    public int hashCode() {
        Impl impl = this.mImpl;
        if (impl == null) {
            return 0;
        }
        return impl.hashCode();
    }

    public WindowInsets toWindowInsets() {
        Impl impl = this.mImpl;
        if (impl instanceof Impl20) {
            return ((Impl20) impl).mPlatformInsets;
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static class Impl {
        static final WindowInsetsCompat CONSUMED = new Builder().build().consumeDisplayCutout().consumeStableInsets().consumeSystemWindowInsets();
        final WindowInsetsCompat mHost;

        void copyRootViewBounds(View rootView) {
        }

        void copyWindowDataInto(WindowInsetsCompat other) {
        }

        DisplayCutoutCompat getDisplayCutout() {
            return null;
        }

        boolean isConsumed() {
            return false;
        }

        boolean isRound() {
            return false;
        }

        public void setOverriddenInsets(Insets[] insetsTypeMask) {
        }

        void setRootViewData(Insets visibleInsets) {
        }

        void setRootWindowInsets(WindowInsetsCompat rootWindowInsets) {
        }

        public void setStableInsets(Insets stableInsets) {
        }

        Impl(WindowInsetsCompat host) {
            this.mHost = host;
        }

        WindowInsetsCompat consumeSystemWindowInsets() {
            return this.mHost;
        }

        WindowInsetsCompat consumeStableInsets() {
            return this.mHost;
        }

        WindowInsetsCompat consumeDisplayCutout() {
            return this.mHost;
        }

        Insets getSystemWindowInsets() {
            return Insets.NONE;
        }

        Insets getStableInsets() {
            return Insets.NONE;
        }

        Insets getSystemGestureInsets() {
            return getSystemWindowInsets();
        }

        Insets getMandatorySystemGestureInsets() {
            return getSystemWindowInsets();
        }

        Insets getTappableElementInsets() {
            return getSystemWindowInsets();
        }

        Insets getInsets(int typeMask) {
            return Insets.NONE;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Impl)) {
                return false;
            }
            Impl impl = (Impl) o;
            return isRound() == impl.isRound() && isConsumed() == impl.isConsumed() && ObjectsCompat.equals(getSystemWindowInsets(), impl.getSystemWindowInsets()) && ObjectsCompat.equals(getStableInsets(), impl.getStableInsets()) && ObjectsCompat.equals(getDisplayCutout(), impl.getDisplayCutout());
        }

        public int hashCode() {
            return ObjectsCompat.hash(Boolean.valueOf(isRound()), Boolean.valueOf(isConsumed()), getSystemWindowInsets(), getStableInsets(), getDisplayCutout());
        }
    }

    /* loaded from: classes.dex */
    public static class Impl20 extends Impl {
        private static Class<?> sAttachInfoClass = null;
        private static Field sAttachInfoField = null;
        private static Method sGetViewRootImplMethod = null;
        private static Class<?> sViewRootImplClass = null;
        private static Field sVisibleInsetsField = null;
        private static boolean sVisibleRectReflectionFetched = false;
        private Insets[] mOverriddenInsets;
        final WindowInsets mPlatformInsets;
        Insets mRootViewVisibleInsets;
        private WindowInsetsCompat mRootWindowInsets;
        private Insets mSystemWindowInsets;

        Impl20(WindowInsetsCompat host, WindowInsets insets) {
            super(host);
            this.mSystemWindowInsets = null;
            this.mPlatformInsets = insets;
        }

        Impl20(WindowInsetsCompat host, Impl20 other) {
            this(host, new WindowInsets(other.mPlatformInsets));
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        boolean isRound() {
            return this.mPlatformInsets.isRound();
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        public Insets getInsets(int typeMask) {
            return getInsets(typeMask, false);
        }

        @SuppressLint({"WrongConstant"})
        private Insets getInsets(final int typeMask, final boolean ignoreVisibility) {
            Insets insets = Insets.NONE;
            for (int i = 1; i <= 256; i <<= 1) {
                if ((typeMask & i) != 0) {
                    insets = Insets.max(insets, getInsetsForType(i, ignoreVisibility));
                }
            }
            return insets;
        }

        protected Insets getInsetsForType(int type, boolean ignoreVisibility) {
            int i;
            DisplayCutoutCompat displayCutoutCompat;
            if (type == 1) {
                if (ignoreVisibility) {
                    return Insets.of(0, Math.max(getRootStableInsets().top, getSystemWindowInsets().top), 0, 0);
                }
                return Insets.of(0, getSystemWindowInsets().top, 0, 0);
            }
            Insets insets = null;
            if (type == 2) {
                if (ignoreVisibility) {
                    Insets rootStableInsets = getRootStableInsets();
                    Insets stableInsets = getStableInsets();
                    return Insets.of(Math.max(rootStableInsets.left, stableInsets.left), 0, Math.max(rootStableInsets.right, stableInsets.right), Math.max(rootStableInsets.bottom, stableInsets.bottom));
                }
                Insets systemWindowInsets = getSystemWindowInsets();
                WindowInsetsCompat windowInsetsCompat = this.mRootWindowInsets;
                if (windowInsetsCompat != null) {
                    insets = windowInsetsCompat.getStableInsets();
                }
                int i2 = systemWindowInsets.bottom;
                if (insets != null) {
                    i2 = Math.min(i2, insets.bottom);
                }
                return Insets.of(systemWindowInsets.left, 0, systemWindowInsets.right, i2);
            } else if (type != 8) {
                if (type == 16) {
                    return getSystemGestureInsets();
                }
                if (type == 32) {
                    return getMandatorySystemGestureInsets();
                }
                if (type == 64) {
                    return getTappableElementInsets();
                }
                if (type == 128) {
                    WindowInsetsCompat windowInsetsCompat2 = this.mRootWindowInsets;
                    if (windowInsetsCompat2 != null) {
                        displayCutoutCompat = windowInsetsCompat2.getDisplayCutout();
                    } else {
                        displayCutoutCompat = getDisplayCutout();
                    }
                    if (displayCutoutCompat != null) {
                        return Insets.of(displayCutoutCompat.getSafeInsetLeft(), displayCutoutCompat.getSafeInsetTop(), displayCutoutCompat.getSafeInsetRight(), displayCutoutCompat.getSafeInsetBottom());
                    }
                    return Insets.NONE;
                }
                return Insets.NONE;
            } else {
                Insets[] insetsArr = this.mOverriddenInsets;
                if (insetsArr != null) {
                    insets = insetsArr[Type.indexOf(8)];
                }
                if (insets != null) {
                    return insets;
                }
                Insets systemWindowInsets2 = getSystemWindowInsets();
                Insets rootStableInsets2 = getRootStableInsets();
                int i3 = systemWindowInsets2.bottom;
                if (i3 > rootStableInsets2.bottom) {
                    return Insets.of(0, 0, 0, i3);
                }
                Insets insets2 = this.mRootViewVisibleInsets;
                if (insets2 != null && !insets2.equals(Insets.NONE) && (i = this.mRootViewVisibleInsets.bottom) > rootStableInsets2.bottom) {
                    return Insets.of(0, 0, 0, i);
                }
                return Insets.NONE;
            }
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        final Insets getSystemWindowInsets() {
            if (this.mSystemWindowInsets == null) {
                this.mSystemWindowInsets = Insets.of(this.mPlatformInsets.getSystemWindowInsetLeft(), this.mPlatformInsets.getSystemWindowInsetTop(), this.mPlatformInsets.getSystemWindowInsetRight(), this.mPlatformInsets.getSystemWindowInsetBottom());
            }
            return this.mSystemWindowInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        void copyWindowDataInto(WindowInsetsCompat other) {
            other.setRootWindowInsets(this.mRootWindowInsets);
            other.setRootViewData(this.mRootViewVisibleInsets);
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        void setRootWindowInsets(WindowInsetsCompat rootWindowInsets) {
            this.mRootWindowInsets = rootWindowInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        void setRootViewData(Insets visibleInsets) {
            this.mRootViewVisibleInsets = visibleInsets;
        }

        private Insets getRootStableInsets() {
            WindowInsetsCompat windowInsetsCompat = this.mRootWindowInsets;
            if (windowInsetsCompat != null) {
                return windowInsetsCompat.getStableInsets();
            }
            return Insets.NONE;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        void copyRootViewBounds(View rootView) {
            Insets visibleInsets = getVisibleInsets(rootView);
            if (visibleInsets == null) {
                visibleInsets = Insets.NONE;
            }
            setRootViewData(visibleInsets);
        }

        private Insets getVisibleInsets(View rootView) {
            if (Build.VERSION.SDK_INT >= 30) {
                throw new UnsupportedOperationException("getVisibleInsets() should not be called on API >= 30. Use WindowInsets.isVisible() instead.");
            }
            if (!sVisibleRectReflectionFetched) {
                loadReflectionField();
            }
            Method method = sGetViewRootImplMethod;
            if (method != null && sAttachInfoClass != null && sVisibleInsetsField != null) {
                try {
                    Object invoke = method.invoke(rootView, new Object[0]);
                    if (invoke == null) {
                        Log.w("WindowInsetsCompat", "Failed to get visible insets. getViewRootImpl() returned null from the provided view. This means that the view is either not attached or the method has been overridden", new NullPointerException());
                        return null;
                    }
                    Rect rect = (Rect) sVisibleInsetsField.get(sAttachInfoField.get(invoke));
                    if (rect == null) {
                        return null;
                    }
                    return Insets.of(rect);
                } catch (ReflectiveOperationException e) {
                    Log.e("WindowInsetsCompat", "Failed to get visible insets. (Reflection error). " + e.getMessage(), e);
                }
            }
            return null;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        public void setOverriddenInsets(Insets[] insetsTypeMask) {
            this.mOverriddenInsets = insetsTypeMask;
        }

        @SuppressLint({"PrivateApi"})
        private static void loadReflectionField() {
            try {
                sGetViewRootImplMethod = View.class.getDeclaredMethod("getViewRootImpl", new Class[0]);
                sViewRootImplClass = Class.forName("android.view.ViewRootImpl");
                Class<?> cls = Class.forName("android.view.View$AttachInfo");
                sAttachInfoClass = cls;
                sVisibleInsetsField = cls.getDeclaredField("mVisibleInsets");
                sAttachInfoField = sViewRootImplClass.getDeclaredField("mAttachInfo");
                sVisibleInsetsField.setAccessible(true);
                sAttachInfoField.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                Log.e("WindowInsetsCompat", "Failed to get visible insets. (Reflection error). " + e.getMessage(), e);
            }
            sVisibleRectReflectionFetched = true;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            return ObjectsCompat$$ExternalSyntheticBackport0.m(this.mRootViewVisibleInsets, ((Impl20) o).mRootViewVisibleInsets);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Impl21 extends Impl20 {
        private Insets mStableInsets;

        Impl21(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
            this.mStableInsets = null;
        }

        Impl21(WindowInsetsCompat host, Impl21 other) {
            super(host, other);
            this.mStableInsets = null;
            this.mStableInsets = other.mStableInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        boolean isConsumed() {
            return this.mPlatformInsets.isConsumed();
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        WindowInsetsCompat consumeStableInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeStableInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        WindowInsetsCompat consumeSystemWindowInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeSystemWindowInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        final Insets getStableInsets() {
            if (this.mStableInsets == null) {
                this.mStableInsets = Insets.of(this.mPlatformInsets.getStableInsetLeft(), this.mPlatformInsets.getStableInsetTop(), this.mPlatformInsets.getStableInsetRight(), this.mPlatformInsets.getStableInsetBottom());
            }
            return this.mStableInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        public void setStableInsets(Insets stableInsets) {
            this.mStableInsets = stableInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Impl28 extends Impl21 {
        Impl28(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl28(WindowInsetsCompat host, Impl28 other) {
            super(host, other);
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        DisplayCutoutCompat getDisplayCutout() {
            return DisplayCutoutCompat.wrap(this.mPlatformInsets.getDisplayCutout());
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        WindowInsetsCompat consumeDisplayCutout() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeDisplayCutout());
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl20, androidx.core.view.WindowInsetsCompat.Impl
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Impl28)) {
                return false;
            }
            Impl28 impl28 = (Impl28) o;
            return ObjectsCompat$$ExternalSyntheticBackport0.m(this.mPlatformInsets, impl28.mPlatformInsets) && ObjectsCompat$$ExternalSyntheticBackport0.m(this.mRootViewVisibleInsets, impl28.mRootViewVisibleInsets);
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        public int hashCode() {
            return this.mPlatformInsets.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Impl29 extends Impl28 {
        private Insets mSystemGestureInsets = null;
        private Insets mMandatorySystemGestureInsets = null;
        private Insets mTappableElementInsets = null;

        @Override // androidx.core.view.WindowInsetsCompat.Impl21, androidx.core.view.WindowInsetsCompat.Impl
        public void setStableInsets(Insets stableInsets) {
        }

        Impl29(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl29(WindowInsetsCompat host, Impl29 other) {
            super(host, other);
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        Insets getSystemGestureInsets() {
            if (this.mSystemGestureInsets == null) {
                this.mSystemGestureInsets = Insets.toCompatInsets(this.mPlatformInsets.getSystemGestureInsets());
            }
            return this.mSystemGestureInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        Insets getMandatorySystemGestureInsets() {
            if (this.mMandatorySystemGestureInsets == null) {
                this.mMandatorySystemGestureInsets = Insets.toCompatInsets(this.mPlatformInsets.getMandatorySystemGestureInsets());
            }
            return this.mMandatorySystemGestureInsets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl
        Insets getTappableElementInsets() {
            if (this.mTappableElementInsets == null) {
                this.mTappableElementInsets = Insets.toCompatInsets(this.mPlatformInsets.getTappableElementInsets());
            }
            return this.mTappableElementInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Impl30 extends Impl29 {
        static final WindowInsetsCompat CONSUMED = WindowInsetsCompat.toWindowInsetsCompat(WindowInsets.CONSUMED);

        @Override // androidx.core.view.WindowInsetsCompat.Impl20, androidx.core.view.WindowInsetsCompat.Impl
        final void copyRootViewBounds(View rootView) {
        }

        Impl30(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl30(WindowInsetsCompat host, Impl30 other) {
            super(host, other);
        }

        @Override // androidx.core.view.WindowInsetsCompat.Impl20, androidx.core.view.WindowInsetsCompat.Impl
        public Insets getInsets(int typeMask) {
            return Insets.toCompatInsets(this.mPlatformInsets.getInsets(TypeImpl30.toPlatformType(typeMask)));
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final BuilderImpl mImpl;

        public Builder() {
            int i = Build.VERSION.SDK_INT;
            if (i >= 30) {
                this.mImpl = new BuilderImpl30();
            } else if (i >= 29) {
                this.mImpl = new BuilderImpl29();
            } else if (i >= 20) {
                this.mImpl = new BuilderImpl20();
            } else {
                this.mImpl = new BuilderImpl();
            }
        }

        public Builder(WindowInsetsCompat insets) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 30) {
                this.mImpl = new BuilderImpl30(insets);
            } else if (i >= 29) {
                this.mImpl = new BuilderImpl29(insets);
            } else if (i >= 20) {
                this.mImpl = new BuilderImpl20(insets);
            } else {
                this.mImpl = new BuilderImpl(insets);
            }
        }

        @Deprecated
        public Builder setSystemWindowInsets(Insets insets) {
            this.mImpl.setSystemWindowInsets(insets);
            return this;
        }

        @Deprecated
        public Builder setStableInsets(Insets insets) {
            this.mImpl.setStableInsets(insets);
            return this;
        }

        public WindowInsetsCompat build() {
            return this.mImpl.build();
        }
    }

    /* loaded from: classes.dex */
    public static class BuilderImpl {
        private final WindowInsetsCompat mInsets;
        Insets[] mInsetsTypeMask;

        void setMandatorySystemGestureInsets(Insets insets) {
        }

        void setStableInsets(Insets insets) {
        }

        void setSystemGestureInsets(Insets insets) {
        }

        void setSystemWindowInsets(Insets insets) {
        }

        void setTappableElementInsets(Insets insets) {
        }

        BuilderImpl() {
            this(new WindowInsetsCompat((WindowInsetsCompat) null));
        }

        BuilderImpl(WindowInsetsCompat insets) {
            this.mInsets = insets;
        }

        protected final void applyInsetTypes() {
            Insets[] insetsArr = this.mInsetsTypeMask;
            if (insetsArr != null) {
                Insets insets = insetsArr[Type.indexOf(1)];
                Insets insets2 = this.mInsetsTypeMask[Type.indexOf(2)];
                if (insets2 == null) {
                    insets2 = this.mInsets.getInsets(2);
                }
                if (insets == null) {
                    insets = this.mInsets.getInsets(1);
                }
                setSystemWindowInsets(Insets.max(insets, insets2));
                Insets insets3 = this.mInsetsTypeMask[Type.indexOf(16)];
                if (insets3 != null) {
                    setSystemGestureInsets(insets3);
                }
                Insets insets4 = this.mInsetsTypeMask[Type.indexOf(32)];
                if (insets4 != null) {
                    setMandatorySystemGestureInsets(insets4);
                }
                Insets insets5 = this.mInsetsTypeMask[Type.indexOf(64)];
                if (insets5 == null) {
                    return;
                }
                setTappableElementInsets(insets5);
            }
        }

        WindowInsetsCompat build() {
            applyInsetTypes();
            return this.mInsets;
        }
    }

    void setOverriddenInsets(Insets[] insetsTypeMask) {
        this.mImpl.setOverriddenInsets(insetsTypeMask);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BuilderImpl20 extends BuilderImpl {
        private static Constructor<WindowInsets> sConstructor = null;
        private static boolean sConstructorFetched = false;
        private static Field sConsumedField = null;
        private static boolean sConsumedFieldFetched = false;
        private WindowInsets mPlatformInsets;
        private Insets mStableInsets;

        BuilderImpl20() {
            this.mPlatformInsets = createWindowInsetsInstance();
        }

        BuilderImpl20(WindowInsetsCompat insets) {
            super(insets);
            this.mPlatformInsets = insets.toWindowInsets();
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setSystemWindowInsets(Insets insets) {
            WindowInsets windowInsets = this.mPlatformInsets;
            if (windowInsets != null) {
                this.mPlatformInsets = windowInsets.replaceSystemWindowInsets(insets.left, insets.top, insets.right, insets.bottom);
            }
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setStableInsets(Insets insets) {
            this.mStableInsets = insets;
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        WindowInsetsCompat build() {
            applyInsetTypes();
            WindowInsetsCompat windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets);
            windowInsetsCompat.setOverriddenInsets(this.mInsetsTypeMask);
            windowInsetsCompat.setStableInsets(this.mStableInsets);
            return windowInsetsCompat;
        }

        private static WindowInsets createWindowInsetsInstance() {
            if (!sConsumedFieldFetched) {
                try {
                    sConsumedField = WindowInsets.class.getDeclaredField("CONSUMED");
                } catch (ReflectiveOperationException e) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets.CONSUMED field", e);
                }
                sConsumedFieldFetched = true;
            }
            Field field = sConsumedField;
            if (field != null) {
                try {
                    WindowInsets windowInsets = (WindowInsets) field.get(null);
                    if (windowInsets != null) {
                        return new WindowInsets(windowInsets);
                    }
                } catch (ReflectiveOperationException e2) {
                    Log.i("WindowInsetsCompat", "Could not get value from WindowInsets.CONSUMED field", e2);
                }
            }
            if (!sConstructorFetched) {
                try {
                    sConstructor = WindowInsets.class.getConstructor(Rect.class);
                } catch (ReflectiveOperationException e3) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets(Rect) constructor", e3);
                }
                sConstructorFetched = true;
            }
            Constructor<WindowInsets> constructor = sConstructor;
            if (constructor != null) {
                try {
                    return constructor.newInstance(new Rect());
                } catch (ReflectiveOperationException e4) {
                    Log.i("WindowInsetsCompat", "Could not invoke WindowInsets(Rect) constructor", e4);
                }
            }
            return null;
        }
    }

    void setStableInsets(Insets stableInsets) {
        this.mImpl.setStableInsets(stableInsets);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BuilderImpl29 extends BuilderImpl {
        final WindowInsets.Builder mPlatBuilder;

        BuilderImpl29() {
            this.mPlatBuilder = new WindowInsets.Builder();
        }

        BuilderImpl29(WindowInsetsCompat insets) {
            super(insets);
            WindowInsets.Builder builder;
            WindowInsets windowInsets = insets.toWindowInsets();
            if (windowInsets != null) {
                builder = new WindowInsets.Builder(windowInsets);
            } else {
                builder = new WindowInsets.Builder();
            }
            this.mPlatBuilder = builder;
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setSystemWindowInsets(Insets insets) {
            this.mPlatBuilder.setSystemWindowInsets(insets.toPlatformInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setSystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setSystemGestureInsets(insets.toPlatformInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setMandatorySystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setMandatorySystemGestureInsets(insets.toPlatformInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setTappableElementInsets(Insets insets) {
            this.mPlatBuilder.setTappableElementInsets(insets.toPlatformInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        void setStableInsets(Insets insets) {
            this.mPlatBuilder.setStableInsets(insets.toPlatformInsets());
        }

        @Override // androidx.core.view.WindowInsetsCompat.BuilderImpl
        WindowInsetsCompat build() {
            applyInsetTypes();
            WindowInsetsCompat windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(this.mPlatBuilder.build());
            windowInsetsCompat.setOverriddenInsets(this.mInsetsTypeMask);
            return windowInsetsCompat;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BuilderImpl30 extends BuilderImpl29 {
        BuilderImpl30() {
        }

        BuilderImpl30(WindowInsetsCompat insets) {
            super(insets);
        }
    }

    /* loaded from: classes.dex */
    public static final class Type {
        public static int ime() {
            return 8;
        }

        static int indexOf(int type) {
            if (type != 1) {
                if (type == 2) {
                    return 1;
                }
                if (type == 4) {
                    return 2;
                }
                if (type == 8) {
                    return 3;
                }
                if (type == 16) {
                    return 4;
                }
                if (type == 32) {
                    return 5;
                }
                if (type == 64) {
                    return 6;
                }
                if (type == 128) {
                    return 7;
                }
                if (type == 256) {
                    return 8;
                }
                throw new IllegalArgumentException("type needs to be >= FIRST and <= LAST, type=" + type);
            }
            return 0;
        }
    }

    /* loaded from: classes.dex */
    private static final class TypeImpl30 {
        static int toPlatformType(final int typeMask) {
            int i;
            int i2 = 0;
            for (int i3 = 1; i3 <= 256; i3 <<= 1) {
                if ((typeMask & i3) != 0) {
                    if (i3 == 1) {
                        i = WindowInsets.Type.statusBars();
                    } else if (i3 == 2) {
                        i = WindowInsets.Type.navigationBars();
                    } else if (i3 == 4) {
                        i = WindowInsets.Type.captionBar();
                    } else if (i3 == 8) {
                        i = WindowInsets.Type.ime();
                    } else if (i3 == 16) {
                        i = WindowInsets.Type.systemGestures();
                    } else if (i3 == 32) {
                        i = WindowInsets.Type.mandatorySystemGestures();
                    } else if (i3 == 64) {
                        i = WindowInsets.Type.tappableElement();
                    } else if (i3 == 128) {
                        i = WindowInsets.Type.displayCutout();
                    }
                    i2 |= i;
                }
            }
            return i2;
        }
    }

    public void setRootWindowInsets(WindowInsetsCompat rootWindowInsets) {
        this.mImpl.setRootWindowInsets(rootWindowInsets);
    }

    void setRootViewData(Insets visibleInsets) {
        this.mImpl.setRootViewData(visibleInsets);
    }

    public void copyRootViewBounds(View rootView) {
        this.mImpl.copyRootViewBounds(rootView);
    }

    /* loaded from: classes.dex */
    public static class Api21ReflectionHolder {
        private static Field sContentInsets;
        private static boolean sReflectionSucceeded;
        private static Field sStableInsets;
        private static Field sViewAttachInfoField;

        static {
            try {
                Field declaredField = View.class.getDeclaredField("mAttachInfo");
                sViewAttachInfoField = declaredField;
                declaredField.setAccessible(true);
                Class<?> cls = Class.forName("android.view.View$AttachInfo");
                Field declaredField2 = cls.getDeclaredField("mStableInsets");
                sStableInsets = declaredField2;
                declaredField2.setAccessible(true);
                Field declaredField3 = cls.getDeclaredField("mContentInsets");
                sContentInsets = declaredField3;
                declaredField3.setAccessible(true);
                sReflectionSucceeded = true;
            } catch (ReflectiveOperationException e) {
                Log.w("WindowInsetsCompat", "Failed to get visible insets from AttachInfo " + e.getMessage(), e);
            }
        }

        public static WindowInsetsCompat getRootWindowInsets(View v) {
            if (sReflectionSucceeded && v.isAttachedToWindow()) {
                try {
                    Object obj = sViewAttachInfoField.get(v.getRootView());
                    if (obj != null) {
                        Rect rect = (Rect) sStableInsets.get(obj);
                        Rect rect2 = (Rect) sContentInsets.get(obj);
                        if (rect != null && rect2 != null) {
                            WindowInsetsCompat build = new Builder().setStableInsets(Insets.of(rect)).setSystemWindowInsets(Insets.of(rect2)).build();
                            build.setRootWindowInsets(build);
                            build.copyRootViewBounds(v.getRootView());
                            return build;
                        }
                    }
                } catch (IllegalAccessException e) {
                    Log.w("WindowInsetsCompat", "Failed to get insets from AttachInfo. " + e.getMessage(), e);
                }
            }
            return null;
        }
    }
}
