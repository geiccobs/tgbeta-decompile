package org.telegram.messenger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.exoplayer2.util.Util;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class BillingController implements PurchasesUpdatedListener, BillingClientStateListener {
    public static ProductDetails PREMIUM_PRODUCT_DETAILS = null;
    private static BillingController instance;
    private BillingClient billingClient;
    public static final String PREMIUM_PRODUCT_ID = "telegram_premium";
    public static final QueryProductDetailsParams.Product PREMIUM_PRODUCT = QueryProductDetailsParams.Product.newBuilder().setProductType("subs").setProductId(PREMIUM_PRODUCT_ID).build();
    private Map<String, Consumer<BillingResult>> resultListeners = new HashMap();
    private List<String> requestingTokens = new ArrayList();
    private Map<String, Integer> currencyExpMap = new HashMap();

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private BillingController(Context ctx) {
        this.billingClient = BillingClient.newBuilder(ctx).enablePendingPurchases().setListener(this).build();
    }

    public int getCurrencyExp(String currency) {
        Integer exp = this.currencyExpMap.get(currency);
        if (exp == null) {
            return 0;
        }
        return exp.intValue();
    }

    public void startConnection() {
        if (isReady()) {
            return;
        }
        if (BuildVars.useInvoiceBilling()) {
            try {
                Context ctx = ApplicationLoader.applicationContext;
                InputStream in = ctx.getAssets().open("currencies.json");
                JSONObject obj = new JSONObject(new String(Util.toByteArray(in), "UTF-8"));
                parseCurrencies(obj);
                in.close();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.billingClient.startConnection(this);
    }

    private void parseCurrencies(JSONObject obj) {
        Iterator<String> it = obj.keys();
        while (it.hasNext()) {
            String key = it.next();
            JSONObject currency = obj.optJSONObject(key);
            this.currencyExpMap.put(key, Integer.valueOf(currency.optInt("exp")));
        }
    }

    public boolean isReady() {
        return this.billingClient.isReady();
    }

    public void queryProductDetails(List<QueryProductDetailsParams.Product> products, ProductDetailsResponseListener responseListener) {
        if (!isReady()) {
            throw new IllegalStateException("Billing controller should be ready for this call!");
        }
        this.billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(products).build(), responseListener);
    }

    public void queryPurchases(String productType, PurchasesResponseListener responseListener) {
        this.billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(productType).build(), responseListener);
    }

    public boolean startManageSubscription(Context ctx, String productId) {
        try {
            ctx.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format("https://play.google.com/store/account/subscriptions?sku=%s&package=%s", productId, ctx.getPackageName()))));
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void addResultListener(String productId, Consumer<BillingResult> listener) {
        this.resultListeners.put(productId, listener);
    }

    public boolean launchBillingFlow(Activity activity, List<BillingFlowParams.ProductDetailsParams> productDetails) {
        return isReady() && this.billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetails).build()).getResponseCode() == 0;
    }

    @Override // com.android.billingclient.api.PurchasesUpdatedListener
    public void onPurchasesUpdated(final BillingResult billingResult, List<Purchase> list) {
        FileLog.d("Billing purchases updated: " + billingResult + ", " + list);
        if (list == null) {
            return;
        }
        for (final Purchase purchase : list) {
            if (purchase.getPurchaseState() == 1 && !purchase.isAcknowledged() && !this.requestingTokens.contains(purchase.getPurchaseToken())) {
                this.requestingTokens.add(purchase.getPurchaseToken());
                TLRPC.TL_payments_assignPlayMarketTransaction req = new TLRPC.TL_payments_assignPlayMarketTransaction();
                req.purchase_token = purchase.getPurchaseToken();
                final AccountInstance acc = AccountInstance.getInstance(UserConfig.selectedAccount);
                acc.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.BillingController$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BillingController.this.m113xaefb09cd(acc, purchase, billingResult, tLObject, tL_error);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$onPurchasesUpdated$0$org-telegram-messenger-BillingController */
    public /* synthetic */ void m113xaefb09cd(AccountInstance acc, Purchase purchase, BillingResult billingResult, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Updates) {
            acc.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            this.requestingTokens.remove(purchase.getPurchaseToken());
            for (String productId : purchase.getProducts()) {
                Consumer<BillingResult> listener = this.resultListeners.remove(productId);
                listener.accept(billingResult);
            }
        }
    }

    @Override // com.android.billingclient.api.BillingClientStateListener
    public void onBillingServiceDisconnected() {
        FileLog.d("Billing service disconnected");
    }

    @Override // com.android.billingclient.api.BillingClientStateListener
    public void onBillingSetupFinished(BillingResult setupBillingResult) {
        if (setupBillingResult.getResponseCode() == 0) {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), BillingController$$ExternalSyntheticLambda0.INSTANCE);
            queryPurchases("subs", new PurchasesResponseListener() { // from class: org.telegram.messenger.BillingController$$ExternalSyntheticLambda1
                @Override // com.android.billingclient.api.PurchasesResponseListener
                public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
                    BillingController.this.onPurchasesUpdated(billingResult, list);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$onBillingSetupFinished$2(BillingResult billingResult, List list) {
        if (billingResult.getResponseCode() == 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                ProductDetails details = (ProductDetails) it.next();
                if (details.getProductId().equals(PREMIUM_PRODUCT_ID)) {
                    PREMIUM_PRODUCT_DETAILS = details;
                }
            }
            AndroidUtilities.runOnUIThread(BillingController$$ExternalSyntheticLambda2.INSTANCE);
        }
    }
}
