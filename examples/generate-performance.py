for i in range(0, 144, 2):
    count = i / 2 * 300
    print "  - start: {}".format(i * 60 * 1000)
    print "    scenarios:"
    print "      - type: CallPerformance"
    print "        count: {}".format(count + 300)
