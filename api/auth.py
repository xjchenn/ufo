from server import db
from flask import Flask, session, g
from models import Sport, Facility, Building, User, Staff, Subscription, Comment

class Auth:
    def _create_user(self, user_id):
        new_user = User(user_id)
        db.session.add(new_user)
        db.session.commit()

    def login_staff(self, user_id, password):
        session.pop('user', None)
        user = Staff.query.get(user_id)
        if user and user.authenticate(password):
            session['user'] = user_id
            return True
        return False

    def login_user(self, user_id):
        session.pop('user', None)
        user = User.query.get(user_id)
        if not user:
            self._create_user(user_id)
        session['user'] = user_id

    def _is_authenticated(self):
        return session.get('user', None)

    def is_auth_user(self):
        user_id = self._is_authenticated()
        return user_id and (User.query.get(user_id) is not None or Staff.query.get(user_id) is not None)

    def is_auth_staff_user(self):
        user_id = self._is_authenticated()
        return user_id and Staff.query.get(user_id) is not None

    #def logout_user(self):
