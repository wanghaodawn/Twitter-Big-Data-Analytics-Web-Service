#!/usr/bin/env python
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
def main():
    readFile()

def readFile():
    filename = "q3useridV2"
    f = open(filename, 'r')
    while 1:
        line = str(f.readline()).strip()
        if not line:
            break
        userid = line.split("\t")[0]
        details = line.split("\t")[1]
        print add_zero(userid) + "\t" + details


def add_zero(userid):
    while len(userid) < 10:
        userid = "0" + userid
    return userid

if __name__ == "__main__":
    main()