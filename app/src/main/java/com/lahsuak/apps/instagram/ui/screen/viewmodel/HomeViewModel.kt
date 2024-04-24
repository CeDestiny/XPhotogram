package com.lahsuak.apps.instagram.ui.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lahsuak.apps.instagram.models.ApiFailure
import com.lahsuak.apps.instagram.models.BaseState
import com.lahsuak.apps.instagram.models.Notification
import com.lahsuak.apps.instagram.models.Post
import com.lahsuak.apps.instagram.models.Story
import com.lahsuak.apps.instagram.models.Tweet
import com.lahsuak.apps.instagram.models.User
import com.lahsuak.apps.instagram.repos.HomeRepo
import com.lahsuak.apps.instagram.util.DemoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepo: HomeRepo,
) : ViewModel() {
    private val _users =
        MutableStateFlow<BaseState<List<User>, ApiFailure>>(BaseState.Loading)
    val users = _users.asStateFlow()

    private val _posts =
        MutableStateFlow<BaseState<List<Post>, ApiFailure>>(BaseState.Loading)
    val posts = _posts.asStateFlow()

    private val _stories =
        MutableStateFlow<BaseState<List<Story>, ApiFailure>>(BaseState.Loading)
    val stories = _stories.asStateFlow()

    private val _notifications =
        MutableStateFlow<BaseState<List<Notification>, ApiFailure>>(BaseState.Loading)
    val notifications = _notifications.asStateFlow()
    private val _tweets =
        MutableStateFlow<BaseState<List<Tweet>, ApiFailure>>(BaseState.Loading)
    val tweets = _tweets.asStateFlow()

    init {
        getUsers()
        getPosts()
        getStories()
        getNotifications()
    }

    fun getUsers() {
        viewModelScope.launch {
            _users.value = BaseState.Success(DemoData.allUsers)
        }
    }

    fun getUserById(userId: String): User? {
        return (_users.value as BaseState.Success).data.find {
            it.id == userId
        }
    }

    fun getPostById(postId: String): Post? {
        return (_posts.value as BaseState.Success).data.find {
            it.id == postId
        }
    }

    fun getStoryById(storyId: String): Story? {
        return (_stories.value as BaseState.Success).data.find {
            it.id == storyId
        }
    }

    fun getPosts() {
        viewModelScope.launch {
            _posts.value = BaseState.Success(DemoData.allPosts)
        }
    }

    fun getStories() {
        viewModelScope.launch {
            _stories.value = BaseState.Success(DemoData.allStories)
        }
    }

    private fun getNotifications() {
        viewModelScope.launch {
            BaseState.Success(DemoData.allNotifications)
        }
    }

    fun getUsersByIds(users: List<User>, userIds: List<String>): List<User> {
        return users.filter { user ->
            userIds.any {
                user.id == it
            }
        }
    }

    fun getPosts(ids: List<String>): List<Post> {
        return (_posts.value as BaseState.Success).data.filter { post ->
            ids.any {
                post.id == it
            }
        }
    }

    fun getStories(ids: List<String>): List<Story> {
        return (_stories.value as BaseState.Success).data.filter { story ->
            ids.any {
                story.id == it
            }
        }
    }

    fun getFollowers(user: User): List<User> {
        return (_users.value as BaseState.Success).data.filter {
            user.followerIds.any { id ->
                it.id == id
            }
        }
    }

    fun getFollowings(user: User): List<User> {
        return (_users.value as BaseState.Success).data.filter {
            user.followingIds.any { id ->
                it.id == id
            }
        }
    }

    fun getTweets(userList: List<User>) {
        viewModelScope.launch {
            val tweetList =
                homeRepo.getTweets().map {
                    val userId = userList.random().id
                    it.copy(userId = userId)
                }
            _tweets.value = BaseState.Success(tweetList.sortedByDescending {  it.timeStamp })
        }
    }

    fun updateTweet(tweet: Tweet) {
        viewModelScope.launch {
            val tweets = (tweets.value as BaseState.Success).data
            _tweets.value = BaseState.Success(tweets.plus(tweet).sortedByDescending{ it.timeStamp })
        }
    }
}