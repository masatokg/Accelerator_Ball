package jp.ac.asojuku.st.accball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
        , SensorEventListener, SurfaceHolder.Callback {

    // プロパティ
    private var surfaceWidth:Int = 0; // サーフェスの幅
    private var surfaceHeight:Int = 0; // サーフェスの高さ

    private val radius = 50.0f; // ボールの半径
    private val coef = 1000.0f; // ボールの移動量を計算するための係数（計数）

    private var ballX:Float = 0f; // ボールの現在のX座標
    private var ballY:Float = 0f; // ボールの現在のY座標
    private var vx:Float = 0f; // ボールのX方向の加速度
    private var vy:Float = 0f; // ボールのY方向の加速度
    private var time:Long = 0L; // 前回の取得時間


    // 誕生時のライフサイクルイベント
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder; // サーフェスホルダーを取得
        // サーフェスホルダーのコールバックに自クラスを追加
        holder.addCallback(this);
        // 画面の縦横指定をアプリから指定してロック(縦方向に指定)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    // 画面表示・再表示のライフサイクルイベント
    override fun onResume() {
        // 親クラスのonResume()処理
        super.onResume()
        // 自クラスのonResume()処理
//        // センサーマネージャをOSから取得
//        val sensorManager =
//                this.getSystemService(Context.SENSOR_SERVICE) as
//                        SensorManager;
//        // 加速度センサー(Accelerometer)を指定してセンサーマネージャからセンサーを取得
//        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        // リスナー登録して加速度センサーの監視を開始
//        sensorManager.registerListener(
//                this,  // イベントリスナー機能をもつインスタンス（自クラスのインスタンス）
//                accSensor, // 監視するセンサー（加速度センサー）
//                SensorManager.SENSOR_DELAY_GAME // センサーの更新頻度
//        )
    }

    // 画面が非表示の時のライフサイクルイベント
    override fun onPause() {
        super.onPause()
//        // センサーマネージャを取得
//        val sensorManager =
//                this.getSystemService(Context.SENSOR_SERVICE) as
//                        SensorManager;
//        // センサーマネージャに登録したリスナーを解除（自分自身を解除）
//        sensorManager.unregisterListener(this);

    }

    // 精度が変わった時のイベントコールバック
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    // センサーの値が変わった時のイベントコールバック
    override fun onSensorChanged(event: SensorEvent?) {
//        Log.d("TAG01","センサーが変わりました")
        // イベントが何もなかったらそのままリターン
        if(event == null){ return; }

//        // センサーの値が変わったらログに出力
//        // 加速度センサーか判定
//        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
//            // ログ出力用文字列を組み立て
//            val str:String = "x = ${event.values[0].toString()}" +
//                    ", y = ${event.values[1].toString()}" +
//                    ", z = ${event.values[2].toString()}";
//            // デバッグログに出力
//            // Log.d("加速度センサー", str);
//        }

        // ボールの描画の計算処理
        if(time==0L){ time = System.currentTimeMillis();} // 最初のタイミングでは現在時刻を保存
        // イベントのセンサー種別の情報がアクセラメーター（加速度センサー）の時だけ以下の処理を実行
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            // センサーのx(左右),y（縦）値を取得
            val x = event.values[0]*-1;
            val y = event.values[1];

            // 経過時間を計算(今の時間-前の時間 = 経過時間)
            var t = (System.currentTimeMillis() - time).toFloat();
            // 今の時間を「前の時間」として保存
            time = System.currentTimeMillis();
            t /= 1000.0f;

            // 移動距離を計算（ボールをどれくらい動かすか）
            val dx = (vx*t) + (x*t*t)/2.0f; // xの移動距離(メートル)
            val dy = (vy*t) + (y*t*t)/2.0f; // yの移動距離（メートル）
            // メートルをピクセルのcmに補正してボールのX座標に足しこむ=新しいボールのX座標
            ballX += (dx*coef);
            // メートルをピクセルのcmに補正してボールのY座標に足しこむ=新しいボールのY座標
            ballY += (dy*coef);
            // 今の各方向の加速度を更新
            vx +=(x*t);
            vy +=(y*t);

            // 画面の端にきたら跳ね返る処理
            // 左右について
            if( (ballX -radius)<0 && vx<0 ){
                // 左にぶつかった時
                vx = -vx /1.5f;
                ballX = radius;
            }else if( (ballX+radius)>surfaceWidth && vx>0){
                // 右にぶつかった時
                vx = -vx/1.5f;
                ballX = (surfaceWidth-radius);
            }
            // 上下について
            if( (ballY -radius)<0 && vy<0 ){
                // 下にぶつかった時
                vy = -vy /1.5f;
                ballY = radius;
            }else if( (ballY+radius)>surfaceHeight && vy>0 ){
                // 上にぶつかった時
                vy = -vy/1.5f;
                ballY = surfaceHeight -radius;
            }

            // キャンバスに描画
            this.drawCanvas();
        }
    }

    // サーフェスが更新された時のイベント
    override fun surfaceChanged(
            holder: SurfaceHolder?, format: Int,
            width: Int, height: Int) {
        // サーフェスの幅と高さをプロパティに保存しておく
        surfaceWidth = width;
        surfaceHeight = height;
        // ボールの初期位置を保存しておく
        ballX = (width/2).toFloat();
        ballY = (height/2).toFloat();
    }

    // サーフェスが破棄された時のイベント
    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        //加速度センサーの登録を解除する流れ
        // センサーマネージャを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as
            SensorManager;
        // センサーマネージャを通じてOSからリスナー（自分自身）を登録解除
        sensorManager.unregisterListener(this);
    }

    // サーフェスが作成された時のイベント
    override fun surfaceCreated(holder: SurfaceHolder?) {
        // 加速度センサーのリスナーを登録する流れ
        // センサーマネージャを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager;
        // センサーマネージャーから加速度センサーを取得
        val accSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 加速度センサーのリスナーをOSに登録
        sensorManager.registerListener(
                this, // リスナー（自クラス）
                accSensor, // 加速度センサー
                SensorManager.SENSOR_DELAY_GAME // センシングの頻度
        )
    }

    // サーフェスのキャンバスに描画するメソッド
    private fun drawCanvas(){
        // キャンバスをロックして取得
        val canvas = surfaceView.holder.lockCanvas();
        // キャンバスの背景色を設定
        canvas.drawColor(Color.DKGRAY);
        // キャンバスに円を描いてボールにする
        canvas.drawCircle(
                ballX, // ボール中心のX座標
                ballY, // ボール中心のY座標
                radius, // 半径
                Paint().apply {
                    color = Color.RED; } // ペイントブラシのインスタンス
        );
        // キャンバスをアンロック（ロック解除）してキャンバスを描画(ポスト)
        surfaceView.holder.unlockCanvasAndPost(canvas);
    }
}






