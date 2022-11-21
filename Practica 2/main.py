import numpy as np
import time

SIZE = 10
NUM_HOLES = 5
NUM_WUMPUS = 1
NUM_GOLD = 1
STENCH_PENALTY = 30
BREEZE_PENALTY = 20
BOARD = np.zeros((SIZE, SIZE))

heuristic_board = np.zeros((SIZE, SIZE))
safe_board = np.zeros((SIZE, SIZE))
known_board = np.zeros((SIZE, SIZE))
known_board.fill(-1)
current_pos = (0, 0)

# EMPTY = 0
# HOLE = 1
# WUMPUS = 2
# GOLD = 3
# BREEZE = 4
# STENCH = 5
# BREEZE_STENCH = 6
# GOLD_BREEZE = 7
# GOLD_STENCH = 8
# GOLD_BREEZE_STENCH = 9


def init_board():
    global BOARD
    rand_idx = np.random.choice(
        np.arange(1, SIZE * SIZE), NUM_HOLES + NUM_WUMPUS + NUM_GOLD, replace=False)

    for i in range(NUM_HOLES):
        BOARD[rand_idx[i] // SIZE, rand_idx[i] % SIZE] = 1  # HOLE

        if rand_idx[i] // SIZE > 0 and BOARD[rand_idx[i] // SIZE - 1, rand_idx[i] % SIZE] != 1:
            BOARD[rand_idx[i] // SIZE - 1, rand_idx[i] % SIZE] = 4  # BREEZE

        if rand_idx[i] // SIZE < SIZE - 1 and BOARD[rand_idx[i] // SIZE + 1, rand_idx[i] % SIZE] != 1:
            BOARD[rand_idx[i] // SIZE + 1, rand_idx[i] % SIZE] = 4  # BREEZE

        if rand_idx[i] % SIZE > 0 and BOARD[rand_idx[i] // SIZE, rand_idx[i] % SIZE - 1] != 1:
            BOARD[rand_idx[i] // SIZE, rand_idx[i] % SIZE - 1] = 4  # BREEZE

        if rand_idx[i] % SIZE < SIZE - 1 and BOARD[rand_idx[i] // SIZE, rand_idx[i] % SIZE + 1] != 1:
            BOARD[rand_idx[i] // SIZE, rand_idx[i] % SIZE + 1] = 4  # BREEZE

    for i in range(1, NUM_WUMPUS + 1):
        BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE] = 2  # WUMPUS

        if rand_idx[-i] // SIZE > 0 and BOARD[rand_idx[-i] // SIZE - 1, rand_idx[-i] % SIZE] == 0:
            BOARD[rand_idx[-i] // SIZE - 1, rand_idx[-i] % SIZE] = 5  # STENCH

        elif rand_idx[-i] // SIZE > 0 and BOARD[rand_idx[-i] // SIZE - 1, rand_idx[-i] % SIZE] == 4:
            BOARD[rand_idx[-i] // SIZE - 1, rand_idx[-i] %
                  SIZE] = 6  # BREEZE_STENCH

        if rand_idx[-i] // SIZE < SIZE - 1 and BOARD[rand_idx[-i] // SIZE + 1, rand_idx[-i] % SIZE] == 0:
            BOARD[rand_idx[-i] // SIZE + 1, rand_idx[-i] % SIZE] = 5  # STENCH
        elif rand_idx[-i] // SIZE < SIZE - 1 and BOARD[rand_idx[-i] // SIZE + 1, rand_idx[-i] % SIZE] == 4:
            BOARD[rand_idx[-i] // SIZE + 1, rand_idx[-i] %
                  SIZE] = 6  # BREEZE_STENCH

        if rand_idx[-i] % SIZE > 0 and BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE - 1] == 0:
            BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE - 1] = 5  # STENCH
        elif rand_idx[-i] % SIZE > 0 and BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE - 1] == 4:
            BOARD[rand_idx[-i] // SIZE, rand_idx[-i] %
                  SIZE - 1] = 6  # BREEZE_STENCH

        if rand_idx[-i] % SIZE < SIZE - 1 and BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE + 1] == 0:
            BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE + 1] = 5  # STENCH
        elif rand_idx[-i] % SIZE < SIZE - 1 and BOARD[rand_idx[-i] // SIZE, rand_idx[-i] % SIZE + 1] == 4:
            BOARD[rand_idx[-i] // SIZE, rand_idx[-i] %
                  SIZE + 1] = 6  # BREEZE_STENCH

    for i in range(1, NUM_GOLD + 1):
        if BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE, rand_idx[-i - NUM_WUMPUS] % SIZE] == 0:
            BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE,
                  rand_idx[-i - NUM_WUMPUS] % SIZE] = 3  # GOLD
        elif BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE, rand_idx[-i - NUM_WUMPUS] % SIZE] == 4:
            BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE,
                  rand_idx[-i - NUM_WUMPUS] % SIZE] = 7  # GOLD_BREEZE
        elif BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE, rand_idx[-i - NUM_WUMPUS] % SIZE] == 5:
            BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE,
                  rand_idx[-i - NUM_WUMPUS] % SIZE] = 8  # GOLD_STENCH
        elif BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE, rand_idx[-i - NUM_WUMPUS] % SIZE] == 6:
            BOARD[rand_idx[-i - NUM_WUMPUS] // SIZE,
                  rand_idx[-i - NUM_WUMPUS] % SIZE] = 9  # GOLD_BREEZE_STENCH


