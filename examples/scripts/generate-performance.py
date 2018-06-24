multiply = 100
for i in range(0, 144, 2):
    count = i / 2 * multiply
    print "  - start: {}".format(i * 60)
    print "    scenarios:"
    print "      - type: CallPerformance"
    print "        count: {}".format(count + multiply)
