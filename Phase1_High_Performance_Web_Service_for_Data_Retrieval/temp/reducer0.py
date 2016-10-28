#!/usr/bin/env python

###########################################
# 
# Implement the Reducer
# AndrewID: haow2
# Hao Wang
#
###########################################

import sys
from sys import argv
from operator import itemgetter

# Get the filename
# script, filename = argv
# f = open(filename, 'r')

curr_name = ""
curr_day  = 1
count_mon = 0
count_day = {1:0,  2:0,  3:0,  4:0,  5:0,  6:0,  7:0,  8:0,  9:0, 10:0,\
			11:0, 12:0, 13:0, 14:0, 15:0, 16:0, 17:0, 18:0, 19:0, 20:0,\
			21:0, 22:0, 23:0, 24:0, 25:0, 26:0, 27:0, 28:0, 29:0, 30:0, 31:0}

# Traverse the file
# while 1:
for line in sys.stdin:
	# line = str(f.readline())
	# If line is null

	line = line.strip()
	if not line:
		break

	line_array = line.split( )

	# Get project name
	name = line_array[0]
	# Init this date
	day  = 0
	# Init access number
	num = 0

	# Parse access number
	# Throws exception once it is not number
	try:
		num = int(line_array[1])
	except:
		# Not a number
		continue

	# Parse current day
	# Throws exception once it is not number
	try:
		day = int(line_array[2][6:8])
	except:
		# Not a number
		continue

	if curr_name == name:
		# Needn't to print this time
		count_mon = count_mon + num
		curr_day = day
		count_day[day] = count_day[day] + num

	else:
		# Goes into a new item, need to print the former one
		if curr_name:

			# The file is not null and the number is larger than 100000, print it out
			if count_mon > 100000:
				res = str(count_mon) + "\t" + curr_name + "\t"
				for i in range(1,32):
					if i < 10:
						res += "2015120" + str(i) + ":" + str(count_day[i])
					else:
						res += "201512"  + str(i) + ":" + str(count_day[i])

					if i != 31:
						res += "\t"
				print(res)

			# Assign new value for the new line
			for i in range(1,32):
				count_day[i] = 0

			count_mon = num
			curr_name = name
			count_day[day] = num
			curr_day = day

		else:
			# Reset necessary value
			curr_name = name
			curr_day = day
			count_mon = count_mon + num
			# print("count_mon: " + str(count_mon))

			count_day[day] = num
