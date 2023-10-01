from os import name
import sqlite3
import argparse
from typing import Counter

conn = sqlite3.connect('C:/Users/othma/Desktop/H4/imdb/imdb.db') # Provide the correct path to your SQLite database file
parser = argparse.ArgumentParser()
parser.add_argument('-g', '--genre', type=str)
parser.add_argument('-d', '--dbname', type=str)
cur = conn.cursor()
cur2 = conn.cursor()
args = parser.parse_args()
inputedgenre = args.genre
inputeddb = args.dbname
print(inputedgenre + ':')
counter = 0
for row in cur.execute(f"SELECT DISTINCT movie_id from genre WHERE genre = '{inputedgenre}'"):
    counter += 1
print(counter)
for row in cur.execute("SELECT DISTINCT year from movie ORDER BY year"):
    counter = 0
    year = row[0]
    for row2 in cur2.execute(f"SELECT DISTINCT year, genre FROM movie m JOIN genre g ON (m.mid = g.movie_id AND m.year = {year} AND g.genre = '{inputedgenre}') ORDER BY year"):
        counter += 1
    if counter > 0:
        print(f"{year};{counter}")
print("TOP 5 movies:")
counter = 0
year = 0
for row in cur.execute(f"SELECT DISTINCT movie_id from genre WHERE genre = '{inputedgenre}' ORDER BY movie_id"):
    Flag = True
    bufferString=";"
    if counter == 5:
        break
    attribute = row[0]
    
    for row2 in cur2.execute(f"SELECT DISTINCT movie_id,genre,year From genre g JOIN movie m ON(g.movie_id = m.mid)"):
        if row2[0] in bufferString:
            continue
        if attribute == row2[0]:
            if Flag :
                year = row2[2]
                bufferString = bufferString + f"{year}" + ";"
                Flag = False
            bufferString = bufferString + row2[1] + ","
    modified_attribute = attribute.split(" (")[0]
    print(f"    {modified_attribute}{bufferString}\n")
    counter+= 1
print("ACTORS\n")
counter = 0  
for row in cur.execute(f"""
    SELECT name, COUNT(DISTINCT movie_id) AS count FROM ( SELECT name, movie_id FROM actor WHERE movie_id IN (  SELECT movie_id FROM genre WHERE genre = '{inputedgenre}')
    UNION
    SELECT name, movie_id FROM actress WHERE movie_id IN ( SELECT movie_id FROM genre WHERE genre = '{inputedgenre}'))
    GROUP BY name ORDER BY name ASC """):
    if counter == 3:
        break
    bufferString=";"
    name = row[0]
    count= row[1]
    print(f"    {name}{bufferString} {count}\n")
    counter+= 1
    

   
     
