package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AvatarsDarawable;
import org.telegram.ui.Components.CounterView;
/* loaded from: classes5.dex */
public class ReactionsLayoutInBubble {
    private static final int ANIMATION_TYPE_IN = 1;
    private static final int ANIMATION_TYPE_MOVE = 3;
    private static final int ANIMATION_TYPE_OUT = 2;
    private static int animationUniq;
    private int animateFromTotalHeight;
    private boolean animateHeight;
    private boolean animateMove;
    private boolean animateWidth;
    boolean attached;
    int availableWidth;
    public boolean drawServiceShaderBackground;
    public int fromWidth;
    private float fromX;
    private float fromY;
    public boolean hasUnreadReactions;
    public int height;
    public boolean isEmpty;
    public boolean isSmall;
    private int lastDrawTotalHeight;
    private int lastDrawnWidth;
    private float lastDrawnX;
    private float lastDrawnY;
    public int lastLineX;
    ReactionButton lastSelectedButton;
    float lastX;
    float lastY;
    Runnable longPressRunnable;
    MessageObject messageObject;
    ChatMessageCell parentView;
    public int positionOffsetY;
    boolean pressed;
    Theme.ResourcesProvider resourcesProvider;
    private String scrimViewReaction;
    public int totalHeight;
    private boolean wasDrawn;
    public int width;
    public int x;
    public int y;
    private static Paint paint = new Paint(1);
    private static TextPaint textPaint = new TextPaint(1);
    private static final ButtonsComparator comparator = new ButtonsComparator();
    private static final Comparator<TLRPC.User> usersComparator = ReactionsLayoutInBubble$$ExternalSyntheticLambda1.INSTANCE;
    ArrayList<ReactionButton> reactionButtons = new ArrayList<>();
    ArrayList<ReactionButton> outButtons = new ArrayList<>();
    HashMap<String, ReactionButton> lastDrawingReactionButtons = new HashMap<>();
    HashMap<String, ReactionButton> lastDrawingReactionButtonsTmp = new HashMap<>();
    HashMap<String, ImageReceiver> animatedReactions = new HashMap<>();
    int currentAccount = UserConfig.selectedAccount;
    private float touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();

    public static /* synthetic */ int lambda$static$0(TLRPC.User user1, TLRPC.User user2) {
        return (int) (user1.id - user2.id);
    }

    public ReactionsLayoutInBubble(ChatMessageCell parentView) {
        this.parentView = parentView;
        paint.setColor(Theme.getColor(Theme.key_chat_inLoader));
        textPaint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
    }

