package tw.edu.pu.csim.tcyang.testandroid2d

import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener{

    lateinit var mygv : GameView
    var bundle = Bundle()
    val screenWidth= Resources.getSystem().displayMetrics.widthPixels  //讀取螢幕寬度
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels  //讀取螢幕高度

    //音效
    lateinit var mper: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        SetFullScreen()  //設定全螢幕

        bundle.putInt("倒數", 1000)
        bundle!!.putInt("背景1x軸", 0)
        bundle!!.putInt("男孩x軸", screenWidth / 10)
        bundle!!.putInt("男孩y軸", screenHeight * 6 / 10)
        bundle!!.putInt("病毒x軸", screenWidth)
        bundle!!.putInt("病毒y軸", screenHeight * 1 / 2)
        bundle!!.putInt("分數", 0)

        mygv = GameView(this,bundle)
        mylayout.addView(mygv)

        //遊戲背景音效
        mper = MediaPlayer()
        mper = MediaPlayer.create(this, R.raw.background)
        mper.setLooping(true)
        mper.start()
    }

    fun SetFullScreen(){
        //隱藏狀態列
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // 隱藏動作列
        val actionBar = supportActionBar
        actionBar!!.hide()

        //不要自動休眠
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()

        mygv.onPause()
        mygv.thread.running = false

        //記錄目前狀態
        bundle!!.putInt("背景1x軸", mygv.bk1_x)
        bundle!!.putInt("男孩x軸", mygv.boy_x)
        bundle!!.putInt("男孩y軸", mygv.boy_y)

        bundle!!.putInt("病毒x軸", mygv.virus_x)
        bundle!!.putInt("病毒y軸", mygv.virus_y)
        bundle!!.putInt("分數", mygv.Score)

        mylayout.removeView(mygv)

        if(mper != null && mper.isPlaying()){
            mper.pause()
        }
        else{
            mper.reset()
        }

    }

    override fun onResume() {
        super.onResume()
        mygv.onResume()

        mygv = GameView(this, bundle)
        mylayout.addView(mygv)

        if(mper != null){
            mper.start()
        }

    }

    fun GameOver(score:Int) {
        val intent = Intent(this, OverActivity::class.java)
        intent.putExtra("分數", score)
        startActivity(intent)
        finish()

        //遊戲結束之音效
        mper.reset()
        mper = MediaPlayer.create(this, R.raw.gameover)
        mper.setLooping(false)
        mper.start()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        mper.release()
    }

}