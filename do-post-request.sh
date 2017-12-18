export LC_ALL=en_US.utf8
DATE=$(date -u +%a,\ %d\ %b\ %Y\ %H:%M:%S\ GMT)

DATA="name=Everyone"
DIGESTED=`echo -n $DATA | openssl dgst -binary -sha256 | base64 - | tr '/+' '_-'`
echo $DIGESTED

TOSIGN="(request-target): post /resource\nhost: localhost:9999\ndate: $DATE\ndigest: SHA-256=$DIGESTED"
echo $TOSIGN
SIGNATURE=`echo -n "$TOSIGN" | openssl dgst -binary -sha256 -hmac 0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87 | base64 - | tr '/+' '_-'`

SIGNATURE_HEADER="keyId=\"key1\",algorithm=\"hmac-sha256\",headers=\"(request-target) host date digest\",signature=\"$SIGNATURE\""

curl -v -H "Date: $DATE" -H "Signature: $SIGNATURE_HEADER" -H "digest: SHA-256=$DIGESTED" http://localhost:9999/resource -d $DATA
