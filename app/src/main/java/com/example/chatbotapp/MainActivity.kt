package com.example.chatbotapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var sendMsgIB: ImageButton
    lateinit var userMsgEdt: EditText
    lateinit var chatsRV: RecyclerView

    private val USER_KEY = 1
    private val BOT_KEY = 2

    // our volley request queue.
    lateinit var mRequestQueue: RequestQueue

    // creating a variable for array list and adapter class.
    lateinit var messageModalArrayList: ArrayList<MessageModel>
    lateinit var messageRVAdapter: MessageRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // on below line we are initializing all our views.
        chatsRV = findViewById<RecyclerView>(R.id.idRVChats)
        sendMsgIB = findViewById(R.id.idIBSend)
        userMsgEdt = findViewById(R.id.idEdtMessage)
        messageModalArrayList = ArrayList<MessageModel>()

        mRequestQueue = Volley.newRequestQueue(this@MainActivity)
        mRequestQueue.cache.clear()

        sendMsgIB.setOnClickListener(View.OnClickListener {
            if (userMsgEdt.text.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter your message..", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            sendMessage(userMsgEdt.text.toString())
            // below line we are setting text in our edit text as empty
            userMsgEdt.setText("")
        })

        messageRVAdapter = MessageRVAdapter(messageModalArrayList, this)
        val linearLayoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        chatsRV.layoutManager = linearLayoutManager
        chatsRV.adapter = messageRVAdapter
    }

    private fun sendMessage(userMsg: String) {
        messageModalArrayList.add( MessageModel(userMsg, 1))
        messageRVAdapter.notifyDataSetChanged()

        val url = "Enter your API KEY&uid=uid&msg=$userMsg"
        val queue = Volley.newRequestQueue(this@MainActivity)

        // on below line we are making a json object request for a get request and passing our url .
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->

                try {
                    val botResponse = response.getString("cnt")
                    messageModalArrayList.add(MessageModel(botResponse, 2))

                    messageRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()

                    messageModalArrayList.add(MessageModel("No response", 2))
                    messageRVAdapter.notifyDataSetChanged()
                }
            }) { // error handling.
            messageModalArrayList.add(MessageModel("Sorry no response found", BOT_KEY))
            Toast.makeText(this@MainActivity, "No response from the bot..", Toast.LENGTH_SHORT).show()
        }
        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest)
    }
}
