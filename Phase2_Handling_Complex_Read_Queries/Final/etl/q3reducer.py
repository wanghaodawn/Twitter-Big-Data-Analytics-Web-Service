#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys

## author = jialingliu

reload(sys)
sys.setdefaultencoding('utf-8')
current_id = None
for line in sys.stdin:
    line = line.strip()
    id = line.split("\t")[0]
    if current_id is None or current_id != id:
        print (line)
    current_id = id
