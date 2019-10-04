
from flaskapp import db,login_manager
from flask_login import UserMixin


#Reloads the user from the user id.
@login_manager.user_loader
def load_user(user_id):
    return UserDoctor.query.get(int(user_id))

class UserDoctor(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    name = db.Column(db.String(100),nullable=False)
    email = db.Column(db.String(1000),nullable=False)
    password = db.Column(db.String(600),nullable=False)

class ktube_db(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True,nullable=False)
    fileName = db.Column(db.String(10000),nullable=False)
    data = db.Column(db.LargeBinary)

