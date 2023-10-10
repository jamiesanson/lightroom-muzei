package dev.sanson.lightroom.circuit

import com.slack.circuitx.android.AndroidScreen
import kotlinx.parcelize.Parcelize

/**
 * An [AndroidScreen] which finishes the current activity with a given [requestCode] set as result
 */
@Parcelize
data class FinishActivityScreen(val requestCode: Int) : AndroidScreen
