import time
import hmac
import hashlib
import base64
import requests
from urllib.parse import quote
def ksort(d):
    return [(k,d[k]) for k in sorted(d.keys())]
def urlcreate(params):
    aws_access_key_id = b"AKIAJIKVGLZ2P37DL2EQ"
    aws_secret_key = b"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"
    endpoint = "webservices.amazon.co.jp"
    uri = "/onca/xml"
    gmtime=time.gmtime()
    '''params = {
        "Service" : "AWSECommerceService",
        "Operation":  "ItemSearch",
        "AWSAccessKeyId":  "AKIAJIKVGLZ2P37DL2EQ",
        "AssociateTag":  "wengyusu-22",
        "SearchIndex" : "DVD",
        "ResponseGroup":  "ItemAttributes,SalesRank",
        "Sort" : "salesrank",
        "BrowseNode":  "4367309051",
        "ItemPage":  str(i)
    }'''
    params["Timestamp"]=time.strftime("%Y-%m-%dT%H:%M:%SZ",gmtime)
    params= ksort(params)
    #print(params)
    '''dict={}
    for item in params:
    	dict[item[0]] = item[1]
    params=dict
    '''
    #params=simplejson.loads(params)
    pairs=[]
    for i in params:
    	pairs.append(i[0]+"="+quote(i[1]))


    canonical_query_string = '&'.join(pairs)
    #print(canonical_query_string)
    string_to_sign = "GET\n"+endpoint+"\n"+uri+"\n"+canonical_query_string
    string_to_sign=str.encode(string_to_sign) 
    signature=base64.b64encode(hmac.new(aws_secret_key,string_to_sign,hashlib.sha256).digest())
    #print(signature)
    request_url = 'http://'+endpoint+uri+'?'+canonical_query_string+'&Signature='+quote(signature)
    # print("Signed URL:{0}".format(request_url))
    # print(request_url)
    return request_url