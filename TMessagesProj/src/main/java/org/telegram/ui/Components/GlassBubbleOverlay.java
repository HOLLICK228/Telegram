package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;

/**
 * Draws a translucent "liquid glass" layer on top of a message bubble path.
 * Mode 1 = matte (soft frosted film), mode 2 = glossy (bright highlight and rim).
 */
public class GlassBubbleOverlay {

    private static final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint rimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Matrix matrix = new Matrix();
    private static LinearGradient glossShader;

    static {
        rimPaint.setStyle(Paint.Style.STROKE);
    }

    private static LinearGradient getGlossShader() {
        if (glossShader == null) {
            // unit gradient (0,0) -> (0,1), scaled to the bubble bounds on every draw
            glossShader = new LinearGradient(0, 0, 0, 1,
                    new int[]{0x7AFFFFFF, 0x1FFFFFFF, 0x05FFFFFF, 0x2EFFFFFF},
                    new float[]{0f, 0.45f, 0.55f, 1f},
                    Shader.TileMode.CLAMP);
        }
        return glossShader;
    }

    public static void draw(Canvas canvas, Path path, Rect bounds) {
        int mode = SharedConfig.liquidGlassMode;
        if (mode == 0 || bounds.width() <= 0 || bounds.height() <= 0) {
            return;
        }
        if (mode == 2) {
            LinearGradient shader = getGlossShader();
            matrix.reset();
            matrix.setScale(1f, bounds.height());
            matrix.postTranslate(0, bounds.top);
            shader.setLocalMatrix(matrix);
            fillPaint.setShader(shader);
            canvas.drawPath(path, fillPaint);

            rimPaint.setStrokeWidth(AndroidUtilities.dpf2(1.2f));
            rimPaint.setColor(0x99FFFFFF);
            canvas.drawPath(path, rimPaint);
        } else {
            fillPaint.setShader(null);
            fillPaint.setColor(0x24FFFFFF);
            canvas.drawPath(path, fillPaint);

            rimPaint.setStrokeWidth(AndroidUtilities.dpf2(1f));
            rimPaint.setColor(0x38FFFFFF);
            canvas.drawPath(path, rimPaint);
        }
    }
}
