package receivers

import android.content.Context
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.receivers.SmartspacerTargetUpdateReceiver
import targets.LivelyGreetingTarget

class LivelyGreetingTargetUpdateReceiver: SmartspacerTargetUpdateReceiver() {

    override fun onRequestSmartspaceTargetUpdate(
        context: Context,
        requestTargets: List<RequestTarget>
    ) {
        // just let smartspacer know update is available and when
        // getSmartspaceTargets gets called new data will be given
        SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
    }
}