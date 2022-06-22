package androidx.core.content.pm;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class ShortcutInfoCompatSaver<T> {

    /* loaded from: classes.dex */
    public static class NoopImpl extends ShortcutInfoCompatSaver<Void> {
        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        /* renamed from: addShortcuts */
        public Void addShortcuts2(List<ShortcutInfoCompat> shortcuts) {
            return null;
        }

        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        public Void removeAllShortcuts() {
            return null;
        }

        @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
        /* renamed from: removeShortcuts */
        public Void removeShortcuts2(List<String> shortcutIds) {
            return null;
        }
    }

    public abstract T addShortcuts(List<ShortcutInfoCompat> shortcuts);

    public abstract T removeAllShortcuts();

    public abstract T removeShortcuts(List<String> shortcutIds);

    public List<ShortcutInfoCompat> getShortcuts() throws Exception {
        return new ArrayList();
    }
}
