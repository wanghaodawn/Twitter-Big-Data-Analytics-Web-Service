#!/usr/bin/env python
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
def main():
    readFile()

def readFile():
    filename = "q3outputV2"
    f = open(filename, 'r')
    while 1:
        line = str(f.readline()).strip()
        if not line:
            break
        userid = line.split("\t")[0]
        date = line.split("\t")[1]
        words = line.split("\t")[2]
        for word_count in words.split(","):
            word = word_count.split(":")[0]
            count = word_count.split(":")[1]
            print word + "_" + date + "\t" + userid + ":" + count


if __name__ == "__main__":
    main()