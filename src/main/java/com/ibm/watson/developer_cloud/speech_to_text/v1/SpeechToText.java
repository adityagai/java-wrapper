/**
 * Copyright 2015 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.watson.developer_cloud.speech_to_text.v1;

import java.io.File;
import java.util.List;

import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.http.HttpStatus;
import com.ibm.watson.developer_cloud.http.RequestBuilder;
import com.ibm.watson.developer_cloud.service.WatsonService;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SessionStatus;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModelSet;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;
import com.ibm.watson.developer_cloud.speech_to_text.v1.util.MediaTypeUtils;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import com.ibm.watson.developer_cloud.util.ResponseUtil;
import com.ibm.watson.developer_cloud.util.Validate;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * The Speech to Text service uses IBM's speech recognition capabilities to convert English speech
 * into text. The transcription of incoming audio is continuously sent back to the client with
 * minimal delay, and it is corrected as more speech is heard.
 * 
 * @version v1
 * @see <a
 *      href="http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/speech-to-text.html">
 *      Speech to Text</a>
 */
public class SpeechToText extends WatsonService {

  private static final String CONTINUOUS = "continuous";
  private static final String INACTIVITY_TIMEOUT = "inactivity_timeout";
  private static final String MAX_ALTERNATIVES = "max_alternatives";
  private static final String MODEL = "model";
  private static final String PATH_MODEL = "/v1/models/%s";
  private static final String PATH_MODELS = "/v1/models";
  private static final String PATH_RECOGNIZE = "/v1/recognize";
  private static final String PATH_SESSION = "/v1/sessions/%s";
  private static final String PATH_SESSION_RECOGNIZE = "/v1/sessions/%s/recognize";
  private static final String PATH_SESSIONS = "/v1/sessions";
  private static final String TIMESTAMPS = "timestamps";
  private final static String URL = "https://stream.watsonplatform.net/speech-to-text/api";
  private static final String WORD_CONFIDENCE = "word_confidence";
  private static final String SESSION = "session";

  /**
   * Instantiates a new speech to text.
   */
  public SpeechToText() {
    super("speech_to_text");
    setEndPoint(URL);
  }

  /**
   * Builds the recognize request using the {@link RecognizeOptions}.
   * 
   * @param requestBuilder the request builder
   * @param options the options
   */
  private void buildRecognizeRequest(RequestBuilder requestBuilder, RecognizeOptions options) {
    if (options == null)
      return;

    if (options.getWordConfidence() != null)
      requestBuilder.withQuery(WORD_CONFIDENCE, options.getWordConfidence());

    if (options.getContinuous() != null)
      requestBuilder.withQuery(CONTINUOUS, options.getContinuous());

    if (options.getMaxAlternatives() != null)
      requestBuilder.withQuery(MAX_ALTERNATIVES, options.getMaxAlternatives());

    if (options.getTimestamps() != null)
      requestBuilder.withQuery(TIMESTAMPS, options.getTimestamps());

    if (options.getInactivityTimeout() != null)
      requestBuilder.withQuery(INACTIVITY_TIMEOUT, options.getInactivityTimeout());

    if (options.getModel() != null)
      requestBuilder.withQuery(MODEL, options.getModel());
  }

  /**
   * Create a session to lock an engine to the session. You can use the session for multiple
   * recognition requests, so that each request is processed with the same speech-to-text engine.
   * Use the cookie that is returned from this operation in the Set-Cookie header for each request
   * that uses this session. The session expires after 15 minutes of inactivity.
   * 
   * @return the {@link SpeechSession}
   */
  public SpeechSession createSession() {
    final String model = null;
    return createSession(model);
  }

  /**
   * Create a session to lock an engine to the session. You can use the session for multiple
   * recognition requests, so that each request is processed with the same speech-to-text engine.
   * Use the cookie that is returned from this operation in the Set-Cookie header for each request
   * that uses this session. The session expires after 15 minutes of inactivity.
   * 
   * @param model the model
   * @return the {@link SpeechSession}
   */
  public SpeechSession createSession(SpeechModel model) {
    Validate.notNull(model, "model cannot be null");
    return createSession(model.getName());
  }

