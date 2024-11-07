package com.rai.reelsdemo.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerDefaults

import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.rai.reelsdemo.R
import com.rai.reelsdemo.ui.components.ReelPlayer
import com.rai.reelsdemo.ui.data.Reel
import com.rai.reelsdemo.ui.data.reelsList

import kotlin.math.abs


@Composable
fun ReelsScreens(
    modifier: Modifier = Modifier, reelViewModel: ReelViewModel = hiltViewModel()
) {
    val state = reelViewModel.uiState.collectAsState()
    val reels = state.value.reels.toMutableList()

    val pagerState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f
    ) { reels.size - 1 }

    val fling = PagerDefaults.flingBehavior(
        state = pagerState, snapAnimationSpec = tween(
            easing = LinearEasing, durationMillis = 300
        )
    )
    val effects= reelViewModel.effect.collectAsState(initial = null)
    var isMuted by remember {
        mutableStateOf(false)
    }
    val onLiked = remember {
        { index: Int, liked: Boolean ->
            reels[index] = reels[index].copy(reelInfo = reels[index].reelInfo.copy(isLiked = liked))
        }
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        VerticalPager(
            state = pagerState,
            flingBehavior = fling,
            beyondViewportPageCount = 1,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { index ->
            val shouldPlay by remember(pagerState) {
                derivedStateOf {
                    val isCurrentPage = pagerState.currentPage == index
                    val isTargetPage = pagerState.targetPage == index
                    val isWithinOffset = abs(pagerState.currentPageOffsetFraction) < 0.5

                    (isWithinOffset && isCurrentPage) || (!isWithinOffset && isTargetPage)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                ReelPlayer(reel = reels[index],
                    shouldPlay = shouldPlay,
                    isMuted = isMuted,
                    isScrolling = pagerState.isScrollInProgress,
                    onMuted = {
                        isMuted = it
                    },
                    onDoubleTap = {
                        onLiked(index, it)
                    })
                ReelContainer(reel = reels[index], onReelShare = {
                    reelViewModel.onShareReel(it)
                }, onBackPress = {
                    reelViewModel.onBackPressed()
                }

                )
            }

        }
        effects.value?.let { EventComposer(effect = it) }
    }


}

@Composable
fun ReelContainer(
    modifier: Modifier = Modifier, reel: Reel, onReelShare: (Reel) -> Unit, onBackPress: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ReelHeader(modifier = modifier.align(Alignment.TopCenter), onBackPress = {
            onBackPress()
        })
        Column(modifier = modifier.align(Alignment.BottomCenter)) {
            //ReelSideBar(modifier=modifier.align(Alignment.End)) //Side bar for like share and comment buttons
            ReelBottomBar(reel = reel, onReelShare = {
                onReelShare(it)
            })
        }

    }
}

@Composable
fun ReelHeader(
    modifier: Modifier = Modifier, onBackPress: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaIcon(faIcon = FaIcons.ArrowLeft,
            size = 28.dp,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
                .clickable {
                    onBackPress()
                })
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Player of The Week",
                color = Color.White,
                fontSize = TextUnit(value = 20F, type = TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            Text(text = "UCL POTW TEST 12345", color = Color.Gray)
        }
        Text(
            text = "Closed",
            color = Color.White,
            modifier = modifier
                .background(color = Color.DarkGray, shape = RoundedCornerShape(6.dp))
                .padding(6.dp)
        )
    }

}


@Composable
fun ReelBottomBar(
    modifier: Modifier = Modifier, reel: Reel, onReelShare: (Reel) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp), Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {

            AsyncImage(
                model = reel.reelInfo.profilePicUrl,
                contentDescription = "User Image",
                modifier = modifier
                    .width(60.dp)
                    .height(60.dp),
                error = painterResource(R.drawable.ic_user_holder),
                fallback = painterResource(R.drawable.ic_user_holder),
                placeholder = painterResource(R.drawable.ic_user_holder)
            )

            Text(text = reel.reelInfo.username, fontWeight = FontWeight.Bold)
            Text(text = reel.reelInfo.description.toString(), maxLines = 3)

        }
        FaIcon(faIcon = FaIcons.Share,
            size = 28.dp,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
                .clickable {
                    Log.d("TAG", "ReelBottomBar: clicked ")
                    onReelShare(reel)
                })

    }

}

@Composable
fun ReelPolls(modifier: Modifier,reel: Reel){
    Column {
        //Yet to Implement...
    }
}

@Composable
fun ReelSideBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FaIcon(
            faIcon = FaIcons.Heart,
            size = 20.dp,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
        FaIcon(
            faIcon = FaIcons.Share,
            size = 20.dp,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
        FaIcon(
            faIcon = FaIcons.Comment,
            size = 20.dp,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )


    }

}

@Composable
fun EventComposer(effect:ReelScreenContract.Effect){
    when(effect){
        is ReelScreenContract.Effect.ShareReelEffect ->{
            ShareReel(reel = effect.reel , context = LocalContext.current)
        }
    }

}
@Composable
fun ShareReel(reel: Reel,context: Context){
    val shareReelInfo =StringBuilder()
    shareReelInfo.append("Hey Checkout thi Amazing reel \n")
    shareReelInfo.append(reel.reelInfo.description)
    shareReelInfo.append("-> ${reel.reelUrl} \n")

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareReelInfo.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(context, shareIntent, null)

}
@Preview
@Composable
fun ReelsScreenPreview() {
    ReelContainer(reel = reelsList[1], onBackPress = {}, onReelShare = {} )
}