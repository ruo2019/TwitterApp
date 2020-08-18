package com.conkermobile.twitterclone.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.fragments.HomeFragment
import com.conkermobile.twitterclone.fragments.MyActivityFragment
import com.conkermobile.twitterclone.fragments.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var sectionsPagerAdapter: SectionPageAdapter? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sectionsPagerAdapter = SectionPageAdapter(supportFragmentManager)

        container.adapter = sectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        logo.setOnClickListener {
            startActivity(ProfileActivity.newIntent(this))
        }
    }
    override fun onResume() {
        super.onResume()
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
    }

    @Suppress("deprecation")
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