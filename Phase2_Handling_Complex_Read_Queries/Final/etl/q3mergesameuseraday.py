#!/usr/bin/env python
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

## author = jialingliu

def main():
    readFile()

def readFile():
    filename = "q3outputV1"
    f = open(filename, 'r')
    firstline_arr = str(f.readline()).strip().split("\t")

    current_user = int(firstline_arr[1])
    current_date = int(firstline_arr[2])
    current_words_map = encodeMap(firstline_arr[3])

    while 1:
        line = str(f.readline()).strip()
        if not line:
            result = str(current_user) + "\t" + str(current_date) + "\t" + decodeMap(current_words_map)
            print (result)
            break
        user = int(line.split("\t")[1])
        date = int(line.split("\t")[2])

        if current_user != user or current_date != date:
            result = str(current_user) + "\t" + str(current_date) + "\t" + decodeMap(current_words_map)
            print (result)
            current_words_map = encodeMap(line.split("\t")[3])
        else:
            current_words_map = updateMap(line.split("\t")[3], current_words_map)
        current_user = user
        current_date = date

## encode a words_str with their counts into a map
def encodeMap(words):
    words_map = {}
    words_arr = words.split(",")
    for i in range(len(words_arr)):
        word = str(words_arr[i].split(":")[0])
        count = int(words_arr[i].split(":")[1])
        words_map[word] = count
    return words_map

## decode a words_map into a words_str alphabetically sorted
def decodeMap(words_map):
    words = ""
    keys = words_map.keys()
    keys.sort()
    for word in keys:
        words += word + ":" + str(words_map[word]) + ","
    words = words[:-1]
    return words

def updateMap(words, current_words_map):
    words_arr = words.split(",")
    for i in range(len(words_arr)):
        word = str(words_arr[i].split(":")[0])
        count = int(words_arr[i].split(":")[1])
        if word in current_words_map.keys():
            current_words_map[word] += count
        else:
            current_words_map[word] = count
    return current_words_map

if __name__ == "__main__":
    main()