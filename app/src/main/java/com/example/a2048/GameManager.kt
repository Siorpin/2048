package com.example.a2048

import kotlinx.coroutines.delay
import java.util.function.UnaryOperator

class GameManager {
    private var tiles = mutableMapOf<Pair<Int, Int>, Int?>(
        (0 to 0) to null,
        (1 to 0) to null,
        (2 to 0) to null,
        (3 to 0) to null,
        (0 to 1) to null,
        (1 to 1) to null,
        (2 to 1) to null,
        (3 to 1) to null,
        (0 to 2) to null,
        (1 to 2) to null,
        (2 to 2) to null,
        (3 to 2) to null,
        (0 to 3) to null,
        (1 to 3) to null,
        (2 to 3) to null,
        (3 to 3) to null,
    )

    private var animation = mutableListOf<Pair<Int, Int>>()

    var score = 0

    fun startGame() {
        for (i in 0..15) {
            animation.add(0 to 0)
        }
        createTile()
    }

    fun updateTiles(tilesList: MutableList<Int?>) {
        for (i in 0 ..3) {
            for (j in 0 .. 3) {
                tiles[i to j] = tilesList[i*4 + j]
            }
        }
    }

    fun getTiles(): MutableList<Int?> {
        val gridElements: MutableList<Int?> = mutableListOf()
        tiles = tiles.toSortedMap(
            compareBy<Pair<Int, Int>>{it.first} then compareBy{it.second}
        )

        for (i in 0..3){
            for (j in 0..3){
                if (tiles.containsKey((i to j))){
                    gridElements.add(tiles[(i to j)])
                }
                else{
                    gridElements.add(null)
                }
            }
        }

        return gridElements
    }

    fun getAnimationValues(): MutableList<Pair<Int, Int>> {
        return animation
    }
//    private
     private fun createTile() {
        // adds 20% changes for creating 4-value tile
        val value: Int = when((1..5).random()){
            5 -> 4
            else -> 2
        }

        var mapKey: Pair<Int, Int> = ((0..3).random() to (0..3).random())

        while(tiles[mapKey] != null){
            if (!tiles.containsValue(null)){
                if (isGameOver()) gameLost()
                return
            }
            mapKey = ((0..3).random() to (0..3).random())
        }

        tiles[mapKey] = value
    }

    fun updateAnimationValue(
        direction: Direction,
        singleData: Pair<Pair<Int, Int>, Int>
        ) {
        var index = 0
        tiles.forEach{el ->
            if (el.key == singleData.first) {
                animation[index] = when(direction) {
                    Direction.TOP -> 0 to -singleData.second
                    Direction.RIGHT -> singleData.second to 0
                    Direction.BOTTOM -> 0 to singleData.second
                    Direction.LEFT -> -singleData.second to 0
                }
            }
            index++
        }
    }


