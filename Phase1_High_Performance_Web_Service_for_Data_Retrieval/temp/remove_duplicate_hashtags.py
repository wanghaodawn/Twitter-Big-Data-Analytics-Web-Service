import sys
from sys import argv
import os
import string
import gc

reload(sys)
sys.setdefaultencoding('utf-8')

def main():

	filename = "final"
	f = open(filename, 'r')

	count_gc = 0
	current_name = ""
	temp = ""

	while 1:
		line = str(f.readline()).strip()
		if not line:
			break

		array = line.split("\t")
		if len(array) != 2:
			continue

		a = array[1].split("!%^?dawn!%^?")

		dic = {}
		i = 0
		while i < len(a):
			if not dic.has_key(a[i]):
				dic[a[i]] = a[i+1]
			i += 2 

		temp = ""
		i = 0
		for s in dic:
			if i == 0:
				temp += s
			else:
				temp += "!%^?dawn!%^?" + s
			i += 1

		print(array[0] + "\t" + temp)

		if count_gc % 10000 == 0:
			gc.collect()

		count_gc += 1


if __name__ == "__main__":
	main()


