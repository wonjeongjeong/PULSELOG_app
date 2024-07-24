package com.example.pulselog_app
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pulselog_app.databinding.ActivityMainBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView


class HomeActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageButton
    private lateinit var menuItem1: LinearLayout
    private lateinit var menuItem2: LinearLayout
    private lateinit var mapView : MapView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        menuButton = findViewById(R.id.toggleBtn)
        menuItem1 = findViewById(R.id.toggleOption1)
        menuItem2 = findViewById(R.id.toggleOption2)
        mapView = findViewById(R.id.kakaoMapView)

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
                Log.i("APIonMapDestroy","정상적으로 지도 API가 종료되었습니다")

            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Log.e("APIonMapError","인증실패 또는 지도 사용중 에러가 발생했습니다")

            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                Log.i("APIonMapCall","인증 완료후 API가 정상 실행되었습니다")
            }
        })

        fun onResume() {
            super.onResume()
            mapView.resume()  // MapView 의 resume 호출
        }

        fun onPause() {
            super.onPause()
            mapView.pause()  // MapView 의 pause 호출
        }

        menuButton.setOnClickListener {
                if (menuItem1.visibility == View.GONE && menuItem2.visibility==View.GONE) {
                    menuItem1.visibility = View.VISIBLE
                    menuItem2.visibility = View.VISIBLE
                } else {
                    menuItem1.visibility = View.GONE
                    menuItem2.visibility = View.GONE
                }
            }






    }
    }

