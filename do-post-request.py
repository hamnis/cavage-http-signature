#!/usr/bin/env python

import urllib2

import locale, datetime
import base64
import hashlib
import hmac
import platform

secret=bytes("0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87").encode("utf-8")

if platform.system() == "Linux":
    locale.setlocale(locale.LC_TIME, 'en_US.utf8')
else:
    locale.setlocale(locale.LC_TIME, 'en_US')

dt = datetime.datetime.utcnow().strftime('%a, %d %b %Y %H:%M:%S GMT')
data = "name=All"
sha = hashlib.sha256()
sha.update(bytes(data).encode("utf-8"))
digest="SHA-256=%s" % (base64.urlsafe_b64encode(sha.digest()))
signdata="(request-target): post /resource\nhost: localhost:9999\ndate: %s\ndigest: %s" % (dt, digest)
tosign=bytes(signdata).encode("utf-8")

signature=base64.urlsafe_b64encode(hmac.new(secret, tosign, digestmod=hashlib.sha256).digest())
signatureHeader='''keyId="key1",algorithm="hmac-sha256",headers="(request-target) host date digest",signature="%s"''' % signature
req = urllib2.Request(url='http://localhost:9999/resource', headers=
{"Date": dt, "Signature": signatureHeader, "digest": digest}, data = data )
f = urllib2.urlopen(req)
print f.read()
