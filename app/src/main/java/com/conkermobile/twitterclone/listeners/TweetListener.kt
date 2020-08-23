package com.conkermobile.twitterclone.listeners

import com.conkermobile.twitterclone.util.Tweet

interface TweetListener {
    fun onLayoutClick(tweet: Tweet?)
    fun onLike(tweet: Tweet?)
    fun onRetweet(tweet: Tweet?)
}