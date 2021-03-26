package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

}