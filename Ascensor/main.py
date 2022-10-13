# Authors:
#   -   Julián Wallis Medina
#   -   Jonathan Salisbury Vega

import time

# Constants
NUM_FLOORS = 10
DELTA = 0.5

# Our elevator can sense the following information:
# S1. The current floor
current_floor = 0

# S2. To which floors do the people want to go
out_requests = [False] * NUM_FLOORS

# S3. In which floors do people want to get in
in_requests = [False] * NUM_FLOORS

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
            if (in_requests[i]):
                return i
    else:
        for i in range(floor, NUM_FLOORS):
            if (out_requests[i]):
                return i
    return 0


# returns the next lower floor for which there is an exit/entry request
def prec(floor, in_out):
    if (in_out == "in"):
        for i in range(floor, -1, -1):
            if (in_requests[i]):
                return i
    else:
        for i in range(floor, -1, -1):
            if (out_requests[i]):
                return i
    return 0


# Function that implements A1
def go_up():
    global current_floor
    current_floor += 1
    print("Going up to floor", current_floor)
    return current_floor


# Function that implements A2
def go_down():
    global current_floor
    current_floor -= 1
    print("Going down to floor", current_floor)
    return current_floor


# Function that implements A3
def open_doors():
    global state_doors
    state_doors = "open"
    print("Doors are open")
    return state_doors


# Function that implements A4
def close_doors():
    global state_doors
    state_doors = "closed"
    print("Doors are closed")
    return state_doors


# Function that implements A5
def wait():
    print("Waiting for", DELTA, "seconds")
    time.sleep(DELTA)
    return DELTA


# Function that implements the elevator's behaviour
def elevator():
    global current_floor, direction
    while True:
        if (state_doors == "closed" and (in_requests[current_floor] or out_requests[current_floor])):
            open_doors()  # 1 , 2
            wait()  # 3
            close_doors()  # 3
            out_requests[current_floor] = False
            in_requests[current_floor] = False
        elif (state_doors == "closed" and direction == "up" and out_requests[current_floor] and sig(current_floor, "out") != 0):
            go_up()  # 5
        elif (state_doors == "closed" and direction == "up" and in_requests[current_floor] and sig(current_floor, "in") != 0):
            go_up()  # 6
        elif (state_doors == "closed" and direction == "down" and out_requests[current_floor] and prec(current_floor, "out") != 0):
            go_down()  # 7
        elif (state_doors == "closed" and direction == "down" and in_requests[current_floor] and prec(current_floor, "in") != 0):
            go_down()  # 8
        elif (state_doors == "closed" and direction == "up" and out_requests[current_floor] and prec(current_floor, "out") != 0):
            go_down()  # 9
        elif (state_doors == "closed" and direction == "up" and in_requests[current_floor] and prec(current_floor, "in") != 0):
            go_down()  # 10
        elif (state_doors == "closed" and direction == "down" and out_requests[current_floor] and sig(current_floor, "out") != 0):
            go_up()  # 11
        elif (state_doors == "closed" and direction == "down" and in_requests[current_floor] and sig(current_floor, "in") != 0):
            go_up()  # 12
