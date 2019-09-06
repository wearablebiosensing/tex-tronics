
from flaskapp import db

class UserDoctor(db.Model):
    id = db.Column(db.Integer,primary_key=True)
    name = db.Column(db.String(100))
    email = db.Column(db.String(1000))
    password = db.Column(db.String(600))

    def __repr__(self):
        return f"UserDoctor('{self.name}','{self.email}')"
