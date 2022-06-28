package org.telegram.ui.Components;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class WallpaperParallaxEffect implements SensorEventListener {
    private Sensor accelerometer;
    private int bufferOffset;
    private Callback callback;
    private boolean enabled;
    private SensorManager sensorManager;
    private WindowManager wm;
    private float[] rollBuffer = new float[3];
    private float[] pitchBuffer = new float[3];

    /* loaded from: classes5.dex */
    public interface Callback {
        void onOffsetsChanged(int i, int i2, float f);
    }

    public WallpaperParallaxEffect(Context context) {
        this.wm = (WindowManager) context.getSystemService("window");
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        this.sensorManager = sensorManager;
        this.accelerometer = sensorManager.getDefaultSensor(1);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            Sensor sensor = this.accelerometer;
            if (sensor == null) {
                return;
            }
            if (enabled) {
                this.sensorManager.registerListener(this, sensor, 1);
            } else {
                this.sensorManager.unregisterListener(this);
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public float getScale(int boundsWidth, int boundsHeight) {
        int offset = AndroidUtilities.dp(16.0f);
        return Math.max((boundsWidth + (offset * 2)) / boundsWidth, (boundsHeight + (offset * 2)) / boundsHeight);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        int rotation = this.wm.getDefaultDisplay().getRotation();
        float x = event.values[0] / 9.80665f;
        float y = event.values[1] / 9.80665f;
        float z = event.values[2] / 9.80665f;
        float pitch = (float) ((Math.atan2(x, Math.sqrt((y * y) + (z * z))) / 3.141592653589793d) * 2.0d);
        float roll = (float) ((Math.atan2(y, Math.sqrt((x * x) + (z * z))) / 3.141592653589793d) * 2.0d);
        switch (rotation) {
            case 1:
                pitch = roll;
                roll = pitch;
                break;
            case 2:
                roll = -roll;
                pitch = -pitch;
                break;
            case 3:
                float tmp = -pitch;
                pitch = roll;
                roll = tmp;
                break;
        }
        float[] fArr = this.rollBuffer;
        int i = this.bufferOffset;
        fArr[i] = roll;
        this.pitchBuffer[i] = pitch;
        this.bufferOffset = (i + 1) % fArr.length;
        float pitch2 = 0.0f;
        float roll2 = 0.0f;
        int i2 = 0;
        while (true) {
            float[] fArr2 = this.rollBuffer;
            if (i2 < fArr2.length) {
                roll2 += fArr2[i2];
                pitch2 += this.pitchBuffer[i2];
                i2++;
            } else {
                int i3 = fArr2.length;
                float roll3 = roll2 / i3;
                float pitch3 = pitch2 / fArr2.length;
                if (roll3 > 1.0f) {
                    roll3 = 2.0f - roll3;
                } else if (roll3 < -1.0f) {
                    roll3 = (-2.0f) - roll3;
                }
                int offsetX = Math.round(AndroidUtilities.dpf2(16.0f) * pitch3);
                int offsetY = Math.round(AndroidUtilities.dpf2(16.0f) * roll3);
                float vx = Math.max(-1.0f, Math.min(1.0f, (-pitch3) / 0.45f));
                float vy = Math.max(-1.0f, Math.min(1.0f, (-roll3) / 0.45f));
                float len = (float) Math.sqrt((vx * vx) + (vy * vy));
                float vx2 = vx / len;
                float vy2 = vy / len;
                float angle = (float) (Math.atan2((vx2 * (-1.0f)) - (vy2 * 0.0f), (vx2 * 0.0f) + (vy2 * (-1.0f))) / 0.017453292519943295d);
                if (angle < 0.0f) {
                    angle += 360.0f;
                }
                Callback callback = this.callback;
                if (callback != null) {
                    callback.onOffsetsChanged(offsetX, offsetY, angle);
                    return;
                }
                return;
            }
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
