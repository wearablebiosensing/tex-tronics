
###########################
#  Author @Shehjar Sadhu  #
#  Date August 8th 2019   #
###########################

import sqlite3 as sql  # Database.
from passlib.hash import sha256_crypt  # for password encryption
#from flask_login import current_user,logout_user  # for handeling log ins and log outs.
# from flask import Flask,request,render_template,flash,redirect,url_for,session,logging #flask  standard.
# from flask_wtf import FlaskForm
# from wtforms import Form, StringField, TextAreaField, PasswordField, validators  #for form validation.
from forms import RegisterFormPatients, LogInFormPatients

# Database path the .db file is stored here on the disk.
DATABASE = "database/users_kaya2.db"
app = Flask(__name__)
app.config['SECRET_KEY'] = 'kaya'
#Configuration setting for the sqlite databses.
app.config['MYSQL_CURSORCLASS'] ='DictCursor'

# Connect to SQLlite3 in memory databses.
connection = sql.connect(DATABASE)
c = connection.cursor()

# Create tables if does not exists.
c.execute(""" CREATE TABLE IF NOT EXISTS patients(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, email VARCHAR, password VARCHAR )""")
c.execute(""" CREATE TABLE IF NOT EXISTS doctors(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, email VARCHAR, password VARCHAR )""")

connection.commit()
connection.close()

@app.route('/')
def home():
    return render_template("home.html")

#Register page also takes in POST method.
@app.route('/registerPatients',methods = ['POST', 'GET'])
def register():
    #Createes an object of the form class.
    form  = RegisterFormPatients(request.form)
    #If the resuest is POST and validate the form fields.
    if request.method == 'POST' and form.validate():
        #If the form is sublitted do ...
        # Get the email and name form HTML.
        name = form.name.data
        email = form.email.data
        password = sha256_crypt.encrypt(str(form.password.data))
        # Connect to the sql databse.
        with sql.connect(DATABASE) as con:
             cur = con.cursor()
             # Insert into the databse.
             cur.execute(" INSERT INTO patients(name,email,password) VALUES (?,?,?)" , (name,email,password) )
             con.commit()
             flash(" You are now registered ")
             redirect(url_for("login"))
    return render_template('register.html',form = form)

#Log-in page also need POST  methods.
@app.route('/login',methods = ['POST', 'GET'])
@app.route('/<user>',methods = ['POST', 'GET'])
def login(user=None):
    form  = LogInFormPatients(request.form)
    # Can only get names and emails using POST methods.
    if request.method == 'POST':
        # Get the user name and password from the from.
        # Name = request.form['name']
        name = request.form['name']
        password_candidate = request.form['password']
        return redirect(url_for('profile.html'))
        # Connect to the sql databse.
        with sql.connect(DATABASE) as con:
             cur = con.cursor()
             #Select from the databse the users.
             result = cur.execute(" SELECT * FROM patients WHERE name = ?" ,[name])
             #If the user exists in the databse.
             if result == True:
                 #Get the stored hash password. Gets the 1st one only.
                 data = cur.fetchone()
                 password = data['password']
                  #Compare the passwords
                 if sha256_crypt.verify(password_candidate,password):
                     #Prints to consol
                     app.logger.info('PASSWORD MATCHED')
            #  else:
            #      #Prints to consol
            #      app.logger.info('USER NOT FOUND!')

             con.commit()
    return render_template("login.html",user=user,form=form)

@app.route('/profile',methods = ['POST', 'GET'])
def profile():
     if request.method == 'POST':
      profile = request.form
      name = request.form['name']
      email = request.form['email']
      return render_template("profile.html", name=name,email=email)

if __name__ =="__main__":
    app.secret_key = '123456'
    app.run(debug=True)
