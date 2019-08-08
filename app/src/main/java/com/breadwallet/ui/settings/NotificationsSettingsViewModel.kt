/**
 * BreadWallet
 * <p/>
 * Created by Pablo Budelli on <pablo.budelli@breadwallet.com> 7/11/19.
 * Copyright (c) 2019 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.breadwallet.repository.NotificationsState
import com.breadwallet.repository.PushNotificationsSettingsRepositoryImpl
import com.breadwallet.tools.threads.executor.BRExecutor

class NotificationsSettingsViewModel : ViewModel() {

    val notificationsEnable = MutableLiveData<NotificationsState>()

    fun togglePushNotifications(enable: Boolean) {
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute {
            PushNotificationsSettingsRepositoryImpl.togglePushNotifications(enable)
            notificationsEnable.postValue(PushNotificationsSettingsRepositoryImpl.getNotificationsState())
        }
    }

    /**
     * Check what is the current state of the notification settings. This is ntended to be called
     * when we return to the settings activity to verify if the notifications are enabled on the
     * OS settings.
     */
    fun refreshState() {
        notificationsEnable.value = PushNotificationsSettingsRepositoryImpl.getNotificationsState()
    }

}