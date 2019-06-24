/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package leakcanary.internal

import android.app.Activity
import android.app.Application
import leakcanary.RefWatcher
import leakcanary.LeakSentry.Config

internal class ActivityDestroyWatcher private constructor(
  private val refWatcher: RefWatcher,
  private val configProvider: () -> Config
) {

  private val lifecycleCallbacks = object : ActivityLifecycleCallbacksAdapter() {
    override fun onActivityDestroyed(activity: Activity) {
      if (configProvider().watchActivities) {
        refWatcher.watch(activity)
      }
    }
  }

  companion object {
    fun install(
      application: Application,
      refWatcher: RefWatcher,
      configProvider: () -> Config
    ) {
      val activityDestroyWatcher =
        ActivityDestroyWatcher(refWatcher, configProvider)
      application.registerActivityLifecycleCallbacks(activityDestroyWatcher.lifecycleCallbacks) // 注册 Activity 生命周期监听
    }
  }
}
