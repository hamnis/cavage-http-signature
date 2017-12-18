export LC_ALL=en_US.utf8
DATE=$(date -u +%a,\ %d\ %b\ %Y\ %H:%M:%S\ GMT)

TOSIGN="(request-target): get /resource\nhost: localhost:9999\ndate: $DATE"
SIGNATURE=`echo -n "$TOSIGN" | openssl dgst -binary -sha256 -hmac 0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87 | base64 - | tr '/+' '_-'`

SIGNATURE_HEADER="keyId=\"key1\",algorithm=\"hmac-sha256\",headers=\"(request-target) host date\",signature=\"$SIGNATURE\""

curl -v -H "Date: $DATE" -H "Signature: $SIGNATURE_HEADER" http://localhost:9999/resource
