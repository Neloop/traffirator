times = range(0, 144, 3);
call_center_list = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 10, 20, 50, 50, 50, 50, 50, 50, 30, 20, 60, 70, 80, 80, 80, 70, 50, 30, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ];
classic_list = [ 500, 300, 200, 150, 150, 100, 100, 80, 100, 130, 200, 350, 600, 1000, 2000, 3000, 5000, 7000, 9000, 10000, 15000, 13000, 11500, 10500, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 9500, 9500, 9000, 8000, 7500, 12000, 11000, 10000, 8000, 5000, 4000, 3000, 2500, 1500, 1000, 800, 500 ];
malfunctioning_list = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 5, 7, 8, 9, 15, 13, 10, 10, 90, 90, 10, 10, 10, 10, 9, 8, 5, 5, 5, 5, 10, 10, 7, 5, 2, 2, 1, 1, 0, 0, 0, 0 ];
travelling_list = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 5, 10, 20, 30, 50, 50, 50, 50, 40, 50, 60, 60, 70, 60, 50, 50, 30, 20, 10, 5, 3, 2, 1, 1, 1, 0, 0, 0, 0, 0, 0 ];

for time, call, classic, malfunctioning, travelling in zip(times, call_center_list, classic_list, malfunctioning_list, travelling_list):
    print "  - start: {}".format(time * 60 * 1000)
    print "    scenarios:"
    print "      - type: CallCenterEmployee"
    print "        count: {}".format(call)
    print "      - type: ClassicUser"
    print "        count: {}".format(classic)
    print "      - type: MalfunctioningCellPhone"
    print "        count: {}".format(malfunctioning)
    print "      - type: TravellingManager"
    print "        count: {}".format(travelling)

