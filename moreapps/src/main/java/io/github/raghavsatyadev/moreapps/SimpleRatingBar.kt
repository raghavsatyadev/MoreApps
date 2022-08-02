@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.raghavsatyadev.moreapps

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Paint.Join.ROUND
import android.graphics.Paint.Style.FILL_AND_STROKE
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.PorterDuff.Mode.CLEAR
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.os.Build.VERSION_CODES
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import io.github.raghavsatyadev.moreapps.R.color
import io.github.raghavsatyadev.moreapps.R.styleable
import io.github.raghavsatyadev.moreapps.SimpleRatingBar.Gravity.Left
import io.github.raghavsatyadev.moreapps.SimpleRatingBar.Gravity.Right
import io.github.raghavsatyadev.moreapps.utils.AppLog.loge
import io.github.raghavsatyadev.moreapps.utils.getConColor
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A simple RatingBar for Android.
 */
class SimpleRatingBar : View {
    // Configurable variables
    @ColorInt
    private var borderColor = 0

    @ColorInt
    private var fillColor = 0

    @ColorInt
    private var tempBackgroundColor = 0

    @ColorInt
    private var starBackgroundColor = 0

    @ColorInt
    private var pressedBorderColor = 0

    @ColorInt
    private var pressedFillColor = 0

    @ColorInt
    private var pressedBackgroundColor = 0

    @ColorInt
    private var pressedStarBackgroundColor = 0
    private var numberOfStars = 0
    private var starsSeparation = 0f
    private var desiredStarSize = 0f
    private var maxStarSize = 0f
    private var stepSize = 0f
    private var rating = 0f
    private var isIndicator = false
    private var gravity: Gravity? = null
    private var starBorderWidth = 0f
    private var starCornerRadius = 0f
    private var drawBorderEnabled = false

