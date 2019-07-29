#------------------------------------------
#--- Author: Nick
#--- Version: 1.0
#--- Python Ver: 2.7
#------------------------------------------


import json
import sqlite3

# SQLite DB Name
DB_Name =  "kaya"

#===============================================================
# Database Manager Class

class DatabaseManager():
	def __init__(self):
		self.conn = sqlite3.connect(DB_Name)
		self.conn.execute('pragma foreign_keys = on')
		self.conn.commit()
		self.cur = self.conn.cursor()
		
	def add_del_update_db_record(self, sql_query, args=()):
		self.cur.execute(sql_query, args)
		self.conn.commit()
		return

	def __del__(self):
		self.cur.close()
		self.conn.close()

#===============================================================
# Functions to push Sensor Data into Database

# Function to save Patient Outcomes to DB Table
def KayaSample(jsonData):
	#Parse Data 
	json_Dict = json.loads(jsonData)
	PatientID = json_Dict['PatientID']
	Data_and_Time = json_Dict['Date']
	Outcomes = json_Dict['Outcomes']
	
	#Push into DB Table
	dbObj = DatabaseManager()
	dbObj.add_del_update_db_record("insert into practice (PatientID, Date_n_Time, Outcomes) values (?,?,?)",[PatientID, Data_and_Time, Outcomes])
	del dbObj
	print "Inserted Patient Outcomes into Database."
	print ""

# Function to save Patient Data to DB Table
def KayaData(jsonData):
	#Parse Data 
	json_Dict = json.loads(jsonData)
	SensorID = json_Dict['Sensor_ID']
	Data_and_Time = json_Dict['Date']
	Data = json_Dict['Data']
	
	#Push into DB Table
	dbObj = DatabaseManager()
	dbObj.add_del_update_db_record("insert into data (SensorID, Date_n_Time, Data) values (?,?,?)",[SensorID, Data_and_Time, Data])
	del dbObj
	print "Inserted Patient Data into Database."
	print ""

def KayaPractice(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into PracticeSessions(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Practice Session into Database."
        print ""

def KayaDevice(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        DeviceName = json_Dict['DeviceName']
        Notes = json_Dict['Notes']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Devices(PatientID, DeviceName, Notes) values (?,?,?)",[PatientID, DeviceName, Notes])
        del dbObj
        print "Inserted Patient Device into Database."
        print ""

def KayaArise(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Arise(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Arise into Database."
        print ""

def KayaFingerTap(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into FingerTap(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient FingerTap into Database."
        print ""

def KayaGait(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Gait(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Gait into Database."
        print ""

def KayaGrasp(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Grasp(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Grasp into Database."
        print ""

def KayaHandFlip(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into HandFlip(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient HandFlip into Database."
        print ""

def KayaHeelTap(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into HeelTap(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Heel Tap into Database."
        print ""

def KayaRestingTremors(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into RestingTermors(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient RestingTremors into Database."
        print ""

def KayaStanding(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Standing(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Standing into Database."
        print ""

def KayaStomping(jsonData):
        #Parse Data
        json_Dict = json.loads(jsonData)
        PatientID = json_Dict['PatientID']
        Data_and_Time = json_Dict['Date']
        Notes = json_Dict['Notes']
	Data = json_Dict['Data']

        #Push into DB Table
        dbObj = DatabaseManager()
        dbObj.add_del_update_db_record("insert into Stomping(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
        del dbObj
        print "Inserted Patient Stomping into Database."
        print ""

#===============================================================
# Master Function to Select DB Funtion based on MQTT Topic

def sensor_Data_Handler(Topic, jsonData):
	if Topic == "kaya/patient/practice":
		KayaPractice(jsonData)
	elif Topic == "kaya/patient/data":
		KayaData(jsonData)	
	elif Topic == "kaya/patient/device":
		KayaDevice(jsonData)
	elif Topic == "kaya/patient/arise":
		KayaArise(jsonData)
	elif Topic == "kaya/patient/fingertap":
		KayaFingerTap(jsonData)
	elif Topic == "kaya/patient/gait":
		KayaGait(jsonData)
	elif Topic == "kaya/patient/grasp":
		Kayagrasp(jsonData)
	elif Topic == "kaya/patient/handflip":
		KayaHandFlip(jsonData)
	elif Topic == "kaya/patient/heeltap":
		KayaHeelTap(jsonData)
	elif Topic == "kaya/patient/RestingTremors":
		KayaRestingTremors(jsonData)
	elif Topic == "kaya/patient/Standing":
		KayaStanding(jsonData)
	elif Topic == "kaya/patient/Stomping":
		KayaStomping(jsonData)

#===============================================================
