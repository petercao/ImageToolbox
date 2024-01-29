/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.feature.erase_background.data

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MlKitBackgroundRemover {

    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param trimEmptyPart After removing the background if its true it will remove the empty part of bitmap. by default its false.
     * @param listener listener for success and failure callback.
     **/
    fun bitmapForProcessing(
        bitmap: Bitmap,
        scope: CoroutineScope,
        listener: suspend (Result<Bitmap>) -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                listener(Result.failure(UnsupportedOperationException("FOSS")))
            }
        }
    }

}