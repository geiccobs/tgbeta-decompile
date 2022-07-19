package org.telegram.ui.Components.Premium;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentGiftPremium;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class GiftPremiumBottomSheet extends BottomSheetWithRecyclerListView {
    private int buttonRow;
    private int footerRow;
    private PremiumGradient.GradientTools gradientTools;
    private int headerRow;
    private PremiumGradient.GradientTools outlineGradient;
    private PremiumButtonView premiumButtonView;
    private int rowsCount;
    private int selectedTierIndex;
    private int tiersEndRow;
    private int tiersStartRow;
    private int totalGradientHeight;
    private TLRPC$User user;
    private List<GiftTier> giftTiers = new ArrayList();
    private PremiumGiftTierCell dummyCell = new PremiumGiftTierCell(getContext());

    @SuppressLint({"NotifyDataSetChanged"})
    public GiftPremiumBottomSheet(BaseFragment baseFragment, TLRPC$User tLRPC$User) {
        super(baseFragment, false, true);
        this.selectedTierIndex = 0;
        this.user = tLRPC$User;
        PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", null, null);
        this.gradientTools = gradientTools;
        gradientTools.exactly = true;
        gradientTools.x1 = 0.0f;
        gradientTools.y1 = 0.0f;
        gradientTools.x2 = 0.0f;
        gradientTools.y2 = 1.0f;
        gradientTools.cx = 0.0f;
        gradientTools.cy = 0.0f;
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.outlineGradient = gradientTools2;
        gradientTools2.paint.setStyle(Paint.Style.STROKE);
        this.outlineGradient.paint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        if (userFull != null) {
            ArrayList arrayList = new ArrayList();
            Iterator<TLRPC$TL_premiumGiftOption> it = userFull.premium_gifts.iterator();
            while (it.hasNext()) {
                GiftTier giftTier = new GiftTier(it.next());
                this.giftTiers.add(giftTier);
                if (!BuildVars.useInvoiceBilling() && giftTier.giftOption.store_product != null) {
                    arrayList.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(giftTier.giftOption.store_product).build());
                }
            }
            if (!arrayList.isEmpty()) {
                BillingController.getInstance().queryProductDetails(arrayList, new ProductDetailsResponseListener() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda3
                    @Override // com.android.billingclient.api.ProductDetailsResponseListener
                    public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                        GiftPremiumBottomSheet.this.lambda$new$1(billingResult, list);
                    }
                });
            }
        }
        if (!this.giftTiers.isEmpty()) {
            this.selectedTierIndex = 0;
            updateButtonText(false);
        }
        int i = this.rowsCount;
        int i2 = i + 1;
        this.rowsCount = i2;
        this.headerRow = i;
        this.tiersStartRow = i2;
        int size = i2 + this.giftTiers.size();
        this.rowsCount = size;
        this.tiersEndRow = size;
        int i3 = size + 1;
        this.rowsCount = i3;
        this.footerRow = size;
        this.rowsCount = i3 + 1;
        this.buttonRow = i3;
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i4) {
                GiftPremiumBottomSheet.this.lambda$new$2(view, i4);
            }
        });
        this.recyclerListView.setOverScrollMode(2);
        final Path path = new Path();
        this.recyclerListView.setSelectorTransformer(new Consumer() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda2
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                GiftPremiumBottomSheet.this.lambda$new$3(path, (Canvas) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$1(BillingResult billingResult, List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ProductDetails productDetails = (ProductDetails) it.next();
            Iterator<GiftTier> it2 = this.giftTiers.iterator();
            while (true) {
                if (it2.hasNext()) {
                    GiftTier next = it2.next();
                    String str = next.giftOption.store_product;
                    if (str != null && str.equals(productDetails.getProductId())) {
                        next.setGooglePlayProductDetails(productDetails);
                        break;
                    }
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GiftPremiumBottomSheet.this.lambda$new$0();
            }
        });
    }

    public /* synthetic */ void lambda$new$0() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
        updateButtonText(false);
    }

    public /* synthetic */ void lambda$new$2(View view, int i) {
        if (view instanceof PremiumGiftTierCell) {
            PremiumGiftTierCell premiumGiftTierCell = (PremiumGiftTierCell) view;
            this.selectedTierIndex = this.giftTiers.indexOf(premiumGiftTierCell.tier);
            updateButtonText(true);
            premiumGiftTierCell.setChecked(true, true);
            for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
                View childAt = this.recyclerListView.getChildAt(i2);
                if (childAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell2 = (PremiumGiftTierCell) childAt;
                    if (premiumGiftTierCell2.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell2.setChecked(false, true);
                    }
                }
            }
            for (int i3 = 0; i3 < this.recyclerListView.getHiddenChildCount(); i3++) {
                View hiddenChildAt = this.recyclerListView.getHiddenChildAt(i3);
                if (hiddenChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell3 = (PremiumGiftTierCell) hiddenChildAt;
                    if (premiumGiftTierCell3.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell3.setChecked(false, true);
                    }
                }
            }
            for (int i4 = 0; i4 < this.recyclerListView.getCachedChildCount(); i4++) {
                View cachedChildAt = this.recyclerListView.getCachedChildAt(i4);
                if (cachedChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell4 = (PremiumGiftTierCell) cachedChildAt;
                    if (premiumGiftTierCell4.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell4.setChecked(false, true);
                    }
                }
            }
            for (int i5 = 0; i5 < this.recyclerListView.getAttachedScrapChildCount(); i5++) {
                View attachedScrapChildAt = this.recyclerListView.getAttachedScrapChildAt(i5);
                if (attachedScrapChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell5 = (PremiumGiftTierCell) attachedScrapChildAt;
                    if (premiumGiftTierCell5.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell5.setChecked(false, true);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3(Path path, Canvas canvas) {
        path.rewind();
        Rect selectorRect = this.recyclerListView.getSelectorRect();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(selectorRect.left + AndroidUtilities.dp(20.0f), selectorRect.top + AndroidUtilities.dp(3.0f), selectorRect.right - AndroidUtilities.dp(20.0f), selectorRect.bottom - AndroidUtilities.dp(3.0f));
        path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
        canvas.clipPath(path);
    }

    public /* synthetic */ void lambda$updateButtonText$4(View view) {
        onGiftPremium();
    }

    private void updateButtonText(boolean z) {
        this.premiumButtonView.setButton(LocaleController.formatString(R.string.GiftSubscriptionFor, this.giftTiers.get(this.selectedTierIndex).getFormattedPrice()), new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GiftPremiumBottomSheet.this.lambda$updateButtonText$4(view);
            }
        }, z);
    }

    private void onGiftPremium() {
        final GiftTier giftTier = this.giftTiers.get(this.selectedTierIndex);
        if (BuildVars.useInvoiceBilling()) {
            if (!(getBaseFragment().getParentActivity() instanceof LaunchActivity)) {
                return;
            }
            Uri parse = Uri.parse(giftTier.giftOption.bot_url);
            if (parse.getHost().equals("t.me") && !parse.getPath().startsWith("/$")) {
                ((LaunchActivity) getBaseFragment().getParentActivity()).setNavigateToPremiumBot(true);
            }
            Browser.openUrl(getBaseFragment().getParentActivity(), giftTier.giftOption.bot_url);
        } else if (!BillingController.getInstance().isReady() || giftTier.googlePlayProductDetails == null) {
        } else {
            final TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium = new TLRPC$TL_inputStorePaymentGiftPremium();
            tLRPC$TL_inputStorePaymentGiftPremium.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user);
            ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = giftTier.googlePlayProductDetails.getOneTimePurchaseOfferDetails();
            tLRPC$TL_inputStorePaymentGiftPremium.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
            double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
            double pow = Math.pow(10.0d, 6.0d);
            Double.isNaN(priceAmountMicros);
            tLRPC$TL_inputStorePaymentGiftPremium.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tLRPC$TL_inputStorePaymentGiftPremium.currency)));
            BillingController.getInstance().addResultListener(giftTier.giftOption.store_product, new Consumer() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    GiftPremiumBottomSheet.this.lambda$onGiftPremium$6((BillingResult) obj);
                }
            });
            final TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium = new TLRPC$TL_payments_canPurchasePremium();
            tLRPC$TL_payments_canPurchasePremium.purpose = tLRPC$TL_inputStorePaymentGiftPremium;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_canPurchasePremium, new RequestDelegate() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    GiftPremiumBottomSheet.this.lambda$onGiftPremium$8(tLRPC$TL_inputStorePaymentGiftPremium, giftTier, tLRPC$TL_payments_canPurchasePremium, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onGiftPremium$6(BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    GiftPremiumBottomSheet.this.lambda$onGiftPremium$5();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onGiftPremium$5() {
        if (getBaseFragment() != null) {
            getBaseFragment().finishFragment();
        }
    }

    public /* synthetic */ void lambda$onGiftPremium$8(final TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium, final GiftTier giftTier, final TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GiftPremiumBottomSheet.this.lambda$onGiftPremium$7(tLObject, tLRPC$TL_inputStorePaymentGiftPremium, giftTier, tLRPC$TL_error, tLRPC$TL_payments_canPurchasePremium);
            }
        });
    }

    public /* synthetic */ void lambda$onGiftPremium$7(TLObject tLObject, TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium, GiftTier giftTier, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(getBaseFragment().getParentActivity(), AccountInstance.getInstance(this.currentAccount), tLRPC$TL_inputStorePaymentGiftPremium, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(giftTier.googlePlayProductDetails).build()));
        } else if (tLRPC$TL_error == null) {
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, getBaseFragment(), tLRPC$TL_payments_canPurchasePremium, new Object[0]);
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        this.premiumButtonView = new PremiumButtonView(getContext(), true);
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        frameLayout2.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 68, 80));
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    private void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.giftTiers.size(); i4++) {
            this.dummyCell.bind(this.giftTiers.get(i4));
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.giftTiers.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected CharSequence getTitle() {
        return LocaleController.getString((int) R.string.GiftTelegramPremiumTitle);
    }

    /* renamed from: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1 */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 extends RecyclerListView.SelectionAdapter {
        AnonymousClass1() {
            GiftPremiumBottomSheet.this = r1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            PremiumGiftTierCell premiumGiftTierCell;
            if (i == 1) {
                final AtomicReference atomicReference = new AtomicReference(Float.valueOf(0.0f));
                final PremiumGiftTierCell premiumGiftTierCell2 = new PremiumGiftTierCell(GiftPremiumBottomSheet.this.getContext()) { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.1.1
                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(this.discountView.getLeft(), this.discountView.getTop(), this.discountView.getRight(), this.discountView.getBottom());
                        GiftPremiumBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), GiftPremiumBottomSheet.this.totalGradientHeight, 0.0f, -this.tier.yOffset);
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), GiftPremiumBottomSheet.this.gradientTools.paint);
                        float floatValue = ((Float) atomicReference.get()).floatValue();
                        int alpha = GiftPremiumBottomSheet.this.outlineGradient.paint.getAlpha();
                        GiftPremiumBottomSheet.this.outlineGradient.paint.setAlpha((int) (floatValue * alpha));
                        rectF.set(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(3.0f), getWidth() - AndroidUtilities.dp(20.0f), getHeight() - AndroidUtilities.dp(3.0f));
                        GiftPremiumBottomSheet.this.outlineGradient.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), 0.0f, 0.0f);
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), GiftPremiumBottomSheet.this.outlineGradient.paint);
                        GiftPremiumBottomSheet.this.outlineGradient.paint.setAlpha(alpha);
                        super.dispatchDraw(canvas);
                    }
                };
                premiumGiftTierCell2.setCirclePaintProvider(new GenericProvider() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda0
                    @Override // org.telegram.messenger.GenericProvider
                    public final Object provide(Object obj) {
                        Paint lambda$onCreateViewHolder$0;
                        lambda$onCreateViewHolder$0 = GiftPremiumBottomSheet.AnonymousClass1.this.lambda$onCreateViewHolder$0(premiumGiftTierCell2, (Void) obj);
                        return lambda$onCreateViewHolder$0;
                    }
                });
                premiumGiftTierCell2.setProgressDelegate(new CheckBoxBase.ProgressDelegate() { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.CheckBoxBase.ProgressDelegate
                    public final void setProgress(float f) {
                        GiftPremiumBottomSheet.AnonymousClass1.lambda$onCreateViewHolder$1(atomicReference, premiumGiftTierCell2, f);
                    }
                });
                premiumGiftTierCell = premiumGiftTierCell2;
            } else if (i == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(GiftPremiumBottomSheet.this.getContext());
                textInfoPrivacyCell.setTopPadding(28);
                textInfoPrivacyCell.getTextView().setGravity(1);
                String string = LocaleController.getString((int) R.string.GiftPremiumListFeaturesAndTerms);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (indexOf != -1 && lastIndexOf != -1) {
                    SpannableString spannableString = new SpannableString(string.substring(0, indexOf) + string.substring(indexOf + 1, lastIndexOf) + string.substring(lastIndexOf + 1));
                    spannableString.setSpan(new LinkSpan(GiftPremiumBottomSheet.this, null), indexOf, lastIndexOf - 1, 33);
                    textInfoPrivacyCell.setText(spannableString);
                } else {
                    textInfoPrivacyCell.setText(string);
                }
                textInfoPrivacyCell.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                premiumGiftTierCell = textInfoPrivacyCell;
            } else {
                if (i != 3) {
                    view = new PremiumGiftHeaderCell(GiftPremiumBottomSheet.this.getContext());
                } else {
                    view = new View(this, GiftPremiumBottomSheet.this.getContext()) { // from class: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.1.2
                        @Override // android.view.View
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), 1073741824));
                        }
                    };
                }
                return new RecyclerListView.Holder(view);
            }
            view = premiumGiftTierCell;
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ Paint lambda$onCreateViewHolder$0(PremiumGiftTierCell premiumGiftTierCell, Void r9) {
            GiftPremiumBottomSheet.this.gradientTools.gradientMatrix(0, 0, premiumGiftTierCell.getMeasuredWidth(), GiftPremiumBottomSheet.this.totalGradientHeight, 0.0f, -premiumGiftTierCell.tier.yOffset);
            return GiftPremiumBottomSheet.this.gradientTools.paint;
        }

        public static /* synthetic */ void lambda$onCreateViewHolder$1(AtomicReference atomicReference, PremiumGiftTierCell premiumGiftTierCell, float f) {
            atomicReference.set(Float.valueOf(f));
            premiumGiftTierCell.invalidate();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (i == GiftPremiumBottomSheet.this.headerRow) {
                ((PremiumGiftHeaderCell) viewHolder.itemView).bind(GiftPremiumBottomSheet.this.user);
            } else if (i < GiftPremiumBottomSheet.this.tiersStartRow || i >= GiftPremiumBottomSheet.this.tiersEndRow) {
            } else {
                PremiumGiftTierCell premiumGiftTierCell = (PremiumGiftTierCell) viewHolder.itemView;
                premiumGiftTierCell.bind((GiftTier) GiftPremiumBottomSheet.this.giftTiers.get(i - GiftPremiumBottomSheet.this.tiersStartRow));
                premiumGiftTierCell.setChecked(i - GiftPremiumBottomSheet.this.tiersStartRow == GiftPremiumBottomSheet.this.selectedTierIndex, false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == GiftPremiumBottomSheet.this.headerRow) {
                return 0;
            }
            if (i >= GiftPremiumBottomSheet.this.tiersStartRow && i < GiftPremiumBottomSheet.this.tiersEndRow) {
                return 1;
            }
            if (i == GiftPremiumBottomSheet.this.footerRow) {
                return 2;
            }
            return i == GiftPremiumBottomSheet.this.buttonRow ? 3 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return GiftPremiumBottomSheet.this.rowsCount;
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected RecyclerListView.SelectionAdapter createAdapter() {
        return new AnonymousClass1();
    }

    /* loaded from: classes3.dex */
    private final class LinkSpan extends ClickableSpan {
        private LinkSpan() {
            GiftPremiumBottomSheet.this = r1;
        }

        /* synthetic */ LinkSpan(GiftPremiumBottomSheet giftPremiumBottomSheet, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View view) {
            GiftPremiumBottomSheet.this.getBaseFragment().presentFragment(new PremiumPreviewFragment("profile"));
            GiftPremiumBottomSheet.this.dismiss();
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }
    }

    /* loaded from: classes3.dex */
    public static final class GiftTier {
        private int discount;
        public final TLRPC$TL_premiumGiftOption giftOption;
        private ProductDetails googlePlayProductDetails;
        private long pricePerMonth;
        public int yOffset;

        public GiftTier(TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption) {
            this.giftOption = tLRPC$TL_premiumGiftOption;
        }

        public void setGooglePlayProductDetails(ProductDetails productDetails) {
            this.googlePlayProductDetails = productDetails;
        }

        public int getMonths() {
            return this.giftOption.months;
        }

        public int getDiscount() {
            long j;
            if (this.discount == 0) {
                if (getPricePerMonth() == 0) {
                    return 0;
                }
                if (BuildVars.useInvoiceBilling()) {
                    MediaDataController.getInstance(UserConfig.selectedAccount).getPremiumPromo();
                    j = 499;
                } else {
                    j = 0;
                }
                ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
                if (productDetails != null) {
                    List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
                    if (!subscriptionOfferDetails.isEmpty()) {
                        Iterator<ProductDetails.PricingPhase> it = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            ProductDetails.PricingPhase next = it.next();
                            if (next.getBillingPeriod().equals("P1M")) {
                                j = next.getPriceAmountMicros();
                                break;
                            }
                        }
                    }
                }
                if (j != 0) {
                    double pricePerMonth = getPricePerMonth();
                    double d = j;
                    Double.isNaN(pricePerMonth);
                    Double.isNaN(d);
                    this.discount = (int) ((1.0d - (pricePerMonth / d)) * 100.0d);
                }
            }
            return this.discount;
        }

        public long getPricePerMonth() {
            if (this.pricePerMonth == 0) {
                long price = getPrice();
                if (price != 0) {
                    this.pricePerMonth = price / this.giftOption.months;
                }
            }
            return this.pricePerMonth;
        }

        public String getFormattedPricePerMonth() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency(), 6);
        }

        public String getFormattedPrice() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPrice(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPrice(), getCurrency(), 6);
        }

        public long getPrice() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.amount;
            }
            ProductDetails productDetails = this.googlePlayProductDetails;
            if (productDetails != null) {
                return productDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
            }
            return 0L;
        }

        public String getCurrency() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.currency;
            }
            ProductDetails productDetails = this.googlePlayProductDetails;
            return productDetails == null ? "" : productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
        }
    }
}
