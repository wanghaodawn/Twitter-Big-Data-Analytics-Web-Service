import sys
from sys import argv
import os
import string
import gc

reload(sys)
sys.setdefaultencoding('utf-8')

def map_density(s):
	return s['density']

def map_time(s):
	return s['time']

def map_tweet_id(s):
	return s['tweet_id']

def main():

	filename = "tweet-sorted"
	f = open(filename, 'r')

	count_gc = 0
	current_name = ""
	temp = []

	while 1:
		line = str(f.readline()).strip()
		if not line:
			# If last line
			if current_name:
				temp = sorted(temp, key = (map_tweet_id), reverse = False)
				temp = sorted(temp, key = (map_time), reverse = False)
				temp = sorted(temp, key = (map_density), reverse = True)

				output = current_name + "\t"
				if len(temp) == 1:
					output += temp[0]['density']+ ":" + temp[0]['time'] + ":" + temp[0]['tweet_id'] + ":" + temp[0]['text']
				else:
					for i in range(0,len(temp)):
						output += temp[i]['density']+ ":" + temp[i]['time'] + ":" + temp[i]['tweet_id'] + ":" + temp[i]['text']
						if i != len(temp)-1:
							output += "!%^?dawn!%^?"
				print(output)
				temp = []
			break

		array = line.split("\t")

		if len(array) != 5:
			continue

		name = array[0]

		if current_name == name:
			# If continue
			temp += [{'name': array[0], 'density': array[1], 'time': array[2], 'tweet_id': array[3], 'text': array[4]}]
		else:
			# If changed topic
			if current_name:
				# If not first line
				temp = sorted(temp, key = (map_tweet_id), reverse = False)
				temp = sorted(temp, key = (map_time), reverse = False)
				temp = sorted(temp, key = (map_density), reverse = True)

				output = current_name + "\t"
				if len(temp) == 1:
					output += temp[0]['density']+ ":" + temp[0]['time'] + ":" + temp[0]['tweet_id'] + ":" + temp[0]['text']
				else:
					for i in range(0,len(temp)):
						output += temp[i]['density']+ ":" + temp[i]['time'] + ":" + temp[i]['tweet_id'] + ":" + temp[i]['text']
						if i != len(temp)-1:
							output += "!%^?dawn!%^?"
				print(output)
				temp = [{'name': array[0], 'density': array[1], 'time': array[2], 'tweet_id': array[3], 'text': array[4]}]
				current_name = name

			else:
				# If first line
				current_name = name
				temp = [{'name': array[0], 'density': array[1], 'time': array[2], 'tweet_id': array[3], 'text': array[4]}]

		if count_gc % 10000 == 0:
			gc.collect()

		count_gc += 1


if __name__ == "__main__":
	main()


















# import sys
# from sys import argv
# import os
# import string
# import gc

# reload(sys)
# sys.setdefaultencoding('utf-8')

# def map_density(s):
# 	return s['density']

# def map_time(s):
# 	return s['time']

# def map_tweet_id(s):
# 	return s['tweet_id']

# def main():

# 	filename = "tweet-sorted"
# 	f = open(filename, 'r')

# 	count_gc = 0
# 	current_name = ""
# 	temp = []

# 	while 1:
# 		line = str(f.readline()).strip()
# 		if not line:
# 			# If last line
# 			if current_name:

# 				if len(temp) == 1:
# 					output = current_name + temp[0]['density']+ ":" + temp[0]['time'] + ":" + temp[0]['tweet_id'] + ":" + temp[0]['text'] + "!?!%^dawn^%!?!"
# 					print(output)
# 					temp = []
# 					current_name = ""
# 					break

# 				temp = sorted(temp, key = (map_tweet_id), reverse = False)
# 				temp = sorted(temp, key = (map_time), reverse = False)
# 				temp = sorted(temp, key = (map_density), reverse = True)

# 				output = ""
# 				output += current_name + "\t"

# 				for j in range(0, len(temp)):
# 					output += temp[j]['density']+ ":" + temp[j]['time'] + ":" + temp[j]['tweet_id'] + ":" + temp[j]['text'] + "!?!%^dawn^%!?!"
				
# 				print(output)
# 				temp = []
# 				current_name = ""
# 			break

# 		array = line.split("\t")

# 		if len(array) != 5:
# 			continue

# 		name = array[0]

# 		if current_name == name:
# 			# If continue
# 			temp += [{'density': array[1], 'time': array[2], 'tweet_id': array[3], 'text': array[4]}]
# 		else:
# 			# If changed topic
# 			if current_name:

# 				if len(temp) == 1:
# 					output = current_name + temp[0]['density']+ ":" + temp[0]['time'] + ":" + temp[0]['tweet_id'] + ":" + temp[0]['text'] + "!?!%^dawn^%!?!"
# 					print(output)
# 					temp = []
# 					current_name = name
# 					continue

# 				# If not first line
# 				temp = sorted(temp, key = (map_tweet_id), reverse = False)
# 				temp = sorted(temp, key = (map_time), reverse = False)
# 				temp = sorted(temp, key = (map_density), reverse = True)

# 				output = ""
# 				output += current_name + "\t"

# 				for j in range(0, len(temp)):
# 					output += temp[j]['density']+ ":" + temp[j]['time'] + ":" + temp[j]['tweet_id'] + ":" + temp[j]['text'] + "!?!%^dawn^%!?!"
				
# 				print(output)
# 				temp = []
# 				current_name = name

# 			else:
# 				# If first line
# 				current_name = name
# 				temp += [{'density': array[1], 'time': array[2], 'tweet_id': array[3], 'text': array[4]}]

# 		if count_gc % 10000 == 0:
# 			gc.collect()

# 		count_gc += 1


# if __name__ == "__main__":
# 	main()




