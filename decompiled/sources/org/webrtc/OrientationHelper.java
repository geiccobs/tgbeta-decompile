package org.webrtc;

import android.view.OrientationEventListener;
import org.telegram.messenger.ApplicationLoader;
/* loaded from: classes5.dex */
public class OrientationHelper {
    private static final int ORIENTATION_HYSTERESIS = 5;
    public static volatile int cameraOrientation;
    public static volatile int cameraRotation;
    private OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) { // from class: org.webrtc.OrientationHelper.1
        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int orientation) {
            if (OrientationHelper.this.orientationEventListener == null || orientation == -1) {
                return;
            }
            OrientationHelper orientationHelper = OrientationHelper.this;
            int newOrietation = orientationHelper.roundOrientation(orientation, orientationHelper.rotation);
            if (newOrietation != OrientationHelper.this.rotation) {
                OrientationHelper orientationHelper2 = OrientationHelper.this;
                orientationHelper2.onOrientationUpdate(orientationHelper2.rotation = newOrietation);
            }
        }
    };
    private int rotation;

    public int roundOrientation(int orientation, int orientationHistory) {
        int dist;
        if (orientationHistory == -1) {
            dist = 1;
        } else {
            int dist2 = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist2, 360 - dist2) >= 50 ? 1 : 0;
        }
        if (dist != 0) {
            return (((orientation + 45) / 90) * 90) % 360;
        }
        return orientationHistory;
    }

    protected void onOrientationUpdate(int orientation) {
    }

    public void start() {
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    public void stop() {
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    public int getOrientation() {
        return this.rotation;
    }
}
