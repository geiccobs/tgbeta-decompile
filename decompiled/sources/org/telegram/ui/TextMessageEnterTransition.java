package org.telegram.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.MessageEnterTransitionContainer;
/* loaded from: classes4.dex */
public class TextMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    private int animationIndex;
    private ValueAnimator animator;
    boolean changeColor;
    private ChatActivity chatActivity;
    MessageEnterTransitionContainer container;
    boolean crossfade;
    Bitmap crossfadeTextBitmap;
    float crossfadeTextOffset;
    MessageObject currentMessageObject;
    boolean drawBitmaps;
    private float drawableFromBottom;
    float drawableFromTop;
    ChatActivityEnterView enterView;
    int fromColor;
    Drawable fromMessageDrawable;
    float fromRadius;
    private float fromStartX;
    private float fromStartY;
    private Matrix gradientMatrix;
    private Paint gradientPaint;
    private LinearGradient gradientShader;
    boolean hasReply;
    float lastMessageX;
    float lastMessageY;
    StaticLayout layout;
    RecyclerListView listView;
    private int messageId;
    ChatMessageCell messageView;
    float progress;
    int replayFromColor;
    int replayObjectFromColor;
    float replyFromObjectStartY;
    float replyFromStartX;
    float replyFromStartY;
    float replyMessageDx;
    float replyNameDx;
    private final Theme.ResourcesProvider resourcesProvider;
    StaticLayout rtlLayout;
    private float scaleFrom;
    private float scaleY;
    Bitmap textLayoutBitmap;
    Bitmap textLayoutBitmapRtl;
    MessageObject.TextLayoutBlock textLayoutBlock;
    float textX;
    float textY;
    int toColor;
    float toXOffset;
    float toXOffsetRtl;
    Paint bitmapPaint = new Paint(1);
    boolean initBitmaps = false;
    private final int currentAccount = UserConfig.selectedAccount;

    /* JADX WARN: Can't wrap try/catch for region: R(44:15|(1:17)|18|(36:20|(1:22)(2:23|(1:25)(2:26|(1:28)))|30|(3:32|(3:35|(2:151|37)(1:38)|33)|152)(1:39)|40|41|(3:45|(1:47)(1:48)|49)(1:44)|50|(1:52)(1:53)|54|(4:57|(2:59|154)(1:155)|60|55)|153|61|(1:63)|64|(1:66)|67|(1:69)(1:70)|71|(3:73|(1:(4:75|(1:77)(1:78)|79|(2:156|81)(1:82))(2:157|83))|84)(1:85)|86|(1:103)(5:90|(3:92|(2:94|(2:96|160)(1:161))(2:97|159)|98)|158|99|(1:101)(1:102))|104|150|105|(4:107|(1:109)|110|(2:112|(1:114)(1:115)))|118|(1:123)(1:122)|124|(1:126)|127|(1:133)|134|(1:140)|141|(1:163)(2:145|146))|29|30|(0)(0)|40|41|(0)|45|(0)(0)|49|50|(0)(0)|54|(1:55)|153|61|(0)|64|(0)|67|(0)(0)|71|(0)(0)|86|(0)|103|104|150|105|(0)|118|(1:120)|123|124|(0)|127|(3:129|131|133)|134|(3:136|138|140)|141|(2:143|163)(1:162)) */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x0523, code lost:
        r36.drawBitmaps = false;
     */
    /* JADX WARN: Removed duplicated region for block: B:107:0x04a5 A[Catch: Exception -> 0x0522, TryCatch #0 {Exception -> 0x0522, blocks: (B:105:0x04a1, B:107:0x04a5, B:109:0x04c9, B:110:0x04e8, B:112:0x04ec, B:114:0x04f6, B:115:0x050a), top: B:150:0x04a1 }] */
    /* JADX WARN: Removed duplicated region for block: B:126:0x053b  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0165  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x01a6  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01ef  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0211  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x02b4  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x02cb  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x02f3  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0343  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x034b  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0365  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x039d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public TextMessageEnterTransition(final org.telegram.ui.Cells.ChatMessageCell r37, final org.telegram.ui.ChatActivity r38, org.telegram.ui.Components.RecyclerListView r39, final org.telegram.ui.MessageEnterTransitionContainer r40, org.telegram.ui.ActionBar.Theme.ResourcesProvider r41) {
        /*
            Method dump skipped, instructions count: 1774
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TextMessageEnterTransition.<init>(org.telegram.ui.Cells.ChatMessageCell, org.telegram.ui.ChatActivity, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.MessageEnterTransitionContainer, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-TextMessageEnterTransition */
    public /* synthetic */ void m4614lambda$new$0$orgtelegramuiTextMessageEnterTransition(ChatActivityEnterView chatActivityEnterView, MessageEnterTransitionContainer container, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatActivityEnterView.getEditField().setAlpha(this.progress);
        container.invalidate();
    }

    public void start() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    private boolean isRtlLine(Layout layout, int line) {
        return layout.getLineRight(line) == ((float) layout.getWidth()) && layout.getLineLeft(line) != 0.0f;
    }

    @Override // org.telegram.ui.MessageEnterTransitionContainer.Transition
    public void onDraw(Canvas canvas) {
        float alphaProgress;
        int clipBottom;
        float progress;
        float messageViewX;
        float fromX;
        boolean z;
        float alphaProgress2;
        float drawableTop;
        float drawableBottom;
        int drawableRight;
        float scale2;
        int replyOwnerMessageColor;
        int replyOwnerMessageColor2;
        float fromReplayX;
        float replyY;
        float replyToMessageX;
        Drawable drawable;
        if (this.drawBitmaps && !this.initBitmaps && this.crossfadeTextBitmap != null && this.messageView.getTransitionParams().wasDraw) {
            this.initBitmaps = true;
            Canvas bitmapCanvas = new Canvas(this.crossfadeTextBitmap);
            bitmapCanvas.translate(0.0f, this.crossfadeTextOffset);
            ChatMessageCell chatMessageCell = this.messageView;
            chatMessageCell.drawMessageText(bitmapCanvas, chatMessageCell.getMessageObject().textLayoutBlocks, true, 1.0f, true);
        }
        float listViewBottom = (this.listView.getY() - this.container.getY()) + this.listView.getMeasuredHeight();
        float fromX2 = this.fromStartX - this.container.getX();
        float fromY = this.fromStartY - this.container.getY();
        this.textX = this.messageView.getTextX();
        this.textY = this.messageView.getTextY();
        if (this.messageView.getMessageObject().stableId == this.messageId) {
            float messageViewX2 = (this.messageView.getX() + this.listView.getX()) - this.container.getX();
            float messageViewY = ((this.messageView.getTop() + this.listView.getTop()) - this.container.getY()) + this.enterView.getTopViewHeight();
            this.lastMessageX = messageViewX2;
            this.lastMessageY = messageViewY;
            float progress2 = ChatListItemAnimator.DEFAULT_INTERPOLATOR.getInterpolation(this.progress);
            float f = this.progress;
            float alphaProgress3 = f > 0.4f ? 1.0f : f / 0.4f;
            float p2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.progress);
            float progressX = CubicBezierInterpolator.EASE_OUT.getInterpolation(p2);
            float toX = messageViewX2 + this.textX;
            float toY = messageViewY + this.textY;
            int clipBottom2 = (int) ((this.container.getMeasuredHeight() * (1.0f - progressX)) + (listViewBottom * progressX));
            boolean messageViewOverscrolled = this.messageView.getBottom() - AndroidUtilities.dp(4.0f) > this.listView.getMeasuredHeight();
            boolean clipBottomWithAlpha = messageViewOverscrolled && (((float) this.messageView.getMeasuredHeight()) + messageViewY) - ((float) AndroidUtilities.dp(8.0f)) > ((float) clipBottom2) && this.container.getMeasuredHeight() > 0;
            if (!clipBottomWithAlpha) {
                clipBottom = clipBottom2;
                alphaProgress = alphaProgress3;
                progress = progress2;
            } else {
                float p22 = Math.max(0.0f, messageViewY);
                clipBottom = clipBottom2;
                alphaProgress = alphaProgress3;
                progress = progress2;
                canvas.saveLayerAlpha(0.0f, p22, this.container.getMeasuredWidth(), this.container.getMeasuredHeight(), 255, 31);
            }
            canvas.save();
            canvas.clipRect(0.0f, ((this.listView.getY() + this.chatActivity.getChatListViewPadding()) - this.container.getY()) - AndroidUtilities.dp(3.0f), this.container.getMeasuredWidth(), this.container.getMeasuredHeight());
            canvas.save();
            float drawableX = this.messageView.getBackgroundDrawableLeft() + messageViewX2 + ((fromX2 - (toX - this.toXOffset)) * (1.0f - progressX));
            float drawableToTop = messageViewY + this.messageView.getBackgroundDrawableTop();
            float drawableTop2 = ((this.drawableFromTop - this.container.getY()) * (1.0f - progress)) + (drawableToTop * progress);
            float drawableH = this.messageView.getBackgroundDrawableBottom() - this.messageView.getBackgroundDrawableTop();
            float drawableBottom2 = ((this.drawableFromBottom - this.container.getY()) * (1.0f - progress)) + ((drawableToTop + drawableH) * progress);
            int drawableRight2 = (int) (this.messageView.getBackgroundDrawableRight() + messageViewX2 + (AndroidUtilities.dp(4.0f) * (1.0f - progressX)));
            Theme.MessageDrawable drawable2 = this.messageView.getCurrentBackgroundDrawable(true);
            if (drawable2 != null) {
                this.messageView.setBackgroundTopY(this.container.getTop() - this.listView.getTop());
                Drawable shadowDrawable = drawable2.getShadowDrawable();
                alphaProgress2 = alphaProgress;
                if (alphaProgress2 == 1.0f || (drawable = this.fromMessageDrawable) == null) {
                    fromX = fromX2;
                    messageViewX = messageViewX2;
                } else {
                    fromX = fromX2;
                    messageViewX = messageViewX2;
                    drawable.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                    this.fromMessageDrawable.draw(canvas);
                }
                if (shadowDrawable != null) {
                    shadowDrawable.setAlpha((int) (progressX * 255.0f));
                    shadowDrawable.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                    shadowDrawable.draw(canvas);
                    shadowDrawable.setAlpha(255);
                }
                drawable2.setAlpha((int) (alphaProgress2 * 255.0f));
                drawable2.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                drawable2.setDrawFullBubble(true);
                drawable2.draw(canvas);
                z = false;
                drawable2.setDrawFullBubble(false);
                drawable2.setAlpha(255);
            } else {
                fromX = fromX2;
                messageViewX = messageViewX2;
                alphaProgress2 = alphaProgress;
                z = false;
            }
            canvas.restore();
            canvas.save();
            if (this.currentMessageObject.isOutOwner()) {
                canvas.clipRect(AndroidUtilities.dp(4.0f) + drawableX, AndroidUtilities.dp(4.0f) + drawableTop2, drawableRight2 - AndroidUtilities.dp(10.0f), drawableBottom2 - AndroidUtilities.dp(4.0f));
            } else {
                canvas.clipRect(AndroidUtilities.dp(4.0f) + drawableX, AndroidUtilities.dp(4.0f) + drawableTop2, drawableRight2 - AndroidUtilities.dp(4.0f), drawableBottom2 - AndroidUtilities.dp(4.0f));
            }
            canvas.translate((this.messageView.getLeft() + this.listView.getX()) - this.container.getX(), ((fromY - toY) * (1.0f - progress)) + messageViewY);
            this.messageView.drawTime(canvas, alphaProgress2, z);
            this.messageView.drawNamesLayout(canvas, alphaProgress2);
            this.messageView.drawCommentButton(canvas, alphaProgress2);
            this.messageView.drawCaptionLayout(canvas, z, alphaProgress2);
            this.messageView.drawLinkPreview(canvas, alphaProgress2);
            canvas.restore();
            if (this.hasReply) {
                this.chatActivity.getReplyNameTextView().setAlpha(0.0f);
                this.chatActivity.getReplyObjectTextView().setAlpha(0.0f);
                float fromReplayX2 = this.replyFromStartX - this.container.getX();
                float fromReplayY = this.replyFromStartY - this.container.getY();
                float toReplayX = messageViewX + this.messageView.replyStartX;
                float toReplayY = messageViewY + this.messageView.replyStartY;
                int replyMessageColor = (!this.currentMessageObject.hasValidReplyMessageObject() || (this.currentMessageObject.replyMessageObject.type != 0 && TextUtils.isEmpty(this.currentMessageObject.replyMessageObject.caption)) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) ? getThemedColor(Theme.key_chat_outReplyMediaMessageText) : getThemedColor(Theme.key_chat_outReplyMessageText);
                if (this.currentMessageObject.isOutOwner()) {
                    int replyOwnerMessageColor3 = getThemedColor(Theme.key_chat_outReplyNameText);
                    replyOwnerMessageColor = getThemedColor(Theme.key_chat_outReplyLine);
                    replyOwnerMessageColor2 = replyOwnerMessageColor3;
                } else {
                    int replyOwnerMessageColor4 = getThemedColor(Theme.key_chat_inReplyNameText);
                    replyOwnerMessageColor = getThemedColor(Theme.key_chat_inReplyLine);
                    replyOwnerMessageColor2 = replyOwnerMessageColor4;
                }
                drawableRight = drawableRight2;
                drawableBottom = drawableBottom2;
                Theme.chat_replyTextPaint.setColor(ColorUtils.blendARGB(this.replayObjectFromColor, replyMessageColor, progress));
                Theme.chat_replyNamePaint.setColor(ColorUtils.blendARGB(this.replayFromColor, replyOwnerMessageColor2, progress));
                if (!this.messageView.needReplyImage) {
                    fromReplayX = fromReplayX2;
                } else {
                    fromReplayX = fromReplayX2 - AndroidUtilities.dp(44.0f);
                }
                float replyX = ((1.0f - progressX) * fromReplayX) + (toReplayX * progressX);
                float replyY2 = (toReplayY * progress) + (((AndroidUtilities.dp(12.0f) * progress) + fromReplayY) * (1.0f - progress));
                Theme.chat_replyLinePaint.setColor(ColorUtils.setAlphaComponent(replyOwnerMessageColor, (int) (Color.alpha(replyOwnerMessageColor) * progressX)));
                drawableTop = drawableTop2;
                canvas.drawRect(replyX, replyY2, replyX + AndroidUtilities.dp(2.0f), replyY2 + AndroidUtilities.dp(35.0f), Theme.chat_replyLinePaint);
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) * progressX, 0.0f);
                if (!this.messageView.needReplyImage) {
                    replyY = replyY2;
                } else {
                    canvas.save();
                    replyY = replyY2;
                    this.messageView.replyImageReceiver.setImageCoords(replyX, replyY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                    this.messageView.replyImageReceiver.draw(canvas);
                    canvas.translate(replyX, replyY);
                    canvas.restore();
                    canvas.translate(AndroidUtilities.dp(44.0f), 0.0f);
                }
                float f2 = this.replyMessageDx;
                float replyToMessageX2 = toReplayX - f2;
                float replyToNameX = toReplayX - this.replyNameDx;
                float replyMessageX = ((fromReplayX - f2) * (1.0f - progressX)) + (replyToMessageX2 * progressX);
                float replyNameX = ((1.0f - progressX) * fromReplayX) + (replyToNameX * progressX);
                if (this.messageView.replyNameLayout != null) {
                    canvas.save();
                    canvas.translate(replyNameX, replyY);
                    this.messageView.replyNameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.messageView.replyTextLayout != null) {
                    canvas.save();
                    canvas.translate(replyMessageX, AndroidUtilities.dp(19.0f) + replyY);
                    canvas.save();
                    SpoilerEffect.clipOutCanvas(canvas, this.messageView.replySpoilers);
                    this.messageView.replyTextLayout.draw(canvas);
                    canvas.restore();
                    for (SpoilerEffect eff : this.messageView.replySpoilers) {
                        float replyMessageX2 = replyMessageX;
                        if (eff.shouldInvalidateColor()) {
                            replyToMessageX = replyToMessageX2;
                            eff.setColor(this.messageView.replyTextLayout.getPaint().getColor());
                        } else {
                            replyToMessageX = replyToMessageX2;
                        }
                        eff.draw(canvas);
                        replyToMessageX2 = replyToMessageX;
                        replyMessageX = replyMessageX2;
                    }
                    canvas.restore();
                }
                canvas.restore();
            } else {
                drawableRight = drawableRight2;
                drawableBottom = drawableBottom2;
                drawableTop = drawableTop2;
            }
            canvas.save();
            canvas.clipRect(AndroidUtilities.dp(4.0f) + drawableX, drawableTop + AndroidUtilities.dp(4.0f), drawableRight - AndroidUtilities.dp(4.0f), drawableBottom - AndroidUtilities.dp(4.0f));
            float scale = progressX + (this.scaleFrom * (1.0f - progressX));
            if (this.drawBitmaps) {
                scale2 = progressX + (this.scaleY * (1.0f - progressX));
            } else {
                scale2 = 1.0f;
            }
            canvas.save();
            canvas.translate(((1.0f - progressX) * fromX) + ((toX - this.toXOffset) * progressX), ((1.0f - progress) * fromY) + ((toY + this.textLayoutBlock.textYOffset) * progress));
            canvas.scale(scale, scale * scale2, 0.0f, 0.0f);
            if (this.drawBitmaps) {
                if (this.crossfade) {
                    this.bitmapPaint.setAlpha((int) ((1.0f - alphaProgress2) * 255.0f));
                }
                canvas.drawBitmap(this.textLayoutBitmap, 0.0f, 0.0f, this.bitmapPaint);
            } else {
                boolean z2 = this.crossfade;
                if (z2 && this.changeColor) {
                    int oldColor = this.layout.getPaint().getColor();
                    this.layout.getPaint().setColor(ColorUtils.setAlphaComponent(ColorUtils.blendARGB(this.fromColor, this.toColor, alphaProgress2), (int) (Color.alpha(oldColor) * (1.0f - alphaProgress2))));
                    this.layout.draw(canvas);
                    this.layout.getPaint().setColor(oldColor);
                } else if (z2) {
                    int oldAlpha = Theme.chat_msgTextPaint.getAlpha();
                    Theme.chat_msgTextPaint.setAlpha((int) (oldAlpha * (1.0f - alphaProgress2)));
                    this.layout.draw(canvas);
                    Theme.chat_msgTextPaint.setAlpha(oldAlpha);
                } else {
                    this.layout.draw(canvas);
                }
            }
            canvas.restore();
            if (this.rtlLayout != null) {
                canvas.save();
                canvas.translate(((1.0f - progressX) * fromX) + ((toX - this.toXOffsetRtl) * progressX), ((1.0f - progress) * fromY) + ((toY + this.textLayoutBlock.textYOffset) * progress));
                canvas.scale(scale, scale * scale2, 0.0f, 0.0f);
                if (this.drawBitmaps) {
                    if (this.crossfade) {
                        this.bitmapPaint.setAlpha((int) ((1.0f - alphaProgress2) * 255.0f));
                    }
                    canvas.drawBitmap(this.textLayoutBitmapRtl, 0.0f, 0.0f, this.bitmapPaint);
                } else {
                    boolean z3 = this.crossfade;
                    if (z3 && this.changeColor) {
                        int oldColor2 = this.rtlLayout.getPaint().getColor();
                        this.rtlLayout.getPaint().setColor(ColorUtils.setAlphaComponent(ColorUtils.blendARGB(this.fromColor, this.toColor, alphaProgress2), (int) (Color.alpha(oldColor2) * (1.0f - alphaProgress2))));
                        this.rtlLayout.draw(canvas);
                        this.rtlLayout.getPaint().setColor(oldColor2);
                    } else if (z3) {
                        int oldAlpha2 = this.rtlLayout.getPaint().getAlpha();
                        this.rtlLayout.getPaint().setAlpha((int) (oldAlpha2 * (1.0f - alphaProgress2)));
                        this.rtlLayout.draw(canvas);
                        this.rtlLayout.getPaint().setAlpha(oldAlpha2);
                    } else {
                        this.rtlLayout.draw(canvas);
                    }
                }
                canvas.restore();
            }
            if (this.crossfade) {
                canvas.save();
                canvas.translate(((this.messageView.getLeft() + this.listView.getX()) - this.container.getX()) + ((fromX - toX) * (1.0f - progressX)), ((fromY - toY) * (1.0f - progress)) + messageViewY);
                canvas.scale(scale, scale * scale2, this.messageView.getTextX(), this.messageView.getTextY());
                canvas.translate(0.0f, -this.crossfadeTextOffset);
                if (this.crossfadeTextBitmap != null) {
                    this.bitmapPaint.setAlpha((int) (alphaProgress2 * 255.0f));
                    canvas.drawBitmap(this.crossfadeTextBitmap, 0.0f, 0.0f, this.bitmapPaint);
                } else {
                    int oldColor3 = Theme.chat_msgTextPaint.getColor();
                    Theme.chat_msgTextPaint.setColor(this.toColor);
                    ChatMessageCell chatMessageCell2 = this.messageView;
                    chatMessageCell2.drawMessageText(canvas, chatMessageCell2.getMessageObject().textLayoutBlocks, false, alphaProgress2, true);
                    if (Theme.chat_msgTextPaint.getColor() != oldColor3) {
                        Theme.chat_msgTextPaint.setColor(oldColor3);
                    }
                }
                canvas.restore();
            }
            canvas.restore();
            if (clipBottomWithAlpha) {
                int clipBottom3 = clipBottom;
                this.gradientMatrix.setTranslate(0.0f, clipBottom3);
                this.gradientShader.setLocalMatrix(this.gradientMatrix);
                canvas.drawRect(0.0f, clipBottom3, this.container.getMeasuredWidth(), this.container.getMeasuredHeight(), this.gradientPaint);
                canvas.restore();
            }
            float f3 = this.progress;
            float sendProgress = f3 > 0.4f ? 1.0f : f3 / 0.4f;
            if (sendProgress == 1.0f) {
                this.enterView.setTextTransitionIsRunning(false);
            }
            if (this.enterView.getSendButton().getVisibility() == 0 && sendProgress < 1.0f) {
                canvas.save();
                canvas.translate(((((this.enterView.getX() + this.enterView.getSendButton().getX()) + ((View) this.enterView.getSendButton().getParent()).getX()) + ((View) this.enterView.getSendButton().getParent().getParent()).getX()) - this.container.getX()) + (AndroidUtilities.dp(52.0f) * sendProgress), (((this.enterView.getY() + this.enterView.getSendButton().getY()) + ((View) this.enterView.getSendButton().getParent()).getY()) + ((View) this.enterView.getSendButton().getParent().getParent()).getY()) - this.container.getY());
                this.enterView.getSendButton().draw(canvas);
                canvas.restore();
                canvas.restore();
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
