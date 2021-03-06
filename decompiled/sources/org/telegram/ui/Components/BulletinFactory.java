package org.telegram.ui.Components;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public final class BulletinFactory {
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;
    private final Theme.ResourcesProvider resourcesProvider;

    public static BulletinFactory of(BaseFragment baseFragment) {
        return new BulletinFactory(baseFragment);
    }

    public static BulletinFactory of(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        return new BulletinFactory(frameLayout, resourcesProvider);
    }

    public static boolean canShowBulletin(BaseFragment baseFragment) {
        return (baseFragment == null || baseFragment.getParentActivity() == null || baseFragment.getLayoutContainer() == null) ? false : true;
    }

    /* JADX WARN: Init of enum AUDIO can be incorrect */
    /* JADX WARN: Init of enum AUDIOS can be incorrect */
    /* JADX WARN: Init of enum GIF_TO_DOWNLOADS can be incorrect */
    /* JADX WARN: Init of enum MEDIA can be incorrect */
    /* JADX WARN: Init of enum PHOTO can be incorrect */
    /* JADX WARN: Init of enum PHOTOS can be incorrect */
    /* JADX WARN: Init of enum PHOTO_TO_DOWNLOADS can be incorrect */
    /* JADX WARN: Init of enum UNKNOWN can be incorrect */
    /* JADX WARN: Init of enum UNKNOWNS can be incorrect */
    /* JADX WARN: Init of enum VIDEO can be incorrect */
    /* JADX WARN: Init of enum VIDEOS can be incorrect */
    /* JADX WARN: Init of enum VIDEO_TO_DOWNLOADS can be incorrect */
    /* loaded from: classes3.dex */
    public enum FileType {
        PHOTO("PhotoSavedHint", R.string.PhotoSavedHint, r7),
        PHOTOS("PhotosSavedHint", r7),
        VIDEO("VideoSavedHint", R.string.VideoSavedHint, r7),
        VIDEOS("VideosSavedHint", r7),
        MEDIA("MediaSavedHint", r7),
        PHOTO_TO_DOWNLOADS("PhotoSavedToDownloadsHint", R.string.PhotoSavedToDownloadsHint, r5),
        VIDEO_TO_DOWNLOADS("VideoSavedToDownloadsHint", R.string.VideoSavedToDownloadsHint, r5),
        GIF("GifSavedHint", R.string.GifSavedHint, Icon.SAVED_TO_GIFS),
        GIF_TO_DOWNLOADS("GifSavedToDownloadsHint", R.string.GifSavedToDownloadsHint, r5),
        AUDIO("AudioSavedHint", R.string.AudioSavedHint, r11),
        AUDIOS("AudiosSavedHint", r11),
        UNKNOWN("FileSavedHint", R.string.FileSavedHint, r5),
        UNKNOWNS("FilesSavedHint", r5);
        
        private final Icon icon;
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        static {
            Icon icon = Icon.SAVED_TO_GALLERY;
            Icon icon2 = Icon.SAVED_TO_DOWNLOADS;
            Icon icon3 = Icon.SAVED_TO_MUSIC;
        }

        FileType(String str, int i, Icon icon) {
            this.localeKey = str;
            this.localeRes = i;
            this.icon = icon;
            this.plural = false;
        }

        FileType(String str, Icon icon) {
            this.localeKey = str;
            this.icon = icon;
            this.localeRes = 0;
            this.plural = true;
        }

        public String getText(int i) {
            if (this.plural) {
                return LocaleController.formatPluralString(this.localeKey, i, new Object[0]);
            }
            return LocaleController.getString(this.localeKey, this.localeRes);
        }

        /* loaded from: classes3.dex */
        public enum Icon {
            SAVED_TO_DOWNLOADS(R.raw.ic_download, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(R.raw.ic_save_to_gallery, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(R.raw.ic_save_to_music, 2, "Box", "Arrow"),
            SAVED_TO_GIFS(R.raw.ic_save_to_gifs, 0, "gif");
            
            private final String[] layers;
            private final int paddingBottom;
            private final int resId;

            Icon(int i, int i2, String... strArr) {
                this.resId = i;
                this.paddingBottom = i2;
                this.layers = strArr;
            }
        }
    }

    private BulletinFactory(BaseFragment baseFragment) {
        this.fragment = baseFragment;
        Theme.ResourcesProvider resourcesProvider = null;
        this.containerLayout = null;
        this.resourcesProvider = baseFragment != null ? baseFragment.getResourceProvider() : resourcesProvider;
    }

    private BulletinFactory(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        this.containerLayout = frameLayout;
        this.fragment = null;
        this.resourcesProvider = resourcesProvider;
    }

    public Bulletin createSimpleBulletin(int i, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        lottieLayout.textView.setText(str);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createEmojiBulletin(TLRPC$Document tLRPC$Document, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(tLRPC$Document, 36, 36, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(lottieLayout, 2750);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, this.resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, 1, resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, i, 0, 0, resourcesProvider);
    }

    public Bulletin createReportSent(Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString("ReportChatSent", R.string.ReportChatSent));
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3) {
        return createDownloadBulletin(fileType, i, i2, i3, null);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout;
        if (i2 != 0 && i3 != 0) {
            lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider, i2, i3);
        } else {
            lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        }
        lottieLayout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        lottieLayout.textView.setText(fileType.getText(i));
        if (fileType.icon.paddingBottom != 0) {
            lottieLayout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(lottieLayout, 1500);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence) {
        return createErrorBulletin(charSequence, null);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createRestrictVoiceMessagesPremiumBulletin() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.voip_muted, new String[0]);
        String string = LocaleController.getString((int) R.string.PrivacyVoiceMessagesPremiumOnly);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
        int indexOf = string.indexOf(42);
        int lastIndexOf = string.lastIndexOf(42);
        spannableStringBuilder.replace(indexOf, lastIndexOf + 1, (CharSequence) string.substring(indexOf + 1, lastIndexOf));
        spannableStringBuilder.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Components.BulletinFactory.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                BulletinFactory.this.fragment.presentFragment(new PremiumPreviewFragment("settings"));
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }
        }, indexOf - 1, lastIndexOf - 1, 33);
        lottieLayout.textView.setText(spannableStringBuilder);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 2750);
    }

    public Bulletin createErrorBulletinSubtitle(CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
        twoLineLottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyBulletin(String str) {
        return createCopyBulletin(str, null);
    }

    public Bulletin createCopyBulletin(String str, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
            twoLineLottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString("LinkCopiedPrivateInfo", R.string.LinkCopiedPrivateInfo));
            return create(twoLineLottieLayout, 2750);
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(LocaleController.getString("LinkCopied", R.string.LinkCopied));
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(String str, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    private Bulletin create(Bulletin.Layout layout, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            return Bulletin.make(baseFragment, layout, i);
        }
        return Bulletin.make(this.containerLayout, layout, i);
    }

    private Context getContext() {
        BaseFragment baseFragment = this.fragment;
        return baseFragment != null ? baseFragment.getParentActivity() : this.containerLayout.getContext();
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, int i) {
        return createMuteBulletin(baseFragment, i, 0, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0080  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0089  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r10, int r11, int r12, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r0 = new org.telegram.ui.Components.Bulletin$LottieLayout
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>(r1, r13)
            java.lang.String r13 = "Hours"
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2131627098(0x7f0e0c5a, float:1.888145E38)
            java.lang.String r5 = "NotificationsMutedForHint"
            r6 = 2
            r7 = 0
            r8 = 1
            if (r11 == 0) goto L6e
            if (r11 == r8) goto L5d
            if (r11 == r6) goto L4c
            if (r11 == r3) goto L42
            if (r11 == r2) goto L36
            if (r11 != r1) goto L30
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatTTLString(r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            r12 = 1
            goto L7d
        L30:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            r10.<init>()
            throw r10
        L36:
            r11 = 2131627123(0x7f0e0c73, float:1.8881502E38)
            java.lang.String r12 = "NotificationsUnmutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r12 = 0
            r13 = 0
            goto L7e
        L42:
            r11 = 2131627099(0x7f0e0c5b, float:1.8881453E38)
            java.lang.String r12 = "NotificationsMutedHint"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L7c
        L4c:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r13 = "Days"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r6, r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            goto L7c
        L5d:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r12 = 8
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r12, r9)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
            goto L7c
        L6e:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r8, r12)
            r11[r7] = r12
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r11)
        L7c:
            r12 = 0
        L7d:
            r13 = 1
        L7e:
            if (r12 == 0) goto L89
            r12 = 2131558498(0x7f0d0062, float:1.8742314E38)
            java.lang.String[] r13 = new java.lang.String[r7]
            r0.setAnimation(r12, r13)
            goto Lc0
        L89:
            if (r13 == 0) goto La8
            r12 = 2131558466(0x7f0d0042, float:1.8742249E38)
            java.lang.String[] r13 = new java.lang.String[r1]
            java.lang.String r1 = "Body Main"
            r13[r7] = r1
            java.lang.String r1 = "Body Top"
            r13[r8] = r1
            java.lang.String r1 = "Line"
            r13[r6] = r1
            java.lang.String r1 = "Curve Big"
            r13[r3] = r1
            java.lang.String r1 = "Curve Small"
            r13[r2] = r1
            r0.setAnimation(r12, r13)
            goto Lc0
        La8:
            r12 = 2131558472(0x7f0d0048, float:1.874226E38)
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r1 = "BODY"
            r13[r7] = r1
            java.lang.String r1 = "Wibe Big"
            r13[r8] = r1
            java.lang.String r1 = "Wibe Big 3"
            r13[r6] = r1
            java.lang.String r1 = "Wibe Small"
            r13[r3] = r1
            r0.setAnimation(r12, r13)
        Lc0:
            android.widget.TextView r12 = r0.textView
            r12.setText(r11)
            r11 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r10 = org.telegram.ui.Components.Bulletin.make(r10, r0, r11)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment, int, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return createMuteBulletin(baseFragment, z ? 3 : 4, 0, resourcesProvider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Bulletin createUnpinAllMessagesBulletin(BaseFragment baseFragment, int i, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout;
        if (baseFragment.getParentActivity() == null) {
            if (runnable2 == null) {
                return null;
            }
            runnable2.run();
            return null;
        }
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(baseFragment.getParentActivity(), resourcesProvider);
            twoLineLottieLayout.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString("PinnedMessagesHidden", R.string.PinnedMessagesHidden));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString("PinnedMessagesHiddenInfo", R.string.PinnedMessagesHiddenInfo));
            lottieLayout = twoLineLottieLayout;
        } else {
            Bulletin.LottieLayout lottieLayout2 = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
            lottieLayout2.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            lottieLayout2.textView.setText(LocaleController.formatPluralString("MessagesUnpinned", i, new Object[0]));
            lottieLayout = lottieLayout2;
        }
        lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true, resourcesProvider).setUndoAction(runnable).setDelayedAction(runnable2));
        return Bulletin.make(baseFragment, lottieLayout, 5000);
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return of(baseFragment).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, resourcesProvider);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, int i, int i2) {
        return of(frameLayout, null).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, 1, i, i2);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", R.string.UserSetAsAdminHint, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createAddedAsAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserAddedAsAdminHint", R.string.UserAddedAsAdminHint, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x00ae  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.telegram.ui.Components.Bulletin createInviteSentBulletin(android.content.Context r4, android.widget.FrameLayout r5, int r6, long r7, int r9, int r10, int r11) {
        /*
            org.telegram.ui.Components.Bulletin$LottieLayout r9 = new org.telegram.ui.Components.Bulletin$LottieLayout
            r0 = 0
            r9.<init>(r4, r0, r10, r11)
            r4 = 300(0x12c, float:4.2E-43)
            r10 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r11 = 1
            r0 = 30
            r1 = 0
            if (r6 > r11) goto L86
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            long r2 = r6.clientUserId
            int r6 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r6 != 0) goto L34
            r4 = 2131626291(0x7f0e0933, float:1.8879814E38)
            java.lang.String r6 = "InvLinkToSavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            r6 = 2131558542(0x7f0d008e, float:1.8742403E38)
            java.lang.String[] r7 = new java.lang.String[r1]
            r9.setAnimation(r6, r0, r0, r7)
            r6 = -1
            goto La7
        L34:
            boolean r6 = org.telegram.messenger.DialogObject.isChatDialog(r7)
            if (r6 == 0) goto L5d
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            long r7 = -r7
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            r7 = 2131626290(0x7f0e0932, float:1.8879812E38)
            java.lang.Object[] r8 = new java.lang.Object[r11]
            java.lang.String r6 = r6.title
            r8[r1] = r6
            java.lang.String r6 = "InvLinkToGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            goto L80
        L5d:
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            r7 = 2131626292(0x7f0e0934, float:1.8879816E38)
            java.lang.Object[] r8 = new java.lang.Object[r11]
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r8[r1] = r6
            java.lang.String r6 = "InvLinkToUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
        L80:
            java.lang.String[] r7 = new java.lang.String[r1]
            r9.setAnimation(r10, r0, r0, r7)
            goto La4
        L86:
            r7 = 2131626289(0x7f0e0931, float:1.887981E38)
            java.lang.Object[] r8 = new java.lang.Object[r11]
            java.lang.Object[] r11 = new java.lang.Object[r1]
            java.lang.String r2 = "Chats"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r2, r6, r11)
            r8[r1] = r6
            java.lang.String r6 = "InvLinkToChats"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            java.lang.String[] r7 = new java.lang.String[r1]
            r9.setAnimation(r10, r0, r0, r7)
        La4:
            r4 = r6
            r6 = 300(0x12c, float:4.2E-43)
        La7:
            android.widget.TextView r7 = r9.textView
            r7.setText(r4)
            if (r6 <= 0) goto Lb7
            org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.BulletinFactory$$ExternalSyntheticLambda1
            r4.<init>()
            long r6 = (long) r6
            r9.postDelayed(r4, r6)
        Lb7:
            r4 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r4 = org.telegram.ui.Components.Bulletin.make(r5, r9, r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createInviteSentBulletin(android.content.Context, android.widget.FrameLayout, int, long, int, int, int):org.telegram.ui.Components.Bulletin");
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0107  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.telegram.ui.Components.Bulletin createForwardedBulletin(android.content.Context r5, android.widget.FrameLayout r6, int r7, long r8, int r10, int r11, int r12) {
        /*
            Method dump skipped, instructions count: 279
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createForwardedBulletin(android.content.Context, android.widget.FrameLayout, int, long, int, int, int):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        String str2;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
        if (tLRPC$User.deleted) {
            str2 = LocaleController.formatString("HiddenName", R.string.HiddenName, new Object[0]);
        } else {
            str2 = tLRPC$User.first_name;
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", R.string.UserRemovedFromChatHint, str2, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment baseFragment, boolean z) {
        String str;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        if (z) {
            lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
            str = LocaleController.getString("UserBlocked", R.string.UserBlocked);
        } else {
            lottieLayout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            str = LocaleController.getString("UserUnblocked", R.string.UserUnblocked);
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(str));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createCopyLinkBulletin(BaseFragment baseFragment) {
        return of(baseFragment).createCopyLinkBulletin();
    }

    public static Bulletin createCopyLinkBulletin(FrameLayout frameLayout) {
        return of(frameLayout, null).createCopyLinkBulletin();
    }

    public static Bulletin createPinMessageBulletin(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(baseFragment, true, null, null, resourcesProvider);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment baseFragment, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(baseFragment, false, runnable, runnable2, resourcesProvider);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment baseFragment, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
        lottieLayout.setAnimation(z ? R.raw.ic_pin : R.raw.ic_unpin, 28, 28, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? R.string.MessagePinnedHint : R.string.MessageUnpinnedHint));
        if (!z) {
            lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true, resourcesProvider).setUndoAction(runnable).setDelayedAction(runnable2));
        }
        return Bulletin.make(baseFragment, lottieLayout, z ? 1500 : 5000);
    }

    public static Bulletin createSoundEnabledBulletin(BaseFragment baseFragment, int i, Theme.ResourcesProvider resourcesProvider) {
        String str;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
        boolean z = true;
        if (i == 0) {
            str = LocaleController.getString("SoundOnHint", R.string.SoundOnHint);
        } else if (i == 1) {
            str = LocaleController.getString("SoundOffHint", R.string.SoundOffHint);
            z = false;
        } else {
            throw new IllegalArgumentException();
        }
        if (z) {
            lottieLayout.setAnimation(R.raw.sound_on, new String[0]);
        } else {
            lottieLayout.setAnimation(R.raw.sound_off, new String[0]);
        }
        lottieLayout.textView.setText(str);
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }
}
