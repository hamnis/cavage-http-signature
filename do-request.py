#/usr/bin/env python

import urllib2

import locale, datetime
import base64
import hashlib
import hmac

secret=bytes("0bc9e15d-eb84-4409-a05d-fdb3b5c4cc87").encode("utf-8")

locale.setlocale(locale.LC_TIME, 'en_US')
dt = datetime.datetime.utcnow().strftime('%a, %d %b %Y %H:%M:%S GMT')
signdata="(request-target): get /resource\nhost: localhost:9999\ndate: %s" % dt
tosign=bytes(signdata).encode("utf-8")

signature=base64.urlsafe_b64encode(hmac.new(secret, tosign, digestmod=hashlib.sha256).digest())

req = urllib2.Request(url='http://localhost:9999/resource', headers=
{"Date": dt, "Signature": """keyId="key1",algorithm="hmac-sha256",headers="(request-target) host date",signature="%s""" % signature} )
f = urllib2.urlopen(req)
print f.read()
