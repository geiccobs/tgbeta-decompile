package org.telegram.ui;

import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.google.firebase.appindexing.Action;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersAlert;
/* loaded from: classes4.dex */
public class EmojiAnimationsOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final HashSet<String> excludeEmojiFromPack;
    private static final HashSet<String> supportedEmoji = new HashSet<>();
    private boolean attached;
    ChatActivity chatActivity;
    FrameLayout contentLayout;
    int currentAccount;
    long dialogId;
    Runnable hintRunnable;
    String lastTappedEmoji;
    RecyclerListView listView;
    Runnable sentInteractionsRunnable;
    TLRPC.TL_messages_stickerSet set;
    int threadMsgId;
    private final int ANIMATION_JSON_VERSION = 1;
    private final String INTERACTIONS_STICKER_PACK = "EmojiAnimations";
    boolean inited = false;
    HashMap<String, ArrayList<TLRPC.Document>> emojiInteractionsStickersMap = new HashMap<>();
    HashMap<Long, Integer> lastAnimationIndex = new HashMap<>();
    Random random = new Random();
    int lastTappedMsgId = -1;
    long lastTappedTime = 0;
    ArrayList<Long> timeIntervals = new ArrayList<>();
    ArrayList<Integer> animationIndexes = new ArrayList<>();
    ArrayList<DrawingObject> drawingObjects = new ArrayList<>();

    static {
        HashSet<String> hashSet = new HashSet<>();
        excludeEmojiFromPack = hashSet;
        hashSet.add("0⃣");
        hashSet.add("1⃣");
        hashSet.add("2⃣");
        hashSet.add("3⃣");
        hashSet.add("4⃣");
        hashSet.add("5⃣");
        hashSet.add("6⃣");
        hashSet.add("7⃣");
        hashSet.add("8⃣");
        hashSet.add("9⃣");
    }

    public EmojiAnimationsOverlay(ChatActivity chatActivity, FrameLayout frameLayout, RecyclerListView chatListView, int currentAccount, long dialogId, int threadMsgId) {
        this.chatActivity = chatActivity;
        this.contentLayout = frameLayout;
        this.listView = chatListView;
        this.currentAccount = currentAccount;
        this.dialogId = dialogId;
        this.threadMsgId = threadMsgId;
    }

    public void onAttachedToWindow() {
        this.attached = true;
        checkStickerPack();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onDetachedFromWindow() {
        this.attached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void checkStickerPack() {
        if (this.inited) {
            return;
        }
        TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("EmojiAnimations");
        this.set = stickerSetByName;
        if (stickerSetByName == null) {
            this.set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("EmojiAnimations");
        }
        if (this.set == null) {
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("EmojiAnimations", false, true);
        }
        if (this.set != null) {
            HashMap<Long, TLRPC.Document> stickersMap = new HashMap<>();
            for (int i = 0; i < this.set.documents.size(); i++) {
                stickersMap.put(Long.valueOf(this.set.documents.get(i).id), this.set.documents.get(i));
            }
            for (int i2 = 0; i2 < this.set.packs.size(); i2++) {
                TLRPC.TL_stickerPack pack = this.set.packs.get(i2);
                if (!excludeEmojiFromPack.contains(pack.emoticon) && pack.documents.size() > 0) {
                    supportedEmoji.add(pack.emoticon);
                    ArrayList<TLRPC.Document> stickers = new ArrayList<>();
                    this.emojiInteractionsStickersMap.put(pack.emoticon, stickers);
                    for (int j = 0; j < pack.documents.size(); j++) {
                        stickers.add(stickersMap.get(pack.documents.get(j)));
                    }
                    if (pack.emoticon.equals("❤")) {
                        String[] heartEmojies = {"🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎"};
                        for (String heart : heartEmojies) {
                            supportedEmoji.add(heart);
                            this.emojiInteractionsStickersMap.put(heart, stickers);
                        }
                    }
                }
            }
            this.inited = true;
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        Integer printingType;
        EmojiAnimationsOverlay emojiAnimationsOverlay = this;
        if (id == NotificationCenter.diceStickersDidLoad) {
            String name = (String) args[0];
            if ("EmojiAnimations".equals(name)) {
                checkStickerPack();
            }
        } else if (id == NotificationCenter.onEmojiInteractionsReceived) {
            long dialogId = ((Long) args[0]).longValue();
            int i = 1;
            TLRPC.TL_sendMessageEmojiInteraction action = (TLRPC.TL_sendMessageEmojiInteraction) args[1];
            if (dialogId == emojiAnimationsOverlay.dialogId && supportedEmoji.contains(action.emoticon)) {
                final int messageId = action.msg_id;
                if (action.interaction.data != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(action.interaction.data);
                        JSONArray array = jsonObject.getJSONArray("a");
                        int i2 = 0;
                        while (i2 < array.length()) {
                            JSONObject actionObject = array.getJSONObject(i2);
                            final int animation = actionObject.optInt("i", i) - i;
                            double time = actionObject.optDouble(Theme.THEME_BACKGROUND_SLUG, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.EmojiAnimationsOverlay.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    EmojiAnimationsOverlay.this.findViewAndShowAnimation(messageId, animation);
                                }
                            }, (long) (time * 1000.0d));
                            i2++;
                            i = 1;
                            emojiAnimationsOverlay = this;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (id == NotificationCenter.updateInterfaces && (printingType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId)) != null && printingType.intValue() == 5) {
            cancelHintRunnable();
        }
    }

    public void findViewAndShowAnimation(int messageId, int animation) {
        if (!this.attached) {
            return;
        }
        ChatMessageCell bestView = null;
        int i = 0;
        while (true) {
            if (i >= this.listView.getChildCount()) {
                break;
            }
            View child = this.listView.getChildAt(i);
            if (child instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) child;
                if (cell.getPhotoImage().hasNotThumb() && cell.getMessageObject().getStickerEmoji() != null && cell.getMessageObject().getId() == messageId) {
                    bestView = cell;
                    break;
                }
            }
            i++;
        }
        if (bestView != null) {
            this.chatActivity.restartSticker(bestView);
            if (!EmojiData.hasEmojiSupportVibration(bestView.getMessageObject().getStickerEmoji())) {
                bestView.performHapticFeedback(3);
            }
            showAnimationForCell(bestView, animation, false, true);
        }
    }

    public void draw(Canvas canvas) {
        float viewX;
        if (!this.drawingObjects.isEmpty()) {
            int i = 0;
            while (i < this.drawingObjects.size()) {
                DrawingObject drawingObject = this.drawingObjects.get(i);
                drawingObject.viewFound = false;
                float childY = 0.0f;
                int k = 0;
                while (true) {
                    if (k >= this.listView.getChildCount()) {
                        break;
                    }
                    View child = this.listView.getChildAt(k);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        if (cell.getMessageObject().getId() == drawingObject.messageId) {
                            drawingObject.viewFound = true;
                            float viewX2 = this.listView.getX() + child.getX();
                            float viewY = this.listView.getY() + child.getY();
                            childY = child.getY();
                            if (drawingObject.isPremiumSticker) {
                                drawingObject.lastX = cell.getPhotoImage().getImageX() + viewX2;
                                drawingObject.lastY = cell.getPhotoImage().getImageY() + viewY;
                            } else {
                                float viewX3 = viewX2 + cell.getPhotoImage().getImageX();
                                float viewY2 = viewY + cell.getPhotoImage().getImageY();
                                if (drawingObject.isOut) {
                                    viewX = viewX3 + ((-cell.getPhotoImage().getImageWidth()) * 2.0f) + AndroidUtilities.dp(24.0f);
                                } else {
                                    viewX = viewX3 + (-AndroidUtilities.dp(24.0f));
                                }
                                drawingObject.lastX = viewX;
                                drawingObject.lastY = viewY2 - cell.getPhotoImage().getImageWidth();
                            }
                            drawingObject.lastW = cell.getPhotoImage().getImageWidth();
                            drawingObject.lastH = cell.getPhotoImage().getImageHeight();
                        }
                    }
                    k++;
                }
                if (!drawingObject.viewFound || drawingObject.lastH + childY < this.chatActivity.getChatListViewPadding() || childY > this.listView.getMeasuredHeight() - this.chatActivity.blurredViewBottomOffset) {
                    drawingObject.removing = true;
                }
                if (drawingObject.removing && drawingObject.removeProgress != 1.0f) {
                    drawingObject.removeProgress = Utilities.clamp(drawingObject.removeProgress + 0.10666667f, 1.0f, 0.0f);
                    drawingObject.imageReceiver.setAlpha(1.0f - drawingObject.removeProgress);
                    this.chatActivity.contentView.invalidate();
                }
                if (drawingObject.isPremiumSticker) {
                    float size = drawingObject.lastH * 1.49926f;
                    float paddingHorizontal = 0.0546875f * size;
                    float centerY = drawingObject.lastY + (drawingObject.lastH / 2.0f);
                    float top = (centerY - (size / 2.0f)) - (0.00279f * size);
                    if (!drawingObject.isOut) {
                        drawingObject.imageReceiver.setImageCoords(drawingObject.lastX - paddingHorizontal, top, size, size);
                    } else {
                        drawingObject.imageReceiver.setImageCoords(((drawingObject.lastX + drawingObject.lastW) - size) + paddingHorizontal, top, size, size);
                    }
                    if (!drawingObject.isOut) {
                        canvas.save();
                        canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                        drawingObject.imageReceiver.draw(canvas);
                        canvas.restore();
                    } else {
                        drawingObject.imageReceiver.draw(canvas);
                    }
                } else {
                    drawingObject.imageReceiver.setImageCoords(drawingObject.lastX + drawingObject.randomOffsetX, drawingObject.lastY + drawingObject.randomOffsetY, drawingObject.lastW * 3.0f, drawingObject.lastW * 3.0f);
                    if (!drawingObject.isOut) {
                        canvas.save();
                        canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                        drawingObject.imageReceiver.draw(canvas);
                        canvas.restore();
                    } else {
                        drawingObject.imageReceiver.draw(canvas);
                    }
                }
                if (drawingObject.removeProgress == 1.0f || (drawingObject.wasPlayed && drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().getCurrentFrame() == drawingObject.imageReceiver.getLottieAnimation().getFramesCount() - 2)) {
                    this.drawingObjects.remove(i);
                    i--;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.wasPlayed = true;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && !drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.imageReceiver.getLottieAnimation().setCurrentFrame(0, true);
                    drawingObject.imageReceiver.getLottieAnimation().start();
                }
                i++;
            }
            if (this.drawingObjects.isEmpty()) {
                onAllEffectsEnd();
            }
            this.contentLayout.invalidate();
        }
    }

    public void onAllEffectsEnd() {
    }

    public boolean onTapItem(ChatMessageCell view, ChatActivity chatActivity) {
        if (chatActivity.isSecretChat() || view.getMessageObject() == null || view.getMessageObject().getId() < 0) {
            return false;
        }
        if (!view.getMessageObject().isPremiumSticker() && chatActivity.currentUser == null) {
            return false;
        }
        boolean show = showAnimationForCell(view, -1, true, false);
        if (show && (!EmojiData.hasEmojiSupportVibration(view.getMessageObject().getStickerEmoji()) || view.getMessageObject().isPremiumSticker())) {
            view.performHapticFeedback(3);
        }
        if (!view.getMessageObject().isPremiumSticker()) {
            Integer printingType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
            boolean canShowHint = true;
            if (printingType != null && printingType.intValue() == 5) {
                canShowHint = false;
            }
            if (canShowHint && this.hintRunnable == null && show && ((Bulletin.getVisibleBulletin() == null || !Bulletin.getVisibleBulletin().isShowing()) && SharedConfig.emojiInteractionsHintCount > 0 && UserConfig.getInstance(this.currentAccount).getClientUserId() != chatActivity.currentUser.id)) {
                SharedConfig.updateEmojiInteractionsHintCount(SharedConfig.emojiInteractionsHintCount - 1);
                TLRPC.Document document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(view.getMessageObject().getStickerEmoji());
                StickerSetBulletinLayout layout = new StickerSetBulletinLayout(chatActivity.getParentActivity(), null, -1, document, chatActivity.getResourceProvider());
                layout.subtitleTextView.setVisibility(8);
                layout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EmojiInteractionTapHint", R.string.EmojiInteractionTapHint, chatActivity.currentUser.first_name)));
                layout.titleTextView.setTypeface(null);
                layout.titleTextView.setMaxLines(3);
                layout.titleTextView.setSingleLine(false);
                final Bulletin bulletin = Bulletin.make(chatActivity, layout, (int) Bulletin.DURATION_LONG);
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.EmojiAnimationsOverlay.2
                    @Override // java.lang.Runnable
                    public void run() {
                        bulletin.show();
                        EmojiAnimationsOverlay.this.hintRunnable = null;
                    }
                };
                this.hintRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 1500L);
            }
            return show;
        }
        view.getMessageObject().forcePlayEffect = false;
        view.getMessageObject().messageOwner.premiumEffectWasPlayed = true;
        chatActivity.getMessagesStorage().updateMessageCustomParams(this.dialogId, view.getMessageObject().messageOwner);
        return show;
    }

    public void cancelHintRunnable() {
        Runnable runnable = this.hintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.hintRunnable = null;
    }

    private boolean showAnimationForCell(ChatMessageCell view, int animation, boolean sendTap, boolean sendSeen) {
        final MessageObject messageObject;
        String emoji;
        TLRPC.Document document;
        TLRPC.VideoSize videoSize;
        int sameAnimationsCountMessageId;
        DrawingObject drawingObject;
        Runnable runnable;
        int animation2 = animation;
        if (this.drawingObjects.size() <= 12 && view.getPhotoImage().hasNotThumb() && (emoji = (messageObject = view.getMessageObject()).getStickerEmoji()) != null) {
            float imageH = view.getPhotoImage().getImageHeight();
            float imageW = view.getPhotoImage().getImageWidth();
            if (imageH > 0.0f && imageW > 0.0f) {
                String emoji2 = unwrapEmoji(emoji);
                boolean isPremiumSticker = messageObject.isPremiumSticker();
                if (supportedEmoji.contains(emoji2) || isPremiumSticker) {
                    ArrayList<TLRPC.Document> arrayList = this.emojiInteractionsStickersMap.get(emoji2);
                    if ((arrayList != null && !arrayList.isEmpty()) || isPremiumSticker) {
                        int sameAnimationsCountMessageId2 = 0;
                        int sameAnimationsCountDocumentId = 0;
                        for (int i = 0; i < this.drawingObjects.size(); i++) {
                            if (this.drawingObjects.get(i).messageId == view.getMessageObject().getId()) {
                                sameAnimationsCountMessageId2++;
                                if (this.drawingObjects.get(i).imageReceiver.getLottieAnimation() == null || this.drawingObjects.get(i).imageReceiver.getLottieAnimation().isGeneratingCache()) {
                                    return false;
                                }
                            }
                            if (this.drawingObjects.get(i).document != null && view.getMessageObject().getDocument() != null && this.drawingObjects.get(i).document.id == view.getMessageObject().getDocument().id) {
                                sameAnimationsCountDocumentId++;
                            }
                        }
                        if (sendTap && isPremiumSticker && sameAnimationsCountMessageId2 > 0) {
                            if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().hash == messageObject.getId()) {
                                return false;
                            }
                            TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                            TLRPC.TL_messages_stickerSet stickerSet = null;
                            if (inputStickerSet.short_name != null) {
                                stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(inputStickerSet.short_name);
                            }
                            if (stickerSet == null) {
                                stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetById(inputStickerSet.id);
                            }
                            if (stickerSet == null) {
                                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                                req.stickerset = inputStickerSet;
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda3
                                    @Override // org.telegram.tgnet.RequestDelegate
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        EmojiAnimationsOverlay.this.m3406x9867d307(messageObject, tLObject, tL_error);
                                    }
                                });
                            } else {
                                m3405x4149e228(stickerSet, messageObject);
                            }
                            return false;
                        } else if (sameAnimationsCountMessageId2 >= 4) {
                            return false;
                        } else {
                            if (isPremiumSticker) {
                                document = null;
                                videoSize = messageObject.getPremiumStickerAnimation();
                            } else {
                                if (animation2 < 0 || animation2 > arrayList.size() - 1) {
                                    animation2 = Math.abs(this.random.nextInt()) % arrayList.size();
                                }
                                document = arrayList.get(animation2);
                                videoSize = null;
                            }
                            if (document == null && videoSize == null) {
                                return false;
                            }
                            DrawingObject drawingObject2 = new DrawingObject();
                            drawingObject2.isPremiumSticker = messageObject.isPremiumSticker();
                            drawingObject2.randomOffsetX = ((this.random.nextInt() % 101) / 100.0f) * (imageW / 4.0f);
                            drawingObject2.randomOffsetY = (imageH / 4.0f) * ((this.random.nextInt() % 101) / 100.0f);
                            drawingObject2.messageId = view.getMessageObject().getId();
                            drawingObject2.document = document;
                            drawingObject2.isOut = view.getMessageObject().isOutOwner();
                            drawingObject2.imageReceiver.setAllowStartAnimation(true);
                            if (document != null) {
                                int w = (int) ((2.0f * imageW) / AndroidUtilities.density);
                                Integer lastIndex = this.lastAnimationIndex.get(Long.valueOf(document.id));
                                int currentIndex = lastIndex == null ? 0 : lastIndex.intValue();
                                sameAnimationsCountMessageId = sameAnimationsCountMessageId2;
                                this.lastAnimationIndex.put(Long.valueOf(document.id), Integer.valueOf((currentIndex + 1) % 4));
                                ImageLocation imageLocation = ImageLocation.getForDocument(document);
                                drawingObject = drawingObject2;
                                drawingObject.imageReceiver.setUniqKeyPrefix(currentIndex + "_" + drawingObject.messageId + "_");
                                drawingObject.imageReceiver.setImage(imageLocation, w + "_" + w + "_pcache", null, "tgs", this.set, 1);
                            } else {
                                sameAnimationsCountMessageId = sameAnimationsCountMessageId2;
                                drawingObject = drawingObject2;
                                int w2 = (int) ((1.5f * imageW) / AndroidUtilities.density);
                                if (sameAnimationsCountDocumentId > 0) {
                                    Integer lastIndex2 = this.lastAnimationIndex.get(Long.valueOf(messageObject.getDocument().id));
                                    int currentIndex2 = lastIndex2 == null ? 0 : lastIndex2.intValue();
                                    this.lastAnimationIndex.put(Long.valueOf(messageObject.getDocument().id), Integer.valueOf((currentIndex2 + 1) % 4));
                                    drawingObject.imageReceiver.setUniqKeyPrefix(currentIndex2 + "_" + drawingObject.messageId + "_");
                                }
                                drawingObject.document = messageObject.getDocument();
                                drawingObject.imageReceiver.setImage(ImageLocation.getForDocument(videoSize, messageObject.getDocument()), w2 + "_" + w2, null, "tgs", this.set, 1);
                            }
                            drawingObject.imageReceiver.setLayerNum(Integer.MAX_VALUE);
                            drawingObject.imageReceiver.setAutoRepeat(0);
                            if (drawingObject.imageReceiver.getLottieAnimation() != null) {
                                if (drawingObject.isPremiumSticker) {
                                    drawingObject.imageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
                                }
                                drawingObject.imageReceiver.getLottieAnimation().start();
                            }
                            this.drawingObjects.add(drawingObject);
                            drawingObject.imageReceiver.onAttachedToWindow();
                            drawingObject.imageReceiver.setParentView(this.contentLayout);
                            this.contentLayout.invalidate();
                            if (sendTap && !isPremiumSticker) {
                                int i2 = this.lastTappedMsgId;
                                if (i2 != 0 && i2 != view.getMessageObject().getId() && (runnable = this.sentInteractionsRunnable) != null) {
                                    AndroidUtilities.cancelRunOnUIThread(runnable);
                                    this.sentInteractionsRunnable.run();
                                }
                                this.lastTappedMsgId = view.getMessageObject().getId();
                                this.lastTappedEmoji = emoji2;
                                if (this.lastTappedTime == 0) {
                                    this.lastTappedTime = System.currentTimeMillis();
                                    this.timeIntervals.clear();
                                    this.animationIndexes.clear();
                                    this.timeIntervals.add(0L);
                                    this.animationIndexes.add(Integer.valueOf(animation2));
                                } else {
                                    this.timeIntervals.add(Long.valueOf(System.currentTimeMillis() - this.lastTappedTime));
                                    this.animationIndexes.add(Integer.valueOf(animation2));
                                }
                                Runnable runnable2 = this.sentInteractionsRunnable;
                                if (runnable2 != null) {
                                    AndroidUtilities.cancelRunOnUIThread(runnable2);
                                    this.sentInteractionsRunnable = null;
                                }
                                Runnable runnable3 = new Runnable() { // from class: org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        EmojiAnimationsOverlay.this.m3407xef85c3e6();
                                    }
                                };
                                this.sentInteractionsRunnable = runnable3;
                                AndroidUtilities.runOnUIThread(runnable3, 500L);
                            }
                            if (!sendSeen) {
                                return true;
                            }
                            MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadMsgId, 11, emoji2, 0);
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$showAnimationForCell$1$org-telegram-ui-EmojiAnimationsOverlay */
    public /* synthetic */ void m3406x9867d307(final MessageObject messageObject, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                EmojiAnimationsOverlay.this.m3405x4149e228(response, messageObject);
            }
        });
    }

    /* renamed from: lambda$showAnimationForCell$2$org-telegram-ui-EmojiAnimationsOverlay */
    public /* synthetic */ void m3407xef85c3e6() {
        sendCurrentTaps();
        this.sentInteractionsRunnable = null;
    }

    /* renamed from: showStickerSetBulletin */
    public void m3405x4149e228(TLRPC.TL_messages_stickerSet stickerSet, final MessageObject messageObject) {
        if (MessagesController.getInstance(this.currentAccount).premiumLocked || this.chatActivity.getParentActivity() == null) {
            return;
        }
        StickerSetBulletinLayout layout = new StickerSetBulletinLayout(this.contentLayout.getContext(), null, -1, messageObject.getDocument(), this.chatActivity.getResourceProvider());
        layout.titleTextView.setText(stickerSet.set.title);
        layout.subtitleTextView.setText(LocaleController.getString("PremiumStickerTooltip", R.string.PremiumStickerTooltip));
        Bulletin.UndoButton viewButton = new Bulletin.UndoButton(this.chatActivity.getParentActivity(), true, this.chatActivity.getResourceProvider());
        layout.setButton(viewButton);
        viewButton.setUndoAction(new Runnable() { // from class: org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                EmojiAnimationsOverlay.this.m3408xc35e9be0(messageObject);
            }
        });
        viewButton.setText(LocaleController.getString(Action.Builder.VIEW_ACTION, R.string.ViewAction));
        Bulletin bulletin = Bulletin.make(this.chatActivity, layout, (int) Bulletin.DURATION_LONG);
        bulletin.hash = messageObject.getId();
        bulletin.show();
    }

    /* renamed from: lambda$showStickerSetBulletin$3$org-telegram-ui-EmojiAnimationsOverlay */
    public /* synthetic */ void m3408xc35e9be0(MessageObject messageObject) {
        StickersAlert alert = new StickersAlert(this.chatActivity.getParentActivity(), this.chatActivity, messageObject.getInputStickerSet(), null, this.chatActivity.chatActivityEnterView, this.chatActivity.getResourceProvider());
        alert.setCalcMandatoryInsets(this.chatActivity.isKeyboardVisible());
        this.chatActivity.showDialog(alert);
    }

    private String unwrapEmoji(String emoji) {
        CharSequence fixedEmoji = emoji;
        int length = emoji.length();
        int a = 0;
        while (a < length) {
            if (a < length - 1 && ((fixedEmoji.charAt(a) == 55356 && fixedEmoji.charAt(a + 1) >= 57339 && fixedEmoji.charAt(a + 1) <= 57343) || (fixedEmoji.charAt(a) == 8205 && (fixedEmoji.charAt(a + 1) == 9792 || fixedEmoji.charAt(a + 1) == 9794)))) {
                fixedEmoji = TextUtils.concat(fixedEmoji.subSequence(0, a), fixedEmoji.subSequence(a + 2, fixedEmoji.length()));
                length -= 2;
                a--;
            } else if (fixedEmoji.charAt(a) == 65039) {
                fixedEmoji = TextUtils.concat(fixedEmoji.subSequence(0, a), fixedEmoji.subSequence(a + 1, fixedEmoji.length()));
                length--;
                a--;
            }
            a++;
            fixedEmoji = fixedEmoji;
        }
        return fixedEmoji.toString();
    }

    private void sendCurrentTaps() {
        if (this.lastTappedMsgId == 0) {
            return;
        }
        TLRPC.TL_sendMessageEmojiInteraction interaction = new TLRPC.TL_sendMessageEmojiInteraction();
        interaction.msg_id = this.lastTappedMsgId;
        interaction.emoticon = this.lastTappedEmoji;
        interaction.interaction = new TLRPC.TL_dataJSON();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("v", 1);
            JSONArray array = new JSONArray();
            for (int i = 0; i < this.timeIntervals.size(); i++) {
                JSONObject action = new JSONObject();
                action.put("i", this.animationIndexes.get(i).intValue() + 1);
                action.put(Theme.THEME_BACKGROUND_SLUG, ((float) this.timeIntervals.get(i).longValue()) / 1000.0f);
                array.put(i, action);
            }
            jsonObject.put("a", array);
            interaction.interaction.data = jsonObject.toString();
            TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
            int i2 = this.threadMsgId;
            if (i2 != 0) {
                req.top_msg_id = i2;
                req.flags = 1 | req.flags;
            }
            req.action = interaction;
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, null);
            clearSendingInfo();
        } catch (JSONException e) {
            clearSendingInfo();
            FileLog.e(e);
        }
    }

    private void clearSendingInfo() {
        this.lastTappedMsgId = 0;
        this.lastTappedEmoji = null;
        this.lastTappedTime = 0L;
        this.timeIntervals.clear();
        this.animationIndexes.clear();
    }

    public void onScrolled(int dy) {
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            if (!this.drawingObjects.get(i).viewFound) {
                this.drawingObjects.get(i).lastY -= dy;
            }
        }
    }

    public boolean isIdle() {
        return this.drawingObjects.isEmpty();
    }

    public boolean checkPosition(ChatMessageCell messageCell, float chatListViewPaddingTop, int bottom) {
        float y = messageCell.getY() + messageCell.getPhotoImage().getCenterY();
        if (y > chatListViewPaddingTop && y < bottom) {
            return true;
        }
        return false;
    }

    /* loaded from: classes4.dex */
    public class DrawingObject {
        TLRPC.Document document;
        ImageReceiver imageReceiver;
        boolean isOut;
        public boolean isPremiumSticker;
        public float lastH;
        public float lastW;
        public float lastX;
        public float lastY;
        int messageId;
        public float randomOffsetX;
        public float randomOffsetY;
        float removeProgress;
        boolean removing;
        public boolean viewFound;
        boolean wasPlayed;

        private DrawingObject() {
            EmojiAnimationsOverlay.this = r1;
            this.imageReceiver = new ImageReceiver();
        }
    }
}
