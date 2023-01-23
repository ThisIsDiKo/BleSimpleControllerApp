package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components

import androidx.compose.material.LeadingIconTab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.SettingsTabItem

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabHeader(tabs: List<SettingsTabItem>, pagerState: PagerState){
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabsPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabsPositions)
            )
        }
    ) {
        tabs.forEachIndexed{ index, tab ->
            LeadingIconTab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch{
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {Text(text = tab.title)},
                icon = {}
            )
        }
    }
}