package com.binay.shaw.justap.utilities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.binay.shaw.justap.utilities.ImageUtils.addBitmapOverlay
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import java.io.IOException

@Throws(WriterException::class, IOException::class)
fun String.roundedQRGenerator(
    dimension: Int,
    overlayBitmap: Bitmap? = null,
    color1: Int,
    color2: Int,
): Bitmap {
    val hints = hashMapOf<EncodeHintType, Int>().also {
        it[EncodeHintType.MARGIN] = 1
    } // Make the QR code buffer border narrower
    val code = Encoder.encode(this, ErrorCorrectionLevel.H, hints)
    val input = code.matrix ?: throw java.lang.IllegalStateException()

    val quietZone = 2

    val inputWidth = input.width
    val inputHeight = input.height
    val qrWidth = inputWidth + quietZone * 2
    val qrHeight = inputHeight + quietZone * 2
    val outputWidth = dimension.coerceAtLeast(qrWidth)
    val outputHeight = dimension.coerceAtLeast(qrHeight)
    val multiple = (outputWidth / qrWidth).coerceAtMost(outputHeight / qrHeight)
    val leftPadding = (outputWidth - inputWidth * multiple) / 2
    val topPadding = (outputHeight - inputHeight * multiple) / 2
    val FINDER_PATTERN_SIZE = 7
    val CIRCLE_SCALE_DOWN_FACTOR = 16f / 30f //Gotta work on this range!
//    val CIRCLE_SCALE_DOWN_FACTOR = 21f / 30f
    val circleSize = (multiple * CIRCLE_SCALE_DOWN_FACTOR).toInt()
//    val circleSize = (multiple * CIRCLE_SCALE_DOWN_FACTOR).toInt()

    val bitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = color2
    paint.isAntiAlias = true
    canvas.drawRect(0f, 0f, dimension.toFloat(), dimension.toFloat(), paint)
    paint.color = color1
    var inputY = 0
    var outputY = topPadding
    while (inputY < inputHeight) {
        var inputX = 0
        var outputX = leftPadding
        while (inputX < inputWidth) {
            if (input[inputX, inputY].toInt() == 1) {
                if (!(inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE || inputX >= inputWidth - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE || inputX <= FINDER_PATTERN_SIZE && inputY >= inputHeight - FINDER_PATTERN_SIZE)) {
                    canvas.drawCircle(
                        outputX.toFloat(),
                        outputY.toFloat(),
                        circleSize.toFloat(),
                        paint
                    )
                }
            }
            inputX++
            outputX += multiple
        }
        inputY++
        outputY += multiple
    }

    val circleDiameter = multiple * FINDER_PATTERN_SIZE / 1.8f
    drawFinderPatternCircleStyle1(
        canvas,
        leftPadding,
        topPadding,
        circleDiameter,
        paint,
        color1,
        color2
    )
    drawFinderPatternCircleStyle1(
        canvas,
        leftPadding + circleSize + (inputWidth - FINDER_PATTERN_SIZE) * multiple,
        topPadding,
        circleDiameter,
        paint,
        color1,
        color2
    )
    drawFinderPatternCircleStyle1(
        canvas,
        leftPadding,
        topPadding + circleSize + (inputHeight - FINDER_PATTERN_SIZE) * multiple,
        circleDiameter,
        paint,
        color1,
        color2
    )

    return if (overlayBitmap != null) {
        bitmap.addOverlayToCenter(ImageUtils.getRoundedCroppedBitmap(overlayBitmap)!!)
    } else {
        bitmap
    }
}

private fun drawFinderPatternCircleStyle1(
    canvas: Canvas,
    x: Int,
    y: Int,
    circleDiameter: Float,
    paint: Paint,
    color1: Int,
    color2: Int
) {
    val WHITE_CIRCLE_DIAMETER = circleDiameter * 5 / 7
    val MIDDLE_DOT_DIAMETER = circleDiameter * 3 / 7

    paint.color = color1
    canvas.drawCircle(
        x + circleDiameter / 1.5f,
        y + circleDiameter / 1.5f,
        circleDiameter,
        paint
    )
    paint.color = color2
    canvas.drawCircle(
        x + circleDiameter / 1.5f,
        y + circleDiameter / 1.5f,
        WHITE_CIRCLE_DIAMETER,
        paint
    )
    paint.color = color1
    canvas.drawCircle(
        x + circleDiameter / 1.5f,
        y + circleDiameter / 1.5f,
        MIDDLE_DOT_DIAMETER,
        paint
    )
}

@Throws(WriterException::class)
fun String.encodeAsQrCodeBitmap(
    dimension: Int,
    overlayBitmap: Bitmap? = null,
    color1: Int,
    color2: Int,
): Bitmap? {

    val result: BitMatrix
    try {
        result = MultiFormatWriter().encode(
            this,
            BarcodeFormat.QR_CODE,
            dimension,
            dimension,
            hashMapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H)
        )
    } catch (e: IllegalArgumentException) {
        // Unsupported format
        return null
    }

    val w = result.width
    val h = result.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        val offset = y * w
        for (x in 0 until w) {
            pixels[offset + x] = if (result.get(x, y)) color1 else color2
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, dimension, 0, 0, w, h)

    return if (overlayBitmap != null) {
//        bitmap.addOverlayToCenter(ImageUtils.getRoundedCroppedBitmap(overlayBitmap)!!)
        bitmap.addBitmapOverlay(ImageUtils.getRoundedCroppedBitmap(overlayBitmap)!!)
    } else {
        bitmap
    }
}


private fun Bitmap.addOverlayToCenter(overlayBitmap: Bitmap): Bitmap {
    val bitmap2Width = overlayBitmap.width
    val bitmap2Height = overlayBitmap.height
    val marginLeft = (this.width * 0.5 - bitmap2Width * 0.5).toFloat()
    val marginTop = (this.height * 0.5 - bitmap2Height * 0.5).toFloat()
    val canvas = Canvas(this)
    canvas.drawBitmap(this, Matrix(), null)
    canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null)
    return this
}