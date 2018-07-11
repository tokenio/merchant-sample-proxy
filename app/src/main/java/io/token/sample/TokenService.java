package io.token.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.token.proto.ProtoJson;
import io.token.proto.common.account.AccountProtos.BankAccount;
import io.token.proto.common.token.TokenProtos.Token;
import io.token.proto.common.transfer.TransferProtos.Transfer;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class TokenService {
    private static String TOKEN_REQUEST_PARAMS = "{\"amount\": \"%s\", " +
            "\"currency\": \"%s\", " +
            "\"description\": \"%s\"," +
            " \"destination\": %s," +
            " \"callbackUrl\": \"%s\"}";
    private static String REDEEM_TOKEN_PARAM = "{\"tokenId\": \"%s\"}";

    private static String BASE_URL = "http://127.0.0.1:4567";

    private final Config config;
    private final BankAccount destination;

    public TokenService() {
        config = ConfigFactory.load();
        destination = ProtoJson.fromJson(
                config.getString("destination"),
                BankAccount.newBuilder());
    }

    public Token getToken(String tokenId) {
        String response = HttpClient.sendGet(toURL(BASE_URL, "/tokens/", tokenId))
                .getAsJsonObject()
                .get("token")
                .getAsJsonObject()
                .toString();
        return ProtoJson.fromJson(response, Token.newBuilder());
    }

    public String storeTokenRequest(
            double amount,
            String currency,
            String description) {
        String params = String.format(
                TOKEN_REQUEST_PARAMS,
                Double.valueOf(amount).toString(),
                currency,
                description,
                ProtoJson.toJson(destination),
                config.getString("callback_url"));

        return HttpClient.sendPost(toURL(BASE_URL, "/token-requests"), params)
                .getAsJsonObject()
                .get("tokenRequestId")
                .getAsString();
    }

    public String generateTokenRequestUrl(String tokenRequestId) {
        return generateTokenRequestUrl(tokenRequestId, null, null);
    }

    public String generateTokenRequestUrl(String tokenRequestId, String state) {
        return generateTokenRequestUrl(tokenRequestId, state, null);
    }

    public String generateTokenRequestUrl(
            String tokenRequestId,
            String state,
            String csrfToken) {
        String query = "?requestId=" + tokenRequestId;

        if (state != null) {
            query += "&state" + state;
        }

        if (csrfToken != null) {
            query += "&csrfToken" + csrfToken;
        }

        return HttpClient.sendGet(toURL(BASE_URL, "/token-request-url", query))
                .getAsJsonObject()
                .get("url")
                .getAsString();
    }

    public String parseTokenRequestCallback(String callback) {
        String query = "url=" + urlEncode(callback);
        return HttpClient.sendGet(toURL(BASE_URL, "/parse-token-request-callback?", query))
                .getAsJsonObject()
                .get("tokenId")
                .getAsString();
    }

    public Transfer redeemToken(String tokenId) {
        String params = String.format(REDEEM_TOKEN_PARAM, tokenId);
        String jsonTransfer = HttpClient.sendPost(toURL(BASE_URL, "/transfers"), params)
                .getAsJsonObject()
                .get("transfer")
                .getAsJsonObject()
                .toString();
        return ProtoJson.fromJson(jsonTransfer, Transfer.newBuilder());
    }

    private static URL toURL(String... components) {
        try {
            return new URL(String.join("", components));
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
