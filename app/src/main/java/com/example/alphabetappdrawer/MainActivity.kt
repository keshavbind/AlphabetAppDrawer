package com.example.alphabetappdrawer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import java.util.Locale
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import androidx.compose.material3.Text

private val Alphabet = ('A'..'Z').map { it.toString() }

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0B0B0F)
                ) {

                    AppDrawerScreen()
                }
            }
        }
    }
}



@Composable
fun AppDrawerScreen() {

    val context = LocalContext.current

    var apps by remember {
        mutableStateOf<List<AppInfo>>(emptyList())
    }

    var selectedLetter by remember {
        mutableStateOf("A")
    }

    var isDragging by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        apps = getInstalledApps(context)
    }


    val filteredApps = remember(
        apps,
        selectedLetter
    ) {

        apps.filter {

            it.name
                .trim()
                .uppercase(Locale.getDefault())
                .startsWith(selectedLetter)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0B0B0F)
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    top = 30.dp,
                    bottom = 20.dp,
                    end = 65.dp
                )
        ) {

            Text(
                text = selectedLetter,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "${filteredApps.size} apps",
                color = Color(0xFF85858C),
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (filteredApps.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No apps starting with $selectedLetter",
                        color = Color(0xFF77777F),
                        fontSize = 15.sp
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                    Arrangement.spacedBy(6.dp)
                ) {

                    items(
                        items = filteredApps,
                        key = {
                            it.packageName
                        }
                    ) { app ->

                        AppRow(
                            app = app,
                            onClick = {
                                launchApp(
                                    context,
                                    app.packageName
                                )
                            }
                        )
                    }
                }
            }
        }



        AlphabetBar(
            selectedLetter = selectedLetter,
            isDragging = isDragging,

            onLetterSelected = { letter ->
                selectedLetter = letter
            },

            onDragStateChanged = { dragging ->
                isDragging = dragging
            },

            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(58.dp)
        )
    }
}



