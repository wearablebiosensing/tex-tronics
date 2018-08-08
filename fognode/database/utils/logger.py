import paho.mqtt.client as mqtt
import csv
from io import StringIO
import json
import os
import datetime
import sqlite3
import matplotlib.pyplot as plt
import argparse

parser = argparse.ArgumentParser(description='Log kaya formatted mqtt data.')
parser.add_argument('--host',metavar='HOST',type=str,nargs=1,default=["fog.wbl.cloud"],help='set the mqtt host')
                   
args = parser.parse_args()

HOST = args.host[0]

#Mac addresses for according sensors
SENSORS = { "CB:4C:FC:A7:0F:17" : "Left_Shoe", "D4:72:11:8B:5A:4D" : "Right_Shoe" }

#function to process json packed csv messages
def process_msg(client, userdata, msg):

    print('-' * 20 + " new message " + '-' * 20)

    #process message
    raw = msg.payload.decode("utf-8").replace('\n','\\n')
    json_record = json.loads( raw )
    print("Date {} | Sensor {} | Exercise {}".format(json_record["Date"],json_record["Sensor_ID"],json_record["Exercise_ID"]))
    
    timestamp = datetime.datetime.strptime(json_record["Date"],"%m-%d-%Y %H:%M:%S:%f")
    
    #perform local save
    directory = str(timestamp.year) + "/" \
            + str(timestamp.month) + "/" \
            + str(timestamp.day) + "/" \
            + json_record["Exercise_ID"] + "/" \
            + SENSORS[json_record["Sensor_ID"]] + "/"
            
    if not os.path.exists(directory):
        os.makedirs(directory)
    with open(directory + str(timestamp.timestamp()) + ".csv","w") as file:
        file.write(json_record["Data"])
        file.close()
        
    # plt.plot(np.mean(h5[:int(len(h5) - len(h5) % res)].reshape( int(len(h5) / res),res),axis=1),'m',label='online actor critic')
    
    # plt.title(name)
    # plt.legend()
    # plt.savefig(file_name+name+''.join(random.choices(string.ascii_uppercase + string.digits, k=10))+'.png')
    # plt.show()
       
    # #create connection and cursor
    # connection = sqlite3.connect("kaya.db")
    # crsr = connection.cursor()
    
    # #query the maximum exercise ID
    # query_find_max = "SELECT MAX(EX_ID) FROM TABLE WHERE 1"
    
    graph_data = []
    reader = csv.reader(StringIO(json_record["Data"]))
    next(reader)
    for row in reader:
        graph_data.append(float(row[3]))
        
    plt.plot(graph_data)
    plt.show()
       
    # connection.commit()
    # connection.close()
    
    
       
#create client and bind functions
client = mqtt.Client()
client.on_message = process_msg
client.on_connect = lambda client,userData,flags,rc: print("Connection Successful")

#connect and loop
client.connect(HOST,1883,60)
client.subscribe("kaya/#")
client.loop_forever()