package com.example.pulselog_app
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

        private lateinit var menuButton: ImageButton
        private lateinit var menuItem1: LinearLayout
        private lateinit var menuItem2: LinearLayout


    @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_home)

            menuButton = findViewById(R.id.toggleBtn)
            menuItem1 = findViewById(R.id.toggleOption1)
            menuItem2 = findViewById(R.id.toggleOption2)

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

