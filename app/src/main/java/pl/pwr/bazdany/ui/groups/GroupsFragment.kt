package pl.pwr.bazdany.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_groups.*
import pl.pwr.bazdany.MainActivity
import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel

class GroupsFragment : Fragment() {

    private lateinit var vm: GroupsViewModel
    private lateinit var navController: NavController
    private lateinit var groupsAdapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm = getViewModel { GroupsViewModel((activity as MainActivity).groupRepo )}

        groupsAdapter = GroupAdapter(vm::joinGroup, vm::leaveGroup)

        with(vm){
            info.observe(viewLifecycleOwner, Observer { onInfo(it) })
            error.observe(viewLifecycleOwner, Observer { onError(it) })
            groups.observe(viewLifecycleOwner, Observer { groupsAdapter.updateGroups(it) })
        }


        groups_recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupsAdapter
        }

        fab.setOnClickListener {
            navController.navigate(R.id.action_navigation_groups_to_navigation_group_create2)
        }
    }

    private fun onInfo(it: String?) {
        it?.let { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() }
    }

    private fun onError(it: String?) {
        it?.let { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() }
    }
}

class GroupAdapter(
    val onGroupJoin: (Long) -> Unit,
    val onGroupLeave: (Long) -> Unit
): RecyclerView.Adapter<GroupAdapter.ViewHolder>(){

    private val groups: MutableList<GroupDomain> = mutableListOf()

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){

        private val memberCount = v.findViewById<TextView>(R.id.group_count)
        private val groupName = v.findViewById<TextView>(R.id.group_name)
        private val groupCity = v.findViewById<TextView>(R.id.group_city)

        private val joinRoot = v.findViewById<ConstraintLayout>(R.id.join_root)
        private val joinButton = v.findViewById<Button>(R.id.join_button)

        private val leaveRoot = v.findViewById<ConstraintLayout>(R.id.leave_root)
        private val leaveButton = v.findViewById<Button>(R.id.leave_button)

        fun bind(group: GroupDomain){
            if(group.isUserIn) {
                joinRoot.visibility = View.INVISIBLE
                leaveRoot.visibility = View.VISIBLE
            }else{
                joinRoot.visibility = View.VISIBLE
                leaveRoot.visibility = View.INVISIBLE
            }

            joinButton.setOnClickListener { onGroupJoin(group.groupId) }
            leaveButton.setOnClickListener { onGroupLeave(group.groupId) }

            groupName.text = group.name
            groupCity.text = group.city
            memberCount.text = group.membersCount
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    fun updateGroups(groupsNew: List<GroupDomain>){
        groups.clear()
        groups.addAll(groupsNew)
        this.notifyDataSetChanged()
    }
}

data class GroupDomain(
    val groupId: Long,
    val name: String,
    val city: String,
    val membersCount: String,
    val isUserIn: Boolean
)