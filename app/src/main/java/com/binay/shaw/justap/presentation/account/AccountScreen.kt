package com.binay.shaw.justap.presentation.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.binay.shaw.justap.BuildConfig
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.components.OptionItem
import com.binay.shaw.justap.presentation.components.ThemeDialog
import com.binay.shaw.justap.presentation.themes.medium14
import com.binay.shaw.justap.presentation.themes.medium16
import com.binay.shaw.justap.presentation.themes.normal14
import com.binay.shaw.justap.presentation.themes.normal16
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.rememberReviewTask
import com.google.android.play.core.review.ReviewManagerFactory

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier
) {

    var openThemeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val reviewManager = remember {
        ReviewManagerFactory.create(context)
    }

    var openLogoutDialog by remember { mutableStateOf(false) }

    var reviewInfo = rememberReviewTask(reviewManager)

    LaunchedEffect(reviewInfo) {
        reviewInfo?.let {
            reviewManager.launchReviewFlow(context as Activity, it)
        }
    }

    when {
        openThemeDialog -> {
            ThemeDialog(onDismissRequest = { openThemeDialog = false })
        }

        openLogoutDialog -> {
            AlertDialog(
                onDismissRequest = { openLogoutDialog = false },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                title = {
                    Text(
                        text = "Logout?",
                        style = normal16.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout?",
                        style = normal16.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Util.clearDataAndLogout(scope, context)
                            openLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            "Logout",
                            style = medium14
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            stringResource(id = R.string.cancel),
                            style = medium14
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .then(modifier)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.aboutme_pfp)
                    .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                    .build(), contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Column {
                Text(text = "John Doe", style = medium16)
                Text(
                    text = "johndoe123@gmail.com",
                    style = normal14.copy(color = MaterialTheme.colorScheme.surfaceTint)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Account",
                    style = medium14.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, bottom = 18.dp)
                )
            }
            items(AccountOptions.entries.subList(0, 3)) {
                OptionItem(it) { onClickOption ->
                    when (onClickOption) {
                        AccountOptions.EDIT_PROFILE -> {

                        }

                        AccountOptions.CUSTOMIZE_QR -> {

                        }

                        AccountOptions.THEME -> {
                            openThemeDialog = true
                        }

                        else -> {}
                    }
                }
            }
            item {
                Text(
                    text = "General",
                    style = medium14.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, bottom = 18.dp)
                )
            }
            items(AccountOptions.entries.subList(3, AccountOptions.entries.size)) {
                OptionItem(it) { onClickOption ->
                    when (onClickOption) {
                        AccountOptions.INVITE_FRIENDS -> {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, Constants.inviteFriendsMessage)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }

                        AccountOptions.LANGUAGE -> {

                        }

                        AccountOptions.PRIVACY_POLICY -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(Constants.privacyPolicyUrl)
                            context.startActivity(intent)
                        }

                        AccountOptions.RATE_US -> {
//                            reviewManager.requestReviewFlow().addOnCompleteListener { reviewTask ->
//                                if (reviewTask.isSuccessful) {
//                                    reviewInfo = reviewTask.result
//                                }
//                            }
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(Constants.APP_URL)
                            context.startActivity(intent)
                        }

                        AccountOptions.HELP_AND_SUPPORT -> {
                            val openURL = Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(context.resources.getString(R.string.mailTo))
                            context.startActivity(openURL)
                        }

                        AccountOptions.LOGOUT -> {
                            openLogoutDialog = true
                        }

                        else -> {}
                    }
                }
            }
            item {
                Text(
                    text = "App Version: ${BuildConfig.VERSION_NAME}",
                    style = normal14.copy(color = MaterialTheme.colorScheme.surfaceTint)
                )
            }
        }
    }
}


@Preview
@Composable
private fun AccountScreenPreview() {
    AccountScreen()
}