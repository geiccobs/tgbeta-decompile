package org.telegram.messenger.ringtone;

import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class RingtoneUploader implements NotificationCenter.NotificationCenterDelegate {
    private boolean canceled;
    private int currentAccount;
    public final String filePath;

    public RingtoneUploader(String filePath, int currentAccount) {
        this.currentAccount = currentAccount;
        this.filePath = filePath;
        subscribe();
        FileLoader.getInstance(currentAccount).uploadFile(filePath, false, true, ConnectionsManager.FileTypeAudio);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileUploaded) {
            String location = (String) args[0];
            if (!this.canceled && location.equals(this.filePath)) {
                TLRPC.InputFile file = (TLRPC.InputFile) args[1];
                TLRPC.TL_account_uploadRingtone req = new TLRPC.TL_account_uploadRingtone();
                req.file = file;
                req.file_name = file.name;
                req.mime_type = FileLoader.getFileExtension(new File(file.name));
                if ("ogg".equals(req.mime_type)) {
                    req.mime_type = "audio/ogg";
                } else {
                    req.mime_type = MimeTypes.AUDIO_MPEG;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ringtone.RingtoneUploader$$ExternalSyntheticLambda2
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        RingtoneUploader.this.m1263x23b86c27(tLObject, tL_error);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$1$org-telegram-messenger-ringtone-RingtoneUploader */
    public /* synthetic */ void m1263x23b86c27(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneUploader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RingtoneUploader.this.m1262x320ec608(response, error);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$0$org-telegram-messenger-ringtone-RingtoneUploader */
    public /* synthetic */ void m1262x320ec608(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            onComplete((TLRPC.Document) response);
        } else {
            error(error);
        }
        unsubscribe();
    }

    private void subscribe() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
    }

    private void unsubscribe() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
    }

    private void onComplete(TLRPC.Document document) {
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, document, false);
    }

    public void cancel() {
        this.canceled = true;
        unsubscribe();
        FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.filePath, false);
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, null, true);
    }

    public void error(final TLRPC.TL_error error) {
        unsubscribe();
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, null, true);
        if (error != null) {
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneUploader$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RingtoneUploader.this.m1264lambda$error$2$orgtelegrammessengerringtoneRingtoneUploader(error);
                }
            });
        }
    }

    /* renamed from: lambda$error$2$org-telegram-messenger-ringtone-RingtoneUploader */
    public /* synthetic */ void m1264lambda$error$2$orgtelegrammessengerringtoneRingtoneUploader(TLRPC.TL_error error) {
        if (error.text.equals("RINGTONE_DURATION_TOO_LONG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneDurationMax)));
        } else if (error.text.equals("RINGTONE_SIZE_TOO_BIG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneSizeMax / 1024)));
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("InvalidFormatError", R.string.InvalidFormatError, new Object[0]), LocaleController.formatString("ErrorRingtoneInvalidFormat", R.string.ErrorRingtoneInvalidFormat, new Object[0]));
        }
    }
}
