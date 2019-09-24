
from flask_wtf import FlaskForm
from wtforms import  StringField, TextAreaField, PasswordField, validators,SubmitField  #for form validation.
from wtforms.validators import DataRequired, Length, EqualTo, Email,ValidationError
from flask_wtf.file import FileField
from flaskapp.models import UserDoctor 


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
    
    #This validates against any names or emails that are already taken.
    def validate_username(self, name):
        user = UserDoctor.query.filter_by(name=name.data).first()
        if user:
            raise ValidationError('That username is taken. Please choose a different one.')

    def validate_email(self, email):
        user = UserDoctor.query.filter_by(email=email.data).first()
        if user:
            raise ValidationError('That email is taken. Please choose a different one.')


class LogInFormDoctors(FlaskForm):

    email = StringField('Email', 
                         validators=[Email(), 
                         Length(min = 1, max = 1000)])
    password = PasswordField('Password',
                 validators=[ DataRequired(), 
                  ])
    submit = SubmitField("Log in")

class ktube_form(FlaskForm):
    file = FileField()
    submit = SubmitField("Upload")
