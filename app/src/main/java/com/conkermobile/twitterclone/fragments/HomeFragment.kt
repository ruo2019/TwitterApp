package com.conkermobile.twitterclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.adapters.TweetListAdapter
import com.conkermobile.twitterclone.listeners.TwittarListenerimpl
import com.conkermobile.twitterclone.util.DATA_TWEETS
import com.conkermobile.twitterclone.util.DATA_TWEET_HASHTAGS
import com.conkermobile.twitterclone.util.DATA_TWEET_USER_IDS
import com.conkermobile.twitterclone.util.Tweet
import com.google.firebase.firestore.QuerySnapshot
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
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }
    }

    override fun updateList() {
        tweetList?.visibility = GONE
        currentUser?.let {
            val tweets = arrayListOf<Tweet>()

            for (hashtag in it.followHashtags!!) {
                firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, hashtag)
                    .get()
                    .addOnSuccessListener { list: QuerySnapshot ->
                        for (document in list.documents) {
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let { tweets.add(it) }
                        }
                        updateAdapter(tweets)
                        tweetList.visibility = VISIBLE
                    }
                    .addOnFailureListener { e: Exception ->
                        e.printStackTrace()
                        tweetList?.visibility = VISIBLE
                    }
            }

            for (followedUser in it.followUsers!!) {
                firebaseDB.collection(DATA_TWEETS)
                    .whereArrayContains(DATA_TWEET_USER_IDS, followedUser).get()
                    .addOnSuccessListener { list: QuerySnapshot ->
                        for (document in list.documents) {
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let { tweets.add(it) }
                        }
                        updateAdapter(tweets)
                        tweetList.visibility = VISIBLE
                    }
                    .addOnFailureListener { e: Exception ->
                        e.printStackTrace()
                        tweetList?.visibility = VISIBLE
                    }
            }
        }
    }

    private fun updateAdapter(tweets: List<Tweet>) {
        val sortedTweets = tweets.sortedWith(compareByDescending { it.timeStamp })
        tweetsAdapter?.updateTweets(removeDuplicates(sortedTweets))
    }

    private fun removeDuplicates(originalList: List<Tweet>) = originalList.distinctBy { it.tweetId }

}