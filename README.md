## Sample: Merchant Checkout With Merchant Proxy

This sample code shows how to realize the
[Token Request](https://developer.token.io/token-request) flow with
the [Token Merchant Proxy](https://github.com/tokenio/merchant-proxy). The Token Request
flow enables merchants to request payments from users at any Token-connected bank.
The Token Merchant Proxy is a wrapper server around
the [Java SDK](https://github.com/tokenio/sdk-java) using a simple HTTP API.

### Dependency
The sample requires the proxy as a dependency. Make sure it is configured properly. Refer
to this [page](https://github.com/tokenio/merchant-proxy) for more details about its usages.

To start the proxy:

`cd merchant-proxy`

`./gradlew build run`

### Usage
To build this sample, you need Java Development Kit (JDK) version 8 or later.

To build:
 
 `./gradlew shadowJar`.

To run:
 
 `java -jar app/build/libs/app-*.jar`

This starts up a server.

The server shows a web page at `localhost:3000`. The page has a checkout button.
Click the button to start the merchant checkout experience. You may need to download
the Token app to link a test bank first. The server will:
1. Create a token request.
2. Redirect the user to a login page to approve the payment.
3. Wait for a callback that contains a token id.
4. Create a transfer by redeeming the received token.
