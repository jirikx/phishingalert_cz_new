/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Part of the used code in this file was taken from:
 * https://github.com/GoogleCloudPlatform/java-docs-samples/blob/main/webrisk/src/main/java/webrisk/SubmitUri.java
 * The rest is the creation of the author of this program.
 */

package cz.phishingalert.core.controllers.admin.reporting

import com.google.api.gax.rpc.UnimplementedException
import com.google.cloud.webrisk.v1.WebRiskServiceClient
import com.google.webrisk.v1.Submission
import com.google.webrisk.v1.SubmitUriRequest
import com.google.webrisk.v1.ThreatInfo
import com.google.longrunning.Operation
import cz.phishingalert.core.RepositoryService
import cz.phishingalert.core.configuration.CoreConfig
import io.grpc.StatusRuntimeException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

@Controller
@RequestMapping(path = ["/admin"])
class WebRiskController(
    val repositoryService: RepositoryService,
    val config: CoreConfig
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Sends the info about given accident identified by [accidentId] to Google WebRisk Submit API
     * ATTENTION: This method does not work if you don't have the WebRisk API correctly set in the Google Cloud settings
     */
    @GetMapping(path = ["/submit/google/{accidentId}"])
    fun sendAccidentToAuthority(@PathVariable accidentId: Int): ModelAndView {
        // Try to get URL corresponding to the accidentId
        val uri = repositoryService.readAccidentById(accidentId)?.url?.toString()
            ?: return ModelAndView("error/404", HttpStatus.NOT_FOUND)

        val webRiskServiceClient = WebRiskServiceClient.create()
        try {
            // Build the Submission object
            val submission = Submission.newBuilder().setUri(uri).build()

            val threatInfo = ThreatInfo.newBuilder()
                .setAbuseType(ThreatInfo.AbuseType.SOCIAL_ENGINEERING)
                .setThreatJustification(ThreatInfo.ThreatJustification.newBuilder()
                    .addComments(repositoryService.readAccidentById(accidentId)?.noteText ?: "-")
                    .build()
                )
                .build()

            val submitUriRequest = SubmitUriRequest.newBuilder()
                .setParent("projects/${config.googleCloudProjectId}")
                .setSubmission(submission)
                .setThreatInfo(threatInfo)
                .build()

            try {
                // Try to submit the URL to the Google WebRisk
                val submissionResponse: Operation = webRiskServiceClient.submitUriCallable()
                    .futureCall(submitUriRequest).get(config.googleCloudConnectionTimeout, TimeUnit.SECONDS)
                logger.info("Response from Google: $submissionResponse")
                return ModelAndView("success")
            } catch (ex: Exception) {
                throw ex.cause ?: ex // When the future call fails, the real cause is hidden inside the thrown exception
            }
        } catch (ex: UnimplementedException) {
            // Handle the case when the Google Cloud doesn't allow the user to use its API method
            logger.error("Google WebRisk didn't allow you to submit the URL=$uri")
            val response = ModelAndView("error/errorPage", HttpStatus.FAILED_DEPENDENCY)
            response.addObject(
                "message",
                "Called API method is not implemented or allowed on the side of Google WebRisk."
            )
            return response
        } catch (ex: Exception) {
            // Handle unknown error
            logger.error(ex.toString())
            val response = ModelAndView("error/errorPage", HttpStatus.INTERNAL_SERVER_ERROR)
            response.addObject("message", ex.message)
            return response
        } finally {
            webRiskServiceClient.close()
        }
    }
}