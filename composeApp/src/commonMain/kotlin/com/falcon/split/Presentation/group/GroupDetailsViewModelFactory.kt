//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.falcon.split.Presentation.group.GroupDetailsViewModel
//import com.falcon.split.data.Repository.ExpenseRepository
//import com.falcon.split.data.Repository.GroupRepository
//
//class GroupDetailsViewModelFactory(
//    private val groupRepository: GroupRepository,
//    private val expenseRepository: ExpenseRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(GroupDetailsViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return GroupDetailsViewModel(groupRepository, expenseRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}