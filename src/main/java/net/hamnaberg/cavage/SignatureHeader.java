package net.hamnaberg.cavage;

import io.vavr.collection.List;
import io.vavr.control.Option;
import okio.ByteString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignatureHeader {
    enum Algorithm {
        RSA_SHA256("rsa-sha256"),
        HMAC_SHA256("hmac-sha256");

        final String value;

        Algorithm(String value) {
            this.value = value;
        }

        static Option<Algorithm> get(String name) {
            for (Algorithm algorithm : values()) {
                if (algorithm.value.equals(name)) {
                    return Option.some(algorithm);
                }
            }
            return Option.none();
        }
    }

    public final String keyId;
    public final Algorithm algorithm;
    public final List<String> headers;
    public final ByteString signature;

    public SignatureHeader(String keyId, Algorithm algorithm, List<String> headerNames, ByteString signature) {
        this.keyId = keyId;
        this.algorithm = algorithm;
        this.headers = headerNames;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return String.format("keyId=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", keyId, algorithm.value, headers.mkString(" "), signature.base64());
    }

    public static Option<SignatureHeader> fromString(String header) {
        String[] fields = header.split(",");
        Map<String, String> map = new HashMap<>();

        for (String field : fields) {
            String[] parts = field.trim().split("=", 2);
            map.put(parts[0].trim().toLowerCase(), parts[1].trim().replace("\"", ""));
        }
        return OptionApply.apply(
                Option.of(map.get("keyid")),
                Option.of(map.get("algorithm")).flatMap(Algorithm::get),
                Option.of(map.get("headers")).map(s -> List.of(s.split("\\s"))),
                Option.of(map.get("signature")).flatMap(sign -> Option.of(ByteString.decodeBase64(sign))),
                SignatureHeader::new
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureHeader that = (SignatureHeader) o;
        return Objects.equals(keyId, that.keyId) &&
                algorithm == that.algorithm &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyId, algorithm, headers, signature);
    }
}
