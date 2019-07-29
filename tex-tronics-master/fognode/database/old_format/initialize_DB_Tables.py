#------------------------------------------
#--- Author: Nick
#--- Version: 1.0
#--- Python Ver: 2.7
#------------------------------------------

import sqlite3

# SQLite DB Name
DB_Name =  "kaya"

# SQLite DB Table Schema
TableSchema="""
create table practice (
  id integer primary key autoincrement,
  PatientID text,
  Date_n_Time text,
  Outcomes text
);

create table data (
  id integer primary key autoincrement,
  EntryID integer,
  ExerciseID integer,
  [Date] text,
  SensorIDTag text,
  ExerciseIDTag text,
  TimeDelta float,
  Thumb float,
  [Index] float,
  Middle float,
  Ring float,
  Pinky float
);
"""

#Connect or Create DB File
conn = sqlite3.connect(DB_Name)
curs = conn.cursor()

#Create Tables
sqlite3.complete_statement(TableSchema)
curs.executescript(TableSchema)

#Close DB
curs.close()
conn.close()
