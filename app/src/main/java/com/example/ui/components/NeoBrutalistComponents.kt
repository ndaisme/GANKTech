package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GankColors

@Composable
fun NeoBrutalistCard(
    modifier: Modifier = Modifier,
    shadowOffset: Dp = 6.dp,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 8.dp,
    backgroundColor: Color = GankColors.White,
    borderColor: Color = GankColors.Ink,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(GankColors.Ink)
        )
        // Main content card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NeoBrutalistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = GankColors.GankYellow,
    icon: ImageVector? = null,
    shadowOffsetMax: Dp = 6.dp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset by animateDpAsState(
        if (!enabled) 2.dp else if (isPressed) 0.dp else shadowOffsetMax,
        label = "shadowOffset"
    )
    val contentOffset by animateDpAsState(
        if (!enabled) shadowOffsetMax - 2.dp else if (isPressed) shadowOffsetMax else 0.dp,
        label = "contentOffset"
    )

    val currentBg = if (enabled) containerColor else GankColors.Silver
    val borderCol = GankColors.Ink

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        // Background black shadow box
        Box(
            Modifier
                .matchParentSize()
                .offset(x = shadowOffsetMax, y = shadowOffsetMax)
                .background(GankColors.Ink, RoundedCornerShape(8.dp))
        )
        // Top action button box
        Box(
            Modifier
                .offset(x = contentOffset, y = contentOffset)
                .background(currentBg, RoundedCornerShape(8.dp))
                .border(3.dp, borderCol, RoundedCornerShape(8.dp))
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GankColors.Ink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Black,
                    color = GankColors.Ink,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoBrutalistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = GankColors.Ink,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, GankColors.Ink, RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GankColors.White,
                unfocusedContainerColor = GankColors.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = GankColors.Ink,
                focusedTextColor = GankColors.Ink,
                unfocusedTextColor = GankColors.Ink
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
fun NeoBrutalistBadge(
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(6.dp))
            .border(2.dp, GankColors.Ink, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = GankColors.Ink
        )
    }
}
