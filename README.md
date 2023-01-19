# 2048-Player

This project contains all of the files for a system that plays the simple game "2048."

## Installation
From a command line, with Java installed:
```bash
javac Strategy2048.java
```

## Usage
```bash
java Strategy2048
```
The system will show every move played sequentially and will stop when the game is over.

## Gameplay
2048 is a game in which the user swipes left, right, up, or down to control the flow of numbered blocks on a 4x4 grid. Blocks of the same value will combine to form one block of double the value. After each swipe, a new block having a value of 2 or 4 will be randomly generated on the grid. 

This app plays the game and outputs the board after each move on the command line. The goal was to see if it was possible to encode my own gameplay strategy into a Java application. While not perfect, the app generally does a good job at the game. 

## Explanation of the separate classes created
### Play2048.java
* This acts as a basic interface to play the game 2048.

### Strategy2048.java
* This class uses the Play2048 class to play the game, using strategy.

### Goal.java
* Abstract class representing a "goal," an object that is used by the Strategy2048 class. 
* A goal is simply something the system is trying to accomplish in gameplay. 

### All other classes
* All the remaining classes extend "Goal.java" to form specific goals that the Strategy2048 class can use. 
* These classes are
    * Board.java
    * Combine.java
    * Fill.java
    * Get.java
    * Move.java
    * MoveHighest.java
    * OneMove.java