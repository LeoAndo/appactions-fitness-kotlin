/*
 * Copyright 2019 Google LLC
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
 *
 */

package com.devrel.android.fitactions.slices

import android.net.Uri
import androidx.slice.Slice
import androidx.slice.SliceProvider
import com.devrel.android.fitactions.DeepLink
import com.devrel.android.fitactions.model.FitRepository


/**
 * SliceProvider implementation that handles the different kind of Uri, creating and
 * holding a FitSlice instance implementation depending on the type of URI
 *
 * This SliceProvider is defined inside the AndroidManifest.
 */
class FitSliceProvider : SliceProvider() {

    /**
     * Keep track of the last created slice so when the slice calls "refresh"
     * we don't create it again.
     */
    private var lastSlice: FitSlice? = null

    override fun onBindSlice(sliceUri: Uri): Slice {
        // When a new request is send to the SliceProvider of this app, this method is called
        // with the given slice URI.
        // Here you could directly handle the uri and create a new slice. But in order to make
        // the slice dynamic and better structured, we use the FitSlice class.
        // Then we check if the new slice uri is the same as the current created slice (if any).
        // If there was none or the uri changed, we create a new instance of FitSlice and return
        // the Slice instance.
        if (lastSlice?.sliceUri != sliceUri) {
            lastSlice = createNewSlice(sliceUri)
        }
        return checkNotNull(lastSlice).getSlice()
    }

    override fun onCreateSliceProvider(): Boolean {
        // TODO grant permissions to assistant package.
        return true
    }

    /**
     * Given the sliceUri create a matching FitSlice instance.
     */
    private fun createNewSlice(sliceUri: Uri): FitSlice {
        val context = requireNotNull(context) {
            "SliceProvider $this not attached to a context."
        }
        return when (sliceUri.path) {

            DeepLink.STATS -> FitStatsSlice(
                context = context,
                sliceUri = sliceUri,
                fitRepo = FitRepository.getInstance(context)
            )
            else -> FitSlice.Unknown(context, sliceUri)
        }
    }
}