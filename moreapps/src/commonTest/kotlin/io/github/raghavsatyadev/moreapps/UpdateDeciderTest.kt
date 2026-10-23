package io.github.raghavsatyadev.moreapps

import io.github.raghavsatyadev.moreapps.data.MemoryStore
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.model.HardUpdateDetails
import io.github.raghavsatyadev.moreapps.model.RedirectDetails
import io.github.raghavsatyadev.moreapps.model.SoftUpdateDetails
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import io.github.raghavsatyadev.moreapps.update.SoftUpdateChannel
import io.github.raghavsatyadev.moreapps.update.SoftUpdateLimiter
import io.github.raghavsatyadev.moreapps.update.decideDialog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdateDeciderTest {
  private val all =
    AppDetails(
      minVersion = 5,
      currentVersion = 7,
      redirectDetails = RedirectDetails(enable = true, hardRedirect = true),
      hardUpdateDetails = HardUpdateDetails(enable = true),
      softUpdateDetails = SoftUpdateDetails(enable = true),
    )

  @Test
  fun redirectWinsOverUpdates() {
    assertEquals(UpdateDialogType.HARD_REDIRECT, decideDialog(all, 1))
    val soft = all.copy(redirectDetails = RedirectDetails(enable = true))
    assertEquals(UpdateDialogType.SOFT_REDIRECT, decideDialog(soft, 100))
  }

  @Test
  fun hardBelowMinVersionThenSoftBelowCurrent() {
    val noRedirect = all.copy(redirectDetails = RedirectDetails())
    assertEquals(UpdateDialogType.HARD_UPDATE, decideDialog(noRedirect, 4))
    assertEquals(UpdateDialogType.SOFT_UPDATE, decideDialog(noRedirect, 6))
    assertEquals(UpdateDialogType.NONE, decideDialog(noRedirect, 7))
  }

  @Test
  fun softCoversMinVersionWhenHardIsOff() {
    val app = all.copy(redirectDetails = RedirectDetails(), hardUpdateDetails = HardUpdateDetails())
    assertEquals(UpdateDialogType.SOFT_UPDATE, decideDialog(app, 4))
  }

  @Test
  fun missingEntryShowsNothing() {
    assertEquals(UpdateDialogType.NONE, decideDialog(null, 1))
  }

  @Test
  fun softLimitCountsPerVersionAndZeroIsUnlimited() {
    val limiter = SoftUpdateLimiter(MemoryStore())
    val dialog = SoftUpdateChannel.DIALOG
    assertTrue(limiter.shouldShow(dialog, limit = 2, publishedVersion = 7))
    limiter.recordShown(dialog)
    assertTrue(limiter.shouldShow(dialog, limit = 2, publishedVersion = 7))
    limiter.recordShown(dialog)
    assertFalse(limiter.shouldShow(dialog, limit = 2, publishedVersion = 7))
    assertTrue(limiter.shouldShow(dialog, limit = 0, publishedVersion = 7))
    // A newer published version starts the count again.
    assertTrue(limiter.shouldShow(dialog, limit = 2, publishedVersion = 8))
  }

  @Test
  fun dialogAndNotificationCountsAreIndependent() {
    val limiter = SoftUpdateLimiter(MemoryStore())
    assertTrue(limiter.shouldShow(SoftUpdateChannel.DIALOG, 1, 7))
    limiter.recordShown(SoftUpdateChannel.DIALOG)
    // 2.x: this call saw a "new" version and wiped the dialog count.
    assertTrue(limiter.shouldShow(SoftUpdateChannel.NOTIFICATION, 1, 7))
    assertFalse(limiter.shouldShow(SoftUpdateChannel.DIALOG, 1, 7))
  }
}
