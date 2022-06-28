package androidx.sharetarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.util.Log;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.sharetarget.ShareTargetCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes3.dex */
public class ChooserTargetServiceCompat extends ChooserTargetService {
    static final String TAG = "ChooserServiceCompat";

    @Override // android.service.chooser.ChooserTargetService
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        Context context = getApplicationContext();
        List<ShareTargetCompat> targets = ShareTargetXmlParser.getShareTargets(context);
        List<ShareTargetCompat> matchedTargets = new ArrayList<>();
        for (ShareTargetCompat target : targets) {
            if (target.mTargetClass.equals(targetActivityName.getClassName())) {
                ShareTargetCompat.TargetData[] targetDataArr = target.mTargetData;
                int length = targetDataArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        ShareTargetCompat.TargetData data = targetDataArr[i];
                        if (!matchedFilter.hasDataType(data.mMimeType)) {
                            i++;
                        } else {
                            matchedTargets.add(target);
                            break;
                        }
                    }
                }
            }
        }
        if (matchedTargets.isEmpty()) {
            return Collections.emptyList();
        }
        ShortcutInfoCompatSaverImpl shortcutSaver = ShortcutInfoCompatSaverImpl.getInstance(context);
        try {
            List<ShortcutInfoCompat> shortcuts = shortcutSaver.getShortcuts();
            if (shortcuts == null || shortcuts.isEmpty()) {
                return Collections.emptyList();
            }
            List<ShortcutHolder> matchedShortcuts = new ArrayList<>();
            for (ShortcutInfoCompat shortcut : shortcuts) {
                Iterator<ShareTargetCompat> it = matchedTargets.iterator();
                while (true) {
                    if (it.hasNext()) {
                        ShareTargetCompat item = it.next();
                        if (shortcut.getCategories().containsAll(Arrays.asList(item.mCategories))) {
                            matchedShortcuts.add(new ShortcutHolder(shortcut, new ComponentName(context.getPackageName(), item.mTargetClass)));
                            break;
                        }
                    }
                }
            }
            return convertShortcutsToChooserTargets(shortcutSaver, matchedShortcuts);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve shortcuts: ", e);
            return Collections.emptyList();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x007e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static java.util.List<android.service.chooser.ChooserTarget> convertShortcutsToChooserTargets(androidx.sharetarget.ShortcutInfoCompatSaverImpl r17, java.util.List<androidx.sharetarget.ChooserTargetServiceCompat.ShortcutHolder> r18) {
        /*
            boolean r0 = r18.isEmpty()
            if (r0 == 0) goto Lc
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            return r0
        Lc:
            java.util.Collections.sort(r18)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = r0
            r0 = 1065353216(0x3f800000, float:1.0)
            r2 = 0
            r3 = r18
            java.lang.Object r2 = r3.get(r2)
            androidx.sharetarget.ChooserTargetServiceCompat$ShortcutHolder r2 = (androidx.sharetarget.ChooserTargetServiceCompat.ShortcutHolder) r2
            androidx.core.content.pm.ShortcutInfoCompat r2 = r2.getShortcut()
            int r2 = r2.getRank()
            java.util.Iterator r4 = r18.iterator()
            r5 = r2
            r2 = r0
        L2e:
            boolean r0 = r4.hasNext()
            if (r0 == 0) goto L94
            java.lang.Object r0 = r4.next()
            r6 = r0
            androidx.sharetarget.ChooserTargetServiceCompat$ShortcutHolder r6 = (androidx.sharetarget.ChooserTargetServiceCompat.ShortcutHolder) r6
            androidx.core.content.pm.ShortcutInfoCompat r7 = r6.getShortcut()
            java.lang.String r0 = r7.getId()     // Catch: java.lang.Exception -> L4c
            r8 = r17
            androidx.core.graphics.drawable.IconCompat r0 = r8.getShortcutIcon(r0)     // Catch: java.lang.Exception -> L4a
            goto L58
        L4a:
            r0 = move-exception
            goto L4f
        L4c:
            r0 = move-exception
            r8 = r17
        L4f:
            java.lang.String r9 = "ChooserServiceCompat"
            java.lang.String r10 = "Failed to retrieve shortcut icon: "
            android.util.Log.e(r9, r10, r0)
            r9 = 0
            r0 = r9
        L58:
            android.os.Bundle r9 = new android.os.Bundle
            r9.<init>()
            java.lang.String r10 = r7.getId()
            java.lang.String r11 = "android.intent.extra.shortcut.ID"
            r9.putString(r11, r10)
            int r10 = r7.getRank()
            if (r5 == r10) goto L74
            r10 = 1008981770(0x3c23d70a, float:0.01)
            float r2 = r2 - r10
            int r5 = r7.getRank()
        L74:
            android.service.chooser.ChooserTarget r15 = new android.service.chooser.ChooserTarget
            java.lang.CharSequence r11 = r7.getShortLabel()
            if (r0 != 0) goto L7e
            r10 = 0
            goto L82
        L7e:
            android.graphics.drawable.Icon r10 = r0.toIcon()
        L82:
            r12 = r10
            android.content.ComponentName r14 = r6.getTargetClass()
            r10 = r15
            r13 = r2
            r16 = r0
            r0 = r15
            r15 = r9
            r10.<init>(r11, r12, r13, r14, r15)
            r1.add(r0)
            goto L2e
        L94:
            r8 = r17
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.sharetarget.ChooserTargetServiceCompat.convertShortcutsToChooserTargets(androidx.sharetarget.ShortcutInfoCompatSaverImpl, java.util.List):java.util.List");
    }

    /* loaded from: classes3.dex */
    public static class ShortcutHolder implements Comparable<ShortcutHolder> {
        private final ShortcutInfoCompat mShortcut;
        private final ComponentName mTargetClass;

        ShortcutHolder(ShortcutInfoCompat shortcut, ComponentName targetClass) {
            this.mShortcut = shortcut;
            this.mTargetClass = targetClass;
        }

        ShortcutInfoCompat getShortcut() {
            return this.mShortcut;
        }

        ComponentName getTargetClass() {
            return this.mTargetClass;
        }

        public int compareTo(ShortcutHolder other) {
            return getShortcut().getRank() - other.getShortcut().getRank();
        }
    }
}
