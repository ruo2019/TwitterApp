package com.conkermobile.twitterclone.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.fragments.HomeFragment
import com.conkermobile.twitterclone.fragments.MyActivityFragment
import com.conkermobile.twitterclone.fragments.SearchFragment
import com.conkermobile.twitterclone.fragments.TwittarFragment
import com.conkermobile.twitterclone.listeners.HomeCallback
import com.conkermobile.twitterclone.util.DATA_USERS
import com.conkermobile.twitterclone.util.User
import com.conkermobile.twitterclone.util.loadURL
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), HomeCallback {

    private var sectionsPagerAdapter: SectionPageAdapter? = null
    private val firebaseAuth = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private var user : User? = null
    private var currentFragment: TwittarFragment = homeFragment

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sectionsPagerAdapter = SectionPageAdapter(supportFragmentManager)

        container.adapter = sectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        titleBar.visibility = VISIBLE
                        titleBar.text = getString(R.string.text_home)
                        searchBar.visibility = GONE
                        currentFragment = homeFragment
                    }
                    1 -> {
                        titleBar.visibility = GONE
                        searchBar.visibility = VISIBLE
                        currentFragment = searchFragment
                    }
                    2 -> {
                        titleBar.visibility = VISIBLE
                        titleBar.text = getString(R.string.text_my_activity)
                        searchBar.visibility = GONE
                        currentFragment = myActivityFragment
                    }
                }
            }
        })
        logo.setOnClickListener {
            startActivity(ProfileActivity.newIntent(this))
        }

        fab.setOnClickListener{
            startActivity(TweetActivity.newIntent(this, userId!!, user?.username))
        }
        homeProgressLayout.setOnTouchListener { _: View, _: MotionEvent -> true }
    }

    fun search(v: View) {
        searchFragment.newHashtag(search.text.toString())
    }

    override fun onResume() {
        super.onResume()
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        } else {
            populate()
        }
    }

    override fun onUserUpdated() {
        populate()
    }

    override fun onRefresh() {
        currentFragment.updateList()
    }

    private fun populate() {

        homeProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                homeProgressLayout.visibility = View.GONE
                user = documentSnapshot.toObject(User::class.java)
                user?.imageURL?.let {
                    logo.loadURL(it, R.drawable.default_user)
                }
                updateFragmentUser()
            }
            .addOnFailureListener { e: Exception ->
                e.printStackTrace()
                finish()
            }
    }

    private fun updateFragmentUser() {
        homeFragment.setUser(user)
        searchFragment.setUser(user)
        myActivityFragment.setUser(user)
        currentFragment.updateList()
    }

    inner class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> homeFragment
                1 -> searchFragment
                else -> myActivityFragment
            }
        }

        override fun getCount() = 3
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}