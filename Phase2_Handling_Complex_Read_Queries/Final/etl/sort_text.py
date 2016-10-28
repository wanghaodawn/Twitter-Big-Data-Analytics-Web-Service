import sys
import gc

reload(sys)
sys.setdefaultencoding('utf-8')


def main():
    filename = "final"
    f = open(filename, 'r')
    count_gc = 0

    while True:
        line = str(f.readline().strip())
        #print line
        if not line:
            break
        array = line.split("\t")
        if len(array) != 2:
            continue

        text = array[1].split("!%^?dawn!%^?")

        temp = []
        for tweet in text:
            if tweet not in temp:
                temp.append(tweet)

        temp = "!%^?dawn!%^?".join(temp)

        print (array[0] + "\t" + temp)

        if count_gc % 10000 == 0:
            gc.collect()
        count_gc += 1


if __name__ == "__main__":
    main()

