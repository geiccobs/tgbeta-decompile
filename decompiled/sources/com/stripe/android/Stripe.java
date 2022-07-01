package com.stripe.android;

import android.os.AsyncTask;
import android.os.Build;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.StripeException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.RequestOptions;
import com.stripe.android.net.StripeApiHandler;
import com.stripe.android.util.StripeNetworkUtils;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class Stripe {
    private String defaultPublishableKey;
    TokenCreator tokenCreator = new TokenCreator() { // from class: com.stripe.android.Stripe.1
        @Override // com.stripe.android.Stripe.TokenCreator
        public void create(final Card card, final String str, Executor executor, final TokenCallback tokenCallback) {
            Stripe.this.executeTokenTask(executor, new AsyncTask<Void, Void, ResponseWrapper>() { // from class: com.stripe.android.Stripe.1.1
                public ResponseWrapper doInBackground(Void... voidArr) {
                    try {
                        return new ResponseWrapper(StripeApiHandler.createToken(StripeNetworkUtils.hashMapFromCard(card), RequestOptions.builder(str).build()), null);
                    } catch (StripeException e) {
                        return new ResponseWrapper(null, e);
                    }
                }

                public void onPostExecute(ResponseWrapper responseWrapper) {
                    Stripe.this.tokenTaskPostExecution(responseWrapper, tokenCallback);
                }
            });
        }
    };

    /* loaded from: classes.dex */
    public interface TokenCreator {
        void create(Card card, String str, Executor executor, TokenCallback tokenCallback);
    }

    public Stripe(String str) throws AuthenticationException {
        setDefaultPublishableKey(str);
    }

    public void createToken(Card card, TokenCallback tokenCallback) {
        createToken(card, this.defaultPublishableKey, tokenCallback);
    }

    public void createToken(Card card, String str, TokenCallback tokenCallback) {
        createToken(card, str, null, tokenCallback);
    }

    public void createToken(Card card, String str, Executor executor, TokenCallback tokenCallback) {
        try {
            if (card == null) {
                throw new RuntimeException("Required Parameter: 'card' is required to create a token");
            }
            if (tokenCallback == null) {
                throw new RuntimeException("Required Parameter: 'callback' is required to use the created token and handle errors");
            }
            validateKey(str);
            this.tokenCreator.create(card, str, executor, tokenCallback);
        } catch (AuthenticationException e) {
            tokenCallback.onError(e);
        }
    }

    public void setDefaultPublishableKey(String str) throws AuthenticationException {
        validateKey(str);
        this.defaultPublishableKey = str;
    }

    private void validateKey(String str) throws AuthenticationException {
        if (str == null || str.length() == 0) {
            throw new AuthenticationException("Invalid Publishable Key: You must use a valid publishable key to create a token.  For more info, see https://stripe.com/docs/stripe.js.", null, 0);
        }
        if (str.startsWith("sk_")) {
            throw new AuthenticationException("Invalid Publishable Key: You are using a secret key to create a token, instead of the publishable one. For more info, see https://stripe.com/docs/stripe.js", null, 0);
        }
    }

    public void tokenTaskPostExecution(ResponseWrapper responseWrapper, TokenCallback tokenCallback) {
        Token token = responseWrapper.token;
        if (token != null) {
            tokenCallback.onSuccess(token);
            return;
        }
        Exception exc = responseWrapper.error;
        if (exc != null) {
            tokenCallback.onError(exc);
        } else {
            tokenCallback.onError(new RuntimeException("Somehow got neither a token response or an error response"));
        }
    }

    public void executeTokenTask(Executor executor, AsyncTask<Void, Void, ResponseWrapper> asyncTask) {
        if (executor != null && Build.VERSION.SDK_INT > 11) {
            asyncTask.executeOnExecutor(executor, new Void[0]);
        } else {
            asyncTask.execute(new Void[0]);
        }
    }

    /* loaded from: classes.dex */
    public class ResponseWrapper {
        final Exception error;
        final Token token;

        private ResponseWrapper(Stripe stripe, Token token, Exception exc) {
            this.error = exc;
            this.token = token;
        }
    }
}
