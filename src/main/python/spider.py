from animediscs import *
from apiurlcreate import *
import requests
import xml.etree.ElementTree as ET 
import datetime

asinlist=[]
for item in Disc.select(Disc.asin):
	asinlist.append(item.asin)
for itemid in asinlist:
	params = {
		"Service" : "AWSECommerceService",
		"Operation":  "ItemLookup",
		"AWSAccessKeyId":  "AKIAJIKVGLZ2P37DL2EQ",
		"AssociateTag":  "wengyusu-22",
		"ItemId" : itemid,
	   	"IdType" : "ASIN",			
	   	"ResponseGroup" : "ItemAttributes,SalesRank"
		}

	url=urlcreate(params)
	now=datetime.datetime.now()
	s=requests.session()
	while True:
			try:
				r=s.get(url,timeout=None)
				if r.status_code == requests.codes.ok:
					break
					# time.sleep(1)
			except Exception as h:
				with open('err.log','a') as f:
					f.write(str(h)+'\n')
	with open('data.xml','wb') as f:
		f.write(r.content)
	xmlns="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}"
	tree = ET.parse('data.xml')
	root = tree.getroot()
	items=root.find(xmlns+'Items')
	item = items.find(xmlns+'Item')
	if item.find(xmlns+'SalesRank') is None:
		break
	else:
		salesrank=item.find(xmlns+'SalesRank').text
			# print(salesrank)
	title=item.find(xmlns+'ItemAttributes').find(xmlns+'Title').text
		# print(itemid)
	discid=Disc.get(Disc.asin==itemid).id
		# print(discid)
	data=Disc(asin=itemid,japan=title,id=discid)
	data.save()

		# discrankid=DiscRank.get(DiscRank.disc==discid).id
	rankrow,created=DiscRank.get_or_create(disc=discid)
		# print(rankrow.park1)
	if int(rankrow.park1)==int(salesrank):
		data1=DiscRank(disc=discid,park=salesrank,padt=now,id=rankrow.id)
		data1.save()
	else:
		data1=DiscRank(disc=discid,park=salesrank,park1=salesrank,park2=rankrow.park1,park3=rankrow.park2,park4=rankrow.park3,park5=rankrow.park4,padt=now
						,padt1=now,padt2=rankrow.padt1,padt3=rankrow.padt2,padt4=rankrow.padt3,padt5=rankrow.padt4,id=rankrow.id)
		data1.save()
		recorddata=DiscRecord(date=now,disc=discid,rank=salesrank)
		recorddata.save()
		print('successfully stored')
# except Exception as e:
# 	print(e)
# 	with open('err.log','a') as f:
# 		f.write(str(e)+'\n')
