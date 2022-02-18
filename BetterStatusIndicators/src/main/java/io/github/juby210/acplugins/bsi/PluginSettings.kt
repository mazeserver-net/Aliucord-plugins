/*
 * Copyright (c) 2021-2022 Juby210
 * Licensed under the Open Software License version 3.0
 */

package io.github.juby210.acplugins.bsi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.views.Divider
import com.aliucord.widgets.LinearLayout
import com.discord.app.AppBottomSheet
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R
import io.github.juby210.acplugins.BetterStatusIndicators

class PluginSettings(private val plugin: BetterStatusIndicators) : AppBottomSheet() {
    override fun getContentViewResId() = 0

    private var avatarStatus: CheckedSetting? = null
    private var filledColors: CheckedSetting? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val context = inflater.context

        return LinearLayout(context).apply {
            setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary))

            addView(createSwitch(
                context,
                "avatarStatus",
                "Avatar Status",
                "Show first platform on avatar.",
                default = true
            ) {
                if (it) {
                    plugin.settings.setBool("filledColors", false)
                    filledColors?.isChecked = false
                }
                plugin.patchStatusView(resources)
            }.also { avatarStatus = it })
            addView(createSwitch(
                context,
                "filledColors",
                "Filled Colors",
                "Use filled circles for avatar status. Incompatible with Avatar Status setting.",
            ) {
                if (it) {
                    plugin.settings.setBool("avatarStatus", false)
                    avatarStatus?.isChecked = false
                }
                plugin.patchStatusView(resources)
            }.also { filledColors = it })
            addView(createSwitch(
                context,
                "chatStatus",
                "Chat Status",
                "Show little status circles in chat next to the username.",
                true
            ) { plugin.patchChatStatus() })
            addView(createSwitch(
                context,
                "chatStatusPlatforms",
                "Chat Status Platforms",
                "Show platforms in chat next to the username."
            ) { plugin.patchChatStatusPlatforms() })
            // addView(createSwitch(
            //     context,
            //     "voiceStatus",
            //     "Voice Status",
            //     "Shows the status ring around the speaking user based on their status instead of only green.",
            //     true
            // ))
            addView(createSwitch(
                context,
                "radialStatus",
                "Radial Status",
                "Shows a status ring around the user avatar."
            ) {
                plugin.patchRadialStatus(it, isPluginEnabled("SquareAvatars"))
                Utils.promptRestart()
            })

            addView(Divider(context))
            addView(TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                typeface = ResourcesCompat.getFont(context, Constants.Fonts.whitney_semibold)
                text = "Colors"
            })
            plugin.settings.run {
                addView(ColorView(
                    context,
                    this,
                    "colorOnline",
                    "Online Color",
                    resources.getColor(R.c.status_green_600, null) - 1
                ))
                addView(ColorView(
                    context,
                    this,
                    "colorIdle",
                    "Idle Color",
                    resources.getColor(R.c.status_yellow, null) - 1
                ))
                addView(ColorView(
                    context,
                    this,
                    "colorDND",
                    "DND Color",
                    resources.getColor(R.c.status_red, null) - 1
                ))
            }
        }
    }

    private fun createSwitch(
        context: Context,
        key: String,
        label: String,
        subtext: String,
        default: Boolean = false,
        onClick: (Boolean) -> Unit
    ) = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext).apply {
        isChecked = plugin.settings.getBool(key, default)
        setOnCheckedListener {
            plugin.settings.setBool(key, it)
            onClick(it)
        }
    }
}
