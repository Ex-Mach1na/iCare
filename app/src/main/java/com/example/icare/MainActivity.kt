package com.example.icare

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greetings()

        val injuryButton:Button = findViewById(R.id.injuryButton)
        val illnessButton:Button = findViewById(R.id.illnessButton)
        val aboutButton:Button = findViewById(R.id.aboutButton)
        val exitButton:Button = findViewById(R.id.exitButton)

        injuryButton.setOnClickListener {
            //Toast.makeText(applicationContext, "Injury button working", LENGTH_SHORT).show()

            val intent1 = Intent(this,  InjuryActivity::class.java)
            startActivity(intent1)
        }

        illnessButton.setOnClickListener {
            Toast.makeText(applicationContext, "Illness button working", LENGTH_SHORT).show()
            val intent2 = Intent(this, IllnessActivity::class.java)
            startActivity(intent2)
        }

        aboutButton.setOnClickListener {
            Toast.makeText(applicationContext, "Stay tuned", LENGTH_SHORT).show()
            //val intent3 = Intent(this, Database::class.java)
            //startActivity(intent3)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun greetings()
    {
        val greetings:TextView = findViewById(R.id.greetings)
        val currentTime = LocalDateTime.now()
        val hours = currentTime.format(DateTimeFormatter.ofPattern("HH"))
        val checker:Int = hours.toInt()
        if(checker in 5..11)
            greetings.text = getString(R.string.time1)
        else if(checker in 12..17)
            greetings.text = getString(R.string.time2)
        else if(checker in 18..22)
            greetings.text = getString(R.string.time3)
        else
            greetings.text = getString(R.string.time4)

    }
}