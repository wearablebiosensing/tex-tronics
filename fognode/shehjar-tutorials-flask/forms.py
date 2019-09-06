

from wtforms import Form, StringField, TextAreaField, PasswordField, validators,SubmitField  #for form validation.
from wtforms.validators import DataRequired, Length, EqualTo, Email


# Register Form class specifies all the fields for the wtf forms.
class RegisterFormPatients(Form):
    name = StringField('Name' ,validators=[Length(min = 1, max = 100)])
    email = StringField('Email', validators=[Email(), Length(min = 1, max = 1000)])
    password = PasswordField('Password',
                 validators=[ DataRequired(), 
                  ])
    comfirm = PasswordField('Confirm Password',
    validators=[DataRequired(),
                EqualTo('password', 
                message = "Passwords do not match")] )
    submit = SubmitField("Sign up")

class LogInFormPatients(Form):
    name = StringField('Name' ,
                        validators=[
                        Length(min = 1, max = 100)])
    email = StringField('Email', 
                         validators=[Email(), 
                         Length(min = 1, max = 1000)])
    submit = SubmitField("Sign up")