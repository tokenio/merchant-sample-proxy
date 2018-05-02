## Sample: Merchant Checkout With Merchant Proxy

This sample code shows how to enable the
[Token Request](https://developer.token.io/token-request) flow with
the [Token Merchant Proxy](https://github.com/tokenio/merchant-proxy). The Token Request
flow enables merchants to request payments from users at any Token-connected bank.
The Token Merchant Proxy is a wrapper server around
the [Java SDK](https://github.com/tokenio/sdk-java) that enables merchants
to accept and initiate payments using a simple HTTP API.

The sample requires the proxy as a dependency. Make sure it is configured properly.
To start the proxy:

`cd .../merchant-proxy`

`./gradlew build run`

To build this code, you need Java Development Kit (JDK) version 8 or later.

To build:
 
 `./gradlew shadowJar`.

To run:
 
 `java -jar app/build/libs/app-*.jar`

This starts up a server.

The server shows a web page at `localhost:3000`. The page has a checkout button.
Click the button to initiates the merchant checkout experience through the Token Request
flow. The server handles endorsed payments by redeeming transfer tokens.
