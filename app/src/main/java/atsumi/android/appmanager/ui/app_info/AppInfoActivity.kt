package jp.bizen.app.minimalist.ui.app_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.bizen.app.minimalist.R
import jp.bizen.app.minimalist.entity.AppInfo
import jp.bizen.app.minimalist.entity.AppType
import jp.bizen.app.minimalist.ui.theme.MinimalistTheme

class AppInfoActivity : ComponentActivity() {
    
    private val viewModel: AppInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinimalistTheme {
                AppInfoScreen(
                    viewModel = viewModel,
                    onUninstallApp = { appInfo ->
                        showUnInstallConfirmDialog(appInfo)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }

    private fun showUnInstallConfirmDialog(appInfo: AppInfo) {
        val uri = Uri.fromParts("package", appInfo.packageName, null)
        startActivity(Intent(Intent.ACTION_DELETE, uri))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen(
    viewModel: AppInfoViewModel,
    onUninstallApp: (AppInfo) -> Unit
) {
    val appList by viewModel.appList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    var selectedFilter by remember { mutableStateOf(FilterType.MANUALLY) }
    
    // エラーメッセージ表示
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Toast相当の処理は省略（SnackbarHostで対応可能）
        }
    }
    
    val filteredAppList = remember(appList, selectedFilter) {
        when (selectedFilter) {
            FilterType.ALL -> appList
            FilterType.MANUALLY -> appList.filter { it.appType == AppType.MANUALLY }
            FilterType.OVER_26 -> appList.filter { it.targetSdkVersion > 25 }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minimalist") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // フィルタードロップダウン
            FilterDropdown(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // メインコンテンツ
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredAppList) { appInfo ->
                            AppInfoCard(
                                appInfo = appInfo,
                                onUninstallClick = { onUninstallApp(appInfo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    selectedFilter: FilterType,
    onFilterChanged: (FilterType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = stringResource(selectedFilter.labelRes),
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FilterType.values().forEach { filter ->
                DropdownMenuItem(
                    text = { Text(stringResource(filter.labelRes)) },
                    onClick = {
                        onFilterChanged(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoCard(
    appInfo: AppInfo,
    onUninstallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // パッケージ名
            Text(
                text = appInfo.packageName,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // アプリ情報行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アプリアイコン
                AppIcon(
                    appInfo = appInfo,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // アプリ詳細
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appInfo.appName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "Min SDK: ${appInfo.minSdkVersionText}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "Target SDK: ${appInfo.targetSdkVersion}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // アンインストールボタン
                TextButton(
                    onClick = onUninstallClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("削除")
                }
            }
        }
    }
}

@Composable
fun AppIcon(
    appInfo: AppInfo,
    modifier: Modifier = Modifier
) {
    val appIcon = appInfo.appIcon
    
    val imageBitmap = remember(appIcon) {
        try {
            appIcon?.toBitmap()?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "${appInfo.appName} icon",
            modifier = modifier
        )
    } else {
        // デフォルトアイコン
        Image(
            painter = painterResource(android.R.drawable.ic_menu_gallery),
            contentDescription = "Default app icon",
            modifier = modifier
        )
    }
}

enum class FilterType(val labelRes: Int) {
    MANUALLY(R.string.spinner_manually),
    ALL(R.string.spinner_all),
    OVER_26(R.string.spinner_over_26)
}

@Preview(showBackground = true)
@Composable
fun AppInfoScreenPreview() {
    MinimalistTheme {
        // プレビュー用の空のスクリーン
        Text("App Info Screen Preview")
    }
}