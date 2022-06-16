/*
 * This file is part of the droidVNC distribution (https://github.com/razar-dev/VNC-android).
 * Copyright © 2022 Sachindra Man Maskey.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.razar.vnc.other

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex

enum class SwapState {
    NONE,
    UP,
    DOWN
}

/**
 * Based on Android Open Source Project
 * [androidx.compose.foundation.gestures]
 */
suspend fun PointerInputScope.detectTapGestures(
    onLongPress: (() -> Unit),
    onDrag: (dragAmount: Offset, pressed: Boolean) -> Unit,
    onTap: (() -> Unit),
    onSwap: (state: SwapState) -> Unit,
    haptic: HapticFeedback,
) = coroutineScope {
    // special signal to indicate to the sending side that it shouldn't intercept and consume
    // cancel/up events as we're only require down events
    val pressScope = PressGestureScopeImpl(this@detectTapGestures)


    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown()
            down.consumeDownChange()
            pressScope.reset()
            var swap = false
            try {
                withTimeout(10) {
                    awaitPointerEvent()
                    swap = true
                }
            } catch (_: PointerEventTimeoutCancellationException) {
                swap = false
            }
            var pressed = false
            var drag: PointerInputChange? = null
            var overSlop = Offset.Zero
            var first: Offset? = null
            var startY = 0f
            val longPressTimeout = viewConfiguration.longPressTimeoutMillis
            try {
                // wait for first tap up or long press
                withTimeout(longPressTimeout) {
                    do {
                        drag = awaitPointerSlopOrCancellation(
                            down.id,
                            down.type
                        ) { change, over ->
                            if (first == null) first = over
                            change.consumePositionChange()
                            overSlop = over
                        }
                        startY = drag?.position?.y?:0f
                    } while (drag != null && !drag!!.positionChangeConsumed())//
                }
            } catch (_: PointerEventTimeoutCancellationException) {
                pressed = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                //drag with pressed button
                do {
                    drag = awaitPointerSlopOrCancellation(
                        down.id,
                        down.type
                    ) { change, over ->
                        change.consumePositionChange()
                        overSlop = over
                    }
                } while (drag != null && !drag!!.positionChangeConsumed())
            }
            if (drag != null) {
                onDrag(overSlop, pressed)
                if (
                    !drag(drag!!.id) {
                        if (swap) {
                            val value = startY - it.position.y
                            val state = when {
                                value > 200 -> SwapState.UP
                                value < 200 -> SwapState.DOWN
                                else -> SwapState.NONE
                            }
                            onSwap(state)
                        } else {
                            onDrag(it.positionChange(), pressed)
                            it.consumePositionChange()
                        }
                    }
                ) {
                    consumeUntilUp()

                    pressScope.release()
                } else {
                    pressScope.release()
                }
                if (swap)
                    onSwap(SwapState.NONE)
                onDrag(overSlop, false)
            } else {
                if (pressed) onLongPress() else onTap()
                pressScope.release()
            }
        }
    }
}

private class PressGestureScopeImpl(
    density: Density,
) : PressGestureScope, Density by density {
    private var isReleased = false
    private var isCanceled = false
    private val mutex = Mutex(locked = false)

    /**
     * Called when all pointers are up.
     */
    fun release() {
        isReleased = true
        mutex.unlock()
    }

    /**
     * Called when a new gesture has started.
     */
    fun reset() {
        mutex.tryLock() // If tryAwaitRelease wasn't called, this will be unlocked.
        isReleased = false
        isCanceled = false
    }

    override suspend fun awaitRelease() {
        if (!tryAwaitRelease()) {
            throw GestureCancellationException("The press gesture was canceled.")
        }
    }

    override suspend fun tryAwaitRelease(): Boolean {
        if (!isReleased && !isCanceled) {
            mutex.lock()
        }
        return isReleased
    }
}

private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    do {
        val event = awaitPointerEvent()
        event.changes.fastForEach { it.consumeAllChanges() }
    } while (event.changes.fastAny { it.pressed })
}

internal suspend fun AwaitPointerEventScope.awaitPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    onPointerSlopReached: (change: PointerInputChange, overSlop: Offset) -> Unit,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    var offset = Offset.Zero
    val touchSlop = viewConfiguration.pointerSlop(pointerType)

    var pointer = pointerId

    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return null
        if (dragEvent.positionChangeConsumed()) {
            return null
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return null
            } else {
                pointer = otherDown.id
            }
        } else {
            offset += dragEvent.positionChange()
            val distance = offset.getDistance()
            var acceptedDrag = false
            if (distance >= touchSlop) {
                val touchSlopOffset = offset / distance * touchSlop
                onPointerSlopReached(dragEvent, offset - touchSlopOffset)
                if (dragEvent.positionChangeConsumed()) {
                    acceptedDrag = true
                } else {
                    offset = Offset.Zero
                }
            }

            if (acceptedDrag) {
                return dragEvent
            } else {
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.positionChangeConsumed()) {
                    return null
                }
            }
        }
    }
}

// This value was determined using experiments and common sense.
// We can't use zero slop, because some hypothetical desktop/mobile devices can send
// pointer events with a very high precision (but I haven't encountered any that send
// events with less than 1px precision)
private val mouseSlop = 0.125.dp
private val defaultTouchSlop = 18.dp // The default touch slop on Android devices
private val mouseToTouchSlopRatio = mouseSlop / defaultTouchSlop

// TODO(demin): consider this as part of ViewConfiguration class after we make *PointerSlop*
//  functions public (see the comment at the top of the file).
//  After it will be a public API, we should get rid of `touchSlop / 144` and return absolute
//  value 0.125.dp.toPx(). It is not possible right now, because we can't access density.
internal fun ViewConfiguration.pointerSlop(pointerType: PointerType): Float {
    return when (pointerType) {
        PointerType.Mouse -> touchSlop * mouseToTouchSlopRatio
        else -> touchSlop
    }
}

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true