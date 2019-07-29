#------------------------------------------
#--- Author: Nick
#--- Version: 1.0
#--- Python Ver: 2.7
#------------------------------------------

import paho.mqtt.client as mqtt
from store_Sensor_Data_to_DB import sensor_Data_Handler,query_db,insert_exercise_record

import csv
import json
import sqlite3
from io import StringIO

# MQTT Settings 
MQTT_BROKER = "fog.wbl.cloud"
MQTT_PORT = 1883
KEEP_ALIVE_INTERVAL = 45
MQTT_TOPIC = "kaya/patient/#"
MQTT_TOPIC_DATA = "kaya/patient/data" # where data is sent edge device - > service
MQTT_TOPIC_DONE = "kaya/patient/done" # where done_exercise flag is sent edge device -> service
MQTT_TOPIC_CALLBACK = "kaya/patient/callback" # where service posts updates
MQTT_TOPIC_PACKAGED_EXERCISE = "kaya/patient/packaged_exercise" # where the service posts finished exercises 

#recurrent exercise progress
progress = {  }
   
#Subscribe to all Sensors at Base Topic
def on_connect(mosq, obj, flags, rc):
    mqttc.subscribe(MQTT_TOPIC, 0)

#Save Data into DB Table
def on_message(mosq, obj, msg):
    # This is the Master Call for saving MQTT Data into DB
    # For details of "sensor_Data_Handler" function please refer "sensor_data_to_db.py"
    print("MQTT Data Received...")
    print("MQTT Topic: " + msg.topic)  
    print("Data: " + msg.payload.decode("utf-8"))
    #sensor_Data_Handler(msg.topic, msg.payload.decode("utf-8"))
    
    if msg.topic == MQTT_TOPIC_DATA:
    
        dict = json.loads(msg.payload.decode("utf-8"))
        
        #insert data into database
        insert_exercise_record(dict)
        
        #get raw data, do some processing on it...
        csv_data = csv.reader(StringIO(dict["Data"].strip()))    
  
        #TODO : do some data processing here
        # ...    
        
    elif msg.topic = MQTT_TOPIC_DONE:
    
        #if data has been collected
        if progress:
        
            #TODO: take and format progress, then send result to mqtt
            
            
            
            progress = {}
        
        else:
            
            mqttc.publish(MQTT_TOPIC_CALLBACK,"No Exercises Were Performed!")
        
def on_subscribe(mosq, obj, mid, granted_qos):
    pass

mqttc = mqtt.Client()

# Assign event callbacks
mqttc.on_message = on_message
mqttc.on_connect = on_connect
mqttc.on_subscribe = on_subscribe

# Connect
mqttc.connect(MQTT_BROKER, int(MQTT_PORT), int(KEEP_ALIVE_INTERVAL))

# Continue the network loop
mqttc.loop_forever()