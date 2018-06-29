times = range(0, 144, 3);
call_center_list = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 100, 200, 500, 500, 500, 500, 500, 500, 300, 200, 600, 700, 800, 800, 800, 700, 500, 300, 100, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ];
classic_list = [ 5000, 3000, 2000, 1500, 1500, 1000, 1000, 800, 1000, 1300, 2000, 3500, 6000, 10000, 20000, 30000, 50000, 70000, 90000, 100000, 150000, 130000, 115000, 105000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 95000, 95000, 90000, 80000, 75000, 120000, 110000, 100000, 80000, 50000, 40000, 30000, 25000, 15000, 10000, 8000, 5000 ];
malfunctioning_list = [ 3, 3, 3, 2, 2, 1, 1, 1, 1, 1, 3, 5, 7, 10, 15, 20, 30, 40, 50, 60, 70, 80, 80, 80, 80, 80, 80, 80, 80, 80, 70, 60, 50, 50, 50, 50, 30, 30, 20, 20, 20, 20, 10, 10, 5, 5, 5, 3 ];
travelling_list = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 5, 10, 30, 50, 100, 200, 300, 500, 500, 500, 500, 400, 500, 600, 600, 700, 600, 500, 500, 300, 200, 100, 50, 30, 20, 10, 10, 10, 0, 0, 0, 0, 0, 0 ];

for time, call, classic, malfunctioning, travelling in zip(times, call_center_list, classic_list, malfunctioning_list, travelling_list):
    print "    - start: {}".format(time * 60)
    print "      scenarios:"
    print "        - type: CallCenterEmployee"
    print "          count: {}".format(call)
    print "        - type: ClassicUser"
    print "          count: {}".format(classic)
    print "        - type: MalfunctioningCellPhone"
    print "          count: {}".format(malfunctioning)
    print "        - type: TravellingManager"
    print "          count: {}".format(travelling)
