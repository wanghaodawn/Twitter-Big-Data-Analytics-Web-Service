#!/usr/bin/env python

import sys
from sys import argv
import os
import string
import gc

reload(sys)
sys.setdefaultencoding('utf-8')

def main():
	filename = "tweet-sort"
	f = open(filename, 'r')

	colon = ":"
	enter = "\n"
	space = "_"

	i = 0

	while 1:
		line = str(f.readline())
		if not line:
			break

		array = line.split("\t")

		if len(array) < 6:
			continue

		for j in range(5, len(array)):
			
			name = array[3].strip() + space + array[j].strip()
			fw = open(name, 'a')

			text = removeSimbols(array[4].strip())

			fw.write(array[0].strip() + colon + array[1].strip() + colon + array[2].strip() + colon + text + enter)

			fw.close()

		if i % 10000 == 0:
			gc.collect()

		i += 1


def removeSimbols(text):
	return text.\
			replace('\\"', '\"').\
            replace('\\v', '\v').\
            replace('\\t', '\t').\
            replace('\\r', '\r').\
            replace('\\n', '\n').\
            replace('\\f', '\f').\
            replace('\\b', '\b').\
            replace('\\a', '\a')

if __name__ == "__main__":
	main()