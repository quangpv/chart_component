package com.example.billyphan.chart_components.utils;

import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Billy Phan on 10/17/2017.
 */

public class DrawUtils {
    //region Make Corner Path
    public static List<Path> makeListCornerRectPath(RectF bound, int segmentSize, float cornerSize, boolean borderLeft, boolean borderRight) {
        float viewLeft = bound.left;
        float viewRight = bound.right;
        int numOfBound = 0;
        if ((viewRight - viewLeft) % segmentSize > 0)
            numOfBound = (int) ((viewRight - viewLeft) / segmentSize) + 1;
        else
            numOfBound = (int) ((viewRight - viewLeft) / segmentSize);
        List<Path> paths = new ArrayList<>();

        for (int i = 0; i < numOfBound; i++) {
            float left = viewLeft + i * segmentSize;
            float right = left + segmentSize;
            if (right > bound.right) right = bound.right;
            float top = bound.top;
            float bottom = bound.bottom;
            Path path = null;

            if (numOfBound == 1) {
                if (borderLeft && borderRight)
                    path = makeCornerRectPath(left, right, top, bottom, cornerSize, cornerSize, cornerSize, cornerSize);
                else if (borderLeft)
                    path = makeCornerRectPath(left, right, top, bottom, cornerSize, 0, 0, cornerSize);
                else if (borderRight)
                    path = makeCornerRectPath(left, right, top, bottom, 0, cornerSize, cornerSize, 0);
            } else {
                if (i == 0) {
                    if (borderLeft)
                        path = makeCornerRectPath(left, right, top, bottom, cornerSize, 0, 0, cornerSize);
                } else if (i == numOfBound - 1) {
                    if (borderRight)
                        path = makeCornerRectPath(left, right, top, bottom, 0, cornerSize, cornerSize, 0);
                }
            }

            if (path == null) path = makeCornerRectPath(left, right, top, bottom, 0, 0, 0, 0);
            paths.add(path);
        }
        return paths;
    }

    public static Path makeCornerRectPath(RectF rectF, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        float left = rectF.left;
        float right = rectF.right;
        float top = rectF.top;
        float bottom = rectF.bottom;

        Path path = new Path();
        topLeft = topLeft < 0 ? 0 : topLeft;
        topRight = topRight < 0 ? 0 : topRight;
        bottomLeft = bottomLeft < 0 ? 0 : bottomLeft;
        bottomRight = bottomRight < 0 ? 0 : bottomRight;

        path.moveTo(left + topLeft / 2, top);
        path.lineTo(right - topRight / 2, top);
        path.quadTo(right, top, right, top + topRight / 2);
        path.lineTo(right, bottom - bottomRight / 2);
        path.quadTo(right, bottom, right - bottomRight / 2, bottom);
        path.lineTo(left + bottomLeft / 2, bottom);
        path.quadTo(left, bottom, left, bottom - bottomLeft / 2);
        path.lineTo(left, top + topLeft / 2);
        path.quadTo(left, top, left + topLeft / 2, top);
        path.close();

        return path;
    }

    public static Path makeCornerRectPath(float left, float right, float top, float bottom
            , float topLeft, float topRight, float bottomRight, float bottomLeft) {
        Path path = new Path();
        topLeft = topLeft < 0 ? 0 : topLeft;
        topRight = topRight < 0 ? 0 : topRight;
        bottomLeft = bottomLeft < 0 ? 0 : bottomLeft;
        bottomRight = bottomRight < 0 ? 0 : bottomRight;

        path.moveTo(left + topLeft / 2, top);
        path.lineTo(right - topRight / 2, top);
        path.quadTo(right, top, right, top + topRight / 2);
        path.lineTo(right, bottom - bottomRight / 2);
        path.quadTo(right, bottom, right - bottomRight / 2, bottom);
        path.lineTo(left + bottomLeft / 2, bottom);
        path.quadTo(left, bottom, left, bottom - bottomLeft / 2);
        path.lineTo(left, top + topLeft / 2);
        path.quadTo(left, top, left + topLeft / 2, top);
        path.close();

        return path;
    }
    //endregion
}
