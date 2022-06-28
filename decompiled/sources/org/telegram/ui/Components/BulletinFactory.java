package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
/* loaded from: classes5.dex */
public final class BulletinFactory {
    public static final int ICON_TYPE_NOT_FOUND = 0;
    public static final int ICON_TYPE_WARNING = 1;
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;
    private final Theme.ResourcesProvider resourcesProvider;

    public static BulletinFactory of(BaseFragment fragment) {
        return new BulletinFactory(fragment);
    }

    public static BulletinFactory of(FrameLayout containerLayout, Theme.ResourcesProvider resourcesProvider) {
        return new BulletinFactory(containerLayout, resourcesProvider);
    }

    public static boolean canShowBulletin(BaseFragment fragment) {
        return (fragment == null || fragment.getParentActivity() == null || fragment.getLayoutContainer() == null) ? false : true;
    }

    /* loaded from: classes5.dex */
    public enum FileType {
        PHOTO("PhotoSavedHint", R.string.PhotoSavedHint, Icon.SAVED_TO_GALLERY),
        PHOTOS("PhotosSavedHint", Icon.SAVED_TO_GALLERY),
        VIDEO("VideoSavedHint", R.string.VideoSavedHint, Icon.SAVED_TO_GALLERY),
        VIDEOS("VideosSavedHint", Icon.SAVED_TO_GALLERY),
        MEDIA("MediaSavedHint", Icon.SAVED_TO_GALLERY),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", R.string.PhotoSavedToDownloadsHint, Icon.SAVED_TO_DOWNLOADS),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", R.string.VideoSavedToDownloadsHint, Icon.SAVED_TO_DOWNLOADS),
        GIF("GifSavedHint", R.string.GifSavedHint, Icon.SAVED_TO_GIFS),
        GIF_TO_DOWNLOADS("GifSavedToDownloadsHint", R.string.GifSavedToDownloadsHint, Icon.SAVED_TO_DOWNLOADS),
        AUDIO("AudioSavedHint", R.string.AudioSavedHint, Icon.SAVED_TO_MUSIC),
        AUDIOS("AudiosSavedHint", Icon.SAVED_TO_MUSIC),
        UNKNOWN("FileSavedHint", R.string.FileSavedHint, Icon.SAVED_TO_DOWNLOADS),
        UNKNOWNS("FilesSavedHint", Icon.SAVED_TO_DOWNLOADS);
        
        private final Icon icon;
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        FileType(String localeKey, int localeRes, Icon icon) {
            this.localeKey = localeKey;
            this.localeRes = localeRes;
            this.icon = icon;
            this.plural = false;
        }

        FileType(String localeKey, Icon icon) {
            this.localeKey = localeKey;
            this.icon = icon;
            this.localeRes = 0;
            this.plural = true;
        }

        private String getText() {
            return getText(1);
        }

        public String getText(int amount) {
            if (this.plural) {
                return LocaleController.formatPluralString(this.localeKey, amount, new Object[0]);
            }
            return LocaleController.getString(this.localeKey, this.localeRes);
        }

        /* loaded from: classes5.dex */
        public enum Icon {
            SAVED_TO_DOWNLOADS(R.raw.ic_download, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(R.raw.ic_save_to_gallery, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(R.raw.ic_save_to_music, 2, "Box", "Arrow"),
            SAVED_TO_GIFS(R.raw.ic_save_to_gifs, 0, "gif");
            
            private final String[] layers;
            private final int paddingBottom;
            private final int resId;

            Icon(int resId, int paddingBottom, String... layers) {
                this.resId = resId;
                this.paddingBottom = paddingBottom;
                this.layers = layers;
            }
        }
    }

    private BulletinFactory(BaseFragment fragment) {
        this.fragment = fragment;
        Theme.ResourcesProvider resourcesProvider = null;
        this.containerLayout = null;
        this.resourcesProvider = fragment != null ? fragment.getResourceProvider() : resourcesProvider;
    }

    private BulletinFactory(FrameLayout containerLayout, Theme.ResourcesProvider resourcesProvider) {
        this.containerLayout = containerLayout;
        this.fragment = null;
        this.resourcesProvider = resourcesProvider;
    }

