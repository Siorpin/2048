import android.util.Log
import androidx.compose.ui.graphics.PathOperation

class GameManager {
    val tiles = mutableMapOf<Pair<Int, Int>, Int?>(
        (0 to 0) to 2,
        (1 to 0) to 2,
        (2 to 0) to 2,
        (3 to 0) to 2,
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

    fun startGame() {
        createTile()
    }

    fun getTiles(): MutableList<Int?> {
        val gridElements: MutableList<Int?> = mutableListOf()

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
        connectTiles(direction)
//        if (direction == Direction.RIGHT){
//            for (i in 0 .. 3){
//                val newRow = connectTiles(mutableListOf(), (i to 3), -1, 0, direction)
//
//                for (j in 0 .. 3){
//                    tiles[i to j] = newRow[j]
//                }
//            }
//        }
//        if (direction == Direction.LEFT){
//            for (i in 0 .. 3){
//                val newRow = connectTiles(mutableListOf(), (i to 0), 1, 0, direction)
//
//                for (j in 3 downTo 0){
//                    tiles[i to j] = newRow[j]
//                }
//            }
//        }
//        if (direction == Direction.TOP){
//            for (i in 0 .. 3){
//                val newRow = connectTiles(mutableListOf(), (0 to i), 0, 1, direction)
//
//                for (j in 0 ..  3){
//                    tiles[j to i] = newRow[j]
//                }
//            }
//        }
//        if (direction == Direction.BOTTOM){
//            for (i in 0 .. 3){
//                val newRow = connectTiles(mutableListOf(), (3 to i), 0, -1, direction)
//
//                for (j in 0 ..  3){
//                    tiles[j to i] = newRow[j]
//                }
//            }
//        }
        createTile()
    }

    private fun connectTiles(direction: Direction){
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















    //        if (row.size == 4){
//            if (offsetY < 0 || offsetX < 0){
//                // idk why, it just works lol
//                row.reverse()
//                // swapping row to the right
//                for (s in 0 .. 3){
//                    for (i in 3 downTo 0) {
//                        if (row[i] == null){
//                            for (j in i downTo 1){
//                                row[j] = row[j-1]
//                            }
//                            row[0] = null
//                        }
//                    }
//                }
//
//                // iterating from right to left tile
//                for (i in 3 downTo 0){
//                    // checking if two tiles are equal
//                    for (j in i - 1 downTo 0){
//                        // prevents jumping
//                        if (row[i] != row[j]){
//                            if (row[i + 1] == null){
//                                row[i + 1] = row[i]
//                                row[i] = null
//                            }
//                            break
//                        }
//                        if (row[i] == row[j]){
//                            row[i] = row[i]?.times(2)
//                            row[j] = null
//                            if (row[i + 1] == null){
//                                row[i + 1] = row[i]
//                                row[i] = null
//                            }
//
//                            // break prevents fast summing (very important, don't ask questions)
//                            break
//                        }
//
//                    }
//                }
//            }
//
//            if(offsetY > 0 || offsetX > 0){
//                // swapping row to the left
//                for (s in 0 .. 3){
//                    for (i in 0 .. 3) {
//                        if (row[i] == null){
//                            for (j in i .. 2){
//                                row[j] = row[j+1]
//                            }
//                            row[3] = null
//                        }
//                    }
//                }
//                // iterating from left to right tile
//                for (i in 0 .. 3){
//                    // checking if two tiles are equal
//                    for (j in i + 1 .. 3){
//                        // prevents jumping
//                        if (row[i] != row[j]){
//                            break
//                        }
//                        if (row[i] == row[j]){
//                            row[i] = row[i]?.times(2)
//                            row[j] = null
//                            // break prevents fast summing (very important, don't ask questions)
//                            break
//                        }
//                    }
//                }
//            }
//            return row
//        }
//        row.add(tiles[tile])
//        return connectTiles(row, (tile.first + offsetY to tile.second + offsetX), offsetX, offsetY, direction)
}

    fun gameLost() {

    }
}
