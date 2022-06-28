package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class BackButtonMenu {

    /* loaded from: classes5.dex */
    public static class PulledDialog<T> {
        Class<T> activity;
        TLRPC.Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC.User user;
    }

    public static ActionBarPopupWindow show(final BaseFragment thisFragment, View backButton, long currentDialogId, Theme.ResourcesProvider resourcesProvider) {
        View fragmentView;
        android.graphics.Rect backgroundPaddings;
        View fragmentView2;
        Drawable thumb;
        String name;
        Drawable thumb2;
        if (thisFragment == null) {
            return null;
        }
        final ActionBarLayout parentLayout = thisFragment.getParentLayout();
        Context context = thisFragment.getParentActivity();
        View fragmentView3 = thisFragment.getFragmentView();
        if (parentLayout != null && context != null) {
            if (fragmentView3 != null) {
                ArrayList<PulledDialog> dialogs = getStackedHistoryDialogs(thisFragment, currentDialogId);
                if (dialogs.size() <= 0) {
                    return null;
                }
                ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, resourcesProvider);
                android.graphics.Rect backgroundPaddings2 = new android.graphics.Rect();
                Drawable shadowDrawable = thisFragment.getParentActivity().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
                shadowDrawable.getPadding(backgroundPaddings2);
                layout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
                final AtomicReference<ActionBarPopupWindow> scrimPopupWindowRef = new AtomicReference<>();
                int i = 0;
                while (i < dialogs.size()) {
                    final PulledDialog pDialog = dialogs.get(i);
                    TLRPC.Chat chat = pDialog.chat;
                    TLRPC.User user = pDialog.user;
                    FrameLayout cell = new FrameLayout(context);
                    cell.setMinimumWidth(AndroidUtilities.dp(200.0f));
                    BackupImageView imageView = new BackupImageView(context);
                    ArrayList<PulledDialog> dialogs2 = dialogs;
                    imageView.setRoundRadius(AndroidUtilities.dp(32.0f));
                    cell.addView(imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 13.0f, 0.0f, 0.0f, 0.0f));
                    TextView titleView = new TextView(context);
                    Context context2 = context;
                    titleView.setLines(1);
                    Drawable shadowDrawable2 = shadowDrawable;
                    titleView.setTextSize(1, 16.0f);
                    titleView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
                    titleView.setEllipsize(TextUtils.TruncateAt.END);
                    cell.addView(titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 59.0f, 0.0f, 12.0f, 0.0f));
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setSmallSize(true);
                    if (chat != null) {
                        avatarDrawable.setInfo(chat);
                        fragmentView2 = fragmentView3;
                        if (chat.photo != null && chat.photo.strippedBitmap != null) {
                            thumb2 = chat.photo.strippedBitmap;
                        } else {
                            thumb2 = avatarDrawable;
                        }
                        backgroundPaddings = backgroundPaddings2;
                        imageView.setImage(ImageLocation.getForChat(chat, 1), "50_50", thumb2, chat);
                        titleView.setText(chat.title);
                    } else {
                        fragmentView2 = fragmentView3;
                        backgroundPaddings = backgroundPaddings2;
                        if (user != null) {
                            if (user.photo != null && user.photo.strippedBitmap != null) {
                                thumb = user.photo.strippedBitmap;
                            } else {
                                thumb = avatarDrawable;
                            }
                            if (pDialog.activity == ChatActivity.class && UserObject.isUserSelf(user)) {
                                name = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                                avatarDrawable.setAvatarType(1);
                                imageView.setImageDrawable(avatarDrawable);
                            } else if (UserObject.isReplyUser(user)) {
                                name = LocaleController.getString("RepliesTitle", R.string.RepliesTitle);
                                avatarDrawable.setAvatarType(12);
                                imageView.setImageDrawable(avatarDrawable);
                            } else if (UserObject.isDeleted(user)) {
                                name = LocaleController.getString("HiddenName", R.string.HiddenName);
                                avatarDrawable.setInfo(user);
                                imageView.setImage(ImageLocation.getForUser(user, 1), "50_50", avatarDrawable, user);
                            } else {
                                name = UserObject.getUserName(user);
                                avatarDrawable.setInfo(user);
                                imageView.setImage(ImageLocation.getForUser(user, 1), "50_50", thumb, user);
                            }
                            titleView.setText(name);
                        }
                    }
                    cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), false));
                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BackButtonMenu$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            BackButtonMenu.lambda$show$0(scrimPopupWindowRef, pDialog, parentLayout, thisFragment, view);
                        }
                    });
                    layout.addView((View) cell, LayoutHelper.createLinear(-1, 48));
                    i++;
                    context = context2;
                    dialogs = dialogs2;
                    shadowDrawable = shadowDrawable2;
                    fragmentView3 = fragmentView2;
                    backgroundPaddings2 = backgroundPaddings;
                }
                View fragmentView4 = fragmentView3;
                android.graphics.Rect backgroundPaddings3 = backgroundPaddings2;
                ActionBarPopupWindow scrimPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
                scrimPopupWindowRef.set(scrimPopupWindow);
                scrimPopupWindow.setPauseNotifications(true);
                scrimPopupWindow.setDismissAnimationDuration(220);
                scrimPopupWindow.setOutsideTouchable(true);
                scrimPopupWindow.setClippingEnabled(true);
                scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                scrimPopupWindow.setFocusable(true);
                layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                scrimPopupWindow.setInputMethodMode(2);
                scrimPopupWindow.setSoftInputMode(0);
                scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
                layout.setFitItems(true);
                int popupX = AndroidUtilities.dp(8.0f) - backgroundPaddings3.left;
                if (!AndroidUtilities.isTablet()) {
                    fragmentView = fragmentView4;
                } else {
                    int[] location = new int[2];
                    fragmentView = fragmentView4;
                    fragmentView.getLocationInWindow(location);
                    popupX += location[0];
                }
                int popupY = (backButton.getBottom() - backgroundPaddings3.top) - AndroidUtilities.dp(8.0f);
                scrimPopupWindow.showAtLocation(fragmentView, 51, popupX, popupY);
                return scrimPopupWindow;
            }
        }
        return null;
    }

    public static /* synthetic */ void lambda$show$0(AtomicReference scrimPopupWindowRef, PulledDialog pDialog, ActionBarLayout parentLayout, BaseFragment thisFragment, View e2) {
        if (scrimPopupWindowRef.get() != null) {
            ((ActionBarPopupWindow) scrimPopupWindowRef.getAndSet(null)).dismiss();
        }
        if (pDialog.stackIndex >= 0) {
            Long nextFragmentDialogId = null;
            if (parentLayout == null || parentLayout.fragmentsStack == null || pDialog.stackIndex >= parentLayout.fragmentsStack.size()) {
                nextFragmentDialogId = null;
            } else {
                BaseFragment nextFragment = parentLayout.fragmentsStack.get(pDialog.stackIndex);
                if (nextFragment instanceof ChatActivity) {
                    nextFragmentDialogId = Long.valueOf(((ChatActivity) nextFragment).getDialogId());
                } else if (nextFragment instanceof ProfileActivity) {
                    nextFragmentDialogId = Long.valueOf(((ProfileActivity) nextFragment).getDialogId());
                }
            }
            if (nextFragmentDialogId != null && nextFragmentDialogId.longValue() != pDialog.dialogId) {
                for (int j = parentLayout.fragmentsStack.size() - 2; j > pDialog.stackIndex; j--) {
                    parentLayout.removeFragmentFromStack(j);
                }
            } else if (parentLayout != null && parentLayout.fragmentsStack != null) {
                for (int j2 = parentLayout.fragmentsStack.size() - 2; j2 > pDialog.stackIndex; j2--) {
                    if (j2 >= 0 && j2 < parentLayout.fragmentsStack.size()) {
                        parentLayout.removeFragmentFromStack(j2);
                    }
                }
                int j3 = pDialog.stackIndex;
                if (j3 < parentLayout.fragmentsStack.size()) {
                    parentLayout.showFragment(pDialog.stackIndex);
                    parentLayout.closeLastFragment(true);
                    return;
                }
            }
        }
        goToPulledDialog(thisFragment, pDialog);
    }

    public static void goToPulledDialog(BaseFragment fragment, PulledDialog dialog) {
        if (dialog == null) {
            return;
        }
        if (dialog.activity == ChatActivity.class) {
            Bundle bundle = new Bundle();
            if (dialog.chat != null) {
                bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, dialog.chat.id);
            } else if (dialog.user != null) {
                bundle.putLong("user_id", dialog.user.id);
            }
            bundle.putInt("dialog_folder_id", dialog.folderId);
            bundle.putInt("dialog_filter_id", dialog.filterId);
            fragment.presentFragment(new ChatActivity(bundle), true);
        } else if (dialog.activity == ProfileActivity.class) {
            Bundle bundle2 = new Bundle();
            bundle2.putLong("dialog_id", dialog.dialogId);
            fragment.presentFragment(new ProfileActivity(bundle2), true);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00d8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<org.telegram.ui.Components.BackButtonMenu.PulledDialog> getStackedHistoryDialogs(org.telegram.ui.ActionBar.BaseFragment r19, long r20) {
        /*
            Method dump skipped, instructions count: 306
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackButtonMenu.getStackedHistoryDialogs(org.telegram.ui.ActionBar.BaseFragment, long):java.util.ArrayList");
    }

    public static /* synthetic */ int lambda$getStackedHistoryDialogs$1(PulledDialog d1, PulledDialog d2) {
        return d2.stackIndex - d1.stackIndex;
    }

    public static void addToPulledDialogs(BaseFragment thisFragment, int stackIndex, TLRPC.Chat chat, TLRPC.User user, long dialogId, int folderId, int filterId) {
        ActionBarLayout parentLayout;
        if ((chat == null && user == null) || thisFragment == null || (parentLayout = thisFragment.getParentLayout()) == null) {
            return;
        }
        if (parentLayout.pulledDialogs == null) {
            parentLayout.pulledDialogs = new ArrayList<>();
        }
        boolean alreadyAdded = false;
        Iterator<PulledDialog> it = parentLayout.pulledDialogs.iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().dialogId == dialogId) {
                    alreadyAdded = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (!alreadyAdded) {
            PulledDialog d = new PulledDialog();
            d.activity = ChatActivity.class;
            d.stackIndex = stackIndex;
            d.dialogId = dialogId;
            d.filterId = filterId;
            d.folderId = folderId;
            d.chat = chat;
            d.user = user;
            parentLayout.pulledDialogs.add(d);
        }
    }

    public static void clearPulledDialogs(BaseFragment thisFragment, int fromIndex) {
        ActionBarLayout parentLayout;
        if (thisFragment != null && (parentLayout = thisFragment.getParentLayout()) != null && parentLayout.pulledDialogs != null) {
            int i = 0;
            while (i < parentLayout.pulledDialogs.size()) {
                if (parentLayout.pulledDialogs.get(i).stackIndex > fromIndex) {
                    parentLayout.pulledDialogs.remove(i);
                    i--;
                }
                i++;
            }
        }
    }
}
