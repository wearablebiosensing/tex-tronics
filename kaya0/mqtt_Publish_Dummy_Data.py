#------------------------------------------
#--- Author: Nick
#--- Version: 1.0
#--- Python Ver: 2.7
#------------------------------------------


import paho.mqtt.client as mqtt
import random, threading, json
from datetime import datetime

#====================================================
# MQTT Settings 
MQTT_Broker = "127.0.0.1"
MQTT_Port = 1883
Keep_Alive_Interval = 45
MQTT_Topic_Data = "kaya/patient/data"


#====================================================

def on_connect(client, userdata, rc):
	if rc != 0:
		pass
		print "Unable to connect to MQTT Broker..."
	else:
		print "Connected with MQTT Broker: " + str(MQTT_Broker)

def on_publish(client, userdata, mid):
	pass
		
def on_disconnect(client, userdata, rc):
	if rc !=0:
		pass
		
mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_disconnect = on_disconnect
mqttc.on_publish = on_publish
mqttc.connect(MQTT_Broker, int(MQTT_Port), int(Keep_Alive_Interval))		

		
def publish_To_Topic(topic, message):
	mqttc.publish(topic,message)
	print ("Published: " + str(message) + " " + "on MQTT Topic: " + str(topic))
	print ""


#====================================================
# FAKE SENSOR 
# Dummy code used as Fake Sensor to publish some random values
# to MQTT Broker

toggle = 0

def publish_Fake_Sensor_Values_to_MQTT():
	threading.Timer(3.0, publish_Fake_Sensor_Values_to_MQTT).start()
	global toggle
	if toggle == 0:
		Patient_Fake_Value = float("{0:.2f}".format(random.uniform(50, 100)))

		Patient_Data = {}
		Patient_Data['PatientID'] = "Dummy-1"
		Patient_Data['Date'] = (datetime.today()).strftime("%d-%b-%Y %H:%M:%S:%f")
		Patient_Data['Outcomes'] = Patient_Fake_Value
		patient_json_data = json.dumps(Patient_Data)

		print "Publishing fake Patient Value: " + str(Patient_Fake_Value) + "..."
		publish_To_Topic (MQTT_Topic_Outcomes, patient_json_data)
		toggle = 1

	else:
		Data_Fake_Value = float("{0:.2f}".format(random.uniform(1, 30)))

		Data_Data = {}
		Data_Data['Sensor_ID'] = "Dummy-2"
		Data_Data['Date'] = (datetime.today()).strftime("%d-%b-%Y %H:%M:%S:%f")
		Data_Data['Data'] = Data_Fake_Value
		data_json_data = json.dumps(Data_Data)

		print "Publishing fake Data Value: " + str(Data_Fake_Value) + "..."
		publish_To_Topic (MQTT_Topic_Data, data_json_data)
		toggle = 0


publish_Fake_Sensor_Values_to_MQTT()

#====================================================
