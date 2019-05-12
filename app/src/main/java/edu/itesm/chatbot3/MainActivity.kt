package com.itecstraining.chatbot
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.bassaer.chatmessageview.model.ChatUser
import com.github.bassaer.chatmessageview.model.Message

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import edu.itesm.chatbot3.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val human = ChatUser(
            1,
            "User",
            BitmapFactory.decodeResource(resources,
                R.drawable.ic_account_circle)
        )


        val agent = ChatUser(
            2,
            "YOUR CHAT BOT",
            BitmapFactory.decodeResource(resources,
                R.drawable.ic_account_circle)
        )

        FuelManager.instance.baseHeaders = mapOf( "Authorization" to "Bearer $ACCESS_TOKEN" )
        FuelManager.instance.basePath =  "https://api.dialogflow.com/v1/"

        FuelManager.instance.baseParams = listOf(
            "v" to "20170712",
            "sessionId" to UUID.randomUUID(),
            "lang" to "en"
        )

        my_chat_view.setOnClickSendButtonListener(
            View.OnClickListener {
                my_chat_view.send(
                    Message.Builder()
                        .setUser(human)
                        .setText(my_chat_view.inputText)
                        .build()
                )

                var reply : JSONObject
                var rep : String = ""
                Fuel.get("/query",
                    listOf("query" to my_chat_view.inputText))
                    .responseJson { _, _, result ->
                        reply = result.get().obj()
                        rep = reply.getJSONObject("result").getJSONObject("fulfillment").get("speech").toString()
                        Log.i("reply", rep)

                        my_chat_view.send(Message.Builder()
                            .setRight(true)
                            .setUser(agent)
                            .setText(rep)
                            .build()
                        )
                    }

            })
    }


    companion object {
        private const val ACCESS_TOKEN = "503ea7bc518e4f60a8205237f976ad21"

    }
}
