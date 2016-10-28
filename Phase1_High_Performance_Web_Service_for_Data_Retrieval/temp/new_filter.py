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
			# If last line
			if current_name:
				output = current_name + "\t" + temp
				print(output)
				current_name = ""
				temp = ""
			break

		array = line.split("\t")

		if len(array) != 2:
			continue

		name = array[0].strip().split("_")[0]
		hashtag = array[0].strip().split("_")[1]

		if current_name == name:
			# If continue
			temp += "!%^?!!!dawn!!!%^?" + hashtag + "!%^?!!!dawn!!!%^?" + array[1] + "!%^?dawn!%^?"
		else:
			# If changed topic
			if current_name:
				# If not first line
				output = current_name + "\t" + temp
				print(output)
				temp = "!%^?!!!dawn!!!%^?" + hashtag + "!%^?!!!dawn!!!%^?" + array[1] + "!%^?dawn!%^?"
				current_name = name
			else:
				# If first line
				current_name = name
				temp = "!%^?!!!dawn!!!%^?" + hashtag + "!%^?!!!dawn!!!%^?" + array[1] + "!%^?dawn!%^?"

		if count_gc % 10000 == 0:
			gc.collect()

		count_gc += 1


if __name__ == "__main__":
	main()


