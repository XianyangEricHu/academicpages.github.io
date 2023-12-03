import mysql.connector

mydb = mysql.connector.connect(
  host="localhost",
  user="root",
  password="Huxy@2020fall"
)

# preparing a cursor object
cursorObject = mydb.cursor()


# create database
cursorObject.execute("CREATE DATABASE IF NOT EXISTS Stock1")