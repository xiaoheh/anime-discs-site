from peewee import *

database = MySQLDatabase('animediscs', **{'user': 'wind','password':'wind'})

class UnknownField(object):
    pass

class BaseModel(Model):
    class Meta:
        database = database

class Season(BaseModel):
    id = BigIntegerField(primary_key=True)
    japan = CharField(unique=True)
    title = CharField()
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'season'

class Anime(BaseModel):
    alias = CharField(null=True)
    id = BigIntegerField(primary_key=True)
    japan = CharField(unique=True)
    season = ForeignKeyField(db_column='season_id', rel_model=Season, to_field='id')
    sname = CharField(null=True)
    title = CharField()
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'anime'

class Disc(BaseModel):
    amzver = BooleanField(null=True)  # bit
    anime = ForeignKeyField(db_column='anime_id', null=True, rel_model=Anime, to_field='id')
    asin = CharField(unique=True)
    id = BigIntegerField(primary_key=True)
    japan = CharField()
    release_date = DateTimeField(null=True)
    sname = CharField(null=True)
    title = CharField()
    type = IntegerField()
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'disc'

class DiscList(BaseModel):
    date = DateTimeField(null=True)
    id = BigIntegerField(primary_key=True)
    name = CharField(unique=True)
    sakura = BooleanField(null=True)  # bit
    title = CharField()
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'disc_list'

class DiscListMap(BaseModel):
    discs = ForeignKeyField(db_column='discs_id', rel_model=Disc, to_field='id')
    id = ForeignKeyField(db_column='id', rel_model=DiscList, to_field='id')

    class Meta:
        db_table = 'disc_list_map'

class DiscRank(BaseModel):
    disc = ForeignKeyField(db_column='disc_id', rel_model=Disc, to_field='id', unique=True)
    id = BigIntegerField(primary_key=True)
    padt = DateTimeField(null=True)
    padt1 = DateTimeField(null=True)
    padt2 = DateTimeField(null=True)
    padt3 = DateTimeField(null=True)
    padt4 = DateTimeField(null=True)
    padt5 = DateTimeField(null=True)
    park = IntegerField(null=True)
    park1 = IntegerField(null=True)
    park2 = IntegerField(null=True)
    park3 = IntegerField(null=True)
    park4 = IntegerField(null=True)
    park5 = IntegerField(null=True)
    spdt = DateTimeField(null=True)
    sprk = IntegerField(null=True)
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'disc_rank'

class DiscRecord(BaseModel):
    date = DateTimeField()
    disc = ForeignKeyField(db_column='disc_id', rel_model=Disc, to_field='id')
    id = BigIntegerField(primary_key=True)
    rank = IntegerField(null=True)
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'disc_record'

class DiscSakura(BaseModel):
    cubk = IntegerField(null=True)
    cupt = IntegerField(null=True)
    curk = IntegerField(null=True)
    date = DateTimeField(null=True)
    disc = ForeignKeyField(db_column='disc_id', rel_model=Disc, to_field='id', unique=True)
    id = BigIntegerField(primary_key=True)
    prrk = IntegerField(null=True)
    sday = IntegerField(null=True)
    version = BigIntegerField(null=True)

    class Meta:
        db_table = 'disc_sakura'

