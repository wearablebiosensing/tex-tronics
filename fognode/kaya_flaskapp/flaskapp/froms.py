
from flask_wtf import FlaskForm
from wtforms import  StringField, TextAreaField, PasswordField, validators,SubmitField  #for form validation.
from wtforms.validators import DataRequired, Length, EqualTo, Email


# Register Form class specifies all the fields for the wtf forms.
class RegisterFormDoctors(FlaskForm):
    name = StringField('Name' ,validators=[ ]) #'''Length(min = 1, max = 100)'''
    email = StringField('Email', validators=[Email()])#''',Length(min = 1, max = 1000)'''
    password = PasswordField('Password',
                 validators=[ DataRequired(), 
                  ])
    comfirm = PasswordField('Confirm Password',
                validators=[DataRequired(),
                    EqualTo('password', 
                    message = "Passwords do not match")] )
    submit = SubmitField("Sign up")

class LogInFormDoctors(FlaskForm):

    email = StringField('Email', 
                         validators=[Email(), 
                         Length(min = 1, max = 1000)])
    password = PasswordField('Password',
                 validators=[ DataRequired(), 
                  ])
    submit = SubmitField("Log in")