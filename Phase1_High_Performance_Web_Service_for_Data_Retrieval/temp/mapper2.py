import sys
from sys import argv
import os
import string
import gc

reload(sys)
sys.setdefaultencoding('utf-8')

def main():
	filename = "tweet"
	f = open(filename, 'r')

	i = 0

	while 1:
		line = str(f.readline())
		if not line:
			break

		array = line.split("\t")

		if len(array) < 6:
			continue

		for j in range(5, len(array)):
			name = array[1].strip() + "_" + array[j].strip()
			print(name + "\t" + array[3].strip() + "\t" + array[2].strip() + "\t" + array[0].strip() + "\t" + array[4])

		if i % 10000 == 0:
			gc.collect()

		i += 1


if __name__ == "__main__":
	main()




