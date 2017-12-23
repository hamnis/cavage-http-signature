package net.hamnaberg.cavage;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import okio.Buffer;
import okio.ByteString;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@PreMatching
@Priority(Priorities.AUTHORIZATION)
//Implementation of https://tools.ietf.org/html/draft-cavage-http-signatures-09
public class SignatureFilter implements ContainerRequestFilter {
    public static final long MAX_SECOND_SKEW = 300L;
    private Set<String> writableMethods = HashSet.of("POST", "PUT", "PATCH");

    private final Map<String, ByteString> keys;

    public SignatureFilter(Map<String, ByteString> keys) {
        this.keys = keys;
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        List<String> defaultHeaders = List.of("(request-target)", "host", "date");
        List<String> headers = writableMethods.contains(request.getMethod().toUpperCase()) ? List.of("digest").prependAll(defaultHeaders) : defaultHeaders;

        Option<SignatureHeader> maybeSignature = Option.of(request.getHeaderString("Signature")).flatMap(SignatureHeader::fromString);
        if (maybeSignature.isEmpty()) {
            request.abortWith(Response.status(403).entity("Missing Signature header").build());
            return;
        }
        maybeSignature.flatMap(header -> validate(header, request, SignatureHeader.Algorithm.HMAC_SHA256, headers)).forEach(request::abortWith);
    }

    public Option<Response> validate(SignatureHeader header, ContainerRequestContext request, SignatureHeader.Algorithm expectedAlgorithm, List<String> expectedHeaders) {
        List<String> values = header.headers.flatMap(name -> getHeader(name.toLowerCase(), request));
        if (header.algorithm != expectedAlgorithm) {
            String message = String.format("Invalid request signature algorithm received; expected %s, got %s", expectedAlgorithm, header.algorithm);
            return Option.some(forbidden(message));
        } else if (header.headers.size() != values.size()) {
            String message = String.format("Diff between headers fields in signature header '%s' and sent headers '%s'", header.headers, values);
            return Option.some(forbidden(message));
        } else if (!expectedHeaders.equals(header.headers)) {
            String message = String.format("Diff between expected headers fields '%s' and header fields declared in signature header '%s'", expectedHeaders, header.headers);
            return Option.some(forbidden(message));
        }
        if (header.headers.contains("date")) {
            long nowInSeconds = Instant.now().getEpochSecond();
            long headerSeconds = request.getDate().toInstant().getEpochSecond();
            long absdiff = Math.abs(nowInSeconds - headerSeconds);
            if (absdiff > MAX_SECOND_SKEW) {
                String message = String.format("Clock skew between server and client is too great: %s", absdiff);
                return Option.some(forbidden(message));
            }
        }
        return verify(header, expectedAlgorithm, values);
    }

    private Response forbidden(String message) {
        return Response.status(403).entity(message).build();
    }

    private Option<Response> verify(SignatureHeader header, SignatureHeader.Algorithm expectedAlgorithm, List<String> values) {
        switch (expectedAlgorithm) {
            case HMAC_SHA256:
                if (keys.containsKey(header.keyId)) {
                    ByteString signature = signHmac(values, keys.get(header.keyId).getOrNull());
                    if (signature.equals(header.signature)) {
                        return Option.none();
                    } else {
                        String message = String.format("Diff between expected signature '%s' and received signature '%s'", signature.base64Url(), header.signature.base64Url());
                        return Option.some(forbidden(message));
                    }
                } else {
                    String message = String.format("keyId=%s did not exist in '%s'", header.keyId, keys.keySet().mkString(","));
                    return Option.some(forbidden(message));
                }
            case RSA_SHA256:
                return Option.some(forbidden("Unsupported signature type"));
            default:
                return Option.none();
        }
    }

    static ByteString signHmac(List<String> values, ByteString hmacKey) {
        ByteString bs = ByteString.encodeUtf8(values.mkString("\n"));
        return bs.hmacSha256(hmacKey);
    }


    private Option<String> getHeader(String name, ContainerRequestContext request) {
        if ("(request-target)".equals(name)) {
            return Option.some(getRequestTarget(request));
        } else if ("digest".equals(name) && writableMethods.contains(request.getMethod().toUpperCase())) {
            if (request.hasEntity()) {
                Buffer buffer = new Buffer();
                try (InputStream is = request.getEntityStream()) {
                    ByteString bs = buffer.readFrom(is).readByteString();
                    request.setEntityStream(new ByteArrayInputStream(bs.toByteArray()));
                    return Option.some("digest: SHA-256=" + bs.sha256().base64Url());
                } catch (IOException e) {
                    throw new WebApplicationException(e.getMessage());
                }
            } else {
                throw new WebApplicationException("Expected entity got none", 400);
            }
        }
        else {
            return Option.of(request.getHeaderString(name)).map(value -> name.toLowerCase() + ": " + value);
        }
    }

    public String getRequestTarget(ContainerRequestContext request) {
        return "(request-target): " + request.getMethod().toLowerCase() + " /" + request.getUriInfo().getPath();
    }
}
