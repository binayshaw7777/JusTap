package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.binay.shaw.justap.presentation.account.AccountOptions
import com.binay.shaw.justap.presentation.themes.normal16

@Composable
fun OptionItem(
    accountOptions: AccountOptions,
    modifier: Modifier = Modifier,
    onClick: (AccountOptions) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(accountOptions) }
            .padding(16.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(accountOptions.iconId)
                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                .build(), contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = accountOptions.displayName,
            color = MaterialTheme.colorScheme.onSurface,
            style = normal16
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    OptionItem(AccountOptions.EDIT_PROFILE) {
        // Do something
    }
}