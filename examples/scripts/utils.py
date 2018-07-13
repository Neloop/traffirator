def distribute(a, n):
    k, m = divmod(a, n)
    result = []
    for i in range(n):
        result.append(k + (1 if m > i else 0))
    return result

def print_real_life_based(times, call_center_list, classic_list, malfunctioning_list, travelling_list):
    previous_time = 0
    previous_call = 0
    previous_classic = 0
    previous_malfunctioning = 0
    previous_travelling = 0
    for time, call, classic, malfunctioning, travelling in zip(times, call_center_list, classic_list, malfunctioning_list, travelling_list):
        seconds = (time - previous_time) * 60
        seconds_s = range(0, seconds)
        calls_s = distribute(call - previous_call, seconds)
        classics_s = distribute(classic - previous_classic, seconds)
        malfs_s = distribute(malfunctioning - previous_malfunctioning, seconds)
        travels_s = distribute(travelling - previous_travelling, seconds)

        for second, call_s, classic_s, malf_s, travel_s in zip(seconds_s, calls_s, classics_s, malfs_s, travels_s):
            previous_call += call_s
            previous_classic += classic_s
            previous_malfunctioning += malf_s
            previous_travelling += travel_s

            print("    - start: {}".format(previous_time * 60 + second))
            print("      scenarios:")
            print("        - type: CallCenterEmployee")
            print("          count: {}".format(previous_call))
            print("        - type: ClassicUser")
            print("          count: {}".format(previous_classic))
            print("        - type: MalfunctioningCellPhone")
            print("          count: {}".format(previous_malfunctioning))
            print("        - type: TravellingManager")
            print("          count: {}".format(previous_travelling))

        previous_time = time