    public Bulletin createSimpleBulletin(int iconRawId, String text) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        layout.setAnimation(iconRawId, 36, 36, new String[0]);
        layout.textView.setText(text);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, 1500);
    }

    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text, CharSequence subtext) {
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        layout.setAnimation(iconRawId, 36, 36, new String[0]);
        layout.titleTextView.setText(text);
        layout.subtitleTextView.setText(subtext);
        return create(layout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, this.resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, 1, resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, filesAmount, 0, 0, resourcesProvider);
    }

    public Bulletin createReportSent(Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        layout.setAnimation(R.raw.chats_infotip, new String[0]);
        layout.textView.setText(LocaleController.getString("ReportChatSent", R.string.ReportChatSent));
        return create(layout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, int backgroundColor, int textColor) {
        return createDownloadBulletin(fileType, filesAmount, backgroundColor, textColor, null);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int filesAmount, int backgroundColor, int textColor, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout layout;
        if (backgroundColor != 0 && textColor != 0) {
            layout = new Bulletin.LottieLayout(getContext(), resourcesProvider, backgroundColor, textColor);
        } else {
            layout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        }
        layout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        layout.textView.setText(fileType.getText(filesAmount));
        if (fileType.icon.paddingBottom != 0) {
            layout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(layout, 1500);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage) {
        return createErrorBulletin(errorMessage, null);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        layout.setAnimation(R.raw.chats_infotip, new String[0]);
        layout.textView.setText(errorMessage);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, 1500);
    }

    public Bulletin createErrorBulletinSubtitle(CharSequence errorMessage, CharSequence errorDescription, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
        layout.setAnimation(R.raw.chats_infotip, new String[0]);
        layout.titleTextView.setText(errorMessage);
        layout.subtitleTextView.setText(errorDescription);
        return create(layout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyBulletin(String message) {
        return createCopyBulletin(message, null);
    }

    public Bulletin createCopyBulletin(String message, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), null);
        layout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        layout.textView.setText(message);
        return create(layout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean isPrivate, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        if (isPrivate) {
            Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
            layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            layout.titleTextView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
            layout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", R.string.LinkCopiedPrivateInfo));
            return create(layout, Bulletin.DURATION_LONG);
        }
        Bulletin.LottieLayout layout2 = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        layout2.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        layout2.textView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
        return create(layout2, 1500);
    }

    public Bulletin createCopyLinkBulletin(String text, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        layout.textView.setText(text);
        return create(layout, 1500);
    }

    private Bulletin create(Bulletin.Layout layout, int duration) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            return Bulletin.make(baseFragment, layout, duration);
        }
        return Bulletin.make(this.containerLayout, layout, duration);
    }

    private Context getContext() {
        BaseFragment baseFragment = this.fragment;
        return baseFragment != null ? baseFragment.getParentActivity() : this.containerLayout.getContext();
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment, int setting) {
        return createMuteBulletin(fragment, setting, 0, null);
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment, int setting, int timeInSeconds, Theme.ResourcesProvider resourcesProvider) {
        boolean mute;
        String text;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
        boolean muteFor = false;
        switch (setting) {
            case 0:
                text = LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatPluralString("Hours", 1, new Object[0]));
                mute = true;
                break;
            case 1:
                text = LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatPluralString("Hours", 8, new Object[0]));
                mute = true;
                break;
            case 2:
                text = LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatPluralString("Days", 2, new Object[0]));
                mute = true;
                break;
            case 3:
                text = LocaleController.getString("NotificationsMutedHint", R.string.NotificationsMutedHint);
                mute = true;
                break;
            case 4:
                text = LocaleController.getString("NotificationsUnmutedHint", R.string.NotificationsUnmutedHint);
                mute = false;
                break;
            case 5:
                text = LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatTTLString(timeInSeconds));
                mute = true;
                muteFor = true;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (muteFor) {
            layout.setAnimation(R.raw.mute_for, new String[0]);
        } else if (mute) {
            layout.setAnimation(R.raw.ic_mute, "Body Main", "Body Top", "Line", "Curve Big", "Curve Small");
        } else {
            layout.setAnimation(R.raw.ic_unmute, "BODY", "Wibe Big", "Wibe Big 3", "Wibe Small");
        }
        layout.textView.setText(text);
        return Bulletin.make(fragment, layout, 1500);
    }

    public static Bulletin createMuteBulletin(BaseFragment fragment, boolean muted, Theme.ResourcesProvider resourcesProvider) {
        return createMuteBulletin(fragment, muted ? 3 : 4, 0, resourcesProvider);
    }

    public static Bulletin createDeleteMessagesBulletin(BaseFragment fragment, int count, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
        layout.setAnimation(R.raw.ic_delete, "Envelope", "Cover", "Bucket");
        layout.textView.setText(LocaleController.formatPluralString("MessagesDeletedHint", count, new Object[0]));
        return Bulletin.make(fragment, layout, 1500);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Bulletin createUnpinAllMessagesBulletin(BaseFragment fragment, int count, boolean hide, Runnable undoAction, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.ButtonLayout buttonLayout;
        if (fragment.getParentActivity() == null) {
            if (delayedAction != null) {
                delayedAction.run();
                return null;
            }
            return null;
        }
        if (hide) {
            Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(fragment.getParentActivity(), resourcesProvider);
            layout.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            layout.titleTextView.setText(LocaleController.getString("PinnedMessagesHidden", R.string.PinnedMessagesHidden));
            layout.subtitleTextView.setText(LocaleController.getString("PinnedMessagesHiddenInfo", R.string.PinnedMessagesHiddenInfo));
            buttonLayout = layout;
        } else {
            Bulletin.LottieLayout layout2 = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
            layout2.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            layout2.textView.setText(LocaleController.formatPluralString("MessagesUnpinned", count, new Object[0]));
            buttonLayout = layout2;
        }
        buttonLayout.setButton(new Bulletin.UndoButton(fragment.getParentActivity(), true, resourcesProvider).setUndoAction(undoAction).setDelayedAction(delayedAction));
        return Bulletin.make(fragment, buttonLayout, 5000);
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment fragment, boolean video, Theme.ResourcesProvider resourcesProvider) {
        return of(fragment).createDownloadBulletin(video ? FileType.VIDEO : FileType.PHOTO, resourcesProvider);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout containerLayout, boolean video, int backgroundColor, int textColor) {
        return of(containerLayout, null).createDownloadBulletin(video ? FileType.VIDEO : FileType.PHOTO, 1, backgroundColor, textColor);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment fragment, String userFirstName) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), fragment.getResourceProvider());
        layout.setAnimation(R.raw.ic_admin, "Shield");
        layout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", R.string.UserSetAsAdminHint, userFirstName)));
        return Bulletin.make(fragment, layout, 1500);
    }

    public static Bulletin createAddedAsAdminBulletin(BaseFragment fragment, String userFirstName) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), fragment.getResourceProvider());
        layout.setAnimation(R.raw.ic_admin, "Shield");
        layout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserAddedAsAdminHint", R.string.UserAddedAsAdminHint, userFirstName)));
        return Bulletin.make(fragment, layout, 1500);
    }

    public static Bulletin createForwardedBulletin(Context context, FrameLayout containerLayout, int dialogsCount, long did, int messagesCount, int backgroundColor, int textColor) {
        CharSequence text;
        CharSequence text2;
        CharSequence text3;
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(context, null, backgroundColor, textColor);
        int hapticDelay = -1;
        if (dialogsCount > 1) {
            if (messagesCount <= 1) {
                text2 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessageToChats", R.string.FwdMessageToChats, LocaleController.formatPluralString("Chats", dialogsCount, new Object[0])));
            } else {
                text2 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessagesToChats", R.string.FwdMessagesToChats, LocaleController.formatPluralString("Chats", dialogsCount, new Object[0])));
            }
            layout.setAnimation(R.raw.forward, 30, 30, new String[0]);
            hapticDelay = 300;
            text = text2;
        } else if (did == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            if (messagesCount <= 1) {
                text = AndroidUtilities.replaceTags(LocaleController.getString("FwdMessageToSavedMessages", R.string.FwdMessageToSavedMessages));
            } else {
                text = AndroidUtilities.replaceTags(LocaleController.getString("FwdMessagesToSavedMessages", R.string.FwdMessagesToSavedMessages));
            }
            layout.setAnimation(R.raw.saved_messages, 30, 30, new String[0]);
        } else {
            if (DialogObject.isChatDialog(did)) {
                TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-did));
                if (messagesCount <= 1) {
                    text3 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessageToGroup", R.string.FwdMessageToGroup, chat.title));
                } else {
                    text3 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessagesToGroup", R.string.FwdMessagesToGroup, chat.title));
                }
            } else {
                TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(did));
                if (messagesCount <= 1) {
                    text3 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessageToUser", R.string.FwdMessageToUser, UserObject.getFirstName(user)));
                } else {
                    text3 = AndroidUtilities.replaceTags(LocaleController.formatString("FwdMessagesToUser", R.string.FwdMessagesToUser, UserObject.getFirstName(user)));
                }
            }
            layout.setAnimation(R.raw.forward, 30, 30, new String[0]);
            hapticDelay = 300;
            text = text3;
        }
        layout.textView.setText(text);
        if (hapticDelay > 0) {
            layout.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Bulletin.LottieLayout.this.performHapticFeedback(3, 2);
                }
            }, hapticDelay);
        }
        return Bulletin.make(containerLayout, layout, 1500);
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment fragment, TLRPC.User user, String chatName) {
        String name;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), fragment.getResourceProvider());
        layout.setAnimation(R.raw.ic_ban, "Hand");
        if (user.deleted) {
            name = LocaleController.formatString("HiddenName", R.string.HiddenName, new Object[0]);
        } else {
            name = user.first_name;
        }
        layout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", R.string.UserRemovedFromChatHint, name, chatName)));
        return Bulletin.make(fragment, layout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment fragment, boolean banned) {
        String text;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), fragment.getResourceProvider());
        if (banned) {
            layout.setAnimation(R.raw.ic_ban, "Hand");
            text = LocaleController.getString("UserBlocked", R.string.UserBlocked);
        } else {
            layout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            text = LocaleController.getString("UserUnblocked", R.string.UserUnblocked);
        }
        layout.textView.setText(AndroidUtilities.replaceTags(text));
        return Bulletin.make(fragment, layout, 1500);
    }

    public static Bulletin createCopyLinkBulletin(BaseFragment fragment) {
        return of(fragment).createCopyLinkBulletin();
    }

    public static Bulletin createCopyLinkBulletin(FrameLayout containerView) {
        return of(containerView, null).createCopyLinkBulletin();
    }

    public static Bulletin createPinMessageBulletin(BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(fragment, true, null, null, resourcesProvider);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment fragment, Runnable undoAction, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(fragment, false, undoAction, delayedAction, resourcesProvider);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment fragment, boolean pinned, Runnable undoAction, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
        layout.setAnimation(pinned ? R.raw.ic_pin : R.raw.ic_unpin, 28, 28, "Pin", "Line");
        layout.textView.setText(LocaleController.getString(pinned ? "MessagePinnedHint" : "MessageUnpinnedHint", pinned ? R.string.MessagePinnedHint : R.string.MessageUnpinnedHint));
        if (!pinned) {
            layout.setButton(new Bulletin.UndoButton(fragment.getParentActivity(), true, resourcesProvider).setUndoAction(undoAction).setDelayedAction(delayedAction));
        }
        return Bulletin.make(fragment, layout, pinned ? 1500 : 5000);
    }

    public static Bulletin createSoundEnabledBulletin(BaseFragment fragment, int setting, Theme.ResourcesProvider resourcesProvider) {
        boolean soundOn;
        String text;
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
        switch (setting) {
            case 0:
                text = LocaleController.getString("SoundOnHint", R.string.SoundOnHint);
                soundOn = true;
                break;
            case 1:
                text = LocaleController.getString("SoundOffHint", R.string.SoundOffHint);
                soundOn = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (soundOn) {
            layout.setAnimation(R.raw.sound_on, new String[0]);
        } else {
            layout.setAnimation(R.raw.sound_off, new String[0]);
        }
        layout.textView.setText(text);
        return Bulletin.make(fragment, layout, 1500);
    }
}
