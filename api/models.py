from server import db

class User(db.Model):
    user_id = db.Column(db.String(80), primary_key=True)
    def __init__(self, user_id):
        self.user_id = user_id

    def __repr__(self):
        return '<Normal User %r>' % self.user_id

    def toDict(self):
        return {    'user_id': self.user_id}


class Staff(db.Model):
    user_id = db.Column(db.String(80), primary_key=True)
    password = db.Column(db.String(100))
    permissions = db.Column(db.String(80), db.ForeignKey('building.name'))
    def __init__(self, user_id, password, permissions):
        self.user_id=user_id
        self.password = password
        self.permissions = permissions

    def __repr__(self):
        return '<Staff User %r>' % self.user_id

    def authenticate(self, password):
        return self.password == password

    def get_permission(self):
        return self.permissions

    def toDict(self):
        return {    'user_id': self.user_id,
                    'permissions': self.permissions}

class Sport(db.Model):
    title = db.Column(db.String(80), primary_key=True)
    def __init__(self, title):
        self.title = title

    def __repr__(self):
        return '<Sport %r>' % self.title

    def get_title(self):
        return self.title

    def toDict(self):
        return {    'title': self.title}

class Building(db.Model):
    name = db.Column(db.String(80), primary_key=True)
    image_url = db.Column(db.String(100))
    lat = db.Column(db.Float)
    lng = db.Column(db.Float)
    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return '<Building %r>' % (self.name)

    def getLatLng(self):
        return {'lat': self.lat, 'lng': self.lng}

    def toDict(self):
        return {    'name': self.name,
                    'image_url': self.image_url,
                    'lat': self.lat,
                    'lng': self.lng}


class Facility(db.Model):
    facility_name = db.Column(db.String(80), primary_key=True)
    building = db.Column(db.String(80), db.ForeignKey('building.name'))
    sport = db.Column(db.String(80), db.ForeignKey('sport.title'))
    rating = db.Column(db.Float)
    availability = db.Column(db.Integer) #where 0 is the most available
    numOfRating = db.Column (db.Integer)

    def __init__(self, facility_name , building, sport,rating):
        self.facility_name = facility_name
        self.building = building
        self.sport = sport
        self.availability = 0
        self.numOfRating = 0
        self.rating = rating

    def __repr__(self):
        return '<Facility %r %r>' %(self.facility_name,self.sport)

    def toDict(self):
        return {    'name': self.facility_name,
                    'availability': self.availability,
                    'building': self.building,
                    'sport': self.sport,
                    'rating':self.rating}

    def getName(self):
        return self.facility_name

    def getBuilding(self):
        return self.building

    def setAvailability(self, avail):
        self.availability=avail

    def addRating(self, rating):
        self.rating = (self.rating*self.numOfRating+rating)/(self.numOfRating+1)
        self.numOfRating+=1
        return self.rating




class Subscription(db.Model):
    user_id = db.Column(db.String(80), db.ForeignKey('user.user_id'), primary_key=True)
    facility = db.Column(db.String(80), db.ForeignKey('facility.facility_name'), primary_key=True)

    def __init__(self, user_id, facility):
        self.user_id = user_id
        self.facility = facility

    def getDict(self):
        return {'user_id': user_id, 'facility': facility}

    def getFacilityDict(self):
        return Facility.query.get(self.facility).toDict()

class Comment(db.Model):
    user_id = db.Column(db.String(80), db.ForeignKey('user.user_id'), primary_key=True)
    facility = db.Column(db.String(80), db.ForeignKey('facility.facility_name'), primary_key=True)
    time = db.Column(db.DateTime)
    text = db.Column(db.Text)

    def __init__(self, user_id, facility):
        self.user_id = user_id
        self.facility = facility
