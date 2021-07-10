package tw.edu.pu.csim.tcyang.testandroid2d

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context?, bundle: Bundle) : SurfaceView(context), SurfaceHolder.Callback {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val screenWidth= Resources.getSystem().displayMetrics.widthPixels  //讀取螢幕寬度
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels  //讀取螢幕高度

    var background1:Background? = null
    var background2:Background? = null
    var backgroundMoveX = 5

    var bk1_x:Int= bundle!!.getInt("背景1x軸")
    var boy_x:Int= bundle!!.getInt("男孩x軸")
    var boy_y:Int= bundle!!.getInt("男孩y軸")
    var virus_x:Int= bundle!!.getInt("病毒x軸")
    var virus_y:Int= bundle!!.getInt("病毒y軸")
    var Score : Int = bundle!!.getInt("分數")

    var thread: GameThread

    var boy:Boy? = null
    var virus:Virus? = null

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {

        background1 = Background(BitmapFactory.decodeResource(resources, R.drawable.forest))
        background1!!.x = bk1_x

        background2 = Background(BitmapFactory.decodeResource(resources, R.drawable.forest))

        if (background1!!.x<=0){
            background2!!.x = background1!!.x + screenWidth
        }
        else{
            background2!!.x = background1!!.x - screenWidth
        }

        boy = Boy(context, resources,
                BitmapFactory.decodeResource(resources, R.drawable.boy1))

        boy!!.x = boy_x
        boy!!.y = boy_y

        virus = Virus(context, resources,
                BitmapFactory.decodeResource(resources, R.drawable.virus1))

        virus!!.x = virus_x
        virus!!.y = virus_y

        thread.running = true
        thread.start()  //開始Thread
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

    fun update() {
        //捲動背景處理
        background1!!.x -= backgroundMoveX
        background2!!.x -= backgroundMoveX

        if (background1!!.x + background1!!.image.getWidth() < 0) {
            background1!!.x = background2!!.x + screenWidth
        }

        if (background2!!.x + background2!!.image.getWidth() < 0) {
            background2!!.x = background1!!.x + screenWidth
        }

        boy!!.update()
        virus!!.update()

        //判斷是否碰撞或男孩到達右邊邊界，結束遊戲
        if(boy!!.getRect().intersect(virus!!.getRect()) || (boy!!.x >= (screenWidth - boy!!.w)) ) {
            thread.running = false

            //呼叫GameActivity的GameOver方法
            var gameActivity:GameActivity = context as GameActivity
            gameActivity.GameOver(Score)
        }

        //判斷病毒是否到達邊界
        if (virus!!.ReachEdge()){
            Score++
            boy!!.x += 20
        }


        bk1_x = background1!!.x
        boy_x = boy!!.x
        boy_y = boy!!.y
        virus_x = virus!!.x
        virus_y = virus!!.y
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        background1!!.draw(canvas)
        background2!!.draw(canvas)

        boy!!.draw(canvas)
        virus!!.draw(canvas)

        paint.color = Color.WHITE
        paint.textSize = 50f
        canvas.drawText("分數：" + Score.toString() + "分", 50f, 50f, paint)
        //半透明背景
        paint.setARGB(5, 0, 0,0)
        canvas.drawRect(0f,0f, screenWidth.toFloat(),80f, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var boyRect: Rect = boy!!.getRect()  //讀取男孩圖形區域

        if (boyRect.contains(event.getX().toInt(), event.getY().toInt())) {
            boy!!.Jump("UP",false)  //按到小男孩，往上跳躍30像素，並往右移動20像素
        }
        else if (virus!!.getRect().contains(event.getX().toInt(), event.getY().toInt())) {
            //往右拖曳病毒
            if (event.action == MotionEvent.ACTION_MOVE){
                if (virus!!.x < event.getX() - virus!!.w / 2) {
                    virus!!.x=event.getX().toInt() - virus!!.w / 2
                    virus!!.y=event.getY().toInt() - virus!!.h / 2
                }
            }
            else if (event.action == MotionEvent.ACTION_UP){
                boy!!.x += 30
            }
        }
        else if (event.action == MotionEvent.ACTION_DOWN) {
            boy!!.Jump("DOWN",false)  //按到其他區域，小男孩往下跳躍30像素，並往右移動20像素
        }
        return true
    }

}