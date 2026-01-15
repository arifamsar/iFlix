package com.arfsar.iflix.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class IFlixButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINED,
    TEXT
}

@Composable
fun IFlixButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IFlixButtonVariant = IFlixButtonVariant.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    when (variant) {
        IFlixButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled && !loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
        IFlixButtonVariant.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled && !loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
        IFlixButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled && !loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
        IFlixButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled && !loading,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

