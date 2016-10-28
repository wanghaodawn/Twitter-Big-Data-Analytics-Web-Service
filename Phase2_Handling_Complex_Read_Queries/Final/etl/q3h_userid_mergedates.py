#!/usr/bin/env python
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
def main():
    readFile()

def readFile():
    filename = "q3useridV1"
    f = open(filename, 'r')
    firstline_arr = f.readline().strip().split("\t")
    current_userid = firstline_arr[0]
    current_date_words = firstline_arr[1]

    while 1:
        line = f.readline().strip()
        if not line:
            result = current_userid + "\t" + current_date_words
            print result
            break

        userid = line.split("\t")[0]
        date_words = line.split("\t")[1]

        if current_userid != userid:
            result = current_userid + "\t" + current_date_words
            print result
            current_date_words = date_words
        else:
            current_date_words += ";" + date_words
        current_userid = userid

if __name__ == "__main__":
    main()