import android.util.Log
import androidx.compose.ui.graphics.PathOperation
import kotlin.reflect.typeOf
import kotlin.time.times

class GameManager {
    var tiles = mutableMapOf<Pair<Int, Int>, Int?>(
        (0 to 0) to 8,
        (1 to 0) to null,
        (2 to 0) to null,
        (3 to 0) to null,
        (0 to 1) to null,
        (1 to 1) to null,
        (2 to 1) to null,
        (3 to 1) to null,
        (0 to 2) to 8,
        (1 to 2) to null,
        (2 to 2) to null,
        (3 to 2) to null,
        (0 to 3) to 16,
        (1 to 3) to null,
        (2 to 3) to null,
        (3 to 3) to null,
    )

    fun startGame() {
        createTile()
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
//    private
     fun createTile() {
        // adds 20% changes for creating 4-value tile
        val value: Int = when((1..5).random()){
            5 -> 4
            else -> 2
        }

        var mapKey: Pair<Int, Int> = ((0..3).random() to (0..3).random())

        while(tiles[mapKey] != null){
            if (!tiles.containsValue(null)){
                gameLost()
                return
            }
            mapKey = ((0..3).random() to (0..3).random())
        }

        tiles[mapKey] = value
    }

    fun onSwap(direction: Direction) {
        val sortedTiles = connectTiles(direction)
        val tilesList = sortedTiles.toList().toMutableList()
        var tilesToCheck = 3
        var listIndex = 0

        sortedTiles.forEach{ el ->
            if (el.value == null) {
                for (i in 1 .. tilesToCheck) {
                    if (tilesList[i + listIndex + (3 - tilesToCheck)].second != null) {
                        tilesList[tilesList.indexOf(el.key to el.value)] = tilesList[tilesList.indexOf(el.key to el.value)].first to
                                tilesList[i + listIndex + (3 - tilesToCheck)].second
                        tilesList[i + listIndex + (3 - tilesToCheck)] = tilesList[i + listIndex + (3 - tilesToCheck)].first to
                                null
                        Log.d("", tilesList.toString())
                        sortedTiles[el.key] = tilesList[tilesList.indexOf(el.key to el.value)].second
                        sortedTiles[tilesList[i + listIndex + (3 - tilesToCheck)].first] = null
                        break
                    }
                }
            }
            else {
                for (i in 1 .. tilesToCheck) {
                    if (tilesList[i + listIndex + (3 - tilesToCheck)].second == tilesList[tilesList.indexOf(el.key to el.value)].second) {

                        break
                    }
                    if (
                        tilesList[tilesList.indexOf(el.key to el.value)].second != tilesList[i + listIndex + (3 - tilesToCheck)].second
                        && tilesList[i + listIndex + (3 - tilesToCheck)].second != null
                    ) {

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

        tiles = sortedTiles
        createTile()
    }

    private fun connectTiles(direction: Direction): MutableMap<Pair<Int, Int>, Int?>{
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

        return newMap
}

    fun gameLost() {

    }
}
