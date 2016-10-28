# coding=utf-8
import sys
import gc

## author = jialingliu

reload(sys)
sys.setdefaultencoding('utf-8')

def main():
    filename = "newfinalV1"
    f = open(filename, 'r')
    count_gc = 0
    while True:
        line = str(f.readline().strip())
        if not line:
            break
        key = line.split("\t")[0]
        text = line.split("\t")[1]
        userid = key.split("_")[0]
        hashtag = key[len(userid)+1:]
        userid = int(userid)
        flag = userid % 5
        if flag == 0:
            print (str(userid) + "_" + hashtag + "\t" + text)
        elif flag == 1:
            print ("aa" + str(userid) + "_" + hashtag + "\t" + text)
        elif flag == 2:
            print ("zz" + str(userid) + "_" + hashtag + "\t" + text)
        elif flag == 3:
            print ("cc" + str(userid) + "_" + hashtag + "\t" + text)
        else:
            print ("xx" + str(userid) + "_" + hashtag + "\t" + text)


    if count_gc % 10000 == 0:
        gc.collect()
    count_gc += 1

if __name__ == "__main__":
    main()


