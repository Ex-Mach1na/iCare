package com.example.icare

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.icare.databinding.ItemMessageReceiveBinding
import com.example.icare.databinding.ItemMessageSendBinding
import com.example.icare.model.InjuryInfo
import com.example.icare.model.Message
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.activity_injury.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InjuryActivity : AppCompatActivity() {

    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var sendButtonInj:Button
    lateinit var editTextInj : AppCompatAutoCompleteTextView
    lateinit var injuryInfo: InjuryInfo
    lateinit var allInjuryInfo: MutableList<InjuryInfo>
    lateinit var tempArray:Array<String>
    var flag:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_injury)

        //getValueFromDatabase()
        val ref = FirebaseDatabase.getInstance().getReference("Injury")
        allInjuryInfo = mutableListOf()
        tempArray = resources.getStringArray(R.array.injury_name)


        injuryRecycleV.adapter = messageAdapter

        flag = 1
        magic()
    }

    private fun magic()
    {
        //Do the magic
        sendButtonInj = findViewById(R.id.sendButtonInj)
        editTextInj = findViewById(R.id.editTextInj)

        if(flag == 1)
        {
            receiveAutoResponse( "start" , flag)
        }


        sendButtonInj.setOnClickListener {
            val message = Message(editTextInj.text.toString(), "me")
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextInj.text.clear()
            if(message.msg in tempArray)
            {
                receiveAutoResponse(message.msg, 2)
            }
            else
                receiveAutoResponse(message.msg,0)

            //receiveAutoResponse(message.msg)
        }
    }

    /**private fun getValueFromDatabase()
    {

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val ref = FirebaseDatabase.getInstance().getReference("Injury")
        allInjuryInfo = mutableListOf()

        Log.d("Fun", "getValueFromDatabase working")
        
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    //for(i in snapshot.children)
                    //{
                    //    val temp = i.getValue(InjuryInfo::class.java)
                    //    allInjuryInfo.add(temp!!)
                    //}

                    var i:Int = 0
                    for(i in 1..6)
                    {
                        val info:InjuryInfo = snapshot.child(i.toString()).getValue(InjuryInfo::class.java)!!
                        Log.d("Tag","Bla bla bla ${info.Name}")
                    }
                }
                else
                    Toast.makeText(applicationContext, "Error!! Database Not Found.", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }*/


    private fun receiveAutoResponse(temp: String, flag :Int){

        var finalString:String = "Sorry. I didn't understand. Can you repeat?"
        Log.d("tag", finalString)
        if(flag == 1 || startOfChat(temp))
        {
            finalString = getString(R.string.start_msg_injury)
            finalString += "\n"
            val newAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, tempArray)
            editTextInj.setAdapter(newAdapter)
            editTextInj.threshold = 1
            editTextInj.text.clear()

            for(i in tempArray)
            {
                finalString += i
                finalString += '\n'
            }
            finalString += "\nPlease tell me which one do you have."
            //Log.d("Tag","${finalString}")
        }

        if(flag == 2)
        {
            finalString = "I have a solution for you\n\n"
            if(temp.equals(getString(R.string.injury_name1),true))
                finalString += getString(R.string.injury1)
            else if(temp.equals(getString(R.string.injury_name2),true))
                finalString += getString(R.string.injury2)
            else if(temp.equals(getString(R.string.injury_name3),true))
                finalString += getString(R.string.injury3)
            else if(temp.equals(getString(R.string.injury_name4),true))
                finalString += getString(R.string.injury4)
            else if(temp.equals(getString(R.string.injury_name5),true))
                finalString += getString(R.string.injury5)
        }

        if(endOfChat(temp))
            finalString = getString(R.string.end_of_conversation)

        GlobalScope.launch(Dispatchers.Main) {

            val receive = Message(
                msg = finalString, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)
            messageAdapter.add(receiveItem)
        }
    }



    private fun endOfChat (msg: String) : Boolean
    {
        val terminateCommands:Array<String> = arrayOf("ok", "thanks", "thank you", "bye")

        for(i in terminateCommands)
        {
            if(msg.equals(i, true))
            {
                return true
            }
        }
        return false
    }

    private fun startOfChat (msg: String) : Boolean
    {
        val startCommands:Array<String> = arrayOf("Hello", "Hi", "Oi", "Can you help?", "can you help")

        for(i in startCommands)
        {
            if(msg.equals(i, true))
                return true
        }
        return false
    }
}

class SendMessageItem(private val message: Message) : BindableItem<ItemMessageSendBinding>(){
    override fun getLayout(): Int {
        return R.layout.item_message_send
    }

    override fun bind(viewBinding: ItemMessageSendBinding, position: Int) {
        viewBinding.message = message
    }
}

class ReceiveMessageItem(private val message: Message) : BindableItem<ItemMessageReceiveBinding>(){
    override fun getLayout(): Int {
        return R.layout.item_message_receive
    }

    override fun bind(viewBinding: ItemMessageReceiveBinding, position: Int) {
        viewBinding.message = message
    }
}