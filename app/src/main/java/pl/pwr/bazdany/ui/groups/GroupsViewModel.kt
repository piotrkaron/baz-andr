package pl.pwr.bazdany.ui.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.BaseSource
import pl.pwr.bazdany.Effect
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

class GroupsViewModel(
    private val repo: GroupRepository
) : ViewModel() {

    private val _groups = MutableLiveData<List<GroupDomain>>(listOf())
    val groups: LiveData<List<GroupDomain>> get() = _groups

    private val _info = MutableLiveData<String?>(null)
    val info: LiveData<String?> get() = _info

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    init {
        getGroups()
    }

    private fun getGroups() {
        viewModelScope.launch {
            val res = repo.getGroups()

            when (res) {
                is Effect.Success -> _groups.postValue(res.data)
                is Effect.Error -> _error.postValue(res.message)
            }
        }
    }

    fun leaveGroup(id: Long) {

    }

    fun joinGroup(id: Long) {
        viewModelScope.launch {
            val res = repo.joinGroup(id)

            when (res) {
                is Effect.Success -> _info.postValue(res.data)
                is Effect.Error -> _error.postValue(res.message)
            }

            getGroups()
        }
    }
}

interface GroupApi{

    @GET("/api/groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @POST("/api/group")
    suspend fun createGroup(@Body dto: CreateGroupRequest): Response<GroupCreatedResponse>

    @PUT("/api/group")
    suspend fun joinGroup(@Body dto: JoinGroupRequest): Response<Boolean>
}

class GroupRepository(private val api: GroupApi): BaseSource(){

    suspend fun getGroups(): Effect<List<GroupDomain>>{
        val res = executeRequest { api.getGroups() }

        return when(res){
            is Effect.Success -> Effect.Success(res.data.map(GroupDto::toDomain))
            is Effect.Error -> Effect.Error("Błąd pobierania grup")
        }
    }

    suspend fun createGroup(name: String, city: String): Effect<String>{
        val res = executeRequest { api.createGroup(CreateGroupRequest(name, city)) }

        return when(res){
            is Effect.Success -> Effect.Success("Utworzono grupę")
            is Effect.Error -> Effect.Error("Błąd tworzenia grupy")
        }
    }

    suspend fun joinGroup(id: Long): Effect<String>{
        val res = executeRequest { api.joinGroup(JoinGroupRequest(id)) }

        return when(res){
            is Effect.Success -> Effect.Success("Dołączono do grupy")
            is Effect.Error -> Effect.Error("Błąd dołączania")
        }
    }

}

data class GroupDto(
    val name: String,
    val city: String,
    //@JsonProperty("dd-MM-yyyy-HH:mm:ss")
    val created: String,
    val membersCount: Int,
    val isUserIn: Boolean,
    val id: Long
)

data class CreateGroupRequest(
    val name: String,
    val city: String
)

data class GroupCreatedResponse(
    val groupId: Long
)

data class JoinGroupRequest(
    val groupId: Long
)

fun GroupDto.toDomain() = GroupDomain(
    id, name, city, membersCount.toString(), isUserIn
)