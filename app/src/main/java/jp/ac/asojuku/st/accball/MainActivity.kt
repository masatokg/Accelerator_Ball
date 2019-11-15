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
        , SensorEventListener {

    // 誕生時のライフサイクルイベント
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // 画面表示・再表示のライフサイクルイベント
    override fun onResume() {
        // 親クラスのonResume()処理
        super.onResume()
        // 自クラスのonResume()処理
        // センサーマネージャをOSから取得
        val sensorManager =
                this.getSystemService(Context.SENSOR_SERVICE) as
                        SensorManager;
        // 加速度センサー(Accelerometer)を指定してセンサーマネージャからセンサーを取得
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // リスナー登録して加速度センサーの監視を開始
        sensorManager.registerListener(
                this,  // イベントリスナー機能をもつインスタンス（自クラスのインスタンス）
                accSensor, // 監視するセンサー（加速度センサー）
                SensorManager.SENSOR_DELAY_GAME // センサーの更新頻度
        )
    }

    // 画面が非表示の時のライフサイクルイベント
    override fun onPause() {
        super.onPause()
        // センサーマネージャを取得
        val sensorManager =
                this.getSystemService(Context.SENSOR_SERVICE) as
                        SensorManager;
        // センサーマネージャに登録したリスナーを解除（自分自身を解除）
        sensorManager.unregisterListener(this);

    }

    // 精度が変わった時のイベントコールバック
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    // センサーの値が変わった時のイベントコールバック
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("TAG01","センサーが変わりました")
        // イベントが何もなかったらそのままリターン
        if(event == null){ return; }

        // センサーの値が変わったらログに出力
        // 加速度センサーか判定
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            // ログ出力用文字列を組み立て
            val str:String = "x = ${event.values[0].toString()}" +
                    ", y = ${event.values[1].toString()}" +
                    ", z = ${event.values[2].toString()}";
            // デバッグログに出力
            Log.d("加速度センサー", str);
        }
    }
}






