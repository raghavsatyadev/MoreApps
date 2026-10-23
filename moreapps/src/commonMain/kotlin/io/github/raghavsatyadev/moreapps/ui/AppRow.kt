@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.raghavsatyadev.moreapps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.raghavsatyadev.moreapps.model.AppDetails

/** One app: icon in an expressive shape, name, description and rating. */
@Composable
internal fun AppRow(app: AppDetails, onClick: () -> Unit, modifier: Modifier = Modifier) {
  ListItem(
    modifier = modifier.clickable(onClick = onClick),
    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
    leadingContent = { AppIcon(app) },
    headlineContent = {
      Text(
        app.name,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    },
    supportingContent = {
      Column {
        if (app.description.isNotBlank()) {
          Text(
            app.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
          )
        }
        if (app.rating > 0) {
          Spacer(Modifier.height(4.dp))
          RatingStars(app.rating)
        }
      }
    },
  )
}

/** The Apps icon sits under the image, so it shows while loading and when the link is broken. */
@Composable
private fun AppIcon(app: AppDetails) {
  Box(
    modifier =
      Modifier.size(56.dp)
        .clip(MaterialShapes.Cookie9Sided.toShape())
        .background(MaterialTheme.colorScheme.secondaryContainer),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      Icons.Rounded.Apps,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSecondaryContainer,
    )
    if (app.imageLink.isNotBlank()) {
      AsyncImage(
        model = app.imageLink,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(56.dp),
      )
    }
  }
}

/** Grouped-list corners: large on the outer edges of the group, small between rows. */
internal fun Modifier.clipToListShape(index: Int, count: Int): Modifier {
  val outer = 20.dp
  val inner = 4.dp
  val top = if (index == 0) outer else inner
  val bottom = if (index == count - 1) outer else inner
  return clip(RoundedCornerShape(top, top, bottom, bottom))
}
