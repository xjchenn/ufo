from server import db
from models import Sport, Facility, Building, User, Staff, Suscription, Comment

sportList=["badminton","table tennis", "basketball", "soccer", "swimming"]
buildings=["UWPlace", "PAC", "V1", "MKV"]
for sport in sportList:
    db.session.add(Sport(sport))
    db.session.commit()


db.session.add(User("Charles"))
db.session.add(Staff("Jerry", "abc", "UWPlace"))

for buildingsName in buildings:
    db.session.add(Building(buildingsName,))
    for sportName in sportList:
        db.session.add(Facility(buildingsName+" "+sportName, buildingsName,sportName, 0))
        db.session.commit()
