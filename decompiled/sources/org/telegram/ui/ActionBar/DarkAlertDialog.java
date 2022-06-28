package org.telegram.ui.ActionBar;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;
/* loaded from: classes4.dex */
public class DarkAlertDialog extends AlertDialog {
    public DarkAlertDialog(Context context, int progressStyle) {
        super(context, progressStyle);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // org.telegram.ui.ActionBar.AlertDialog
    public int getThemedColor(String key) {
        char c;
        switch (key.hashCode()) {
            case -1849805674:
                if (key.equals(Theme.key_dialogBackground)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -451706526:
                if (key.equals(Theme.key_dialogScrollGlow)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -93324646:
                if (key.equals(Theme.key_dialogButton)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1828201066:
                if (key.equals(Theme.key_dialogTextBlack)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return -14277082;
            case 1:
            case 2:
            case 3:
                return -1;
            default:
                return super.getThemedColor(key);
        }
    }

    /* loaded from: classes4.dex */
    public static class Builder extends AlertDialog.Builder {
        public Builder(Context context) {
            super(new DarkAlertDialog(context, 0));
        }

        public Builder(Context context, int progressViewStyle) {
            super(new DarkAlertDialog(context, progressViewStyle));
        }
    }
}
