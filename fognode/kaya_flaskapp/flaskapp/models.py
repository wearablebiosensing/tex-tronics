
#####################################
# Author: Shehjar Sadhu.            #
# Project: Kaya web scoring portal. #
# Date: December 2019.              #
#####################################

from flaskapp import db,login_manager
from flask_login import UserMixin

# Reloads the user from the user id.
@login_manager.user_loader
def load_user(user_id):
    return UserDoctor.query.get(int(user_id))

class UserDoctor(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    name = db.Column(db.String(100),nullable=False)
    profile_image = db.Column(db.String(100),default = "URILogo_default.png",nullable=False)
    email = db.Column(db.String(1000),nullable=False)
    password = db.Column(db.String(600),nullable=False)
    # Defines a one to many relationship from dr to patient score table.
    score_db = db.relationship('score_db', backref='dorctorwho', lazy=True)

class UserPatient(db.Model):
        id = db.Column(db.Integer,primary_key=True,nullable=False)
        name = db.Column(db.String(100),nullable=False)
        close_grip = db.relationship('close_grip', backref='patientwho', lazy=True)
        resting_hands_on_thighs =  db.relationship('resting_hands_on_thighs', backref='patientwho', lazy=True)
        holding_hands_out_straight =  db.relationship('holding_hands_out_straight', backref='patientwho', lazy=True)
        finger_tap =  db.relationship('finger_tap', backref='patientwho', lazy=True)
        finger_to_nose =  db.relationship('finger_to_nose', backref='patientwho', lazy=True)
class close_grip(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class resting_hands_on_thighs(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class holding_hands_out_straight(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class finger_tap(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class finger_to_nose(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class hand_flip(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class heel_stomp(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class toe_tap(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
class walk_steps(db.Model):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    video_file = db.Column(db.LargeBinary)
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
    
# ##########################
# # Not in use for testing #
# ##########################
class ktube_db(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    data = db.Column(db.LargeBinary)
    # Add the name for the person who is logged in.
    # name = db.Column(db.String(100),nullable=False) nullable=False).

class score_db(db.Model):
    id = db.Column(db.Integer, primary_key=True, nullable=False)
    doctor_id = db.Column(db.Integer, db.ForeignKey('user_doctor.id'))
    patient_id = db.Column(db.Integer, db.ForeignKey('user_patient.id'))
    # Adding all the hand exercises.
    resting_hands_lscore = db.Column(db.Integer)
    resting_hands_rscore = db.Column(db.Integer)
    
    holding_hands_out_lscore = db.Column(db.Integer)
    holding_hands_out_rscore = db.Column(db.Integer)
    
    hand_flip_lscore = db.Column(db.Integer)
    hand_flip_rscore = db.Column(db.Integer)
    
    close_grip_lscore = db.Column(db.Integer)
    close_grip_rscore = db.Column(db.Integer)
    
    finger_tap_lscore = db.Column(db.Integer)
    finger_tap_rscore = db.Column(db.Integer)
    
    finger_to_nose_lscore   = db.Column(db.Integer)
    finger_to_nose_rscore  = db.Column(db.Integer)

    finger_to_nose_lscore   = db.Column(db.Integer)
    finger_to_nose_rscore  = db.Column(db.Integer)

    finger_to_nose_lscore   = db.Column(db.Integer)
    finger_to_nose_rscore  = db.Column(db.Integer)

    heel_stomp_lscore   = db.Column(db.Integer)
    heel_stomp_rscore  = db.Column(db.Integer)

    toe_tap_lscore   = db.Column(db.Integer)
    toe_tap_rscore  = db.Column(db.Integer)

    walk_steps_lscore   = db.Column(db.Integer)
    walk_steps_rscore  = db.Column(db.Integer)