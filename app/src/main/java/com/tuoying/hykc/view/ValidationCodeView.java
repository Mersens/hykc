package com.tuoying.hykc.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.tuoying.hykc.R;

import java.util.ArrayList;
import java.util.Random;
public class ValidationCodeView extends View {
    /**
     * 控件的宽度
     */
    private int mWidth;
    /**
     * 控件的高度
     */
    private int mHeight;
    /**
     * 验证码文本画笔
     */
    private Paint mTextPaint; // 文本画笔
    /**
     * 干扰点坐标的集合
     */
    private ArrayList<PointF> mPoints = new ArrayList<PointF>();
    private Random mRandom = new Random();;
    /**
     * 干扰点画笔
     */
    private Paint mPointPaint;
    /**
     * 绘制贝塞尔曲线的路径集合
     */
    private ArrayList<Path> mPaths = new ArrayList<Path>();
    /**
     * 干扰线画笔
     */
    private Paint mPathPaint;
    /**
     * 验证码字符串
     */
    private String mCodeString;
    /**
     * 验证码的位数
     */
    private int mCodeCount;
    /**
     * 验证码字符的大小
     */
    private float mTextSize;
    /**
     * 验证码字符串的显示宽度
     */
    private float mTextWidth;
    public ValidationCodeView(Context context) {
        this(context,null);
    }

    public ValidationCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ValidationCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrValues(context, attrs);
        init();
    }

    /**
     * java生成随机数字和字母组合
     * @return
     */
    public static String getCharAndNumr(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 获取布局文件中的值
     * @param context
     */
    private void getAttrValues(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndentifyingCode);
        mCodeCount = typedArray.getInteger(R.styleable.IndentifyingCode_codeCount, 4); // 获取布局中验证码位数属性值，默认为5个
        // 获取布局中验证码文字的大小，默认为20sp
        mTextSize = typedArray.getDimension(R.styleable.IndentifyingCode_textSize, typedArray.getDimensionPixelSize(R.styleable.IndentifyingCode_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics())));
        // 一个好的习惯是用完资源要记得回收，就想打开数据库和IO流用完后要记得关闭一样
        typedArray.recycle();
    }

    /**
     * 要像layout_width和layout_height属性支持wrap_content就必须重新这个方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 分别测量控件的宽度和高度，基本为模板方法
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);

        // 其实这个方法最终会调用setMeasuredDimension(int measureWidth,int measureHeight);
        // 将测量出来的宽高设置进去完成测量
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 初始化数据
        initData();

        int length = mCodeString.length();
        float charLength = mTextWidth/length;
        for(int i=1;i<=length;i++){
            int offsetDegree = mRandom.nextInt(15);
            // 这里只会产生0和1，如果是1那么正旋转正角度，否则旋转负角度
            offsetDegree = mRandom.nextInt(2) == 1?offsetDegree:-offsetDegree;
            canvas.save();
            canvas.rotate(offsetDegree, mWidth / 2, mHeight / 2);
            // 给画笔设置随机颜色
            mTextPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawText(String.valueOf(mCodeString.charAt(i - 1)), (i-1) * charLength * 1.6f+30, mHeight * 2 / 3f, mTextPaint);
            canvas.restore();
        }

        // 产生干扰效果1 -- 干扰点
        for(PointF pointF : mPoints){
            mPointPaint.setARGB(255,mRandom.nextInt(200)+20,mRandom.nextInt(200)+20,mRandom.nextInt(200)+20);
            canvas.drawPoint(pointF.x,pointF.y,mPointPaint);
        }

        // 产生干扰效果2 -- 干扰线
        for(Path path : mPaths){
            mPathPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawPath(path, mPathPaint);
        }
    }

    private void initData() {
        // 获取控件的宽和高，此时已经测量完成
        mHeight = getHeight();
        mWidth = getWidth();

        mPoints.clear();
        // 生成干扰点坐标
        for(int i=0;i<40;i++){
            PointF pointF = new PointF(mRandom.nextInt(mWidth)+10,mRandom.nextInt(mHeight)+10);
            mPoints.add(pointF);
        }

        mPaths.clear();
        // 生成干扰线坐标
        for(int i=0;i<1;i++){
            Path path = new Path();
            int startX = mRandom.nextInt(mWidth/3)+10;
            int startY = mRandom.nextInt(mHeight/3)+10;
            int endX = mRandom.nextInt(mWidth/2)+mWidth/2-10;
            int endY = mRandom.nextInt(mHeight/2)+mHeight/2-10;
            path.moveTo(startX,startY);
            path.quadTo(Math.abs(endX-startX)/2,Math.abs(endY-startY)/2,endX,endY);
            mPaths.add(path);
        }
    }

    /**
     * 初始化一些数据
     */
    private void init() {
        // 生成随机数字和字母组合
        mCodeString = getCharAndNumr(mCodeCount);

        // 初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(3); // 画笔大小为3
        mTextPaint.setTextSize(mTextSize); // 设置文字大小

        // 初始化干扰点画笔
        mPointPaint = new Paint();
        mPointPaint.setStrokeWidth(6);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND); // 设置断点处为圆形

        // 初始化干扰线画笔
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(5);
        mPathPaint.setColor(Color.GRAY);
        mPathPaint.setStyle(Paint.Style.STROKE); // 设置画笔为空心
        mPathPaint.setStrokeCap(Paint.Cap.ROUND); // 设置断点处为圆形

        // 取得验证码字符串显示的宽度值
        mTextWidth = mTextPaint.measureText(mCodeString);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 重新生成随机数字和字母组合
                mCodeString = getCharAndNumr(mCodeCount);
                invalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 测量宽度
     * @param widthMeasureSpec
     */
    private int measureWidth(int widthMeasureSpec) {
        int result = (int) (mTextWidth*1.8f);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){
            // 精确测量模式，即布局文件中layout_width或layout_height一般为精确的值或match_parent
            result = widthSize; // 既然是精确模式，那么直接返回测量的宽度即可
        }else{
            if(widthMode == MeasureSpec.AT_MOST) {
                // 最大值模式，即布局文件中layout_width或layout_height一般为wrap_content
                result = Math.min(result,widthSize);
            }
        }
        return result;
    }

    /**
     * 测量高度
     * @param heightMeasureSpec
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = (int) (mTextWidth/1.6f);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){
            // 精确测量模式，即布局文件中layout_width或layout_height一般为精确的值或match_parent
            result = heightSize; // 既然是精确模式，那么直接返回测量的宽度即可
        }else{
            if(heightMode == MeasureSpec.AT_MOST) {
                // 最大值模式，即布局文件中layout_width或layout_height一般为wrap_content
                result = Math.min(result,heightSize);
            }
        }
        return result;
    }

    /**
     * 获取验证码字符串，进行匹配的时候只需要字符串比较即可（具体比较规则自己决定）
     * @return 验证码字符串
     */
    public String getCodeString() {
        return mCodeString;
    }

    public void refreshCodeString(){
        mCodeString = getCharAndNumr(mCodeCount);
        invalidate();
    }

}
