package com.raite.dhvsu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

            Handler().postDelayed({
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            },3000)
        }

}