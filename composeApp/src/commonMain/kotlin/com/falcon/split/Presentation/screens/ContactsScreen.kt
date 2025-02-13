import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.falcon.split.contact.ContactInfo
import com.falcon.split.contact.ContactManager

// shared/src/commonMain/kotlin/com/falcon/split/screens/ContactPickerScreen.kt
@Composable
fun ContactPicker(
    contactManager: ContactManager,
    onContactPicked: (ContactInfo?) -> Unit
) {
    var showPermissionRequest by remember { mutableStateOf(false) }

    if (!contactManager.hasPermission()) {
        if (!showPermissionRequest) {
            showPermissionRequest = true
            contactManager.requestPermission { granted ->
                if (granted) {
                    contactManager.pickContact(onContactPicked)
                }
            }
        }
    } else {
        contactManager.pickContact(onContactPicked)
    }
}