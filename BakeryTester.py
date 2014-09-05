#!/usr/bin/python

"""
BakeryTester - ver 0.2
(c) Vincent Au (u5388374)

The program can be used to do some preliminary testing of your Bakery.
Do not fully trust this program to determine if your code is fully working
or not. I take no responsibility if your code passes the tests yet actually
contains errors.

This program tests that the customers execute their commands in the correct
order and also that the tickets are taken and called in the correct order.

At the current iteration, it cannot take input from terminal but only
takes input as a file name of the log.
Usage:
java -ea Bakery -n [params if desired] > log.txt
./BakeryTester log.txt
"""

import sys
import subprocess

trace = [] #global last trace, not sure if needed

def print_trace():
    global trace
    print("." * 30)
    print("error trace")
    print("-----------")
    for line in trace:
        print(line)
    print("." * 30)

def get_numbers(line):
    nc = 0
    ns = 0
    nb = 0
    for i in line.split():
        if i.find("NC=") >= 0:
            nc = int(i[3:])
        elif i.find("NS=") >= 0:
            ns = int(i[3:])
        elif i.find("NB=") >= 0:
            nb = int(i[3:])
    return (nc, ns, nb)

def check_file(name):
    print("TESTING %s" % name)
    print("=" * (8 + len(name)))
    (nc, ns, nb) = get_numbers(next(open(name, "r")))
    something_failed = False

    print("checking customers")
    print("------------------")
    for i in range(0, nc):
        if check_customer(name, i):
            print("customer %d passed!" % i)
        else:
            print("customer %d failed..." % i)
            print_trace()
            something_failed = True

    print("\nchecking ticket take order")
    print("----------------------------")
    if check(name, "takes", nc):
        print("ticket takes passed!")
    else:
        print("ticket takes failed...")
        print_trace()
        something_failed = True
    
    print("\nchecking ticket call order")
    print("----------------------------")
    if check(name, "calls", nc):
        print("ticket calls passed!")
    else:
        print("ticket calls failed...")
        print_trace()
        something_failed = True
    
    if not something_failed:
        print("\nResult: %s PASSED!\n" % name)
    else:
        print("\nSomething failed in %s\n" % name)
    return not something_failed

def find_grep(filename, search):
    cmd = "cat %s | grep %s" % (filename, search)
    p = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
    (output, err) = p.communicate()
    return output.split("\n")

def check_customer(filename, customer_no):
    expects = "takes"
    global trace
    trace = []
    for line in find_grep(filename, "\"cust.\s\+%d\"" % customer_no):
        trace.append(line)
        if len(line):
            if not line.find(expects) >= 0:
                trace.append("error: <%s> expected" % expects)
                return False
            if expects == "takes":
                expects = "pays"
            elif expects == "pays":
                expects = "receives"
            elif expects == "receives":
                expects = "takes"
    return True

def check(filename, search, buns):
    value = 0
    global trace
    trace = []
    for line in find_grep(filename, search):
        trace.append(line)
        if len(line):
            if int(line[-1:]) != value % buns:
                trace.append("error: <ticket %d> expected" \
                        % (value % buns))
                return False
            value += 1
    return True

def main(argv):
    total = 0
    successes = 0
    if len(argv) <= 1:
        msg = "This program takes the logs filename as an argument and" \
                + " cannot be piped to. Sorry!"
        print(msg)
        return
    for a in argv[1:]:
        if check_file(a):
            successes += 1
        total += 1
        print("**************************************\n")
    print("RESULT: %d/%d (%.0f%%)" % \
            (successes, total, 100.0 * successes/total))
    if successes == total:
        print("CONGRATZ! EVERYTHING PASSED! YOU ARE AWESOME!\n")

main(sys.argv)
