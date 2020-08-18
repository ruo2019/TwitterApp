package com.conkermobile.twitterclone.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.R.layout
import com.conkermobile.twitterclone.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private var imageURL: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_profile)

        if(userID == null) {
            finish()
        }

        profileProgressLayout.setOnTouchListener{ _: View, _: MotionEvent -> true}

        photoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        populateInfo()
    }

    private fun populateInfo() {
        profileProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userID!!).get()
            .addOnSuccessListener { documentSnapshot  ->
                val user = documentSnapshot.toObject(User::class.java)
                newUsernameET.setText(user?.username, TextView.BufferType.EDITABLE)
                newEmailET.setText(user?.email, TextView.BufferType.EDITABLE)
                imageURL?.let {
                    photoIV.loadURL(user?.imageUrl, R.drawable.logo)
                }
                profileProgressLayout.visibility = View.GONE
            }
            .addOnFailureListener { e :Exception ->
                e.printStackTrace()
                finish()
            }
    }

    fun onApply(v: View) {
        profileProgressLayout.visibility = View.VISIBLE
        val username = newUsernameET.text.toString()
        val email = newEmailET.text.toString()
        val map = HashMap<String, Any>()
        map[DATA_USER_USERNAME] = username
        map[DATA_USER_EMAIL] = email

        firebaseDB.collection(DATA_USERS).document(userID!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e :Exception ->
                e.printStackTrace()
                Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show()
                profileProgressLayout.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
    }

    private fun storeImage(imageURL: Uri?) {
        imageURL?.let {
            Toast.makeText(this,"Uploading...", Toast.LENGTH_SHORT).show()
            profileProgressLayout.visibility = View.VISIBLE
            val fillPath : FirebaseStorage = firebaseStorage.
        }
    }

    fun onSignout(v: View) {
        firebaseAuth.signOut()
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }
}