package com.conkermobile.twitterclone.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.conkermobile.twitterclone.R
import com.conkermobile.twitterclone.adapters.TweetListAdapter
import com.conkermobile.twitterclone.util.*
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : TwittarFragment() {

    private var currentHashtag = ""
    private var hashtagFollowed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        followHashtag.setOnClickListener {
            followHashtag.isClickable = false
            val followed = currentUser?.followHashtags
            if(hashtagFollowed) {
                followed?.remove(currentHashtag)
            } else {
                followed?.add(currentHashtag)
            }
            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USERS_HASHTAGS, followed)
                .addOnSuccessListener {
                    callback?.onUserUpdate()
                    followHashtag.isClickable = true
                }
                .addOnFailureListener{ e :Exception ->
                    e.printStackTrace()
                    followHashtag.isClickable = true
                }
        }
    }

    fun newHashtag(term: String) {
        currentHashtag = term
        followHashtag.visibility = VISIBLE
        updateList()
    }

    override fun updateList() {
        tweetList?.visibility = View.GONE
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(
            DATA_TWEET_HASHTAGS, currentHashtag).get()
            .addOnSuccessListener { list :QuerySnapshot->
                val tweets = arrayListOf<Tweet>()
                for(document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { tweets.add(it) }
                }
                val sortedTweets = tweets.sortedWith(compareByDescending { it.timeStamp })
                tweetsAdapter?.updateTweets(sortedTweets)
                tweetList.visibility = VISIBLE
            }
            .addOnFailureListener { e :Exception ->
                e.printStackTrace()
            }

        updateFollowDrawable()
    }

    private fun updateFollowDrawable() {
        hashtagFollowed = currentUser?.followHashtags?.contains(currentHashtag) == true
        context?.let {
        if(hashtagFollowed) {
            followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow))
            } else {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow_inactive))
            }
        }
    }
}