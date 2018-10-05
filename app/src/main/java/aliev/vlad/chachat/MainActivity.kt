package aliev.vlad.chachat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chat_list_item.*
import com.firebase.ui.database.FirebaseListOptions



class MainActivity : AppCompatActivity() {

    private var adapter: FirebaseListAdapter<Message>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.title = "Чат без клиентов"

        if(FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    Constants.SIGN_IN_REQUEST_CODE
            )
        } else {
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance().currentUser!!.displayName,
                    Toast.LENGTH_LONG)
                    .show()

            displayChatMessages();
        }

        fab.setOnClickListener {
                FirebaseDatabase.getInstance()
                        .reference
                        .push()
                        .setValue(Message(input.text.toString(), FirebaseAuth.getInstance()
                                        .currentUser!!
                                        .displayName!!))
                input.setText("")
        }
    }

    private fun displayChatMessages() {
        val query = FirebaseDatabase.getInstance().reference
        val options = FirebaseListOptions.Builder<Message>()
                .setQuery(query, Message::class.java)
                .setLayout(R.layout.chat_list_item)
                .build()

        adapter = object : FirebaseListAdapter<Message>(options) {

            override fun populateView(v: View, model: Message, position: Int) {
                message_text.text = model.text
                message_user.text = model.userName
                message_time.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.time)
            }
        }
        list_of_messages.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SIGN_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show()
                displayChatMessages()
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show()
                finish()
            }
        }
    }
}
