package com.falcon.split.domain.useCase.group

import com.falcon.split.data.Repository.GroupRepository
import com.falcon.split.data.network.models_app.Group

class CreateGroupUseCase(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(name: String, members: List<String>): Result<Group> =
        groupRepository.createGroup(name, members)
}