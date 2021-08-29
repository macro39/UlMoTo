package com.example.ulmoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ulmoto.FilterBy
import com.example.ulmoto.db.AppDatabase
import com.example.ulmoto.db.models.Record
import com.example.ulmoto.db.models.RecordWithRepairs
import com.example.ulmoto.db.models.Repair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    private val _records = Channel<List<Record>>(Channel.BUFFERED)
    val records = _records.receiveAsFlow()

    private val _selectedRecord = Channel<RecordWithRepairs>(Channel.BUFFERED)
    val selectedRecord = _selectedRecord.receiveAsFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            db.recordDao().getAll()
                .collect {
                    _records.send(it)
                }
        }
    }

    fun selectRecord(recordId: Long) {
        viewModelScope.launch {
            db.recordWithRepairsDao().getRecordWithRepairs(recordId)
                .collect {
                    _selectedRecord.send(it)
                }
        }
    }

    fun filter(filterBy: FilterBy, string: String) {
        viewModelScope.launch {
            val flow =
                when (filterBy) {
                    FilterBy.FIRST_NAME -> {
                        db.recordDao().getAllByFirstName(string)
                    }
                    FilterBy.LAST_NAME -> {
                        db.recordDao().getAllByLastName(string)
                    }
                    FilterBy.LICENCE_PLATE -> {
                        db.recordDao().getAllByLicencePlate(string)
                    }
                }

            flow
                .collect {
                    _records.send(it)
                }
        }
    }

    fun delete(recordWithRepairs: RecordWithRepairs) {
        viewModelScope.launch(Dispatchers.IO) {
            db.recordWithRepairsDao().delete(recordWithRepairs.record, recordWithRepairs.repairList)
        }
    }

    fun delete(repair: Repair) {
        viewModelScope.launch(Dispatchers.IO) {
            db.repairDao().delete(repair)
        }
    }

    suspend fun checkLicencePlateAlreadyExists(licencePlate: String): Boolean {
        return db.recordDao().existsByLicencePlate(licencePlate)
    }

    fun upsertRecord(record: Record) = viewModelScope.launch(Dispatchers.IO) {
        db.recordDao().upsert(record)
    }

    fun upsertRepair(repair: Repair) = viewModelScope.launch(Dispatchers.IO) {
        db.repairDao().upsert(repair)
    }

}