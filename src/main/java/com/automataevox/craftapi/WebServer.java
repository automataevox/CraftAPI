package com.automataevox.craftapi;

import com.automataevox.craftapi.utils.Logger;
import com.automataevox.craftapi.utils.RouteDefinition;
import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class WebServer extends NanoHTTPD {
    private final boolean isAuthenticated;
    private final String authKey;
    private final List<RouteDefinition> routes = new ArrayList<>();
    private final WebSocketServerHandler webSocketServer; // Declare it here as an instance variable


    public WebServer(final int port, final boolean authenticationEnabled, final String authenticationKey, final int webSocketPort) {
        super(port);
        this.isAuthenticated = authenticationEnabled;
        this.authKey = authenticationKey;
        this.webSocketServer = new WebSocketServerHandler(webSocketPort); // Initialize it here
        this.webSocketServer.start(); // Start the WebSocket server
    }

    @Override
    public void stop() {
        super.stop();
        if (webSocketServer != null) {
            try {
                webSocketServer.stop();
                System.out.println("WebSocket server stopped successfully.");
            } catch (Exception e) {
                System.err.println("Error stopping WebSocket server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public Response serve(final IHTTPSession session) {
        String uri = session.getUri();
        NanoHTTPD.Method method = session.getMethod();
        Map<String, String> params = new HashMap<>();
        boolean swaggerDocumentation = CraftAPI.config.getBoolean("swagger", true);

        Logger.debug("Received request for: " + uri + " with method: " + method);

        if (session.getHeaders().containsKey("Upgrade") && session.getHeaders().get("Upgrade").equalsIgnoreCase("websocket")) {
            return handleWebSocketUpgrade(session);
        }

        if (method.equals(NanoHTTPD.Method.OPTIONS)) {
            return addCorsHeaders(newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, ""));
        }

        // Define Map of allowed origins
        AtomicBoolean isAllowedPath = getAtomicBoolean(swaggerDocumentation, uri);

        // Exception for the root path, swagger files and /api-docs from authentication
        if (isAuthenticated) {
            if (isAllowedPath.get()) {
                Logger.debug("Allowed path: " + uri);
            } else {
                Logger.debug("Checking authentication for: " + uri);
                String authHeader = session.getHeaders().get("authorization");
                if (authHeader == null || !authHeader.equals(authKey)) {
                    Logger.debug("Unauthorized request for: " + uri);
                    return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Unauthorized");
                }
            }
        }

        if (swaggerDocumentation) {
            // Serve OpenAPI spec
            if ("/api-docs".equalsIgnoreCase(uri)) {
                InputStream apiSpecStream = getClass().getResourceAsStream("/api.yaml");
                if (apiSpecStream != null) {
                    return newChunkedResponse(Response.Status.OK, "application/yaml", apiSpecStream);
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "API documentation not found");
                }
            }
        }

        // Serve Swagger UI files on root path
        if (swaggerDocumentation) {
            if (uri.equalsIgnoreCase("/") || uri.startsWith("/swagger") || isAllowedPath.get()) {
                if ("/".equals(uri)) {
                    uri = "/index.html"; // Redirect to the main Swagger UI page
                }
                InputStream resourceStream = getClass().getResourceAsStream("/swagger" + uri);
                if (resourceStream != null) {
                    String mimeType = determineMimeType(uri);
                    return newChunkedResponse(Response.Status.OK, mimeType, resourceStream);
                } else {
                    Logger.debug("Resource not found: " + uri);
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Route not Found");
                }
            }
        }

        // Extract query parameters and add them to the params map
        if (session.getQueryParameterString() != null) {
            session.getParameters().forEach((key, value) -> params.put(key, value.get(0)));
        }

        for (RouteDefinition route : routes) {
            if (route.matches(uri, method, params)) {
                return addCorsHeaders(route.getHandler().apply(params));
            }
        }

        Logger.debug("No route found for: " + uri + " with method: " + method);

        // Default response
        return addCorsHeaders(newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found"));
    }

    private static AtomicBoolean getAtomicBoolean(boolean swaggerDocumentation, String uri) {
        List<String> allowedPaths = List.of();

        if (swaggerDocumentation) {
            allowedPaths = List.of(
                    "/", "/swagger-ui-bundle.js", "/swagger-ui.css", "/api-docs", "/index.css",
                    "/searchPlugin.js", "/swagger-ui-standalone-preset.js", "/swagger-initializer.js", "/favicon-32x32.png",
                    "/swagger-ui.css.map", "/favicon-16x16.png"
            );
        }

        // Check if the path is allowed
        AtomicBoolean isAllowedPath = new AtomicBoolean(false);
        allowedPaths.forEach(path -> {
            if (uri.equals(path) || uri.startsWith("/swagger")) {
                isAllowedPath.set(true);
            }
        });
        return isAllowedPath;
    }

    // Method to determine MIME type
    private String determineMimeType(final String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".yaml")) return "application/yaml";
        return "text/plain";
    }

    private Response addCorsHeaders(Response response) {
        // Create a new Response with the same status, MIME type, and data from the original response
        if (response.getData() != null) {

            return getResponse(response);
        } else {
            // Handle cases where response data is not InputStream
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Unsupported response data type");
        }
    }

    private static Response getResponse(Response response) {
        InputStream originalData = response.getData();
        Response newResponse = newChunkedResponse(response.getStatus(), response.getMimeType(), originalData);

        // Add CORS headers
        newResponse.addHeader("Access-Control-Allow-Origin", "*");
        newResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        newResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        newResponse.addHeader("Access-Control-Max-Age", "3600");
        return newResponse;
    }


    public void addRoute(final NanoHTTPD.Method method, final String routePattern, final Function<Map<String, String>, Response> handler) {
        routes.add(new RouteDefinition(method, routePattern, params -> {
            Response response = handler.apply(params);
            return addCorsHeaders(response);
        }));
    }

    private Response handleWebSocketUpgrade(IHTTPSession session) {
        // Upgrade request to WebSocket
        return newFixedLengthResponse(Response.Status.SWITCH_PROTOCOL, MIME_PLAINTEXT, "");
    }
}
