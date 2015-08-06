package widget;

import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by dupengtao on 14-9-4.
 */
public class LockPatternHelper {

    public static final int DISAPPEAR_TIME = 250;
    private final LockPatternRing[][] mLockPatternRings;
    private final LockPatternLine mLockPatternLine;
    private final LockPatternView mLockPatternView;
    private Runnable mResetCallback;
    private boolean mIsReset;
    public boolean isLineError;
    private int mErrorCellSize;
    private ArrayList<LockMovePoint> mLockMovePoints = new ArrayList<LockMovePoint>();
    private ArrayList<LockPatternView.Cell> mErrorCells = new ArrayList<LockPatternView.Cell>();

    public LockPatternHelper(LockPatternView lockPatternView, LockPatternRing[][] lockPatternRings, LockPatternLine lockPatternLine) {
        mLockPatternRings = lockPatternRings;
        mLockPatternLine = lockPatternLine;
        mLockPatternView = lockPatternView;
    }

    public LockPatternRing[][] getLockPatternRings() {
        return mLockPatternRings;
    }

    public LockPatternLine getLockPatternLine() {
        return mLockPatternLine;
    }

    public void moveAnim(int row, int column) {
        if (mIsReset) {
            resetLockPatternRings();
            mLockPatternView.getHelper().clearAndCancel();
            mIsReset = false;
            isLineError = false;
        }
        mLockPatternRings[row][column].downAnim();
    }

    public void resetLine() {
        mLockPatternLine.resetLine();
        mLockPatternView.invalidate();
    }

    public void resetLockPatternRings() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mLockPatternRings[i][j].resetRing();
            }
        }
    }

    public void reset() {
        resetLine();
        resetLockPatternRings();
    }

    public void drawLine(Canvas canvas, Path path) {
        mLockPatternLine.drawLine(canvas, path);
    }

    private void doLockPatternRingsError(boolean[][] patternDrawLookup) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (patternDrawLookup[i][j]) {
                    mLockPatternRings[i][j].doError();
                }
            }
        }
        mLockPatternView.invalidate();
    }

    private void clearAndCancel() {
        mLockPatternView.clearPattern();
        //mLockPatternView.enableInput();
    }

    private void disableInput() {
        mLockPatternView.disableInput();
    }

    public void patternErrorDetected() {
        if (mResetCallback == null) {
            mResetCallback = new Runnable() {
                @Override
                public void run() {
                    if (mIsReset) {
                        mLockPatternView.getHelper().clearAndCancel();
                        mIsReset = false;
                        // isLineError=false;
                    }
                }
            };
        } else {
            mLockPatternView.removeCallbacks(mResetCallback);
        }
        mIsReset = true;
        mLockPatternView.postDelayed(mResetCallback, LockPatternHelper.DISAPPEAR_TIME * mErrorCellSize);
        isLineError = true;
    }

    public void doError(ArrayList<LockPatternView.Cell> pattern, boolean[][] patternDrawLookup) {
        doLockPatternRingsError(patternDrawLookup);
        getPaths(pattern, patternDrawLookup);

        LockPatternView.Cell cell = mErrorCells.get(0);
        if (cell != null) {
            mLockPatternRings[cell.row][cell.column].resetRing();
        }

        doNewLineError(new LockPatternLine.LineAnimListener() {
            @Override
            public void onErrorLineAnim(float x, float y, int times) {
                try {

                    LockPatternView.Cell cell = mErrorCells.get(times);
                    if (cell != null) {
                        mLockPatternRings[cell.row][cell.column].resetRing();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doNewLineError(LockPatternLine.LineAnimListener listener) {
        mLockMovePoints.remove(mLockMovePoints.size() - 1);
        mLockPatternLine.doNewError(mLockMovePoints, listener);
    }

    public void getPaths(ArrayList<LockPatternView.Cell> mPattern, boolean[][] mPatternDrawLookup) {

        ArrayList<LockPatternView.Cell> pattern = mPattern;
        final int count = pattern.size();
        mLockMovePoints.clear();
        mErrorCells.clear();
        for (int i = 0; i < count; i++) {

            LockPatternView.Cell cell = pattern.get(i);
            // only draw the part of the pattern stored in
            // the lookup table (this is only different in the case
            // of animation).
            if (!mPatternDrawLookup[cell.row][cell.column]) {
                break;
            }
            mErrorCells.add(cell);
            mLockMovePoints.add(new LockMovePoint());
            float centerX = mLockPatternView.getCenterXForColumn(cell.column);
            float centerY = mLockPatternView.getCenterYForRow(cell.row);

            if (i == 0) {
                mLockMovePoints.get(i).setCurX(centerX);
                mLockMovePoints.get(i).setCurY(centerY);
            } else {
                mLockMovePoints.get(i).setCurX(centerX);
                mLockMovePoints.get(i).setCurY(centerY);
                mLockMovePoints.get(i - 1).setMoveX(centerX);
                mLockMovePoints.get(i - 1).setMoveY(centerY);
            }
        }
        mErrorCellSize = mErrorCells.size();
    }

}
