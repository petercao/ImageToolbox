package ru.tech.imageresizershrinker.presentation.root.theme.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Analytics: ImageVector
    get() {
        if (_analytics != null) {
            return _analytics!!
        }
        _analytics = Builder(
            name = "Analytics", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 192.0f, viewportHeight = 192.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF9AB00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(130.0f, 29.0f)
                verticalLineToRelative(132.0f)
                curveToRelative(0.0f, 14.77f, 10.19f, 23.0f, 21.0f, 23.0f)
                curveToRelative(10.0f, 0.0f, 21.0f, -7.0f, 21.0f, -23.0f)
                verticalLineTo(30.0f)
                curveToRelative(0.0f, -13.54f, -10.0f, -22.0f, -21.0f, -22.0f)
                reflectiveCurveTo(130.0f, 17.33f, 130.0f, 29.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE37400)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(75.0f, 96.0f)
                verticalLineToRelative(65.0f)
                curveToRelative(0.0f, 14.77f, 10.19f, 23.0f, 21.0f, 23.0f)
                curveToRelative(10.0f, 0.0f, 21.0f, -7.0f, 21.0f, -23.0f)
                verticalLineTo(97.0f)
                curveToRelative(0.0f, -13.54f, -10.0f, -22.0f, -21.0f, -22.0f)
                reflectiveCurveTo(75.0f, 84.33f, 75.0f, 96.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE37400)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(41.0f, 163.0f)
                moveToRelative(-21.0f, 0.0f)
                arcToRelative(
                    21.0f, 21.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 42.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    21.0f, 21.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -42.0f,
                    dy1 = 0.0f
                )
            }
        }
            .build()
        return _analytics!!
    }

private var _analytics: ImageVector? = null