package com.example.data.security

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs

object QrCodeGenerator {

    /**
     * Generates a high-contrast QR-style matrix Bitmap encoding the ticket token.
     * Contains standard QR finder patterns at top-left, top-right, bottom-left,
     * timing strips, and deterministic payload bit encoding.
     */
    fun generateQrBitmap(data: String, size: Int = 512): Bitmap {
        val modules = 25 // 25x25 grid (similar to QR Version 2)
        val matrix = Array(modules) { BooleanArray(modules) }

        // 1. Draw 7x7 Finder Patterns
        fun drawFinder(startX: Int, startY: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    matrix[startY + r][startX + c] = isBorder || isCenter
                }
            }
        }

        drawFinder(0, 0) // Top Left
        drawFinder(modules - 7, 0) // Top Right
        drawFinder(0, modules - 7) // Bottom Left

        // 2. Timing Patterns
        for (i in 8 until modules - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // 3. Encode data bits deterministically
        val bytes = data.toByteArray(Charsets.UTF_8)
        var byteIdx = 0
        var bitIdx = 0

        for (r in 0 until modules) {
            for (c in 0 until modules) {
                // Skip finder patterns & timing lines
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= modules - 8
                val inBottomLeft = r >= modules - 8 && c < 8
                val inTiming = r == 6 || c == 6

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                    if (byteIdx < bytes.size) {
                        val currentByte = bytes[byteIdx].toInt()
                        val bit = (currentByte shr (7 - bitIdx)) and 1
                        matrix[r][c] = (bit == 1)
                        bitIdx++
                        if (bitIdx == 8) {
                            bitIdx = 0
                            byteIdx++
                        }
                    } else {
                        // Pseudo-random deterministic fill based on hash
                        val hash = abs((data.hashCode() * 31 + r * 17 + c * 23))
                        matrix[r][c] = (hash % 3 != 0)
                    }
                }
            }
        }

        // Render to Bitmap
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
        }

        val padding = size * 0.08f
        val drawingSize = size - (padding * 2)
        val cellSize = drawingSize / modules

        for (r in 0 until modules) {
            for (c in 0 until modules) {
                if (matrix[r][c]) {
                    val left = padding + c * cellSize
                    val top = padding + r * cellSize
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, paint)
                }
            }
        }

        return bitmap
    }
}
