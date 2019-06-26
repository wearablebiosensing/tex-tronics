Python 3 MQTT Data Logger

This logger creates a logfile for all messages for all topics to which this MQTT client
is subscribed.
Note: by default it will only log changed messages. This is for sensors 
that send out their state a regular intervals but that state doesn't change.

You need to provide the script with:
    List of topics to monitor
    broker name and port
    username and password if needed.
    base log directory and number of logs have defaults
    
Valid command line Options:
	--help <help>
	-h <broker> 
	-b <broker> 
	-p <port>
	-t <topic> 
	-q <QOS>
	-v <verbose>
	-d logging debug 
	-n <Client ID or Name>
	-u Username 
	-P Password
	-s <store all data>
	-l <log directory default= mlogs> 
	-r <number of records default=100>
	-f <number of log files default= unlimited"

Example Usage:
You will always need to specify the broker name or IP address and the topics to log

1. Specify broker and topics:     python3 mqtt-data-logger.py -t wbl/#

2. Specify broker and multiple topics:    python3 mqtt-data-logger.py -t wbl/# -t  kaya/#

3. Log All Data:    python3 mqtt-data-logger.py b fog.wbl.cloud -t wbl/# -s 

4. Specify the client name used by the logger:    python3 mqtt_data_logger.py b fog.wbl.cloud -t wbl/# -n data-logger

5. Specify the log directory:    python3 mqtt_data_logger.py b fog.wbl.cloud -t wbl/# -l mylogs

---------
Logger

The class is implemented in a module called m_logger.py (message logger).
To create an instance you need to supply three parameters:

    The log directory- defaults to mlogs
    Number of records to log per log- defaults to 1000
    Number of logs. 0 for no limit.- defaults to 0

log=m_logger(log_dir="logs",log_recs=1000,number_logs=0):
The logger creates the log files in the directory using the current date and time for the directory names.
The format is month-day-hour-minute
You can log data either in plain text format or JSON format.
To log data as JSON encoded data call the json_log(data) method.
To log data either in plain text then use the log_data(data) method.
Both method takes a single parameter containing the data to log as a string, list or dictionary..
e.g. : log.log_data(data) or log.log_json(data)
#The log file will contain the data as 
#plain text or  JSON encoded data strings
#each on a newline.
The logger will return True if successful and False if not.
To prevent loss of data in the case of computer failure the logs are continuously flushed to disk .
 
