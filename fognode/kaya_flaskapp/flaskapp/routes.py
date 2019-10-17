
from flask import render_template, url_for, flash, redirect,session,request
from flaskapp.froms import RegisterFormDoctors, LogInFormDoctors, ktube_form
from flaskapp import app,db,bcrypt
from flaskapp.models import UserDoctor,ktube_db
from flask_login import login_user, current_user, logout_user, login_required

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/about')
def about():
    return render_template('about.html')
    
#Registers the doctors and inserts their information in the database
@app.route('/registerdoctor',methods = ["GET", "POST"])
def register_doctors():
    form = RegisterFormDoctors()
    # If form is valid print a message and redirect the users to the log in page
    if form.validate_on_submit():
        # Generate the hased the password using bcrypt API .
        pass_hash = bcrypt.generate_password_hash(form.password.data).decode("utf-8")
        # Create a new user instance.
        user = UserDoctor(name=form.name.data,email=form.email.data, password=pass_hash)
        db.session.add(user)
        db.session.commit()
        flash(f"Account is created for {form.name.data}!","success")
        return redirect(url_for("login_doctors"))
    #Else redirect back to the register page.
    return render_template('registerDoctors.html',form=form)

@app.route('/logindoctor',methods = ["POST", "GET"])
def login_doctors():
    if current_user.is_authenticated:
        return redirect(url_for('home'))
    form = LogInFormDoctors()
    user = UserDoctor.query.filter_by(email=form.email.data).first()
    # Check is the fields in the form are valid.
    if form.validate_on_submit():
        # If they are valid then check if the user exists in the database and check if they entered the correct password.
        if user and bcrypt.check_password_hash(user.password, form.password.data):
            login_user(user)
            flash(f"Logged in {form.email.data}","success")
            return redirect(url_for("home"))
        #Else display error message.
        else:
            flash(f"Not logged in","danger")
    return render_template('loginDoctors.html', title='login',form=form)

@app.route('/profile_doctors', methods = ["POST", "GET"])
@login_required
def doctor_profile():
    # image_file = url_for("static",filename ='images/'+ current_user.image_file)
    return render_template('doctors_profile.html',image_file=image_file)


#Check which file is uploaded by getting the file name from html.
@app.route('/upload',methods = ["POST","GET"])
@login_required
def upload():
    if request.method == "POST":
        file = request.files['inputFile']
        newFile = ktube_db(fileName=file.filename,data=file.read())
        db.session.add(newFile)
        db.session.commit()
        return "Saved" + file.filename + "to the db"
    return render_template('home.html') 

#File uploader page.
@app.route('/ktube',methods = ["POST","GET"])
@login_required
def kaya_tube():
   form = ktube_form()
   if request.method == "POST":
       file = request.files['inputFile']
       return file.filename
   return render_template('ktube.html',form=form) 

@app.route('/signout')
def signout():
    logout_user()
    return redirect(url_for("home"))
