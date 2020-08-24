package com.conkermobile.twitterclone.listeners

import androidx.recyclerview.widget.RecyclerView
import com.conkermobile.twitterclone.util.DATA_TWEETS
import com.conkermobile.twitterclone.util.DATA_TWEETS_LIKES
import com.conkermobile.twitterclone.util.Tweet
import com.conkermobile.twitterclone.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TwittarListenerimpl(
    private val tweetList: RecyclerView, var user: User?, private val callback: HomeCallback?): TweetListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onLayoutClick(tweet: Tweet?) {

    }

    override fun onLike(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val likes :ArrayList<String>? = tweet.likes
            if(tweet.likes?.contains(userId) == true) {
                likes?.remove(userId!!)
            } else {
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEETS_LIKES, likes)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }

    override fun onRetweet(tweet: Tweet?) {
    }
}