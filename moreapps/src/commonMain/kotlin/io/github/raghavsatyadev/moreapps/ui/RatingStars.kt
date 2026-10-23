package io.github.raghavsatyadev.moreapps.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.raghavsatyadev.moreapps.resources.Res
import io.github.raghavsatyadev.moreapps.resources.more_apps_rating
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

/** Five stars in half steps, read out as one "Rated x out of 5" label. */
@Composable
internal fun RatingStars(rating: Double, modifier: Modifier = Modifier, starSize: Dp = 16.dp) {
  val halves = (rating.coerceIn(0.0, 5.0) * 2).roundToInt()
  val label = stringResource(Res.string.more_apps_rating, formatRating(halves))
  Row(modifier.semantics(mergeDescendants = true) { contentDescription = label }) {
    repeat(5) { index ->
      val filled = halves - index * 2
      val icon =
        when {
          filled >= 2 -> Icons.Rounded.Star
          filled == 1 -> Icons.AutoMirrored.Rounded.StarHalf
          else -> Icons.Rounded.StarOutline
        }
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.size(starSize),
      )
    }
  }
}

private fun formatRating(halves: Int): String =
  if (halves % 2 == 0) "${halves / 2}" else "${halves / 2}.5"
