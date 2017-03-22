import os
import csv

params = []
average = []

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
    params.append(next(readerObj))
    next(readerObj)
    # for each row in the file
    for row in readerObj:
        # add row to list
        csvRows.append(row)
    csvFileObj.close()

    total = 0
    for row in csvRows:
        total += float(row[3])

    average.append(total / len(csvRows))

with open('consolidate.csv', 'w') as csv_file:
    csv_writer = csv.writer(csv_file)
    csv_writer.writerow([params])
    for val in average:
        csv_writer.writerow([val])
