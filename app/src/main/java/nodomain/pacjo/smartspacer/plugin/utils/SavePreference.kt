package nodomain.pacjo.smartspacer.plugin.utils

import android.content.Context
import org.json.JSONObject
import java.io.File

@Deprecated("Use Preference DataStore functions instead.")
fun savePreference(context: Context, id: String, value: Any) {
    val file = File(context.filesDir, "data.json")

    // create empty JSONObject if file doesn't exist
    val jsonObject = JSONObject(when (file.exists()) {
        true -> file.readText()
        else -> ""
    })

    val preferencesObject: JSONObject
    if (jsonObject.has("preferences")) {
        preferencesObject = jsonObject.getJSONObject("preferences")
    } else {
        preferencesObject = JSONObject()
        jsonObject.put("preferences", preferencesObject)
    }

    preferencesObject.put(id, value)

    file.writeText(jsonObject.toString())
}
