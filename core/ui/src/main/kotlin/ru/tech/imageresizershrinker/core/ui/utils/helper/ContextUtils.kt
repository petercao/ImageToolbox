/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("SameParameterValue", "KotlinConstantConditions")

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import androidx.core.os.LocaleListCompat
import androidx.documentfile.provider.DocumentFile
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionStatus
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.checkPermissions
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.setPermissionsAllowed
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.ceil


object ContextUtils {

    fun Activity.requestStoragePermission() {
        val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val state = checkPermissions(permissions)
        when (state.permissionStatus.values.first()) {
            PermissionStatus.NOT_GIVEN -> {
                ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    0
                )
            }

            PermissionStatus.DENIED_PERMANENTLY -> {
                askUserToRequestPermissionExplicitly()
                Toast.makeText(this, R.string.grant_permission_manual, Toast.LENGTH_LONG).show()
            }

            PermissionStatus.ALLOWED -> Unit
        }
    }

    fun Context.needToShowStoragePermissionRequest(): Boolean {
        val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val show = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) false
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) false
        else !permissions.all { (this as Activity).hasPermissionAllowed(it) }

        if (!show) setPermissionsAllowed(permissions)

        return show
    }

    fun Context.adjustFontSize(
        scale: Float?
    ): Context {
        val configuration = resources.configuration
        configuration.fontScale = scale ?: resources.configuration.fontScale
        return createConfigurationContext(configuration)
    }

    fun Context.isInstalledFromPlayStore(): Boolean = verifyInstallerId(
        listOf(
            "com.android.vending",
            "com.google.android.feedback"
        )
    )

    private fun Context.verifyInstallerId(
        validInstallers: List<String>
    ): Boolean = validInstallers.contains(getInstallerPackageName(packageName))

    private fun Context.getInstallerPackageName(packageName: String): String? {
        kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                return packageManager.getInstallSourceInfo(packageName).installingPackageName
            @Suppress("DEPRECATION")
            return packageManager.getInstallerPackageName(packageName)
        }
        return null
    }

    fun Context.getFilename(uri: Uri): String? = DocumentFile.fromSingleUri(this, uri)?.name

    fun Context.parseImageFromIntent(
        intent: Intent?,
        onStart: () -> Unit,
        onColdStart: () -> Unit,
        showToast: (message: String, icon: ImageVector) -> Unit,
        navigate: (Screen) -> Unit,
        onGetUris: (List<Uri>) -> Unit,
        onHasExtraImageType: (String) -> Unit,
        notHasUris: Boolean,
        onWantGithubReview: () -> Unit,
        openEditInsteadOfPreview: Boolean
    ) {
        onStart()
        if (intent?.type != null && notHasUris) onColdStart()

        if (intent?.action == Intent.ACTION_BUG_REPORT) {
            onWantGithubReview()
            return
        }

        runCatching {
            val startsWithImage = intent?.type?.startsWith("image/") == true
            val hasJxl = intent?.clipData?.clipList()
                ?.any { it.toString().endsWith(".jxl") } == true
            val dataHasJxl = intent?.data.toString().endsWith(".jxl")

            if ((startsWithImage || hasJxl || dataHasJxl) && intent != null) {
                when (intent.action) {
                    Intent.ACTION_VIEW -> {
                        val data = intent.data
                        val clipData = intent.clipData
                        val uris =
                            clipData?.clipList() ?: data?.let { listOf(it) } ?: return@runCatching

                        if (openEditInsteadOfPreview) {
                            onGetUris(uris)
                        } else {
                            navigate(Screen.ImagePreview(uris))
                        }
                    }

                    Intent.ACTION_SEND -> {
                        intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (intent.getStringExtra("screen") == Screen.PickColorFromImage::class.simpleName) {
                                navigate(Screen.PickColorFromImage(it))
                            } else {
                                if (intent.type?.contains("gif") == true) {
                                    onHasExtraImageType("gif")
                                }
                                onGetUris(listOf(it))
                            }
                        }
                    }

                    Intent.ACTION_SEND_MULTIPLE -> {
                        intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (intent.type?.contains("gif") == true) {
                                onHasExtraImageType("gif")
                                it.firstOrNull()?.let { uri ->
                                    onGetUris(listOf(uri))
                                }
                            } else onGetUris(it)
                        }
                    }

                    else -> {
                        intent.data?.let {
                            if (intent.type?.contains("gif") == true) {
                                onHasExtraImageType("gif")
                            }
                            onGetUris(listOf(it))
                        }
                    }
                }
            } else if (intent?.type != null) {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val multiplePdfs = intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM) != null

                if (
                    intent.type?.contains("pdf") == true && !multiplePdfs
                ) {
                    val uri = intent.data ?: intent.parcelable<Uri>(Intent.EXTRA_STREAM)
                    uri?.let {
                        if (intent.action == Intent.ACTION_VIEW) {
                            navigate(Screen.PdfTools(Screen.PdfTools.Type.Preview(it)))
                        } else {
                            onHasExtraImageType("pdf")
                            onGetUris(listOf(uri))
                        }
                    }
                } else if (text != null) {
                    onHasExtraImageType(text)
                    onGetUris(listOf())
                } else {
                    when (intent.action) {
                        Intent.ACTION_SEND_MULTIPLE -> {
                            intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                                navigate(Screen.Zip(it))
                            }
                        }

                        Intent.ACTION_SEND -> {
                            intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                                onHasExtraImageType("file")
                                onGetUris(listOf(it))
                            }
                        }

                        else -> null
                    } ?: showToast(
                        getString(R.string.unsupported_type, intent.type),
                        Icons.Rounded.ErrorOutline
                    )
                }
            } else Unit
        }.getOrNull() ?: showToast(
            getString(R.string.something_went_wrong),
            Icons.Rounded.ErrorOutline
        )
    }

    val Context.performanceClass: PerformanceClass
        get() {
            val androidVersion = Build.VERSION.SDK_INT
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val memoryClass =
                (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
            var totalCpuFreq = 0
            var freqResolved = 0
            for (i in 0 until cpuCount) {
                runCatching {
                    val reader = RandomAccessFile(
                        String.format(
                            Locale.ENGLISH,
                            "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq",
                            i
                        ), "r"
                    )
                    val line = reader.readLine()
                    if (line != null) {
                        totalCpuFreq += line.toInt() / 1000
                        freqResolved++
                    }
                    reader.close()
                }
            }
            val maxCpuFreq =
                if (freqResolved == 0) -1 else ceil((totalCpuFreq / freqResolved.toFloat()).toDouble())
                    .toInt()

            return if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24) {
                PerformanceClass.Low
            } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 2050 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
                PerformanceClass.Average
            } else {
                PerformanceClass.High
            }
        }

    tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    /** Save a text into the clipboard. */
    fun Context.copyToClipboard(
        label: String,
        value: String
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }

    fun Context.getStringLocalized(
        @StringRes
        resId: Int,
        locale: Locale
    ): String = createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).getText(resId).toString()

    /** Receive the clipboard data. */
    fun Context.pasteColorFromClipboard(
        onPastedColor: (Int) -> Unit,
        onPastedColorFailure: (String) -> Unit,
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()
        text?.let {
            runCatching {
                onPastedColor(android.graphics.Color.parseColor(it))
            }.getOrElse {
                onPastedColorFailure(getString(R.string.clipboard_paste_invalid_color_code))
            }
        } ?: run {
            onPastedColorFailure(getString(R.string.clipboard_paste_invalid_empty))
        }
    }

    fun isMiUi(): Boolean {
        return !getSystemProperty("ro.miui.ui.version.name").isNullOrBlank()
    }

    fun isRedMagic(): Boolean {
        val osName = runCatching {
            System.getProperty("os.name")
        }.getOrNull() ?: getSystemProperty("os.name")
        return listOf("redmagic", "magic", "red").all {
            osName?.contains(it, true) ?: false
        }
    }

    private fun getSystemProperty(name: String): String? {
        return runCatching {
            val p = Runtime.getRuntime().exec("getprop $name")
            BufferedReader(InputStreamReader(p.inputStream), 1024).use {
                return@runCatching it.readLine()
            }
        }.getOrNull()
    }

    fun Context.getLanguages(): Map<String, String> {
        val languages = mutableListOf("" to getString(R.string.system)).apply {
            addAll(
                LocaleConfigCompat(this@getLanguages)
                    .supportedLocales!!.toList()
                    .map {
                        it.toLanguageTag() to it.getDisplayName(it)
                            .replaceFirstChar(Char::uppercase)
                    }
            )
        }

        return languages.let { tags ->
            listOf(tags.first()) + tags.drop(1).sortedBy { it.second }
        }.toMap()
    }

    fun Context.getCurrentLocaleString(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales == LocaleListCompat.getEmptyLocaleList()) {
            return getString(R.string.system)
        }
        return getDisplayName(locales.toLanguageTags())
    }

    private fun getDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }

}