/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.samples.apps.sunflower.adapters.PlantAdapter
import com.google.samples.apps.sunflower.databinding.FragmentPlantListBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel

//recycleview 관련 용어
/*
Adapter: RecyclerView.Adapter의 subclass로 data set에 있는 item을 나타내게하는 view를 제공하는 책임
Position: Adapter내에 있는 data item의 position
Index : getChildAt(int) 호출에 사용하는 attached child view의 index
Binding : adapter내에 position에 관련된 data를 표시하기 위해서 child view를 준비하는 과정
Recycle : 지정한 adapeter position에 대해서 data를 표시하기 위해 이전에 사용한 view를 cache에 넣어서 나중에 다시 동일한 type data를 보여주는데 재사용
Scrap: layout에서 임시로 detached state로 진입하는 child view. rebinding이 필요하지 않은 경우 parent RecyclerVidew에서 완전히 detached하지 않고도 재사용이 가능.
Dirty : 표시하기 전에 adapter로 rebound 해야만 하는 child view
 */
/*
ViewModel
 * Activity가 너무 많은 일을 하니까 이를 나눈다.
 * Activity : UI그리기, 사용자 입력 수신
 * ViewModel : UI Data 보유
 * Repository : app data를 저장 및 로딩
 * Presenter : UI를 위한 data 처리
 */
class PlantListFragment : Fragment() {

    private lateinit var viewModel: PlantListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Fragment와 xml을 binding
        val binding = FragmentPlantListBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        //ViewModelFactory로 ViewModel 얻기
        val factory = InjectorUtils.providePlantListViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(PlantListViewModel::class.java)

        // adapeter를 binding
        val adapter = PlantAdapter()
        binding.plantList.adapter = adapter
        subscribeUi(adapter)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_plant_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_zone -> {
                updateData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeUi(adapter: PlantAdapter) {
        viewModel.plants.observe(viewLifecycleOwner, Observer { plants ->
            if (plants != null) adapter.submitList(plants)
        })
    }

    private fun updateData() {
        with(viewModel) {
            if (isFiltered()) {
                clearGrowZoneNumber()
            } else {
                setGrowZoneNumber(9)
            }
        }
    }
}