@Composable
fun AppRow(
    app: AppInfo,
    onClick: () -> Unit
) {

    val bitmap = remember(app.packageName) {

        app.icon
            .toBitmap(
                width = 96,
                height = 96
            )
            .asImageBitmap()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(
                Color(0xFF15151A)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            bitmap = bitmap,
            contentDescription = app.name,

            modifier = Modifier
                .size(44.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Text(
            text = app.name,
            color = Color.White,
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}



@Composable
fun AlphabetBar(
    selectedLetter: String,
    isDragging: Boolean,
    onLetterSelected: (String) -> Unit,
    onDragStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val maxBendPx = with(density) { 55.dp.toPx() }

    var touchY by remember { mutableFloatStateOf(-1f) }
    var barHeight by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .onSizeChanged { barHeight = it.height.toFloat() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDragStateChanged(true)
                        touchY = offset.y

                        val index = calculateLetterIndex(
                            offset.y,
                            size.height.toFloat()
                        )
                        onLetterSelected(Alphabet[index])
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchY = change.position.y

                        val index = calculateLetterIndex(
                            change.position.y,
                            size.height.toFloat()
                        )
                        onLetterSelected(Alphabet[index])
                    },
                    onDragEnd = {
                        onDragStateChanged(false)
                        touchY = -1f
                    },
                    onDragCancel = {
                        onDragStateChanged(false)
                        touchY = -1f
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.End
        ) {
            Alphabet.forEachIndexed { index, letter ->
                CurvedLetter(
                    index = index,
                    letter = letter,
                    selectedLetter = selectedLetter,
                    touchY = touchY,
                    barHeight = barHeight,
                    maxBendPx = maxBendPx
                )
            }
        }

        if (isDragging) {
            LetterBubble(
                letter = selectedLetter,
                touchY = touchY
            )
        }
    }
}



@Composable
fun CurvedLetter(
    index: Int,
    letter: String,
    selectedLetter: String,
    touchY: Float,
    barHeight: Float,
    maxBendPx: Float
) {
    val letterPosition =
        index.toFloat() / (Alphabet.size - 1)

    val touchPosition =
        if (touchY >= 0f && barHeight > 0f)
            (touchY / barHeight).coerceIn(0f, 1f)
        else -10f

    val distance = abs(letterPosition - touchPosition)
    val sigma = 0.15f

    val influence =
        if (touchY >= 0f)
            exp(-(distance * distance) / (2f * sigma * sigma)).toFloat()
        else 0f

    val strongInfluence = influence * influence

    val targetX = -maxBendPx * strongInfluence

    val targetScale =
        1f + (0.55f * strongInfluence)

    val direction =
        when {
            letterPosition < touchPosition -> -1f
            letterPosition > touchPosition -> 1f
            else -> 0f
        }

    val targetRotation =
        direction * 28f * influence

    val verticalDirection =
        if (letterPosition < touchPosition) -1f else 1f

    val targetY =
        verticalDirection * 5f * strongInfluence

    val xOffset = remember(index) { Animatable(0f) }
    val yOffset = remember(index) { Animatable(0f) }
    val letterScale = remember(index) { Animatable(1f) }
    val letterRotation = remember(index) { Animatable(0f) }

    LaunchedEffect(targetX) {
        xOffset.animateTo(
            targetX,
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
    }

    LaunchedEffect(targetY) {
        yOffset.animateTo(
            targetY,
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
    }

    LaunchedEffect(targetScale) {
        letterScale.animateTo(
            targetScale,
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
    }

    LaunchedEffect(targetRotation) {
        letterRotation.animateTo(
            targetRotation,
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
    }

    Text(
        text = letter,
        color = if (letter == selectedLetter)
            Color.White
        else
            Color(0xFF77777F),
        fontSize = if (letter == selectedLetter)
            15.sp
        else
            12.sp,
        fontWeight = if (letter == selectedLetter)
            FontWeight.Bold
        else
            FontWeight.Normal,
        modifier = Modifier
            .graphicsLayer {
                translationX = xOffset.value
                translationY = yOffset.value
                scaleX = letterScale.value
                scaleY = letterScale.value
                rotationZ = letterRotation.value
            }
            .padding(end = 10.dp)
    )
}




@Composable
fun BoxScope.LetterBubble(
    letter: String,
    touchY: Float
) {

    if (touchY < 0f) {
        return
    }

    val density =
        LocalDensity.current

    val bubbleSizePx =
        with(density) {
            52.dp.toPx()
        }

    val y =
        (
                touchY -
                        bubbleSizePx / 2f
                ).roundToInt()

    Box(
        modifier = Modifier
            /*
             * Because this function is BoxScope,
             * align() now works correctly.
             */
            .align(Alignment.CenterEnd)

            .padding(
                end = 52.dp
            )

            .offset {
                IntOffset(
                    x = 0,
                    y = y
                )
            }

            .size(52.dp)

            .clip(
                CircleShape
            )

            .background(
                Color(0xFF2D2D35)
            ),

        contentAlignment =
        Alignment.Center
    ) {

        androidx.compose.material3.Text(
            text = letter,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



fun calculateLetterIndex(
    y: Float,
    height: Float
): Int {

    if (height <= 0f) {
        return 0
    }

    val normalized =
        (y / height)
            .coerceIn(
                0f,
                0.999999f
            )

    val index =
        (
                normalized *
                        Alphabet.size
                ).toInt()

    return index.coerceIn(
        0,
        Alphabet.lastIndex
    )
}



fun getInstalledApps(
    context: Context
): List<AppInfo> {

    val packageManager =
        context.packageManager

    val intent =
        Intent(
            Intent.ACTION_MAIN,
            null
        ).apply {

            addCategory(
                Intent.CATEGORY_LAUNCHER
            )
        }

    val activities =
        packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL
        )

    return activities

        .mapNotNull { resolveInfo ->

            val name =
                resolveInfo
                    .loadLabel(packageManager)
                    ?.toString()
                    ?.trim()

            if (name.isNullOrEmpty()) {
                return@mapNotNull null
            }

            val packageName =
                resolveInfo
                    .activityInfo
                    .packageName

            val icon =
                resolveInfo
                    .loadIcon(packageManager)

            AppInfo(
                name = name,
                packageName = packageName,
                icon = icon
            )
        }

        .distinctBy {
            it.packageName
        }


        .filter {

            val first =
                it.name
                    .uppercase(Locale.getDefault())
                    .firstOrNull()

            first != null &&
                    first in 'A'..'Z'
        }


        .sortedBy {

            it.name.lowercase(
                Locale.getDefault()
            )
        }
}



fun launchApp(
    context: Context,
    packageName: String
) {

    try {

        val launchIntent =
            context.packageManager
                .getLaunchIntentForPackage(
                    packageName
                )

        launchIntent?.let {

            it.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(it)
        }

    } catch (e: Exception) {

        e.printStackTrace()
    }
}