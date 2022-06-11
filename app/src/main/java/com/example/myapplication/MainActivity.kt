package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.TransactionAdapter
import com.example.myapplication.entity.User
import com.example.myapplication.listener.TransactionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener, TransactionListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private var mUserKey = ""

    private lateinit var mMainFabAdd: FloatingActionButton
    private lateinit var mMainRevenueList: RecyclerView
    private lateinit var mMainExpenseList: RecyclerView
    private lateinit var mMainUsername: TextView

     private lateinit var mRevenueAdapter: TransactionAdapter
     private lateinit var mExpenseAdapter: TransactionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mMainUsername = findViewById(R.id.main_textView_username)
        mMainRevenueList = findViewById(R.id.main_recyclerview_revenue)
        mMainRevenueList.layoutManager = LinearLayoutManager(this)
        mMainExpenseList = findViewById(R.id.main_recyclerview_expenses)
        mMainExpenseList.layoutManager = LinearLayoutManager(this)

        mMainFabAdd = findViewById(R.id.main_fab_task_add)
        mMainFabAdd.setOnClickListener {
            val it = Intent(this, TransactionActivity::class.java)
            startActivity(it)
        }



        /* val usersRef = mDatabase.getReference("/users");
        usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email).addChildEventListener(object:ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.let { Log.i("App", it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
         */



    }

    override fun onStart() {
        super.onStart()



        val userRef = mDatabase.getReference("/users")
        userRef
            .orderByChild("email")
            .equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (children in snapshot.children){
                        val user = snapshot.children.first().getValue(User::class.java)
                        mUserKey = user?.id ?: ""

                        mMainUsername.text = user?.name

                        var receitas = user?.transactions?.values?.toList()?.filter { it.revenue }?.sortedWith(
                            compareBy { it.date })?.take(5)
                        mRevenueAdapter = receitas?.toList()?.let { TransactionAdapter(it) }!!
                        mRevenueAdapter.setOnTransactionListener(this@MainActivity)
                        mMainRevenueList.adapter = mRevenueAdapter

                        var despesas = user?.transactions?.values?.toList()?.filter { !it.revenue }?.sortedWith(
                            compareBy { it.date })?.take(5)
                        mExpenseAdapter = despesas?.toList()?.let { TransactionAdapter(it) }!!
                        mExpenseAdapter.setOnTransactionListener(this@MainActivity)
                        mMainExpenseList.adapter = mExpenseAdapter



                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    override fun setOnItemClickListener(view: View, position: Int){
        if( view.parent == mMainRevenueList) {
            val it = Intent(this, TransactionActivity::class.java)
            it.putExtra("transactionKey",  mRevenueAdapter.list[position].id)
            it.putExtra("userKey",  mUserKey)
            startActivity(it)
        }else{
            val it = Intent(this, TransactionActivity::class.java)
            it.putExtra("transactionKey",  mExpenseAdapter.list[position].id)
            it.putExtra("userKey",  mUserKey)
            startActivity(it)
        }
    }

    override fun setOnItemLongClickListener(view: View, position: Int) {
        if (view.parent == mMainRevenueList) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Quallet")
                .setMessage("Are you sure that you want to delete this transaction?")
                .setCancelable(false)
                .setPositiveButton("Yes"){ dialog, _ ->
                    val transaction = mRevenueAdapter.list[position]
                    val transactionRef = mDatabase
                        .reference
                        .child("/users")
                        .child(mUserKey)
                        .child("/transactions")
                        .child(transaction.id)

                    transactionRef.removeValue()

                    dialog.dismiss()
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        } else if (view.parent == mMainExpenseList){

            val dialog = AlertDialog.Builder(this)
                .setTitle("Quallet")
                .setMessage("Are you sure that you want to delete this transaction?")
                .setCancelable(false)
                .setPositiveButton("Yes"){ dialog, _ ->
                    val transaction = mExpenseAdapter.list[position]
                    val transactionRef = mDatabase
                        .reference
                        .child("/users")
                        .child(mUserKey)
                        .child("/transactions")
                        .child(transaction.id)

                    transactionRef.removeValue()

                    dialog.dismiss()
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()

        }

    }


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}