package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import ru.dikoresearch.blesimplecontrollerapp.R
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ApplicationSettings
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components.TabHeader
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.subscreens.ApplicationSettingsScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.subscreens.ControllerSettingsScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenViewModel
import ru.dikoresearch.blesimplecontrollerapp.ui.theme.BleSimpleControllerAppTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SettingsScreen(
    navController: NavController
){
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val lifeCycleOwner = LocalLifecycleOwner.current
    val pagerState = rememberPagerState()
    val scaffoldState = rememberScaffoldState()

    val screenState = viewModel.state.collectAsState()
    val appSettingsState = viewModel.appSettingsState.collectAsState()
    val controllerSettings = viewModel.controllerSettingsState.collectAsState()

    val tabs = listOf(
        SettingsTabItem.ApplicationSettings,
        SettingsTabItem.ControllerSettings
    )

    LaunchedEffect(key1 = Unit){
        lifeCycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.uiEvent.collectLatest { event->
                when(event){
                    is StartScreenUIEvent.Navigate -> {
                        Log.e("", "Navigation event in ui handler")
                        navController.navigate(event.destination.url, event.options)
                    }
                    is StartScreenUIEvent.ShowToast -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if(screenState.value.showDeleteConfirmationDialog){
            Dialog(
                onDismissRequest = {
                    viewModel.hideDeleteConfirmationDialog()
                }
            ){
                Card(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Clear device info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Delete device info and go to scan screen",
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ){
                            TextButton(onClick = {viewModel.hideDeleteConfirmationDialog()}) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            TextButton(onClick = {viewModel.deleteAllSettings()}) {
                                Text(
                                    text = "Clear",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabHeader(tabs = tabs, pagerState = pagerState)
                HorizontalPager(count = tabs.size, state = pagerState){ page ->
                    when(tabs[page]){
                        is SettingsTabItem.ApplicationSettings -> {
                            ApplicationSettingsScreen(
                                state = appSettingsState.value,
                                paddingValues = PaddingValues(10.dp, 4.dp),
                                onControlSchemeChanged = { viewModel.controlSchemeChanged(it) },
                                onPressureSensorChanged = { viewModel.pressureSensorChanged(it) },
                                onPressureUnitsChanged = { viewModel.pressureUnitsChanges(it) },
                                onShowPressureTankChanged = { viewModel.showTankPressure(it) },
                                onShowControlsChanged = { viewModel.showControls(it) },
                                onShowPressureRegulationChanged = { viewModel.showPressureRegulation(it) }
                            )
                        }
                        is SettingsTabItem.ControllerSettings -> {
                            ControllerSettingsScreen(
                                state = controllerSettings.value,
                                onDeleteControllerClicked = { viewModel.showDeleteConfirmationDialog() }
                            )
                        }
                    }
                }
            }
        }

    }
}





















