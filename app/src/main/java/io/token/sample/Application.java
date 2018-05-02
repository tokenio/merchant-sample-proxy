package io.token.sample;

import static com.google.common.base.Charsets.UTF_8;

import com.google.common.io.Resources;
import io.token.proto.common.token.TokenProtos.Token;
import io.token.proto.common.transfer.TransferProtos.Transfer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import spark.Spark;

/**
 * Application main entry point.
 * To execute, one needs to run something like:
 * <p>
 * <pre>
 * ./gradlew :app:shadowJar
 * java -jar ./app/build/libs/app-1.0.0-all.jar
 * </pre>
 */
public class Application {
    private static TokenService tokenService = new TokenService();

    /**
     * Main function.
     *
     * @param args command line arguments
     * @throws IOException thrown on errors
     */
    public static void main(String[] args) throws IOException {
        // Initializes the server
        Spark.port(3000);

        // Endpoint for transfer payment, called by client side to initiate a payment.
        Spark.post("/transfer", (req, res) -> {
            Map<String, String> params = parseFormData(req.body());
            double amount = Double.valueOf(params.get("amount"));
            String currency = params.get("currency");
            String description = params.get("description");

            String tokenRequestId = tokenService.storeTokenRequest(
                    amount,
                    currency,
                    description);

            String tokenRequestUrl = tokenService.generateTokenRequestUrl(tokenRequestId);

            //send user to Token cloud
            res.status(302);
            res.redirect(tokenRequestUrl);
            return null;
        });

        // Endpoint for transfer token redemption.
        Spark.get("/redeem", (req, res) -> {
            String callbackUri = req.raw().getRequestURL().toString()
                    + "?"
                    + req.raw().getQueryString();
            String tokenId = tokenService.parseTokenRequestCallback(callbackUri);

            //get the token and check its validity
            Token token = tokenService.getToken(tokenId);

            //redeem the token at the server to move the funds
            Transfer transfer = tokenService.redeemToken(token.getId());
            res.status(200);
            return "Success! Redeemed transfer: " + transfer.getId();
        });

        // Serve the web page, stylesheet and JS script:
        String script = Resources.toString(Resources.getResource("script.js"), UTF_8);
        Spark.get("/script.js", (req, res) -> script);
        String style = Resources.toString(Resources.getResource("style.css"), UTF_8);
        Spark.get("/style.css", (req, res) -> {
            res.type("text/css");
            return style;
        });
        String page = Resources.toString(Resources.getResource("index.html"), UTF_8);
        Spark.get("/", (req, res) -> page);
    }

    /**
     * Parse form data
     */
    private static Map<String, String> parseFormData(String query) {
        try {
            Map<String, String> queryPairs = new LinkedHashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                queryPairs.put(
                        URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return queryPairs;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Couldn't parse form data");
        }
    }
}
