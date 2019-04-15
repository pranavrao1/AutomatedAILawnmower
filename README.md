# assignment6
This is the repository for assignment 6 for group A6-60

These are the commands to compile and run the java program:
```
java -jar osmowsis.jar <name of test scenario file>
javac *.java
jar cfe osmowsis.jar Main *.class
```

## Mower Algorithm

The algorithm for each mower to determine what action to perform each turn.
# algorithm as discussed on March 27
1. if MOWING_DONE
    1. turn_off()
2. else if nearbysquare(UNKNOWN_CODE)
    1. scan()
3. else // move
    1. move max number of the steps possible where in current direction. (This probably needs some improvement)
        1. Do not ignore puppies only if found in current turn.
    2. On the new spot,
        1. if surrounded by any green
            1. pick a green square randomly as direction.
        2. else
            1. pick a safe square randomly as direction.
      
End of all mowers turn:
  Remove puppies from the map


# Pranav's algorithm (March 25)
1. Each turn the mower will check to see if it is adjacent to an empty square space. 
    1. If it is an empty square the mower will conduct a scan for the turn
2. The mower will then check to see if there is grass in the squares in the direction it is pointin too. If it is available on the map it will check the square after that (x+/- 2, y +/- 2). 
    1. It will them move two or one step in the currect direction.
    2. _An Enhancement can be made here if the map has information about the surouding squares on the destination, we can pick the direction it will turn too._
3. The mower will now check to see if the remaining seven adjacent sides have any green grass.
    1. It will also check one square over if the information is available. This is how we will assign priority of which side needs to be mowed first is listed below.
    2. Depending on which direction gets the higest points we can decide the direction to turn the mower.
    3. _An Enhancement can be made here if the map has information about the surouding squares on the destination, we can pick the direction it will turn too. We will need to que it here so that the mower will remember to include in its next turn._ 
4. If none of grass locations can be accessible, then the following actions can be done:
    1. Check to see if map is fenced off and all grass is mowed.
        1. Then turn off all mower.
    2. If not pick random direction by removing all frence, creater and other mower squares.
    3. Run validation to ensure random direction and move are correct with mower map.

Table 1: Points for each Grass Square.

Total Points | First Squre | Second Squre
------------ | ------------- | -------------
3 | Grass | Grass
2 | Grass | Empty
2 | Grass | Fence/Crater/Unknown
1 | Empty | Grass
0 | Empty | Empty
-1 | Fence/Crater/Unknown | Empty/Grass


#  Input File Format
Your system must be able to read in an input file to begin the simulation run. Given the addition of
puppies and other changes to the simulation environment, we are providing an updated input file
format:
1. <the width (horizontal/X-direction) of the lawn>
2. <the height (vertical/Y-direction) of the lawn>
3. <the number of lawnmowers being used>
4. <the mower “collision delay”: the number of turns stalled if it collides with another mower>
5. <the initial location and direction of each lawnmower> [one line per lawnmower]
6. <the number of craters on the lawn>
7. <the location of each crater> [one line per crater]
8. <the number of puppies on the lawn>
9. <the puppy “stay percentage”: the probability that a puppy stays at its current location>
10. <the initial location of each puppy> [one line per puppy]
11. <the maximum number of turns for the simulation>
