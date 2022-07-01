package androidx.sharetarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.util.Log;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.sharetarget.ShareTargetCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ChooserTargetServiceCompat extends ChooserTargetService {
    @Override // android.service.chooser.ChooserTargetService
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        Context applicationContext = getApplicationContext();
        ArrayList<ShareTargetCompat> shareTargets = ShareTargetXmlParser.getShareTargets(applicationContext);
        ArrayList arrayList = new ArrayList();
        for (ShareTargetCompat shareTargetCompat : shareTargets) {
            if (shareTargetCompat.mTargetClass.equals(targetActivityName.getClassName())) {
                ShareTargetCompat.TargetData[] targetDataArr = shareTargetCompat.mTargetData;
                int length = targetDataArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (matchedFilter.hasDataType(targetDataArr[i].mMimeType)) {
                        arrayList.add(shareTargetCompat);
                        break;
                    } else {
                        i++;
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            return Collections.emptyList();
        }
        ShortcutInfoCompatSaverImpl shortcutInfoCompatSaverImpl = ShortcutInfoCompatSaverImpl.getInstance(applicationContext);
        try {
            List<ShortcutInfoCompat> shortcuts = shortcutInfoCompatSaverImpl.getShortcuts();
            if (shortcuts == null || shortcuts.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList arrayList2 = new ArrayList();
            for (ShortcutInfoCompat shortcutInfoCompat : shortcuts) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        ShareTargetCompat shareTargetCompat2 = (ShareTargetCompat) it.next();
                        if (shortcutInfoCompat.getCategories().containsAll(Arrays.asList(shareTargetCompat2.mCategories))) {
                            arrayList2.add(new ShortcutHolder(shortcutInfoCompat, new ComponentName(applicationContext.getPackageName(), shareTargetCompat2.mTargetClass)));
                            break;
                        }
                    }
                }
            }
            return convertShortcutsToChooserTargets(shortcutInfoCompatSaverImpl, arrayList2);
        } catch (Exception e) {
            Log.e("ChooserServiceCompat", "Failed to retrieve shortcuts: ", e);
            return Collections.emptyList();
        }
    }

    static List<ChooserTarget> convertShortcutsToChooserTargets(ShortcutInfoCompatSaverImpl shortcutSaver, List<ShortcutHolder> matchedShortcuts) {
        IconCompat iconCompat;
        if (matchedShortcuts.isEmpty()) {
            return new ArrayList();
        }
        Collections.sort(matchedShortcuts);
        ArrayList arrayList = new ArrayList();
        float f = 1.0f;
        int rank = matchedShortcuts.get(0).getShortcut().getRank();
        for (ShortcutHolder shortcutHolder : matchedShortcuts) {
            ShortcutInfoCompat shortcut = shortcutHolder.getShortcut();
            Icon icon = null;
            try {
                iconCompat = shortcutSaver.getShortcutIcon(shortcut.getId());
            } catch (Exception e) {
                Log.e("ChooserServiceCompat", "Failed to retrieve shortcut icon: ", e);
                iconCompat = null;
            }
            Bundle bundle = new Bundle();
            bundle.putString("android.intent.extra.shortcut.ID", shortcut.getId());
            if (rank != shortcut.getRank()) {
                f -= 0.01f;
                rank = shortcut.getRank();
            }
            CharSequence shortLabel = shortcut.getShortLabel();
            if (iconCompat != null) {
                icon = iconCompat.toIcon();
            }
            arrayList.add(new ChooserTarget(shortLabel, icon, f, shortcutHolder.getTargetClass(), bundle));
        }
        return arrayList;
    }

    /* loaded from: classes.dex */
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
