# Authors:
#   -   Julián Wallis Medina
#   -   Jonathan Salisbury Vega

import time

# Constants
NUM_FLOORS = 11
DELTA = 0.5

# Our elevator can sense the following information:
# S1. The current floor
current_floor = 0

# S2. To which floors do the people want to go
# out_requests = [False] * NUM_FLOORS
out_requests = [False] * NUM_FLOORS
# S3. In which floors do people want to get in
# in_requests = [False, True, False, False, True, False,
#                False, False, False, False, False, False, False, False]
in_requests = [9, -1, -1, 7, -1, 2, -1, -1, -1, -1, 6]
direction_requests = ["up", "no", "no", "up", "no", "down", "no",
                      "no", "no", "no", "down"]

# S4. The state of the doors (open/closed)
state_doors = "closed"

# Our elevator can perform the following actions:
# A1. Go to the floor above, unless it is the top floor
# A2. Go to the floor below, unless it is the bottom floor
# A3. Open the door
# A4. Close the door
# A5. Wait for DELTA seconds, to simulate the time it takes for the passengers to enter/exit the elevator

direction = "up"  # Current direction

# returns the next higher floor for which there is an exit/entry request


def sig(floor, in_out):
    if (in_out == "in"):
        for i in range(floor, NUM_FLOORS):
            if (in_requests[i] != -1):
                return i
    else:
        for i in range(floor, NUM_FLOORS):
            if (out_requests[i]):
                return i
    return -1


# returns the next lower floor for which there is an exit/entry request
def prec(floor, in_out):
    if (in_out == "in"):
        for i in range(floor, -1, -1):
            if (in_requests[i] != -1):
                return i
    else:
        for i in range(floor, -1, -1):
            if (out_requests[i]):
                return i
    return -1


# Function that implements A1
def go_up():
    global current_floor, direction
    current_floor += 1
    direction = "up"
    print("Going up to floor", str(current_floor) + "\n")
    return current_floor


# Function that implements A2
def go_down():
    global current_floor, direction
    current_floor -= 1
    direction = "down"
    print("Going down to floor", str(current_floor) + "\n")
    return current_floor


# Function that implements A3
def open_doors():
    global state_doors
    state_doors = "open"
    print("Doors are open \n")
    return state_doors


# Function that implements A4
def close_doors():
    global state_doors
    state_doors = "closed"
    print("Doors are closed \n")
    return state_doors


# Function that implements A5
def wait():
    print("Waiting for", DELTA, "seconds \n")
    time.sleep(DELTA)
    return DELTA


# Function that implements the elevator's behaviour
def elevator():
    global current_floor, direction, out_requests, in_requests, state_doors
    while True:
        # print("Current floor:", current_floor)
        # print("in_requests:", in_requests)
        # print("out_requests:", out_requests)
        # print("direction_requests:", direction_requests)
        if (state_doors == "closed" and direction == "up" and (direction_requests[current_floor] == "up" or out_requests[current_floor] or (current_floor == NUM_FLOORS - 1 and in_requests[current_floor] != -1))):
            print("1")
            open_doors()  # 1 , 2
            wait()  # 3
            close_doors()  # 3
            out_requests[current_floor] = False
            if in_requests[current_floor] != -1:
                out_requests[in_requests[current_floor]] = True
            in_requests[current_floor] = -1
            direction_requests[current_floor] = "no"
        elif (state_doors == "closed" and direction == "down" and (direction_requests[current_floor] == "down" or out_requests[current_floor] or (current_floor == 0 and in_requests[current_floor] != -1))):
            print("2")
            open_doors()  # 1 , 2
            wait()  # 3
            close_doors()  # 3
            out_requests[current_floor] = False
            if in_requests[current_floor] != -1:
                out_requests[in_requests[current_floor]] = True
            in_requests[current_floor] = -1
            direction_requests[current_floor] = "no"
        elif (state_doors == "closed" and direction == "up" and not out_requests[current_floor] and sig(current_floor, "out") != -1):
            print("3")
            go_up()  # 5
        elif (state_doors == "closed" and direction == "up" and direction_requests[current_floor] == "no" and sig(current_floor, "in") != -1):
            print("4")
            go_up()  # 6
        elif (state_doors == "closed" and direction == "down" and not out_requests[current_floor] and prec(current_floor, "out") != -1):
            print("5")
            go_down()  # 7
        elif (state_doors == "closed" and direction == "down" and direction_requests[current_floor] == "no" and prec(current_floor, "in") != -1):
            print("6")
            go_down()  # 8
        elif (state_doors == "closed" and direction == "up" and not out_requests[current_floor] and prec(current_floor, "out") != -1):
            print("7")
            go_down()  # 9
        elif (state_doors == "closed" and direction == "up" and direction_requests[current_floor] == "no" and prec(current_floor, "in") != -1):
            print("8")
            go_down()  # 10
        elif (state_doors == "closed" and direction == "down" and not out_requests[current_floor] and sig(current_floor, "out") != -1):
            print("9")
            go_up()  # 11
        elif (state_doors == "closed" and direction == "down" and direction_requests[current_floor] == "no" and sig(current_floor, "in") != -1):
            print("10")
            go_up()  # 12
        else:
            print("11")
            break


elevator()
