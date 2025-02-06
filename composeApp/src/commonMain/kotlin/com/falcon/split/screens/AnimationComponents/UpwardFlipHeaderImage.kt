package com.falcon.split.screens.AnimationComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    picture : DrawableResource,
    lazyListState: LazyListState
) {
    val imageHeight = 250.dp

    // Get scroll offset and convert to rotation
    val scrollOffset = if (lazyListState.firstVisibleItemIndex == 0) {
        min(lazyListState.firstVisibleItemScrollOffset.toFloat(), 500f)
    } else 500f

    // Calculate rotation (0 to -90 degrees)
    val rotationDegrees = (scrollOffset / 750f) * +90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) {
        Image(
            painter = painterResource(picture),
            contentDescription = "Group header illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .graphicsLayer {
                    // Apply rotation transformation
                    rotationX = rotationDegrees
                    // Set pivot point to top edge
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    // Add perspective
                    cameraDistance = 8f * density
                    translationY = -100f
                }
                .clip(RectangleShape) // Clip the content to the bounds
                .padding(0.dp),
            contentScale = ContentScale.Crop
        )
    }
}