  /**
   * Create a session to lock an engine to the session. You can use the session for multiple
   * recognition requests, so that each request is processed with the same speech-to-text engine.
   * Use the cookie that is returned from this operation in the Set-Cookie header for each request
   * that uses this session. The session expires after 15 minutes of inactivity.
   * 
   * @param model the model
   * @return the {@link SpeechSession}
   */
  public SpeechSession createSession(final String model) {
    final RequestBuilder request = RequestBuilder.post(PATH_SESSIONS);

    if (model != null)
      request.withQuery(MODEL, model);

    final Response response = execute(request.build());
    final SpeechSession speechSession = ResponseUtil.getObject(response, SpeechSession.class);
    return speechSession;
  }

  /**
   * Delete a session.
   * 
   * @param session the speech session
   */
  public void deleteSession(final SpeechSession session) {
    if (session == null)
      throw new IllegalArgumentException("session was not specified");

    final Request request =
        RequestBuilder.delete(String.format(PATH_SESSION, session.getSessionId())).build();
    final Response response = execute(request);

    ResponseUtil.getString(response);
    if (response.code() != HttpStatus.NO_CONTENT)
      throw new RuntimeException("Cound't delete session");
  }

  /**
   * Gets the speech model.
   * 
   * @param name the name
   * @return the model
   */
  public SpeechModel getModel(final String name) {
    if (name == null)
      throw new IllegalArgumentException("name was not specified");

    final Request request = RequestBuilder.get(String.format(PATH_MODEL, name)).build();
    return executeRequest(request, SpeechModel.class);
  }

  /**
   * Gets the models.
   * 
   * @return the models
   */
  public List<SpeechModel> getModels() {
    final Request request = RequestBuilder.get(PATH_MODELS).build();
    return executeRequest(request, SpeechModelSet.class).getModels();
  }

  /**
   * Gets the session status. Concurrent recognition tasks during the same session are not allowed.
   * This method offers a way to check whether the session can accept another recognition task. The
   * returned state must be "initialized" to call {@link #recognize(File, String, RecognizeOptions)}
   * .
   * 
   * @param session the speech session
   * @return the model
   */
  public SessionStatus getRecognizeStatus(final SpeechSession session) {
    if (session == null)
      throw new IllegalArgumentException("session was not specified");

    final Request request =
        RequestBuilder.get(String.format(PATH_SESSION_RECOGNIZE, session.getSessionId())).build();
    final Response response = execute(request);
    final JsonObject jsonObject = ResponseUtil.getJsonObject(response);
    return GsonSingleton.getGson().fromJson(jsonObject.get(SESSION), SessionStatus.class);
  }

  /**
   * Recognizes an audio file and returns {@link SpeechResults}. It will try to recognize the audio
   * format based on the file extension.
   * 
   * @param audio the audio file
   * @return the {@link SpeechResults}
   */
  public SpeechResults recognize(File audio) {
    String contentType = MediaTypeUtils.getMediaTypeFromFile(audio);
    Validate.notNull(contentType, "Audio format cannot be recognized");
    return recognize(audio, contentType, null);
  }

  /**
   * Recognizes an audio file and returns {@link SpeechResults}.
   * 
   * @param audio the audio file
   * @param contentType the media type of the audio. If you use the audio/l16 MIME type, specify the
   *        rate and channels.
   * @return the {@link SpeechResults}
   */
  public SpeechResults recognize(File audio, String contentType) {
    return recognize(audio, contentType, null);
  }

  /**
   * Recognizes an audio file and returns {@link SpeechResults}.
   * 
   * @param audio the audio file
   * @param contentType the media type of the audio. If you use the audio/l16 MIME type, specify the
   *        rate and channels.
   * @param options the {@link RecognizeOptions}
   * @return the {@link SpeechResults}
   */
  public SpeechResults recognize(File audio, String contentType, RecognizeOptions options) {
    Validate.isTrue(audio != null && audio.exists(), "audio file is null or does not exist");
    Validate.isTrue(audio != null && audio.exists(), "audio file is null or does not exist");

    Validate.isTrue((audio.length() / (1024 * 1024)) < 100.0,
        "The audio file is greater than 100MB.");

    Validate.isTrue(MediaType.parse(contentType) != null,
        "contentType is not a valid mime audio format. Valid formats start with 'audio/'");

    String path = PATH_RECOGNIZE;
    if (options != null && (options.getSessionId() != null && !options.getSessionId().isEmpty()))
      path = String.format(PATH_SESSION_RECOGNIZE, options.getSessionId());

    final RequestBuilder requestBuilder = RequestBuilder.post(path);

    buildRecognizeRequest(requestBuilder, options);

    requestBuilder.withBody(RequestBody.create(MediaType.parse(contentType), audio));
    return executeRequest(requestBuilder.build(), SpeechResults.class);
  }
}