def check_surroundings():
    global BOARD, current_pos, heuristic_board, safe_board, known_board

    known_board[current_pos[0]][current_pos[1]
                                ] = BOARD[current_pos[0]][current_pos[1]]
    heuristic_board[current_pos[0]][current_pos[1]] += 1
    # Our agent can only move in 4 directions (up, down, left, right)
    if BOARD[current_pos[0]][current_pos[1]] == 1 or BOARD[current_pos[0]][current_pos[1]] == 2:
        return -1  # Agent is dead
    if BOARD[current_pos[0]][current_pos[1]] == 3 or BOARD[current_pos[0]][current_pos[1]] == 7 or\
            BOARD[current_pos[0]][current_pos[1]] == 8 or BOARD[current_pos[0]][current_pos[1]] == 9:
        safe_board[current_pos[0]][current_pos[1]] = 1
        return 1  # Agent has found the gold
    else:
        safe_board[current_pos[0]][current_pos[1]] = 1
        add_heuristic(known_board[current_pos[0]][current_pos[1]])
        thinking()
        return 0


def add_heuristic(mode):
    global current_pos, heuristic_board, safe_board, known_board
    adjs = [[0, 1], [0, -1], [1, 0], [-1, 0]]
    for i in range(4):
        if check_edges(adjs[i]):
            if safe_board[current_pos[0] + adjs[i][0]][current_pos[1] + adjs[i][1]] == 0:
                if mode == 4 or mode == 5:
                    heuristic_board[current_pos[0] + adjs[i]
                                    [0]][current_pos[1]+adjs[i][1]] += 20
                if mode == 6:
                    heuristic_board[current_pos[0] + adjs[i]
                                    [0]][current_pos[1] + adjs[i][1]] += 40


def thinking():
    global BOARD, current_pos, heuristic_board, safe_board, known_board
    diagonals = [[-1, -1], [-1, 1], [1, -1], [1, 1]]
    for i in range(4):
        if check_edges(diagonals[i]):
            if known_board[current_pos[0] + diagonals[i][0]][current_pos[1]+diagonals[i][1]] != -1:
                safe_pairs = [[0, 4], [0, 5], [0, 6], [0, 7], [0, 8], [0, 9], [4, 5], [4, 8], [
                    5, 7], [4, 0], [5, 0], [6, 0], [7, 0], [8, 0], [9, 0], [5, 4], [8, 4], [7, 5]]
                pair = [known_board[current_pos[0]][current_pos[1]],
                        known_board[current_pos[0] + diagonals[i][0]][current_pos[1] + diagonals[i][1]]]

                if pair in safe_pairs:
                    safe_board[current_pos[0],
                               current_pos[1] + diagonals[i][1]] = 1
                    safe_board[current_pos[0] +
                               diagonals[i][0], current_pos[1]] = 1
                    heuristic_board[current_pos[0],
                                    current_pos[1] + diagonals[i][1]] = 0
                    heuristic_board[current_pos[0] +
                                    diagonals[i][0], current_pos[1]] = 0


def check_edges(movement):
    global current_pos, SIZE
    if current_pos[0] + movement[0] < 0 or current_pos[0] + movement[0] > SIZE or current_pos[1] + movement[1] < 0 or \
            current_pos[1] + movement[1] > SIZE:
        return False
    return True


def move():
    global current_pos, heuristic_board
    move = ["RIGHT", "LEFT", "DOWN", "UP"]
    adjs = [[0, -1], [0, 1], [1, 0], [-1, 0]]
    min = 1000000
    for i in range(4):
        if check_edges(adjs[i]):
            if heuristic_board[current_pos[0] + adjs[i][0]][current_pos[1]+adjs[i][1]] < min:
                min = heuristic_board[current_pos[0] +
                                      adjs[i][0]][current_pos[1]+adjs[i][1]]
                min_idx = i

    print("Moving " + move[min_idx])
    current_pos = [current_pos[0] + adjs[min_idx]
                   [0], current_pos[1] + adjs[min_idx][1]]
    return


if __name__ == "__main__":
    init_board()
    print(BOARD)
    idx = 0
    while True:
        if idx < 200:
            idx += 1
            result = check_surroundings()
            if result == -1:
                print("Agent is dead")
                break
            if result == 1:
                print("Agent has found the gold")
                break
            if result == 0:
                move()
                print(heuristic_board)
                time.sleep(1)
        else:
            break
    print("\n known board")
    print(known_board)
    print("\n heuristic board")
    print(heuristic_board)
    print("\n safe board")
    print(safe_board)
