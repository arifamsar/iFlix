package com.arfsar.iflix.ui.foundation

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arfsar.iflix.ui.theme.IFlixRed

/**
 * Custom iFlix ripple implementation using the new Indication API.
 * This is a modern replacement for the deprecated rememberRipple function.
 *
 * This implementation uses the new createRippleModifierNode API which provides
 * better performance and is compatible with the latest Material Design guidelines.
 */

/**
 * Creates an iFlix-themed ripple indication using the modern Indication API.
 *
 * @param bounded Whether the ripple should be bound to the composable's size.
 * @param radius The radius of the ripple. Use Dp.Unspecified for default behavior.
 * @param color The color of the ripple. Defaults to IFlixRed.
 */
@Composable
fun iFlixRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = IFlixRed
): Indication {
    return IFlixRippleIndication(
        bounded = bounded,
        radius = radius,
        color = color
    )
}

/**
 * iFlix ripple indication implementation that uses createRippleModifierNode
 * for optimal performance and Material 3 compatibility.
 */
@Immutable
private class IFlixRippleIndication(
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: Color
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return createRippleModifierNode(
            interactionSource = interactionSource,
            bounded = bounded,
            radius = radius,
            color = { color },
            rippleAlpha = { IFlixRippleAlpha }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IFlixRippleIndication) return false

        if (bounded != other.bounded) return false
        if (radius != other.radius) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bounded.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}

/**
 * Custom ripple alpha values for iFlix theme.
 * These values control the opacity of the ripple effect in different states.
 */
@Stable
private val IFlixRippleAlpha = RippleAlpha(
    pressedAlpha = 0.16f,
    focusedAlpha = 0.12f,
    draggedAlpha = 0.16f,
    hoveredAlpha = 0.08f
)

/**
 * Creates a custom unbounded ripple for navigation items and floating actions.
 * This is optimized for navigation bar items and icon buttons.
 *
 * @param radius The radius of the unbounded ripple.
 * @param color The color of the ripple.
 */
@Composable
fun iFlixUnboundedRipple(
    radius: Dp = 36.dp,
    color: Color = IFlixRed
): Indication {
    return iFlixRipple(
        bounded = false,
        radius = radius,
        color = color
    )
}

/**
 * Creates a custom bounded ripple for buttons and clickable cards.
 * The ripple will be clipped to the bounds of the component.
 *
 * @param color The color of the ripple.
 */
@Composable
fun iFlixBoundedRipple(
    color: Color = IFlixRed
): Indication {
    return iFlixRipple(
        bounded = true,
        radius = Dp.Unspecified,
        color = color
    )
}

