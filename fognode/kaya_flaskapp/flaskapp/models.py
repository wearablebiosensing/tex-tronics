
from flaskapp import db,login_manager
from flask_login import UserMixin

@login_manager.user_loader
def load_user(user_id):
    return UserDoctor.query.get(int())

class UserDoctor(db.Model,UserMixin):
    id = db.Column(db.Integer,primary_key=True)
    name = db.Column(db.String(100))
    email = db.Column(db.String(1000))
    password = db.Column(db.String(600),nullable=False)

    def __repr__(self):
        return f"UserDoctor('{self.name}','{self.email}')"
