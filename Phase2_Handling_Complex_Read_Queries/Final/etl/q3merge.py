#!/usr/bin/env python
import sys
import glob
reload(sys)
sys.setdefaultencoding('utf-8')
## author = jialingliu

# the path for all the files
path='/Users/jialingliu/desktop/15-619/teamproject/phase2/q3output1/*'
files = glob.glob(path)
for name in files:
    with open(name, 'r') as f:
        for line in f:
            arr = line.split("\t")
            date = arr[2]
            date_arr = date.split("-")
            date_str = "".join(date_arr)
            words = []
            for i in range(3, len(arr)):
                words.append(arr[i])
            words_str = ",".join(words).strip()
            print (arr[0] + "\t" + arr[1] + "\t" + date_str + "\t" + words_str)