    public void setMessage(MessageObject messageObject, boolean isSmall, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.isSmall = isSmall;
        this.messageObject = messageObject;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        this.hasUnreadReactions = false;
        this.reactionButtons.clear();
        if (messageObject != null) {
            if (messageObject.messageOwner.reactions != null && messageObject.messageOwner.reactions.results != null) {
                int totalCount = 0;
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    totalCount += messageObject.messageOwner.reactions.results.get(i2).count;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= messageObject.messageOwner.reactions.results.size()) {
                        break;
                    }
                    TLRPC.TL_reactionCount reactionCount = messageObject.messageOwner.reactions.results.get(i3);
                    ReactionButton button = new ReactionButton(reactionCount);
                    this.reactionButtons.add(button);
                    if (!isSmall && messageObject.messageOwner.reactions.recent_reactions != null) {
                        ArrayList<TLRPC.User> users = null;
                        if (reactionCount.count <= 3 && totalCount <= 3) {
                            for (int j = 0; j < messageObject.messageOwner.reactions.recent_reactions.size(); j++) {
                                TLRPC.TL_messagePeerReaction reccent = messageObject.messageOwner.reactions.recent_reactions.get(j);
                                if (reccent.reaction.equals(reactionCount.reaction) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reccent.peer_id))) != null) {
                                    if (users == null) {
                                        users = new ArrayList<>();
                                    }
                                    users.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reccent.peer_id))));
                                }
                            }
                            button.setUsers(users);
                            if (users != null && !users.isEmpty()) {
                                button.count = 0;
                                button.counterDrawable.setCount(0, false);
                            }
                        }
                    }
                    if (isSmall && reactionCount.count > 1 && reactionCount.chosen) {
                        this.reactionButtons.add(new ReactionButton(reactionCount));
                        this.reactionButtons.get(0).isSelected = false;
                        this.reactionButtons.get(1).isSelected = true;
                        this.reactionButtons.get(0).realCount = 1;
                        this.reactionButtons.get(1).realCount = 1;
                        this.reactionButtons.get(1).key += "_";
                        break;
                    } else if (isSmall && i3 == 2) {
                        break;
                    } else {
                        if (this.attached) {
                            button.attach();
                        }
                        i3++;
                    }
                }
            }
            if (!isSmall) {
                ButtonsComparator buttonsComparator = comparator;
                buttonsComparator.currentAccount = this.currentAccount;
                Collections.sort(this.reactionButtons, buttonsComparator);
            }
            this.hasUnreadReactions = MessageObject.hasUnreadReactions(messageObject.messageOwner);
        }
        this.isEmpty = this.reactionButtons.isEmpty();
    }

    public void measure(int availableWidth, int gravity) {
        this.height = 0;
        this.width = 0;
        this.positionOffsetY = 0;
        this.totalHeight = 0;
        if (this.isEmpty) {
            return;
        }
        this.availableWidth = availableWidth;
        int maxWidth = 0;
        int currentX = 0;
        int currentY = 0;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ReactionButton button = this.reactionButtons.get(i);
            if (this.isSmall) {
                button.width = AndroidUtilities.dp(14.0f);
                button.height = AndroidUtilities.dp(14.0f);
            } else {
                button.width = AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(4.0f);
                if (button.avatarsDarawable == null || button.users.size() <= 0) {
                    button.width = (int) (button.width + button.counterDrawable.textPaint.measureText(button.countText) + AndroidUtilities.dp(8.0f));
                } else {
                    button.users.size();
                    int c2 = button.users.size() > 1 ? button.users.size() - 1 : 0;
                    button.width = (int) (button.width + AndroidUtilities.dp(2.0f) + (AndroidUtilities.dp(20.0f) * 1) + (AndroidUtilities.dp(20.0f) * c2 * 0.8f) + AndroidUtilities.dp(1.0f));
                    button.avatarsDarawable.height = AndroidUtilities.dp(26.0f);
                }
                button.height = AndroidUtilities.dp(26.0f);
            }
            if (button.width + currentX > availableWidth) {
                currentX = 0;
                currentY += button.height + AndroidUtilities.dp(4.0f);
            }
            button.x = currentX;
            button.y = currentY;
            currentX += button.width + AndroidUtilities.dp(4.0f);
            if (currentX > maxWidth) {
                maxWidth = currentX;
            }
        }
        if (gravity == 5 && !this.reactionButtons.isEmpty()) {
            int fromP = 0;
            int startY = this.reactionButtons.get(0).y;
            for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
                if (this.reactionButtons.get(i2).y != startY) {
                    int lineOffset = (availableWidth - this.reactionButtons.get(i2 - 1).x) + this.reactionButtons.get(i2 - 1).width;
                    for (int k = fromP; k < i2; k++) {
                        this.reactionButtons.get(k).x += lineOffset;
                    }
                    fromP = i2;
                }
            }
            int last = this.reactionButtons.size() - 1;
            if (fromP != last) {
                int lineOffset2 = availableWidth - (this.reactionButtons.get(last).x + this.reactionButtons.get(last).width);
                for (int k2 = fromP; k2 <= last; k2++) {
                    this.reactionButtons.get(k2).x += lineOffset2;
                }
            }
        }
        this.lastLineX = currentX;
        if (gravity == 5) {
            this.width = availableWidth;
        } else {
            this.width = maxWidth;
        }
        this.height = (this.reactionButtons.size() == 0 ? 0 : AndroidUtilities.dp(26.0f)) + currentY;
        this.drawServiceShaderBackground = false;
    }

    public void draw(Canvas canvas, float animationProgress, String drawOnlyReaction) {
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return;
        }
        float totalX = this.x;
        float totalY = this.y;
        if (this.isEmpty) {
            totalX = this.lastDrawnX;
            totalY = this.lastDrawnY;
        } else if (this.animateMove) {
            totalX = (totalX * animationProgress) + (this.fromX * (1.0f - animationProgress));
            totalY = (totalY * animationProgress) + (this.fromY * (1.0f - animationProgress));
        }
        canvas.save();
        canvas.translate(totalX, totalY);
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ReactionButton reactionButton = this.reactionButtons.get(i);
            if (!reactionButton.reaction.equals(this.scrimViewReaction) && (drawOnlyReaction == null || reactionButton.reaction.equals(drawOnlyReaction))) {
                canvas.save();
                float x = reactionButton.x;
                float y = reactionButton.y;
                if (animationProgress != 1.0f && reactionButton.animationType == 3) {
                    x = (reactionButton.x * animationProgress) + (reactionButton.animateFromX * (1.0f - animationProgress));
                    y = (reactionButton.y * animationProgress) + (reactionButton.animateFromY * (1.0f - animationProgress));
                }
                canvas.translate(x, y);
                float alpha = 1.0f;
                if (animationProgress != 1.0f && reactionButton.animationType == 1) {
                    float s = (animationProgress * 0.5f) + 0.5f;
                    alpha = animationProgress;
                    canvas.scale(s, s, reactionButton.width / 2.0f, reactionButton.height / 2.0f);
                }
                reactionButton.draw(canvas, reactionButton.animationType == 3 ? animationProgress : 1.0f, alpha, drawOnlyReaction != null);
                canvas.restore();
            }
        }
        for (int i2 = 0; i2 < this.outButtons.size(); i2++) {
            ReactionButton reactionButton2 = this.outButtons.get(i2);
            canvas.save();
            canvas.translate(reactionButton2.x, reactionButton2.y);
            float s2 = ((1.0f - animationProgress) * 0.5f) + 0.5f;
            canvas.scale(s2, s2, reactionButton2.width / 2.0f, reactionButton2.height / 2.0f);
            this.outButtons.get(i2).draw(canvas, 1.0f, 1.0f - animationProgress, false);
            canvas.restore();
        }
        canvas.restore();
    }

    public void recordDrawingState() {
        this.lastDrawingReactionButtons.clear();
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.lastDrawingReactionButtons.put(this.reactionButtons.get(i).key, this.reactionButtons.get(i));
        }
        this.wasDrawn = !this.isEmpty;
        this.lastDrawnX = this.x;
        this.lastDrawnY = this.y;
        this.lastDrawnWidth = this.width;
        this.lastDrawTotalHeight = this.totalHeight;
    }

    public boolean animateChange() {
        if (this.messageObject == null) {
            return false;
        }
        boolean changed = false;
        this.lastDrawingReactionButtonsTmp.clear();
        for (int i = 0; i < this.outButtons.size(); i++) {
            this.outButtons.get(i).detach();
        }
        this.outButtons.clear();
        this.lastDrawingReactionButtonsTmp.putAll(this.lastDrawingReactionButtons);
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton button = this.reactionButtons.get(i2);
            ReactionButton lastButton = this.lastDrawingReactionButtonsTmp.remove(button.key);
            if (lastButton != null) {
                if (button.x != lastButton.x || button.y != lastButton.y || button.width != lastButton.width || button.count != lastButton.count || button.backgroundColor != lastButton.backgroundColor || button.avatarsDarawable != null || lastButton.avatarsDarawable != null) {
                    button.animateFromX = lastButton.x;
                    button.animateFromY = lastButton.y;
                    button.animateFromWidth = lastButton.width;
                    button.fromTextColor = lastButton.lastDrawnTextColor;
                    button.fromBackgroundColor = lastButton.lastDrawnBackgroundColor;
                    button.animationType = 3;
                    if (button.count != lastButton.count) {
                        button.counterDrawable.setCount(lastButton.count, false);
                        button.counterDrawable.setCount(button.count, true);
                    }
                    if (button.avatarsDarawable != null || lastButton.avatarsDarawable != null) {
                        if (button.avatarsDarawable == null) {
                            button.setUsers(new ArrayList<>());
                        }
                        if (lastButton.avatarsDarawable == null) {
                            lastButton.setUsers(new ArrayList<>());
                        }
                        button.avatarsDarawable.animateFromState(lastButton.avatarsDarawable, this.currentAccount, false);
                    }
                    changed = true;
                } else {
                    button.animationType = 0;
                }
            } else {
                changed = true;
                button.animationType = 1;
            }
        }
        if (!this.lastDrawingReactionButtonsTmp.isEmpty()) {
            changed = true;
            this.outButtons.addAll(this.lastDrawingReactionButtonsTmp.values());
            for (int i3 = 0; i3 < this.outButtons.size(); i3++) {
                this.outButtons.get(i3).drawImage = this.outButtons.get(i3).lastImageDrawn;
                this.outButtons.get(i3).attach();
            }
        }
        if (this.wasDrawn) {
            float f = this.lastDrawnX;
            if (f != this.x || this.lastDrawnY != this.y) {
                this.animateMove = true;
                this.fromX = f;
                this.fromY = this.lastDrawnY;
                changed = true;
            }
        }
        int i4 = this.lastDrawnWidth;
        if (i4 != this.width) {
            this.animateWidth = true;
            this.fromWidth = i4;
            changed = true;
        }
        int i5 = this.lastDrawTotalHeight;
        if (i5 != this.totalHeight) {
            this.animateHeight = true;
            this.animateFromTotalHeight = i5;
            return true;
        }
        return changed;
    }

    public void resetAnimation() {
        for (int i = 0; i < this.outButtons.size(); i++) {
            this.outButtons.get(i).detach();
        }
        this.outButtons.clear();
        this.animateMove = false;
        this.animateWidth = false;
        this.animateHeight = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            this.reactionButtons.get(i2).animationType = 0;
        }
    }

    public ReactionButton getReactionButton(String reaction) {
        if (this.isSmall) {
            HashMap<String, ReactionButton> hashMap = this.lastDrawingReactionButtons;
            ReactionButton button = hashMap.get(reaction + "_");
            if (button != null) {
                return button;
            }
        }
        return this.lastDrawingReactionButtons.get(reaction);
    }

    public void setScrimReaction(String scrimViewReaction) {
        this.scrimViewReaction = scrimViewReaction;
    }

    /* loaded from: classes5.dex */
    public class ReactionButton {
        public int animateFromWidth;
        public int animateFromX;
        public int animateFromY;
        public int animationType;
        AvatarsDarawable avatarsDarawable;
        int backgroundColor;
        int count;
        String countText;
        CounterView.CounterDrawable counterDrawable;
        public int fromBackgroundColor;
        public int fromTextColor;
        public int height;
        boolean isSelected;
        public String key;
        int lastDrawnBackgroundColor;
        int lastDrawnTextColor;
        public boolean lastImageDrawn;
        String reaction;
        private final TLRPC.TL_reactionCount reactionCount;
        public int realCount;
        int serviceBackgroundColor;
        int serviceTextColor;
        int textColor;
        ArrayList<TLRPC.User> users;
        public boolean wasDrawn;
        public int width;
        public int x;
        public int y;
        public boolean drawImage = true;
        ImageReceiver imageReceiver = new ImageReceiver();

        public ReactionButton(TLRPC.TL_reactionCount reactionCount) {
            TLRPC.TL_availableReaction r;
            ReactionsLayoutInBubble.this = this$0;
            this.counterDrawable = new CounterView.CounterDrawable(this$0.parentView, false, null);
            this.reactionCount = reactionCount;
            this.reaction = reactionCount.reaction;
            this.count = reactionCount.count;
            this.realCount = reactionCount.count;
            this.key = this.reaction;
            this.countText = Integer.toString(reactionCount.count);
            this.imageReceiver.setParentView(this$0.parentView);
            this.isSelected = reactionCount.chosen;
            this.counterDrawable.updateVisibility = false;
            this.counterDrawable.shortFormat = true;
            boolean z = reactionCount.chosen;
            String str = Theme.key_chat_outReactionButtonBackground;
            if (z) {
                this.backgroundColor = Theme.getColor(this$0.messageObject.isOutOwner() ? str : Theme.key_chat_inReactionButtonBackground, this$0.resourcesProvider);
                this.textColor = Theme.getColor(this$0.messageObject.isOutOwner() ? Theme.key_chat_outReactionButtonTextSelected : Theme.key_chat_inReactionButtonTextSelected, this$0.resourcesProvider);
                this.serviceTextColor = Theme.getColor(!this$0.messageObject.isOutOwner() ? Theme.key_chat_inReactionButtonBackground : str, this$0.resourcesProvider);
                this.serviceBackgroundColor = Theme.getColor(this$0.messageObject.isOutOwner() ? Theme.key_chat_outBubble : Theme.key_chat_inBubble);
            } else {
                this.textColor = Theme.getColor(this$0.messageObject.isOutOwner() ? Theme.key_chat_outReactionButtonText : Theme.key_chat_inReactionButtonText, this$0.resourcesProvider);
                int color = Theme.getColor(!this$0.messageObject.isOutOwner() ? Theme.key_chat_inReactionButtonBackground : str, this$0.resourcesProvider);
                this.backgroundColor = color;
                this.backgroundColor = ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * 0.156f));
                this.serviceTextColor = Theme.getColor(Theme.key_chat_serviceText, this$0.resourcesProvider);
                this.serviceBackgroundColor = 0;
            }
            if (this.reaction != null && (r = MediaDataController.getInstance(this$0.currentAccount).getReactionsMap().get(this.reaction)) != null) {
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(r.static_icon, Theme.key_windowBackgroundGray, 1.0f);
                this.imageReceiver.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", svgThumb, "webp", r, 1);
            }
            this.counterDrawable.setSize(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(100.0f));
            this.counterDrawable.textPaint = ReactionsLayoutInBubble.textPaint;
            this.counterDrawable.setCount(this.count, false);
            this.counterDrawable.setType(2);
            this.counterDrawable.gravity = 3;
        }

        public void draw(Canvas canvas, float progress, float alpha, boolean drawOverlayScrim) {
            Theme.MessageDrawable messageBackground;
            this.wasDrawn = true;
            if (ReactionsLayoutInBubble.this.isSmall) {
                this.imageReceiver.setAlpha(alpha);
                this.imageReceiver.setImageCoords(0.0f, 0.0f, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
                drawImage(canvas, alpha);
                return;
            }
            updateColors(progress);
            ReactionsLayoutInBubble.textPaint.setColor(this.lastDrawnTextColor);
            ReactionsLayoutInBubble.paint.setColor(this.lastDrawnBackgroundColor);
            if (alpha != 1.0f) {
                ReactionsLayoutInBubble.textPaint.setAlpha((int) (ReactionsLayoutInBubble.textPaint.getAlpha() * alpha));
                ReactionsLayoutInBubble.paint.setAlpha((int) (ReactionsLayoutInBubble.paint.getAlpha() * alpha));
            }
            this.imageReceiver.setAlpha(alpha);
            int w = this.width;
            if (progress != 1.0f && this.animationType == 3) {
                w = (int) ((this.width * progress) + (this.animateFromWidth * (1.0f - progress)));
            }
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, w, this.height);
            float rad = this.height / 2.0f;
            if (ReactionsLayoutInBubble.this.drawServiceShaderBackground) {
                Paint paint1 = ReactionsLayoutInBubble.this.getThemedPaint(Theme.key_paint_chatActionBackground);
                Paint paint2 = Theme.chat_actionBackgroundGradientDarkenPaint;
                int oldAlpha = paint1.getAlpha();
                int oldAlpha2 = paint2.getAlpha();
                paint1.setAlpha((int) (oldAlpha * alpha));
                paint2.setAlpha((int) (oldAlpha2 * alpha));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, paint1);
                if (ReactionsLayoutInBubble.this.hasGradientService()) {
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, paint2);
                }
                paint1.setAlpha(oldAlpha);
                paint2.setAlpha(oldAlpha2);
            }
            if (!ReactionsLayoutInBubble.this.drawServiceShaderBackground && drawOverlayScrim && (messageBackground = ReactionsLayoutInBubble.this.parentView.getCurrentBackgroundDrawable(false)) != null) {
                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, messageBackground.getPaint());
            }
            canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, ReactionsLayoutInBubble.paint);
            this.imageReceiver.setImageCoords(AndroidUtilities.dp(8.0f), (this.height - AndroidUtilities.dp(20.0f)) / 2.0f, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
            drawImage(canvas, alpha);
            if (this.count != 0 || this.counterDrawable.countChangeProgress != 1.0f) {
                canvas.save();
                canvas.translate(AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f), 0.0f);
                this.counterDrawable.draw(canvas);
                canvas.restore();
            }
            if (this.avatarsDarawable != null) {
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f), 0.0f);
                this.avatarsDarawable.setAlpha(alpha);
                this.avatarsDarawable.setTransitionProgress(progress);
                this.avatarsDarawable.onDraw(canvas);
                canvas.restore();
            }
        }

        private void updateColors(float progress) {
            if (ReactionsLayoutInBubble.this.drawServiceShaderBackground) {
                this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.serviceTextColor, progress);
                this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.serviceBackgroundColor, progress);
                return;
            }
            this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.textColor, progress);
            this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.backgroundColor, progress);
        }

        private void drawImage(Canvas canvas, float alpha) {
            if (this.drawImage && (this.realCount > 1 || !ReactionsEffectOverlay.isPlaying(ReactionsLayoutInBubble.this.messageObject.getId(), ReactionsLayoutInBubble.this.messageObject.getGroupId(), this.reaction) || !this.isSelected)) {
                ImageReceiver imageReceiver2 = ReactionsLayoutInBubble.this.animatedReactions.get(this.reaction);
                boolean drawStaticImage = true;
                if (imageReceiver2 != null) {
                    if (imageReceiver2.getLottieAnimation() != null && imageReceiver2.getLottieAnimation().hasBitmap()) {
                        drawStaticImage = false;
                    }
                    if (alpha != 1.0f) {
                        imageReceiver2.setAlpha(alpha);
                        if (alpha <= 0.0f) {
                            imageReceiver2.onDetachedFromWindow();
                            ReactionsLayoutInBubble.this.animatedReactions.remove(this.reaction);
                        }
                    } else if (imageReceiver2.getLottieAnimation() != null && !imageReceiver2.getLottieAnimation().isRunning()) {
                        drawStaticImage = true;
                        float alpha1 = imageReceiver2.getAlpha() - 0.08f;
                        if (alpha1 <= 0.0f) {
                            imageReceiver2.onDetachedFromWindow();
                            ReactionsLayoutInBubble.this.animatedReactions.remove(this.reaction);
                        } else {
                            imageReceiver2.setAlpha(alpha1);
                        }
                        ReactionsLayoutInBubble.this.parentView.invalidate();
                    }
                    imageReceiver2.setImageCoords(this.imageReceiver.getImageX() - (this.imageReceiver.getImageWidth() / 2.0f), this.imageReceiver.getImageY() - (this.imageReceiver.getImageWidth() / 2.0f), this.imageReceiver.getImageWidth() * 2.0f, this.imageReceiver.getImageHeight() * 2.0f);
                    imageReceiver2.draw(canvas);
                }
                if (drawStaticImage) {
                    this.imageReceiver.draw(canvas);
                }
                this.lastImageDrawn = true;
                return;
            }
            this.imageReceiver.setAlpha(0.0f);
            this.imageReceiver.draw(canvas);
            this.lastImageDrawn = false;
        }

        public void setUsers(ArrayList<TLRPC.User> users) {
            this.users = users;
            if (users != null) {
                Collections.sort(users, ReactionsLayoutInBubble.usersComparator);
                if (this.avatarsDarawable == null) {
                    AvatarsDarawable avatarsDarawable = new AvatarsDarawable(ReactionsLayoutInBubble.this.parentView, false);
                    this.avatarsDarawable = avatarsDarawable;
                    avatarsDarawable.transitionDuration = 250L;
                    this.avatarsDarawable.transitionInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
                    this.avatarsDarawable.setSize(AndroidUtilities.dp(20.0f));
                    this.avatarsDarawable.width = AndroidUtilities.dp(100.0f);
                    this.avatarsDarawable.height = this.height;
                    if (ReactionsLayoutInBubble.this.attached) {
                        this.avatarsDarawable.onAttachedToWindow();
                    }
                }
                for (int i = 0; i < users.size() && i != 3; i++) {
                    this.avatarsDarawable.setObject(i, ReactionsLayoutInBubble.this.currentAccount, users.get(i));
                }
                this.avatarsDarawable.commitTransition(false);
            }
        }

        public void attach() {
            ImageReceiver imageReceiver = this.imageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onAttachedToWindow();
            }
            AvatarsDarawable avatarsDarawable = this.avatarsDarawable;
            if (avatarsDarawable != null) {
                avatarsDarawable.onAttachedToWindow();
            }
        }

        public void detach() {
            ImageReceiver imageReceiver = this.imageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onDetachedFromWindow();
            }
            AvatarsDarawable avatarsDarawable = this.avatarsDarawable;
            if (avatarsDarawable != null) {
                avatarsDarawable.onDetachedFromWindow();
            }
        }
    }

    public boolean chekTouchEvent(MotionEvent event) {
        MessageObject messageObject;
        if (this.isEmpty || this.isSmall || (messageObject = this.messageObject) == null || messageObject.messageOwner == null || this.messageObject.messageOwner.reactions == null) {
            return false;
        }
        float x = event.getX() - this.x;
        float y = event.getY() - this.y;
        if (event.getAction() == 0) {
            int i = 0;
            int n = this.reactionButtons.size();
            while (true) {
                if (i >= n) {
                    break;
                } else if (x <= this.reactionButtons.get(i).x || x >= this.reactionButtons.get(i).x + this.reactionButtons.get(i).width || y <= this.reactionButtons.get(i).y || y >= this.reactionButtons.get(i).y + this.reactionButtons.get(i).height) {
                    i++;
                } else {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    this.lastSelectedButton = this.reactionButtons.get(i);
                    Runnable runnable = this.longPressRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.longPressRunnable = null;
                    }
                    final ReactionButton selectedButtonFinal = this.lastSelectedButton;
                    if (this.messageObject.messageOwner.reactions.can_see_list) {
                        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ReactionsLayoutInBubble.this.m2946xed008495(selectedButtonFinal);
                            }
                        };
                        this.longPressRunnable = runnable2;
                        AndroidUtilities.runOnUIThread(runnable2, ViewConfiguration.getLongPressTimeout());
                    }
                    this.pressed = true;
                }
            }
        } else if (event.getAction() == 2) {
            if ((this.pressed && Math.abs(event.getX() - this.lastX) > this.touchSlop) || Math.abs(event.getY() - this.lastY) > this.touchSlop) {
                this.pressed = false;
                this.lastSelectedButton = null;
                Runnable runnable3 = this.longPressRunnable;
                if (runnable3 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable3);
                    this.longPressRunnable = null;
                }
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            Runnable runnable4 = this.longPressRunnable;
            if (runnable4 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable4);
                this.longPressRunnable = null;
            }
            if (this.pressed && this.lastSelectedButton != null && event.getAction() == 1 && this.parentView.getDelegate() != null) {
                this.parentView.getDelegate().didPressReaction(this.parentView, this.lastSelectedButton.reactionCount, false);
            }
            this.pressed = false;
            this.lastSelectedButton = null;
        }
        return this.pressed;
    }

    /* renamed from: lambda$chekTouchEvent$1$org-telegram-ui-Components-Reactions-ReactionsLayoutInBubble */
    public /* synthetic */ void m2946xed008495(ReactionButton selectedButtonFinal) {
        this.parentView.getDelegate().didPressReaction(this.parentView, selectedButtonFinal.reactionCount, true);
        this.longPressRunnable = null;
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }

    public Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint2 = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint2 != null ? paint2 : Theme.getThemePaint(paintKey);
    }

    public float getCurrentWidth(float transitionProgress) {
        if (this.animateWidth) {
            return (this.fromWidth * (1.0f - transitionProgress)) + (this.width * transitionProgress);
        }
        return this.width;
    }

    public float getCurrentTotalHeight(float transitionProgress) {
        if (this.animateHeight) {
            return (this.animateFromTotalHeight * (1.0f - transitionProgress)) + (this.totalHeight * transitionProgress);
        }
        return this.totalHeight;
    }

    /* loaded from: classes5.dex */
    public static class ButtonsComparator implements Comparator<ReactionButton> {
        int currentAccount;

        private ButtonsComparator() {
        }

        public int compare(ReactionButton o1, ReactionButton o2) {
            if (o1.realCount != o2.realCount) {
                return o2.realCount - o1.realCount;
            }
            TLRPC.TL_availableReaction availableReaction1 = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(o1.reaction);
            TLRPC.TL_availableReaction availableReaction2 = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(o2.reaction);
            if (availableReaction1 != null && availableReaction2 != null) {
                return availableReaction1.positionInList - availableReaction2.positionInList;
            }
            return 0;
        }
    }

    public void onAttachToWindow() {
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).attach();
        }
    }

    public void onDetachFromWindow() {
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        if (!this.animatedReactions.isEmpty()) {
            for (ImageReceiver imageReceiver : this.animatedReactions.values()) {
                imageReceiver.onDetachedFromWindow();
            }
        }
        this.animatedReactions.clear();
    }

    public void animateReaction(String reaction) {
        TLRPC.TL_availableReaction r;
        if (this.animatedReactions.get(reaction) == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setParentView(this.parentView);
            int i = animationUniq;
            animationUniq = i + 1;
            imageReceiver.setUniqKeyPrefix(Integer.toString(i));
            if (reaction != null && (r = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(reaction)) != null) {
                imageReceiver.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_nolimit", null, "tgs", r, 1);
            }
            imageReceiver.setAutoRepeat(0);
            imageReceiver.onAttachedToWindow();
            this.animatedReactions.put(reaction, imageReceiver);
        }
    }
}
