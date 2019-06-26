
import csv
import argparse
import os
import os.path
import matplotlib.pyplot as plt
import numpy as np
import math

def get_files(target_dir):
    item_list = os.listdir(target_dir)

    file_list = list()
    for item in item_list:
        item_dir = os.path.join(target_dir,item)
        if os.path.isdir(item_dir):
            file_list += get_files(item_dir)
        else:
            file_list.append(item_dir)
    return file_list

parser = argparse.ArgumentParser(description='Graph CSV.')
parser.add_argument('path', metavar='path', type=str, nargs=1,
                    help='folder containing csv files to graph')
                    
args = parser.parse_args()
path = args.path[0]
files = get_files(path)

for file in files:
    with open(file,"r") as file:
        reader = csv.reader(file)
        next(reader)
        buff1 = []
        buff2 = []
        for row in reader:
            buff1.append(float(row[3]))
            buff2.append(float(row[4]))
            
        size = len(buff1)
        plt.plot(buff1[::math.ceil(size / 1024)])
        plt.plot(buff2[::math.ceil(size / 1024)])
        plt.show()
