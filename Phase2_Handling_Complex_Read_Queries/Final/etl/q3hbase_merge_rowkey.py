#!/usr/bin/env python
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

## author = jialingliu
def main():
    readFile()

def readFile():
    filename = "q3hbaseV1"
    f = open(filename, 'r')
    firstline_arr = f.readline().strip().split("\t")

    current_word_date = firstline_arr[0]
    current_userid_count = firstline_arr[1]

    while 1:
        line = f.readline().strip()
        if not line:
            result = current_word_date + "\t" + sortUserid(current_userid_count)
            print result
            break

        word_date = line.split("\t")[0]
        userid_count = line.split("\t")[1]

        if current_word_date != word_date:
            result = current_word_date + "\t" + sortUserid(current_userid_count)
            print result
            current_userid_count = userid_count
        else:
            current_userid_count += "," + userid_count
        current_word_date = word_date

def sortUserid(userids_count):
    map = {}
    for userid_count in userids_count.split(","):
        userid = int(userid_count.split(":")[0])
        count = userid_count.split(":")[1]
        map[userid] = count
    keys = map.keys()
    keys.sort()
    result = ""
    for key in keys:
        result += str(key) + ":" + map[key] + ","
    return result[:-1]


if __name__ == "__main__":
    main()