    suspend fun onSwap(direction: Direction) {
        val tilesList = sortMapByDirection(direction)
        val previousTilesState = tiles
        var tilesToCheck = 3
        var listIndex = 0

        tiles = tiles.toSortedMap(
            compareBy<Pair<Int, Int>>{it.first} then compareBy{it.second}
        )

        animation.removeAll(animation)
        for (i in 0..15) {
            animation.add(0 to 0)
        }

        tilesList.forEach{ el ->
            if (el.second == null) {
                for (i in 1 .. tilesToCheck) {
                    // case when checked tile is not null - shift him
                    if (tilesList[i + listIndex + (3 - tilesToCheck)].second != null) {
                        // checking if we can connect any tile to this one
                        for (j in i+1 .. tilesToCheck) {
                            // if yes, then do it
                            if (tilesList[i + listIndex + (3 - tilesToCheck)].second == tilesList[j + listIndex + (3 - tilesToCheck)].second) {
                                tilesList[i + listIndex + (3 - tilesToCheck)] = tilesList[i + listIndex + (3 - tilesToCheck)].first to
                                        tilesList[i + listIndex + (3 - tilesToCheck)].second?.times(2)
                                tilesList[j + listIndex + (3 - tilesToCheck)] = tilesList[j + listIndex + (3 - tilesToCheck)].first to
                                        null

                                updateAnimationValue(direction, tilesList[j + listIndex + (3 - tilesToCheck)].first to j)
                                // add score
                                addScore(tilesList[i + listIndex + (3 - tilesToCheck)].second!!)
                                break
                            }
                            if (tilesList[j + listIndex + (3 - tilesToCheck)].second != null) {
                                break
                            }
                        }
                        // shifting modified (or not) tile to empty space
                        tilesList[tilesList.indexOf(el)] = el.first to
                                tilesList[i + listIndex + (3 - tilesToCheck)].second
                        tilesList[i + listIndex + (3 - tilesToCheck)] = tilesList[i + listIndex + (3 - tilesToCheck)].first to
                                null

                        updateAnimationValue(direction, tilesList[i + listIndex + (3 - tilesToCheck)].first to i)
                        break
                    }
                }
            }
            else {
                for (i in 1 .. tilesToCheck) {
                    // case when corresponding tile is different - break the loop and go to the next element
                    if (
                        tilesList[i + listIndex + (3 - tilesToCheck)].second != el.second &&
                        tilesList[i + listIndex + (3 - tilesToCheck)].second != null
                        ) {
                        break
                    }
                    // case when two corresponding tiles are the same - connect them
                    if (tilesList[i + listIndex + (3 - tilesToCheck)].second == el.second) {
                        tilesList[tilesList.indexOf(el)] = el.first to el.second?.times(2)
                        tilesList[i + listIndex + (3 - tilesToCheck)] = tilesList[i + listIndex + (3 - tilesToCheck)].first to
                                null

                        updateAnimationValue(direction, tilesList[i + listIndex + (3 - tilesToCheck)].first to i)
                        // add score
                        addScore(el.second!!)
                        break
                    }
                }
            }
            if (tilesToCheck == 0) {
                tilesToCheck = 3
                listIndex += 4
            }
            else tilesToCheck--

        }
        delay(200)
        animation.replaceAll { 0 to 0 }
        tiles = tilesList.toMap().toMutableMap()
        if (previousTilesState != tiles) createTile()
    }

    private fun sortMapByDirection(direction: Direction):  MutableList<Pair<Pair<Int, Int>, Int?>>{
        var newMap = mutableMapOf<Pair<Int, Int>, Int?>()
        if (direction == Direction.LEFT){
            newMap = tiles.toSortedMap(
                compareByDescending<Pair<Int, Int>>{it.first} then compareBy{it.second}
            )
        }
        if (direction == Direction.TOP){
            newMap = tiles.toSortedMap(
                compareByDescending<Pair<Int, Int>>{it.second} then compareBy{it.first}
            )
        }
        if (direction == Direction.RIGHT){
            newMap = tiles.toSortedMap(
                compareBy<Pair<Int, Int>>{it.first} then compareByDescending{it.second}
            )
        }
        if (direction == Direction.BOTTOM){
            newMap = tiles.toSortedMap(
                compareBy<Pair<Int, Int>>{it.second} then compareByDescending{it.first}
            )
        }

        return newMap.toList().toMutableList()
    }

    private fun isGameOver(): Boolean {
        var tilesList = sortMapByDirection(Direction.TOP)
        for (i in 0 .. 3) {
            for (j in i * 4 .. 4 * i + 2) {
                if (tilesList[j] == tilesList[j + 1]) return false
            }
        }
        tilesList = sortMapByDirection(Direction.LEFT)
        for (i in 0 .. 3) {
            for (j in i * 4 .. 4 * i + 2) {
                if (tilesList[j] == tilesList[j + 1]) return false
            }
        }
        return true
    }

    private fun gameLost() {

    }

    private fun addScore(scoreToAdd: Int) {
        score += scoreToAdd
    }

    fun resetTiles() {
        tiles.forEach{ el ->
            tiles[el.key] = null
        }
    }
}
