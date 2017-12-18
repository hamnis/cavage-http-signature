package net.hamnaberg.cavage;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HttpDate;
import okio.ByteString;

import java.io.IOException;
import java.util.Date;

public class TestGET {

    public static void main(String[] args) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        String date = HttpDate.format(new Date());

        String toSign = String.format("(request-target): get /resource\nhost: localhost:9999\ndate: %s", date);
        ByteString bs = ByteString.encodeUtf8(toSign).hmacSha256(ByteString.encodeUtf8("0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87"));

        String signatureHeader = String.format("keyId=\"key1\", algorithm=\"hmac-sha256\", headers=\"(request-target) host date\", signature=\"%s\"", bs.base64Url());
        System.out.println("signatureHeader = " + signatureHeader);

        try(Response response = httpClient.newCall(
                new Request.Builder().get().url("http://localhost:9999/resource")
                        .addHeader("date", date)
                        .addHeader("Signature", signatureHeader)
                        .build()
        ).execute()) {
            System.out.println(response.code() + " " + response.message());
            System.out.println(response.body().string());
        }


    }
}
