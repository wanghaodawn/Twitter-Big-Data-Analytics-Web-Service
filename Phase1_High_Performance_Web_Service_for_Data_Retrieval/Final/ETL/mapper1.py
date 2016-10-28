#!/usr/bin/env python

###########################################
# 
# Implement the Mapper
# AndrewID: haow2
# Hao Wang
#
###########################################

import sys
from sys import argv
import os
import json
import urllib2
import dateutil.parser
import re
import string
import dateutil.tz

reload(sys)
sys.setdefaultencoding('utf-8')


def main():
	# Read all files
	dic_afinn = readAfinn()
	# print(dic_afinn)
	dic_stopword = readStopWord()
	# print(dic_stopword)
	dic_bannedword = readBannedWord()
	# print(dic_bannedword)

	readJSON(dic_afinn, dic_stopword, dic_bannedword)
	

def readJSON(dic_afinn, dic_stopword, dic_bannedword):
	filename = "part-00000"
	f = open(filename, 'r')

	while 1:
		line = str(f.readline())
		# line = line.strip()
		# If line is null
		if not line:
			break

		data = json.loads(line)

		# Continue if hashtags are null
		if not data['entities']:
			continue
		hashtags = ""
		ii = 0
		dic_tags = {}
		for tag in data['entities']['hashtags']:
			if ii != 0:
				hashtags += "\t"
			temp = tag['text']
			if not dic_tags.has_key(temp):
				hashtags += temp
			ii += 1

		if not hashtags:
			continue

		# Read fields
		user_id = data['user']['id_str']
		tweet_id = data['id_str']
		if not tweet_id:
			tweet_id = data['id']
		text = data['text']

		# Change time zone
		# utc = dateutil.tz.gettz('UTC')
		tweet_time = data['created_at']
		time_data = dateutil.parser.parse(tweet_time).replace(tzinfo=dateutil.tz.gettz('UTC'))
		tweet_time = str(time_data)[0: 19]
		tweet_time = tweet_time.replace(":", "-")

		# Remove malformed data
		if (not tweet_id) or (not tweet_time) or (not text):
			continue

		# print(tweet_id)
		# print(hashtags)

		before_words = pattern1.findall(text)

		sentiment_density = sentimentDensity(before_words, dic_afinn, dic_stopword)
		text = textCensoring(text, dic_bannedword)

		text = removeSimbols(text)

		print(str(tweet_id) + "\t" + str(user_id) + "\t" + tweet_time + "\t" + "%.3f"% sentiment_density + "\t" + text + "\t" + hashtags)


def removeSimbols(text):
	return text.\
			replace('\a', '\\a').\
            replace('\b', '\\b').\
            replace('\f', '\\f').\
            replace('\n', '\\n').\
            replace('\r', '\\r').\
            replace('\t', '\\t').\
            replace('\v', '\\v').\
            replace('\"', '\\"')

def textCensoring(text, dic_bannedword):
#    assert isinstance(text, str)
    words = pattern1.findall(text)
    # print words
    punc = pattern1.split(text)
    # print punc
    after_words = []
    Pindex = 0
    Windex = 0
    for i in range(len(words) + len(punc)):
        if i%2 == 0:
            after_words.append(punc[Pindex])
            Pindex = Pindex + 1
        else:
            if words[Windex].lower() in dic_bannedword:
                after_words.append(newWord(words[Windex]))
            else:
                after_words.append(words[Windex])
            Windex = Windex + 1
    # print after_words
    return "".join(after_words)

def newWord(word):
    newWord = word[0]
    for i in range(1, len(word)-1):
        newWord += "*"
    newWord += word[-1]
#    assert isinstance(newWord, str)
    return newWord


def sentimentDensity(words, dic_afinn, dic_stopword):
	
	# print(text)
	# print(words)
	count_effective = 0
	sentiment_score = 0
	for word in words:
		# Count effective word
		if not dic_stopword.has_key(word.lower()):
			count_effective += 1

		# Compute Sentiment Score
		if dic_afinn.has_key(word.lower()):
			sentiment_score += dic_afinn.get(word.lower())

	sentiment_density = 0.0
	if count_effective != 0:
		sentiment_density = 1.0 * sentiment_score / count_effective

	sentiment_density = round(sentiment_density, 3)

	# print("%.3f"% sentiment_density)
	return sentiment_density


def readBannedWord():
	bannedword_file = urllib2.urlopen("https://cmucc-datasets.s3.amazonaws.com/15619/f15/banned.txt").read()
	words = bannedword_file.split("\n")

	rot13 = string.maketrans( 
    "ABCDEFGHIJKLMabcdefghijklmNOPQRSTUVWXYZnopqrstuvwxyz", 
    "NOPQRSTUVWXYZnopqrstuvwxyzABCDEFGHIJKLMabcdefghijklm")
	dic = {}

	for word in words:
		word = word.strip()
		if not word:
			continue

		# If not missing
		word = string.translate(word, rot13)
		dic[word] = 1

	return dic


def readAfinn():
	afinn_file = urllib2.urlopen("https://cmucc-datasets.s3.amazonaws.com/15619/f15/afinn.txt").read()
	lines = afinn_file.split("\n")
	dic = {}
	for line in lines:
		array = line.strip().split("\t")
		
		# If missing fields
		if len(array) != 2:
			continue

		# If not missing
		dic[array[0]] = int(array[1])
	return dic


def readStopWord():
	stopword_file = urllib2.urlopen("https://s3.amazonaws.com/cmucc-datasetss16/common-english-word.txt").read()
	words = stopword_file.split(",")
	dic = {}
	for word in words:

		word = word.strip()
		if not word:
			continue

		# If not missing
		dic[word] = 1

	return dic


if __name__ == "__main__":
	pattern1 = re.compile(r'[a-zA-Z0-9]+')
	# pattern2 = re.compile('([a-zA-Z0-9]+)')
	main()