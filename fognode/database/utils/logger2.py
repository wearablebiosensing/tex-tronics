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
SENSORS = { "FF:13:4E:9E:DF:2A" : "Left_Shoe", "EA:5A:11:02:8F:90" : "Right_Shoe", "EA:EB:C9:B6:1F:5A" : "Left_Glove", "D4:72:11:8B:5A:4D" : "Right_Glove" }

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

    graph_data1 = []
    graph_data2 = []
    graph_data3 = []
    graph_data4 = []
    graph_data5 = []
    graph_data6 = []
    graph_data7 = []
    graph_data8 = []
    graph_data9 = []

    reader = csv.reader(StringIO(json_record["Data"]))
    next(reader)
    for row in reader:
        graph_data1.append(float(row[3]))
        graph_data2.append(float(row[4]))
        graph_data3.append(float(row[6]))
        graph_data4.append(float(row[7]))
        graph_data5.append(float(row[8]))
        graph_data6.append(float(row[9]))
        graph_data7.append(float(row[10]))
        graph_data8.append(float(row[11]))
        graph_data9.append(float(row[12]))



    #plt.plot(graph_data)
    #plt.show()

    fig, ((ax1, ax2, ax3), (ax4,ax5,ax6),(ax7, ax8,ax9)) = plt.subplots(3, 3, sharex=False)
    fig.suptitle('Kaya Data Check')
    ax1.plot(graph_data1)
    ax2.plot(graph_data2, 'tab:orange')
    ax3.plot(graph_data3, 'tab:green')
    ax4.plot(graph_data4, 'tab:red')
    ax5.plot(graph_data5)
    ax6.plot(graph_data6, 'tab:orange')
    ax7.plot(graph_data7, 'tab:green')
    ax8.plot(graph_data8, 'tab:red')
    ax9.plot(graph_data9)

    for ax in fig.get_axes():
        ax.label_outer()

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
