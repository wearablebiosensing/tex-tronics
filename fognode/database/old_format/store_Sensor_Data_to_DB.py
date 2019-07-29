#------------------------------------------
#--- Author: Nick
#--- Version: 1.0
#--- Python Ver: 2.7
#------------------------------------------

import csv
import json
import sqlite3
from io import StringIO

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
        
    def get_db_record(self,sql_query):
        self.cur.execute(sql_query)
        result = self.cur.fetchall()
        return result

    def __del__(self):
        self.cur.close()
        self.conn.close()

#===============================================================
# Functions to push Sensor Data into Database

# Function to take some data row and insert it into the database
def insert_record(row,table,extra=""):

    db = DatabaseManager()
    query = "insert into {} ".format(table)
    query += "(" + str(list(row.keys()))[1:-1] + ")" + " values " + "(" + str(list(row.values()))[1:-1] + ")"
    query += extra
    db.add_del_update_db_record(query)
    
def query_db(db_query):
    db = DatabaseManager()
    return db.get_db_record(db_query)
    
# takes a dictionary with keys ['Date','Sensor_ID','Exercise_ID','Data'] 
# Date is in format like 07-23-2018 11:37:26:886
# Sensor_ID is the sensor MAC addresss
# Exercise_ID is the name of the exercise it is used for
# Data is a CSV formatted string of format [Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky]
def insert_exercise_record(dict):

    csv_data = csv.reader(StringIO(dict["Data"].strip()))
    query_id = query_db("SELECT MAX(EntryID) FROM data") #get next id to use
    entry_id = 0 if query_id[0][0] is None else query_id[0][0] + 1 #if table is empty use 0 else use max + 1

    # clear header entry
    next(csv_data)
    
    #go through the attached csv and make an insertion for each sensor update
    for row in csv_data:
        entry = {}
        # format entry for sql table columns and insert corresponding data from the json formatted data
        
        #entries which are the same for this entry id
        entry["EntryID"] = entry_id # the id for this particular record
        entry["ExerciseID"] = 0 # TODO: add exercise identifers in the future
        entry["Date"] = dict["Date"]
        entry["SensorIDTag"] = dict["Sensor_ID"]
        entry["ExerciseIDTag"] = dict["Exercise_ID"]

        #entries which are different for each csv sensor record
        entry["TimeDelta"] = row[2]
        entry["Thumb"] = row[3]
        entry["Index"] = row[4]
        entry["Middle"] = row[5]
        entry["Ring"] = row[6]
        entry["Pinky"] = row[7]
        
        #insert into database
        insert_record(entry,"data")    
        
    print("Inserted Patient Record Into Data")
    
#===============================================================
# Older Methods

# Function to save Patient Outcomes to DB Table
# def KayaSample(jsonData):
    # #Parse Data 
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Outcomes = json_Dict['Outcomes']
    
    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into practice (PatientID, Date_n_Time, Outcomes) values (?,?,?)",[PatientID, Data_and_Time, Outcomes])
    # del dbObj
    # print("Inserted Patient Outcomes into Database.")
    # print("")

# def KayaPractice(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into PracticeSessions(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Practice Session into Database.")
    # print("")

# # Function to save Patient Data to DB Table
# def KayaData(jsonData):
    # #Parse Data 
    # json_Dict = json.loads(jsonData)
    # SensorID = json_Dict['Sensor_ID']
    # Data_and_Time = json_Dict['Date']
    # Data = json_Dict['Data']
    
    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into data (SensorID, Date_n_Time, Data) values (?,?,?)",[SensorID, Data_and_Time, Data])
    # del dbObj
    # print("Inserted Patient Data into Database.")
    # print("")

# def KayaDevice(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # DeviceName = json_Dict['DeviceName']
    # Notes = json_Dict['Notes']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Devices(PatientID, DeviceName, Notes) values (?,?,?)",[PatientID, DeviceName, Notes])
    # del dbObj
    # print("Inserted Patient Device into Database.")
    # print("")

# def KayaArise(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Arise(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Arise into Database.")
    # print("")

# def KayaFingerTap(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into FingerTap(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient FingerTap into Database.")
    # print("")

# def KayaGait(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Gait(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Gait into Database.")
    # print("")

# def KayaGrasp(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Grasp(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Grasp into Database.")
    # print("")

# def KayaHandFlip(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into HandFlip(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient HandFlip into Database.")
    # print("")

# def KayaHeelTap(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into HeelTap(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Heel Tap into Database.")
    # print("")

# def KayaRestingTremors(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into RestingTermors(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient RestingTremors into Database.")
    # print("")

# def KayaStanding(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Standing(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Standing into Database.")
    # print("")

# def KayaStomping(jsonData):
    # #Parse Data
    # json_Dict = json.loads(jsonData)
    # PatientID = json_Dict['PatientID']
    # Data_and_Time = json_Dict['Date']
    # Notes = json_Dict['Notes']
    # Data = json_Dict['Data']

    # #Push into DB Table
    # dbObj = DatabaseManager()
    # dbObj.add_del_update_db_record("insert into Stomping(PatientID, Date_and_Time, Notes, Data) values (?,?,?,?)",[PatientID, Data_and_Time, Notes, Data])
    # del dbObj
    # print("Inserted Patient Stomping into Database.")
    # print("")

# #===============================================================
# # Master Function to Select DB Funtion based on MQTT Topic

# def sensor_Data_Handler(Topic, jsonData):
    # if Topic == "kaya/patient/practice":
        # KayaPractice(jsonData)
    # elif Topic == "kaya/patient/data":
        # KayaData(jsonData)    
    # elif Topic == "kaya/patient/device":
        # KayaDevice(jsonData)
    # elif Topic == "kaya/patient/arise":
        # KayaArise(jsonData)
    # elif Topic == "kaya/patient/fingertap":
        # KayaFingerTap(jsonData)
    # elif Topic == "kaya/patient/gait":
        # KayaGait(jsonData)
    # elif Topic == "kaya/patient/grasp":
        # Kayagrasp(jsonData)
    # elif Topic == "kaya/patient/handflip":
        # KayaHandFlip(jsonData)
    # elif Topic == "kaya/patient/heeltap":
        # KayaHeelTap(jsonData)
    # elif Topic == "kaya/patient/RestingTremors":
        # KayaRestingTremors(jsonData)
    # elif Topic == "kaya/patient/Standing":
        # KayaStanding(jsonData)
    # elif Topic == "kaya/patient/Stomping":
        # KayaStomping(jsonData)
    