    // Internal variables
    private var currentStarSize = 0f
    private var defaultStarSize = 0f
    private var paintStarOutline: Paint? = null
    private var paintStarBorder: Paint? = null
    private var paintStarFill: Paint? = null
    private var paintStarBackground: Paint? = null
    private var cornerPathEffect: CornerPathEffect? = null
    private var starPath: Path? = null
    private var ratingAnimator: ValueAnimator? = null
    private var ratingListener: OnRatingBarChangeListener? = null
    private var clickListener: OnClickListener? = null
    private var touchInProgress = false
    private var starVertex: FloatArray = floatArrayOf()
    private var starsDrawingSpace: RectF? = null
    private var starsTouchSpace: RectF? = null
    private var internalCanvas: Canvas? = null
    private var internalBitmap: Bitmap? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        parseAttrs(attrs)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttrs(attrs)
        initView()
    }

    /**
     * Initiates paint objects and default values.
     */
    private fun initView() {
        starPath = Path()
        cornerPathEffect = CornerPathEffect(starCornerRadius)
        paintStarOutline = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paintStarOutline!!.style = FILL_AND_STROKE
        paintStarOutline!!.isAntiAlias = true
        paintStarOutline!!.isDither = true
        paintStarOutline!!.strokeJoin = ROUND
        paintStarOutline!!.strokeCap = Cap.ROUND
        paintStarOutline!!.color = Color.BLACK
        paintStarOutline!!.pathEffect = cornerPathEffect
        paintStarBorder = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paintStarBorder!!.style = STROKE
        paintStarBorder!!.strokeJoin = ROUND
        paintStarBorder!!.strokeCap = Cap.ROUND
        paintStarBorder!!.strokeWidth = starBorderWidth
        paintStarBorder!!.pathEffect = cornerPathEffect
        paintStarBackground = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paintStarBackground!!.style = FILL_AND_STROKE
        paintStarBackground!!.isAntiAlias = true
        paintStarBackground!!.isDither = true
        paintStarBackground!!.strokeJoin = ROUND
        paintStarBackground!!.strokeCap = Cap.ROUND
        paintStarFill = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paintStarFill!!.style = FILL_AND_STROKE
        paintStarFill!!.isAntiAlias = true
        paintStarFill!!.isDither = true
        paintStarFill!!.strokeJoin = ROUND
        paintStarFill!!.strokeCap = Cap.ROUND
        defaultStarSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)
    }

    /**
     * Parses attributes defined in XML.
     */
    private fun parseAttrs(attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, styleable.SimpleRatingBar)
        borderColor = arr.getColor(
            styleable.SimpleRatingBar_srb_borderColor, context.getConColor(
                color.srb_golden_stars
            )
        )
        fillColor = arr.getColor(styleable.SimpleRatingBar_srb_fillColor, borderColor)
        starBackgroundColor =
            arr.getColor(styleable.SimpleRatingBar_srb_starBackgroundColor, Color.TRANSPARENT)
        tempBackgroundColor =
            arr.getColor(styleable.SimpleRatingBar_srb_backgroundColor, Color.TRANSPARENT)
        pressedBorderColor =
            arr.getColor(styleable.SimpleRatingBar_srb_pressedBorderColor, borderColor)
        pressedFillColor = arr.getColor(styleable.SimpleRatingBar_srb_pressedFillColor, fillColor)
        pressedStarBackgroundColor = arr.getColor(
            styleable.SimpleRatingBar_srb_pressedStarBackgroundColor,
            starBackgroundColor
        )
        pressedBackgroundColor =
            arr.getColor(styleable.SimpleRatingBar_srb_pressedBackgroundColor, tempBackgroundColor)
        numberOfStars = arr.getInteger(styleable.SimpleRatingBar_srb_numberOfStars, 5)
        starsSeparation = arr.getDimensionPixelSize(
            styleable.SimpleRatingBar_srb_starsSeparation,
            valueToPixels(4f, Dimension.DP).toInt()
        ).toFloat()
        maxStarSize =
            arr.getDimensionPixelSize(styleable.SimpleRatingBar_srb_maxStarSize, Int.MAX_VALUE)
                .toFloat()
        desiredStarSize =
            arr.getDimensionPixelSize(styleable.SimpleRatingBar_srb_starSize, Int.MAX_VALUE)
                .toFloat()
        stepSize = arr.getFloat(styleable.SimpleRatingBar_srb_stepSize, 0.1f)
        starBorderWidth = arr.getFloat(styleable.SimpleRatingBar_srb_starBorderWidth, 5f)
        starCornerRadius = arr.getFloat(styleable.SimpleRatingBar_srb_starCornerRadius, 6f)
        rating = normalizeRating(arr.getFloat(styleable.SimpleRatingBar_srb_rating, 0f))
        isIndicator = arr.getBoolean(styleable.SimpleRatingBar_srb_isIndicator, false)
        drawBorderEnabled = arr.getBoolean(styleable.SimpleRatingBar_srb_drawBorderEnabled, true)
        gravity = Gravity.fromId(arr.getInt(styleable.SimpleRatingBar_srb_gravity, Left.id))
        arr.recycle()
        validateAttrs()
    }

    /**
     * Validates parsed attributes. It will throw IllegalArgumentException if severe inconsistency is found.
     * Warnings will be logged to LogCat.
     */
    private fun validateAttrs() {
        require(numberOfStars > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for numberOfStars. Found %d, but should be greater than 0",
                numberOfStars
            )
        }
        if (desiredStarSize != Float.MAX_VALUE && maxStarSize != Float.MAX_VALUE && desiredStarSize > maxStarSize) {
            loge(
                true,
                "SimpleRatingBar.java",
                "validateAttrs",
                String.format(
                    Locale.getDefault(),
                    "Initialized with conflicting values: starSize is greater than maxStarSize (%f > %f). I will ignore maxStarSize",
                    desiredStarSize,
                    maxStarSize
                ),
                Exception()
            )
        }
        require(stepSize > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for stepSize. Found %f, but should be greater than 0",
                stepSize
            )
        }
        require(starBorderWidth > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for starBorderWidth. Found %f, but should be greater than 0",
                starBorderWidth
            )
        }
        require(starCornerRadius >= 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for starCornerRadius. Found %f, but should be greater or equal than 0",
                starBorderWidth
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
        val width = measureWidth(widthMode, widthSize)

        val tentativeStarSize =
            width - paddingLeft - paddingRight - starsSeparation * (numberOfStars - 1) / numberOfStars

        //Measure Height
        val height = measureHeight(heightMode, heightSize, tentativeStarSize)

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    private fun measureHeight(
        heightMode: Int,
        heightSize: Int,
        tentativeStarSize: Float,
    ) = if (heightMode == MeasureSpec.EXACTLY) {
        //Must be this size
        heightSize
    } else if (heightMode == MeasureSpec.AT_MOST) {
        //Can't be bigger than...
        if (desiredStarSize != Float.MAX_VALUE) {
            // user specified a specific star size, so there is a desired width
            val desiredHeight = calculateTotalHeight(desiredStarSize, true)
            min(desiredHeight, heightSize)
        } else if (maxStarSize != Float.MAX_VALUE) {
            // user specified a max star size, so there is a desired width
            val desiredHeight = calculateTotalHeight(maxStarSize, true)
            min(desiredHeight, heightSize)
        } else {
            // using defaults
            val desiredHeight = calculateTotalHeight(tentativeStarSize, true)
            min(desiredHeight, heightSize)
        }
    } else {
        //Be whatever you want
        if (desiredStarSize != Float.MAX_VALUE) {
            // user specified a specific star size, so there is a desired width
            calculateTotalHeight(desiredStarSize, true)
        } else if (maxStarSize != Float.MAX_VALUE) {
            // user specified a max star size, so there is a desired width
            calculateTotalHeight(maxStarSize, true)
        } else {
            // using defaults
            calculateTotalHeight(tentativeStarSize, true)
        }
    }

    private fun measureWidth(widthMode: Int, widthSize: Int) =
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            if (desiredStarSize != Float.MAX_VALUE) {
                // user specified a specific star size, so there is a desired width
                val desiredWidth =
                    calculateTotalWidth(desiredStarSize, numberOfStars, starsSeparation, true)
                min(desiredWidth, widthSize)
            } else if (maxStarSize != Float.MAX_VALUE) {
                // user specified a max star size, so there is a desired width
                val desiredWidth =
                    calculateTotalWidth(maxStarSize, numberOfStars, starsSeparation, true)
                min(desiredWidth, widthSize)
            } else {
                // using defaults
                val desiredWidth =
                    calculateTotalWidth(defaultStarSize, numberOfStars, starsSeparation, true)
                min(desiredWidth, widthSize)
            }
        } else {
            //Be whatever you want
            if (desiredStarSize != Float.MAX_VALUE) {
                // user specified a specific star size, so there is a desired width
                calculateTotalWidth(desiredStarSize, numberOfStars, starsSeparation, true)
            } else if (maxStarSize != Float.MAX_VALUE) {
                // user specified a max star size, so there is a desired width
                calculateTotalWidth(maxStarSize, numberOfStars, starsSeparation, true)
            } else {
                // using defaults
                calculateTotalWidth(defaultStarSize, numberOfStars, starsSeparation, true)
            }
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = width
        val height = height
        currentStarSize = if (desiredStarSize == Float.MAX_VALUE) {
            calculateBestStarSize(width, height)
        } else {
            desiredStarSize
        }
        performStarSizeAssociatedCalculations(width, height)
    }

    /**
     * Calculates largest possible star size, based on chosen width and height.
     * If maxStarSize is present, it will be considered and star size will not be greater than this value.
     */
    private fun calculateBestStarSize(width: Int, height: Int): Float {
        return if (maxStarSize != Float.MAX_VALUE) {
            val desiredTotalWidth =
                calculateTotalWidth(maxStarSize, numberOfStars, starsSeparation, true).toFloat()
            val desiredTotalHeight = calculateTotalHeight(maxStarSize, true).toFloat()
            if (desiredTotalWidth >= width || desiredTotalHeight >= height) {
                // we need to shrink the size of the stars
                val sizeBasedOnWidth =
                    width - paddingLeft - paddingRight - starsSeparation * (numberOfStars - 1) / numberOfStars
                val sizeBasedOnHeight = (height - paddingTop - paddingBottom).toFloat()
                min(sizeBasedOnWidth, sizeBasedOnHeight)
            } else {
                maxStarSize
            }
        } else {
            // expand the most we can
            val sizeBasedOnWidth =
                width - paddingLeft - paddingRight - starsSeparation * (numberOfStars - 1) / numberOfStars
            val sizeBasedOnHeight = (height - paddingTop - paddingBottom).toFloat()
            min(sizeBasedOnWidth, sizeBasedOnHeight)
        }
    }

    /**
     * Performs auxiliary calculations to later speed up drawing phase.
     */
    private fun performStarSizeAssociatedCalculations(width: Int, height: Int) {
        val totalStarsWidth =
            calculateTotalWidth(currentStarSize, numberOfStars, starsSeparation, false).toFloat()
        val totalStarsHeight = calculateTotalHeight(currentStarSize, false).toFloat()
        val startingX =
            (width - paddingLeft - paddingRight).toFloat() / 2 - totalStarsWidth / 2 + paddingLeft
        val startingY =
            (height - paddingTop - paddingBottom).toFloat() / 2 - totalStarsHeight / 2 + paddingTop
        starsDrawingSpace =
            RectF(startingX, startingY, startingX + totalStarsWidth, startingY + totalStarsHeight)
        val aux = starsDrawingSpace!!.width() * 0.05f
        starsTouchSpace = RectF(
            starsDrawingSpace!!.left - aux,
            starsDrawingSpace!!.top,
            starsDrawingSpace!!.right + aux,
            starsDrawingSpace!!.bottom
        )
        val bottomFromMargin = currentStarSize * 0.2f
        val triangleSide = currentStarSize * 0.35f
        val half = currentStarSize * 0.5f
        val tipVerticalMargin = currentStarSize * 0.05f
        val tipHorizontalMargin = currentStarSize * 0.03f
        val innerUpHorizontalMargin = currentStarSize * 0.38f
        val innerBottomHorizontalMargin = currentStarSize * 0.32f
        val innerBottomVerticalMargin = currentStarSize * 0.6f
        val innerCenterVerticalMargin = currentStarSize * 0.27f
        starVertex = floatArrayOf(
            tipHorizontalMargin,
            innerUpHorizontalMargin,  // top left
            tipHorizontalMargin + triangleSide,
            innerUpHorizontalMargin,
            half,
            tipVerticalMargin,  // top tip
            currentStarSize - tipHorizontalMargin - triangleSide,
            innerUpHorizontalMargin,
            currentStarSize - tipHorizontalMargin,
            innerUpHorizontalMargin,  // top right
            currentStarSize - innerBottomHorizontalMargin,
            innerBottomVerticalMargin,
            currentStarSize - bottomFromMargin,
            currentStarSize - tipVerticalMargin,  // bottom right
            half,
            currentStarSize - innerCenterVerticalMargin,
            bottomFromMargin,
            currentStarSize - tipVerticalMargin,  // bottom left
            innerBottomHorizontalMargin,
            innerBottomVerticalMargin
        )
    }

    /**
     * Calculates total width to occupy based on several parameters
     */
    private fun calculateTotalWidth(
        starSize: Float,
        numberOfStars: Int,
        starsSeparation: Float,
        padding: Boolean,
    ): Int {
        return ((starSize * numberOfStars + starsSeparation * (numberOfStars - 1)).roundToInt()
                + if (padding) paddingLeft + paddingRight else 0)
    }

    /**
     * Calculates total height to occupy based on several parameters
     */
    private fun calculateTotalHeight(starSize: Float, padding: Boolean): Int {
        return starSize.roundToInt() + if (padding) paddingTop + paddingBottom else 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generateInternalCanvas(w, h)
    }

    /**
     * Generates internal canvas on which the ratingbar will be drawn.
     */
    private fun generateInternalCanvas(w: Int, h: Int) {
        if (internalBitmap != null) {
            // avoid leaking memory after losing the reference
            internalBitmap!!.recycle()
        }
        if (w > 0 && h > 0) {
            // if width == 0 or height == 0 we don't need internal bitmap, cause view won't be drawn anyway.
            internalBitmap = Bitmap.createBitmap(w, h, ARGB_8888)
            internalBitmap?.eraseColor(Color.TRANSPARENT)
            internalBitmap?.let {
                internalCanvas = Canvas(it)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = height
        val width = width
        if (width == 0 || height == 0) {
            // don't draw view with width or height equal zero.
            return
        }

        // clean internal canvas
        internalCanvas!!.drawColor(0, CLEAR)

        // choose colors
        setupColorsInPaint()

        // draw stars
        if (gravity == Left) {
            drawFromLeftToRight(internalCanvas)
        } else {
            drawFromRightToLeft(internalCanvas)
        }

        // draw view background color
        if (touchInProgress) {
            canvas.drawColor(pressedBackgroundColor)
        } else {
            canvas.drawColor(tempBackgroundColor)
        }

        // draw internal bitmap to definite canvas
        canvas.drawBitmap(internalBitmap!!, 0f, 0f, null)
    }

    /**
     * Sets the color for the different paints depending on whether current state is pressed or normal.
     */
    private fun setupColorsInPaint() {
        if (touchInProgress) {
            paintStarBorder!!.color = pressedBorderColor
            paintStarFill!!.color = pressedFillColor
            if (pressedFillColor != Color.TRANSPARENT) {
                paintStarFill!!.xfermode = PorterDuffXfermode(SRC_ATOP)
            } else {
                paintStarFill!!.xfermode = PorterDuffXfermode(CLEAR)
            }
            paintStarBackground!!.color = pressedStarBackgroundColor
            if (pressedStarBackgroundColor != Color.TRANSPARENT) {
                paintStarBackground!!.xfermode = PorterDuffXfermode(SRC_ATOP)
            } else {
                paintStarBackground!!.xfermode = PorterDuffXfermode(CLEAR)
            }
        } else {
            paintStarBorder!!.color = borderColor
            paintStarFill!!.color = fillColor
            if (fillColor != Color.TRANSPARENT) {
                paintStarFill!!.xfermode = PorterDuffXfermode(SRC_ATOP)
            } else {
                paintStarFill!!.xfermode = PorterDuffXfermode(CLEAR)
            }
            paintStarBackground!!.color = starBackgroundColor
            if (starBackgroundColor != Color.TRANSPARENT) {
                paintStarBackground!!.xfermode = PorterDuffXfermode(SRC_ATOP)
            } else {
                paintStarBackground!!.xfermode = PorterDuffXfermode(CLEAR)
            }
        }
    }

    /**
     * Draws the view when gravity is Left
     */
    private fun drawFromLeftToRight(internalCanvas: Canvas?) {
        var remainingTotalRating = rating
        var startingX = starsDrawingSpace!!.left
        val startingY = starsDrawingSpace!!.top
        for (i in 0 until numberOfStars) {
            if (remainingTotalRating >= 1) {
                drawStar(internalCanvas, startingX, startingY, 1f, Left)
                remainingTotalRating -= 1f
            } else {
                drawStar(internalCanvas, startingX, startingY, remainingTotalRating, Left)
                remainingTotalRating = 0f
            }
            startingX += starsSeparation + currentStarSize
        }
    }

    /**
     * Draws the view when gravity is Right
     */
    private fun drawFromRightToLeft(internalCanvas: Canvas?) {
        var remainingTotalRating = rating
        var startingX = starsDrawingSpace!!.right - currentStarSize
        val startingY = starsDrawingSpace!!.top
        for (i in 0 until numberOfStars) {
            if (remainingTotalRating >= 1) {
                drawStar(internalCanvas, startingX, startingY, 1f, Right)
                remainingTotalRating -= 1f
            } else {
                drawStar(internalCanvas, startingX, startingY, remainingTotalRating, Right)
                remainingTotalRating = 0f
            }
            startingX -= starsSeparation + currentStarSize
        }
    }

    /**
     * Draws a star in the provided canvas.
     *
     * @param x       left of the star
     * @param y       top of the star
     * @param filled  between 0 and 1
     * @param gravity Left or Right
     */
    private fun drawStar(canvas: Canvas?, x: Float, y: Float, filled: Float, gravity: Gravity) {
        // calculate fill in pixels
        val fill = currentStarSize * filled

        // prepare path for star
        starPath!!.reset()
        starPath!!.moveTo(x + starVertex[0], y + starVertex[1])
        var i = 2
        while (i < starVertex.size) {
            starPath!!.lineTo(x + starVertex[i], y + starVertex[i + 1])
            i += 2
        }
        starPath!!.close()

        // draw star outline
        canvas!!.drawPath(starPath!!, paintStarOutline!!)

        // Note: below, currentStarSize*0.02f is a minor correction so the user won't see a vertical black line in between the fill and empty color
        if (gravity == Left) {
            // color star fill
            canvas.drawRect(
                x,
                y,
                x + fill + currentStarSize * 0.02f,
                y + currentStarSize,
                paintStarFill!!
            )
            // draw star background
            canvas.drawRect(
                x + fill,
                y,
                x + currentStarSize,
                y + currentStarSize,
                paintStarBackground!!
            )
        } else {
            // color star fill
            canvas.drawRect(
                x + currentStarSize - (fill + currentStarSize * 0.02f),
                y,
                x + currentStarSize,
                y + currentStarSize,
                paintStarFill!!
            )
            // draw star background
            canvas.drawRect(
                x,
                y,
                x + currentStarSize - fill,
                y + currentStarSize,
                paintStarBackground!!
            )
        }

        // draw star border on top
        if (drawBorderEnabled) {
            canvas.drawPath(starPath!!, paintStarBorder!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (shouldAllowTouch()) {
            return false
        }
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->                 // check if action is performed on stars
                if (starsTouchSpace!!.contains(event.x, event.y)) {
                    touchInProgress = true
                    setNewRatingFromTouch(event.x)
                } else {
                    if (touchInProgress && ratingListener != null) {
                        ratingListener!!.onRatingChanged(this, rating, true)
                    }
                    touchInProgress = false
                    return false
                }
            MotionEvent.ACTION_UP -> {
                setNewRatingFromTouch(event.x)
                if (clickListener != null) {
                    clickListener!!.onClick(this)
                }
                if (ratingListener != null) {
                    ratingListener!!.onRatingChanged(this, rating, true)
                }
                touchInProgress = false
            }
            MotionEvent.ACTION_CANCEL -> {
                if (ratingListener != null) {
                    ratingListener!!.onRatingChanged(this, rating, true)
                }
                touchInProgress = false
            }
        }
        invalidate()
        return true
    }

    private fun shouldAllowTouch() =
        isIndicator || ratingAnimator != null && ratingAnimator!!.isRunning

    /**
     * Assigns a rating to the touch event.
     */
    private fun setNewRatingFromTouch(oldX: Float) {
        // normalize x to inside starsDrawingSpace
        var x = oldX
        if (gravity != Left) {
            x = width - x
        }

        // we know that touch was inside starsTouchSpace, but it might be outside starsDrawingSpace
        if (x < starsDrawingSpace!!.left) {
            rating = 0f
            return
        } else if (x > starsDrawingSpace!!.right) {
            rating = numberOfStars.toFloat()
            return
        }
        x -= starsDrawingSpace!!.left
        // reduce the width to allow the user reach the top and bottom values of rating (0 and numberOfStars)
        rating = numberOfStars / starsDrawingSpace!!.width() * x

        // correct rating in case step size is present
        val mod = rating % stepSize
        if (mod < stepSize / 4) {
            rating -= mod
            rating = max(0f, rating)
        } else {
            rating = rating - mod + stepSize
            rating = min(numberOfStars.toFloat(), rating)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.rating = getRating()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        setRating(savedState.rating)
    }

    fun getRating(): Float {
        return rating
    }

    /**
     * Sets rating.
     * If provided value is less than 0, rating will be set to 0.
     * * If provided value is greater than numberOfStars, rating will be set to numberOfStars.
     */
    fun setRating(rating: Float) {
        this.rating = normalizeRating(rating)
        // request redraw of the view
        invalidate()
        if (ratingListener != null && (ratingAnimator == null || !ratingAnimator!!.isRunning)) {
            ratingListener!!.onRatingChanged(this, rating, false)
        }
    }

    /* ----------- GETTERS AND SETTERS ----------- */
    fun getStepSize(): Float {
        return stepSize
    }

    /**
     * Sets step size of rating.
     * Throws IllegalArgumentException if provided value is less or equal than zero.
     */
    fun setStepSize(stepSize: Float) {
        this.stepSize = stepSize
        require(stepSize > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for stepSize. Found %f, but should be greater than 0",
                stepSize
            )
        }
        // request redraw of the view
        invalidate()
    }

    fun isIndicator(): Boolean {
        return isIndicator
    }

    /**
     * Sets indicator property.
     * If provided value is true, touch events will be deactivated, and thus user interaction will be deactivated.
     */
    fun setIndicator(indicator: Boolean) {
        isIndicator = indicator
        touchInProgress = false
    }

    /**
     * Returns max star size in pixels.
     */
    fun getMaxStarSize(): Float {
        return maxStarSize
    }

    /**
     * Sets maximum star size in pixels.
     * If current star size is less than provided value, this has no effect on the view.
     */
    fun setMaxStarSize(maxStarSize: Float) {
        this.maxStarSize = maxStarSize
        if (currentStarSize > maxStarSize) {
            // force re-calculating the layout dimension
            requestLayout()
            generateInternalCanvas(width, height)
            // request redraw of the view
            invalidate()
        }
    }

    /**
     * Returns max star size in the requested dimension.
     */
    fun getMaxStarSize(
        @Dimension
        dimen: Int,
    ): Float {
        return valueFromPixels(maxStarSize, dimen)
    }

    /**
     * Sets maximum star size using the given dimension.
     * If current star size is less than provided value, this has no effect on the view.
     */
    fun setMaxStarSize(
        maxStarSize: Float,
        @Dimension
        dimen: Int,
    ) {
        setMaxStarSize(valueToPixels(maxStarSize, dimen))
    }
    /**
     * Return star size in pixels.
     */// force re-calculating the layout dimension
    // request redraw of the view
    /**
     * Sets exact star size in pixels.
     */
    var starSize: Float
        get() = currentStarSize
        set(starSize) {
            desiredStarSize = starSize
            if (starSize != Float.MAX_VALUE && maxStarSize != Float.MAX_VALUE && starSize > maxStarSize) {
                loge(
                    true,
                    kotlinFileName,
                    "starSize",
                    "Initialized with conflicting values: starSize is greater than maxStarSize ($starSize > $maxStarSize). I will ignore maxStarSize",
                    Exception()
                )
            }
            // force re-calculating the layout dimension
            requestLayout()
            generateInternalCanvas(width, height)
            // request redraw of the view
            invalidate()
        }

    /**
     * Return star size in the requested dimension.
     */
    fun getStarSize(
        @Dimension
        dimen: Int,
    ): Float {
        return valueFromPixels(currentStarSize, dimen)
    }

    /**
     * Sets exact star size using the given dimension.
     */
    fun setStarSize(
        starSize: Float,
        @Dimension
        dimen: Int,
    ) {
        this.starSize = valueToPixels(starSize, dimen)
    }

    /**
     * Returns stars separation in pixels.
     */
    fun getStarsSeparation(): Float {
        return starsSeparation
    }

    /**
     * Sets separation between stars in pixels.
     */
    fun setStarsSeparation(starsSeparation: Float) {
        this.starsSeparation = starsSeparation
        // force re-calculating the layout dimension
        requestLayout()
        generateInternalCanvas(width, height)
        // request redraw of the view
        invalidate()
    }

    /**
     * Returns stars separation in the requested dimension.
     */
    fun getStarsSeparation(
        @Dimension
        dimen: Int,
    ): Float {
        return valueFromPixels(starsSeparation, dimen)
    }

    /**
     * Sets separation between stars using the given dimension.
     */
    fun setStarsSeparation(
        starsSeparation: Float,
        @Dimension
        dimen: Int,
    ) {
        setStarsSeparation(valueToPixels(starsSeparation, dimen))
    }

    fun getNumberOfStars(): Int {
        return numberOfStars
    }

    /**
     * Sets number of stars.
     * It also sets the rating to zero.
     * Throws IllegalArgumentException if provided value is less or equal than zero.
     */
    fun setNumberOfStars(numberOfStars: Int) {
        this.numberOfStars = numberOfStars
        require(numberOfStars > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for numberOfStars. Found %d, but should be greater than 0",
                numberOfStars
            )
        }
        rating = 0f
        // force re-calculating the layout dimension
        requestLayout()
        generateInternalCanvas(width, height)
        // request redraw of the view
        invalidate()
    }

    /**
     * Returns star border width in pixels.
     */
    fun getStarBorderWidth(): Float {
        return starBorderWidth
    }

    /**
     * Sets border width of stars in pixels.
     * Throws IllegalArgumentException if provided value is less or equal than zero.
     */
    fun setStarBorderWidth(starBorderWidth: Float) {
        this.starBorderWidth = starBorderWidth
        require(starBorderWidth > 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for starBorderWidth. Found %f, but should be greater than 0",
                starBorderWidth
            )
        }
        paintStarBorder!!.strokeWidth = starBorderWidth
        // request redraw of the view
        invalidate()
    }

    /**
     * Returns star border width in the requested dimension.
     */
    fun getStarBorderWidth(
        @Dimension
        dimen: Int,
    ): Float {
        return valueFromPixels(starBorderWidth, dimen)
    }

    /**
     * Sets border width of stars using the given dimension.
     * Throws IllegalArgumentException if provided value is less or equal than zero.
     */
    fun setStarBorderWidth(
        starBorderWidth: Float,
        @Dimension
        dimen: Int,
    ) {
        setStarBorderWidth(valueToPixels(starBorderWidth, dimen))
    }

    /**
     * Returns start corner radius in pixels,
     */
    fun getStarCornerRadius(): Float {
        return starCornerRadius
    }

    /**
     * Sets radius of star corner in pixels.
     * Throws IllegalArgumentException if provided value is less than zero.
     */
    fun setStarCornerRadius(starCornerRadius: Float) {
        this.starCornerRadius = starCornerRadius
        require(starCornerRadius >= 0) {
            String.format(
                "SimpleRatingBar initialized with invalid value for starCornerRadius. Found %f, but should be greater or equal than 0",
                starCornerRadius
            )
        }
        cornerPathEffect = CornerPathEffect(starCornerRadius)
        paintStarBorder!!.pathEffect = cornerPathEffect
        paintStarOutline!!.pathEffect = cornerPathEffect
        // request redraw of the view
        invalidate()
    }

    /**
     * Returns start corner radius in the requested dimension,
     */
    fun getStarCornerRadius(
        @Dimension
        dimen: Int,
    ): Float {
        return valueFromPixels(starCornerRadius, dimen)
    }

    /**
     * Sets radius of star corner using the given dimension.
     * Throws IllegalArgumentException if provided value is less than zero.
     */
    fun setStarCornerRadius(
        starCornerRadius: Float,
        @Dimension
        dimen: Int,
    ) {
        setStarCornerRadius(valueToPixels(starCornerRadius, dimen))
    }

    @ColorInt
    fun getBorderColor(): Int {
        return borderColor
    }

    /**
     * Sets border color of stars in normal state.
     */
    fun setBorderColor(
        @ColorInt
        borderColor: Int,
    ) {
        this.borderColor = borderColor
        // request redraw of the view
        invalidate()
    }

    @ColorInt
    fun getFillColor(): Int {
        return fillColor
    }

    /**
     * Sets fill color of stars in normal state.
     */
    fun setFillColor(
        @ColorInt
        fillColor: Int,
    ) {
        this.fillColor = fillColor
        // request redraw of the view
        invalidate()
    }

    @ColorInt
    fun getStarBackgroundColor(): Int {
        return starBackgroundColor
    }

    /**
     * Sets background color of stars in normal state.
     */
    fun setStarBackgroundColor(
        @ColorInt
        starBackgroundColor: Int,
    ) {
        this.starBackgroundColor = starBackgroundColor
        // request redraw of the view
        invalidate()
    }

    @ColorInt
    fun getPressedBorderColor(): Int {
        return pressedBorderColor
    }

    /**
     * Sets border color of stars in pressed state.
     */
    fun setPressedBorderColor(
        @ColorInt
        pressedBorderColor: Int,
    ) {
        this.pressedBorderColor = pressedBorderColor
        // request redraw of the view
        invalidate()
    }

    @ColorInt
    fun getPressedFillColor(): Int {
        return pressedFillColor
    }

    /**
     * Sets fill color of stars in pressed state.
     */
    fun setPressedFillColor(
        @ColorInt
        pressedFillColor: Int,
    ) {
        this.pressedFillColor = pressedFillColor
        // request redraw of the view
        invalidate()
    }

    @ColorInt
    fun getPressedStarBackgroundColor(): Int {
        return pressedStarBackgroundColor
    }

    /**
     * Sets background color of stars in pressed state.
     */
    fun setPressedStarBackgroundColor(
        @ColorInt
        pressedStarBackgroundColor: Int,
    ) {
        this.pressedStarBackgroundColor = pressedStarBackgroundColor
        // request redraw of the view
        invalidate()
    }

    fun getGravity(): Gravity? {
        return gravity
    }

    /**
     * Sets gravity of fill.
     */
    fun setGravity(gravity: Gravity?) {
        this.gravity = gravity
        // request redraw of the view
        invalidate()
    }

    fun isDrawBorderEnabled(): Boolean {
        return drawBorderEnabled
    }

    /**
     * Sets drawBorder property.
     * If provided value is true, border will be drawn, otherwise it will be omitted.
     */
    fun setDrawBorderEnabled(drawBorderEnabled: Boolean) {
        this.drawBorderEnabled = drawBorderEnabled
        // request redraw of the view
        invalidate()
    }

    /**
     * Convenience method to convert a value in the given dimension to pixels.
     */
    private fun valueToPixels(
        value: Float,
        @Dimension
        dimen: Int,
    ): Float {
        return when (dimen) {
            Dimension.DP -> TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                resources.displayMetrics
            )
            Dimension.SP -> TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                value,
                resources.displayMetrics
            )
            else -> value
        }
    }

    /**
     * Convenience method to convert a value from pixels to the given dimension.
     */
    private fun valueFromPixels(
        value: Float,
        @Dimension
        dimen: Int,
    ): Float {
        return when (dimen) {
            Dimension.DP -> value / resources.displayMetrics.density
            Dimension.SP -> value / resources.displayMetrics.scaledDensity
            else -> value
        }
    }

    /**
     * Sets rating with animation.
     */
    private fun animateRating(builder: AnimationBuilder) {
        builder.ratingTarget = normalizeRating(builder.ratingTarget)
        ratingAnimator = ValueAnimator.ofFloat(0f, builder.ratingTarget)
        ratingAnimator?.duration = builder.duration
        ratingAnimator?.repeatCount = builder.repeatCount
        ratingAnimator?.repeatMode = builder.repeatMode

        // Callback that executes on animation steps.
        ratingAnimator?.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            setRating(value)
        }
        if (builder.interpolator != null) {
            ratingAnimator?.interpolator = builder.interpolator
        }
        if (builder.animatorListener != null) {
            ratingAnimator?.addListener(builder.animatorListener)
        }
        ratingAnimator?.addListener(object : AnimatorListener {
            override fun onAnimationStart(animator: Animator) = Unit
            override fun onAnimationEnd(animator: Animator) {
                if (ratingListener != null) {
                    ratingListener!!.onRatingChanged(this@SimpleRatingBar, rating, false)
                }
            }

            override fun onAnimationCancel(animator: Animator) {
                if (ratingListener != null) {
                    ratingListener!!.onRatingChanged(this@SimpleRatingBar, rating, false)
                }
            }

            override fun onAnimationRepeat(animator: Animator) {
                if (ratingListener != null) {
                    ratingListener!!.onRatingChanged(this@SimpleRatingBar, rating, false)
                }
            }
        })
        ratingAnimator?.start()
    }

    /**
     * Returns a new AnimationBuilder.
     */
    val animationBuilder: AnimationBuilder
        get() = AnimationBuilder.getInstance(this)

    /**
     * Normalizes rating passed by argument between 0 and numberOfStars.
     */
    private fun normalizeRating(rating: Float): Float {
        return if (rating < 0) {
            loge(
                true,
                kotlinFileName,
                "normalizeRating",
                "Assigned rating is less than 0 ($rating < 0), I will set it to exactly 0",
                Exception()
            )
            0f
        } else if (rating > numberOfStars) {
            loge(
                true,
                kotlinFileName,
                "normalizeRating",
                "Assigned rating is greater than numberOfStars ($rating > $numberOfStars), I will set it to exactly numberOfStars",
                Exception()
            )
            numberOfStars.toFloat()
        } else {
            rating
        }
    }

    /**
     * Sets OnClickListener.
     */
    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }

    /**
     * Sets OnRatingBarChangeListener.
     */
    fun setOnRatingBarChangeListener(listener: OnRatingBarChangeListener?) {
        ratingListener = listener
    }

    /**
     * Represents gravity of the fill in the bar.
     */
    enum class Gravity(val id: Int) {
        /**
         * Left gravity is default: the bar will be filled starting from left to right.
         */
        Left(0),

        /**
         * Right gravity: the bar will be filled starting from right to left.
         */
        Right(1);

        companion object {
            fun fromId(id: Int): Gravity {
                for (f in values()) {
                    if (f.id == id) return f
                }
                // default value
                loge(
                    true,
                    kotlinFileName,
                    "fromId",
                    "Gravity chosen is neither 'left' nor 'right', I will set it to Left",
                    Exception()
                )
                return Left
            }
        }
    }

    interface OnRatingBarChangeListener {
        /**
         * Notification that the rating has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically. This will not be called continuously
         * while the user is dragging, only when the user finalizes a rating by
         * lifting the touch.
         *
         * @param simpleRatingBar The RatingBar whose rating has changed.
         * @param rating          The current rating. This will be in the range
         * 0..numStars.
         * @param fromUser        True if the rating change was initiated by a user's
         * touch gesture or arrow key/horizontal trackball movement.
         */
        fun onRatingChanged(simpleRatingBar: SimpleRatingBar?, rating: Float, fromUser: Boolean)
    }

    private open class SavedState : BaseSavedState {
        var rating = 0.0f

        constructor(source: Parcel) : super(source) {
            rating = source.readFloat()
        }

        @TargetApi(VERSION_CODES.N)
        protected constructor(source: Parcel?, loader: ClassLoader?) : super(source, loader)

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(rating)
        }

        companion object {
            @JvmField
            val CREATOR: Creator<SavedState> = object : Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    /**
     * Helper class to build rating animation.
     * Provides good defaults:
     * - Target rating: numberOfStars
     * - Animation: Bounce
     * - Duration: 2s
     */
    class AnimationBuilder private constructor(private val ratingBar: SimpleRatingBar) {
        var duration: Long = 2000
        var interpolator: Interpolator?
        var ratingTarget: Float
        var repeatCount: Int
        var repeatMode: Int
        var animatorListener: AnimatorListener? = null

        init {
            interpolator = BounceInterpolator()
            ratingTarget = ratingBar.getNumberOfStars().toFloat()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }

        /**
         * Sets duration of animation.
         */
        fun setDuration(duration: Long): AnimationBuilder {
            this.duration = duration
            return this
        }

        /**
         * Sets interpolator for animation.
         */
        fun setInterpolator(interpolator: Interpolator?): AnimationBuilder {
            this.interpolator = interpolator
            return this
        }

        /**
         * Sets rating after animation has ended.
         */
        fun setRatingTarget(ratingTarget: Float): AnimationBuilder {
            this.ratingTarget = ratingTarget
            return this
        }

        /**
         * Sets repeat count for animation.
         *
         * @param repeatCount must be a positive value or ValueAnimator.INFINITE
         */
        fun setRepeatCount(repeatCount: Int): AnimationBuilder {
            this.repeatCount = repeatCount
            return this
        }

        /**
         * Sets repeat mode for animation.
         *
         * @param repeatMode must be ValueAnimator.RESTART or ValueAnimator.REVERSE
         */
        fun setRepeatMode(repeatMode: Int): AnimationBuilder {
            this.repeatMode = repeatMode
            return this
        }

        /**
         * Sets AnimatorListener.
         */
        fun setAnimatorListener(animatorListener: AnimatorListener?): AnimationBuilder {
            this.animatorListener = animatorListener
            return this
        }

        /**
         * Starts animation.
         */
        fun start() {
            ratingBar.animateRating(this)
        }

        companion object {
            fun getInstance(simpleRatingBar: SimpleRatingBar) =
                AnimationBuilder(simpleRatingBar)
        }
    }
}