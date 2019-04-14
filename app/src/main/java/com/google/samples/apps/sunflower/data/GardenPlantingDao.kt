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

package com.google.samples.apps.sunflower.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/*
data를 보유하는 class로 observable 속성을 가짐.
LiveData vs. 일반 observable 차이
 * lifecycle 인지 : activity, fragment, service와 같은 app component의 lifecycle과 연동
 * active lifecycle 상태에서만 update를 통보한다.
 * LifecycleOwner와 함께 사용해서 DESTROYED로 변경되는 경우 subscribe를 하지 않음 -> memory leak 방지

장점 :
 * UI와 data 상태 매칭
 * memory leak 방지
 * 정지된 activity에 의한 crash 방지
 * 수동으로 lifecycle 처리하지 않아도 됨
 * 항상 최신 data로 업데이트
 * activity 새로 생성되는 경우에도 최신 data로 업데이트
 * resource 공유
*/

/**
 * The Data Access Object for the [GardenPlanting] class.
 */
@Dao
interface GardenPlantingDao {
    @Query("SELECT * FROM garden_plantings")
    fun getGardenPlantings(): LiveData<List<GardenPlanting>>

    @Query("SELECT * FROM garden_plantings WHERE id = :gardenPlantingId")
    fun getGardenPlanting(gardenPlantingId: Long): LiveData<GardenPlanting>

    @Query("SELECT * FROM garden_plantings WHERE plant_id = :plantId")
    fun getGardenPlantingForPlant(plantId: String): LiveData<GardenPlanting>

    /**
     * This query will tell Room to query both the [Plant] and [GardenPlanting] tables and handle
     * the object mapping.
     */
    @Transaction
    @Query("SELECT * FROM plants")
    fun getPlantAndGardenPlantings(): LiveData<List<PlantAndGardenPlantings>>

    @Insert
    fun insertGardenPlanting(gardenPlanting: GardenPlanting): Long

    @Delete
    fun deleteGardenPlanting(gardenPlanting: GardenPlanting)
}