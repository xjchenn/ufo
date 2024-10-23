from flask import Flask, request, jsonify, json, session, render_template, g, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from flask_socketio import SocketIO, send, join_room, leave_room
import os
import json
from time import strftime

app = Flask(__name__, static_url_path='/static')
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///main.db'
db = SQLAlchemy(app)
socketio = SocketIO(app)
app.secret_key = os.urandom(24)
app.config['SECRET_KEY'] = os.urandom(42)

from models import Sport, Facility, Building, User, Staff, Subscription, Comment
from auth import Auth

db.create_all()
auth = Auth()

#app.secret_key = os.urandom[24]
#@app.before_request
#def before_request():
#    g.user = None
#    print session
#    if 'user' in session:
#        g.user = session['user']



@app.route('/',methods=['GET', 'POST'])
def index():
    if  auth.is_auth_staff_user():
        return render_template('staff.html')
    if request.method == 'POST':
        user_id = request.form['username']
        password = request.form['password'] # TODO: encrypt this
        if auth.login_staff(user_id, password):
            return render_template('staff.html')
    return render_template('index.html')

@app.route('/login', methods=['POST'])
def login():
    g_account_name = request.form['google_account']
    auth.login_user(g_account_name)
    return json.dumps({'success':True}), 200, {'ContentType':'application/json'}

@app.route('/buildings')
def get_building():
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    building_list = Building.query.all()
    return jsonify([building.toDict() for building in building_list])

@app.route('/sports')
def get_sport():
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    sports_list = Sport.query.all()
    return jsonify([sport.get_title() for sport in sports_list])

@app.route('/facility/sport/<string:sport>')
def get_facilities_from_sport(sport):
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    facilityList = Facility.query.filter_by(sport=sport)
    return jsonify([facility.toDict() for facility in facilityList])

@app.route('/facility/building/<string:building>')
def get_facilities_from_building(building):
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    facilityList = Facility.query.filter_by(building=building)
    return jsonify([facility.toDict() for facility in facilityList])

@app.route('/building/facility/<string:facilityName>')
def get_building_from_facility(facilityName):
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    facility = Facility.query.get(facilityName)
    print facility.getBuilding()
    building = Building.query.get(facility.getBuilding())
    return jsonify(building.toDict())

@app.route('/facility/<string:facilityName>')
def get_facility_by_name(facilityName):
    if not (auth.is_auth_user() or auth.is_auth_staff_user()):
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    facility = Facility.query.get(facilityName)
    return jsonify(facility.toDict())

@app.route('/facility/setAvailability/', methods=['POST'])
def setAvailability():
    if not auth.is_auth_staff_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    facilityName=request.form['name']
    availabilityValue=int(request.form['availability'])
    facility = Facility.query.get(facilityName)
    facility.setAvailability(availabilityValue)
    db.session.commit()
    #socketio.emit('notify', {'name':facilityName, 'availability': availabilityValue}, room=facilityName)
    socketio.emit('notify', {'name':facilityName, 'availability': availabilityValue}, broadcast=True)
    return json.dumps({'success':True}), 200, {'ContentType':'application/json'}

@app.route('/facility/addRating/', methods=['POST'])
def addRating():
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    rating=request.form['rating']
    facilityName=request.form['facility']
    facility = Facility.query.get(facilityName)
    newRating = facility.addRating(float(rating))
    db.session.commit()
    return json.dumps({'success':True, 'newRating':newRating}), 200, {'ContentType':'application/json'}

@app.route('/add/sport/<string:title>')
def add_sport(title):
    db.session.add(Sport(title))
    db.session.commit()
    sports_list = Sport.query.all()
    return jsonify([sport.get_title() for sport in sports_list])


@app.route("/myFacilities")
def get_staff_availibility():
    if not auth.is_auth_staff_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    userName = session.get('user', None)
    buildingName = Staff.query.get(userName).get_permission()
    facilityList = Facility.query.filter_by(building=buildingName)
    return jsonify({
        'buildingName': buildingName,
        'facilityList': [facility.toDict() for facility in facilityList]})

@app.route('/getSubscribed')
def get_my_subscriptions():
    if not auth.is_auth_user():
        return json.dumps({'failure':'Not authenticated'}), 403, {'ContentType':'application/json'}
    userName = session.get('user', None)
    subscriptionList = Subscription.query.filter_by(user_id=userName)
    return jsonify([subscription.getFacilityDict() for subscription in subscriptionList])

@socketio.on("subscribe")
def subscribe_facility(data):
    print '------subscribe--------'
    jsonParam =json.loads(data)
    username = jsonParam['username']
    facilityName = jsonParam['facility']
    join_room(facilityName)
    db.engine.execute("delete from subscription where user_id='" + username  + "' AND facility='" + facilityName + "';")
    db.session.add(Subscription(username, facilityName))
    db.session.commit()
    log_request()
    return json.dumps({'success':True}), 200, {'ContentType':'application/json'}

@socketio.on("unsubscribe")
def unsubscribe_facility(data):
    print '------unsubscribe--------'
    jsonParam =json.loads(data)
    username = jsonParam['username']
    facilityName = jsonParam['facility']
    leave_room(facilityName)
    db.engine.execute("delete from subscription where user_id='" + username  + "' AND facility='" + facilityName + "';")
    log_request()
    return json.dumps({'success':True}), 200, {'ContentType':'application/json'}

def log_request():
    timestamp = strftime('[%Y-%b-%d %H:%M]')
    print '%s %s %s %s %s' % (timestamp, request.remote_addr, request.method, request.scheme, request.full_path)

@app.after_request
def after_request(response):
    timestamp = strftime('[%Y-%b-%d %H:%M]')
    print '%s %s %s %s %s %s' % (timestamp, request.remote_addr, request.method, request.scheme, request.full_path, response.status)
    return response



if __name__ == '__main__':
    app.run(host='0.0.0.0', threaded=True, debug=True)
    #socketio.run(app, host='0.0.0.0')
