package com.yara.ai.notify

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.yara.ai.YaraApp
import com.yara.ai.ui.MainActivity
import kotlin.random.Random

/** Posts Yara chat messages as interactive notifications with inline quick-reply. */
object Notifier {
    private const val KEY_REPLY = "yara_reply"

    fun notifyYara(ctx: Context, text: String) {
        val replyIntent = Intent(ctx, YaraQuickReplyReceiver::class.java)
        val replyPending = PendingIntent.getBroadcast(
            ctx, Random.nextInt(), replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val remoteInput = RemoteInput.Builder(KEY_REPLY).setLabel("Reply to Yara").build()
        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add, "Reply", replyPending
        ).addRemoteInput(remoteInput).build()

        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val n: Notification = NotificationCompat.Builder(ctx, YaraApp.CHANNEL_CHAT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Yara 💛")
            .setContentText(text)
            .setContentIntent(open)
            .setAutoCancel(true)
            .addAction(action)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(ctx).notify(Random.nextInt(10000, 99999), n)
    }
}

/** Receives inline replies typed in the notification shade. */
class YaraQuickReplyReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val bundle = androidx.core.app.RemoteInput.getResultsFromIntent(intent) ?: return
        val text = bundle.getCharSequence("yara_reply")?.toString() ?: return
        // Launch app with the text to process
        val i = Intent(ctx, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("quick_reply", text)
        }
        ctx.startActivity(i)
    }
}

/** Restart listener after reboot. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(ctx, com.yara.ai.voice.YaraService::class.java)
            androidx.core.content.ContextCompat.startForegroundService(ctx, i)
        }
    }
}
