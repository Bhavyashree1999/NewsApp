package com.example.newsheadlines

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsheadlines.service.Article
import com.example.newsheadlines.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun NewsHeadlinesScreen(viewModel: NewsViewModel) {
    val newsHeadlines by viewModel.newsHeadlines
    val isLoading by viewModel.isLoading

    var isLongPressActive by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }
    val focusRequester = remember { FocusRequester() }

    // Fetch news when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchNews()
    }

    // CoroutineScope for handling long press logic
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP,
                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT
                    -> {
                        when (event.nativeKeyEvent.action) {
                            KeyEvent.ACTION_DOWN -> {
                                pressStartTime = System.currentTimeMillis()
                                isLongPressActive = true
                                // Start a coroutine to check for long press duration
                                scope.launch {
                                    delay(500)
                                    if (isLongPressActive && (System.currentTimeMillis() - pressStartTime) >= 500) {
                                        viewModel.fetchNews()
                                        isLongPressActive = false // Reset after refresh
                                    }
                                }
                            }

                            KeyEvent.ACTION_UP -> {
                                isLongPressActive = true
                                Log.e("TESTING", "Key Up: $isLongPressActive")
                            }
                        }
                        true
                    }

                    else -> false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(60.dp),
                color = Color.Blue
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(colorResource(id = R.color.teal_blue)),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = stringResource(R.string.hindustan_times),
                color = Color.White,
                style = MaterialTheme.typography.h1.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(newsHeadlines) { article ->
                Column {
                    NewsItem(article = article)
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    // Request focus when the composable is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
fun NewsItem(article: Article) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxHeight(0.60f), verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display title

        Text(
            text = article.title,
            color = Color.Black,
            style = MaterialTheme.typography.h2.copy(
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        // Display "Read more" link as a clickable URL
        article.url?.let { url ->
            Text(
                text = "Read more",
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.Blue,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display source name
            article.source?.name?.let {
                Text(
                    text = it,
                    color = Color.Black.copy(0.5f),
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),

                    )
            }

            // Display author
            article.author?.let {
                Text(
                    text = it,
                    color = Color.Black.copy(0.5f),
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),
                )
            }

            // Display description
            article.description?.let {
                Text(
                    text = it,
                    color = Color.Black.copy(0.5f),
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),
                )
            }

            // Display publishedAt
            article.publishedAt?.let {
                Text(
                    text = it,
                    color = Color.Black.copy(0.5f),
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),

                    )
            }

            // Display content
            article.content?.let {
                Text(
                    text = it,
                    color = Color.Black.copy(0.5f),
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }


    }
    Spacer(modifier = Modifier.height(10.dp))
}








