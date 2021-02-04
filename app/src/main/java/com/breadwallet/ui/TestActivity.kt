/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/12/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.base.AtmResult
import cash.just.override.CoinsquareConstants
import cash.just.ui.CashUI
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.breadwallet.BuildConfig
import com.breadwallet.R
import com.breadwallet.app.BreadApp
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.breadbox.TransferSpeed
import com.breadwallet.breadbox.feeForSpeed
import com.breadwallet.crypto.Wallet
import com.breadwallet.legacy.presenter.entities.CryptoRequest
import com.breadwallet.logger.logDebug
import com.breadwallet.tools.animation.BRDialog
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.BrdUserState
import com.breadwallet.tools.util.BRConstants
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.tools.util.Link
import com.breadwallet.tools.util.Utils
import com.breadwallet.ui.auth.AuthenticationController
import com.breadwallet.ui.disabled.DisabledController
import com.breadwallet.ui.keystore.KeyStoreController
import com.breadwallet.ui.login.LoginController
import com.breadwallet.ui.migrate.MigrateController
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.navigation.RouterNavigator
import com.breadwallet.ui.onboarding.IntroController
import com.breadwallet.ui.onboarding.OnBoardingController
import com.breadwallet.ui.pin.InputPinController
import com.breadwallet.ui.recovery.RecoveryKey
import com.breadwallet.ui.recovery.RecoveryKeyController
import com.breadwallet.ui.send.SendSheetController
import com.breadwallet.util.ControllerTrackingListener
import com.breadwallet.util.errorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class TestActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val userManager by instance<BrdUserManager>()
    private val breadBox by instance<BreadBox>()

    lateinit var router: Router
    private var trackingListener: ControllerTrackingListener? = null

    private val resumedScope = CoroutineScope(
        Default + SupervisorJob() + errorHandler("resumedScope")
    )


    @Suppress("ComplexMethod", "LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The view of this activity is nothing more than a Controller host with animation support
        setContentView(R.layout.test_activity)
        resumedScope.launch {
            getWallet()
        }
    }

    private suspend fun getWallet() {
        breadBox.wallet("BTC").first()
            .feeForSpeed(TransferSpeed.Regular("BTC"))
    }

    override fun onDestroy() {
        super.onDestroy()
        trackingListener?.run(router::removeChangeListener)
        trackingListener = null
        resumedScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        BreadApp.setBreadContext(this)
    }

    override fun onPause() {
        super.onPause()
        BreadApp.setBreadContext(null)
        resumedScope.coroutineContext.cancelChildren()
    }

    override fun onBackPressed() {
        // Defer to controller back-press control before exiting.
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
