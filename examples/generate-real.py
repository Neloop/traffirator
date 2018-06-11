for i in range(0, 144, 6):
    print "  - start: {}".format(i * 60 * 1000)
    print "    scenarios:"
    print "      - type: CallCenterEmployee"
    print "        count: 1"
    print "      - type: ClassicUser"
    print "        count: 1"
    print "      - type: MalfunctioningCellPhone"
    print "        count: 1"
    print "      - type: TravellingManager"
    print "        count: 1"

