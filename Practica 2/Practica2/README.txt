EXTRAS PARA PONER DESDE EL PRINCIPIO:
	- Múltiples pozos, wumpus y oros. Cuando no quedan oros se vuelve.
	- Flechas para matar wumpus -> sigue habiendo hedor (cadaver)
	- Rejillas para tapar pozos -> sigue habiendo brisa

BOARD -> tablero original
     * EMPTY = 0
     * HOLE = 1
     * WUMPUS = 2
     * GOLD = 3
     * BREEZE = 4
     * STENCH = 5
     * BREEZE_STENCH = 6
     * GOLD_BREEZE = 7
     * GOLD_STENCH = 8
     * GOLD_BREEZE_STENCH = 9

known_board -> tablero con -1 en las casillas no visitadas y x en las sí visitadas
	esto nos ayudará para:
		1. Volver cuando encontremos el oro
		2. Disparar al wumpus
		3. Tapar pozos
		4. Asegurarnos de donde estan los wumpus y pozos (brisa rodeada de n-1 vacias implica que la n es pozo)
	hay que diferenciar el wumpus??? y pozo??? de wumpus!!! pozo!!!

heuristic_board -> tablero que permite llevar una cuenta de las casillas que hemos ido visitando.

Truquitos:
1. 	Si diagonalmente tenemos hedor/brisa y algo que no sea hedor/brisa correspondientemente significa que las casillas adyacentes a ambas casillas son seguras. 
	Las parejas seguras son:
	(vacio-brisa), (vacio-hedor), (vacio-brisa_hedor), (brisa-hedor), (vacio-oro_brisa), (vacio-oro_hedor), (oro_brisa-oro_hedor), (vacio-oro_brisa_hedor) 

2.	Si solo nos falta una casilla por conocer alrededor de una brisa/hedor podemos confirmar que es un pozo/wumpus

3.	Una vez hayamos encontrado todos los oros, debemos volver a la casilla inicial yendo solo sobre las casillas que estamos seguros que son safe
	