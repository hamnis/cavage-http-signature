DATE="Mon, 18 Dec 2017 19:14:50 GMT"

DATA="(request-target): get /resource\nhost: localhost:9999\ndate: $DATE"
EXPECTED="MYu3d3vpd07Q53pQu2ubgka6gLEYB031Jh69wjoNXUc="
EXPECTED_HEX="349eede74f02714aa101f4f668a2c374c6e49999c43b1e91e29bd75c98558fdf"
OPENSSL=/usr/local/Cellar/openssl/1.0.2l/bin/openssl

KEY=0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87

SIGNATURE=$(echo -n "$DATA" | $OPENSSL dgst -hex -hmac "$KEY" -sha256)
echo "Expected: $EXPECTED_HEX"

echo "was $SIGNATURE"


