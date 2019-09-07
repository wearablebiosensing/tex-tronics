
from flask import render_template, url_for, flash, redirect
from flaskapp.froms import RegisterFormDoctors, LogInFormDoctors
from flaskapp import app,db,bcrypt
from flaskapp.models import UserDoctor 
from flask_login import login_user
@app.route('/')
def home():
    return render_template('home.html')

@app.route('/about')
def about():
    return render_template('about.html')

@app.route('/registerdoctor',methods = ["GET", "POST"])
def register_doctors():
    form = RegisterFormDoctors()
    
    #if form valid print a message.
    if form.is_submitted():
        # Hash the password 
        pass_hash = bcrypt.generate_password_hash(form.password.data).decode("utf-8")
            # Create a new user instance.
        user = UserDoctor(name=form.name.data,email=form.email.data, password=pass_hash)
        db.session.add(user)
        db.session.commit()
        flash(f"Account is created!","success")
        return redirect(url_for("login_doctors"))
    return render_template('registerDoctors.html',form=form)

@app.route('/logindoctor',methods = ["GET", "POST"])
def login_doctors():
    form = LogInFormDoctors()
    user = UserDoctor.query.filter_by(email=form.email.data).first()
    if form.is_submitted():
        if user and bcrypt.check_password_hash(user.password, form.password.data):
            login_user(user)
            flash(f"Logged in","success")
            return redirect(url_for("home"))
        else:
            flash(f"Not logged in","danger")
    
    return render_template('loginDoctors.html',form=form)
