package com.falcon.split.screens.AnimationComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min

@Composable
fun UpwardFlipHeaderImage(
    picture: DrawableResource,
    lazyListState: LazyListState
) {
    val imageHeight = 180.dp

    val scrollOffset = if (lazyListState.firstVisibleItemIndex == 0) {
        min(lazyListState.firstVisibleItemScrollOffset.toFloat(), 500f)
    } else 500f

    val rotationDegrees = (scrollOffset / 750f) * +90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Remove wrapContentSize and set a fixed height that accounts for translation
            .height(190.dp) // This should be imageHeight - translationValue in dp
    ) {
        Image(
            painter = painterResource(picture),
            contentDescription = "Group header illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .graphicsLayer {
                    rotationX = rotationDegrees
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    cameraDistance = 8f * density
                    translationY = 0f
                }
                .clip(RectangleShape),
            contentScale = ContentScale.FillWidth
        )
    }
}