package com.example.a2048

import android.os.Bundle
import android.service.quicksettings.Tile
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2048.ui.theme._2048Theme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val gameManager = GameManager()

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _2048Theme {
                Scaffold(
                    containerColor = Color(0xFFFAD6CA),
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFFFDCFC0)
                            ),
                            title = {
                            Text(
                                text = stringResource(R.string.GameTitle),
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.W500,
                                fontSize = 30.sp
                                )
                            },
                            modifier = Modifier
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    gameManager.startGame()

                    MainSurface(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainSurface(modifier: Modifier = Modifier) {
    var tiles by remember {
        mutableStateOf(gameManager.getTiles())
    }
    var previousTilesValue by remember {
        mutableStateOf(gameManager.getTiles())
    }
    val animation by remember {
        mutableStateOf(gameManager.getAnimationValues())
    }
    var gameScore: Int by remember { // It will be modified by Game() function
        mutableIntStateOf(gameManager.score)
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ){
        Score(gameScore)
        Game(
            tiles,
            animation,
            {
                previousTilesValue = tiles
                animation.replaceAll{0 to 0}
                tiles = gameManager.getTiles()
                gameScore = gameManager.score
            }
        )
        ButtonsRow(
            resetButtonFunction = {
                gameScore = 0
                gameManager.resetTiles()
                gameManager.startGame()
                tiles = gameManager.getTiles()
            },
            backButtonFunction = {
                tiles = previousTilesValue
                gameManager.updateTiles(tiles)
            }
        )
    }
}

@Composable
fun Game(
    tiles: MutableList<Int?>,
    animation: MutableList<Pair<Int, Int>>,
    modifyScore: () -> Unit,
    modifier: Modifier = Modifier
){
    // List that contains current game state. Refreshing every swap
    Box(
        modifier = modifier
            .padding(20.dp)
            .pointerInput(Unit) {
                var drag = 0f
                // detects horizontal dragging
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        drag = dragAmount
                    },
                    onDragEnd = {
                        // left
                        if (drag < 0) {
                            GlobalScope.launch {gameManager.onSwap(Direction.LEFT) }

                        }
                        // right
                        if (drag > 0) {
                            GlobalScope.launch {gameManager.onSwap(Direction.RIGHT)}
                        }
                        modifyScore()
                    }
                )
            }
            .pointerInput(Unit) {
                var dx = 0f
                // detects vertical dragging
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        dx = dragAmount
                    },
                    onDragEnd = {
                        // top
                        if (dx < 0) {
                            GlobalScope.launch {gameManager.onSwap(Direction.TOP)}
                        }
                        // down
                        if (dx > 0) {
                            GlobalScope.launch {gameManager.onSwap(Direction.BOTTOM)}
                        }
                        modifyScore()
                    }
                )
            }
    ){
        // this grid contains border, background and "empty" boxes
        LazyVerticalGrid(
            contentPadding = PaddingValues(5.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = modifier
                .background(
                    color = Color(0xFFFDCFC0),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .border(
                    width = 5.dp,
                    color = Color(0xFFFDCFC0),
                    shape = RoundedCornerShape(16.dp)
                )
                .aspectRatio(1f)
        ) {
            // spawning 16 empty boxes
            items(16){
                Box(
                    modifier = modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .background(color = Color(0xFFE6E6E6))
                )
            }
        }
        // this grid contains only value boxes - no border, no background
        LazyVerticalGrid(
            contentPadding = PaddingValues(5.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            var itemsCounter = 0
            items(tiles){element ->
                when(itemsCounter) {
                    0 -> GameCell(
                        element,
                        animation[itemsCounter],
                        RoundedCornerShape(topStart = 12.dp)
                    )
                    3 -> GameCell(
                        element,
                        animation[itemsCounter],
                        RoundedCornerShape(topEnd = 12.dp)

                    )
                    12 -> GameCell(
                        element,
                        animation[itemsCounter],
                        RoundedCornerShape(bottomStart = 12.dp)
                    )
                    15 -> GameCell(
                        element,
                        animation[itemsCounter],
                        RoundedCornerShape(bottomEnd = 12.dp)

                    )

                    else -> GameCell(
                        element,
                        animation[itemsCounter],
                    )
                }
                itemsCounter++
            }
        }
    }
}

@Composable
fun GameCell(
    value: Int?,
    animation: Pair<Int, Int>,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
    modifier: Modifier = Modifier
){
    var height by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    var xOffset = animateDpAsState(
        if (animation.first != 0) (height + 5.dp) * animation.first
        else 0.dp,
        tween(
            durationMillis = 20
            )
    )
    var yOffset = animateDpAsState(
        if (animation.second != 0) (height + 5.dp) * animation.second
        else 0.dp,
        tween(
            durationMillis = 200
        )
    )

    val text: String = when(value){
        null -> ""
        else -> value.toString()
    }

    val blockColor: Color = when(value){
        2 -> Color(0xFFFAF3B1)
        4 -> Color(0xFFFAF096)
        8 -> Color(0xFFFDE87F)
        16 -> Color(0xFFFDE467)
        32 -> Color(0xFFFFE14A)
        64 -> Color(0xFFFFDB23)
        128 -> Color(0xFFFDD406)
        256 -> Color(0xFFFC6B6B)
        512 -> Color(0xFFFF3030)
        1024 -> Color(0xFFDE68FF)
        2048 -> Color(0xFFCC17FF)
        4096 -> Color(0xFF8800FF)
        else -> Color.Transparent
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .offset(xOffset.value, yOffset.value)
            .onGloballyPositioned {
                height = with(density) {
                    it.size.height.toDp()
                }
            }
    ){
        Surface(
            color = blockColor,
            modifier = modifier
                .fillMaxSize(),
            shape = shape
        ) {}
        Text(
            text = text,
            color = Color(0xFFFDFDFD),
            fontSize = 24.sp,
            fontWeight = FontWeight.W900,
            modifier = modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun Score(
    score: Int,
    modifier: Modifier = Modifier
){
    Text(
        text = stringResource(R.string.score, score),
        fontSize = 24.sp,
        fontWeight = FontWeight.W400
    )
}

@Composable
fun ButtonsRow(
    resetButtonFunction: () -> Unit,
    backButtonFunction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
    ) {
        GameButton("go back Button", Icons.Default.ArrowBack, backButtonFunction)
        GameButton("restart the game button", Icons.Default.Refresh, resetButtonFunction)
    }
}

@Composable
fun GameButton (
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Icon(icon, contentDescription)
        }
    }
}