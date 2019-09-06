
from flask import render_template, url_for, flash, redirect
from flaskapp.froms import RegisterFormDoctors, LogInFormDoctors
from flaskapp import app
from flaskapp.models import UserDoctor


@app.route('/')
def home():
    return render_template('home.html')

@app.route('/about')
def about():
    return render_template('about.html')

@app.route('/registerdoctor',methods = ["GET", "POST"])
def register_doctors():
    form = RegisterFormDoctors()
    if form.validate_on_submit():
        flash(f"Account is created for{form.name.data} !","success")
        return redirect(url_for("home"))
    return render_template('registerDoctors.html',form=form)

@app.route('/logindoctor',methods = ["GET", "POST"])
def login_doctors():
    form = LogInFormDoctors()
    return render_template('loginDoctors.html',form=form)
