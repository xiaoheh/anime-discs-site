from fuhaiwei import *
from apiurlcreate import *
import requests
import xml.etree.ElementTree as ET 
import datetime

asinlist=[]
for cd in DiscListDiscs.select().join(DiscList).switch(DiscListDiscs).join(Disc).where(DiscListDiscs.disc_list==4):
	asinlist.append(cd.id.asin)

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
	r=requests.get(url)
	while r.status_code != requests.codes.ok:
		r=requests.get(url)
	with open('dalao.xml','wb') as f:
		f.write(r.content)
		print('success')
	xmlns="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}"
	tree = ET.parse('dalao.xml')
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
	discrankid=DiscRank.get(DiscRank.disc==discid).id
	rankrow=DiscRank.get(DiscRank.disc==discid)
	if rankrow.park1==salesrank:
		data1=DiscRank(disc=discid,park=salesrank,padt=now,id=discrankid)
		data1.save()
	else:
		data1=DiscRank(disc=discid,park=salesrank,park1=salesrank,park2=rankrow.park1,park3=rankrow.park2,park4=rankrow.park3,park5=rankrow.park4,padt=now
						,padt1=now,padt2=rankrow.padt1,padt3=rankrow.padt2,padt4=rankrow.padt3,padt5=rankrow.padt4,id=discrankid)
		data1.save()
	print("sucessfully stored")