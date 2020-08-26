package com.conkermobile.twitterclone.listeners

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.recyclerview.widget.RecyclerView
import com.conkermobile.twitterclone.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TwittarListenerimpl(
    private val tweetList: RecyclerView,
    var user: User?,
    private val callback: HomeCallback?
) : TweetListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onLayoutClick(tweet: Tweet?) {
        tweet?.let {
            val owner: String? = tweet.userIds?.get(0)
            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Unfollow ${tweet.username}?")
                        .setPositiveButton("yes") { _: DialogInterface, _: Int ->
                            tweetList.isClickable = false
                            var followUsers = user?.followUsers
                            if (followUsers == null) {
                                followUsers = arrayListOf()
                            }
                            followUsers?.remove(owner)
                            firebaseDB.collection(DATA_USERS).document(userId!!)
                                .update(DATA_USERS_FOLLOW, followUsers)
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callback?.onUserUpdated()
                                }
                        }
                        .setNegativeButton("cancel") { _: DialogInterface?, _: Int -> }
                        .show()
                } else {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Follow ${tweet.username}?")
                        .setPositiveButton("yes") { _: DialogInterface, _: Int ->
                            tweetList.isClickable = false
                            var followUsers = user?.followUsers
                            if (followUsers == null) {
                                followUsers = arrayListOf()
                            }
                            owner?.let {
                                followUsers?.add(owner)
                                firebaseDB.collection(DATA_USERS).document(userId!!)
                                    .update(DATA_USERS_FOLLOW, followUsers)
                                    .addOnSuccessListener {
                                        tweetList.isClickable = true
                                        callback?.onUserUpdated()
                                    }
                            }
                        }
                        .setNegativeButton("cancel") { _: DialogInterface?, _: Int -> }
                        .show()
                }
            }
        }
    }

    override fun onLike(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val likes = tweet.likes
            if (tweet.likes?.contains(userId) == true) {
                likes?.remove(userId)
            } else {
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEETS_LIKES, likes)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
        }
    }

    override fun onRetweet(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val retweets = tweet.userIds
            if (retweets?.contains(userId) == true) {
                retweets?.remove(userId)
            } else {
                retweets?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEET_USER_IDS, retweets)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }
}