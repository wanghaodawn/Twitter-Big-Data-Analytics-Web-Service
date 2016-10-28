#!/usr/bin/env python
import sys
import json
import urllib2
import dateutil.parser
import re
import string
import dateutil.tz

reload(sys)
sys.setdefaultencoding('utf-8')

## author = jialingliu


def main():
    dic_stopword = readStopWord()
    dic_bannedword = readBannedWord()

    readJSON(dic_stopword, dic_bannedword)


def readJSON(dic_stopword, dic_bannedword):
    # filename = "part-00000"
    # f = open(filename, 'r')
    # while 1:
    for line in sys.stdin:
        # line = str(f.readline())
        if not line:
            break
        try:
            data = json.loads(line)
        except:
            continue

        # Read fields

        user_id = data['user']['id_str']
        tweet_id = data['id_str']
        if not tweet_id:
            tweet_id = data['id']
        text = data['text']

        # Change time zone utc = dateutil.tz.gettz('UTC')
        tweet_time = data['created_at']
        time_data = dateutil.parser.parse(tweet_time).replace(tzinfo=dateutil.tz.gettz('UTC'))
        tweet_time = str(time_data)[0: 10]
        tweet_time = tweet_time.replace(":", "-")

        # Remove malformed data
        if (not tweet_id) or (not tweet_time) or (not text):
            continue

        # continue if lang field doesn't exist or not 'en'
        try:
            lang = data['lang']
        except KeyError:
            continue
        if lang != 'en':
            continue

        text = re.sub(pattern2, "", text)
        before_words = pattern1.findall(text)
        dic_after_words = getWords(before_words, dic_stopword, dic_bannedword)
        words = ""
        keys = dic_after_words.keys()
        keys.sort()
        for word in keys:
            words += word + ":" + str(dic_after_words[word]) + "\t"
        words = words.strip()
        if len(words) == 0:
            continue
        print (str(tweet_id) + "\t" + str(user_id) + "\t" + tweet_time + "\t" + words)


def getWords(before_words, dic_stopword, dic_bannedword):
    dic_after_words = {}
    for word in before_words:
        word = word.lower()
        if word not in dic_after_words.keys():
            if word not in dic_stopword and word not in dic_bannedword:
                dic_after_words[word] = 1
        else:
            dic_after_words[word] += 1
    return dic_after_words


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
    pattern2 = "(https?|ftp):\/\/[^\s/$.?#][^\s]*"
    # pattern2 = re.compile('([a-zA-Z0-9]+)')
    main()