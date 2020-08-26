package com.conkermobile.twitterclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.adapters.TweetListAdapter
import com.conkermobile.twitterclone.listeners.TwittarListenerimpl
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : TwittarFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = TwittarListenerimpl(tweetList, currentUser, callback)

        tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)
        tweetList?.apply {
            layout
        }
    }

    override fun updateList() {
    }
}