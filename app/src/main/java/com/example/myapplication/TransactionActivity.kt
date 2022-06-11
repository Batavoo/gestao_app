package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import com.example.myapplication.listener.TransactionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.example.myapplication.entity.Transaction
import com.example.myapplication.entity.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TransactionActivity : AppCompatActivity(), TransactionListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private var isRevenue: Boolean = false

    private lateinit var mTransactionName: EditText
    private lateinit var mTransactionValue: EditText
    private lateinit var mTransactionDate: EditText
    private lateinit var mTransactionType: RadioGroup
    private lateinit var mTransactionAction: Button
    private lateinit var mTransactionRevenue: RadioButton
    private lateinit var mTransactionExpense: RadioButton


    private var mUserKey = ""
    private var mTransactionKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mUserKey = intent.getStringExtra("userKey") ?: ""
        mTransactionKey = intent.getStringExtra("transactionKey") ?: ""

        mTransactionName = findViewById(R.id.transaction_editext_name)
        mTransactionValue = findViewById(R.id.transaction_editext_value)
        mTransactionDate = findViewById(R.id.transaction_editext_date)

        mTransactionType = findViewById(R.id.transaction_rg)
        mTransactionRevenue = findViewById(R.id.rbRevenue)
        mTransactionExpense = findViewById(R.id.rbExpense)

        mTransactionAction = findViewById(R.id.transaction_button_create)
        mTransactionAction.setOnClickListener{

            val name = mTransactionName.text.toString().trim()
            val value = mTransactionValue.text.toString().trim()
            val date = mTransactionDate.text.toString().trim()
            val type = mTransactionType.checkedRadioButtonId

            if (name.isBlank()){
                mTransactionName.error = "This field is mandatory"
                return@setOnClickListener
            }


            if (type == 2131296800){
            isRevenue = true
            } else if (type == 2131296799){
            isRevenue = false
            }


            val usersRef = mDatabase.getReference("/users")
            usersRef
                .orderByChild("email").equalTo(mAuth.currentUser?.email)
                .addChildEventListener(object :ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        val userId = snapshot.key ?: ""
                        val transactionRef = usersRef.child(userId).child("/transactions")

                        val transactionId = transactionRef.push().key ?: ""
                        val transaction = Transaction(transactionId, name,value,date,isRevenue)

                        transactionRef.child(transactionId).setValue(transaction)

                        Toast.makeText(
                            baseContext, "Transaction \"$name\" created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()

                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }

    }

    override fun onResume() {
        super.onResume()

        if (mTransactionKey.isBlank()){
            mTransactionAction.text = "Create"
        } else {
            mTransactionAction.text = "Save"

            val userRef = mDatabase.getReference("/users")
            userRef
                .orderByChild("email")
                .equalTo(mAuth.currentUser?.email!!)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.children.first().getValue(User::class.java)
                        val transaction = user?.transactions?.values?.find { it.id == mTransactionKey }
                        mTransactionName.text = Editable.Factory.getInstance().newEditable(transaction?.name)
                        mTransactionValue.text = Editable.Factory.getInstance().newEditable(transaction?.value)
                        mTransactionDate.text = Editable.Factory.getInstance().newEditable(transaction?.date)
                        mTransactionRevenue.isChecked = transaction?.revenue == true
                        mTransactionExpense.isChecked = transaction?.revenue == false
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
    }

    override fun setOnItemClickListener(view: View, position: Int) {
        TODO("Not yet implemented")
    }

    override fun setOnItemLongClickListener(view: View, position: Int) {
        TODO("Not yet implemented")
    }
}