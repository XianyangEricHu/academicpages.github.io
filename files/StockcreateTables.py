import mysql.connector


mydb = mysql.connector.connect(
host="localhost",
user="root",
password="Huxy@2020fall",
database="Stock1"
)

mycursor = mydb.cursor()


#############################################
createCompany = '''CREATE TABLE IF NOT EXISTS  Company (
  company_id INT PRIMARY KEY,
  company_name VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  email_address VARCHAR(255) NOT NULL
);
'''
mycursor.execute(createCompany)

############################################
createStock = '''CREATE TABLE IF NOT EXISTS  Stock (
  stock_id INT PRIMARY KEY,
  stock_symbol LONGTEXT NOT NULL,
  date_issued DATE NOT NULL,
  initial_price DECIMAL(10,2) NOT NULL,
  company_id INT NOT NULL,
  FOREIGN KEY (company_id) REFERENCES Company(company_id)
);
'''
mycursor.execute(createStock)

##################################################
createTrader = '''CREATE TABLE IF NOT EXISTS  Trader (
  trader_id INT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email_address VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL
);
'''
mycursor.execute(createTrader)

##################################################
createBroker = '''CREATE TABLE IF NOT EXISTS  Broker (
  broker_id INT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email_address VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL
);
'''
mycursor.execute(createBroker)

##################################################
createTransaction = '''CREATE TABLE IF NOT EXISTS  Transaction (
  tsac_id INT PRIMARY KEY,
  tsac_type VARCHAR(10) NOT NULL,
  tsac_date DATE NOT NULL,
  tsac_price DECIMAL(10,2) NOT NULL,
  tsac_volume INT NOT NULL,
  stock_id INT NOT NULL,
  trader_id INT NOT NULL,
  broker_id INT NOT NULL,
  FOREIGN KEY (stock_id) REFERENCES Stock(stock_id),
  FOREIGN KEY (trader_id) REFERENCES Trader(trader_id),
  FOREIGN KEY (broker_id) REFERENCES Broker(broker_id)
);
'''
mycursor.execute(createTransaction)

##################################################
createMarket_Data = '''CREATE TABLE IF NOT EXISTS  Market_Data (
  market_data_id INT PRIMARY KEY,
  close_price DECIMAL(10,2) NOT NULL,
  high_price DECIMAL(10,2) NOT NULL,
  low_price DECIMAL(10,2) NOT NULL,
  trading_volume INT NOT NULL,
  last_updated_date DATE NOT NULL,
  stock_id INT NOT NULL,
  FOREIGN KEY (stock_id) REFERENCES Stock(stock_id)
);
'''
mycursor.execute(createMarket_Data)
