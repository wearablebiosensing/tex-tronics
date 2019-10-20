from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_login import LoginManager
from flask_migrate import Migrate

app = Flask(__name__)

app.config['SECRET_KEY'] ="Kaya"
#Set database instances
app.config['SQLALCHEMY_DATABASE_URI'] = "sqlite:///site1.db"
db = SQLAlchemy(app)
migrate = Migrate(app, db)

bcrypt = Bcrypt(app)

#Init log in manager for flask app.
login_manager = LoginManager(app)
login_manager.login_view = 'login'

from flaskapp import routes, models
