import os
import csv

params = []
vals = []

dir_files = sorted(os.listdir(os.getcwd()))

for csvFileName in dir_files:
    # if not a .csv file, skip
    if not csvFileName.endswith('.csv'):
        continue

    print('Reading ' + csvFileName)
    csvRows = []
    # open .csv file
    csvFileObj = open(csvFileName)
    readerObj = csv.reader(csvFileObj)
    params = [next(readerObj)]
    next(readerObj)
    # for each row in the file
    for row in readerObj:
        # add row to list
        csvRows.append(row)
    csvFileObj.close()

    total = 0
    for row in csvRows:
        if row[1] == "final":
            vals.append(float(row[3]))

with open('consolidate.csv', 'w', newline='\n') as csv_file:
    csv_writer = csv.writer(csv_file)
    csv_writer.writerow([params])
    for val in vals:
        csv_writer.writerow([val])
