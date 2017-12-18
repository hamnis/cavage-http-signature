package net.hamnaberg.cavage;

import okhttp3.*;
import okhttp3.internal.http.HttpDate;
import okio.Buffer;
import okio.ByteString;

import java.io.IOException;
import java.util.Date;

public class TestPOST {

    public static void main(String[] args) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        String date = HttpDate.format(new Date());

        FormBody form = new FormBody.Builder().add("name", "Everyone").build();
        Buffer buffer = new Buffer();
        form.writeTo(buffer);

        String digest = String.format("SHA-256=%s", buffer.readByteString().sha256().base64Url());
        String toSign = String.format("(request-target): post /resource\nhost: localhost:9999\ndate: %s\ndigest: %s", date, digest);
        ByteString bs = ByteString.encodeUtf8(toSign).hmacSha256(ByteString.encodeUtf8("0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87"));

        String signatureHeader = String.format("keyId=\"key1\", algorithm=\"hmac-sha256\", headers=\"(request-target) host date digest\", signature=\"%s\"", bs.base64Url());
        System.out.println("signatureHeader = " + signatureHeader);

        try(Response response = httpClient.newCall(
                new Request.Builder().post(form).url("http://localhost:9999/resource")
                        .addHeader("date", date)
                        .addHeader("digest", digest)
                        .addHeader("Signature", signatureHeader)
                        .build()
        ).execute()) {
            System.out.println(response.code() + " " + response.message());
            System.out.println(response.body().string());
        }


    }
}
