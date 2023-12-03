import mysql.connector
import csv
from datetime import datetime
mydb = mysql.connector.connect(
host="localhost",
user="root",
password="Huxy@2020fall",
database="Stock1"
)

mycursor = mydb.cursor()

with open("stock.csv", "r") as ip:
    csv_reader = csv.reader(ip)
    for record in csv_reader:
        line = "INSERT INTO stock VALUES (" + record[0] + ",\"" + record[1] + "\",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\");"
        mycursor.execute(line)

with open("company.csv", "r") as ip:
    csv_reader = csv.reader(ip)

    for record in csv_reader:
        line = "INSERT INTO company VALUES (" + record[0] + ",\"" + record[1] + "\",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\");"
        mycursor.execute(line)

with open("trader.csv", "r") as ip:
    csv_reader = csv.reader(ip)

    for record in csv_reader:
        line = "INSERT INTO trader VALUES (" + record[0] + ",\"" + record[1] + "\",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\");"
        mycursor.execute(line)

with open("broker.csv", "r") as ip:
    csv_reader = csv.reader(ip)

    for record in csv_reader:
        line = "INSERT INTO broker VALUES (" + record[0] + ",\"" + record[1] + "\",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\");"
        mycursor.execute(line)

with open("transaction.csv", "r") as ip:
    csv_reader = csv.reader(ip)

    for record in csv_reader:
        line = "INSERT INTO transation VALUES (" + record[0] + "," + record[1] +",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\",\"" + record[5] + "\",\"" + record[6] + "\",\"" + record[7] + "\");"
        mycursor.execute(line)

#with open("market_data.csv", "r") as ip:
#    csv_reader = csv.reader(ip)

#    for record in csv_reader:
#        line = "INSERT INTO market_data VALUES (" + record[0] + "," + record[1] +",\"" + record[2] + "\"," + record[3] + ",\"" + record[4] + "\"," + record[5] + ",\"" + record[6] + "\");"
#        mycursor.execute(line)


mydb.commit()
mycursor.close()
