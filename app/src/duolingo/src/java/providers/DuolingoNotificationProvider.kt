package providers

import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.TextView
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerNotificationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.findViewByIdentifier
import org.json.JSONObject
import java.io.File

class DuolingoNotificationProvider: SmartspacerNotificationProvider() {

    companion object {
        private const val PACKAGE_NAME = "com.duolingo"
    }

    override fun onNotificationsChanged(smartspacerId: String, isListenerEnabled: Boolean, notifications: List<StatusBarNotification>) {
        if (!isListenerEnabled) {
            //Smartspacer's notification listener is disabled, ignore it for now
            // TODO: save to file and notify user
            return
        }

//        notifications.forEach {
//            val title = it.notification.extras.toString()
//            val content = "" //it.notification.getContentText()
//            Log.i("pacjodebug", "$title, $content")
//        }

        notifications.forEachIndexed { index, it ->
            val title = it.notification.extras.toString()
            val content = "" //it.notification.getContentText()
            Log.i("pacjodebug", "$title, $content")

            val file = File(context?.filesDir, "${System.currentTimeMillis()}.json")

            // Read JSON
            val jsonObject = JSONObject()

            // add string to file
            jsonObject.put(index.toString(), "$it, ${it.notification}, ${it.notification.extras}")

            // write JSON
            file.writeText(jsonObject.toString())
        }
    }

    // TODO: add dismissing

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            packages = setOf(PACKAGE_NAME)
        )
    }

}