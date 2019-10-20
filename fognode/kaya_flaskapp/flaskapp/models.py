
from flaskapp import db,login_manager
from flask_login import UserMixin


#Reloads the user from the user id.
@login_manager.user_loader
def load_user(user_id):
    return UserDoctor.query.get(int(user_id))

class UserDoctor(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    name = db.Column(db.String(100),nullable=False)
    profile_image = db.Column(db.String(100),default = "URILogo_default.png",nullable=False)
    email = db.Column(db.String(1000),nullable=False)
    password = db.Column(db.String(600),nullable=False)
    #Defines a one to many relationship from dr to patient score table.
    score_db = db.relationship('score_db', backref='dorctorwho', lazy=True)

class ktube_db(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    data = db.Column(db.LargeBinary)
    # Add the name for the person who is logged in.
    # name = db.Column(db.String(100),nullable=False) nullable=False).

class score_db(db.Model):
    id = db.Column(db.Integer, primary_key=True, nullable=False)
    doctor_id = db.Column(db.Integer, db.ForeignKey('user_doctor.id'))
    # Adding all the hand exercises.
    resting_hands_score = db.Column(db.Integer,nullable=False)
    holding_hands_out_score = db.Column(db.Integer,nullable=False)
    hand_flip_score = db.Column(db.Integer,nullable=False)
    close_grip_score = db.Column(db.Integer,nullable=False)
    finger_tap_score = db.Column(db.Integer,nullable=False)
    finger_to_nose  = db.Column(db.Integer,nullable=False)